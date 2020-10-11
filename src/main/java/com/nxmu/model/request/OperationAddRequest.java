package com.nxmu.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OperationAddRequest implements Serializable {

    @NotNull
    private String userName;

    @NotNull
    private String password;

    @NotNull
    private Integer orgId;
}
