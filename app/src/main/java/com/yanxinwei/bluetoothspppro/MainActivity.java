package com.yanxinwei.bluetoothspppro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.actMain;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.globalPool;
import com.yanxinwei.bluetoothspppro.activity.ControlEquipmentActivity;
import com.yanxinwei.bluetoothspppro.activity.ImportTaskActivity;
import com.yanxinwei.bluetoothspppro.activity.SettingActivity;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;
import com.yanxinwei.bluetoothspppro.util.SPUtils;

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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_connection:
                intent = new Intent(this, actMain.class);
                break;
            case R.id.btn_import_task:
                intent = new Intent(this, ImportTaskActivity.class);
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
