package edu.sysu.jimmy.runner;

import edu.sysu.jimmy.soot.SootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.config.IInfoflowConfig;

public class EnvSetup {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EnvSetup.class);
    protected static InfoflowConfiguration globalConf;
    protected static IInfoflowConfig sootConfig;


    public static void resetEnv() {
        soot.G.reset();
        System.gc();
    }

    public static void globalSetup() {
        // set up soot class path

        // config information flow
        globalConf = new InfoflowConfiguration();
        //more precise call graph algorithm
        globalConf.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        globalConf.setImplicitFlowMode(InfoflowConfiguration.ImplicitFlowMode.AllImplicitFlows);

        globalConf.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.PropagateConstants);
//        globalConf.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        globalConf.getAccessPathConfiguration().setAccessPathLength(5);
        globalConf.getSolverConfiguration().setDataFlowSolver(InfoflowConfiguration.DataFlowSolver.ContextFlowSensitive);
        globalConf.getSolverConfiguration().setMaxJoinPointAbstractions(-1);
        globalConf.setMaxThreadNum(-1);

//        globalConf.setInspectSources(false);
//        globalConf.setInspectSinks(false);

        globalConf.setAliasingAlgorithm(InfoflowConfiguration.AliasingAlgorithm.FlowSensitive);
//        globalConf.setAliasingAlgorithm(InfoflowConfiguration.AliasingAlgorithm.None);
//        globalConf.setFlowSensitiveAliasing(false);
        globalConf.setFlowSensitiveAliasing(true);
        globalConf.setStopAfterFirstFlow(false);
        globalConf.setStaticFieldTrackingMode(InfoflowConfiguration.StaticFieldTrackingMode.ContextFlowSensitive);
        globalConf.setEnableExceptionTracking(false);
//        globalConf.setOneSourceAtATime(true);
//        globalConf.getPathConfiguration().setPathReconstructionMode(InfoflowConfiguration.PathReconstructionMode.Precise);
//        globalConf.getPathConfiguration().setPathReconstructionMode(InfoflowConfiguration.PathReconstructionMode.Fast);
        globalConf.getPathConfiguration().setPathReconstructionMode(InfoflowConfiguration.PathReconstructionMode.NoPaths);
//        globalConf.getSolverConfiguration().setSingleJoinPointAbstraction(true);
        sootConfig = new SootConfig();
        LOGGER.info("information flow config done.");
    }
}
