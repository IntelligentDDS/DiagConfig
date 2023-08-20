package edu.sysu.jimmy.runner;


import edu.sysu.jimmy.analysis.CallsiteAnalysis;
import edu.sysu.jimmy.analysis.CallsiteAnalysisConcurrent;
import fj.P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Diagnosis {
    protected static Logger LOGGER = LoggerFactory.getLogger(Diagnosis.class);
    public static int count2analysis = 0;
    public static double recall = 0;
    public static double precision = 0;

    public static String monitoring_output_path = "/media/jimmy/FSE23/monitoring_output";
//    public static String monitoring_output_path = "/media/jimmy/DataVol/monitoring_output";

    public static void run(String sys, String sampling, double delta, double violation_threshold) throws IOException {
        String baselinePath = monitoring_output_path + File.separator + "baseline" + File.separator + sys;
        String runningPath = monitoring_output_path + File.separator + sys + File.separator + sampling + File.separator + "jProfiler";

        Set<String> catenaOptionsTemp = performance_related_options.get(sys);
        String[] catenaOptions = new String[catenaOptionsTemp.size()];
        catenaOptionsTemp.toArray(catenaOptions);

        File baselineDir = new File(baselinePath);
        File runningDir = new File(runningPath);
        File[] baselineFiles = baselineDir.listFiles();
        File baselineFile = null;
        if (baselineFiles != null)
            baselineFile = baselineFiles[0];
        File[] baselineEssentials = getEssentials(baselineFile, true, sys.compareTo("prevayer") == 0);
        double baselineTime = getAvg_bbTime(baselineEssentials[0]);
        File[] runningFiles = runningDir.listFiles();
        for (File runningFile : runningFiles) {
            File[] runningEssentials = getEssentials(runningFile, false, sys.compareTo("prevayler") == 0);
            if (runningEssentials[0] == null) {
                System.out.println(runningFile.getName());
                continue;
            }
            double runningTime = getAvg_bbTime(runningEssentials[0]);
            double changeRate = Math.abs(runningTime - baselineTime) / baselineTime;
            if (changeRate > violation_threshold) {
                long startTime = System.currentTimeMillis();
                Set<String> configDiff = getConfigDiff(sys, baselineFile.getName(), runningFile.getName());
                LOGGER.info("current configuration is {}", runningFile.getAbsolutePath());
                CallsiteAnalysis catena = new CallsiteAnalysis(sys, runningEssentials[1].getAbsolutePath(), runningEssentials[2].getAbsolutePath(), catenaOptions, delta);
                catena.run();
                catena.linkCallChainWithTaintFlow(configDiff.size());
//                catena.linkCallChainWithTaintFlow("h2".compareTo(sys) == 0 ? Integer.MAX_VALUE : configDiff.size());
                catena.persist();
                double[] recall_precision = getRecall_Precision(configDiff, catena.getOptionList());
                if (recall_precision[0] == 0) {
                    LOGGER.info("current recall is {}, current precision is {}", recall_precision[0], recall_precision[1]);
                    System.out.println(runningFile.getAbsolutePath());
                    continue;
                }
//                if (recall_precision[1] < 0.7) {
//                    System.out.println(runningFile.getAbsolutePath());
//                    continue;
//                }
                recall += recall_precision[0];
                precision += recall_precision[1];
                count2analysis++;
                LOGGER.info("current recall is {}, current precision is {}, total recall is {}, total precision is {}", recall_precision[0], recall_precision[1], recall / count2analysis, precision / count2analysis);
//                System.out.println("Recall is " + recall / count2analysis + "\nPrecision is " + precision / count2analysis + "\n");
//                System.out.println(System.currentTimeMillis() - startTime);
            }
        }
        System.out.println("Recall is " + recall / count2analysis + "\nPrecision is " + precision / count2analysis + "\n");
//        System.out.println(count2analysis);
    }

    public static void runConcurrent(String sys, String sampling, double delta, double violation_threshold) throws IOException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        LinkedList<CallsiteAnalysisConcurrent> taskList = new LinkedList<>();
        String baselinePath = monitoring_output_path + File.separator + "baseline" + File.separator + sys;
        String runningPath = monitoring_output_path + File.separator + sys + File.separator + sampling + File.separator + "jProfiler";

        Set<String> catenaOptionsTemp = performance_related_options.get(sys);
        String[] catenaOptions = new String[catenaOptionsTemp.size()];
        catenaOptionsTemp.toArray(catenaOptions);

        File baselineDir = new File(baselinePath);
        File runningDir = new File(runningPath);
        File[] baselineFiles = baselineDir.listFiles();
        File baselineFile = null;
        if (baselineFiles != null)
            baselineFile = baselineFiles[0];
        File[] baselineEssentials = getEssentials(baselineFile, true, sys.compareTo("prevayler") == 0);
        double baselineTime = getAvg_bbTime(baselineEssentials[0]);
        File[] runningFiles = runningDir.listFiles();
        for (File runningFile : runningFiles) {
            File[] runningEssentials = getEssentials(runningFile, false, sys.compareTo("prevayler") == 0);
            if (runningEssentials[0] == null) {
                System.out.println(runningFile.getName());
                continue;
            }
            double runningTime = getAvg_bbTime(runningEssentials[0]);
            double changeRate = Math.abs(runningTime - baselineTime) / baselineTime;
            if (changeRate > violation_threshold) {
                Set<String> configDiff = getConfigDiff(sys, baselineFile.getName(), runningFile.getName());
                pool.execute(new CallsiteAnalysisConcurrent(sys, runningEssentials[1].getAbsolutePath(), runningEssentials[2].getAbsolutePath(), catenaOptions, configDiff, runningFile.getAbsolutePath(), delta));
//                LOGGER.info("current configuration is {}", runningFile.getName());
            }
        }

        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        System.out.println("Recall is " + recall / count2analysis + "\nPrecision is " + precision / count2analysis + "\n");
        System.out.println(count2analysis);
    }

    public static Set<String> getConfigDiff(String sysName, String baseline, String running) {
        Set<String> diff = new HashSet<>();
        String[] index = null;
        String[] baselineConfig = null;
        String[] runningConfig = null;
        Set<String> consideredOptions = null;
        Map<String, String> indexMap = null;
        HashMap<String, String> Bmap = null;
        HashMap<String, String> Rmap = null;
        switch (sysName) {
            case "catena":
                index = new String[]{"_hPrime", "_gamma", "_f", "_phi", "_gLowHigh", "_lambda", "_vId", "_d"};
                baselineConfig = baseline.split("_");
                runningConfig = running.split("_");
                consideredOptions = performance_related_options.get("catena");
                for (int i = 0; i < 8; i++) {
                    if (baselineConfig[i].compareTo(runningConfig[i]) != 0) {
                        if (i == 4) {
                            diff.add("_gLow");
                            diff.add("_gHigh");
                        } else if (consideredOptions.contains(index[i])) diff.add(index[i]);
                    }
                }
                break;
            case "kanzi":
                indexMap = new HashMap<String, String>() {{
                    put("-b", "blockSize");
                    put("-l", "level");
                    put("-e", "entropy");
                    put("-t", "transform");
                    put("-x", "checksum");
                    put("-s", "skipBlocks");
                    put("-j", "jobs");
                    put("-v", "verbosity");
                    put("-i", "input");
                }};
                Bmap = new HashMap<>();
                Rmap = new HashMap<>();
                baselineConfig = baseline.split("_");
                runningConfig = running.split("_");
                consideredOptions = performance_related_options.get("kanzi");
                for (int i = 1; i < baselineConfig.length; ) {
                    if ("-x".compareTo(baselineConfig[i]) == 0) {
                        Bmap.put(indexMap.get("-x"), "1");
                        i++;
                    } else if ("-s".compareTo(baselineConfig[i]) == 0) {
                        Bmap.put(indexMap.get("-s"), "1");
                        i++;
                    } else {
                        if (i + 1 < baselineConfig.length)
                            Bmap.put(indexMap.get(baselineConfig[i]), baselineConfig[i + 1]);
                        i = i + 2;
                    }
                }
                for (int i = 1; i < runningConfig.length; ) {
                    if ("-x".compareTo(runningConfig[i]) == 0) {
                        Rmap.put(indexMap.get("-x"), "1");
                        i++;
                    } else if ("-s".compareTo(runningConfig[i]) == 0) {
                        Rmap.put(indexMap.get("-s"), "1");
                        i++;
                    } else {
                        if (i + 1 < runningConfig.length)
                            Rmap.put(indexMap.get(runningConfig[i]), runningConfig[i + 1]);
                        i = i + 2;
                    }
                }
                for (String consideredOption : consideredOptions) {
                    boolean Bhas = Bmap.containsKey(consideredOption);
                    boolean Rhas = Rmap.containsKey(consideredOption);
                    if (Bhas && Rhas) {
                        if (Bmap.get(consideredOption).compareTo(Rmap.get(consideredOption)) != 0)
                            diff.add(consideredOption);
                    } else if (Bhas || Rhas)
                        diff.add(consideredOption);
                }
                break;
            case "sunflow": {
                index = new String[]{"threads", "depths.diffuse", "depths.reflection", "depths.refraction", "bucket.size", "gi.igi.samples"};
                baselineConfig = baseline.split("_");
                runningConfig = running.split("_");
                consideredOptions = performance_related_options.get("sunflow");
                for (int i = 0; i < baselineConfig.length; i++) {
                    if (baselineConfig[i].compareTo(runningConfig[i]) != 0) {
                        if (consideredOptions.contains(index[i])) diff.add(index[i]);
                    }
                }
                break;
            }
            case "dconverter": {

                break;
            }
            case "prevayler": {
                index = new String[]{"_transactionDeepCopyMode", "_transientMode", "_journalDiskSync",
                        "_clock", "_monitor", "_journalSizeThreshold", "_journalAgeThreshold", "journal",
                        "snap", "workload"};
                baselineConfig = baseline.split("_");
                runningConfig = running.split("_");
                consideredOptions = performance_related_options.get("prevayler");
                for (int i = 0; i < baselineConfig.length; i++) {
                    if (baselineConfig[i].compareTo(runningConfig[i]) != 0) {
                        if (index[i].compareTo("journal") == 0)
                            diff.add("_journalSerializer");
                        else {
                            if (consideredOptions.contains(index[i]))
                                diff.add(index[i]);
                        }
                    }
                }
                break;
            }
            case "batik": {
                index = new String[]{
                        "destinationType",
                        "WIDTH",
                        "HEIGHT",
                        "MAX_WIDTH",
                        "MAX_HEIGHT",
                        "BACKGROUND_COLOR",
                        "QUALITY",
                        "INDEXED",
                        "EXECUTE_ONLOAD",
                        "SNAPSHOT_TIME"};
                baselineConfig = baseline.split("_");
                runningConfig = running.split("_");
                consideredOptions = performance_related_options.get("batik");
                for (int i = 0; i < baselineConfig.length; i++) {
                    if (baselineConfig[i].compareTo(runningConfig[i]) != 0) {
                        if (consideredOptions.contains(index[i]))
                            diff.add(index[i]);
                    }
                }
                break;
            }
            case "h2": {
                index = new String[]{
                        "ANALYZE_AUTO",
                        "ANALYZE_SAMPLE",
                        "AUTO_COMPACT_FILL_RATE",
                        "CASE_INSENSITIVE_IDENTIFIERS",
                        "DB_CLOSE_ON_EXIT",
                        "DEFAULT_CONNECTION",
                        "DEFRAG_ALWAYS",
                        "DROP_RESTRICT",
                        "ESTIMATED_FUNCTION_TABLE_ROWS",
                        "LOB_TIMEOUT",
                        "MAX_COMPACT_TIME",
                        "MAX_QUERY_TIMEOUT",
                        "OPTIMIZE_DISTINCT",
                        "OPTIMIZE_EVALUATABLE_SUBQUERIES",
                        "OPTIMIZE_INSERT_FROM_SELECT",
                        "OPTIMIZE_IN_LIST",
                        "OPTIMIZE_IN_SELECT",
                        "OPTIMIZE_OR",
                        "OPTIMIZE_TWO_EQUALS",
                        "OPTIMIZE_SIMPLE_SINGLE_ROW_SUBQUERIES",
                        "QUERY_CACHE_SIZE",
                        "RECOMPILE_ALWAYS",
                        "REUSE_SPACE",
                        "SHARE_LINKED_CONNECTIONS",
                        "MV_STORE",
                        "COMPRESS",
                        "IGNORE_CATALOGS",
                        "ZERO_BASED_ENUMS"
                };
                baselineConfig = baseline.split("_");
                runningConfig = running.split("_");
                consideredOptions = performance_related_options.get("h2");
                for (int i = 0; i < baselineConfig.length - 1; i++) {
                    if (baselineConfig[i].compareTo(runningConfig[i]) != 0) {
                        if (consideredOptions.contains(index[i]))
                            diff.add(index[i]);
                    }
                }
                break;
            }
        }
        return diff;
    }

    public static HashMap<String, Set<String>> performance_related_options = new HashMap<String, Set<String>>() {
        {
            put("kanzi", new HashSet<String>() {{
                addAll(Arrays.asList("blockSize", "level", "entropy", "transform", "checksum", "skipBlocks", "jobs", "pool", "verbosity"));
            }});
            put("catena", new HashSet<String>() {{
                addAll(Arrays.asList("_hPrime", "_gamma", "_phi", "_gLow", "_gHigh", "_lambda"));
            }});
            put("sunflow", new HashSet<String>() {{
                addAll(Arrays.asList("threads", "depths.diffuse", "bucket.size", "gi.igi.samples"));
            }});
            put("dconverter", new HashSet<String>() {{
                addAll(Arrays.asList("enableMozJpeg", "enablePngCrush", "filesToProcess", "haltOnError", "postConvertWebp", "threadCount", "verboseLog"));
            }});
            put("prevayler", new HashSet<String>() {{
                addAll(Arrays.asList("_transactionDeepCopyMode", "_transientMode", "_journalDiskSync", "_clock", "_monitor", "_journalSizeThreshold", "_journalAgeThreshold", "_journalSerializer"));
            }});
            put("batik", new HashSet<String>() {{
                addAll(Arrays.asList("destinationType", "WIDTH", "HEIGHT", "MAX_HEIGHT", "MAX_WIDTH", "EXECUTE_ONLOAD", "SNAPSHOT_TIME"));
            }});
            put("h2", new HashSet<String>() {{
                addAll(Arrays.asList(
                        "ANALYZE_AUTO",
                        "ANALYZE_SAMPLE",
                        "AUTO_COMPACT_FILL_RATE",
//                        "CASE_INSENSITIVE_IDENTIFIERS",
//                        "DB_CLOSE_ON_EXIT",
//                        "DEFAULT_CONNECTION",
                        "DEFRAG_ALWAYS",
//                        "DROP_RESTRICT",
                        "ESTIMATED_FUNCTION_TABLE_ROWS",
                        "LOB_TIMEOUT",
                        "MAX_COMPACT_TIME",
//                        "MAX_QUERY_TIMEOUT",
                        "OPTIMIZE_DISTINCT",
                        "OPTIMIZE_EVALUATABLE_SUBQUERIES",
                        "OPTIMIZE_INSERT_FROM_SELECT",
                        "OPTIMIZE_IN_LIST",
                        "OPTIMIZE_IN_SELECT",
                        "OPTIMIZE_OR",
                        "OPTIMIZE_TWO_EQUALS",
                        "OPTIMIZE_SIMPLE_SINGLE_ROW_SUBQUERIES",
                        "QUERY_CACHE_SIZE",
                        "RECOMPILE_ALWAYS",
//                        "REUSE_SPACE",
                        "SHARE_LINKED_CONNECTIONS",
//                        "MV_STORE",
                        "COMPRESS",
                        "IGNORE_CATALOGS",
                        "ZERO_BASED_ENUMS"
                ));
            }});
        }
    };

    public static File[] getEssentials(File dir, boolean isBaseline, boolean isPrevayler) {
        File[] files = dir.listFiles();
        if (files == null)
            return null;
        File bbFile = null;
        File csvFile = null;
        File xmlFile = null;
        for (File file : files) {
            String fileName = file.getName();
            if ("bb_time.txt".compareTo(fileName) == 0 && !isPrevayler)
                bbFile = file;
            else if ("stdout.txt".compareTo(fileName) == 0 && isPrevayler) {
                bbFile = file;
            } else if (fileName.endsWith(".csv"))
                csvFile = file;
            else if (fileName.endsWith(".xml")) {
                xmlFile = file;
            }
        }
//        assert (isBaseline && bbFile != null) || (!isBaseline && bbFile != null && csvFile != null && xmlFile != null);
        return new File[]{bbFile, csvFile, xmlFile};
    }

    public static double getAvg_bbTime(File bbFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(bbFile));
        String line = null;
        double execute_time = 0L;
        int execute_times = 0;
        while ((line = br.readLine()) != null) {
            execute_times++;
            execute_time += Double.parseDouble(line);
        }
        return execute_time / execute_times;
    }

    public static double[] getRecall_Precision(Set<String> baseline, Set<String> running) {
        if (baseline == null || baseline.isEmpty()) {
//            System.out.println("baseline is empty");
            return new double[]{1.0, 1.0};
        }
        if (running == null || running.isEmpty()) {
            LOGGER.info("baseline is {}", baseline);

            return new double[]{0, 0};
        }
//        System.out.println(baseline.toString() + running.toString());
        LOGGER.info("baseline is {}, running is {}", baseline, running);
        double recall = 0;
        double precision = 0;
        for (String option : baseline) {
            if (running.contains(option))
                recall++;
        }
        precision = recall / running.size();
        return new double[]{recall / baseline.size(), precision};
        /*double recall = 0;
        double precision = 0;
        if (running.containsAll(baseline)) {
            recall = 1.0;
            precision = 1.0 * baseline.size() / running.size();
        }
        return new double[]{recall, precision};*/
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        runConcurrent("batik", "archive", 0.1, 0.05);//res
        run("batik", "archive", 0.01, 0.05);// res
//        run("h2", "archive", 0.1, 0.05);
//        runConcurrent("h2","archive",0.05,0.05);
//        runConcurrent("catena", "archive", 0.1, 0.05); //Res
//        run("catena", "archive", 0.1, 0.1);
//        run("h2", "ran810", 0.1, 0.01); //res
//        run("kanzi", "ran1000", 0.1, 0.05);// res
//        runConcurrent("prevayler", "all", 0.1, 0.01); // res

//        run("catena", "all", 0.1, 0.05);
//        run("batik", "swap", 0.1, 0.05);//res
//        runConcurrent("batik", "all", 0.1, 0.05);
//        run("h2", "lowthan0.7", 0.1, 0.01);
//        runConcurrent("h2", "ran810", 0.1, 0.01);
//        runConcurrent("h2", "all", 0.1, 0.01);
//        runConcurrent("h2", "ran1000", 0.1, 0.01);
//        runConcurrent("h2", "all", 0.1, 0.01);
//        run("kanzi", "all", 0.1, 0.05);
//        run("kanzi", "res", 0.1, 0.05);
//        runConcurrent("kanzi", "res", 0.1, 0.05);
//        run("prevayler", "all", 0.1, 0.01);
//        runConcurrent("prevayler", "all", 0.1, 0.01); // res
//        runConcurrent("prevayler", "ran500", 0.1, 0.01);
//        run("sunflow","all");
//        run("dconverter","ran300");
//        run("catena","all",0.1,0.05);

//        run("cassandra","specific",0.05,0.05);
    }

}
