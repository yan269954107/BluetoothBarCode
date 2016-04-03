package com.yanxinwei.bluetoothspppro.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.adapter.TaskAdapter;
import com.yanxinwei.bluetoothspppro.core.AppConstants;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;
import com.yanxinwei.bluetoothspppro.model.NormalTask;
import com.yanxinwei.bluetoothspppro.util.F;
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

public class ImportTaskActivity extends BaseActivity {

    public static final String TASK_PATH = "taskPath";
    //1:检测任务  2:复检任务
    public static final String TASK_TYPE = "taskType";
//    public static final String TASK_REPEAT_PATH = "taskRepeatPath";
//    //0:未初始化  1:普通任务  2:复检任务
//    public static final String LATEST_TASK_TYPE = "latestTaskType";

    private static final int MENU_ID_IMPORT_TASK = 0x01;
    private static final int MENU_ID_IMPORT_REPEAT_TASK = 0x02;

    private static final int FILE_SELECT_CODE = 1001;
    private static final int REQUEST_CODE = 1002;

    private String taskPath;

    private ArrayList<NormalTask> mNormalTasks = new ArrayList<>(100);

    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler();

    private TaskAdapter mAdapter;

    private int taskType = 0;

    @Bind(R.id.list_task)
    ListView mTaskList;

    @Bind(R.id.empty_view)
    TextView mEmptyView;

    @Bind(R.id.linearLayout)
    LinearLayout mLinearLayout;

    @Bind(R.id.txt_task_num)
    TextView txtTaskNum;

    @Bind(R.id.txt_task_tested)
    TextView txtTaskTested;

    @Bind(R.id.txt_task_leakage)
    TextView txtTaskLeakage;

    public static Intent createIntent(Context context, String path, int type){
        Intent intent = new Intent(context, ImportTaskActivity.class);
        intent.putExtra(TASK_PATH, path);
        intent.putExtra(TASK_TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_task);

        ButterKnife.bind(this);

//        SDLog.open();
        taskType = getIntent().getIntExtra(TASK_TYPE, 0);
        if (taskType == 1){
            taskPath = F.TEST_TASK_DIR.concat("/").concat(getIntent().getStringExtra(TASK_PATH));
        }else if (taskType == 2){
            taskPath = F.REPEAT_TASK_DIR.concat("/").concat(getIntent().getStringExtra(TASK_PATH));
        }


        mTaskList.setEmptyView(mEmptyView);
        mTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (taskType == 1){
                   intent = NormalTaskActivity.createIntent(ImportTaskActivity.this,
                            mAdapter.getItem(position), position, taskPath);
                }else {

                }

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        loadTask();
//        taskType = (int) SPUtils.get(this, LATEST_TASK_TYPE, 0);
//        //未加载到任务,显示空界面
//        if (taskType == 0){
//            mTaskList.setVisibility(View.GONE);
//            mEmptyView.setVisibility(View.VISIBLE);
//        } else {
//            if (taskType == 1){
//                taskPath = (String) SPUtils.get(this, TASK_PATH, "");
//                getSupportActionBar().setTitle("检测任务");
//            } else {
//                taskPath = (String) SPUtils.get(this, TASK_REPEAT_PATH, "");
//                getSupportActionBar().setTitle("复检任务");
//            }
//            loadTask();
//        }

    }

    @Override
    protected void onDestroy() {
//        SDLog.close();
        super.onDestroy();
    }

    private void loadTask() {

        if (!taskPath.equals("") && taskType != 0){
            importTask(taskPath);
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem miImportTask = menu.add(0, MENU_ID_IMPORT_TASK, 0, getString(R.string.menu_import_task));
//        miImportTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //一直隐藏
//
//        MenuItem miImportRepeatTask = menu.add(0, MENU_ID_IMPORT_REPEAT_TASK, 0,
//                getString(R.string.menu_import_repeat_task));
//        miImportRepeatTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //一直隐藏
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case MENU_ID_IMPORT_TASK:
//                taskType = 1;
//                showFileSelected();
//                return true;
//            case MENU_ID_IMPORT_REPEAT_TASK:
////                taskType = 2;
////                showFileSelected();
//                return  true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    private void showFileSelected() {
//
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(Intent.createChooser(intent, "请选择一个要导入的excel任务"),
//                    FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            T.showShort(this, "请安装文件管理器");
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILE_SELECT_CODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                T.showShort(this, uri.getPath());
//                SDLog.appendLog("path"+uri.getPath());
                importTask(uri.getPath());
            }else {
                T.showShort(this, "请选择一个需要导入的任务文件");
            }
        }else if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK){
                int row = data.getIntExtra(NormalTaskActivity.SAVED_POSITION, -1);
                if (row != -1){
                    String detectDate = data.getStringExtra(NormalTaskActivity.DETECTED_DATE);
                    if (!TextUtils.isEmpty(detectDate)){
                        NormalTask task = mNormalTasks.get(row);
                        task.setDetectDate(detectDate);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void importTask(final String path){
//        SDLog.appendLog("importTask:start import");
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
//                    SDLog.appendLog("rowCount:"+rowCount);
                    for (int r = 1; r < rowCount; r++){
                        row = sheet.getRow(r);
//                        int cellCount = row.getPhysicalNumberOfCells();
                        NormalTask normalTask = new NormalTask();
                        for (int c = 0; c < AppConstants.CELL_NUMBER; c++){
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

                } catch (IOException e) {
//                    e.printStackTrace();
//                    SDLog.appendLog("219 IOException:"+e.getMessage());
                } finally {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            mAdapter = new TaskAdapter(ImportTaskActivity.this, mNormalTasks);
                            mTaskList.setAdapter(mAdapter);
                            showCount();
//                            SPUtils.put(ImportTaskActivity.this, LATEST_TASK_TYPE, taskType);
//                            String key;
//                            if (taskType == 1){
//                                key = TASK_PATH;
//                                getSupportActionBar().setTitle("检测任务");
//                            }else {
//                                key = TASK_REPEAT_PATH;
//                                getSupportActionBar().setTitle("复检任务");
//                            }
//                            SPUtils.put(ImportTaskActivity.this, key, path);
                        }
                    });
                }
            }
        }).start();

    }

    private void showCount(){
        mLinearLayout.setVisibility(View.VISIBLE);
        int taskNum = 0,taskTested = 0,taskLeakage = 0;
        if (taskType == 1){
            for (NormalTask task : mNormalTasks){
                taskNum ++;
                if (!TextUtils.isEmpty(task.getDetectDate())){
                    taskTested ++;
                    if (task.getDetectValue() > task.getLeakageThreshold()){
                        taskLeakage ++;
                    }
                }
            }
        }else if (taskType == 2){

        }
        txtTaskNum.setText("任务点数:"+taskNum);
        txtTaskTested.setText("已测点数:"+taskTested);
        txtTaskLeakage.setText("泄露点数:"+taskLeakage);
    }

    private Object convertCellValue(Cell cell){
        try {
            if (null == cell){
                return "";
            }else {
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
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
