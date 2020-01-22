package com.nxmu.exception;

import lombok.Data;

/**
 * 结果异常，会被 ExceptionHandler 捕捉并返回给前端
 *
 * @author lily.zhang
 * @date 2020/01/17
 */

@Data
public class ResultException extends RuntimeException {

    private int code;
    private String status;

    public ResultException(String message) {
        super(message);
        this.status = "fail";
        this.code = 1001;
    }
}
