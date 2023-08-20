package edu.sysu.jimmy.soot.jimple.internal;

import soot.Unit;
import soot.jimple.internal.JTableSwitchStmt;

import java.util.ArrayList;

public class ResolveJTableSwitchConstant {
    public static ArrayList<Integer> resolveConstant(JTableSwitchStmt jTableSwitchStmt) {
        ArrayList<Integer> constants = new ArrayList<>();
        Unit defaultTarget = jTableSwitchStmt.getDefaultTarget();
        int lowIndex = jTableSwitchStmt.getLowIndex();
        int highIndex = jTableSwitchStmt.getHighIndex();
        for (int i = lowIndex; i < highIndex; i++) {
            Unit target = jTableSwitchStmt.getTarget(i - lowIndex);
            if (target != null && !target.equals(defaultTarget))
                constants.add(i);
        }
        Unit target = jTableSwitchStmt.getTarget(highIndex - lowIndex);
        if (target!= null && !target.equals(defaultTarget))
            constants.add(highIndex);
        return constants;
    }

}
