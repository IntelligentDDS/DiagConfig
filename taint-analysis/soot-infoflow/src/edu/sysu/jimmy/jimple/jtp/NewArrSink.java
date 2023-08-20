package edu.sysu.jimmy.jimple.jtp;

import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegion;
import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegionFinder;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.util.Chain;

import java.util.*;

public class NewArrSink extends BodyTransformer {
    private static NewArrSink instance = new NewArrSink();
    public static final String directlyNewArrSink = "void directlyNewArrSink(java.lang.Object)";
    public static final String ifNewArrSink = "void ifNewArrSink(java.lang.Object)";
    public static final String loopNewArrSink = "void loopNewArrSink(java.lang.Object)";

    private NewArrSink() {
    }

    public static NewArrSink v() {
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

        // handle the sync situation
        for (Unit unit : units) {
            // record local variable related to unit
            checkNewArr(unit, locals, loops, ifRegions, unitsToValuesDirectly, unitsToValuesIfControl, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
        }
        InstrumentManager.insertSink(units, unitsToValuesDirectly, directlyNewArrSink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesIfControl, ifNewArrSink, sinkUnitToOriginalUnit);
        InstrumentManager.insertSink(units, unitsToValuesLoopControl, loopNewArrSink, sinkUnitToOriginalUnit);
    }

    public static void checkNewArr(Unit unit, Chain<Local> locals, Set<Loop> loops, Set<IfRegion> ifRegions,
                                   Map<Unit, Set<Value>> unitsToValuesDirectly,
                                   Map<Unit, Set<Value>> unitsToValuesIfControl,
                                   Map<Unit, Set<Value>> unitsToValuesLoopControl,
                                   Map<Unit, Set<Unit>> sinkUnitToOriginalUnit
    ) {
        if (unit instanceof JAssignStmt) {
            Stmt stmt = (Stmt) unit;
            JAssignStmt assignStmt = (JAssignStmt) stmt;
            Value rightOp = assignStmt.getRightOp();
            if (rightOp instanceof JNewArrayExpr || rightOp instanceof JNewMultiArrayExpr) {
                List<ValueBox> useBoxes = rightOp.getUseBoxes();
                ArrayList<Value> values = new ArrayList<>();
                // Directly
                for (ValueBox vb : useBoxes) {
                    values.add(vb.getValue());
                }
                if (!values.isEmpty())
                    InstrumentManager.directlyImpact(unit, values, locals, unitsToValuesDirectly, sinkUnitToOriginalUnit);
                // Loop Control
                InstrumentManager.loopImpact(unit, locals, loops, unitsToValuesLoopControl, sinkUnitToOriginalUnit);
                // If Control
                if (ifRegions != null)
                    InstrumentManager.ifImpact(unit, locals, ifRegions, unitsToValuesIfControl, sinkUnitToOriginalUnit);
            }
        }
    }
}
