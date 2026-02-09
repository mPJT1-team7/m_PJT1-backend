package com.example.miniproj.user.domain.dto;

import com.example.miniproj.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class UserResponseDTO {
    private Integer userId;
    String email;
    String pwd;

    public static UserResponseDTO fromEntity(UserEntity entity) {
        return UserResponseDTO.builder()
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .pwd(entity.getPwd())
                .build();
    }

}
