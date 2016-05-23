package com.yanxinwei.bluetoothspppro.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.BaseCommActivity;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.actMain;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.globalPool;
import com.yanxinwei.bluetoothspppro.BuildConfig;
import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.bluetooth.BluetoothSppClient;
import com.yanxinwei.bluetoothspppro.util.SPUtils;
import com.yanxinwei.bluetoothspppro.util.T;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackgroundActivity extends AppCompatActivity implements OnClickListener{

    private static final String BACK_VALUE_1 = "backValue1";
    private static final String BACK_VALUE_2 = "backValue2";
    private static final String BACK_VALUE_3 = "backValue3";
    private static final String BACK_VALUE_4 = "backValue4";
    private static final String BACK_VALUE_5 = "backValue5";

    public static final String BACK_AVERAGE_VALUE_RESULT = "backAverageValueResult";

    /**
     * 对象:引用全局的蓝牙连接对象
     */
    protected BluetoothSppClient mBSC = null;
    protected globalPool mGP = null;
    private Double mDetectMaxValue = -99999.0;

    //是否正在检测数据
    private boolean isDetecting = false;

    //蓝牙接收数据相关参数
    private boolean mbThreadStop = false;

    @Bind(R.id.txt_background_1)
    TextView txtBack1;

    @Bind(R.id.txt_background_2)
    TextView txtBack2;

    @Bind(R.id.txt_background_3)
    TextView txtBack3;

    @Bind(R.id.txt_background_4)
    TextView txtBack4;

    @Bind(R.id.txt_background_5)
    TextView txtBack5;

    @Bind(R.id.txt_background_average)
    TextView txtAverage;

    private float backValue1 = 0.0f;
    private float backValue2 = 0.0f;
    private float backValue3 = 0.0f;
    private float backValue4 = 0.0f;
    private float backValue5 = 0.0f;
    private float averageValue = 0.0f;

    private Dialog mDialogDetect;
    private View mDialogViewDetect;

    private LayoutInflater mInflater;
    private Button mBtnComplete;
    private TextView mTxtDetectValue;
    private TextView mTxtDetectMaxValue;
    private View mLlRemainder;

    private String mCurrentKey = BACK_VALUE_1;

    private receiveTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        ButterKnife.bind(this);

        this.mBSC = ((globalPool) this.getApplicationContext()).mBSC;
        mGP = (globalPool) this.getApplicationContext();

        mInflater = getLayoutInflater();

        bindBackValue();

        initView();
    }

    private void bindBackValue() {

        averageValue =  (float) SPUtils.get(this, NormalTaskActivity.BACKGROUND_VALUE, 0f);
        backValue1 = (float) SPUtils.get(this, BACK_VALUE_1, 0f);
        backValue2 = (float) SPUtils.get(this, BACK_VALUE_2, 0f);
        backValue3 = (float) SPUtils.get(this, BACK_VALUE_3, 0f);
        backValue4 = (float) SPUtils.get(this, BACK_VALUE_4, 0f);
        backValue5 = (float) SPUtils.get(this, BACK_VALUE_5, 0f);

    }

    private void initView() {
        txtBack1.setText(backValue1+"");
        txtBack2.setText(backValue2+"");
        txtBack3.setText(backValue3+"");
        txtBack4.setText(backValue4+"");
        txtBack5.setText(backValue5+"");
        txtAverage.setText(averageValue+"");
    }

    private void test() {
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
        if (mTask == null) {
            mTask = new receiveTask();
            mTask.executeOnExecutor(BaseCommActivity.FULL_TASK_EXECUTOR);
        }
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
            mLlRemainder = mDialogViewDetect.findViewById(R.id.ll_remainder);
            mLlRemainder.setVisibility(View.GONE);

            dialogBuilder.setView(mDialogViewDetect);
            dialogBuilder.setTitle("测量背景值");
            dialogBuilder.setCancelable(false);
            mDialogDetect = dialogBuilder.show();
        } else {
            mDialogDetect.show();
        }
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
        if (KeyEvent.KEYCODE_BACK == keyCode) {    //按回退键的处理
            this.mbThreadStop = true; //终止接收线程
            Intent data = new Intent();
            data.putExtra(BACK_AVERAGE_VALUE_RESULT, averageValue);
            this.setResult(Activity.RESULT_OK, data);
            this.finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                saveBackValue();
                break;
        }
    }

    private void saveBackValue() {

        isDetecting = false;
        mDialogDetect.dismiss();

        processDetectValue();
        setValue();
        SPUtils.put(this, mCurrentKey, mDetectMaxValue.floatValue());
        countAverage();
//        bindBackValue();
        initView();
    }

    private void setValue() {
        switch (mCurrentKey) {
            case BACK_VALUE_1:
                backValue1 = mDetectMaxValue.floatValue();
                break;
            case BACK_VALUE_2:
                backValue2 = mDetectMaxValue.floatValue();
                break;
            case BACK_VALUE_3:
                backValue3 = mDetectMaxValue.floatValue();
                break;
            case BACK_VALUE_4:
                backValue4 = mDetectMaxValue.floatValue();
                break;
            case BACK_VALUE_5:
                backValue5 = mDetectMaxValue.floatValue();
                break;
        }
    }

    private void countAverage() {
        averageValue = (backValue1 + backValue2 + backValue3 + backValue4 + backValue5) * 0.2f;
        BigDecimal bigDecimal = new BigDecimal(averageValue);
        Double doubleValue = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        averageValue = doubleValue.floatValue();
        SPUtils.put(this, NormalTaskActivity.BACKGROUND_VALUE, averageValue);
    }

    private void processDetectValue() {
        if (mDetectMaxValue < 1) {
            mDetectMaxValue = 0.0;
        } else if (mDetectMaxValue > 30000) {
            mDetectMaxValue = 100000.0;
        }
        if (BuildConfig.DEBUG)
            mDetectMaxValue = 11.11;

        BigDecimal bigDecimal = new BigDecimal(mDetectMaxValue);
        mDetectMaxValue = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
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
//                    mTxtDetectValue.setText(value + "");
//                    if (value > mDetectMaxValue) {
                        mDetectMaxValue = value;
                        mTxtDetectMaxValue.setText(mDetectMaxValue + "");
//                    }
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
                T.showShort(BackgroundActivity.this, "通信连接丢失请重新连接设备");
            else {
//                if (isShowToast)
//                    T.showShort(BackgroundActivity.this, "接收数据终止");
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

    @OnClick(R.id.btn_test_1)
    void test1(){
        mCurrentKey = BACK_VALUE_1;
        test();
    }

    @OnClick(R.id.btn_test_2)
    void test2(){
        mCurrentKey = BACK_VALUE_2;
        test();
    }

    @OnClick(R.id.btn_test_3)
    void test3(){
        mCurrentKey = BACK_VALUE_3;
        test();
    }

    @OnClick(R.id.btn_test_4)
    void test4(){
        mCurrentKey = BACK_VALUE_4;
        test();
    }

    @OnClick(R.id.btn_test_5)
    void test5(){
        mCurrentKey = BACK_VALUE_5;
        test();
    }
}
