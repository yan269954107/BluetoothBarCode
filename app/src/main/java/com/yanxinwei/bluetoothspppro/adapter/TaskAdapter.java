package com.yanxinwei.bluetoothspppro.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.model.NormalTask;

import java.util.ArrayList;

/**
 * Created by yanxinwei on 16/3/17.
 */
public class TaskAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<NormalTask> mTasks;
    private LayoutInflater mInflater;

    public TaskAdapter(Context context, ArrayList<NormalTask> tasks) {
        mContext = context;
        mTasks = tasks;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public NormalTask getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_task_info, null);
            holder = new ViewHolder();
            holder.txtUnitSubType = (TextView) convertView.findViewById(R.id.txt_unit_sub_type);
            holder.txtUnitType = (TextView) convertView.findViewById(R.id.txt_unit_type);
            holder.txtLabelNumber = (TextView) convertView.findViewById(R.id.txt_label_number);
            holder.imgState = (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        NormalTask normalTask = mTasks.get(position);
        holder.txtUnitSubType.setText(normalTask.getUnitSubType());
        holder.txtUnitType.setText(normalTask.getUnitType());
        holder.txtLabelNumber.setText(normalTask.getLabelNumber());
        if (TextUtils.isEmpty(normalTask.getDetectDate())){
            holder.imgState.setImageResource(R.drawable.ic_gray_tick_circle);
        }else {
            if (normalTask.getDetectValue() > normalTask.getLeakageThreshold()){
                holder.imgState.setImageResource(R.drawable.ic_red_tick_circle);
            }else {
                holder.imgState.setImageResource(R.drawable.ic_green_tick_circle);
            }
        }
        return convertView;
    }

    static class ViewHolder{
        TextView txtLabelNumber;
        TextView txtUnitType;
        TextView txtUnitSubType;
        ImageView imgState;
    }
}
