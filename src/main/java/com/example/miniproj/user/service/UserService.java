package com.example.miniproj.user.service;

import org.springframework.stereotype.Service;

import com.example.miniproj.user.dao.UserRepository;
import com.example.miniproj.user.domain.dto.UserPwdRequestDTO;
import com.example.miniproj.user.domain.dto.UserRequestDTO;
import com.example.miniproj.user.domain.dto.UserResponseDTO;
import com.example.miniproj.user.domain.entity.UserEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO signup(UserRequestDTO request) {
        System.out.println(">>> User Service signup");

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 새 사용자 저장
        UserEntity savedUser = userRepository.save(request.toEntity());

        // DTO 변환 후 반환
        return UserResponseDTO.fromEntity(savedUser);

    }

    public UserResponseDTO signin(UserRequestDTO request) {
        System.out.println(">>> User Service signin");

        // 사용자 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!user.getPwd().equals(request.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO pwd(UserPwdRequestDTO request) {
        System.out.println(">>> User Service pwd");

        // 사용자 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!user.getPwd().equals(request.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        user.setPwd(request.getNewPwd());

        return UserResponseDTO.fromEntity(user);
    }

}
