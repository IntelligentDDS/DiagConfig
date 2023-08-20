package edu.sysu.jimmy.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sysu.jimmy.analysis.result.HotSpotDef;
import edu.sysu.jimmy.analysis.result.SourceSinkInfo;
import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegion;
import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegionFinder;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static edu.sysu.jimmy.utility.SubjectSysInfo.prepareArgs;

public class CallsiteAnalysis {
    protected static Logger LOGGER = LoggerFactory.getLogger(CallsiteAnalysis.class);
    public static String[] initArgs = {
            "-cp", null,
            "-pp",
            "-allow-phantom-refs",
            "-w",
            "cg.spark", "on",
            "-keep-line-number",
            "-f", "n",
    };
    public String[] sootArgs;
    public String sysName;
    public VariabilityParsing sysDiff;
    public static MethodSignatureConverter methodSignatureConverter = null;
    public TaintFlow taintFlow;
    public CallGraph cg;

    public double delta;
    public Map<String, List<CallChainWithOption>> hotSpotWithOption;

    private static Map<String, EnhancedUnitGraph> caller2Eug = new HashMap<>();
    private static Map<String, Set<Loop>> caller2Loops = new HashMap<>();
    private static Map<String, Set<IfRegion>> caller2Ifs = new HashMap<>();


    public CallsiteAnalysis(String sysName, String csvFilePath, String xmlFilePath, String[] consideredOptions, double delta) {
        this.sysName = sysName;
        this.sootArgs = prepareArgs(this.sysName, initArgs);
        this.sysDiff = new VariabilityParsing(csvFilePath, xmlFilePath, this.delta);
        this.taintFlow = new TaintFlow(this.sysName, consideredOptions);
        this.hotSpotWithOption = new HashMap<>();
        this.delta = delta;
    }

    public void run() {
        if (CallsiteAnalysis.methodSignatureConverter == null) {
            methodSignatureConverter = MethodSignatureConverter.v();
//            LOGGER.info("Started resolving method signature.");
//            methodSignatureConverter = new MethodSignatureConverter();
            PackManager.v().getPack("jtp").add(new Transform("jtp.resolveMethodSignature", methodSignatureConverter));

            soot.Main.main(this.sootArgs);
//            LOGGER.info("Finished resolving method signature.");
        }
//        this.methodSignatureConverter = new MethodSignatureConverter(this.sysDiff);
//        PackManager.v().getPack("jtp").add(new Transform("jtp.resolveMethodSignature", this.methodSignatureConverter));
//        soot.Main.main(this.sootArgs);
//        LOGGER.info("Started converting method signature.");
        this.convert();
//        LOGGER.info("Finished converting method signature.");
        this.sysDiff.buildIndex();
        this.sysDiff.callChainParser();
//        LOGGER.info("Started getting call graph.");
        this.cg = Scene.v().getCallGraph();
//        LOGGER.info("Finished getting call graph.");
    }

    public void convert() {
        for (Map.Entry<String, HotSpotDef> hotSpotDefEntry : this.sysDiff.consideredHotSpotsIndex.entrySet()) {
            if (methodSignatureConverter.methodSig2bytecodeSigIndex.containsKey(hotSpotDefEntry.getKey())) {
                hotSpotDefEntry.getValue().methodSignature = methodSignatureConverter.methodSig2bytecodeSigIndex.get(hotSpotDefEntry.getKey());
            }

        }
/*        if (variabilityParsing.consideredHotSpotsIndex.containsKey(methodSig)) {
            variabilityParsing.consideredHotSpotsIndex.get(methodSig).methodSignature = bytecodeSig;
//            variabilityParsing.consideredHotSpotsIndex.get(methodSig).methodSignature = sootSig;
        }*/
    }

    private class CallChainWithOption {
        public String methodSignature;
        public LinkedList<String> callChain;
        Map<String, Set<SourceSinkInfo>> options; // influencing option at callsite in method
        public Set<String> optionList;

        public CallChainWithOption(String methodSignature, LinkedList<String> callChain, Map<String, LinkedList<SourceSinkInfo>> option) {
            this.methodSignature = methodSignature;
            this.callChain = callChain;
            this.options = new HashMap<>();
            this.optionList = new HashSet<>();
        }

        public void addOption(String methodSignature, SourceSinkInfo taintInfo) {
            if (!this.options.containsKey(methodSignature)) {
                this.options.put(methodSignature, new HashSet<>());
            }
            this.options.get(methodSignature).add(taintInfo);
            this.optionList.addAll(taintInfo.getOptions());
        }

        public String getMethodSignature() {
            return methodSignature;
        }

        public void setMethodSignature(String methodSignature) {
            this.methodSignature = methodSignature;
        }

        public LinkedList<String> getCallChain() {
            return callChain;
        }

        public void setCallChain(LinkedList<String> callChain) {
            this.callChain = callChain;
        }

        public Map<String, Set<SourceSinkInfo>> getOptions() {
            return options;
        }

        public void setOptions(Map<String, Set<SourceSinkInfo>> options) {
            this.options = options;
        }

        public Set<String> getOptionList() {
            return optionList;
        }

        public void setOptionList(Set<String> optionList) {
            this.optionList = optionList;
        }

        public CallChainWithOption() {
        }
    }

    public void linkCallChainWithTaintFlow(int k) {
//        LOGGER.info("Started call-site analysis.");
        for (HotSpotDef consideredHotspot : this.sysDiff.consideredHotspots) {
            // directly influence hotspot
            if (this.taintFlow.methodTaintInfo.containsKey(consideredHotspot.methodSignature)) {
                CallChainWithOption callChainWithOption = null;
                for (SourceSinkInfo tainFlow : this.taintFlow.methodTaintInfo.get(consideredHotspot.methodSignature)) {
                    if (!tainFlow.getOperationType().contains("Thread")) {
                        if (callChainWithOption == null) {
                            callChainWithOption = new CallChainWithOption(consideredHotspot.methodSignature, new LinkedList<String>() {{
                                add(consideredHotspot.methodSignature);
                            }}, new HashMap<>());
                        }
                        callChainWithOption.addOption(consideredHotspot.methodSignature, tainFlow);
                    }

                }
                if (!this.hotSpotWithOption.containsKey(consideredHotspot.methodSignature)) {
                    this.hotSpotWithOption.put(consideredHotspot.methodSignature, new LinkedList<>());
                }
                this.hotSpotWithOption.get(consideredHotspot.methodSignature).add(callChainWithOption);
                continue;
            }

            // call influence
            LinkedList<LinkedList<String>> callChainList = sysDiff.consideredCallChains.get(consideredHotspot.methodSignature);
            if (callChainList == null || callChainList.isEmpty())
                continue;
            for (LinkedList<String> callChain : callChainList) {
//                long startTime = System.currentTimeMillis();
//                LOGGER.info("Started call-site analysis for call chain");
                CallChainWithOption callChainWithOption = null;
                Iterator<String> call = callChain.iterator();
                String callee = call.next();
                String caller;
                while (call.hasNext()) {
                    caller = call.next();
                    if (this.taintFlow.methodTaintInfo.containsKey(caller)) {
                        List<SourceSinkInfo> taintFlows = this.taintFlow.methodTaintInfo.get(caller);
                        for (SourceSinkInfo taintFlow : taintFlows) {
                            if (checkCallSite(callee, caller, taintFlow)) {
                                if (callChainWithOption == null) {
                                    callChainWithOption = new CallChainWithOption(consideredHotspot.methodSignature, callChain, new HashMap<>());
                                }
                                callChainWithOption.addOption(caller, taintFlow);
                            }
                            if (callChainWithOption != null && callChainWithOption.optionList.size() == k) {
                                break;
                            }
                        }
                    }
                    callee = caller;
                }
                if (callChainWithOption != null) {
                    if (!this.hotSpotWithOption.containsKey(callChainWithOption.methodSignature)) {
                        this.hotSpotWithOption.put(callChainWithOption.methodSignature, new LinkedList<>());
                    }
                    this.hotSpotWithOption.get(callChainWithOption.methodSignature).add(callChainWithOption);
                }
//                LOGGER.info("Finished call-site analysis for call chain");
//                System.out.println(System.currentTimeMillis() - startTime);
            }
        }
//        LOGGER.info("Finished call-site analysis.");
    }

    public boolean checkCallSite(String callee, String caller, SourceSinkInfo taintFlow) {
        if ("cassandra".compareTo(this.sysName) == 0)
            return true;
        // if ("catena".compareTo(this.sysName) == 0 || "prevayler".compareTo(this.sysName) == 0 || "batik".compareTo(this.sysName) == 0 || "h2".compareTo(this.sysName) == 0)
        // return true;
        String operationType = taintFlow.getOperationType();
        if (!operationType.contains("Thread"))
            return true;
        Body body = this.methodSignatureConverter.byteSig2MethodBody.get(caller);
        if (body == null)
            return true;

        if (operationType.startsWith("directly")) {
            UnitPatchingChain units = body.getUnits();
            for (Unit unit : units) {
                Stmt stmt = (Stmt) unit;
                if (stmt.toString().compareTo(taintFlow.getOriginalStmt()) == 0) {
                    Iterator<Edge> edgeIterator = cg.edgesOutOf(unit);
                    while (edgeIterator.hasNext()) {
                        Edge next = edgeIterator.next();
                        if (callee.compareTo(next.getTgt().method().getBytecodeSignature()) == 0)
                            return true;
                    }
                }
            }
        } else if (operationType.startsWith("loop")) {
            EnhancedUnitGraph g = null;
            Set<Loop> loops = null;
            if (caller2Loops.containsKey(caller)) {
                loops = caller2Loops.get(caller);
            } else {
                if (caller2Eug.containsKey(caller)) {
                    g = caller2Eug.get(caller);
                } else {
                    g = new EnhancedUnitGraph(body);
                    caller2Eug.put(caller, g);
                }
                loops = new LoopFinder().getLoops(g);
                caller2Loops.put(caller, loops);
            }
            for (Loop loop : loops) {
                for (Stmt loopExit : loop.getLoopExits()) {
                    if (this.getJavaLine(loopExit.getTags()) == taintFlow.getJavaLine()) {
                        for (Stmt loopStatement : loop.getLoopStatements()) {
                            Iterator<Edge> edgeIterator = cg.edgesOutOf(loopStatement);
                            while (edgeIterator.hasNext()) {
                                Edge next = edgeIterator.next();
                                if (callee.compareTo(next.getTgt().method().getBytecodeSignature()) == 0)
                                    return true;
                            }
                        }
                        break;
                    }
                }
            }
        } else if (operationType.startsWith("if")) {
            EnhancedUnitGraph g = null;
            Set<Loop> loops = null;
            Set<IfRegion> ifRegions = null;
            if (caller2Ifs.containsKey(caller)) {
                ifRegions = caller2Ifs.get(caller);
            } else {
                if (caller2Eug.containsKey(caller)) {
                    g = caller2Eug.get(caller);
                } else {
                    g = new EnhancedUnitGraph(body);
                    caller2Eug.put(caller, g);
                }

                if (caller2Loops.containsKey(caller)) {
                    loops = caller2Loops.get(caller);
                } else {
                    loops = new LoopFinder().getLoops(g);
                    caller2Loops.put(caller, loops);
                }
                try {
                    ifRegions = new IfRegionFinder().getIfRegions(g, loops);
                } catch (Exception | Error ignored) {
                    ifRegions = null;
                }
                if (ifRegions != null)
                    caller2Ifs.put(caller, ifRegions);
            }
            if (ifRegions != null) {
                for (IfRegion ifRegion : ifRegions) {
                    if (this.getJavaLine(ifRegion.getHeader().getTags()) == taintFlow.getJavaLine()) {
                        for (Stmt stmt : ifRegion.getIfStatements()) {
                            Iterator<Edge> edgeIterator = cg.edgesOutOf(stmt);
                            while (edgeIterator.hasNext()) {
                                Edge next = edgeIterator.next();
                                if (callee.compareTo(next.getTgt().method().getBytecodeSignature()) == 0)
                                    return true;
                            }
                        }
                    }
                }
            }
        }

        return false;

    }

    public int getJavaLine(List<Tag> Tags) {
        List<Integer> javaLines = new ArrayList<>();

        for (Tag tag : Tags) {
            if (tag instanceof LineNumberTag) {
                int javaLine = ((LineNumberTag) tag).getLineNumber();
                javaLines.add(javaLine);
            }
        }

        if (javaLines.isEmpty()) {
            javaLines.add(-1);
        }

        int javaLine;

        if (javaLines.size() == 1) {
            javaLine = javaLines.get(0);
        } else {
            int index = javaLines.indexOf(Collections.min(javaLines));
            javaLine = javaLines.get(index);
        }
        return javaLine;
    }

    public void persist() throws IOException {
        LinkedList<CallChainWithOption> data2persist = new LinkedList<>();
        for (HotSpotDef consideredHotspot : this.sysDiff.consideredHotspots) {
            List<CallChainWithOption> callChainWithOptions = this.hotSpotWithOption.get(consideredHotspot.methodSignature);
            if (callChainWithOptions != null) {
                data2persist.addAll(callChainWithOptions);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        File outputFile = new File(this.sysDiff.xmlFilePath.substring(0, this.sysDiff.xmlFilePath.lastIndexOf("/")) + File.separator + "cause-effectChains" + ".json");
        outputFile.getParentFile().mkdirs();
        mapper.writeValue(outputFile, data2persist);
    }

    public Set<String> getOptionList() {
        HashSet<String> options = new HashSet<>();
        for (List<CallChainWithOption> value : this.hotSpotWithOption.values()) {
            for (CallChainWithOption callChainWithOption : value) {
                options.addAll(callChainWithOption.optionList);
            }
        }
        return options;
    }

}
