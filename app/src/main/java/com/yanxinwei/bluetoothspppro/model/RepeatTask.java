package com.yanxinwei.bluetoothspppro.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 * Created by yanxinwei on 16/3/19.
 */
public class RepeatTask implements Serializable{

    private double detectId;
    private String detectedEquipment;
    private String area;
    private String detectedDevice;
    private String labelNumber;
    private String address;
    private String unitType;
    private String unitSubType;
    private double leakageThreshold;
    private double detectMiniTime;
    private String detectDate;
    private double detectValue;
    private String maintainDate;
    private String maintainPersonnel;
    private String maintainMeasure;
    private String repeatDate;
    private String repeatDevice;
    private double repeatValue;
    private String remarks;

    public RepeatTask() {
    }

    @SuppressLint("UseSparseArrays")
    public static final HashMap<Integer,MethodParams> convertMap = new HashMap<Integer,MethodParams>(){
        {
            put(0,  new MethodParams("setDetectId",double.class));
            put(1,  new MethodParams("setDetectedEquipment",String.class));
            put(2,  new MethodParams("setArea",String.class));
            put(3,  new MethodParams("setDetectedDevice",String.class));
            put(4,  new MethodParams("setLabelNumber",String.class));
            put(5,  new MethodParams("setAddress",String.class));
            put(6,  new MethodParams("setUnitType",String.class));
            put(7,  new MethodParams("setUnitSubType",String.class));
            put(8,  new MethodParams("setLeakageThreshold",double.class));
            put(9,  new MethodParams("setDetectMiniTime",double.class));
            put(10, new MethodParams("setDetectDate",String.class));
            put(11, new MethodParams("setDetectValue",double.class));
            put(12, new MethodParams("setMaintainDate",String.class));
            put(13, new MethodParams("setMaintainPersonnel",String.class));
            put(14, new MethodParams("setMaintainMeasure",String.class));
            put(15, new MethodParams("setRepeatDate",String.class));
            put(16, new MethodParams("setRepeatDevice",String.class));
            put(17, new MethodParams("setRepeatValue",double.class));
            put(18, new MethodParams("setRemarks",String.class));

        }
    };

    public static final void convertField(RepeatTask repeatTask, int cellIndex, Object args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Class cls = RepeatTask.class;
            MethodParams methodParams = convertMap.get(cellIndex);
            Method method = cls.getMethod(methodParams.getMethodName(),methodParams.getParamsType());
//        L.d("@@@@cellIndex:"+cellIndex+"   methodName:"+methodParams.getMethodName()
//                +"    paramsType:"+methodParams.getParamsType()+"    args:"+args);
            method.invoke(repeatTask, args);
        }catch (Exception e) {
            try {
                Class cls = NormalTask.class;
                MethodParams methodParams = convertMap.get(cellIndex);
                Method method = cls.getMethod(methodParams.getMethodName(),methodParams.getParamsType());
//            L.d("@@@@cellIndex:"+cellIndex+"   methodName:"+methodParams.getMethodName()
//                    +"    paramsType:"+methodParams.getParamsType()+"    args:"+args);
                double d = 0.0;
                method.invoke(repeatTask, d);
            }catch (Exception e1){

            }
        }

    }

    public double getDetectId() {
        return detectId;
    }

    public void setDetectId(double detectId) {
        this.detectId = detectId;
    }

    public String getDetectedEquipment() {
        return detectedEquipment;
    }

    public void setDetectedEquipment(String detectedEquipment) {
        this.detectedEquipment = detectedEquipment;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetectedDevice() {
        return detectedDevice;
    }

    public void setDetectedDevice(String detectedDevice) {
        this.detectedDevice = detectedDevice;
    }

    public String getLabelNumber() {
        return labelNumber;
    }

    public void setLabelNumber(String labelNumber) {
        this.labelNumber = labelNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitSubType() {
        return unitSubType;
    }

    public void setUnitSubType(String unitSubType) {
        this.unitSubType = unitSubType;
    }

    public double getLeakageThreshold() {
        return leakageThreshold;
    }

    public void setLeakageThreshold(double leakageThreshold) {
        this.leakageThreshold = leakageThreshold;
    }

    public double getDetectMiniTime() {
        return detectMiniTime;
    }

    public void setDetectMiniTime(double detectMiniTime) {
        this.detectMiniTime = detectMiniTime;
    }

    public String getDetectDate() {
        return detectDate;
    }

    public void setDetectDate(String detectDate) {
        this.detectDate = detectDate;
    }

    public double getDetectValue() {
        return detectValue;
    }

    public void setDetectValue(double detectValue) {
        this.detectValue = detectValue;
    }

    public String getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(String maintainDate) {
        this.maintainDate = maintainDate;
    }

    public String getMaintainPersonnel() {
        return maintainPersonnel;
    }

    public void setMaintainPersonnel(String maintainPersonnel) {
        this.maintainPersonnel = maintainPersonnel;
    }

    public String getMaintainMeasure() {
        return maintainMeasure;
    }

    public void setMaintainMeasure(String maintainMeasure) {
        this.maintainMeasure = maintainMeasure;
    }

    public String getRepeatDate() {
        return repeatDate;
    }

    public void setRepeatDate(String repeatDate) {
        this.repeatDate = repeatDate;
    }

    public String getRepeatDevice() {
        return repeatDevice;
    }

    public void setRepeatDevice(String repeatDevice) {
        this.repeatDevice = repeatDevice;
    }

    public double getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(double repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
