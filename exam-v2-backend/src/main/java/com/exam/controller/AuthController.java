package com.exam.controller;

import com.exam.common.ApiResponse;
import com.exam.dto.LoginRequest;
import com.exam.service.AuthService;
import com.exam.vo.LoginUserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginUserVO> login(@Valid @RequestBody LoginRequest request) {
        LoginUserVO user = authService.login(request.getUsername(), request.getPassword());
        if (user == null) {
            return ApiResponse.fail("用户名或密码错误");
        }
        return ApiResponse.success(user);
    }
}
