package edu.sysu.jimmy.analysis;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sysu.jimmy.analysis.result.SourceSinkInfo;

import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TaintFlow {
    public String sysName;
    public static String BASE_PATH = "/home/jimmy/Documents/srcrepo/configresearch/taint-analysis/subjectSys/output";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public Set<String> consideredOptions;
    public String dirPath;

    public HashMap<String, List<SourceSinkInfo>> taintInfo; // option with taint flow result

    public Map<String, List<SourceSinkInfo>> methodTaintInfo; // method with taint flow result

    public TaintFlow(String sysName) {
        this.sysName = sysName;
        this.dirPath = BASE_PATH + File.separator + this.sysName + File.separator + "20480m";
        this.consideredOptions = null;
        this.taintInfoParser();
    }

    public TaintFlow(String sysName, String[] consideredOptions) {
        this.sysName = sysName;
        this.dirPath = BASE_PATH + File.separator + this.sysName + File.separator + "20480m";
        this.consideredOptions = new HashSet<>(Arrays.asList(consideredOptions));
        this.taintInfoParser();
    }


    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public void taintInfoParser() {
        File file = new File(this.dirPath);
        File[] jsonFiles = file.listFiles((dir, name) -> name.endsWith(".json"));
        JavaType type = getCollectionType(List.class, SourceSinkInfo.class);
        HashMap<String, List<SourceSinkInfo>> option2Info = new HashMap<>();
        HashMap<String, List<SourceSinkInfo>> method2Info = new HashMap<>();
        assert jsonFiles != null;
        for (File json : jsonFiles) {
            String fileName = json.getName();
            String option = fileName.substring(0, fileName.indexOf(".json"));
            if (this.consideredOptions != null && !this.consideredOptions.contains(option))
                continue;
            List<SourceSinkInfo> tempInfo = null;
            try {
                tempInfo = objectMapper.readValue(json, type);
                option2Info.put(option, tempInfo);
                for (SourceSinkInfo sourceSinkInfo : tempInfo) {
                    if (!method2Info.containsKey(sourceSinkInfo.getMethodSignature()))
                        method2Info.put(sourceSinkInfo.getMethodSignature(), new LinkedList<>());
                    method2Info.get(sourceSinkInfo.getMethodSignature()).add(sourceSinkInfo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.taintInfo = option2Info;
        this.methodTaintInfo = method2Info;
    }


    public static void main(String[] args) {
        TaintFlow kanziInfo = new TaintFlow("kanzi", new String[]{
                "verbosity",
                "level",
                "transform",
                "jobs",
                "blockSize",
                "pool",
                "checksum",
                "skipBlocks",
        });
//        System.out.println();
//        System.out.println();
    }
}
