package com.example.miniproj.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.miniproj.common.service.RefreshTokenService;
import com.example.miniproj.common.util.JwtProvider;
import com.example.miniproj.user.dao.UserRepository;
import com.example.miniproj.user.domain.dto.UserPwdRequestDTO;
import com.example.miniproj.user.domain.dto.UserRequestDTO;
import com.example.miniproj.user.domain.dto.UserResponseDTO;
import com.example.miniproj.user.domain.entity.UserEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.miniproj.common.exception.CustomException;
import com.example.miniproj.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    // 회원가입 암호 해싱처리
    private final PasswordEncoder passwordEncoder;

    //
    // private final TokenService tokenService;

    public UserResponseDTO signup(UserRequestDTO request) {
        System.out.println(">>> User Service signup");

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .pwd(passwordEncoder.encode(request.getPwd()))
                .build();

        // 새 사용자 저장
        UserEntity savedUser = userRepository.save(user);

        // DTO 변환 후 반환
        return UserResponseDTO.fromEntity(savedUser);

    }

    public UserResponseDTO signin(UserRequestDTO request) {
        System.out.println(">>> User Service signin");

        // 사용자 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPwd(), user.getPwd())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 발급
        String accessToken = jwtProvider.createAT(user.getEmail());
        String refreshToken = jwtProvider.createRT(user.getEmail());

        // Refresh Token 저장
        refreshTokenService.saveToken(user.getEmail(), refreshToken);

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public UserResponseDTO pwd(UserPwdRequestDTO request) {
        System.out.println(">>> User Service pwd");

        // 사용자 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPwd(), user.getPwd())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 기존 비밀번호 != 새 비밀번호 확인
        if (passwordEncoder.matches(request.getNewPwd(), user.getPwd())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        user.setPwd(passwordEncoder.encode(request.getNewPwd()));

        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public void logout(String email) {
        System.out.println(">>> User Service logout");

        refreshTokenService.deleteToken(email);

        System.out.println(">>> UserService logout completed");
    }

}
