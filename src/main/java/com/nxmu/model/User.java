package com.nxmu.model;

import com.nxmu.common.utils.NumberUtil;
import com.nxmu.common.utils.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;

    private String userName;

    private String nickName;

    private String jobNumber;

    private Integer roleId;

    private Integer orgId;

    private String password;

    private String email;

    private String wechat;

    private String qq;

    private Integer createUser;

    private Boolean isDel;

    private Date createTime;

    private Date updateTime;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User perfectUser(int userId, int roleId) {
        User user = new User();
        user.setId(userId);
        user.setCreateUser(userId);
        user.setJobNumber(TimeUtil.getCurrentYear() + NumberUtil.getRoleNum(roleId) + NumberUtil.getUserNum(userId));
        return user;
    }
}