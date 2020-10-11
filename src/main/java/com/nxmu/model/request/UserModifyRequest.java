package com.nxmu.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserModifyRequest implements Serializable {

    private String userName;

    private String email;

    private String qq;

    private String wechat;

    private Integer orgId;
}
