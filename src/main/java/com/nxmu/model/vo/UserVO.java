package com.nxmu.model.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {

    private Integer id;

    private String userName;

    private String jobNumber;

    private Integer roleId;

    private String roleDesc;

    private Integer orgId;

    private String orgName;

    private String createTime;

    private String createUserName;
}
