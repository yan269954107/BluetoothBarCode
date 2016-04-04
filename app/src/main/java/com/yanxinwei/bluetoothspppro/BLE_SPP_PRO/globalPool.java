package com.yanxinwei.bluetoothspppro.BLE_SPP_PRO;

import android.support.multidex.MultiDexApplication;

import com.yanxinwei.bluetoothspppro.R;
import com.yanxinwei.bluetoothspppro.bluetooth.BluetoothSppClient;
import com.yanxinwei.bluetoothspppro.model.NormalTask;
import com.yanxinwei.bluetoothspppro.model.RepeatTask;
import com.yanxinwei.bluetoothspppro.storage.CJsonStorage;
import com.yanxinwei.bluetoothspppro.storage.CKVStorage;

import java.util.ArrayList;

public class globalPool extends MultiDexApplication
{
	/**蓝牙SPP通信连接对象*/
	public BluetoothSppClient mBSC = null;
	/**动态公共存储对象*/
	public CKVStorage mDS = null;

	private ArrayList<NormalTask> mNormalTasks = null;
	private ArrayList<RepeatTask> mRepeatTasks = null;
	/**
	 * 覆盖构造
	 * */
	@Override
	public void onCreate(){
		super.onCreate();
		this.mDS = new CJsonStorage(this, getString(R.string.app_name));
	}
	
	/**
	 * 建立蓝牙连接
	 * @param sMac 蓝牙硬件地址
	 * @return boolean
	 * */
	public boolean createConn(String sMac){
		if (null == this.mBSC)
		{
			this.mBSC = new BluetoothSppClient(sMac);
			if (this.mBSC.createConn())
				return true;
			else{
				this.mBSC = null;
				return false;
			}
		}
		else
			return true;
	}
	
	/**
	 * 关闭并释放连接
	 * @return void
	 * */
	public void closeConn(){
		if (null != this.mBSC){
			this.mBSC.closeConn();
			this.mBSC = null;
		}
	}

	public ArrayList<NormalTask> getNormalTasks() {
		return mNormalTasks;
	}

	public void setNormalTasks(ArrayList<NormalTask> normalTasks) {
		mNormalTasks = normalTasks;
	}

	public ArrayList<RepeatTask> getRepeatTasks() {
		return mRepeatTasks;
	}

	public void setRepeatTasks(ArrayList<RepeatTask> repeatTasks) {
		mRepeatTasks = repeatTasks;
	}
}
