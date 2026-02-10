package com.example.miniproj.interview.service;

import com.example.miniproj.interview.dao.InterviewRepository;
import com.example.miniproj.interview.domain.dto.InterviewLevelRequestDTO;
import com.example.miniproj.interview.domain.entity.InterviewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;

    @Transactional
    public void updateLevel(InterviewLevelRequestDTO dto) {
        InterviewEntity entity = interviewRepository.findById(dto.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 면접 문항입니다. id=" + dto.getInterviewId()));
        entity.setLevel(dto.getLevel());
        interviewRepository.save(entity);
    }
}