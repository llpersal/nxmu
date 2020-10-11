package com.nxmu.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserCopyRequest implements Serializable {

    private String fromUserName;

    private String toUserName;
}
