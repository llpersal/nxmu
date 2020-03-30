package com.nxmu.model.vo;

import lombok.Data;

@Data
public class LoginUser {
    private Integer userId;
    private String userName;
    private Integer roleId;
    private String roleDesc;
    private String token;
}
