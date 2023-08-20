package edu.sysu.jimmy.runner;

import edu.sysu.jimmy.analysis.TaintTracking;
import edu.sysu.jimmy.utility.SubjectSysInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;

public class AnalyzeKanzi {
    protected static String appPath;
    protected static String libPath;
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeKanzi.class);

    public static void kanziSetup() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        if (!(testSrc1.exists())) {
            LOGGER.error("Test aborted - none of the test sources are available");
        }

        appPath = SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[4]);

        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar" + File.pathSeparator + testSrc1.getAbsolutePath();
        LOGGER.info("app and lib class path prepared");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        kanziSetup();
        TaintTracking kanzi = new TaintTracking("kanzi");
//        globalConf.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        kanzi.setConfig(globalConf);
        kanzi.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        kanzi.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        kanzi.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
        String entryPoint = "<kanzi.app.Kanzi: void main(java.lang.String[])>";
//        kanzi.trackOneSourceOneEntrypoint(appPath, libPath, entryPoint);
//        kanzi.computeInfoflow(appPath, libPath, SubjectSysInfo.getEntryPoint(SubjectSysInfo.TARGET[4]), kanzi.getSources(), kanzi.getSinks());
//        kanzi.collectResults2Json();
//        kanzi.trackOneSourceOneEntrypoint(appPath, libPath, entryPoint);
//        kanzi.trackOneSourceMultiEntrypoint(appPath, libPath, SubjectSysInfo.getEntryPoint(SubjectSysInfo.TARGET[4]));
        kanzi.computeOneSourceOneEntrypoint(appPath, libPath, entryPoint, Arrays.asList(SubjectSysInfo.ARGS[SubjectSysInfo.getOrder("kanzi")]));
//        kanzi.computeOneSourceMultiEntrypoint(appPath, libPath, SubjectSysInfo.getEntryPoint(SubjectSysInfo.TARGET[4]), Arrays.asList(SubjectSysInfo.ARGS[4]));
        kanzi.collectOptionType();
    }
}
