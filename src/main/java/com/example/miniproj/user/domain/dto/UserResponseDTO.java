package com.example.miniproj.user.domain.dto;

import com.example.miniproj.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDTO {
    private Integer userId;
    private String email;
    private String accessToken;
    private String refreshToken;

    public static UserResponseDTO fromEntity(UserEntity entity) {
        return UserResponseDTO.builder()
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .build();
    }
}
