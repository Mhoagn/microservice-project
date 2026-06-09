package com.myproject.auth_service.controller;

import com.myproject.auth_service.dto.request.AuthRequest;
import com.myproject.auth_service.dto.request.LogoutRequest;
import com.myproject.auth_service.dto.request.NewAccessTokenRequest;
import com.myproject.auth_service.dto.request.RegisterRequest;
import com.myproject.auth_service.dto.response.BaseResponse;
import com.myproject.auth_service.dto.response.LoginResponse;
import com.myproject.auth_service.dto.response.NewAccessTokenResponse;
import com.myproject.auth_service.dto.response.UserResponse;
import com.myproject.auth_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterRequest registerRequest) {
        BaseResponse<UserResponse> response =
                userService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody AuthRequest authRequest) {
        BaseResponse<LoginResponse> response =
                userService.login(authRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        BaseResponse<Void> response = userService.logout(logoutRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refresh(@RequestBody NewAccessTokenRequest request) {
        BaseResponse<NewAccessTokenResponse> response = userService.refreshToken(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
