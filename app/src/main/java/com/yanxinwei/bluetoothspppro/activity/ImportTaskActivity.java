package com.yanxinwei.bluetoothspppro.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.adapter.TaskAdapter;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;
import com.yanxinwei.bluetoothspppro.model.NormalTask;
import com.yanxinwei.bluetoothspppro.util.L;
import com.yanxinwei.bluetoothspppro.util.SPUtils;
import com.yanxinwei.bluetoothspppro.util.T;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImportTaskActivity extends BaseActivity implements View.OnClickListener{

    private static final String TASK_PATH = "taskPath";
    private static final String TASK_REPEAT_PATH = "taskRepeatPath";
    //0:未初始化  1:普通任务  2:复检任务
    private static final String LATEST_TASK_TYPE = "latestTaskType";

    private static final int MENU_ID_IMPORT_TASK = 0x01;
    private static final int MENU_ID_IMPORT_REPEAT_TASK = 0x02;

    private static final int FILE_SELECT_CODE = 1001;

    private String taskPath;

    private ArrayList<NormalTask> mNormalTasks = new ArrayList<>(100);

    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler();

    private TaskAdapter mAdapter;

    @Bind(R.id.list_task)
    ListView mTaskList;

    @Bind(R.id.empty_view)
    TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_task);

        ButterKnife.bind(this);

        mTaskList.setEmptyView(mEmptyView);

        int taskType = (int) SPUtils.get(this, LATEST_TASK_TYPE, 0);
        mEmptyView.setOnClickListener(this);
        //未加载到任务,显示空界面
        if (taskType == 0){
            mTaskList.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            if (taskType == 1){
                taskPath = (String) SPUtils.get(this, TASK_PATH, "");
            } else {
                taskPath = (String) SPUtils.get(this, TASK_REPEAT_PATH, "");
            }
            loadTask();
        }
    }

    private void loadTask() {



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem miImportTask = menu.add(0, MENU_ID_IMPORT_TASK, 0, getString(R.string.menu_import_task));
        miImportTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //一直隐藏

        MenuItem miImportRepeatTask = menu.add(0, MENU_ID_IMPORT_REPEAT_TASK, 0,
                getString(R.string.menu_import_repeat_task));
        miImportRepeatTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //一直隐藏
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_ID_IMPORT_TASK:
                showFileSelected();
                return true;
            case MENU_ID_IMPORT_REPEAT_TASK:
                showFileSelected();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.empty_view:
                showFileSelected();
                break;
        }
    }

    private void showFileSelected() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要导入的excel任务"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILE_SELECT_CODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                T.showShort(this, uri.getPath());
                importTask(uri.getPath());
            }else {
                T.showShort(this, "请选择一个需要导入的任务文件");
            }
        }
    }

    private void importTask(final String path){
        mProgressDialog = ProgressDialog.show(this, null, "正在导入任务", true, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mNormalTasks.clear();
                    Row row;
                    Cell cell;
                    InputStream is = new FileInputStream(path);
                    XSSFWorkbook workbook = new XSSFWorkbook(is);
                    Sheet sheet = workbook.getSheetAt(1);
                    int rowCount = sheet.getPhysicalNumberOfRows();
                    for (int r = 1; r < rowCount; r++){
                        row = sheet.getRow(r);
                        int cellCount = row.getPhysicalNumberOfCells();
                        NormalTask normalTask = new NormalTask();
                        for (int c = 0; c < cellCount; c++){
                            cell = row.getCell(c);
                            try {
                                NormalTask.convertField(normalTask, c, convertCellValue(cell));
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                                normalTask = null;
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                                normalTask = null;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                normalTask = null;
                            }
                        }
                        if (null != normalTask)
                            mNormalTasks.add(normalTask);
                    }
//                    for (NormalTask normalTask : mNormalTasks){
//                        L.d("####"+normalTask.toString());
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            TaskAdapter adapter = new TaskAdapter(ImportTaskActivity.this, mNormalTasks);
                            mTaskList.setAdapter(adapter);
                        }
                    });
                }
            }
        }).start();

    }

    private Object convertCellValue(Cell cell){
        try {
            switch (cell.getCellType()){
                case Cell.CELL_TYPE_BLANK:
                    return "";
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
                case Cell.CELL_TYPE_ERROR:
                    return "";
                case Cell.CELL_TYPE_NUMERIC:
                    return cell.getNumericCellValue();
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                default:
                    return "";
            }
        }catch (Exception e){
            L.d(e.getMessage());
            return "";
        }
    }
}
