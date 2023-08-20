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

public class JavaIOSink extends BodyTransformer {
    private static JavaIOSink instance = new JavaIOSink();
    public static String directlyIoSink = "void directlyIoSink(java.lang.Object)";
    public static String ifIoSink = "void ifIoSink(java.lang.Object)";
    public static String loopIoSink = "void loopIoSink(java.lang.Object)";

    private JavaIOSink() {
    }

    public static JavaIOSink v() {
        return JavaIOSink.instance;
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
            checkIO(unit, locals, loops, ifRegions, unitsToValuesDirectly, unitsToValuesIfControl, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
        }
        InstrumentManager.insertSink(units, unitsToValuesDirectly, directlyIoSink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesIfControl, ifIoSink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesLoopControl, loopIoSink, sinkUnitToOriginalUnit);
    }

    public static void checkIO(Unit unit, Chain<Local> locals, Set<Loop> loops, Set<IfRegion> ifRegions,
                               Map<Unit, Set<Value>> unitsToValuesDirectly,
                               Map<Unit, Set<Value>> unitsToValuesIfControl,
                               Map<Unit, Set<Value>> unitsToValuesLoopControl,
                               Map<Unit, Set<Unit>> sinkUnitToOriginalUnit
    ) {
        Stmt stmt = (Stmt) unit;
        if (stmt.containsInvokeExpr()) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            SootClass declaringClass = invokeExpr.getMethod().getDeclaringClass();
            String packageName = declaringClass.getPackageName();
            if ((packageName.contains("java.io") || packageName.contains("java.nio")) && !declaringClass.getName().endsWith("Exception")) {
                // Directly
                List<Value> args = invokeExpr.getArgs();
                if (!args.isEmpty())
                    InstrumentManager.directlyImpact(unit, args, locals, unitsToValuesDirectly, sinkUnitToOriginalUnit);
                // Loop Control
                InstrumentManager.loopImpact(unit, locals, loops, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
                // If Control
                if (ifRegions != null)
                    InstrumentManager.ifImpact(unit, locals, ifRegions, unitsToValuesIfControl, sinkUnitToOriginalUnit);
            }

        }

    }
}
