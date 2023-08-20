package edu.sysu.jimmy.analysis;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.sysu.jimmy.analysis.result.HotSpotDef;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class VariabilityParsing {
    public String csvFilePath;
    public String xmlFilePath;
    public HashMap<String, HotSpotDef> consideredHotSpotsIndex; // index for searching hotspot   <method signature, hotspot>
    public LinkedList<HotSpotDef> consideredHotspots; // hotspot that diff over threshold
    public Map<String, LinkedList<LinkedList<String>>> consideredCallChains; // hotspot and its call chain

    private boolean isInvocations = false;
    public double delta;

    public VariabilityParsing(String csvFilePath, String xmlFilePath) {
        this.csvFilePath = csvFilePath;
        this.xmlFilePath = xmlFilePath;
        this.delta = 0.1;
        this.isInvocations = (xmlFilePath.contains("h2") || xmlFilePath.contains("prevayler"));
        this.comparisonParser();
        this.buildIndex();
        this.consideredCallChains = null;

//        this.consideredHotSpotsIndex = this.buildIndex(this.consideredHotspots);
//        this.consideredCallChains = this.callChainParser(this.xmlFilePath);
    }

    public VariabilityParsing(String csvFilePath, String xmlFilePath, double delta) {
        this.csvFilePath = csvFilePath;
        this.xmlFilePath = xmlFilePath;
        this.delta = delta;
        this.isInvocations = (xmlFilePath.contains("h2") || xmlFilePath.contains("prevayler"));
        this.comparisonParser();
        this.buildIndex();
        this.consideredCallChains = null;
//        this.consideredHotSpotsIndex = this.buildIndex(this.consideredHotspots);
//        this.consideredCallChains = this.callChainParser(this.xmlFilePath);
    }

    public void callChainParser() {
        SAXReader saxReader = new SAXReader();
        HashMap<String, LinkedList<LinkedList<String>>> callChainMap = null;
        try {
            Document document = saxReader.read(this.xmlFilePath);
            Element root = document.getRootElement();
            callChainMap = new HashMap<>();
            for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
                Element element = i.next();
                String methodSignature = "<" + element.attributeValue("class") + ": " + element.attributeValue("methodName") + element.attributeValue("methodSignature") + ">";
                if (this.consideredHotSpotsIndex.containsKey(methodSignature)) {
                    HotSpotDef hotSpotDef = this.consideredHotSpotsIndex.get(methodSignature);
                    hotSpotDef.percent = Double.parseDouble(element.attributeValue("percent"));
                    buildCallChain(element, new LinkedList<>(), callChainMap);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        this.consideredCallChains = callChainMap;
    }

    public void buildCallChain(Element root, LinkedList<String> callChain, Map<String, LinkedList<LinkedList<String>>> callChainMap) {
        callChain.offer("<" + root.attributeValue("class") + ": " + root.attributeValue("methodName") + root.attributeValue("methodSignature") + ">");
        String leaf = root.attributeValue("leaf");
        boolean b = Boolean.parseBoolean(leaf);
        if (!b) {
            for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
                buildCallChain(i.next(), callChain, callChainMap);
                callChain.pollLast();
            }
        } else {
            if (!callChainMap.containsKey(callChain.peek()))
                callChainMap.put(callChain.peek(), new LinkedList<>());
            callChainMap.get(callChain.peek()).offer(new LinkedList<>(callChain));
        }
    }

    public void comparisonParser() {
        CSVReader reader = null;
        LinkedList<HotSpotDef> hotSpotList = new LinkedList<>();
        try {
            reader = new CSVReader(new FileReader(this.csvFilePath));
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                HotSpotDef newHotSpot = new HotSpotDef(line[0], line[1], line[2], line[3], line[4]);
                if (checkHotSpot(newHotSpot))
                    hotSpotList.offer(newHotSpot);
            }
            Collections.sort(hotSpotList);

        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }
        this.consideredHotspots = hotSpotList;
    }

    public boolean checkHotSpot(HotSpotDef hotSpot) {
//        return true;

//        return isInvocations ? hotSpot.invocationsDiffRate > this.delta : hotSpot.selfTimeDiffRate > this.delta;
        return hotSpot.invocationsDiffRate > this.delta;
    }

    public void buildIndex() {
        HashMap<String, HotSpotDef> index = new HashMap<>();
        for (HotSpotDef hotSpotDef : this.consideredHotspots) {
            index.put(hotSpotDef.methodSignature, hotSpotDef);
        }
        this.consideredHotSpotsIndex = index;
    }

}
