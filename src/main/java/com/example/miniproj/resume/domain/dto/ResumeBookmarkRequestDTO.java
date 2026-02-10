package com.example.miniproj.resume.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeBookmarkRequestDTO {
    private Integer resumeId;
    private boolean bookmark;
}
