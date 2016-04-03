package com.yanxinwei.bluetoothspppro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;
import com.yanxinwei.bluetoothspppro.util.F;
import com.yanxinwei.bluetoothspppro.util.L;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestTaskActivity extends BaseActivity {

    private static final int MENU_ID_IMPORT_TASK = 0x01;
    private static final int MENU_ID_IMPORT_REPEAT_TASK = 0x02;

    private int taskType = 1;
    private ArrayList<String> fileNames;
    private ArrayAdapter<String> mArrayAdapter;

    @Bind(R.id.list_task_file)
    ListView mTaskFiles;

    @Bind(R.id.empty_view)
    TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_task);

        ButterKnife.bind(this);

        mTaskFiles.setEmptyView(mEmptyView);
        mTaskFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = ImportTaskActivity.createIntent(TestTaskActivity.this,
                        fileNames.get(position), taskType);
                startActivity(intent);
            }
        });

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem miImportTask = menu.add(0, MENU_ID_IMPORT_TASK, 0, getString(R.string.menu_task));
        miImportTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //一直隐藏

        MenuItem miImportRepeatTask = menu.add(0, MENU_ID_IMPORT_REPEAT_TASK, 0,
                getString(R.string.menu_repeat_task));
        miImportRepeatTask.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //一直隐藏
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_ID_IMPORT_TASK:
                taskType = 1;
                loadData();
                return true;
            case MENU_ID_IMPORT_REPEAT_TASK:
                taskType = 2;
                loadData();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void loadData() {
        String dir;
        if (taskType == 1){
            dir = F.TEST_TASK_DIR;
        }else {
            dir = F.REPEAT_TASK_DIR;
        }
        fileNames = new ArrayList<>();
        File file = new File(dir);
        if (file.exists()){
            for (File f : file.listFiles()){
                L.d("@@@@"+f.getName());
                if (f.getName().endsWith(".xlsx"))
                    fileNames.add(f.getName());
            }
        }
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        mTaskFiles.setAdapter(mArrayAdapter);

    }


}
