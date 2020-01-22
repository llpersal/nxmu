package com.nxmu.exception.entity;

public class ErrorCode {
    private String errorCode;
    private String errorMsg;

    public ErrorCode() {
    }

    public ErrorCode(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public static ErrorCode create() {
        return new ErrorCode();
    }

    public static ErrorCode create(String errorCode, String errorMsg) {
        return new ErrorCode(errorCode, errorMsg);
    }

    public ErrorCode errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ErrorCode errorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
