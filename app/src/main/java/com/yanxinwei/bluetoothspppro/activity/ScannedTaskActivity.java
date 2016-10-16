package com.yanxinwei.bluetoothspppro.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.adapter.TaskAdapter;
import com.yanxinwei.bluetoothspppro.event.TaskCompleteEvent;
import com.yanxinwei.bluetoothspppro.model.NormalTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.yanxinwei.bluetoothspppro.activity.ImportTaskActivity.TASK_PATH;

public class ScannedTaskActivity extends AppCompatActivity {

    public static ArrayList<NormalTask> sNormalTasks;
    public static final String FIRST_INDEX = "firstIndex";

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

    private TaskAdapter mAdapter;

    private String taskPath;
    private int firstIndex;

    public static Intent createIntent(Context context, String path, int firstIndex) {
        Intent intent = new Intent(context, ScannedTaskActivity.class);
        intent.putExtra(TASK_PATH, path);
        intent.putExtra(FIRST_INDEX, firstIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_task);

        if (sNormalTasks == null) {
            finish();
            return;
        }

        ButterKnife.bind(this);

        taskPath = getIntent().getStringExtra(TASK_PATH);
        firstIndex = getIntent().getIntExtra(FIRST_INDEX, 0);

        mAdapter = new TaskAdapter(this, sNormalTasks, null);
        mTaskList.setEmptyView(mEmptyView);
        mTaskList.setAdapter(mAdapter);
        mTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int totalIndex = position + firstIndex;  //在文件中总的index
                Intent intent = NormalTaskActivity.createIntent(ScannedTaskActivity.this,
                        (NormalTask) mAdapter.getItem(position), totalIndex, taskPath, position);
                startActivity(intent);
            }
        });

        showCount();

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onCompleteEvent(TaskCompleteEvent event) {
        mAdapter.notifyDataSetChanged();
        showCount();
    }

    private void showCount() {
        mLinearLayout.setVisibility(View.VISIBLE);
        int taskTested = 0, taskLeakage = 0;

        int taskNum = sNormalTasks.size();
        for (NormalTask task : sNormalTasks) {
            if (!TextUtils.isEmpty(task.getDetectDate())) {
                taskTested++;
                if (task.getDetectValue() > task.getLeakageThreshold()) {
                    taskLeakage++;
                }
            }
        }

        txtTaskNum.setText("任务点数:" + taskNum);
        txtTaskTested.setText("已测点数:" + taskTested);
        txtTaskLeakage.setText("泄露点数:" + taskLeakage);
    }
}
