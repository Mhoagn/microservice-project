package com.myproject.auth_service.dto.response;

import com.myproject.auth_service.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {

    private Long id;
    private String email;
    private String userRole;

    private String accessToken;
    private String refreshToken;
}