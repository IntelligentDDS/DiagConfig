package edu.sysu.jimmy.runner;

import edu.sysu.jimmy.analysis.TaintTracking;
import edu.sysu.jimmy.utility.SubjectSysInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;

public class AnalyzeSunflow {
    protected static String appPath;
    protected static String libPath;
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeSunflow.class);

    public static void sunflowSetup() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");

        if (!(testSrc1.exists()
        ))
            LOGGER.error("Test aborted - none of the test sources are available");
        appPath = testSrc1.getAbsolutePath()
                + File.separator +
                SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[2]);
        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
        LOGGER.info("class path is prepared.");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        sunflowSetup();
        TaintTracking sunflow = new TaintTracking("sunflow");
        sunflow.setConfig(globalConf);
        sunflow.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        sunflow.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        sunflow.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
//        String entryPoing = "<SunflowGUI: void main(java.lang.String[])>";
        String entryPoint = "<org.sunflow.Benchmark: void main(java.lang.String[])>";
        sunflow.computeOneSourceOneEntrypoint(appPath, libPath, entryPoint, Arrays.asList(SubjectSysInfo.ARGS[SubjectSysInfo.getOrder("sunflow")]));
//        sunflow.trackOneSourceOneEntrypoint(appPath, libPath, entryPoint,sunflow.getSources(),sunflow.getSinks());
//        sunflow.collectOptionType();
    }
}
