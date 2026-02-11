package com.example.miniproj.resume.ctrl;

import com.example.miniproj.resume.domain.dto.ResumeBookmarkRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeProgressRequestDTO;
import com.example.miniproj.resume.domain.dto.ResumeResponseDTO;
import com.example.miniproj.resume.domain.dto.ResumeTitleRequestDTO;
import com.example.miniproj.resume.service.ResumeService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    // MyPage List Read API
    @GetMapping
    public ResponseEntity<List<ResumeResponseDTO>> getMyResumes(@RequestParam Integer userId) {

        List<ResumeResponseDTO> list = resumeService.getMyResumeList(userId);        
        return ResponseEntity.ok(list); // Return to "200, LIST"
    }

    // MyPage BookMark Update API
    @PutMapping("/bookmark")
    public ResponseEntity<?> updateBookmark(@RequestBody ResumeBookmarkRequestDTO dto) {

        try {
            resumeService.updateBookmark(dto);
            return ResponseEntity.ok("즐겨찾기 상태가 변경되었습니다.");    // Return to "200, MESSAGE"
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MyPage Progress Update API
    @PutMapping("/progress")
    public ResponseEntity<?> updateProgress(@RequestBody ResumeProgressRequestDTO dto) {

        try {
            resumeService.updateProgress(dto);
            return ResponseEntity.ok("진행 상태가 변경되었습니다.");    // Return to "200, MESSAGE"
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // @PutMapping("/progress")
    // public ResponseEntity<?> updateProgress(
    //     @RequestBody ResumeProgressRequestDTO dto,
    //     Authentication authentication
    // ) {

    //     try {
    //         String userEmail = authentication.getName();
    //         resumeService.updateProgress(dto, userEmail);
    //         return ResponseEntity.ok("진행 상태가 변경되었습니다.");    // Return to "200, MESSAGE"
    //     }
    //     catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    //     }
    // }

    // MyPage Resume Delete API
    @DeleteMapping("/delete/{resumeId}")
    public ResponseEntity<?> deleteResume(@PathVariable Integer resumeId) {
        try {
            resumeService.deleteResume(resumeId);
            return ResponseEntity.ok("자소서가 삭제되었습니다.");   // Return to "200, MESSAGE"
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MyPage Resume Title Update API
    @PatchMapping("/title")
    public ResponseEntity<?> updateTitle(@RequestBody ResumeTitleRequestDTO dto) {
        try {
            resumeService.updateTitle(dto);
            return ResponseEntity.ok("자소서 제목이 변경되었습니다.");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}