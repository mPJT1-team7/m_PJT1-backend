package com.example.miniproj.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproj.common.service.RefreshTokenService;
import com.example.miniproj.common.util.JwtProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Security Test", description = "Security and JWT Test")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class SecurityTestController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Generate Token", description = "액세스 토큰 생성 테스트")
    @GetMapping("/generate-token")
    public String generateToken(@RequestParam String email) {
        String accessToken = jwtProvider.createAT(email);
        String refreshToken = jwtProvider.createRT(email);
        
        refreshTokenService.saveToken(email, refreshToken);
        
        return "Access Token: " + accessToken;
    }

    @Operation(summary = "Test Secured Endpoint", description = "토큰 인증 테스트")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/secured")
    public String secured() {
        return "You have accessed a secured endpoint!";
    }
}
