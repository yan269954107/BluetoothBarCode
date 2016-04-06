package com.yanxinwei.bluetoothspppro.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.BaseCommActivity;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.actMain;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.globalPool;
import com.yanxinwei.bluetoothspppro.BuildConfig;
import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.bluetooth.BluetoothSppClient;
import com.yanxinwei.bluetoothspppro.core.AppConstants;
import com.yanxinwei.bluetoothspppro.event.TaskCompleteEvent;
import com.yanxinwei.bluetoothspppro.itf.AnimatorEndListener;
import com.yanxinwei.bluetoothspppro.model.NormalTask;
import com.yanxinwei.bluetoothspppro.util.SPUtils;
import com.yanxinwei.bluetoothspppro.util.T;
import com.yanxinwei.bluetoothspppro.view.NumberTextView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NormalTaskActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String NORMAL_TASK = "normal_task";
    public static final String TASK_ON_EXCEL_ROW = "task_on_excel_row";
    public static final String TASK_FILE_PATH = "tak=sk_file_path";

    public static final String IS_SAVED = "isSaved";
    public static final String SAVED_POSITION = "savedPosition";
    public static final String DETECTED_DATE = "detectedDate";

    @Bind(R.id.edt_detected_equipment)
    EditText mEdtDetectedEquipment;

    @Bind(R.id.edt_area)
    EditText mEdtArea;

    @Bind(R.id.edt_detected_device)
    EditText mEdtDetectedDevice;

    @Bind(R.id.edt_label_number)
    EditText mEdtLabelNumber;

    @Bind(R.id.edt_address)
    EditText mEdtAddress;

    @Bind(R.id.edt_unit_type)
    EditText mEdtUnitType;

    @Bind(R.id.edt_unit_sub_type)
    EditText mEdtUnitSubType;

    @Bind(R.id.edt_leakage_threshold)
    EditText mEdtLeakageThreshold;

    @Bind(R.id.edt_min_time)
    EditText mEdtMinTime;

    @Bind(R.id.edt_detect_date)
    EditText mEdtDetectDate;

    @Bind(R.id.edt_detect_device)
    EditText mEdtDetectDevice;

    @Bind(R.id.edt_detect_value)
    EditText mEdtDetectValue;

    @Bind(R.id.edt_is_leakage)
    EditText mEdtIsLeakage;

    @Bind(R.id.edt_leakage_position)
    EditText mEdtLeakagePosition;

    @Bind(R.id.edt_remarks)
    EditText mEdtRemarks;

    @Bind(R.id.btn_test)
    Button mBeginTest;

    @Bind(R.id.btn_save)
    Button mSaveResult;

    @Bind(R.id.btn_previous)
    Button mBtnPrevious;

    @Bind(R.id.btn_next)
    Button mBtnNext;

    @Bind(R.id.btn_quit)
    Button mBtnQuit;

    @Bind(R.id.btn_alarm)
    Button mBtnAlarm;

    private View mDialogViewDetect;
    private Dialog mDialogDetect;

    private NormalTask task;
    private LayoutInflater mInflater;
    private Button mBtnComplete;
    private TextView mTxtDetectValue;
    private TextView mTxtDetectMaxValue;
    private NumberTextView mTxtRemainderTime;

    private int mDetectMinTime;
    private String mDetectTime;

    //是否正在检测数据
    private boolean isDetecting = false;
    private boolean isSaved = false;

    private int mExcelRow = -1;
    private String mTaskPath;

    private boolean isCompleted = false;

    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler();

    //蓝牙接收数据相关参数
    private boolean mbThreadStop = false;
    /**
     * 对象:引用全局的蓝牙连接对象
     */
    protected BluetoothSppClient mBSC = null;
    protected globalPool mGP = null;
    private Double mDetectMaxValue = -99999.0;

    public static Intent createIntent(Context context, NormalTask normalTask, int row, String taskFilePath) {
        Intent intent = new Intent(context, NormalTaskActivity.class);
        intent.putExtra(NORMAL_TASK, normalTask);
        intent.putExtra(TASK_ON_EXCEL_ROW, row);
        intent.putExtra(TASK_FILE_PATH, taskFilePath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_task);

        ButterKnife.bind(this);

        this.mBSC = ((globalPool) this.getApplicationContext()).mBSC;
        mGP = (globalPool) this.getApplicationContext();

        bindValue();

        mBeginTest.setOnClickListener(this);
        mSaveResult.setOnClickListener(this);
        mBtnPrevious.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnQuit.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mInflater = getLayoutInflater();

        mExcelRow = getIntent().getIntExtra(TASK_ON_EXCEL_ROW, -1);
        if (mExcelRow != -1) {
            mExcelRow += 1;  //要考虑表头的1行
        }
        mTaskPath = getIntent().getStringExtra(TASK_FILE_PATH);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbThreadStop = true;
        if (null != mBSC)
            mBSC.killReceiveData_StopFlg();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent();
//            intent.putExtra(IS_SAVED, isSaved);
//            if (isSaved) {
//                intent.putExtra(SAVED_POSITION, mExcelRow - 1);
//                intent.putExtra(DETECTED_DATE, mEdtDetectDate.getText().toString());
//                setResult(RESULT_OK, intent);
//            } else {
//                setResult(RESULT_CANCELED, intent);
//            }
//        }
        return super.onKeyDown(keyCode, event);
    }

    private void bindValue() {

        task = (NormalTask) getIntent().getSerializableExtra(NORMAL_TASK);
        mEdtDetectedEquipment.setText(task.getDetectedEquipment());
        mEdtArea.setText(task.getArea());
        mEdtDetectedDevice.setText(task.getDetectedDevice());
        mEdtLabelNumber.setText(task.getLabelNumber());
        mEdtAddress.setText(task.getAddress());
        mEdtUnitType.setText(task.getUnitType());
        mEdtUnitSubType.setText(task.getUnitSubType());
        mEdtLeakageThreshold.setText(task.getLeakageThreshold() + "");
        mEdtMinTime.setText(task.getDetectMiniTime() + "");
        mDetectMinTime = (int) task.getDetectMiniTime();
        if (BuildConfig.DEBUG)
            mDetectMinTime = 3;
        mEdtDetectDate.setText(task.getDetectDate());
        mEdtDetectDevice.setText(task.getDetectDevice());
        mEdtDetectValue.setText(task.getDetectValue() + "");
        if (task.getIsLeakage() == 0) {
            mEdtIsLeakage.setText("否");
        } else {
            mEdtIsLeakage.setText("是");
        }
        mEdtLeakagePosition.setText(task.getLeakagePosition());
        mEdtLeakagePosition.setOnClickListener(this);
        mEdtRemarks.setText(task.getRemarks());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                startDetect();
                break;
            case R.id.btn_save:
//                saveTask();
                preSaveTask();
                break;
            case R.id.edt_leakage_position:
                showDialogList();
                break;
            case R.id.btn_complete:
                completeDetect();
                break;
            case R.id.btn_previous:
                jumpToTask(1);
                break;
            case R.id.btn_quit:
                finish();
                break;
            case R.id.btn_next:
                jumpToTask(2);
                break;
            case R.id.btn_alarm:
                alarm();
                break;
        }
    }

    private void alarm() {
        if (!isCompleted){
            T.showShort(this, "请先进行检测");
            return;
        }
        mDetectMaxValue = 100000.0;
        mEdtDetectValue.setText(mDetectMaxValue + "");
        mEdtIsLeakage.setText("是");
        task.setIsLeakage(1);
        showDialogList();
    }

    /**
     * 跳转任务
     * @param type  1:前一条   2:下一条
     */
    private void jumpToTask(int type){
        int row = mExcelRow - 1;
        if (type == 1){
            row -= 1;
        }else {
            row += 1;
        }
        int length = mGP.getNormalTasks().size();
        if (row < 0){
            T.showShort(this, "已经是第一条了");
        }else if (row > length - 1){
            T.showShort(this, "已经是最后一条了");
        }else {
            NormalTask targetTask = mGP.getNormalTasks().get(row);
            String path = mTaskPath;
            Intent intent = createIntent(this, targetTask, row, path);
            startActivity(intent);
            finish();
        }
    }

    private void preSaveTask(){
        if (!isCompleted) {
            T.showShort(this, "请检测后再保存");
            return;
        }

        if (mDetectMaxValue >= task.getLeakageThreshold() &&
                TextUtils.isEmpty(mEdtLeakagePosition.getText().toString())) {
            showDialogList();
            return;
        }

        saveTask();
    }

    private void saveTask() {

        mProgressDialog = ProgressDialog.show(this, null, "正在保存数据", true, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = mTaskPath;
                if (!path.equals("")) {
                    try {
                        InputStream is = new FileInputStream(path);
                        XSSFWorkbook workbook = new XSSFWorkbook(is);
                        Sheet sheet = workbook.getSheetAt(1);
                        Row row = sheet.getRow(mExcelRow);

                        setCellValue(row, AppConstants.CELL_DETECT_DATE, mEdtDetectDate.getText().toString());

                        setCellValue(row, AppConstants.CELL_DETECT_DEVICE, mEdtDetectDevice.getText().toString());

                        Cell cellDetectValue = getCell(row, AppConstants.CELL_DETECT_VALUE);
                        cellDetectValue.setCellValue(Double.parseDouble(mEdtDetectValue.getText().toString()));

                        int isLeakage = (int) task.getIsLeakage();
                        Cell cellIsLeakage = getCell(row, AppConstants.CELL_IS_LEAKAGE);
                        cellIsLeakage.setCellValue(task.getIsLeakage());

                        if (isLeakage == 1) {
                            setCellValue(row, AppConstants.CELL_LEAKAGE_POSITION, mEdtLeakagePosition.getText().toString());
                        }

                        setCellValue(row, AppConstants.CELL_REMARKS, mEdtRemarks.getText().toString());

                        FileOutputStream os = new FileOutputStream(path);
                        workbook.write(os);

                        is.close();
                        os.close();

                        showToast("保存成功");
                        isSaved = true;

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                NormalTask normalTask = mGP.getNormalTasks().get(mExcelRow - 1);
                                normalTask.setDetectValue(mDetectMaxValue);
                                normalTask.setDetectDate(mDetectTime);
                                String detectDevice = (String) SPUtils.get(NormalTaskActivity.this,
                                        SPUtils.SP_DETECT_DEVICE, "未设置");
                                normalTask.setDetectDevice(detectDevice);
                                normalTask.setIsLeakage(task.getIsLeakage());
                                EventBus.getDefault().post(new TaskCompleteEvent());
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        dismissProgress();
                    }

                } else {
                    showToast("请退出后重新导入任务");
                    dismissProgress();
                }
            }
        }).start();


    }

    private void showToast(final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                T.showShort(NormalTaskActivity.this, content);
            }
        });
    }

    private void dismissProgress() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    private void setCellValue(Row row, int cellNumber, String value) {
        Cell cell = getCell(row, cellNumber);
        cell.setCellValue(value);
    }

    private Cell getCell(Row row, int cellNumber) {
        Cell cell = row.getCell(cellNumber);
        if (null == cell) {
            cell = row.createCell(cellNumber);
        }
        return cell;
    }

    private void completeDetect() {
        isDetecting = false;
        mDialogDetect.dismiss();

        mDetectTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        mEdtDetectDate.setText(mDetectTime);
        String detectDevice = (String) SPUtils.get(this, SPUtils.SP_DETECT_DEVICE, "未设置");
        mEdtDetectDevice.setText(detectDevice);

        if (mDetectMaxValue < 1){
            mDetectMaxValue = 0.0;
        }else if (mDetectMaxValue > 30000){
            mDetectMaxValue = 100000.0;
        }
        if (BuildConfig.DEBUG)
            mDetectMaxValue = 1111.0;

        mEdtDetectValue.setText(mDetectMaxValue + "");
        if (mDetectMaxValue >= task.getLeakageThreshold()) {
            mEdtIsLeakage.setText("是");
            task.setIsLeakage(1);
        } else {
            mEdtIsLeakage.setText("否");
            task.setIsLeakage(0);
        }

        isCompleted = true;

    }


    private void showDialogList() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View convertView = mInflater.inflate(R.layout.dialog_list_view, null);
        dialogBuilder.setView(convertView);
        dialogBuilder.setTitle("请选择泄露部位");
        ListView lv = (ListView) convertView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                AppConstants.leakagePosition);
        lv.setAdapter(adapter);
        final AlertDialog dialog = dialogBuilder.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog != null) {
                    String p = AppConstants.leakagePosition[position];
                    mEdtLeakagePosition.setText(p);
                    NormalTask normalTask = mGP.getNormalTasks().get(mExcelRow - 1);
                    normalTask.setLeakagePosition(p);
                    dialog.dismiss();
                    if (isCompleted){
                        jumpToTask(2);
                    }
                }
            }
        });
    }

    private void startDetect() {
        this.mBSC = ((globalPool) this.getApplicationContext()).mBSC;
        if (null == this.mBSC || !this.mBSC.isConnect()) {    //当进入时，发现连接已丢失，则直接返回主界面
//            this.setResult(Activity.RESULT_CANCELED); //返回到主界面
//            this.finish();
            Intent intent = new Intent(this, actMain.class);
            startActivity(intent);
            T.showShort(this, "请先连接设备");
            return;
        }
        initIO_Mode();
        setEndFlg();
        //初始化结束，启动接收线程
        new receiveTask()
                .executeOnExecutor(BaseCommActivity.FULL_TASK_EXECUTOR);
        showDetectDialog();
    }

    private void showDetectDialog() {

        isDetecting = true;
        if (mDialogDetect == null) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            mDialogViewDetect = mInflater.inflate(R.layout.dialog_detect_data, null);

            mBtnComplete = (Button) mDialogViewDetect.findViewById(R.id.btn_complete);
            mBtnComplete.setOnClickListener(this);
            mTxtDetectValue = (TextView) mDialogViewDetect.findViewById(R.id.txt_detect_value);
            mTxtDetectMaxValue = (TextView) mDialogViewDetect.findViewById(R.id.txt_detect_max_value);
            mTxtRemainderTime = (NumberTextView) mDialogViewDetect.findViewById(R.id.txt_remainder_time);


            dialogBuilder.setView(mDialogViewDetect);
            dialogBuilder.setTitle("检测任务");
            dialogBuilder.setCancelable(false);
            mDialogDetect = dialogBuilder.show();
        } else {
            mDialogDetect.show();
        }
        showRemainderTime();
    }

    private void showRemainderTime() {
        mBtnComplete.setVisibility(View.INVISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofInt(mTxtRemainderTime, "number", mDetectMinTime, 0)
                .setDuration(mDetectMinTime * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBtnComplete.setVisibility(View.VISIBLE);
            }
        });
    }


    //----------------蓝牙接受数据相关方法-------------------//
    /*多线程处理(建立蓝牙设备的串行通信连接)*/
    private class receiveTask extends AsyncTask<String, String, Integer> {
        /**
         * 常量:连接丢失
         */
        private final static int CONNECT_LOST = 0x01;
        /**
         * 常量:线程任务结束
         */
        private final static int THREAD_END = 0x02;

        /**
         * 线程启动初始化操作
         */
        @Override
        public void onPreExecute() {
            mbThreadStop = false;
        }

        /**
         * 线程异步处理
         */
        @Override
        protected Integer doInBackground(String... arg0) {
            mBSC.Receive(); //首次启动调用一次以启动接收线程
            while (!mbThreadStop) {
                if (!mBSC.isConnect())
                    return CONNECT_LOST; //检查连接是否丢失
                else
                    SystemClock.sleep(10);//接收等待延时，提高接收效率

                if (mBSC.getReceiveBufLen() > 0)
                    this.publishProgress(mBSC.Receive());
            }
            return THREAD_END;
        }

        /**
         * 线程内更新处理
         */
        @Override
        public void onProgressUpdate(String... progress) {
//            mtvRecView.append(progress[0]); //显示区中追加数据
            String data = progress[0];
            if (isDetecting) {
                int index = data.indexOf("OK");
                if (index != -1) {
                    data = data.substring(0, index);
                }
                try {
                    Double value = Double.valueOf(data);
                    mTxtDetectValue.setText(value + "");
                    if (value > mDetectMaxValue) {
                        mDetectMaxValue = value;
                        mTxtDetectMaxValue.setText(mDetectMaxValue + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 阻塞任务执行完后的清理工作
         */
        @Override
        public void onPostExecute(Integer result) {
            if (CONNECT_LOST == result) //通信连接丢失
                T.showShort(NormalTaskActivity.this, "通信连接丢失请重新连接设备");
            else
                T.showShort(NormalTaskActivity.this, "接收数据终止");
        }
    }

    /**
     * 初始化输入输出模式
     *
     * @return void
     */
    protected void initIO_Mode() {
        mBSC.setRxdMode(BluetoothSppClient.IO_MODE_STRING);
        mBSC.setTxdMode(BluetoothSppClient.IO_MODE_STRING);
    }

    /**
     * 载入终止符配置信息
     * String sModelName 模块名称
     *
     * @return void
     */
    private void setEndFlg() {
        this.mBSC.setReceiveStopFlg(BaseCommActivity.msEND_FLGS[0]); //设置结束符
    }

}
