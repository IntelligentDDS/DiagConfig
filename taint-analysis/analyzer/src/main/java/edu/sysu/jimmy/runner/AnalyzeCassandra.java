package edu.sysu.jimmy.runner;

import edu.sysu.jimmy.analysis.TaintTracking;
import edu.sysu.jimmy.utility.SubjectSysInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class AnalyzeCassandra {
    protected static String appPath;
    protected static String libPath;
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeCassandra.class);

    public static void cassandraSetUp() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        if (!(testSrc1.exists())) {
            LOGGER.error("Test aborted - none of the test sources are available");
        }
//        appPath = SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[8]);
        File cassandra_classes = new File(f, "subjectSys" + File.separator + "cassandra" + File.separator + "classes" + File.separator + "main");
        appPath = cassandra_classes.getAbsolutePath();

        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar" + File.pathSeparator + testSrc1.getAbsolutePath();
        LOGGER.info("app and lib class path prepared");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        cassandraSetUp();
        TaintTracking cassandra = new TaintTracking("cassandra");
        cassandra.setConfig(globalConf);
        cassandra.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        cassandra.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        cassandra.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
        ArrayList<String> entryPoints = new ArrayList<>();
//        String entrypoint = "<org.apache.cassandra.service.CassandraDaemon: void activate()>";
//        String entrypoint = "<org.apache.cassandra.service.CassandraDaemon: void main(java.lang.String[])>";
//        String entrypoint = "<org.apache.cassandra.transport.messages.StartupMessage: java.util.Map upperCaseKeys(java.util.Map)>";
//        String entrypoint = "<org.apache.cassandra.service.CassandraDaemon: void main(java.lang.String[])>";
        entryPoints.add("<org.apache.cassandra.service.CassandraDaemon: void main(java.lang.String[])>");
        entryPoints.add("<org.apache.cassandra.transport.Message$Response processRequest(org.apache.cassandra.transport.ServerConnection, org.apache.cassandra.transport.Message$Request)>");
        cassandra.trackAndPersist(appPath, libPath, entryPoints, cassandra.getSources(), cassandra.getSinks());
    }
}
