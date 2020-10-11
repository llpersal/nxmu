package com.nxmu.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {

    private String userName;

    private String password;

    private String verifyCode;
}
