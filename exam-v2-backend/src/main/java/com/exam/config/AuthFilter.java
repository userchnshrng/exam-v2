package com.exam.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 简单鉴权过滤器 —— 从请求头提取用户信息，存入 UserContext
 * 不做强制拦截，由各 Controller 按需使用 UserContext 做数据隔离
 */
@Component
@Order(1)
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("X-User-Id");
        String userRole = req.getHeader("X-User-Role");
        String userTable = req.getHeader("X-User-Table");

        // 仅当请求头包含用户信息时设置
        if (userId != null && !userId.isEmpty()) {
            UserContext.set(userId, userRole, userTable);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
