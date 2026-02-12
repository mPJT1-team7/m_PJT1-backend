package com.example.miniproj.resume.service;

import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproj.resume.dao.ResumeRepository;
import com.example.miniproj.resume.domain.dto.ResumeBookmarkRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeProgressRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeReminderResponseDTO;
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

    // 팝업
    @Transactional(readOnly = true)
    public ResumeReminderResponseDTO getRandomReminder(Integer userId, String email) {
        // 1. 보안 컨셉: 유저 확인 및 권한 체크
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        
        if (!user.getEmail().equals(email)) {
            throw new SecurityException("해당 데이터를 조회할 권한이 없습니다.");
        }

        // 2. 자소서 조회 및 필터링
        List<ResumeEntity> entities = resumeRepository.findByUser_UserId(userId);
        LocalDate today = LocalDate.now();

        // 3. 조건에 맞는 후보군을 찾고, 바로 DTO로 변환
        List<ResumeReminderResponseDTO> candidates = entities.stream()
                .filter(ResumeEntity::isAnswer_state) // 피드백 완료 상태
                .map(r -> {
                    long days = ChronoUnit.DAYS.between(r.getCreated_at(), today);
                    if (days > 0 && days % 10 == 0) {
                        return ResumeReminderResponseDTO.builder()
                                .resumeId(r.getResumeId())
                                .title(r.getTitle())
                                .daysPassed(days)
                                .build();
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        // 4. 랜덤 반환
        if (candidates.isEmpty()) return null;
        Collections.shuffle(candidates);
        return candidates.get(0);
    }
}