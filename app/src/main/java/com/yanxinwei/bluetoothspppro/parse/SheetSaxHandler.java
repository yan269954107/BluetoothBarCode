package com.yanxinwei.bluetoothspppro.parse;

import android.util.Log;

import com.yanxinwei.bluetoothspppro.activity.ImportTaskActivity;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SheetSaxHandler
 * Created by yanxinwei on 16/10/7.
 */
public class SheetSaxHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    public static final SimpleDateFormat sCellFormat = new SimpleDateFormat("M/d/yy H:mm");
    private int currentRow;

    @Override
    public void startRow(int rowNum) {
        currentRow = rowNum;
        Log.d("tag", "---------------startRow : " + rowNum);
    }

    @Override
    public void endRow() {
        Log.d("tag", "---------------endRow : ");
    }

    @Override
    public void cell(String cellReference, String formattedValue) {
        if (cellReference.matches("J\\d+") && currentRow > 1) {
            try {
                Date date = sCellFormat.parse(formattedValue);
                formattedValue = ImportTaskActivity.date1.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.d("tag", "---------------cellReference : " + cellReference + "  formattedValue : " + formattedValue);
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        Log.d("tag", "---------------text : " + text + " isHeader : " + isHeader + " tagName : " + tagName );
    }


}
