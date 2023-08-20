package edu.sysu.jimmy.jimple.jtp;

import edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic.IfRegion;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.tagkit.BytecodeOffsetTag;
import soot.tagkit.LineNumberTag;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.util.Chain;

import java.util.*;

public class InstrumentManager {
    public static void insertSink(Chain<Unit> units, Map<Unit, Set<Value>> unitsToValues, String methodSignature, Map<Unit, Set<Unit>> sinkUnitToOriginalUnit) {
        if (unitsToValues.isEmpty())
            return;
        SootClass sootClass = Scene.v().loadClassAndSupport("edu.sysu.jimmy.analysis.option.MySink");
        SootMethod sootMethod = sootClass.getMethod(methodSignature);
        for (Map.Entry<Unit, Set<Value>> unitToValues : unitsToValues.entrySet()) {
            for (Value value : unitToValues.getValue()) {
                // associate variable with sink which is contained in control decision statement
                StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(sootMethod.makeRef(), value);
                Stmt stmt = Jimple.v().newInvokeStmt(staticInvokeExpr);
                Tag lineNumberTag = null;
                Tag bytecodeOffsetTag = null;
                Tag stringTag = null;

                Unit key_unit = unitToValues.getKey();
                for (Tag tag : key_unit.getTags()) {
                    if (tag instanceof LineNumberTag) {
                        int javaLine = ((LineNumberTag) tag).getLineNumber();
                        lineNumberTag = new LineNumberTag(javaLine);
                    }

                    if (tag instanceof BytecodeOffsetTag) {
                        int bytecodeIndex = ((BytecodeOffsetTag) tag).getBytecodeOffset();
                        bytecodeOffsetTag = new BytecodeOffsetTag(bytecodeIndex);
                    }

                }

                if (lineNumberTag == null) {
                    lineNumberTag = new LineNumberTag(-1);
                }

                if (bytecodeOffsetTag == null) {
                    bytecodeOffsetTag = new BytecodeOffsetTag(-1);
                }
                stmt.addTag(lineNumberTag);
                stmt.addTag(bytecodeOffsetTag);
                int temp_num = 1;
                if (sinkUnitToOriginalUnit != null && !sinkUnitToOriginalUnit.isEmpty())
                    for (Unit unit : sinkUnitToOriginalUnit.get(key_unit)) {
                        stringTag = new StringTag(unit.toString(), "original" + temp_num++);
                        stmt.addTag(stringTag);
                    }
                // Stub sink
                units.insertBefore(stmt, key_unit);
            }
        }
    }

    public static void directlyImpact(Unit unit, List<Value> args, Chain<Local> locals, Map<Unit, Set<Value>> unitsToValuesDirectly, Map<Unit, Set<Unit>> sinkUnitToOriginalUnit) {
//        List<Value> args = invokeExpr.getArgs();
        for (Value arg : args) {
            if (locals.contains(arg)) {
                if (!unitsToValuesDirectly.containsKey(unit)) {
                    unitsToValuesDirectly.put(unit, new HashSet<>());
                }
                unitsToValuesDirectly.get(unit).add(arg);
            }
        }
        if (!sinkUnitToOriginalUnit.containsKey(unit))
            sinkUnitToOriginalUnit.put(unit, new HashSet<>());
        sinkUnitToOriginalUnit.get(unit).add(unit);
    }

    public static void loopImpact(Unit unit, Chain<Local> locals, Set<Loop> loops, Map<Unit, Set<Value>> unitsToValuesLoopControl, Map<Unit, Set<Unit>> sinkUnitToOriginalUnit) {
        Stmt stmt = (Stmt) unit;
        for (Loop loop : loops) {
            List<Stmt> loopStatements = loop.getLoopStatements();
            if (loopStatements.contains(stmt)) {
                Collection<Stmt> loopExits = loop.getLoopExits();
                for (Stmt loopExit : loopExits) {
                    if (loopExit instanceof IfStmt) {
                        IfStmt ifStmt = (IfStmt) loopExit;
                        Value condition = ifStmt.getCondition();
                        if (condition instanceof ConditionExpr) {
                            ConditionExpr conditionExpr = (ConditionExpr) condition;
                            Value op1 = conditionExpr.getOp1();
                            Value op2 = conditionExpr.getOp2();
                            if (locals.contains(op1)) {
                                if (!unitsToValuesLoopControl.containsKey(ifStmt)) {
                                    unitsToValuesLoopControl.put(ifStmt, new HashSet<>());
                                }
                                unitsToValuesLoopControl.get(ifStmt).add(op1);
                            }

                            if (locals.contains(op2)) {
                                if (!unitsToValuesLoopControl.containsKey(ifStmt)) {
                                    unitsToValuesLoopControl.put(ifStmt, new HashSet<>());
                                }
                                unitsToValuesLoopControl.get(ifStmt).add(op2);
                            }
                            if (!sinkUnitToOriginalUnit.containsKey(ifStmt))
                                sinkUnitToOriginalUnit.put(ifStmt, new HashSet<>());
                            sinkUnitToOriginalUnit.get(ifStmt).add(unit);
                        }
                    }
                }
/*                Stmt conditionStmt = null;
                conditionStmt = IfRegionFinder.getLoopCondition(loop);
*//*                        Stmt backJumpStmt = loop.getBackJumpStmt();
                        if (backJumpStmt instanceof IfStmt) {
                            conditionStmt = backJumpStmt;
                        } else
                            for (Stmt loopBodyStmt : loopStatements) {
                                if (loopBodyStmt instanceof IfStmt) {
                                    conditionStmt = loopBodyStmt;
                                    break;
                                }
                            }*//*
                if (conditionStmt == null)
                    continue;*/

            }
        }
    }

    public static void ifImpact(Unit unit, Chain<Local> locals, Set<IfRegion> ifRegions, Map<Unit, Set<Value>> unitsToValuesIfControl, Map<Unit, Set<Unit>> sinkUnitToOriginalUnit) {
        Stmt stmt = (Stmt) unit;
        if (ifRegions != null)
            for (IfRegion ifRegion : ifRegions) {
                List<Stmt> ifStatements = ifRegion.getIfStatements();
                if (ifStatements.contains(stmt)) {
                    Stmt ifRegionHeader = ifRegion.getHeader();
                    if (!(ifRegionHeader instanceof IfStmt))
                        continue;
                    IfStmt header = (IfStmt) ifRegionHeader;
                    Value condition = header.getCondition();
                    if (condition instanceof ConditionExpr) {
                        ConditionExpr conditionExpr = (ConditionExpr) condition;
                        Value op1 = conditionExpr.getOp1();
                        Value op2 = conditionExpr.getOp2();
                        if (locals.contains(op1)) {
                            if (!unitsToValuesIfControl.containsKey(header)) {
                                unitsToValuesIfControl.put(header, new HashSet<>());
                            }
                            unitsToValuesIfControl.get(header).add(op1);
                        }

                        if (locals.contains(op2)) {
                            if (!unitsToValuesIfControl.containsKey(header)) {
                                unitsToValuesIfControl.put(header, new HashSet<>());
                            }
                            unitsToValuesIfControl.get(header).add(op2);
                        }
                        if (!sinkUnitToOriginalUnit.containsKey(header))
                            sinkUnitToOriginalUnit.put(header, new HashSet<>());
                        sinkUnitToOriginalUnit.get(header).add(unit);
                    }
                }
            }
    }
}
