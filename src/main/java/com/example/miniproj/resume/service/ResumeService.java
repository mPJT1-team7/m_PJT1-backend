package com.example.miniproj.resume.service;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproj.resume.dao.ResumeRepository;
import com.example.miniproj.resume.domain.dto.ResumeBookmarkRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeProgressRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeResponseDTO;
import com.example.miniproj.resume.domain.entity.ResumeEntity;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    // MyPage List Read API
    public List<ResumeResponseDTO> getMyResumeList(Integer userId) {

        List<ResumeEntity> entities = resumeRepository.findByUser_UserId((userId));
        return entities.stream()
                .map(entity -> {
                    ResumeResponseDTO response = ResumeResponseDTO.fromEntity(entity);
                    return response;
                })
                .collect(Collectors.toList());
    }

    // MyPage BookMark Update API
    @Transactional
    public void updateBookmark(ResumeBookmarkRequestDTO dto) {

        ResumeEntity entity = resumeRepository.findById(dto.getResumeId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + dto.getResumeId()));
        entity.setBookmark(dto.isBookmark());
        resumeRepository.save(entity);
    }

    // MyPage Progress Update API
    @Transactional
    public void updateProgress(ResumeProgressRequestDTO dto) {

        ResumeEntity entity = resumeRepository.findById(dto.getResumeId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + dto.getResumeId()));
        entity.setProgress(dto.getProgress());
        resumeRepository.save(entity);
    }

    // MyPage Resume Delete API
    @Transactional
    public void deleteResume(Integer resumeId) {

        ResumeEntity entity = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + resumeId));
        resumeRepository.deleteById(entity.getResumeId());
    }
}