package com.example.miniproj.resume.domain.dto;

import java.time.LocalDate;

import com.example.miniproj.resume.domain.entity.ResumeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponseDTO {
private Integer resumeId;
    private String title;
    private String category;
    private String progress;
    private boolean bookmark;
    private LocalDate createdAt;

    public static ResumeResponseDTO fromEntity(ResumeEntity entity) {
        return ResumeResponseDTO.builder()
                .resumeId(entity.getResumeId())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .progress(entity.getProgress())
                .bookmark(entity.isBookmark())
                .createdAt(entity.getCreated_at())
                .build();
    }
}
