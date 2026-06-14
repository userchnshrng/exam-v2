package com.exam.service.impl;

import com.exam.mapper.LoginMapper;
import com.exam.service.AuthService;
import com.exam.vo.LoginUserVO;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private static final List<TableDefinition> TABLES = List.of(
            new TableDefinition("adminuser", "ADMIN",
                    List.of("aid", "adminid", "admin_id", "adminno", "admin_no", "account", "username", "login_name", "aname", "adminname"),
                    List.of("apassword", "adminpassword", "admin_password", "password", "passwd", "pwd")),
            new TableDefinition("teacher", "TEACHER",
                    List.of("tid", "teacherid", "teacher_id", "account", "username", "login_name", "tname", "teachername"),
                    List.of("tpassword", "teacherpassword", "teacher_password", "password", "passwd", "pwd")),
            new TableDefinition("student", "STUDENT",
                    List.of("sid", "studentid", "student_id", "account", "username", "login_name", "sname", "studentname"),
                    List.of("spassword", "studentpassword", "student_password", "password", "passwd", "pwd"))
    );

    private final LoginMapper loginMapper;

    public AuthServiceImpl(LoginMapper loginMapper) {
        this.loginMapper = loginMapper;
    }

    @Override
    public LoginUserVO login(String username, String password) {
        for (TableDefinition tableDefinition : TABLES) {
            List<String> columns = loginMapper.listColumns(tableDefinition.tableName());
            if (columns == null || columns.isEmpty()) {
                continue;
            }

            String accountColumn = findColumn(columns, tableDefinition.accountColumns());
            String passwordColumn = findColumn(columns, tableDefinition.passwordColumns());
            if (accountColumn == null || passwordColumn == null) {
                continue;
            }

            Map<String, Object> row = loginMapper.findOneByColumn(tableDefinition.tableName(), accountColumn, username);
            if (row == null || row.isEmpty()) {
                continue;
            }

            Object dbPassword = row.get(passwordColumn);
            if (!Objects.equals(normalize(dbPassword), password)) {
                continue;
            }

            LoginUserVO userVO = new LoginUserVO();
            userVO.setRole(tableDefinition.role());
            userVO.setTableName(tableDefinition.tableName());
            userVO.setUserInfo(filterSensitiveFields(row, passwordColumn));
            return userVO;
        }
        return null;
    }

    private String findColumn(List<String> columns, List<String> candidates) {
        for (String candidate : candidates) {
            for (String column : columns) {
                if (column != null && column.equalsIgnoreCase(candidate)) {
                    return column;
                }
            }
        }
        return null;
    }

    private String normalize(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Map<String, Object> filterSensitiveFields(Map<String, Object> row, String passwordColumn) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.equalsIgnoreCase(passwordColumn)) {
                continue;
            }
            result.put(key, entry.getValue());
        }
        return result;
    }

    private record TableDefinition(String tableName, String role, List<String> accountColumns, List<String> passwordColumns) {
    }
}
