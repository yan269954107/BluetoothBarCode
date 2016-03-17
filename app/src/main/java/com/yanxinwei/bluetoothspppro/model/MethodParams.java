package com.yanxinwei.bluetoothspppro.model;

/**
 * Created by yanxinwei on 16/3/16.
 */
public class MethodParams {

    private String methodName;
    private Class paramsType;

    public MethodParams(String methodName, Class paramsType) {
        this.methodName = methodName;
        this.paramsType = paramsType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class getParamsType() {
        return paramsType;
    }

    public void setParamsType(Class paramsType) {
        this.paramsType = paramsType;
    }
}
