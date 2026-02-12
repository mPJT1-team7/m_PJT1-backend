package com.example.miniproj.resume.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeReminderResponseDTO {
    private Integer resumeId;   // 보러가기 클릭 시 이동할 ID
    private String title;       // 자소서 제목
    private Long daysPassed;    // 경과 일수 (10, 20, 30...)
}
