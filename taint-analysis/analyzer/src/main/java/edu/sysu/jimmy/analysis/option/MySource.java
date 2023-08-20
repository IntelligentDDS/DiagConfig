package edu.sysu.jimmy.analysis.option;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;

public class MySource {
    protected static final String SEP = System.getProperty("file.separator");

    public static String get(final String name) throws DocumentException {
        String result = null;
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File("src" + SEP + "main" + SEP + "resources" + SEP + "confSample.xml"));
        Element root = document.getRootElement();
        Iterator item = root.elementIterator();
        while (item.hasNext()) {
            Element option = (Element) item.next();
            Iterator iterator = option.elementIterator();
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                if (!name.equals(element.getStringValue()))
                    break;
                else {
                    result = ((Element) iterator.next()).getStringValue();
                }
            }
        }
        return result;
    }

    public static int getOptionA() {
        return 'A';
    }

    public static String getOptionB() {
        return "B";
    }
}
