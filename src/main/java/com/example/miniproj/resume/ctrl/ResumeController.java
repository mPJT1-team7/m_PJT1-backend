package com.example.miniproj.resume.ctrl;

import com.example.miniproj.resume.domain.dto.ResumeBookmarkRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeProgressRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeReminderResponseDTO;
import com.example.miniproj.resume.domain.dto.ResumeResponseDTO;
import com.example.miniproj.resume.domain.dto.ResumeTitleRequestDTO;
import com.example.miniproj.resume.service.ResumeService;
import com.example.miniproj.user.domain.dto.UserPwdRequestDTO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    // MyPage List Read API
    @GetMapping("/{userId}")
    public ResponseEntity<List<ResumeResponseDTO>> getMyResumes(
            @Parameter(description = "사용자 자기소개서 전체 조회", required = true) @PathVariable Integer userId,
            Authentication authentication) {

        try {
            List<ResumeResponseDTO> list = resumeService.getMyResumeList(userId, authentication.getName());
            return ResponseEntity.ok(list); // Return to "200, LIST"
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
        }
    }

    // MyPage BookMark Update API
    @PutMapping("/bookmark")
    public ResponseEntity<?> updateBookmark(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "자기소개서 즐겨찾기 수정", required = true, content = @Content(schema = @Schema(implementation = ResumeBookmarkRequestDTO.class))) @RequestBody ResumeBookmarkRequestDTO dto,
            Authentication authentication) {

        try {
            resumeService.updateBookmark(dto, authentication.getName());
            return ResponseEntity.ok("즐겨찾기 상태가 변경되었습니다."); // Return to "200, MESSAGE"
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MyPage Progress Update API
    @PutMapping("/progress")
    public ResponseEntity<?> updateProgress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "자기소개서 진행도 수정", required = true, content = @Content(schema = @Schema(implementation = ResumeProgressRequestDTO.class))) @RequestBody ResumeProgressRequestDTO dto,
            Authentication authentication) {

        try {
            resumeService.updateProgress(dto, authentication.getName());
            return ResponseEntity.ok("진행 상태가 변경되었습니다."); // Return to "200, MESSAGE"
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MyPage Resume Delete API
    @DeleteMapping("/delete/{resumeId}")
    public ResponseEntity<?> deleteResume(
            @Parameter(description = "자기소개서 삭제", required = true) @PathVariable Integer resumeId,
            Authentication authentication) {
        try {
            resumeService.deleteResume(resumeId, authentication.getName());
            return ResponseEntity.ok("자소서가 삭제되었습니다."); // Return to "200, MESSAGE"
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MyPage Resume Title Update API
    @PatchMapping("/title")
    public ResponseEntity<?> updateTitle(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "자기소개서 제목 수정", required = true, content = @Content(schema = @Schema(implementation = ResumeTitleRequestDTO.class))) @RequestBody ResumeTitleRequestDTO dto,
            Authentication authentication) {
        try {
            resumeService.updateTitle(dto, authentication.getName());
            return ResponseEntity.ok("자소서 제목이 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/popup/{userId}")
    public ResponseEntity<ResumeReminderResponseDTO> getReminderPopup(
            @Parameter(description = "자기소개서 리마인드", required = true) @PathVariable("userId") Integer userId,
            Authentication auth) {

        ResumeReminderResponseDTO reminder = resumeService.getRandomReminder(userId, auth.getName());

        if (reminder == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reminder);
    }
}