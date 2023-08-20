package edu.sysu.jimmy.analysis;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodSignatureConverter extends BodyTransformer {
    private static final MethodSignatureConverter instance = new MethodSignatureConverter();

    public static MethodSignatureConverter v() {
        return instance;
    }

    VariabilityParsing variabilityParsing;
    private static Pattern patternMethodSig = null;

    public ConcurrentHashMap<String, Body> byteSig2MethodBody;
//    public Map<String, Body> byteSig2MethodBody;

    public Map<String, String> methodSig2bytecodeSigIndex;

    public MethodSignatureConverter(String csvFilePath, String xmlFilePath) {
        this.variabilityParsing = new VariabilityParsing(csvFilePath, xmlFilePath);
//        this.byteSig2MethodBody = new HashMap<>();
        this.byteSig2MethodBody = new ConcurrentHashMap<>();
    }

    public MethodSignatureConverter(VariabilityParsing variabilityParsing) {
        this.variabilityParsing = variabilityParsing;
        //        this.byteSig2MethodBody = new HashMap<>();

        this.byteSig2MethodBody = new ConcurrentHashMap<>();
    }

    private MethodSignatureConverter() {
//        this.byteSig2MethodBody = new HashMap<>();
        this.byteSig2MethodBody = new ConcurrentHashMap<>();
        this.methodSig2bytecodeSigIndex = new ConcurrentHashMap<>();
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        SootMethod sootMethod = b.getMethod();
        String sootSig = sootMethod.getSignature();
        String bytecodeSig = sootMethod.getBytecodeSignature();
        this.byteSig2MethodBody.put(bytecodeSig, b);
        String methodSig = getMethodSignatureWithoutReturnType(sootSig);
        methodSig2bytecodeSigIndex.put(methodSig, bytecodeSig);
        /*if (variabilityParsing.consideredHotSpotsIndex.containsKey(methodSig)) {
            variabilityParsing.consideredHotSpotsIndex.get(methodSig).methodSignature = bytecodeSig;
//            variabilityParsing.consideredHotSpotsIndex.get(methodSig).methodSignature = sootSig;
        }*/
    }

    public static String getMethodSignatureWithoutReturnType(String parseString) {
        if (parseString == null || parseString.isEmpty())
            return null;

        if (!parseString.startsWith("<") || !parseString.endsWith(">")) {
            throw new IllegalArgumentException(
                    "Illegal format of " + parseString + " (should use soot method representation)");
        }

        if (patternMethodSig == null)
            patternMethodSig = Pattern
                    .compile("<(?<className>.*?): (?<returnType>.*?) (?<methodName>.*?)\\((?<parameters>.*?)\\)>");

        Matcher matcher = patternMethodSig.matcher(parseString);
        if (matcher.find()) {
            String className = matcher.group("className");
//            String returnType = matcher.group("returnType");
            String methodName = matcher.group("methodName");
            String paramList = matcher.group("parameters");
//            return new SootMethodAndClass(methodName, className, returnType, paramList);
            return className + "." + methodName + "(" + paramList + ")";
        }

        return null;
    }
}
