package com.myproject.auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewAccessTokenResponse {
    private String accessToken;
}
