package edu.sysu.jimmy.soot.jimple.toolkits.annotation.logic;

import soot.jimple.Stmt;
import soot.toolkits.graph.pdg.IRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IfRegion {
    protected final Stmt header;
    protected List<Stmt> ifStatements;
    //    protected  UnitGraph g;
    protected final List<IRegion> regionList;

    IfRegion(Stmt header, List<IRegion> regionList) {
        this.header = header;
        this.ifStatements = null;
        this.regionList = regionList;
    }

    public Stmt getHeader() {
        return header;
    }

    public List<Stmt> getIfStatements() {
        if (this.ifStatements != null)
            return ifStatements;
        return traverseIRegionList();
    }

    private List<Stmt> traverseIRegionList() {
        ArrayList<Stmt> stmts = new ArrayList<>();
        for (IRegion iRegion : regionList) {
//            List<Unit> units = iRegion.getUnits();
//            Stream<Stmt> stmtStream = units.stream().filter(unit -> unit instanceof Stmt).map(unit -> (Stmt) unit);
            stmts.addAll(iRegion.getUnits().stream().filter(unit -> unit instanceof Stmt).map(unit -> (Stmt) unit).collect(Collectors.toList()));
        }
        this.ifStatements = stmts;
        return stmts;
    }

    public List<IRegion> getRegionList() {
        return regionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IfRegion ifRegion = (IfRegion) o;

        if (!header.equals(ifRegion.header)) return false;
        return regionList.equals(ifRegion.regionList);
    }

    @Override
    public int hashCode() {
        int result = header.hashCode();
        result = 31 * result + regionList.hashCode();
        return result;
    }

}
