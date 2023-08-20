package edu.sysu.jimmy.runner;

import edu.sysu.jimmy.analysis.TaintTracking;
import edu.sysu.jimmy.utility.SubjectSysInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.entryPointCreators.SequentialEntryPointCreator;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;

public class AnalyzeCatena {
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeCatena.class);
    protected static String appPath;
    protected static String libPath;

    public static void catenaSetup() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        if (!(
                testSrc1.exists()
        )) {
            LOGGER.error("Test aborted - none of the test sources are available");
        }
        appPath = SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[1]);

        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar" + File.pathSeparator + testSrc1.getAbsolutePath();
        LOGGER.info("class path is prepared.");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        catenaSetup();
        TaintTracking catena = new TaintTracking("catena");
        catena.setConfig(globalConf);
        catena.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        catena.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        catena.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
        String entryPoint = "<monitoring.CatenaMonitoring: void main(java.lang.String[])>";
//        catena.computeInfoflow(appPath, libPath, entryPoint, catena.getSources(), catena.getSinks());
        catena.trackOneSourceOneEntrypoint(appPath, libPath, entryPoint);
//        catena.trackAndPersist(appPath, libPath, entryPoint, catena.getSources(), catena.getSinks());
        catena.collectOptionType();
    }
}
