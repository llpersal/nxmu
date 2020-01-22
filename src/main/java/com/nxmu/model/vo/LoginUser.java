package com.nxmu.model.vo;

import lombok.Data;

@Data
public class LoginUser {
    private int userId;
    private String userName;
    private int roleId;
    private String token;
}
