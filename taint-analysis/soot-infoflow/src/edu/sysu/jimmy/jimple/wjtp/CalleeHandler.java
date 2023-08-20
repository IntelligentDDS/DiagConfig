package edu.sysu.jimmy.jimple.wjtp;

import soot.*;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.Tag;

import java.util.Iterator;
import java.util.Map;

public class CalleeHandler extends SceneTransformer {

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        CallGraph cg = Scene.v().getCallGraph();
        Iterator<MethodOrMethodContext> methodOrMethodContextIterator = cg.sourceMethods();
        while (methodOrMethodContextIterator.hasNext()) {
            MethodOrMethodContext methodOrMethodContext = methodOrMethodContextIterator.next();
            SootMethod method = null;
            if (methodOrMethodContext instanceof SootMethod) {
                method = (SootMethod) methodOrMethodContext;
            } else {
                MethodContext methodContext = (MethodContext) methodOrMethodContext;
                method = methodContext.method();
            }
//                    System.out.println(method.getSignature());
            if (method.getSignature().contains("kanzi.io.CompressedOutputStream") && method.getSignature().contains("processBlock")) {
                for (Unit unit : method.getActiveBody().getUnits()) {
                    if (unit instanceof Stmt) {
                        Stmt stmt = (Stmt) unit;
                        if (stmt.containsInvokeExpr()) {
//                                    InvokeExpr invokeExpr = stmt.getInvokeExpr();
//                                    SootMethod invokeExprMethod = invokeExpr.getMethod();
//                                    System.out.println(invokeExprMethod.getSignature());
                            Iterator<Edge> edgeIterator = cg.edgesOutOf(unit);
                            while (edgeIterator.hasNext()) {
                                Edge next = edgeIterator.next();
                                String signature = next.getTgt().method().getSignature();
                                if (signature.contains("kanzi.io.CompressedOutputStream$EncodingTask") && signature.contains("call")) {
                                    System.out.println(unit);
                                    for (Tag tag : unit.getTags()) {
                                        System.out.println(tag);
                                    }
//                                            System.out.println(unit.getJavaSourceStartLineNumber());
                                }

//                                        System.out.println(signature);
                            }
                        }
                    }
                }
            }
        }
    }
}
