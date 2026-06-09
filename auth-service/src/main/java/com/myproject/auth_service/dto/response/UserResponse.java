package com.myproject.auth_service.dto.response;

import com.myproject.auth_service.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private UserRole userRole;
}
