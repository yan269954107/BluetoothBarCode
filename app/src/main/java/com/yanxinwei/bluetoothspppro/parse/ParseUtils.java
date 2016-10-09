package com.yanxinwei.bluetoothspppro.parse;

import com.yanxinwei.bluetoothspppro.model.NormalTask;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.SAXParserFactory;

/**
 * ParseUtils
 * Created by yanxinwei on 16/10/8.
 */

public class ParseUtils {

    public static void convertNormal(String path, ArrayList<NormalTask> normalTasks) throws Exception{
        OPCPackage pkg = OPCPackage.open(path, PackageAccess.READ);
        XSSFReader xssfReader = new XSSFReader(pkg);
        StylesTable styles = xssfReader.getStylesTable();
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
        Iterator<InputStream> sheetsData = xssfReader.getSheetsData();
        sheetsData.next();
        InputStream sheetInputStream = sheetsData.next();

        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        XMLReader sheetParser = saxFactory.newSAXParser().getXMLReader();
        NormalSaxHandler normalSaxHandler = new NormalSaxHandler(normalTasks);
        ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, normalSaxHandler, true);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(new InputSource(sheetInputStream));
    }

    public static String getLetter(String str) {
        String result = str.replaceAll("[0-9]", "");
        return result;
    }

}
