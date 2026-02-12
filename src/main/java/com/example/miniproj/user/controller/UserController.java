package com.example.miniproj.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproj.user.domain.dto.UserPwdRequestDTO;
import com.example.miniproj.user.domain.dto.UserRequestDTO;
import com.example.miniproj.user.domain.dto.UserResponseDTO;
import com.example.miniproj.user.service.UserService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API 명세서")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "사용자 회원가입", required = true, content = @Content(schema = @Schema(implementation = UserRequestDTO.class))) @RequestBody UserRequestDTO request) {

        System.out.println(">>> User Controller: /signup");
        System.out.println(request);

        UserResponseDTO response = userService.signup(request);

        System.out.println(">>> User Controller signup response: " + response);

        if (response != null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<UserResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "사용자 로그인", required = true, content = @Content(schema = @Schema(implementation = UserRequestDTO.class))) @RequestBody UserRequestDTO request) {
        System.out.println(">>> User Controller: /signin");
        System.out.println(request);

        UserResponseDTO response = userService.signin(request);
        System.out.println(">>> User Controller signin response: " + response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<String> password(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "사용자 비밀번호 변경", required = true, content = @Content(schema = @Schema(implementation = UserPwdRequestDTO.class))) @RequestBody UserPwdRequestDTO request) {
        System.out.println(">>> User Controller: /password");
        System.out.println(request);

        try {
            userService.pwd(request);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "사용자 로그아웃", required = true, content = @Content(schema = @Schema(implementation = UserRequestDTO.class))) @RequestBody UserRequestDTO request) {

        System.out.println(">>> User Controller: /logout");

        userService.logout(request.getEmail());

        return ResponseEntity.noContent().build();
    }

}
