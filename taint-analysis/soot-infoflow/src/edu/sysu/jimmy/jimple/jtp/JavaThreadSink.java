package edu.sysu.jimmy.jimple.jtp;

import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegion;
import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegionFinder;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.util.Chain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaThreadSink extends BodyTransformer {
    private static JavaThreadSink instance = new JavaThreadSink();
    public static final String directlySink = "void directlyThreadSink(java.lang.Object)";
    public static final String ifControlSink = "void ifThreadSink(java.lang.Object)";
    public static final String loopControlSink = "void loopThreadSink(java.lang.Object)";

    private JavaThreadSink() {
    }

    public static JavaThreadSink v() {
        return instance;
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        UnitGraph g = null;
        if (b.getMethod().getName().contains("<clinit>") || b.getMethod().getName().contains("<init>"))

            g = new BriefUnitGraph(b);
        else
            g = new EnhancedUnitGraph(b);
        Set<Loop> loops = new LoopFinder().getLoops(g);
        Set<IfRegion> ifRegions = null;
        try {
            ifRegions = new IfRegionFinder().getIfRegions(g, loops);
        } catch (Exception | Error ignored) {
            ifRegions = null;
        }
        Chain<Unit> units = b.getUnits();
        Chain<Local> locals = b.getLocals();
        Map<Unit, Set<Value>> unitsToValuesDirectly = new HashMap<>();
        Map<Unit, Set<Value>> unitsToValuesIfControl = new HashMap<>();
        Map<Unit, Set<Value>> unitsToValuesLoopControl = new HashMap<>();
        Map<Unit, Set<Unit>> sinkUnitToOriginalUnit = new HashMap<>();
        for (Unit unit : units) {
            checkThread(unit, locals, loops, ifRegions, unitsToValuesDirectly, unitsToValuesIfControl, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
        }
        InstrumentManager.insertSink(units, unitsToValuesDirectly, directlySink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesIfControl, ifControlSink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesLoopControl, loopControlSink, sinkUnitToOriginalUnit);

    }

    public static void checkThread(Unit unit, Chain<Local> locals, Set<Loop> loops, Set<IfRegion> ifRegions,
                                   Map<Unit, Set<Value>> unitsToValuesDirectly,
                                   Map<Unit, Set<Value>> unitsToValuesIfControl,
                                   Map<Unit, Set<Value>> unitsToValuesLoopControl,
                                   Map<Unit, Set<Unit>> sinkUnitToOriginalUnit
    ) {
        Stmt stmt = (Stmt) unit;
        if (stmt.containsInvokeExpr()) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            SootMethod invokedMethod = invokeExpr.getMethod();
            SootClass invokedMethodDeclaringClass = invokedMethod.getDeclaringClass();
            String packageName = invokedMethodDeclaringClass.getPackageName();
            // implement runnable interface
            if ("run".compareTo(invokedMethod.getName()) == 0) {
                SootClass declaringClass = invokedMethodDeclaringClass;
                boolean isImplRunnable = false;
                if (declaringClass.getInterfaceCount() > 0) {
                    Chain<SootClass> interfaces = declaringClass.getInterfaces();
                    for (SootClass anInterface : interfaces) {
                        if (anInterface.getName().contains("java.lang.Runnable")) {
                            isImplRunnable = true;
                            break;
                        }
                    }
                    if (isImplRunnable) {
                        // Directly ??
//                        Value value = invokeExpr.getUseBoxes().get(0).getValue();
//                        InstrumentManager.directlyImpact(unit, Collections.singletonList(value), locals, unitsToValuesDirectly, sinkUnitToOriginalUnit);
                        // Loop Control
                        InstrumentManager.loopImpact(unit, locals, loops, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
                        // If Control
                        InstrumentManager.ifImpact(unit, locals, ifRegions, unitsToValuesIfControl, sinkUnitToOriginalUnit);
                    }
                }
            } else if ((invokedMethodDeclaringClass.getName().contains("java.lang.Thread") ||
                    packageName.contains("java.util.concurrent")) &&
                    !(packageName.contains("java.util.concurrent.locks") || packageName.contains("java.util.concurrent.atomic")) &&
                    !invokedMethodDeclaringClass.getName().endsWith("Exception")) {
                // Directly
                List<Value> args = invokeExpr.getArgs();
                if (!args.isEmpty())
                    InstrumentManager.directlyImpact(unit, args, locals, unitsToValuesDirectly, sinkUnitToOriginalUnit);
                // Loop Control
                InstrumentManager.loopImpact(unit, locals, loops, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
                // If Control
                InstrumentManager.ifImpact(unit, locals, ifRegions, unitsToValuesIfControl, sinkUnitToOriginalUnit);
            }
        }
    }
}
