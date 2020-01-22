package com.nxmu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {
    private Integer id;

    private String userName;

    private String sid;

    private Long outTime;

    private Boolean isDel;

    private Date createTime;

    private Date updateTime;

    public PasswordReset(String userName, String sid, Long outTime) {
        this.userName = userName;
        this.sid = sid;
        this.outTime = outTime;
    }

    public PasswordReset(String userName, Boolean isDel) {
        this.userName = userName;
        this.isDel = isDel;
    }
}