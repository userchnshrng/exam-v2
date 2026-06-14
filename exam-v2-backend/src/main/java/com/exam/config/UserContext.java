package com.exam.config;

/**
 * 请求级用户上下文 — ThreadLocal 存储
 */
public class UserContext {

    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ROLE = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_TABLE = new ThreadLocal<>();

    public static void set(String userId, String userRole, String userTable) {
        USER_ID.set(userId);
        USER_ROLE.set(userRole);
        USER_TABLE.set(userTable);
    }

    public static void clear() {
        USER_ID.remove();
        USER_ROLE.remove();
        USER_TABLE.remove();
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static String getUserRole() {
        return USER_ROLE.get();
    }

    public static String getUserTable() {
        return USER_TABLE.get();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(USER_ROLE.get());
    }

    public static boolean isTeacher() {
        return "TEACHER".equals(USER_ROLE.get());
    }

    public static boolean isStudent() {
        return "STUDENT".equals(USER_ROLE.get());
    }
}
