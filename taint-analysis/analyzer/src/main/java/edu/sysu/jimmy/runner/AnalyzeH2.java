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
import java.util.ArrayList;

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;

public class AnalyzeH2 {
    protected static String appPath;
    protected static String libPath;
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeH2.class);

    public static void h2Setup() {

        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        if (!(
                testSrc1.exists()
        )) {
            LOGGER.error("Test aborted - none of the test sources are available");
        }

        appPath = SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[3]);

        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar" + File.pathSeparator + testSrc1.getAbsolutePath();
        LOGGER.info("app and lib class path prepared");


    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        globalConf.setOneSourceAtATime(true);
        h2Setup();
        TaintTracking h2 = new TaintTracking("h2");
        h2.setConfig(globalConf);
        h2.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        h2.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        h2.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
        ArrayList<String> entryPoints = new ArrayList<>();
        entryPoints.add("<org.h2.tools.Server: void main(java.lang.String[])>");
        entryPoints.add("<org.h2.server.TcpServerThread: void run()>");
//        h2.trackOneSourceMultiEntrypoint(appPath, libPath, entryPoints, h2.getSources(), h2.getSinks());
        h2.trackAndPersist(appPath, libPath, entryPoints, h2.getSources(), h2.getSinks());

//        entryPoints.add("<org.h2.test.bench.TestScalability: void main(java.lang.String[])>");
//        String entryPoint = "<profiling.Profiler: void main(java.lang.String[])>";
//        String entryPoint = "<org.h2.test.bench.TestPerformance: void main(java.lang.String[])>";
//        h2.trackAndPersist(appPath, libPath, entryPoint, h2.getSources(), h2.getSinks());
//        h2.collectOptionType();
    }
}
