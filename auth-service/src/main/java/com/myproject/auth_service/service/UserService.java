package com.myproject.auth_service.service;

import com.myproject.auth_service.dto.request.AuthRequest;
import com.myproject.auth_service.dto.request.LogoutRequest;
import com.myproject.auth_service.dto.request.NewAccessTokenRequest;
import com.myproject.auth_service.dto.request.RegisterRequest;
import com.myproject.auth_service.dto.response.BaseResponse;
import com.myproject.auth_service.dto.response.LoginResponse;
import com.myproject.auth_service.dto.response.NewAccessTokenResponse;
import com.myproject.auth_service.dto.response.UserResponse;

public interface UserService {
    BaseResponse<UserResponse> register(RegisterRequest request);
    BaseResponse<LoginResponse> login(AuthRequest request);
    BaseResponse<Void> logout(LogoutRequest logoutRequest);
    BaseResponse<NewAccessTokenResponse> refreshToken(NewAccessTokenRequest request);
}
