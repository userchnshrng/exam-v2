package com.exam.vo;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginUserVO {

    private String role;
    private String tableName;
    private Map<String, Object> userInfo = new LinkedHashMap<>();

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }
}
