package com.yanxinwei.bluetoothspppro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.actMain;
import com.yanxinwei.bluetoothspppro.BLE_SPP_PRO.globalPool;
import com.yanxinwei.bluetoothspppro.activity.ControlEquipmentActivity;
import com.yanxinwei.bluetoothspppro.activity.ImportTaskActivity;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    @Bind(R.id.btn_connection)
    ImageButton btnConnection;

    @Bind(R.id.btn_import_task)
    ImageButton btnImportTask;

    @Bind(R.id.btn_testing)
    ImageButton btnTesting;

    @Bind(R.id.btn_control_equipment)
    ImageButton btnControlEquipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        btnConnection.setOnClickListener(this);
        btnImportTask.setOnClickListener(this);
        btnTesting.setOnClickListener(this);
        btnControlEquipment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
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

        ((globalPool)this.getApplicationContext()).closeConn();//关闭连接

        //检查如果进入前蓝牙是关闭的状态，则退出时关闭蓝牙
//        if (null != mBT && !this.mbBleStatusBefore)
//            mBT.disable();
    }
}
