package com.nxmu.common.enums;

public enum RoleEnum {

    ADMIN(1, "管理员", "admin"),
    OPERATOR(2, "操作员", "operator"),
    USER(3, "用户", "user"),
    GUEST(4, "游客", "guest"),
    ;

    public static String getRoleDesc(Integer id) {
        for(RoleEnum role: RoleEnum.values()){
            if(id.equals(role.getId())){
                return role.getDesc();
            }
        }
        return  GUEST.getDesc();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RoleEnum(Integer id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    private Integer id;
    private String name;
    private String desc;
}
