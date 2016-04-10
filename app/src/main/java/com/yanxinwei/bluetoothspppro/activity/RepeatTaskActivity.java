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
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
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
import com.yanxinwei.bluetoothspppro.model.RepeatTask;
import com.yanxinwei.bluetoothspppro.util.SPUtils;
import com.yanxinwei.bluetoothspppro.util.T;
import com.yanxinwei.bluetoothspppro.view.NumberTextView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepeatTaskActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String REPEAT_TASK = "repeat_task";

    private XSSFCellStyle cellStyle1;
    private XSSFCellStyle cellStyle2;

    private static final int MODE_DETECT = 1;
    private static final int MODE_BACKGROUND = 2;
    private int detectMode = MODE_DETECT;

    @Bind(R.id.edt_detected_id)
    EditText mEdtDetectedId;

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

    @Bind(R.id.edt_detect_value)
    EditText mEdtDetectValue;

    @Bind(R.id.edt_maintain_date)
    EditText mEdtMaintainDate;

    @Bind(R.id.edt_maintain_personnel)
    EditText mEDTMaintainPersonnel;

    @Bind(R.id.edt_maintain_measure)
    EditText mEdtMaintainMeasure;

    @Bind(R.id.edt_repeat_date)
    EditText mEdtRepeatDate;

    @Bind(R.id.edt_repeat_device)
    EditText mEdtRepeatDevice;

    @Bind(R.id.edt_repeat_value)
    EditText mEdtRepeatValue;

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

    @Bind(R.id.btn_background)
    Button mBtnBackground;

    @Bind(R.id.txt_background)
    TextView mTxtBackground;

    private View mDialogViewDetect;
    private Dialog mDialogDetect;

    private RepeatTask task;
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

    private boolean isShowToast = true;

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

    private float mBackgroundValue = 0;

    public static Intent createIntent(Context context, RepeatTask repeatTask, int row, String taskFilePath){
        Intent intent = new Intent(context, RepeatTaskActivity.class);
        intent.putExtra(REPEAT_TASK, repeatTask);
        intent.putExtra(NormalTaskActivity.TASK_ON_EXCEL_ROW, row);
        intent.putExtra(NormalTaskActivity.TASK_FILE_PATH, taskFilePath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_task);
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
        mBtnBackground.setOnClickListener(this);
        mInflater = getLayoutInflater();

        mExcelRow = getIntent().getIntExtra(NormalTaskActivity.TASK_ON_EXCEL_ROW, -1);
        if (mExcelRow != -1) {
            mExcelRow += 1;  //要考虑表头的1行
        }
        mTaskPath = getIntent().getStringExtra(NormalTaskActivity.TASK_FILE_PATH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbThreadStop = true;
        if (null != mBSC)
            mBSC.killReceiveData_StopFlg();
    }

    private void bindValue() {
        task = (RepeatTask)getIntent().getSerializableExtra(REPEAT_TASK);

        mEdtDetectedId.setText(((int) task.getDetectId())+"");
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
        mEdtDetectValue.setText(task.getDetectValue() + "");
        mEdtMaintainDate.setText(task.getMaintainDate());
        mEDTMaintainPersonnel.setText(task.getMaintainPersonnel());
        mEdtMaintainMeasure.setText(task.getMaintainMeasure());
        mEdtRepeatDate.setText(task.getRepeatDate());
        mEdtRepeatDevice.setText(task.getRepeatDevice());
        mEdtRepeatValue.setText(task.getRepeatValue()+"");

        mEdtRemarks.setText(task.getRemarks());

        mBackgroundValue = (float) SPUtils.get(this, NormalTaskActivity.BACKGROUND_VALUE, 0f);
        mTxtBackground.setText("背景值:"+mBackgroundValue);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                detectMode = MODE_DETECT;
                startDetect();
                break;
            case R.id.btn_save:
//                saveTask();
                preSaveTask();
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
            case R.id.btn_background:
                detectMode = MODE_BACKGROUND;
                startDetect();
                break;
        }
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

    private void preSaveTask(){
        if (!isCompleted) {
            T.showShort(this, "请检测后再保存");
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

                        CreationHelper helper = workbook.getCreationHelper();
                        cellStyle1 = workbook.createCellStyle();
                        cellStyle1.setDataFormat(helper.createDataFormat().getFormat("yyyy/M/d h:mm"));
                        cellStyle2 = workbook.createCellStyle();
                        cellStyle2.setDataFormat(helper.createDataFormat().getFormat("yyyy/M/d"));

                        Row row = sheet.getRow(mExcelRow);

                        setCellDateValue(row, AppConstants.CELL_REPEAT_DATE, mEdtRepeatDate.getText().toString(), 2);

                        setCellValue(row, AppConstants.CELL_REPEAT_DEVICE, mEdtRepeatDevice.getText().toString());

                        Cell cellRepeatValue = getCell(row, AppConstants.CELL_REPEAT_VALUE);
                        cellRepeatValue.setCellValue(Double.parseDouble(mEdtRepeatValue.getText().toString()));

                        setCellValue(row, AppConstants.CELL_REPEAT_REMARK, mEdtRemarks.getText().toString());

                        FileOutputStream os = new FileOutputStream(path);
                        workbook.write(os);

                        is.close();
                        os.close();

                        showToast("保存成功");
                        isSaved = true;

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                RepeatTask repeatTask = mGP.getRepeatTasks().get(mExcelRow - 1);
                                repeatTask.setRepeatValue(mDetectMaxValue);
                                repeatTask.setRepeatDate(mDetectTime);
                                String detectDevice = (String) SPUtils.get(RepeatTaskActivity.this,
                                        SPUtils.SP_DETECT_DEVICE, "未设置");
                                repeatTask.setRepeatDevice(detectDevice);
                                EventBus.getDefault().post(new TaskCompleteEvent());
                                jumpToTask(2);
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

    private void setCellValue(Row row, int cellNumber, String value) {
        Cell cell = getCell(row, cellNumber);
        cell.setCellValue(value);
    }

    /**
     * @param row
     * @param cellNumber
     * @param value
     * @param type       1:yyyy/M/d h:mm   2:yyyy/M/d
     */
    private void setCellDateValue(Row row, int cellNumber, String value, int type) {
        Cell cell = getCell(row, cellNumber);
        Date date;
        try {
            if (type == 1) {
                date = ImportTaskActivity.date1.parse(value);
                cell.setCellValue(date);
                cell.setCellStyle(cellStyle1);
            } else {
                date = ImportTaskActivity.date2.parse(value);
                cell.setCellValue(date);
                cell.setCellStyle(cellStyle2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            cell.setCellValue(value);
        }
    }

    private Cell getCell(Row row, int cellNumber) {
        Cell cell = row.getCell(cellNumber);
        if (null == cell) {
            cell = row.createCell(cellNumber);
        }
        return cell;
    }

    private void showToast(final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                T.showShort(RepeatTaskActivity.this, content);
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

    private void alarm() {
        if (!isCompleted) {
            T.showShort(this, "请先进行检测");
            return;
        }
        mDetectMaxValue = 100000.0;
        mEdtRepeatValue.setText(mDetectMaxValue + "");
    }

    private void completeDetect() {
        isDetecting = false;
        mDialogDetect.dismiss();

        if (detectMode == MODE_DETECT) {
            saveDetect();
        } else {
            saveBackground();
        }
    }

    private void saveDetect() {
        mDetectTime = ImportTaskActivity.date1.format(new Date());
        mEdtDetectDate.setText(mDetectTime);
        String detectDevice = (String) SPUtils.get(this, SPUtils.SP_DETECT_DEVICE, "未设置");
        mEdtRepeatDevice.setText(detectDevice);

        mDetectMaxValue = mDetectMaxValue - mBackgroundValue;
        processDetectValue();
        mEdtRepeatValue.setText(mDetectMaxValue + "");

        isCompleted = true;
    }

    private void saveBackground() {
        processDetectValue();
        mBackgroundValue = mDetectMaxValue.floatValue();
        mTxtBackground.setText("背景值:" + mBackgroundValue);
        SPUtils.put(this, NormalTaskActivity.BACKGROUND_VALUE, mDetectMaxValue.floatValue());
    }

    private void processDetectValue() {
        if (mDetectMaxValue < 1) {
            mDetectMaxValue = 0.0;
        } else if (mDetectMaxValue > 30000) {
            mDetectMaxValue = 100000.0;
        }
        if (BuildConfig.DEBUG)
            mDetectMaxValue = 11.15;

        BigDecimal bigDecimal = new BigDecimal(mDetectMaxValue);
        mDetectMaxValue = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 跳转任务
     *
     * @param type 1:前一条   2:下一条
     */
    private void jumpToTask(int type) {
        isShowToast = false;
        int row = mExcelRow - 1;
        if (type == 1) {
            row -= 1;
        } else {
            row += 1;
        }
        int length = mGP.getRepeatTasks().size();
        if (row < 0) {
            T.showShort(this, "已经是第一条了");
        } else if (row > length - 1) {
            T.showShort(this, "已经是最后一条了");
        } else {
            RepeatTask targetTask = mGP.getRepeatTasks().get(row);
            String path = mTaskPath;
            Intent intent = createIntent(this, targetTask, row, path);
            startActivity(intent);
            finish();
        }
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
            if (detectMode == MODE_DETECT){
                dialogBuilder.setTitle("检测任务");
            }else {
                dialogBuilder.setTitle("测量背景值");
            }
            dialogBuilder.setCancelable(false);
            mDialogDetect = dialogBuilder.show();
        } else {
            if (detectMode == MODE_DETECT){
                mDialogDetect.setTitle("检测任务");
            }else {
                mDialogDetect.setTitle("测量背景值");
            }
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
                T.showShort(RepeatTaskActivity.this, "通信连接丢失请重新连接设备");
            else{
                if (isShowToast)
                    T.showShort(RepeatTaskActivity.this, "接收数据终止");
            }


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
