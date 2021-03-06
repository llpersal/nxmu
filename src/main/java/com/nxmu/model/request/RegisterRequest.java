package com.nxmu.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel public class RegisterRequest implements Serializable {
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("角色ID：登录时不需要，注册时需要")
    private Integer roleId;
    @ApiModelProperty("验证码")
    private String verifyCode;
}
