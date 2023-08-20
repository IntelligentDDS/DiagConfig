package edu.sysu.jimmy.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sysu.jimmy.analysis.result.SourceSinkInfo;
import edu.sysu.jimmy.jimple.infoflow.sourcesSinks.manager.MapGetSourceSinkManager;
import edu.sysu.jimmy.jimple.infoflow.sourcesSinks.manager.SVGBatikSouceSinkManager;
import edu.sysu.jimmy.jimple.jtp.*;
import edu.sysu.jimmy.soot.jimple.internal.ResolveJTableSwitchConstant;
import edu.sysu.jimmy.utility.SubjectSysInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.SequentialEntryPointCreator;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.sourcesSinks.manager.DefaultSourceSinkManager;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JTableSwitchStmt;
import soot.tagkit.BytecodeOffsetTag;
import soot.tagkit.LineNumberTag;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalGraph;
import soot.util.MultiMap;
import soot.util.queue.QueueReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TaintTracking extends Infoflow {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaintTracking.class);
    public static final String OUTPUT_DIR = "subjectSys" + File.separator + "output" + File.separator;
    public static final String PATHS_DIR = "src/main/resources/paths/";

    private String targetName;
    private Map<String, String> sourceToOptions = new HashMap<>();// 记录source方法签名和对应的配置选项<methodSignature, option>
    private List<String> sinks = new ArrayList<>();// 记录sink方法签名
    private List<String> packages = new ArrayList<>();

    public TaintTracking(String targetName) {
        this.targetName = targetName;
        int targetOrder = SubjectSysInfo.getOrder(targetName);
        packages.addAll(Arrays.asList(SubjectSysInfo.PKG[targetOrder]));
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void directlyLockSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void ifLockSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void loopLockSink(java.lang.Object)>");

        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void directlyIoSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void ifIoSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void loopIoSink(java.lang.Object)>");


        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void directlyNewArrSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void ifNewArrSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void loopNewArrSink(java.lang.Object)>");


        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void directlyThreadSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void ifThreadSink(java.lang.Object)>");
        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void loopThreadSink(java.lang.Object)>");

        sinks.add("<edu.sysu.jimmy.analysis.option.MySink: void sink(java.lang.Object)>");

        initSource(targetOrder);

    }

    @Override
    protected void constructCallgraph() {
        super.constructCallgraph();
        QueueReader<MethodOrMethodContext> listener = Scene.v().getReachableMethods().listener();
//        PackManager.v().getPack("jtp").add(new Transform("jtp.mayVariablePropagation", MayVariablePropagation.v()));
//        PackManager.v().getPack("jtp").add(new Transform("jtp.controlflowsink", ControlFlowSink.v()));

        /*PackManager.v().getPack("jtp").add(new Transform("jtp.showNeededInfo", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                SootMethod method = b.getMethod();
                if (method.getDeclaringClass().getName().contains("SVGConverter")){
                    System.out.println(method.getSignature());
                }
            }
        }));*/
        PackManager.v().getPack("jtp").add(new Transform("jtp.SyncEnterSink", SyncEnterSink.v()));
        PackManager.v().getPack("jtp").add(new Transform("jtp.JavaIOSink", JavaIOSink.v()));
        PackManager.v().getPack("jtp").add(new Transform("jtp.NewArrSink", NewArrSink.v()));
        PackManager.v().getPack("jtp").add(new Transform("jtp.ThreadSink", JavaThreadSink.v()));
//        PackManager.v().getPack("jtp").add(new Transform("jtp.ControlFlowSink", ControlFlowSink.v()));

        while (listener.hasNext()) {
            MethodOrMethodContext m = listener.next();
            SootMethod method = m.method();
            String methodPackage = method.getDeclaringClass().getPackageName();
            for (String packageName : packages) {
                if (methodPackage.contains(packageName) && method.hasActiveBody()) {
                    PackManager.v().getPack("jtp").apply(method.getActiveBody());
                }
            }
        }
    }

    public void trackOneSourceMultiEntrypoint(String appPath, String libPaht, Collection<String> entryPonits) throws IOException {
        trackOneSourceMultiEntrypoint(appPath, libPaht, entryPonits, sourceToOptions.keySet(), sinks);
    }

    public void trackOneSourceMultiEntrypoint(String appPath, String libPath, Collection<String> entryPoints, Collection<String> sources, Collection<String> sinks) throws IOException {
        for (String source : sources) {
            String currentOption = this.sourceToOptions.get(source);
            LOGGER.info("############Analyzing option " + currentOption);
            List<String> intermediateSources = new ArrayList<String>();
            intermediateSources.add(source);
            this.computeInfoflow(appPath, libPath, new SequentialEntryPointCreator(entryPoints), intermediateSources, (List<String>) sinks);
            collectResultsByOptions(currentOption);
//            List<String> entryPoints = new ArrayList<>();
//            entryPoints.add(entryPoint);
//            LOGGER.info("show Results");
//            showPathPrecise();
//            this.results.printResults();
//            showResults();
        }
    }

    public void trackOneSourceOneEntrypoint(String appPath, String libPath, String entryPoint) throws IOException {
//        trackOneSourceMultiEntrypoint(appPath, libPath, new ArrayList<String>() {{
//            add(entryPoint);
//        }}, sourceToOptions.keySet(), sinks);
        trackOneSourceOneEntrypoint(appPath, libPath, entryPoint, this.sourceToOptions.keySet(), this.sinks);
    }

    public void computeOneSourceOneEntrypoint(String appPath, String libPath, String entryPoint, Collection<String> args) throws IOException {
        for (String arg : args) {
            LOGGER.info("############Analyzing option " + arg);
            ArrayList<String> entryPoints = new ArrayList<>();
            entryPoints.add(entryPoint);
            HashSet<String> intermediateArgs = new HashSet<>();
            intermediateArgs.add(arg);
            this.computeInfoflow(appPath, libPath, new DefaultEntryPointCreator(entryPoints), new MapGetSourceSinkManager(intermediateArgs, sourceToOptions.keySet(), this.sinks));
//            this.computeInfoflow(appPath, libPath, new SequentialEntryPointCreator(entryPoints), new SVGBatikSouceSinkManager(intermediateArgs, sourceToOptions.keySet(), this.sinks));
            collectResultsByOptions(arg);
        }
    }

    public void computeOneSourceMultiEntrypoint(String appPath, String libPath, Collection<String> entryPoints, Collection<String> args) throws IOException {
        for (String arg : args) {
            LOGGER.info("############Analyzing option " + arg);
//            ArrayList<String> entryPoints = new ArrayList<>();
//            entryPoints.add(entryPoint);
            HashSet<String> intermediateArgs = new HashSet<>();
            intermediateArgs.add(arg);
            this.computeInfoflow(appPath, libPath, new DefaultEntryPointCreator(entryPoints), new MapGetSourceSinkManager(intermediateArgs, sourceToOptions.keySet(), this.sinks));
//            this.computeInfoflow(appPath, libPath, new DefaultEntryPointCreator(entryPoints), new SVGBatikSouceSinkManager(intermediateArgs, sourceToOptions.keySet(), this.sinks));
            collectResultsByOptions(arg);
        }
    }

    public void trackOneSourceOneEntrypoint(String appPath, String libPath, String entryPoint,
                                            Collection<String> sources, Collection<String> sinks) throws IOException {
        for (String source : sources) {
            String currentOption = this.sourceToOptions.get(source);
//            if ("idx".compareTo(currentOption) != 0)
//                continue;
            LOGGER.info("############Analyzing option " + currentOption);
            List<String> entryPoints = new ArrayList<>();
            entryPoints.add(entryPoint);
            List<String> intermediateSources = new ArrayList<String>();
            intermediateSources.add(source);
//            this.computeInfoflow(appPath, libPath, entryPoints, intermediateSources, sinks);
            this.computeInfoflow(appPath, libPath, new SequentialEntryPointCreator(entryPoints), new DefaultSourceSinkManager(intermediateSources, sinks));
            collectResultsByOptions(currentOption);
//            LOGGER.info("show Results");
//            showPathPrecise();
//            this.results.printResults();
//            showResults();
//            ObjectMapper mapper = new ObjectMapper();
//            File outputFile = new File(TaintTracking.OUTPUT_DIR + "/" + this.targetName + "/" + currentOption + ".json");
//            outputFile.getParentFile().mkdirs();
        }
    }

    public void trackAndPersist(String appPath, String libPath, String entryPoint, Collection<String> sources, Collection<String> sinks) throws IOException {
        this.computeInfoflow(appPath, libPath, new SequentialEntryPointCreator(Collections.singleton(entryPoint)), new DefaultSourceSinkManager(sources, sinks));
        this.persistence();
    }
    public void trackAndPersist(String appPath, String libPath, List<String> entryPoint, Collection<String> sources, Collection<String> sinks) throws IOException {
        this.computeInfoflow(appPath, libPath, new SequentialEntryPointCreator(entryPoint), new DefaultSourceSinkManager(sources, sinks));
        this.persistence();
    }

    public void saveDotStringFiles() throws FileNotFoundException {
        IInfoflowCFG iCfg = this.icfgFactory.buildBiDirICFG(config.getCallgraphAlgorithm(), config.getEnableExceptionTracking());

        List<SootMethod> methods = new ArrayList<>(this.getMethodsForSeeds(iCfg));

        Map<String, StringBuilder> classesToDoStrings = new HashMap<>();

        for (SootMethod method : methods) {
            if (!method.hasActiveBody()) {
                continue;
            }

            String className = method.getDeclaringClass().getName();

            if (!classesToDoStrings.containsKey(className)) {
                classesToDoStrings.put(className, new StringBuilder());
            }

            StringBuilder classDotString = classesToDoStrings.get(className);
            classDotString.append("digraph ");
            classDotString.append(method.getName());
            classDotString.append(" {\n");

            DirectedGraph<Unit> graph = iCfg.getOrCreateUnitGraph(method);

//            if(className.contains("Interaction")) {
//                System.out.println();
//            }

            Iterator<Unit> units = graph.iterator();
            Set<String> instrumentedNodes = new HashSet<>();

            while (units.hasNext()) {
                Unit unit = units.next();
                List<Unit> succs = graph.getSuccsOf(unit);

                for (Unit succ : succs) {
                    String node = unit.toString().replace("\"", "\\\"");
                    node += " {" + unit.hashCode() + "}";

                    if (unit instanceof NopStmt) {
                        instrumentedNodes.add(node);
                    } else if (unit instanceof JInvokeStmt) {
                        InvokeExpr v = ((JInvokeStmt) unit).getInvokeExpr();
                        String name = v.getMethod().getName();

                        if (name.equals("sink")) {
                            instrumentedNodes.add(node);
                        }
                    }

                    classDotString.append('"');
                    classDotString.append(node);
                    classDotString.append('"');
                    classDotString.append(" -> ");

                    node = succ.toString().replace("\"", "\\\"");
                    node += " {" + succ.hashCode() + "}";

                    if (succ instanceof NopStmt) {
                        instrumentedNodes.add(node);
                    } else if (succ instanceof JInvokeStmt) {
                        InvokeExpr v = ((JInvokeStmt) succ).getInvokeExpr();
                        String name = v.getMethod().getName();

                        if (name.equals("sink")) {
                            instrumentedNodes.add(node);
                        }
                    }

                    classDotString.append('"');
                    classDotString.append(node);
                    classDotString.append('"');

                    if (graph instanceof ExceptionalGraph) {
                        ExceptionalGraph<Unit> exceptionalGraph = (ExceptionalGraph<Unit>) graph;
                        if (exceptionalGraph.getExceptionalSuccsOf(unit).contains(succ)) {
                            classDotString.append("[penwidth=3, color=\"red\"]");
                        }
                    }

                    classDotString.append(";\n");
                }
            }

            for (String nopNode : instrumentedNodes) {
                classDotString.append('"');
                classDotString.append(nopNode);
                classDotString.append('"');
                classDotString.append("[fontcolor=\"blue\", penwidth=3, color=\"blue\"];\n");
            }

            classDotString.append("}\n\n");

        }

        String root = "dotStringOutput/";

        for (Map.Entry<String, StringBuilder> classDotString : classesToDoStrings.entrySet()) {
            PrintWriter out = new PrintWriter(root + classDotString.getKey());
            out.println(classDotString.getValue());
            out.close();
            out.flush();
        }
    }

    public void saveJimpleFiles() throws FileNotFoundException {
        IInfoflowCFG iCfg = this.icfgFactory.buildBiDirICFG(config.getCallgraphAlgorithm(), config.getEnableExceptionTracking());
        List<SootMethod> methods = new ArrayList<>(this.getMethodsForSeeds(iCfg));

        Map<String, StringBuilder> classesToMethods = new HashMap<>();

        for (SootMethod method : methods) {
            if (!method.hasActiveBody()) {
                continue;
            }

//            if(method.getName().contains("main")) {
//                System.out.println(this.iCfg.getCallersOf(method));
//            }

            String className = method.getDeclaringClass().getName();

            if (!classesToMethods.containsKey(className)) {
                classesToMethods.put(className, new StringBuilder());
            }

            StringBuilder classBody = classesToMethods.get(className);
            classBody.append(method.getActiveBody().toString());
            classBody.append("\n");
        }

        String root = "sootOutput/";

        for (Map.Entry<String, StringBuilder> classToMethods : classesToMethods.entrySet()) {
            PrintWriter out = new PrintWriter(root + classToMethods.getKey());
            out.println(classToMethods.getValue());
            out.close();
            out.flush();
        }
    }

    private void showPathPrecise() {
        if (this.results == null)
            return;
        MultiMap<ResultSinkInfo, ResultSourceInfo> resultsMap = this.results.getResults();
        if (resultsMap != null)
            for (ResultSinkInfo sink : resultsMap.keySet()) {
                LOGGER.info("Found a flow to sink {}, from the following sources:", sink);
                for (ResultSourceInfo source : resultsMap.get(sink)) {
                    LOGGER.info("\t- {}", source.getStmt());
                    if (source.getPath() != null) {
                        Stmt[] pathStmts = source.getPath();
                        for (int i = 0; i < pathStmts.length; i++) {
                            if (i == 0)
                                LOGGER.info("\t\ton Path {}", pathStmts[i]);
                            else
                                LOGGER.info("\t\t\t\t {}", pathStmts[i]);
                        }
                    }
                }
            }
    }

    public void collectResultsByOptions(String option) throws IOException {
        MultiMap<ResultSinkInfo, ResultSourceInfo> resultsMap = this.getResults().getResults();
        if (resultsMap == null || resultsMap.isEmpty())
            return;
        List<SourceSinkInfo> results = new ArrayList<>();
        IInfoflowCFG iCfg = this.icfgFactory.buildBiDirICFG(config.getCallgraphAlgorithm(), config.getEnableExceptionTracking());
        for (ResultSinkInfo resultSinkInfo : resultsMap.keySet()) {

            // Sink Info
            Stmt sinkStmt = resultSinkInfo.getStmt();
            String operationType = sinkStmt.getInvokeExpr().getMethod().getName();
            SootMethod sinkAtMethod = iCfg.getMethodOf(sinkStmt);
            String methodBytecodeSignature = sinkAtMethod.getBytecodeSignature();
            SootClass sinkAtClass = sinkAtMethod.getDeclaringClass();
            String className = sinkAtClass.getShortName();
            String packageName = sinkAtClass.getPackageName();
            String originalStmt = null;
            ArrayList<StringTag> stringTags = new ArrayList<>();
            for (Tag tag : sinkStmt.getTags()) {
                if (tag instanceof StringTag)
                    stringTags.add((StringTag) tag);
//                    originalStmt = ((StringTag) tag).getInfo();
            }

            List<Integer> bytecodeIndexes = new ArrayList<>();

            for (Tag tag : sinkStmt.getTags()) {
                if (tag instanceof BytecodeOffsetTag) {
                    int bytecodeIndex = ((BytecodeOffsetTag) tag).getBytecodeOffset();
                    bytecodeIndexes.add(bytecodeIndex);
                }
            }

            if (bytecodeIndexes.isEmpty()) {
                bytecodeIndexes.add(-1);
            }

            int bytecodeIndex;

            if (bytecodeIndexes.size() == 1) {
                bytecodeIndex = bytecodeIndexes.get(0);
            } else {
                int index = bytecodeIndexes.indexOf(Collections.min(bytecodeIndexes));
                bytecodeIndex = bytecodeIndexes.get(index);
            }

            List<Integer> javaLines = new ArrayList<>();

            for (Tag tag : sinkStmt.getTags()) {
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


            // Source Info
            Set<String> sources = new HashSet<>();

            for (ResultSourceInfo resultSourceInfo : resultsMap.get(resultSinkInfo)) {
                Stmt source = resultSourceInfo.getStmt();

                if (!source.containsInvokeExpr()) {
                    throw new RuntimeException("The source does not have an invoke: " + source.toString());
                }

                InvokeExpr expr = source.getInvokeExpr();
                SootMethod exprMethod = expr.getMethod();
                sources.add(option);
                /*String resultOption = this.sourceToOptions.get(exprMethod.getSignature());

                if (resultOption == null) {
                    continue;
//                    throw new RuntimeException("Source is not associated to an option: " + source);
                }
                if (option != null) {
                    if (!resultOption.equals(option)) {
                        continue;
//                        throw new RuntimeException("The option in this source is different than the one we analyzed");
                    }

                    sources.add(option);
                } else sources.add(resultOption);*/
            }

            //value Info
            Set<String> mayValues = new HashSet<>();
            if (operationType.compareTo("sink") == 0) {
                Unit next = sinkStmt;
                List<Unit> succsOf = null;
                IfStmt valueInfoIfStmt = null;
                SwitchStmt valueInfoSwitchStmt = null;
                while (true) {
                    succsOf = iCfg.getSuccsOf(next);
                    if (succsOf.size() != 1)
                        break;
                    next = succsOf.get(0);
                    if (next instanceof IfStmt) {
                        valueInfoIfStmt = (IfStmt) next;
                        break;
                    } else if (next instanceof SwitchStmt) {
                        valueInfoSwitchStmt = (SwitchStmt) next;
                        break;
                    }
                }
                if (valueInfoIfStmt != null) {
                    for (ValueBox useBox : valueInfoIfStmt.getCondition().getUseBoxes()) {
                        Value value = useBox.getValue();
                        if (value instanceof Constant) {
                            mayValues.add(value.toString());
                        }
                    }
                } else if (valueInfoSwitchStmt != null) {
                    if (valueInfoSwitchStmt instanceof JLookupSwitchStmt) {
                        JLookupSwitchStmt jLookupSwitchStmt = (JLookupSwitchStmt) valueInfoSwitchStmt;
                        List<IntConstant> lookupValues = jLookupSwitchStmt.getLookupValues();
                        for (IntConstant lookupValue : lookupValues) {
                            mayValues.add(lookupValue.toString());
                        }
                    } else if (valueInfoSwitchStmt instanceof JTableSwitchStmt) {
                        JTableSwitchStmt jTableSwitchStmt = (JTableSwitchStmt) valueInfoSwitchStmt;
                        ArrayList<Integer> tableValues = ResolveJTableSwitchConstant.resolveConstant(jTableSwitchStmt);
                        for (Integer tableValue : tableValues) {
                            mayValues.add(tableValue.toString());
                        }
                    }
                }

            }

            // gen result
//            SourceSinkInfo sourceSinkInfo = new SourceSinkInfo(packageName, className, methodBytecodeSignature, javaLine, bytecodeIndex, operationType, sources);
            if (!stringTags.isEmpty()) {
                for (StringTag stringTag : stringTags) {
                    SourceSinkInfo sourceSinkInfo = new SourceSinkInfo(packageName, className, methodBytecodeSignature, javaLine, bytecodeIndex, operationType, sources, stringTag.getInfo(), mayValues);
                    results.add(sourceSinkInfo);
                }
            } else {
                SourceSinkInfo sourceSinkInfo = new SourceSinkInfo(packageName, className, methodBytecodeSignature, javaLine, bytecodeIndex, operationType, sources, originalStmt, mayValues);
                results.add(sourceSinkInfo);
            }
        }
        iCfg.purge();
        ObjectMapper mapper = new ObjectMapper();
        File outputFile = new File(TaintTracking.OUTPUT_DIR + File.separator + this.targetName + File.separator + "20480m" + File.separator + option + ".json");
        outputFile.getParentFile().mkdirs();
        mapper.writeValue(outputFile, results);
    }

    private void persistence() throws IOException {
        MultiMap<ResultSinkInfo, ResultSourceInfo> resultsMap = this.getResults().getResults();
        if (resultsMap == null || resultsMap.isEmpty())
            return;
        HashMap<String, List<SourceSinkInfo>> option2result = new HashMap<>();
        IInfoflowCFG iCfg = this.icfgFactory.buildBiDirICFG(config.getCallgraphAlgorithm(), config.getEnableExceptionTracking());
        for (ResultSinkInfo resultSinkInfo : resultsMap.keySet()) {

            // Sink Info
            Stmt sinkStmt = resultSinkInfo.getStmt();
            String operationType = sinkStmt.getInvokeExpr().getMethod().getName();
            SootMethod sinkAtMethod = iCfg.getMethodOf(sinkStmt);
            String methodBytecodeSignature = sinkAtMethod.getBytecodeSignature();
            SootClass sinkAtClass = sinkAtMethod.getDeclaringClass();
            String className = sinkAtClass.getShortName();
            String packageName = sinkAtClass.getPackageName();
            String originalStmt = null;
            ArrayList<StringTag> stringTags = new ArrayList<>();
            for (Tag tag : sinkStmt.getTags()) {
                if (tag instanceof StringTag)
                    stringTags.add((StringTag) tag);
//                    originalStmt = ((StringTag) tag).getInfo();
            }

            List<Integer> bytecodeIndexes = new ArrayList<>();

            for (Tag tag : sinkStmt.getTags()) {
                if (tag instanceof BytecodeOffsetTag) {
                    int bytecodeIndex = ((BytecodeOffsetTag) tag).getBytecodeOffset();
                    bytecodeIndexes.add(bytecodeIndex);
                }
            }

            if (bytecodeIndexes.isEmpty()) {
                bytecodeIndexes.add(-1);
            }

            int bytecodeIndex;

            if (bytecodeIndexes.size() == 1) {
                bytecodeIndex = bytecodeIndexes.get(0);
            } else {
                int index = bytecodeIndexes.indexOf(Collections.min(bytecodeIndexes));
                bytecodeIndex = bytecodeIndexes.get(index);
            }

            List<Integer> javaLines = new ArrayList<>();

            for (Tag tag : sinkStmt.getTags()) {
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


            //value Info
            Set<String> mayValues = new HashSet<>();
            if (operationType.compareTo("sink") == 0) {
                Unit next = sinkStmt;
                List<Unit> succsOf = null;
                IfStmt valueInfoIfStmt = null;
                SwitchStmt valueInfoSwitchStmt = null;
                while (true) {
                    succsOf = iCfg.getSuccsOf(next);
                    if (succsOf.size() != 1)
                        break;
                    next = succsOf.get(0);
                    if (next instanceof IfStmt) {
                        valueInfoIfStmt = (IfStmt) next;
                        break;
                    } else if (next instanceof SwitchStmt) {
                        valueInfoSwitchStmt = (SwitchStmt) next;
                        break;
                    }
                }
                if (valueInfoIfStmt != null) {
                    for (ValueBox useBox : valueInfoIfStmt.getCondition().getUseBoxes()) {
                        Value value = useBox.getValue();
                        if (value instanceof Constant) {
                            mayValues.add(value.toString());
                        }
                    }
                } else if (valueInfoSwitchStmt != null) {
                    if (valueInfoSwitchStmt instanceof JLookupSwitchStmt) {
                        JLookupSwitchStmt jLookupSwitchStmt = (JLookupSwitchStmt) valueInfoSwitchStmt;
                        List<IntConstant> lookupValues = jLookupSwitchStmt.getLookupValues();
                        for (IntConstant lookupValue : lookupValues) {
                            mayValues.add(lookupValue.toString());
                        }
                    } else if (valueInfoSwitchStmt instanceof JTableSwitchStmt) {
                        JTableSwitchStmt jTableSwitchStmt = (JTableSwitchStmt) valueInfoSwitchStmt;
                        ArrayList<Integer> tableValues = ResolveJTableSwitchConstant.resolveConstant(jTableSwitchStmt);
                        for (Integer tableValue : tableValues) {
                            mayValues.add(tableValue.toString());
                        }
                    }
                }
            }

            // Source Info
            Set<String> sources = new HashSet<>();

            for (ResultSourceInfo resultSourceInfo : resultsMap.get(resultSinkInfo)) {
                Stmt source = resultSourceInfo.getStmt();

                if (!source.containsInvokeExpr()) {
                    throw new RuntimeException("The source does not have an invoke: " + source.toString());
                }

                InvokeExpr expr = source.getInvokeExpr();
                SootMethod exprMethod = expr.getMethod();
//                sources.add(option);
                String option = this.sourceToOptions.get(exprMethod.getSignature());

                // gen result
                if (!stringTags.isEmpty()) {
                    for (StringTag stringTag : stringTags) {
                        SourceSinkInfo sourceSinkInfo = new SourceSinkInfo(packageName, className, methodBytecodeSignature, javaLine, bytecodeIndex, operationType, Collections.singleton(option), stringTag.getInfo(), mayValues);
                        if (!option2result.containsKey(option)) {
                            List<SourceSinkInfo> sourceSinkInfos = new ArrayList<>();
                            sourceSinkInfos.add(sourceSinkInfo);
                            option2result.put(option,sourceSinkInfos);
                        } else {
                            option2result.get(option).add(sourceSinkInfo);
                        }
                    }
                } else {
                    SourceSinkInfo sourceSinkInfo = new SourceSinkInfo(packageName, className, methodBytecodeSignature, javaLine, bytecodeIndex, operationType, Collections.singleton(option), originalStmt, mayValues);
                    if (!option2result.containsKey(option)) {
                        List<SourceSinkInfo> sourceSinkInfos = new ArrayList<>();
                        sourceSinkInfos.add(sourceSinkInfo);
                        option2result.put(option,sourceSinkInfos);
                    } else {
                        option2result.get(option).add(sourceSinkInfo);
                    }
                }
            }
        }
        iCfg.purge();
        ObjectMapper mapper = new ObjectMapper();
        for (Map.Entry<String, List<SourceSinkInfo>> entry : option2result.entrySet()) {
            File outputFile = new File(TaintTracking.OUTPUT_DIR + File.separator + this.targetName + File.separator + "20480m" + File.separator + entry.getKey() + ".json");
            outputFile.getParentFile().mkdirs();
            mapper.writeValue(outputFile, entry.getValue());
        }
    }

    public List<String> getSinks() {
        return sinks;
    }

    public List<String> getSources() {
        return new ArrayList<>(sourceToOptions.keySet());
    }

    private void initSource(int targetOrder) {
        for (int i = 0; i < SubjectSysInfo.CONF_API[targetOrder].length; i++) {
            int i1 = SubjectSysInfo.CONF_API[targetOrder][i].indexOf("|");
            if (i1 == -1)
                sourceToOptions.put(SubjectSysInfo.CONF_API[targetOrder][i], "para" + i);
            else {
                String methodSignature = SubjectSysInfo.CONF_API[targetOrder][i].substring(0, i1);
                String para = SubjectSysInfo.CONF_API[targetOrder][i].substring(i1 + 1);
                sourceToOptions.put(methodSignature, para);
            }
        }
    }

    public void collectOptionType() throws IOException {
        class OptionType {
            String optionName;
            String optionType;

            public OptionType(String optionName, String optionType) {
                this.optionName = optionName;
                this.optionType = optionType;
            }

            public String getOptionName() {
                return optionName;
            }

            public void setOptionName(String optionName) {
                this.optionName = optionName;
            }

            public String getOptionType() {
                return optionType;
            }

            public void setOptionType(String optionType) {
                this.optionType = optionType;
            }
        }
        ArrayList<OptionType> optionTypes = new ArrayList<>();
        for (Map.Entry<String, String> sourceOption : sourceToOptions.entrySet()) {
            optionTypes.add(new OptionType(sourceOption.getValue(), sourceOption.getKey().split(" ")[1]));
        }
        ObjectMapper mapper = new ObjectMapper();
        File outputFile = new File(TaintTracking.OUTPUT_DIR + File.separator + this.targetName + File.separator + this.targetName + "optionType" + ".json");
        outputFile.getParentFile().mkdirs();
        mapper.writeValue(outputFile, optionTypes);
    }
}
