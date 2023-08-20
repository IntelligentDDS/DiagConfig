package edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.IRegion;
import soot.toolkits.graph.pdg.PDGNode;

import java.util.*;

public class IfRegionFinder extends BodyTransformer {
    public static final HashMap<String, HashMutablePDG> METHOD_PDG_LAZY = new HashMap<>();
    private Set<IfRegion> ifRegions;

    public IfRegionFinder() {
        ifRegions = null;
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        getIfRegions(b);
    }

    public Set<IfRegion> getIfRegions(Body b) {
        if (ifRegions != null)
            return ifRegions;
        EnhancedUnitGraph g = new EnhancedUnitGraph(b);
//        BriefUnitGraph g = new BriefUnitGraph(b);
        return getIfRegions(g, new LoopFinder().getLoops(g));
    }

    public Set<IfRegion> getIfRegions(Body b, Set<Loop> loops) {
        if (ifRegions != null)
            return ifRegions;
        return getIfRegions(new EnhancedUnitGraph(b), loops);
    }

    public Set<IfRegion> getIfRegions(UnitGraph g, Set<Loop> loops) {
        Set<IfRegion> ans = new HashSet<>();
        HashMutablePDG pdg = null;
        String signature = g.getBody().getMethod().getSignature();
        if (METHOD_PDG_LAZY.containsKey(signature))
            pdg = METHOD_PDG_LAZY.get(signature);
        else {
            pdg = new HashMutablePDG(g);
            METHOD_PDG_LAZY.put(signature, pdg);
        }
        HashSet<Unit> loopEntryIf = new HashSet<>();
        HashSet<Unit> loopFirstIfSet = new HashSet<>();
        initLoopIf(g, loops, loopEntryIf, loopFirstIfSet);

        IRegion startRegion = pdg.GetStartRegion();
//        System.out.println(startRegion);
        List<IRegion> childRegions = startRegion.getChildRegions();
        List<Unit> units = startRegion.getUnits();
        for (Unit unit : units) {
            if (unit instanceof IfStmt) {
                if (loopEntryIf.contains(unit) || loopFirstIfSet.contains(unit)) {
                    continue;
                }
                IfStmt ifStmt = (IfStmt) unit;
                List<Unit> succsOf = g.getSuccsOf(ifStmt);
                if (!succsOf.contains(ifStmt.getTarget()))
                    succsOf.add(ifStmt.getTarget());
                List<IRegion> iRegionList = new ArrayList<>();
                for (Unit succsOfIf : succsOf) {
                    for (IRegion childRegion : childRegions) {
                        iRegionList.addAll(dealCDRandGetRegionList(g, childRegion, succsOfIf, ans, loopEntryIf, loopFirstIfSet));
                    }
                }
                ans.add(new IfRegion(ifStmt, iRegionList));
            }
        }
        this.ifRegions = ans;
        return ans;
    }

    private List<IRegion> dealCDRandGetRegionList(UnitGraph g, IRegion curRegion, Unit begin, Set<IfRegion> ans, HashSet<Unit> loopEntryIf, HashSet<Unit> loopFirstIfSet) {
        ArrayList<IRegion> iRegionList = new ArrayList<>();
        List<Unit> units = curRegion.getUnits();
        if (units.contains(begin)) {
//            System.out.println(curRegion);
            for (Unit unit : units) {
                if (unit instanceof IfStmt) {

                    IfStmt ifStmt = (IfStmt) unit;
                    List<Unit> succsOf = g.getSuccsOf(ifStmt);
                    if (!succsOf.contains(ifStmt.getTarget()))
                        succsOf.add(ifStmt.getTarget());
                    List<IRegion> curIfRelatedRL = new ArrayList<>();
                    for (Unit unitSuccsOf : succsOf) {
                        for (IRegion childRegion : curRegion.getChildRegions()) {
                            curIfRelatedRL.addAll(dealCDRandGetRegionList(g, childRegion, unitSuccsOf, ans, loopEntryIf, loopFirstIfSet));
                        }
                    }
                    if (!loopEntryIf.contains(unit) && !loopFirstIfSet.contains(unit))
                        ans.add(new IfRegion(ifStmt, new ArrayList<>(curIfRelatedRL)));
                    iRegionList.addAll(curIfRelatedRL);
                }
            }
            iRegionList.add(0, curRegion);
        }

        return iRegionList;
    }

    private void initLoopIf(UnitGraph g, Set<Loop> loops, HashSet<Unit> loopEntryIf, HashSet<Unit> loopFirstIfSet) {
        for (Loop loop : loops) {
            boolean loopEntryFounded = false;
            if (loop.getHead() instanceof IfStmt) {
                loopEntryIf.add(loop.getHead());
                loopEntryFounded = true;
            }
            if (loopEntryFounded)
                continue;
            Stmt backJumpStmt = loop.getBackJumpStmt();
            if (backJumpStmt instanceof IfStmt) {
                loopEntryIf.add(backJumpStmt);
                loopEntryFounded = true;
            }
            if (loopEntryFounded)
                continue;
            Queue<Unit> waitList = new LinkedList<>();
            HashSet<Unit> processed = new HashSet<>();
            waitList.add(backJumpStmt);
            Unit theFirstUnit = null;
            while (!waitList.isEmpty()) {
                Unit unit = waitList.remove();
                processed.add(unit);
                if (unit instanceof IfStmt) {
                    theFirstUnit = unit;
                    break;
                }
                List<Unit> succsOf = g.getSuccsOf(unit);
                for (Unit succsUnit : succsOf) {
                    if (!processed.contains(succsUnit))
                        waitList.add(succsUnit);
                }
            }
            if (theFirstUnit instanceof IfStmt)
                loopFirstIfSet.add(theFirstUnit);
        }
    }

}
