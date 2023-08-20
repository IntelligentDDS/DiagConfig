package edu.sysu.jimmy.jimple.infoflow.sourcesSinks.manager;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import heros.solver.IDESolver;
import soot.*;
import soot.jimple.*;
import soot.jimple.infoflow.InfoflowManager;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition;
import soot.jimple.infoflow.sourcesSinks.manager.DefaultSourceSinkManager;
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager;
import soot.jimple.infoflow.sourcesSinks.manager.SinkInfo;
import soot.jimple.infoflow.sourcesSinks.manager.SourceInfo;
import soot.jimple.infoflow.util.SystemClassHandler;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.BriefUnitGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SVGBatikSouceSinkManager implements ISourceSinkManager {
    public Collection<String> args;

    protected Collection<String> sourceDefs;
    protected Collection<String> sinkDefs;

    private Collection<SootMethod> sources;
    private Collection<SootMethod> sinks;

    private Collection<String> returnTaintMethodDefs;
    private Collection<String> parameterTaintMethodDefs;

    private Collection<SootMethod> returnTaintMethods;
    private Collection<SootMethod> parameterTaintMethods;
    protected final LoadingCache<SootClass, Collection<SootClass>> interfacesOf = IDESolver.DEFAULT_CACHE_BUILDER
            .build(new CacheLoader<SootClass, Collection<SootClass>>() {

                @Override
                public Collection<SootClass> load(SootClass sc) throws Exception {
                    Set<SootClass> set = new HashSet<SootClass>(sc.getInterfaceCount());
                    for (SootClass i : sc.getInterfaces()) {
                        set.add(i);
                        set.addAll(interfacesOf.getUnchecked(i));
                    }
                    SootClass superClass = sc.getSuperclassUnsafe();
                    if (superClass != null)
                        set.addAll(interfacesOf.getUnchecked(superClass));
                    return set;
                }

            });

    public SVGBatikSouceSinkManager(Collection<String> args, Collection<String> sourceDefs, Collection<String> sinkDefs) {
        this(args, sourceDefs, sinkDefs, null, null);
    }

    public SVGBatikSouceSinkManager(Collection<String> args, Collection<String> sourceDefs, Collection<String> sinkDefs,
                                    Collection<String> returnTaintMethodDefs, Collection<String> parameterTaintMethodDefs) {
        this.args = args;
        this.sourceDefs = sourceDefs;
        this.sinkDefs = sinkDefs;
        this.returnTaintMethodDefs = (returnTaintMethodDefs != null) ? returnTaintMethodDefs : new HashSet<>();
        this.parameterTaintMethodDefs = (parameterTaintMethodDefs != null) ? parameterTaintMethodDefs : new HashSet<>();
    }

    public SourceInfo getSourceInfo(Stmt sCallSite, InfoflowManager manager) {
        SootMethod callee = sCallSite.containsInvokeExpr() ? sCallSite.getInvokeExpr().getMethod() : null;

        AccessPath targetAP = null;
        if (isSourceMethod(manager, sCallSite)) {
            if (callee.getReturnType() != null && sCallSite instanceof DefinitionStmt) {
                // Taint the return value
                Value leftOp = ((DefinitionStmt) sCallSite).getLeftOp();
                targetAP = manager.getAccessPathFactory().createAccessPath(leftOp, true);
            } else if (sCallSite.getInvokeExpr() instanceof InstanceInvokeExpr) {
                // Taint the base object
                Value base = ((InstanceInvokeExpr) sCallSite.getInvokeExpr()).getBase();
                targetAP = manager.getAccessPathFactory().createAccessPath(base, true);
            }
        }
        // Check whether we need to taint parameters
        else if (sCallSite instanceof IdentityStmt) {
            IdentityStmt istmt = (IdentityStmt) sCallSite;
            if (istmt.getRightOp() instanceof ParameterRef) {
                ParameterRef pref = (ParameterRef) istmt.getRightOp();
                SootMethod currentMethod = manager.getICFG().getMethodOf(istmt);
                if (parameterTaintMethods != null && parameterTaintMethods.contains(currentMethod))
                    targetAP = manager.getAccessPathFactory()
                            .createAccessPath(currentMethod.getActiveBody().getParameterLocal(pref.getIndex()), true);
            }
        }

        if (targetAP == null)
            return null;

        // Create the source information data structure
        return new SourceInfo(callee == null ? null : new MethodSourceSinkDefinition(new SootMethodAndClass(callee)),
                targetAP);
    }

    protected boolean isSourceMethod(InfoflowManager manager, Stmt sCallSite) {

        // We only support method calls
        if (!sCallSite.containsInvokeExpr())
            return false;
        // Check for arguments
        InvokeExpr invokeExpr = sCallSite.getInvokeExpr();
        int argCount = invokeExpr.getArgCount();
        if (argCount <= 0)
            return false;

        // Check for a direct match
        SootMethod callee = invokeExpr.getMethod();
        if (!this.sources.contains(callee))
            return false;
        Value arg1 = invokeExpr.getArg(0);
        Type arg1Type = arg1.getType();
        String typeStr = arg1Type.toString();
        if ("org.apache.batik.transcoder.TranscodingHints$Key".compareTo(typeStr) != 0)
            return false;
        IInfoflowCFG icfg = manager.getICFG();
        SootMethod caller = icfg.getMethodOf(sCallSite);
        Body body = caller.getActiveBody();
        BriefUnitGraph g = new BriefUnitGraph(body);
        List<Unit> predsOf = g.getPredsOf(sCallSite);
        if (predsOf.isEmpty())
            return false;
        List<ValueBox> useAndDefBoxes = predsOf.get(0).getUseAndDefBoxes();
        if (useAndDefBoxes.isEmpty())
            return false;
        if (!arg1.equivTo(useAndDefBoxes.get(0).getValue()))
            return false;

        String option = useAndDefBoxes.get(1).getValue().toString();
        option = option.substring(option.indexOf("_") + 1, option.length() - 1);
        if (this.args.contains(option))
            return true;


        // Check whether we have any of the interfaces on the list
        String subSig = callee.getSubSignature();
        for (SootClass i : interfacesOf.getUnchecked(invokeExpr.getMethod().getDeclaringClass())) {
            SootMethod sm = i.getMethodUnsafe(subSig);
            if (sm != null && this.sources.contains(sm) && this.args.contains(option))
                return true;
        }

        // Ask the CFG in case we don't know any better
        for (SootMethod sm : manager.getICFG().getCalleesOfCallAt(sCallSite)) {
            if (this.sources.contains(sm) && this.args.contains(option))
                return true;
        }

        // nothing found
        return false;


    }


    @Override
    public SinkInfo getSinkInfo(Stmt sCallSite, InfoflowManager manager, AccessPath ap) {
        // Check whether values returned by the current method are to be
        // considered as sinks
        if (this.returnTaintMethods != null && sCallSite instanceof ReturnStmt) {
            SootMethod sm = manager.getICFG().getMethodOf(sCallSite);
            if (this.returnTaintMethods != null && this.returnTaintMethods.contains(sm))
                return new SinkInfo(new MethodSourceSinkDefinition(new SootMethodAndClass(sm)));
        }

        // Check whether the callee is a sink
        if (this.sinks != null && !sinks.isEmpty() && sCallSite.containsInvokeExpr()) {
            InvokeExpr iexpr = sCallSite.getInvokeExpr();

            // Is this method on the list?
            SootMethodAndClass smac = isSinkMethod(manager, sCallSite);
            if (smac != null) {
                // Check that the incoming taint is visible in the callee at all
                if (SystemClassHandler.v().isTaintVisible(ap, iexpr.getMethod())) {
                    // If we don't have an access path, we can only
                    // over-approximate
                    if (ap == null)
                        return new SinkInfo(new MethodSourceSinkDefinition(smac));

                    // The given access path must at least be referenced
                    // somewhere in the sink
                    if (!ap.isStaticFieldRef()) {
                        for (int i = 0; i < iexpr.getArgCount(); i++)
                            if (iexpr.getArg(i) == ap.getPlainValue()) {
                                if (ap.getTaintSubFields() || ap.isLocal())
                                    return new SinkInfo(new MethodSourceSinkDefinition(smac));
                            }
                        if (iexpr instanceof InstanceInvokeExpr)
                            if (((InstanceInvokeExpr) iexpr).getBase() == ap.getPlainValue())
                                return new SinkInfo(new MethodSourceSinkDefinition(smac));
                    }
                }
            }
        }

        return null;
    }


    /**
     * Checks whether the given call sites invokes a sink method
     *
     * @param manager   The manager object providing access to the configuration and
     *                  the interprocedural control flow graph
     * @param sCallSite The call site to check
     * @return The method that was discovered as a sink, or null if no sink could be
     * found
     */
    protected SootMethodAndClass isSinkMethod(InfoflowManager manager, Stmt sCallSite) {
        // Is the method directly in the sink set?
        SootMethod callee = sCallSite.getInvokeExpr().getMethod();
        if (this.sinks.contains(callee))
            return new SootMethodAndClass(callee);

        // Check whether we have any of the interfaces on the list
        String subSig = callee.getSubSignature();
        for (SootClass i : interfacesOf.getUnchecked(sCallSite.getInvokeExpr().getMethod().getDeclaringClass())) {
            SootMethod sm = i.getMethodUnsafe(subSig);
            if (sm != null && this.sinks.contains(sm))
                return new SootMethodAndClass(sm);
        }

        // Ask the CFG in case we don't know any better
        for (SootMethod sm : manager.getICFG().getCalleesOfCallAt(sCallSite)) {
            if (this.sinks.contains(sm))
                return new SootMethodAndClass(sm);
        }

        // nothing found
        return null;
    }

    @Override
    public void initialize() {
        if (sourceDefs != null) {
            sources = new HashSet<>();
            for (String signature : sourceDefs) {
                SootMethod sm = Scene.v().grabMethod(signature);
                if (sm != null)
                    sources.add(sm);
            }
            sourceDefs = null;
        }

        if (sinkDefs != null) {
            sinks = new HashSet<>();
            for (String signature : sinkDefs) {
                SootMethod sm = Scene.v().grabMethod(signature);
                if (sm != null)
                    sinks.add(sm);
            }
            sinkDefs = null;
        }

        if (returnTaintMethodDefs != null) {
            returnTaintMethods = new HashSet<>();
            for (String signature : returnTaintMethodDefs) {
                SootMethod sm = Scene.v().grabMethod(signature);
                if (sm != null)
                    returnTaintMethods.add(sm);
            }
            returnTaintMethodDefs = null;
        }

        if (parameterTaintMethodDefs != null) {
            parameterTaintMethods = new HashSet<>();
            for (String signature : parameterTaintMethodDefs) {
                SootMethod sm = Scene.v().grabMethod(signature);
                if (sm != null)
                    parameterTaintMethods.add(sm);
            }
            parameterTaintMethodDefs = null;
        }
    }
}
