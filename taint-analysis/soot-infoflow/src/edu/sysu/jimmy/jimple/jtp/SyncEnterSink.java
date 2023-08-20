package edu.sysu.jimmy.jimple.jtp;

import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegion;
import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegionFinder;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.util.Chain;

import java.util.*;

public class SyncEnterSink extends BodyTransformer {
    private static SyncEnterSink instance = new SyncEnterSink();
    public static final String directlySink = "void directlyLockSink(java.lang.Object)";
    public static final String ifControlSink = "void ifLockSink(java.lang.Object)";
    public static final String loopControlSink = "void loopLockSink(java.lang.Object)";

    private SyncEnterSink() {
    }

    public static SyncEnterSink v() {
        return instance;
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        // handle the sync situation
        UnitGraph g = null;
        if (b.getMethod().getName().contains("<clinit>") || b.getMethod().getName().contains("<init>"))

            g = new BriefUnitGraph(b);
        else g = new EnhancedUnitGraph(b);
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
            checkSync(unit, locals, loops, ifRegions, unitsToValuesDirectly, unitsToValuesIfControl, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
        }
        InstrumentManager.insertSink(units, unitsToValuesDirectly, directlySink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesIfControl, ifControlSink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesLoopControl, loopControlSink, sinkUnitToOriginalUnit);

    }

    public static void checkSync(Unit unit, Chain<Local> locals, Set<Loop> loops, Set<IfRegion> ifRegions,
                                 Map<Unit, Set<Value>> unitsToValuesDirectly,
                                 Map<Unit, Set<Value>> unitsToValuesIfControl,
                                 Map<Unit, Set<Value>> unitsToValuesLoopControl,
                                 Map<Unit, Set<Unit>> sinkUnitToOriginalUnit
    ) {
        Stmt stmt = (Stmt) unit;
        // synchronized keyword
        if (unit instanceof JEnterMonitorStmt) {
            // Directly
            JEnterMonitorStmt enterMonitorStmt = (JEnterMonitorStmt) unit;
            Value op = enterMonitorStmt.getOp();
            InstrumentManager.directlyImpact(unit, Collections.singletonList(op), locals, unitsToValuesDirectly, sinkUnitToOriginalUnit);
            // Loop Control
            InstrumentManager.loopImpact(unit, locals, loops, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
            // If Control
            InstrumentManager.ifImpact(unit, locals, ifRegions, unitsToValuesIfControl, sinkUnitToOriginalUnit);
        } else if (stmt.containsInvokeExpr()) { //  explicitly lock
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            SootMethod invokeExprMethod = invokeExpr.getMethod();
            SootClass invokeExprMethodDeclaringClass = invokeExprMethod.getDeclaringClass();
            String invokeExprMethodDeclaringClassPackageName = invokeExprMethodDeclaringClass.getPackageName();
            if ((invokeExprMethodDeclaringClassPackageName.contains("java.util.concurrent.locks") || invokeExprMethodDeclaringClassPackageName.contains("java.util.concurrent.atomic")) && !invokeExprMethodDeclaringClass.getName().endsWith("Exception")) {
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

    /*public static void insertSink(Chain<Unit> units, Map<Unit, List<Value>> unitsToValues, String methodSignature) {
        if (unitsToValues.isEmpty())
            return;
        SootClass sootClass = Scene.v().loadClassAndSupport("edu.sysu.cs.jimmy.analysis.option.MySink");
        SootMethod sootMethod = sootClass.getMethod(methodSignature);
        for (Map.Entry<Unit, List<Value>> unitToValues : unitsToValues.entrySet()) {
            for (Value value : unitToValues.getValue()) {
                // associate variable with sink which is contained in control decision statement
                StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(sootMethod.makeRef(), value);
                Stmt stmt = Jimple.v().newInvokeStmt(staticInvokeExpr);
                Tag lineNumberTag = null;
                Tag bytecodeOffsetTag = null;

                for (Tag tag : unitToValues.getKey().getTags()) {
                    if (tag instanceof LineNumberTag) {
                        int javaLine = ((LineNumberTag) tag).getLineNumber();
                        lineNumberTag = new LineNumberTag(javaLine);
                    }

                    if (tag instanceof BytecodeOffsetTag) {
                        int bytecodeIndex = ((BytecodeOffsetTag) tag).getBytecodeOffset();
                        bytecodeOffsetTag = new BytecodeOffsetTag(bytecodeIndex);
                    }
                    if (tag instanceof MayValueTag)
                        stmt.addTag(tag);
                }

                if (lineNumberTag == null) {
                    lineNumberTag = new LineNumberTag(-1);
                }

                if (bytecodeOffsetTag == null) {
                    bytecodeOffsetTag = new BytecodeOffsetTag(-1);
                }

                stmt.addTag(lineNumberTag);
                stmt.addTag(bytecodeOffsetTag);

                // Stub sink
                units.insertBefore(stmt, unitToValues.getKey());
            }
        }
    }*/
}
