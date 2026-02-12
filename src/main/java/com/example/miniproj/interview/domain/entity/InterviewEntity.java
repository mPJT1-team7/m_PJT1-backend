package com.example.miniproj.interview.domain.entity;

import java.time.LocalDate;

import com.example.miniproj.resume.domain.entity.ResumeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interviews")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewId;

    private String question;
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String feedback;
    // 질문 만족도 (0~5)
    private int level;

    private LocalDate created_at;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resumeId")
    private ResumeEntity resume;
}