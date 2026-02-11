package com.example.miniproj.user.domain.dto;

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
public class UserPwdRequestDTO {

    private String email;
    private String pwd;
    private String newPwd;

}