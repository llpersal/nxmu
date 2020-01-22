package com.nxmu.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PwdReset {
    private String userName;
    private String newPassword;
    private String sid;
}
