package com.yanxinwei.bluetoothspppro.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.core.AppConstants;
import com.yanxinwei.bluetoothspppro.model.NormalTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NormalTaskActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String NORMAL_TASK = "normal_task";

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

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.txt_begin_test)
    TextView mBeginTest;

    private NormalTask task;

    public static Intent createIntent(Context context, NormalTask normalTask){
        Intent intent = new Intent(context, NormalTaskActivity.class);
        intent.putExtra(NORMAL_TASK, normalTask);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_task);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        bindValue();

        mBeginTest.setOnClickListener(this);
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
        mEdtLeakageThreshold.setText(task.getLeakageThreshold()+"");
        mEdtMinTime.setText(task.getDetectMiniTime()+"");
        mEdtDetectDate.setText(task.getDetectDate());
        mEdtDetectDevice.setText(task.getDetectDevice());
        mEdtDetectValue.setText(task.getDetectValue()+"");
        if (task.getIsLeakage() == 0){
            mEdtIsLeakage.setText("否");
        }else {
            mEdtIsLeakage.setText("是");
        }
        mEdtLeakagePosition.setText(task.getLeakagePosition());
        mEdtLeakagePosition.setOnClickListener(this);
        mEdtRemarks.setText(task.getRemarks());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_begin_test:

                break;
            case R.id.txt_save_result:

                break;
            case R.id.edt_leakage_position:
                showDialogList();
                break;
        }
    }

    private void showDialogList(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog_list_view, null);
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
                if (dialog != null){
                    String p = AppConstants.leakagePosition[position];
                    mEdtLeakagePosition.setText(p);
                    dialog.dismiss();
                }
            }
        });
    }
}
