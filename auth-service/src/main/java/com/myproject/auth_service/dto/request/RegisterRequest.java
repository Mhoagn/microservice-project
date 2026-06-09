package com.myproject.auth_service.dto.request;

import com.myproject.auth_service.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest extends AuthRequest{
    private String confirmedPassword;
    private UserRole userRole;

    public RegisterRequest(String email, String password, String confirmedPassword, UserRole userRole) {
        super(email, password);
        this.confirmedPassword = confirmedPassword;
        this.userRole = userRole;
    }
}
