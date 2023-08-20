package edu.sysu.jimmy.runner;

import edu.sysu.jimmy.analysis.TaintTracking;
import edu.sysu.jimmy.utility.SubjectSysInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.source.parsers.xml.XMLSourceSinkParser;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.methodSummary.taintWrappers.SummaryTaintWrapper;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;
import soot.jimple.infoflow.sourcesSinks.manager.DefaultSourceSinkManager;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.sysu.jimmy.runner.EnvSetup.*;

public class AnalyzeBatikSVG {
    protected static String appPath;
    protected static String libPath;
    protected static Logger LOGGER = LoggerFactory.getLogger(AnalyzeBatikSVG.class);

    public static void batikSVGSetup() {
        File f = new File(".");
        File testSrc1 = new File(f, "analyzer" + File.separator + "target" + File.separator + "classes");
        if (!(testSrc1.exists())) {
            LOGGER.error("Test aborted - none of the test sources are available");
        }

        appPath = SubjectSysInfo.getAppClassPath(SubjectSysInfo.TARGET[5]);

        libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
                + File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar" + File.pathSeparator + testSrc1.getAbsolutePath();
        LOGGER.info("app and lib class path prepared");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        EnvSetup.resetEnv();
        EnvSetup.globalSetup();
        batikSVGSetup();
        globalConf.setOneSourceAtATime(true);
        TaintTracking batik = new TaintTracking("batik");
        batik.setConfig(globalConf);
        batik.setSootConfig(sootConfig);
        InfoflowConfiguration.PathConfiguration pathConfiguration = new InfoflowConfiguration.PathConfiguration();
        pathConfiguration.setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextSensitive);
        batik.setPathBuilderFactory(new DefaultPathBuilderFactory(pathConfiguration));
        batik.setTaintWrapper(TaintWrapperFactory.createTaintWrapperEager());
        ArrayList<String> entryPoints = new ArrayList<>();
        entryPoints.add("<org.apache.batik.apps.rasterizer.Main: void main(java.lang.String[])>");
        entryPoints.add("<org.apache.batik.transcoder.image.PNGTranscoder: void transcode(org.w3c.dom.Document,java.lang.String,org.apache.batik.transcoder.TranscoderOutput)>");
        entryPoints.add("<org.apache.batik.transcoder.image.JPEGTranscoder: void transcode(org.w3c.dom.Document,java.lang.String,org.apache.batik.transcoder.TranscoderOutput)>");
        entryPoints.add("<org.apache.batik.transcoder.image.TIFFTranscoder: void transcode(org.w3c.dom.Document,java.lang.String,org.apache.batik.transcoder.TranscoderOutput)>");
        entryPoints.add("<org.apache.fop.svg.PDFTranscoder: void transcode(org.w3c.dom.Document,java.lang.String,org.apache.batik.transcoder.TranscoderOutput)>");
        batik.computeOneSourceMultiEntrypoint(appPath, libPath, entryPoints, Arrays.asList(SubjectSysInfo.ARGS[SubjectSysInfo.getOrder("batik")]));
//        batik.computeOneSourceOneEntrypoint(appPath, libPath, entryPoint, Arrays.asList(SubjectSysInfo.ARGS[SubjectSysInfo.getOrder("batik")]));
//        batik.trackAndPersist(appPath, libPath, entryPoint, batik.getSources(), batik.getSinks());
//        batik.collectOptionType();
    }
}
