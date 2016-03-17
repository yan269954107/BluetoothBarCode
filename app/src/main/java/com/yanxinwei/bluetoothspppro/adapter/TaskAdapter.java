package com.yanxinwei.bluetoothspppro.adapter;

import android.content.Context;
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
    public Object getItem(int position) {
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
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txt_address);
            holder.txtUnitType = (TextView) convertView.findViewById(R.id.txt_unit_type);
            holder.txtLabelNumber = (TextView) convertView.findViewById(R.id.txt_label_number);
            holder.imgState = (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        NormalTask normalTask = mTasks.get(position);
        holder.txtAddress.setText(normalTask.getAddress());
        holder.txtUnitType.setText(normalTask.getUnitType());
        holder.txtLabelNumber.setText(normalTask.getLabelNumber());
        return convertView;
    }

    static class ViewHolder{
        TextView txtAddress;
        TextView txtUnitType;
        TextView txtLabelNumber;
        ImageView imgState;
    }
}
