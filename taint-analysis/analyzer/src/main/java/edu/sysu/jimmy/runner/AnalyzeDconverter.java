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

import static edu.sysu.jimmy.runner.EnvSetup.globalConf;
import static edu.sysu.jimmy.runner.EnvSetup.sootConfig;

public class AnalyzeDconverter {
    protected static String appPath;
    protected static String libPath;
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeDconverter.class);

    public static void dconverterSetup() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");

        if (!(testSrc1.exists()
        ))
            LOGGER.error("Test aborted - none of the test sources are available");
        appPath = testSrc1.getAbsolutePath()
                + File.separator +
                SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[6]);
        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
        LOGGER.info("class path is prepared.");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        dconverterSetup();
        globalConf.setOneSourceAtATime(true);
        TaintTracking dconverter = new TaintTracking("dconverter");
        dconverter.setConfig(globalConf);
        dconverter.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        dconverter.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        dconverter.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
        String entryPoint = "<at.favre.tools.dconvert.Main: void main(java.lang.String[])>";
//        String entryPoint = "<at.favre.tools.dconvert.DConvert: void execute(at.favre.tools.dconvert.arg.Arguments, boolean, at.favre.tools.dconvert.DConvert$HandlerCallback)>";
        dconverter.trackOneSourceOneEntrypoint(appPath, libPath, entryPoint, dconverter.getSources(), dconverter.getSinks());
//        dconverter.trackAndPersist(appPath, libPath, entryPoint, dconverter.getSources(), dconverter.getSinks());
        dconverter.collectOptionType();
    }
}
