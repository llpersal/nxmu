package com.nxmu.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserVO implements Serializable {
    private Integer userId;
    private String userName;
    private Integer roleId;
    private String roleDesc;
    private String token;
}
