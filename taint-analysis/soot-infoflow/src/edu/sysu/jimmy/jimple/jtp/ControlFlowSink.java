package edu.sysu.jimmy.jimple.jtp;


import soot.*;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.jimple.SwitchStmt;
import soot.util.Chain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlFlowSink extends BodyTransformer {
    private static final ControlFlowSink instance = new ControlFlowSink();

    private ControlFlowSink() {
    }

    public static ControlFlowSink v() {
        return ControlFlowSink.instance;
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        Chain<Unit> units = b.getUnits();
        Chain<Local> locals = b.getLocals();
        Map<Unit, Set<Value>> unitsToValues = new HashMap<>();
        // handle the control situation
        for (Unit unit : units) {
            // record local variable related to unit
            if (unit instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) unit;
                Value cond = ifStmt.getCondition();

                if (cond instanceof ConditionExpr) {
                    ConditionExpr condExpr = (ConditionExpr) cond;
                    Value op1 = condExpr.getOp1();
                    Value op2 = condExpr.getOp2();

                    if (locals.contains(op1)) {
                        if (!unitsToValues.containsKey(unit)) {
                            unitsToValues.put(unit, new HashSet<>());
                        }

                        unitsToValues.get(unit).add(op1);
                    }

                    if (locals.contains(op2)) {
                        if (!unitsToValues.containsKey(unit)) {
                            unitsToValues.put(unit, new HashSet<>());
                        }

                        unitsToValues.get(unit).add(op2);
                    }
                } else {
                    throw new RuntimeException("Other type of condition");
                }
            } else if (unit instanceof SwitchStmt) {
                SwitchStmt switchStmt = (SwitchStmt) unit;
                Value cond = switchStmt.getKey();

                if (!unitsToValues.containsKey(unit)) {
                    unitsToValues.put(unit, new HashSet<>());
                }

                unitsToValues.get(unit).add(cond);
            }
        }
        if (unitsToValues.isEmpty())
            return;
        InstrumentManager.insertSink(units, unitsToValues, "void sink(java.lang.Object)", null);
    }
}
