package com.yanxinwei.bluetoothspppro.parse;

import com.yanxinwei.bluetoothspppro.activity.ImportTaskActivity;
import com.yanxinwei.bluetoothspppro.model.NormalTask;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * NormalSaxHandler
 * Created by yanxinwei on 16/10/8.
 */

public class NormalSaxHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    public static final SimpleDateFormat sCellFormat = new SimpleDateFormat("M/d/yy H:mm");

    private ArrayList<NormalTask> mNormalTasks;

    private NormalTask currentNormalTask;
    private int currentRow;

    public NormalSaxHandler(ArrayList<NormalTask> normalTasks) {
        mNormalTasks = normalTasks;
    }

    @Override
    public void startRow(int rowNum) {
        currentRow = rowNum;
        if (currentRow > 0) currentNormalTask = new NormalTask();
    }

    @Override
    public void endRow() {
        if (currentRow > 0) mNormalTasks.add(currentNormalTask);
    }

    @Override
    public void cell(String cellReference, String formattedValue) {
        if (currentRow > 0) {
            if (cellReference.matches("J\\d+")) {
                try {
                    Date date = sCellFormat.parse(formattedValue);
                    formattedValue = ImportTaskActivity.date1.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            currentNormalTask.setData(ParseUtils.getLetter(cellReference), formattedValue);
        }
    }

    @Override
    public void headerFooter(String s, boolean b, String s1) {

    }
}
