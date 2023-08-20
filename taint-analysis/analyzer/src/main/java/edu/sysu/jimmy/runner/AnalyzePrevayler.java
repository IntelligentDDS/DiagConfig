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
import java.util.List;

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;


public class AnalyzePrevayler {
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzePrevayler.class);
    protected static String appPath;
    protected static String libPath;

    public static void prevaylerSetup() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        appPath = SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[0]);

        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar" + File.pathSeparator + testSrc1.getAbsolutePath();
        LOGGER.info("app and lib class path prepared");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        prevaylerSetup();
        TaintTracking prevayler = new TaintTracking("prevayler");
        globalConf.setOneSourceAtATime(true);
        prevayler.setConfig(globalConf);
        prevayler.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        prevayler.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        prevayler.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
//        String entryPoint = "<org.prevayler.socketserver.server.Main: void main(java.lang.String[])>";
        List<String> entryPoints = new ArrayList<>();
        entryPoints.add("<org.prevayler.socketserver.server.Main: void main(java.lang.String[])>");
//        entryPoints.add("<org.prevayler.socketserver.server.CommandThread: void handleRequests()>");
        entryPoints.add("<org.prevayler.demos.scalability.ProfilingEntry: void main(java.lang.String[])>");
        entryPoints.add("<org.prevayler.demos.scalability.Main: void main(java.lang.String[])>");
        entryPoints.add("<org.prevayler.demos.demo1.Main: void main(java.lang.String[])>");
        entryPoints.add("<org.prevayler.demos.jxpath.Main: void main(java.lang.String[])>");
        prevayler.trackOneSourceMultiEntrypoint(appPath, libPath, entryPoints, prevayler.getSources(), prevayler.getSinks());
//        prevayler.trackAndPersist(appPath, libPath, entryPoints, prevayler.getSources(), prevayler.getSinks());
        prevayler.collectOptionType();
    }
}
