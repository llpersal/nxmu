package com.nxmu.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nxmu.exception.entity.ErrorCode;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 7563572234517927934L;
    private ApiResponse.Status status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMsg;

    private ApiResponse() {
    }

    public static <T> ApiResponse success(T content) {
        ApiResponse response = new ApiResponse();
        if (content != null) {
            response.content = content;
        }

        response.status = ApiResponse.Status.ok;
        return response;
    }

    public static ApiResponse<String> success() {
        return success("ok");
    }

    public static ApiResponse successMap(Object... keyValues) {
        Map<Object, Object> mm = new LinkedHashMap(keyValues.length / 2);

        for(int i = 0; i < keyValues.length; ++i) {
            Object var10001 = keyValues[i];
            ++i;
            mm.put(var10001, keyValues[i]);
        }

        return success(mm);
    }

    public static ApiResponse failed(String errorCode, String errorMsg) {
        ApiResponse response = new ApiResponse();
        response.status = ApiResponse.Status.fail;
        response.errorCode = errorCode;
        response.errorMsg = errorMsg;
        return response;
    }

    public static ApiResponse failed(String errorMsg) {
        return failed("101", errorMsg);
    }

    public static ApiResponse failed(ErrorCode errorCode) {
        return failed(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public ApiResponse.Status getStatus() {
        return this.status;
    }

    public T getContent() {
        return this.content;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    @JsonIgnore
    public boolean isFailed() {
        return ApiResponse.Status.fail == this.status;
    }

    @JsonIgnore
    public boolean isOk() {
        return ApiResponse.Status.ok == this.status;
    }

    public ApiResponse setContent(T content) {
        this.content = content;
        return this;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("status:").append(this.status).append(" ").append("content:").append(this.content).append(" ").append("errorCode:").append(this.errorCode).append(" ").append("errorMsg:").append(this.errorMsg);
        return builder.toString();
    }

    public static enum Status {
        ok,
        fail;

        private Status() {
        }
    }
}

