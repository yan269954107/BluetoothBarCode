package com.yanxinwei.bluetoothspppro.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yanxinwei on 16/4/1.
 */
public class F {

    public static final String P = "com.yanxinwei.bluetoothspppro";

    public static final String TEST_TASK_DIR = SDLog.SD_PATH.concat("/检测任务");
    public static final String REPEAT_TASK_DIR = SDLog.SD_PATH.concat("/复检任务");

    public static String readerSign(Context context){
        String content = null;
        try {
            FileInputStream fis = context.openFileInput("sign.csr");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            S.g();
            content = S.sign(line);
            reader.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String readerSign(String path){
        String content = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            S.g();
            content = S.sign(line);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @return boolean
     */
    public static void copyFile(Context context, String oldPath) {
        File oldFile = new File(oldPath);
        try {
            int byteRead;
            if (oldFile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fos = context.openFileOutput("sign.csr", Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteRead);
                    fos.flush();
                }
                inStream.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        oldFile.deleteOnExit();
    }

    public static void createFolder(){
        File testFolder = new File(TEST_TASK_DIR);
        File repeatFolder = new File(REPEAT_TASK_DIR);
        if (!testFolder.exists()){
            testFolder.mkdir();
        }
        if (!repeatFolder.exists()){
            repeatFolder.mkdir();
        }
    }

}
