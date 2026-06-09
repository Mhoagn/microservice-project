package com.myproject.auth_service.dto.request;

import lombok.Data;

@Data
public class NewAccessTokenRequest {
    private String refreshToken;
}