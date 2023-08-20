package edu.sysu.jimmy.jimple.infoflow.entryPointCreators;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.entryPointCreators.BaseEntryPointCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CassandraEntryPoint extends BaseEntryPointCreator {

    private final List<String> codeToExecute = new ArrayList<>();

    public CassandraEntryPoint() {
        // init
        codeToExecute.add("<org.apache.cassandra.service.CassandraDaemon: void main(java.lang.String[])>");
        // message

    }



    @Override
    protected SootMethod createDummyMainInternal() {
        return null;
    }

    @Override
    public Collection<String> getRequiredClasses() {
        return null;
    }

    @Override
    public Collection<SootMethod> getAdditionalMethods() {
        return null;
    }

    @Override
    public Collection<SootField> getAdditionalFields() {
        return null;
    }
}
