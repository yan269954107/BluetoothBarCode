package com.yanxinwei.bluetoothspppro.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.util.SPUtils;
import com.yanxinwei.bluetoothspppro.util.T;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @Bind(R.id.edt_detect_personal)
    EditText mEdtDetectPersonal;

    @Bind(R.id.edt_detect_device)
    EditText mEdtDetectDevice;

    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetectInfo();
            }
        });

        showDetectInfo();
    }

    private void showDetectInfo(){
        String device = (String) SPUtils.get(this, SPUtils.SP_DETECT_DEVICE, "");
        String personal = (String) SPUtils.get(this, SPUtils.SP_DETECT_PERSONAL, "");
        if (!"".equals(device)){
            mEdtDetectDevice.setText(device);
        }
        if (!"".equals(personal)){
            mEdtDetectPersonal.setText(personal);
        }
    }

    private void saveDetectInfo(){
        String detectPersonal = mEdtDetectPersonal.getText().toString().trim();
        String detectDevice = mEdtDetectDevice.getText().toString().trim();
        if (TextUtils.isEmpty(detectPersonal) || TextUtils.isEmpty(detectDevice)){
            T.showShort(this, "检测人员或检测设备不能为空");
            return;
        }
        SPUtils.put(this, SPUtils.SP_DETECT_PERSONAL, detectPersonal);
        SPUtils.put(this, SPUtils.SP_DETECT_DEVICE, detectDevice);
        setResult(RESULT_OK);
        finish();
    }
}
