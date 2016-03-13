package com.yanxinwei.bluetoothspppro.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.BaseCommActivity;
import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.util.T;

public class ControlEquipmentActivity extends BaseCommActivity implements View.OnClickListener{

    private static final String IGNITE_FID = "ignite";
    private static final String PUMP_ON = "pump on";
    private static final String PUMP_OFF = "pump off";

    /**
     * 当前使用的结束符
     */
    private String msEndFlg = msEND_FLGS[0];

    /**
     * 发送数据视图
     */
    private TextView mtvSendView = null;
    /**
     * 接收数据视图
     */
    private TextView mtvRecView = null;
    /**
     * 发送区标题对象
     */
    private TextView mtvRecAreaTitle = null;
    /**
     * 发送视图滚屏
     */
    private ScrollView msvSendView = null;
    /**
     * 接收视图滚屏
     */
    private ScrollView msvRecView = null;
    /**
     * 接收区域控制区
     */
    private RelativeLayout mrlSendArea = null;

    /**
     * 当前是否隐藏发送区
     */
    private boolean mbHideSendArea = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_equipment);

        this.mtvSendView = (TextView) this.findViewById(R.id.actKeyBoard_tv_send_data_show);
        this.mtvRecView = (TextView) this.findViewById(R.id.actKeyBoard_tv_receive_show);
        this.mtvRecAreaTitle = (TextView) this.findViewById(R.id.actKeyBoard_tv_receive_area_title);
        this.msvSendView = (ScrollView) this.findViewById(R.id.actKeyBoard_sv_send_data_scroll);
        this.msvRecView = (ScrollView) this.findViewById(R.id.actKeyBoard_sv_receive_data_scroll);
        this.mrlSendArea = (RelativeLayout) this.findViewById(R.id.actKeyBoard_rl_send_area);
        this.mtvRecView.setText(""); //初始化接收区内容
        this.mtvRecAreaTitle.append("\t\t(");//设置接收区标题
        this.mtvRecAreaTitle.append(getString(R.string.tips_click_to_hide));//设置接收区标题
        this.mtvRecAreaTitle.append(":" + getString(R.string.tv_send_area_title));//设置接收区标题
        this.mtvRecAreaTitle.append(")");//设置发送区标题
        this.mtvRecAreaTitle.setOnClickListener(new TextView.OnClickListener() { //处理发送区的显示与隐藏
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.actKeyBoard_tv_receive_area_title) {
                    String sTitle = getString(R.string.tv_receive_area_title);
                    TextView tv = ((TextView) v);
                    if (mbHideSendArea) {
                        sTitle += "\t\t(" + getString(R.string.tips_click_to_hide);
                        sTitle += ":" + getString(R.string.tv_send_area_title) + ")";
                        tv.setText(sTitle);
                        mrlSendArea.setVisibility(View.VISIBLE);
                    } else {
                        sTitle += "\t\t(" + getString(R.string.tips_click_to_show);
                        sTitle += ":" + getString(R.string.tv_send_area_title) + ")";
                        tv.setText(sTitle);
                        mrlSendArea.setVisibility(View.GONE);
                    }
                    mrlSendArea.refreshDrawableState(); //刷新发送区
                    mbHideSendArea = !mbHideSendArea;
                }
            }
        });

		/*默认启动时隐藏发送区*/
        String sTitle = getString(R.string.tv_receive_area_title);
        sTitle += "\t\t(" + getString(R.string.tips_click_to_show);
        sTitle += ":" + getString(R.string.tv_send_area_title) + ")";
        this.mtvRecAreaTitle.setText(sTitle);
        this.mrlSendArea.setVisibility(View.GONE);
        this.mrlSendArea.refreshDrawableState(); //刷新发送区
        this.mbHideSendArea = true; //隐藏发送区

        findViewById(R.id.btn_ignite_fid).setOnClickListener(this);
        findViewById(R.id.btn_pump_on).setOnClickListener(this);
        findViewById(R.id.btn_pump_off).setOnClickListener(this);
        findViewById(R.id.btn_reboot).setOnClickListener(this);

        try {
            this.enabledBack(); //激活回退按钮
            this.initIO_Mode(); //初始化输入输出模式
            this.usedDataCount(); //启用数据统计状态条
            this.setEndFlg(); //载入终止符

            //初始化结束，启动接收线程
            new receiveTask()
                    .executeOnExecutor(FULL_TASK_EXECUTOR);
        }catch (NullPointerException e){
            T.showShort(this, "请先连接设备");
            finish();
        }

    }

    /**
     * 析构
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBSC)
            mBSC.killReceiveData_StopFlg(); //强制终止接收函数
    }

    /**
     * 屏幕旋转时的处理
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * add top menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //清屏
        MenuItem miClear = menu.add(0, MEMU_CLEAR, 0, getString(R.string.menu_clear));
        miClear.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.mBSC.killReceiveData_StopFlg(); //强制终止接收函数
                this.mbThreadStop = true; //终止接收线程
                this.setResult(Activity.RESULT_CANCELED); //返回到主界面
                this.finish();
                return true;
            case MEMU_CLEAR: //清除屏幕
                this.mtvSendView.setText("");
                this.mtvRecView.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * 按键监听处理
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {    //按回退键的处理
            this.mbThreadStop = true; //终止接收线程
            this.setResult(Activity.RESULT_CANCELED, null);
            this.finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    /**
     * 载入终止符配置信息
     * String sModelName 模块名称
     *
     * @return void
     */
    private void setEndFlg() {
        this.mBSC.setReceiveStopFlg(this.msEndFlg); //设置结束符
    }

    /**
     * 数据的发送处理
     *
     * @return void
     */
    private void Send(String sData) {
        if (!sData.equals("")) {
            String sSend = sData;
            int iRet = 0;
            if (!this.msEndFlg.isEmpty()) //加入结束符的处理
                iRet = this.mBSC.Send(sSend.concat(this.msEndFlg));
            else
                iRet = this.mBSC.Send(sSend);

            if (iRet >= 0) { //检查通信状态
                if (View.VISIBLE == this.mrlSendArea.getVisibility()) { //发送区显示时才输出到TextView
                    if (iRet == 0)
                        this.mtvSendView.append(sSend.concat("(fail) "));
                    else
                        this.mtvSendView.append(sSend.concat("(succeed) "));
                }
            } else {    //链接丢失
                Toast.makeText(ControlEquipmentActivity.this, //提示 连接丢失
                        getString(R.string.msg_msg_bt_connect_lost),
                        Toast.LENGTH_LONG).show();
                this.mtvRecView.append(this.getString(R.string.msg_msg_bt_connect_lost) + "\n");
            }
            this.refreshTxdCount();//刷新发送值
            this.autoScroll(); //滚屏处理
        }
    }

    /**
     * 自动滚屏的处理
     *
     * @return void
     */
    private void autoScroll() {
        int iOffset = 0;
        //自动滚屏处理
        iOffset = this.mtvRecView.getMeasuredHeight() - this.msvRecView.getHeight();
        if (iOffset > 0)
            this.msvRecView.scrollTo(0, iOffset);

        if (this.mrlSendArea.getVisibility() == View.VISIBLE) {    //当发送区显示的时候，才组刷新处理
            iOffset = this.mtvSendView.getMeasuredHeight() - this.msvSendView.getHeight();
            if (iOffset > 0)
                this.msvSendView.scrollTo(0, iOffset);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ignite_fid:
                Send(IGNITE_FID);
                break;
            case R.id.btn_pump_on:
                Send(PUMP_ON);
                break;
            case R.id.btn_pump_off:
                Send(PUMP_OFF);
                break;
            case R.id.btn_reboot:
                T.showShort(this, "待确定");
                break;
        }
    }

    //----------------
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
            mtvRecView.append(getString(R.string.msg_receive_data_wating));
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
            mtvRecView.append(progress[0]); //显示区中追加数据
            autoScroll(); //自动卷屏处理
            refreshRxdCount(); //刷新接收数据统计值
        }

        /**
         * 阻塞任务执行完后的清理工作
         */
        @Override
        public void onPostExecute(Integer result) {
            if (CONNECT_LOST == result) //通信连接丢失
                mtvRecView.append(getString(R.string.msg_msg_bt_connect_lost));
            else
                mtvRecView.append(getString(R.string.msg_receive_data_stop));//提示接收终止
            refreshHoldTime(); //刷新数据统计状态条-运行时间
        }
    }
}