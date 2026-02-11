package com.example.miniproj.resume.service;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproj.resume.dao.ResumeRepository;
import com.example.miniproj.resume.domain.dto.ResumeBookmarkRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeProgressRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeResponseDTO;
import com.example.miniproj.resume.domain.dto.ResumeTitleRequestDTO;
import com.example.miniproj.resume.domain.entity.ResumeEntity;
import com.example.miniproj.user.dao.UserRepository;
import com.example.miniproj.user.domain.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    // MyPage List Read API
    public List<ResumeResponseDTO> getMyResumeList(Integer userId, String email) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        
        if (!user.getEmail().equals(email)) {
            throw new IllegalArgumentException("해당 자소서 목록을 조회할 권한이 없습니다.");
        }
        List<ResumeEntity> entities = resumeRepository.findByUser_UserId(userId);
        return entities.stream()
                .map(ResumeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // MyPage BookMark Update API
    @Transactional
    public void updateBookmark(
        ResumeBookmarkRequestDTO dto, String email) {
        ResumeEntity entity = resumeRepository.findById(dto.getResumeId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + dto.getResumeId()));
        
        if (!entity.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("해당 자소서를 수정할 권한이 없습니다.");
        }
        entity.setBookmark(dto.isBookmark());
        resumeRepository.save(entity);
    }

    // MyPage Progress Update API
    @Transactional
    public void updateProgress(ResumeProgressRequestDTO dto, String email) {

        ResumeEntity entity = resumeRepository.findById(dto.getResumeId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + dto.getResumeId()));
        if (!entity.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("해당 자소서를 수정할 권한이 없습니다.");
        }
        entity.setProgress(dto.getProgress());
        resumeRepository.save(entity);
    }

    // MyPage Resume Delete API
    @Transactional
    public void deleteResume(Integer resumeId, String email) {

        ResumeEntity entity = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + resumeId));
        if (!entity.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("해당 자소서를 삭제할 권한이 없습니다.");
        }
        resumeRepository.deleteById(entity.getResumeId());
    }

    // MyPage Resume Title Update API
    @Transactional
    public void updateTitle(ResumeTitleRequestDTO dto, String email) {

        ResumeEntity entity = resumeRepository.findById(dto.getResumeId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자소서입니다. id=" + dto.getResumeId()));
        if (!entity.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("해당 자소서를 수정할 권한이 없습니다.");
        }
        entity.setTitle(dto.getTitle());
        resumeRepository.save(entity);
    }
}