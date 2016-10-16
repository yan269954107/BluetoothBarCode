package com.yanxinwei.bluetoothspppro.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.globalPool;
import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.adapter.TaskAdapter;
import com.yanxinwei.bluetoothspppro.core.AppConstants;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;
import com.yanxinwei.bluetoothspppro.event.TaskCompleteEvent;
import com.yanxinwei.bluetoothspppro.model.NormalTask;
import com.yanxinwei.bluetoothspppro.model.RepeatTask;
import com.yanxinwei.bluetoothspppro.parse.ParseUtils;
import com.yanxinwei.bluetoothspppro.util.DialogUtils;
import com.yanxinwei.bluetoothspppro.util.F;
import com.yanxinwei.bluetoothspppro.util.MyConstants;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImportTaskActivity extends BaseActivity {

    public static final SimpleDateFormat date1 = new SimpleDateFormat("yyyy/M/d H:mm");
    public static final SimpleDateFormat date2 = new SimpleDateFormat("yyyy/M/d");

    public static final String TASK_PATH = "taskPath";
    //1:检测任务  2:复检任务
    public static final String TASK_TYPE = "taskType";
//    public static final String TASK_REPEAT_PATH = "taskRepeatPath";
//    //0:未初始化  1:普通任务  2:复检任务
//    public static final String LATEST_TASK_TYPE = "latestTaskType";

    private static final int MENU_ID_IMPORT_TASK = 0x01;
    private static final int MENU_ID_IMPORT_REPEAT_TASK = 0x02;
    private static final int MENU_ID_SCAN = 0X03;
    private static final int MENU_ID_WRITE = 0X04;

    private globalPool mGP = null;

    private static final int FILE_SELECT_CODE = 1001;
    private static final int REQUEST_CODE = 1002;
    private static final int REQUEST_CODE_SCAN = 1003;

    private String taskPath;

    private ArrayList<NormalTask> mNormalTasks = new ArrayList<>(20000);
    private ArrayList<RepeatTask> mRepeatTasks = new ArrayList<>(20000);

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

    public static Intent createIntent(Context context, String path, int type) {
        Intent intent = new Intent(context, ImportTaskActivity.class);
        intent.putExtra(TASK_PATH, path);
        intent.putExtra(TASK_TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_task);

        Log.d("tag", "@@@@onCreate");

        ButterKnife.bind(this);

        mGP = (globalPool) getApplicationContext();

        if (savedInstanceState != null) {
            taskType = savedInstanceState.getInt(TASK_TYPE);
            taskPath = savedInstanceState.getString(TASK_PATH);
        } else {
            taskType = getIntent().getIntExtra(TASK_TYPE, 0);
            if (taskType == 1) {
                taskPath = F.TEST_TASK_DIR.concat("/").concat(getIntent().getStringExtra(TASK_PATH));
            } else if (taskType == 2) {
                taskPath = F.REPEAT_TASK_DIR.concat("/").concat(getIntent().getStringExtra(TASK_PATH));
            }
        }



        mTaskList.setEmptyView(mEmptyView);
        mTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (taskType == 1) {
                    intent = NormalTaskActivity.createIntent(ImportTaskActivity.this,
                            (NormalTask) mAdapter.getItem(position), position, taskPath);
                } else if (taskType == 2) {
                    intent = RepeatTaskActivity.createIntent(ImportTaskActivity.this,
                            (RepeatTask) mAdapter.getItem(position), position, taskPath);
                }
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        loadTask(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
//        SDLog.close();
        Log.d("tag", "@@@@onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("tag", "@@@@onSaveInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("tag", "@@@@onSaveInstanceState");
        outState.putInt(TASK_TYPE, taskType);
        outState.putString(TASK_PATH, taskPath);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onCompleteEvent(TaskCompleteEvent event) {
        mAdapter.notifyDataSetChanged();
        showCount();
    }

    private void loadTask(Bundle bundle) {

        if (bundle != null) {
            if (taskType == 1) {
                mNormalTasks = mGP.getNormalTasks();
                mAdapter = new TaskAdapter(ImportTaskActivity.this, mNormalTasks, null);
                mTaskList.setAdapter(mAdapter);
                showCount();
            }
        } else {
            if (!taskPath.equals("") && taskType != 0) {
                importTask(taskPath);
            }
        }

    }

    private void importTask(final String path) {
        if (taskType == 1) {
            try {
                importNormalTask1(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mProgressDialog = ProgressDialog.show(this, null, "正在导入任务", true, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (taskType == 2) {
                            importRepeatTask(path);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                    SDLog.appendLog("219 IOException:"+e.getMessage());
                    } finally {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (taskType == 1) {
                                    mGP.setNormalTasks(mNormalTasks);
                                    mAdapter = new TaskAdapter(ImportTaskActivity.this, mNormalTasks, null);
                                } else if (taskType == 2) {
                                    mGP.setRepeatTasks(mRepeatTasks);
                                    mAdapter = new TaskAdapter(ImportTaskActivity.this, null, mRepeatTasks);
                                }
                                mTaskList.setAdapter(mAdapter);
                                showCount();
                                mProgressDialog.dismiss();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void importNormalTask(String path) throws IOException {
        mNormalTasks.clear();
        Row row;
        Cell cell;
        InputStream is = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        Sheet sheet = workbook.getSheetAt(MyConstants.WORKSHEET_INDEX);
        int rowCount = sheet.getPhysicalNumberOfRows();
//                    SDLog.appendLog("rowCount:"+rowCount);
        for (int r = 1; r < rowCount; r++) {
            row = sheet.getRow(r);
//                        int cellCount = row.getPhysicalNumberOfCells();
            NormalTask normalTask = new NormalTask();
            for (int c = 0; c < MyConstants.CELL_NUMBER_NORMAL; c++) {
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
    }

    private void importNormalTask1(final String path) throws Exception {
        mNormalTasks.clear();
        final long start = System.currentTimeMillis();
        mProgressDialog = ProgressDialog.show(this, null, "正在导入任务", true, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ParseUtils.convertNormal(path, mNormalTasks);
                    Log.d("tag", "@@@@ time : " + (System.currentTimeMillis() - start));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("tag", "@@@@ time : " + (System.currentTimeMillis() - start));
                            mGP.setNormalTasks(mNormalTasks);
                            mAdapter = new TaskAdapter(ImportTaskActivity.this, mNormalTasks, null);
                            mTaskList.setAdapter(mAdapter);
                            showCount();
                            mProgressDialog.dismiss();
                            Log.d("tag", "@@@@ time : " + (System.currentTimeMillis() - start));
                        }
                    });
                }
            }
        }).start();
    }


    private void importRepeatTask(String path) throws IOException {
        mRepeatTasks.clear();
        Row row;
        Cell cell;
        InputStream is = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(1);
        int rowCount = sheet.getPhysicalNumberOfRows();
//                    SDLog.appendLog("rowCount:"+rowCount);
        for (int r = 1; r < rowCount; r++) {
            row = sheet.getRow(r);
//                        int cellCount = row.getPhysicalNumberOfCells();
            RepeatTask repeatTask = new RepeatTask();
            for (int c = 0; c < AppConstants.CELL_NUMBER_REPEAT; c++) {
                cell = row.getCell(c);
                try {
                    Object cellValue = convertCellValue(cell);
                    RepeatTask.convertField(repeatTask, c, cellValue);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    repeatTask = null;
                    break;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    repeatTask = null;
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    repeatTask = null;
                    break;
                }
            }
            if (null != repeatTask)
                mRepeatTasks.add(repeatTask);
        }
    }

    private void showCount() {
        mLinearLayout.setVisibility(View.VISIBLE);
        int taskNum = 0, taskTested = 0, taskLeakage = 0;
        if (taskType == 1) {
            taskNum = mNormalTasks.size();
            for (NormalTask task : mNormalTasks) {
                if (!TextUtils.isEmpty(task.getDetectDate())) {
                    taskTested++;
                    if (task.getDetectValue() > task.getLeakageThreshold()) {
                        taskLeakage++;
                    }
                }
            }
        } else if (taskType == 2) {
            taskNum = mNormalTasks.size();
            for (RepeatTask task : mRepeatTasks) {
                if (!TextUtils.isEmpty(task.getRepeatDate())) {
                    taskTested++;
                    if (task.getRepeatValue() > task.getLeakageThreshold()) {
                        taskLeakage++;
                    }
                }
            }
        }
        txtTaskNum.setText("任务点数:" + taskNum);
        txtTaskTested.setText("已测点数:" + taskTested);
        txtTaskLeakage.setText("泄露点数:" + taskLeakage);
    }

    private Object convertCellValue(Cell cell) {
        try {
            if (null == cell) {
                return "";
            } else {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK:
                        return "";
                    case Cell.CELL_TYPE_BOOLEAN:
                        return cell.getBooleanCellValue();
                    case Cell.CELL_TYPE_ERROR:
                        return "";
                    case Cell.CELL_TYPE_NUMERIC:
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            if (cell.getCellStyle().getDataFormat() == 14) {
                                return date2.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                            } else if (cell.getCellStyle().getDataFormat() == 22) {
                                return date1.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                            } else {
                                return date1.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                            }
                        }
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        else if (cell.getCellStyle().getDataFormat() == 58) {
                            return date1.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                        } else {
                            return cell.getNumericCellValue();
                        }

                    case Cell.CELL_TYPE_STRING:
                        return cell.getStringCellValue();
                    default:
                        return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (taskType == 1) {
            MenuItem miImportTask = menu.add(0, MENU_ID_SCAN, 0, getString(R.string.menu_scan));
            miImportTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            MenuItem miWrite = menu.add(0, MENU_ID_WRITE, 1, "输入");
            miWrite.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_SCAN:
                Intent intent = new Intent(this, SimpleScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                return true;
            case MENU_ID_WRITE:
                DialogUtils.showDialogInput("输入标签号", this, new DialogUtils.DialogInputListener() {
                    @Override
                    public void onInput(String content) {
                        navigateToScannedTask(content);
                    }
                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            long start = System.currentTimeMillis();
//                            Fillo fillo = new Fillo();
//                            Connection connection = fillo.getConnection(taskPath);
//                            String strQuery="Update 检测信息表 Set 检测日期='2016/3/28 18:10' where 标签号='01542.001'";
//                            connection.executeUpdate(strQuery);
//                            connection.close();
//                            System.out.println("total time : " + (System.currentTimeMillis() - start));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN) {
                String code = data.getStringExtra(SimpleScannerActivity.SCAN_CODE);
//                Log.d("tag", "#### code : " + code);
                if (code != null && code.length() == 8) {
                    String label = code.substring(3);

                    navigateToScannedTask(label);
                }
            }
        }
    }

    private void navigateToScannedTask(String label) {
        int index = searchNormalTask(label);
        if (index == -1) {
            Toast.makeText(this, "在当前文档中未能找到标签号为" + label + "的设备", Toast.LENGTH_SHORT).show();
        } else {
            int totalSize = mNormalTasks.size();
            ArrayList<NormalTask> scanTasks = new ArrayList<>();
            Intent intent = ScannedTaskActivity.createIntent(ImportTaskActivity.this, taskPath, index);
            NormalTask task = mNormalTasks.get(index);
            while (task.getLabelNumber().startsWith(label)) {
                scanTasks.add(task);
                index++;
                if (index > totalSize - 1) {
                    break;
                }
                task = mNormalTasks.get(index);
            }
            ScannedTaskActivity.sNormalTasks = scanTasks;
            startActivity(intent);
        }
    }

    private int searchNormalTask(String label) {
        label = label + ".000";
        int low = 0;
        int high = mNormalTasks.size() - 1;
        int mid;
        NormalTask normalTask;
        while (low <= high) {
            mid = (low + high) / 2;
            normalTask = mNormalTasks.get(mid);
            if (label.compareTo(normalTask.getLabelNumber()) > 0) {
                low = mid + 1;
            } else if (label.compareTo(normalTask.getLabelNumber()) < 0) {
                high = mid - 1;
            } else {
                System.out.println(normalTask.toString());
                return mid;
            }
        }
        return -1;
    }
}
