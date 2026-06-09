package com.myproject.auth_service.service.Impl;

import com.myproject.auth_service.dao.UserDao;
import com.myproject.auth_service.dto.kafka.RestaurantOwnerCreatedEvent;
import com.myproject.auth_service.dto.request.AuthRequest;
import com.myproject.auth_service.dto.request.LogoutRequest;
import com.myproject.auth_service.dto.request.NewAccessTokenRequest;
import com.myproject.auth_service.dto.request.RegisterRequest;
import com.myproject.auth_service.dto.response.BaseResponse;
import com.myproject.auth_service.dto.response.LoginResponse;
import com.myproject.auth_service.dto.response.NewAccessTokenResponse;
import com.myproject.auth_service.dto.response.UserResponse;
import com.myproject.auth_service.entity.User;
import com.myproject.auth_service.exception.EmailExistedException;
import com.myproject.auth_service.exception.NotFoundException;
import com.myproject.auth_service.exception.PasswordMismatchException;
import com.myproject.auth_service.kafka.RestaurantOwnerProducer;
import com.myproject.auth_service.security.JwtService;
import com.myproject.auth_service.security.RefreshTokenService;
import com.myproject.auth_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RestaurantOwnerProducer restaurantOwnerProducer;

    @Override
    public BaseResponse<UserResponse> register(RegisterRequest request) {
        String email = request.getEmail();

        User existedUser = userDao.findByEmail(email);

        if(existedUser != null) {
            throw new EmailExistedException("Email is existed");
        }

        if (!request.getPassword().equals(request.getConfirmedPassword())) {
            throw new PasswordMismatchException("Password confirmation does not match");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setUserRole(request.getUserRole());

        userDao.saveUser(newUser);
        if(newUser.getUserRole().name().equals("RestaurantOwner")) {
            RestaurantOwnerCreatedEvent event = new RestaurantOwnerCreatedEvent(newUser.getUserId());
            restaurantOwnerProducer.sendRestaurantOwnerCreatedEvent(event);
        }

        UserResponse response = UserResponse.builder()
                .email(newUser.getEmail())
                .userRole(newUser.getUserRole())
                .build();

        return new BaseResponse<>(
                true,
                "Register successfully",
                response
        );
    }

    @Override
    public BaseResponse<LoginResponse> login(AuthRequest request) {

        String email = request.getEmail();

        User existedUser = userDao.findByEmail(email);

        if (existedUser == null) {
            throw new NotFoundException("Invalid email or password");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                existedUser.getPassword()
        )) {
            throw new PasswordMismatchException("Invalid email or password");
        }

        String accessToken =
                jwtService.generateAccessToken(
                        existedUser.getUserId(),
                        existedUser.getEmail(),
                        existedUser.getUserRole().name()
                );

        String refreshToken =
                jwtService.generateRefreshToken(
                        existedUser.getUserId()
                );

        refreshTokenService.save(
                refreshToken,
                existedUser.getUserId()
        );

        LoginResponse response = LoginResponse.builder()
                .id(existedUser.getUserId())
                .email(existedUser.getEmail())
                .userRole(existedUser.getUserRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return new BaseResponse<>(
                true,
                "Login successfully",
                response
        );
    }

    public BaseResponse<Void> logout(LogoutRequest logoutRequest) {
        String refreshToken = logoutRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is required");
        }

        // Xoá khỏi Redis => revoke token
        refreshTokenService.delete(refreshToken);

        return new BaseResponse<>(
                true,
                "Logout successfully",
                null
        );
    }

    @Override
    public BaseResponse<NewAccessTokenResponse> refreshToken(NewAccessTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. check null
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is required");
        }

        // 2. check Redis (token chưa bị revoke)
        if (!refreshTokenService.exists(refreshToken)) {
            throw new RuntimeException("Refresh token revoked or expired");
        }

        // 3. validate JWT refresh token
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 4. extract userId
        Long userId = jwtService.extractUserIdFromRefreshToken(refreshToken);

        // 5. get user from DB
        User user = userDao.findById(userId);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // 6. generate new access token
        String newAccessToken = jwtService.generateAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getUserRole().name()
        );

        // 7. response
        NewAccessTokenResponse response = NewAccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();

        return new BaseResponse<>(
                true,
                "Refresh token success",
                response
        );
    }
}
