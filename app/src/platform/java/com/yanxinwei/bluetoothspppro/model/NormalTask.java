package com.yanxinwei.bluetoothspppro.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by yanxinwei on 16/3/16.
 */
public class NormalTask implements Serializable {

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
    private String detectDevice;
    private double detectValue;
    private double isLeakage;
    private String leakagePosition;
    private String remarks;

    public NormalTask(String detectedEquipment, String area, String detectedDevice, String labelNumber,
                      String address, String unitType, String unitSubType, double leakageThreshold,
                      double detectMiniTime, String detectDate, String detectDevice, double detectValue,
                      double isLeakage, String leakagePosition, String remarks) {
        this.detectedEquipment = detectedEquipment;
        this.area = area;
        this.detectedDevice = detectedDevice;
        this.labelNumber = labelNumber;
        this.address = address;
        this.unitType = unitType;
        this.unitSubType = unitSubType;
        this.leakageThreshold = leakageThreshold;
        this.detectMiniTime = detectMiniTime;
        this.detectDate = detectDate;
        this.detectDevice = detectDevice;
        this.detectValue = detectValue;
        this.isLeakage = isLeakage;
        this.leakagePosition = leakagePosition;
        this.remarks = remarks;
    }

    public NormalTask() {
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

    public String getDetectDevice() {
        return detectDevice;
    }

    public void setDetectDevice(String detectDevice) {
        this.detectDevice = detectDevice;
    }

    public double getDetectValue() {
        return detectValue;
    }

    public void setDetectValue(double detectValue) {
        this.detectValue = detectValue;
    }

    public double getIsLeakage() {
        return isLeakage;
    }

    public void setIsLeakage(double isLeakage) {
        this.isLeakage = isLeakage;
    }

    public String getLeakagePosition() {
        return leakagePosition;
    }

    public void setLeakagePosition(String leakagePosition) {
        this.leakagePosition = leakagePosition;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @SuppressLint("UseSparseArrays")
    public static final HashMap<Integer, MethodParams> convertMap = new HashMap<Integer, MethodParams>() {
        {
            put(0, new MethodParams("setDetectedEquipment", String.class));
            put(1, new MethodParams("setArea", String.class));
            put(2, new MethodParams("setDetectedDevice", String.class));
            put(3, new MethodParams("setLabelNumber", String.class));
            put(4, new MethodParams("setAddress", String.class));
            put(5, new MethodParams("setUnitType", String.class));
            put(6, new MethodParams("setUnitSubType", String.class));
            put(7, new MethodParams("setLeakageThreshold", double.class));
            put(8, new MethodParams("setDetectMiniTime", double.class));
            put(9, new MethodParams("setDetectDate", String.class));
            put(10, new MethodParams("setDetectDevice", String.class));
            put(11, new MethodParams("setDetectValue", double.class));
            put(12, new MethodParams("setIsLeakage", double.class));
            put(13, new MethodParams("setLeakagePosition", String.class));
            put(14, new MethodParams("setRemarks", String.class));

        }
    };

    public void setData(String columnName, String data) {
        switch (columnName) {
            case "A":
                setDetectedEquipment(data);
                break;
            case "B":
                setArea(data);
                break;
            case "C":
                setDetectedDevice(data);
                break;
            case "D":
                setLabelNumber(data);
                break;
            case "E":
                setAddress(data);
                break;
            case "F":
                setUnitType(data);
                break;
            case "G":
                setUnitSubType(data);
                break;
            case "H":
                setLeakageThreshold(Double.parseDouble(data));
                break;
            case "I":
                setDetectMiniTime(Double.parseDouble(data));
                break;
            case "J":
                setDetectDate(data);
                break;
            case "K":
                setDetectDevice(data);
                break;
            case "L":
                setDetectValue(Double.parseDouble(data));
                break;
            case "M":
                setIsLeakage(Double.parseDouble(data));
                break;
            case "N":
                setLeakagePosition(data);
                break;
            case "O":
                setRemarks(data);
                break;
        }
    }

    public static final void convertField(NormalTask normalTask, int cellIndex, Object args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Class cls = NormalTask.class;
            MethodParams methodParams = convertMap.get(cellIndex);
            Method method = cls.getMethod(methodParams.getMethodName(), methodParams.getParamsType());
//            L.d("@@@@cellIndex:"+cellIndex+"   methodName:"+methodParams.getMethodName()
//                    +"    paramsType:"+methodParams.getParamsType()+"    args:"+args);
            method.invoke(normalTask, args);
        } catch (Exception e) {
            try {
                Class cls = NormalTask.class;
                MethodParams methodParams = convertMap.get(cellIndex);
                Method method = cls.getMethod(methodParams.getMethodName(), methodParams.getParamsType());
//            L.d("@@@@cellIndex:"+cellIndex+"   methodName:"+methodParams.getMethodName()
//                    +"    paramsType:"+methodParams.getParamsType()+"    args:"+args);
                double d = 0.0;
                method.invoke(normalTask, d);
            } catch (Exception e1) {

            }
        }
    }

    @Override
    public String toString() {
        return "NormalTask{" +
                "detectedEquipment='" + detectedEquipment + '\'' +
                ", detectValue=" + detectValue +
                ", isLeakage=" + isLeakage +
                ", detectedDevice='" + detectedDevice + '\'' +
                ", address='" + address + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
