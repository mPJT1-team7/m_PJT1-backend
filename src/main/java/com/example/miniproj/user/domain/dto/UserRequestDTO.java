package com.example.miniproj.user.domain.dto;

import com.example.miniproj.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRequestDTO {

    private String email;
    private String pwd;

    public UserEntity toEntity() {
        return UserEntity.builder().email(email).pwd(pwd).build();

    }

}
