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
import com.yanxinwei.bluetoothspppro.model.RepeatTask;

import java.util.ArrayList;

/**
 * Created by yanxinwei on 16/3/17.
 */
public class TaskAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<NormalTask> mTasks = null;
    private ArrayList<RepeatTask> mRepeatTasks = null;
    private LayoutInflater mInflater;

    public TaskAdapter(Context context, ArrayList<NormalTask> tasks, ArrayList<RepeatTask> repeatTasks) {
        mContext = context;
        mTasks = tasks;
        mRepeatTasks = repeatTasks;
        mInflater = LayoutInflater.from(mContext);
    }

//    public TaskAdapter(Context context, ArrayList<RepeatTask> repeatTasks) {
//        mContext = context;
//        mRepeatTasks = repeatTasks;
//        mInflater = LayoutInflater.from(mContext);
//    }

    @Override
    public int getCount() {
        if (mTasks != null)
            return mTasks.size();
        else
            return mRepeatTasks.size();
    }

    @Override
    public Object getItem(int position) {
        if (mTasks != null)
            return mTasks.get(position);
        else
            return mRepeatTasks.get(position);
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
//        NormalTask normalTask = mTasks.get(position);
        holder.txtUnitSubType.setText(getParams(position, 1));
        holder.txtUnitType.setText(getParams(position, 2));
        holder.txtLabelNumber.setText(getParams(position, 3));
        if (getBoolParams(position, 1)){
            holder.imgState.setImageResource(R.drawable.ic_gray_tick_circle);
        }else {
            if (getBoolParams(position, 2)){
                holder.imgState.setImageResource(R.drawable.ic_red_tick_circle);
            }else {
                holder.imgState.setImageResource(R.drawable.ic_green_tick_circle);
            }
        }
        return convertView;
    }

    /**
     * @param position
     * @param type  1:unitSubType  2:unitType  3:labelNumber
     * @return
     */
    private String getParams(int position, int type){
        if (mTasks != null){
            NormalTask normalTask = mTasks.get(position);
            switch (type){
                case 1:
                    return normalTask.getUnitSubType();
                case 2:
                    return normalTask.getUnitType();
                case 3:
                    return normalTask.getLabelNumber();
            }
        }else {
            RepeatTask repeatTask = mRepeatTasks.get(position);
            switch (type){
                case 1:
                    return repeatTask.getUnitSubType();
                case 2:
                    return repeatTask.getUnitType();
                case 3:
                    return repeatTask.getLabelNumber();
            }
        }
        return "";
    }

    /**
     * @param position
     * @param type  1:idDetected  2:isLeak
     * @return
     */
    private boolean getBoolParams(int position, int type){
        if (mTasks != null){
            NormalTask normalTask = mTasks.get(position);
            switch (type){
                case 1:
                    return TextUtils.isEmpty(normalTask.getDetectDate());
                case 2:
                    return normalTask.getDetectValue() > normalTask.getLeakageThreshold();
            }
        }else {
            RepeatTask repeatTask = mRepeatTasks.get(position);
            switch (type){
                case 1:
                    return TextUtils.isEmpty(repeatTask.getRepeatDate());
                case 2:
                    return repeatTask.getRepeatValue() > repeatTask.getLeakageThreshold();
            }
        }
        return false;
    }

    static class ViewHolder{
        TextView txtLabelNumber;
        TextView txtUnitType;
        TextView txtUnitSubType;
        ImageView imgState;
    }
}
