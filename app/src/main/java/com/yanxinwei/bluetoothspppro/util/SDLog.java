package com.yanxinwei.bluetoothspppro.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by yanxinwei on 16/3/24.
 */
public class SDLog {

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String LOG_PATH = SD_PATH.concat("/").concat("bluetoothpro.log");

    private static PrintWriter mPrintWriter = null;

    public static void open(){
        try {
            File file = new File(LOG_PATH);
            if (!file.exists())
                file.createNewFile();
            if (mPrintWriter == null)
                mPrintWriter = new PrintWriter(file);
        }catch (IOException e) {

        }
    }

    public static void appendLog(String info){
        mPrintWriter.append(info+"\r\n");
            L.d("append:"+info);
        mPrintWriter.flush();
    }

    public static void close(){
        if (null != mPrintWriter)
            mPrintWriter.close();
        mPrintWriter = null;
    }

}
