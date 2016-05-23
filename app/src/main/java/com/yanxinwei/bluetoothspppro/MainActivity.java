package com.yanxinwei.bluetoothspppro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.actMain;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.globalPool;
import com.yanxinwei.bluetoothspppro.activity.ControlEquipmentActivity;
import com.yanxinwei.bluetoothspppro.activity.SettingActivity;
import com.yanxinwei.bluetoothspppro.activity.TestTaskActivity;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;
import com.yanxinwei.bluetoothspppro.util.F;
import com.yanxinwei.bluetoothspppro.util.SDLog;
import com.yanxinwei.bluetoothspppro.util.SPUtils;
import com.yanxinwei.bluetoothspppro.util.T;
import com.yanxinwei.bluetoothspppro.util.Test;
import com.yanxinwei.bluetoothspppro.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.btn_connection)
    ImageButton btnConnection;

    @Bind(R.id.btn_import_task)
    ImageButton btnImportTask;

    @Bind(R.id.btn_testing)
    ImageButton btnTesting;

    @Bind(R.id.btn_control_equipment)
    ImageButton btnControlEquipment;

    @Bind(R.id.drawer)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    TextView txtDetectDevice;
    TextView txtDetectPersonal;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();

        switch(0)
        {
            case 1:
                JSONObject jsoObj;
                String date=null;
                String second=null;
                try
                {
                    jsoObj=new JSONObject();
                    date=jsoObj.getString("date");
                    second=jsoObj.getString("second");
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                Test.settime(date,second);
                break;
        }
        if (!BuildConfig.DEBUG && !String.valueOf(Test.P).equals(Util.getInfo(this))){
//            L.d("@@@@"+String.valueOf(Test.P)+"@");
//            L.d("@@@@"+Util.getInfo(this));
//            L.d("@@@@finish0");
            finish();
        }

        boolean isCopy = (boolean) SPUtils.get(this, "IS_COPY", false);
        if (isCopy){
            String c = F.readerSign(this);
//            L.d("@@@@"+c);
            if (c == null || !c.equals("081efe81f3bee0d92457ae1f04f662ea")){
                checkSDCard();
            }
        }else {
            checkSDCard();
        }
        F.createFolder();
    }



    private void checkSDCard(){
        String p = SDLog.SD_PATH.concat("/").concat("sign.csr");
        String c = F.readerSign(p);
//        L.d("@@@@"+c);
        if (c == null || !c.equals("081efe81f3bee0d92457ae1f04f662ea")){
//            L.d("@@@@finish1");
            finish();
        } else {
            F.copyFile(this, p);
            SPUtils.put(this, "IS_COPY", true);
        }
    }

    private void initView() {
        btnConnection.setOnClickListener(this);
        btnImportTask.setOnClickListener(this);
        btnTesting.setOnClickListener(this);
        btnControlEquipment.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        mDrawerLayout.addDrawerListener(drawerToggle);

        View headerView = mNavigationView.getHeaderView(0);
        txtDetectDevice = (TextView) headerView.findViewById(R.id.txt_detect_device);
        txtDetectPersonal = (TextView) headerView.findViewById(R.id.txt_detect_personal);
        showDetectInfo();

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()){
                            case R.id.nav_setting:
                                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void showDetectInfo(){
        String device = (String) SPUtils.get(this, SPUtils.SP_DETECT_DEVICE, "");
        if (device.equals("")) {
            txtDetectDevice.setText("检测设备:未设置");
        } else {
            txtDetectDevice.setText("检测设备:" + device);
        }
        String personal = (String) SPUtils.get(this, SPUtils.SP_DETECT_PERSONAL, "");
        if (personal.equals("")) {
            txtDetectPersonal.setText("检测人员:未设置");
        } else {
            txtDetectPersonal.setText("检测人员:" + personal);
        }
    }

    private boolean checkSettingUser(){
        String device = (String) SPUtils.get(this, SPUtils.SP_DETECT_DEVICE, "");
        String personal = (String) SPUtils.get(this, SPUtils.SP_DETECT_PERSONAL, "");
        if (TextUtils.isEmpty(device) || TextUtils.isEmpty(personal)){
//            mNavigationView.
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (!checkSettingUser()){
            T.showShort(this, "请先设置检测信息");
            return;
        }
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_connection:
                intent = new Intent(this, actMain.class);
                break;
            case R.id.btn_import_task:
                intent = new Intent(this, TestTaskActivity.class);
                break;
            case R.id.btn_testing:

                break;
            case R.id.btn_control_equipment:
                intent = new Intent(this, ControlEquipmentActivity.class);
                break;
        }
        if (null != intent)
            startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ((globalPool) this.getApplicationContext()).closeConn();//关闭连接

        //检查如果进入前蓝牙是关闭的状态，则退出时关闭蓝牙
//        if (null != mBT && !this.mbBleStatusBefore)
//            mBT.disable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            showDetectInfo();
        }

    }
}
