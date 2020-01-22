package com.nxmu.exception.entity;

import lombok.Data;

/**
 * @author lily.zhang
 * @date 2018/9/14
 */
@Data
public class Result<T> {

    private int code;
    private String msg;
    private String status;

    public Result() {
    }

    public Result(String status, int code, String msg) {
        this(code, msg);
        this.status = status;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
