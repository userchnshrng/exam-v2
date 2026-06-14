package com.exam.service;

import com.exam.vo.LoginUserVO;

public interface AuthService {

    LoginUserVO login(String username, String password);
}
