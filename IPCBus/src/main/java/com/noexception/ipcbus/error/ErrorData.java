package com.noexception.ipcbus.error;

/**
 * 存放错误的数据
 */
public class ErrorData {

    // 错误码
    private int errorCode;

    // 错误描述
    private String description;

    public ErrorData() {}

    public ErrorData(int errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ErrorData{" +
                "errorCode=" + errorCode +
                ", description='" + description + '\'' +
                '}';
    }
}
