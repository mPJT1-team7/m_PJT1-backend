package com.example.miniproj.resume.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.miniproj.interview.domain.entity.InterviewEntity;
import com.example.miniproj.user.domain.entity.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resumes")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resumeId;

    private LocalDate created_at;

    private String title;

    private String content;

    private String category;

    private boolean answer_state;

    // 자소서 즐겨찾기
    private boolean bookmark;

    // 자소서 진도 체크
    private String progress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @OneToMany(mappedBy = "resume")
    private List<InterviewEntity> interview = new ArrayList<>();

}

/*
 * 
 * user(1) - (n)resume(1) - (n)interview
 * 
 */