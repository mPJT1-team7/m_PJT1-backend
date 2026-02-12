package com.example.miniproj.interview.dao;

import com.example.miniproj.interview.domain.entity.InterviewEntity;
import com.example.miniproj.resume.domain.entity.ResumeEntity; // ResumeEntity 위치 확인
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// [중요] ID 타입이 Long에서 Integer로 변경됨
public interface InterviewRepository extends JpaRepository<InterviewEntity, Integer> {
    
    // 특정 자소서에 달린 질문들 가져오기
    List<InterviewEntity> findAllByResume(ResumeEntity resume);
}