package com.nxmu.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PwdResetRequest implements Serializable {
    private String userName;
    private String newPassword;
    private String sid;
}
