package com.example.miniproj.resume.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miniproj.resume.domain.entity.ResumeEntity;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Integer> {
    // 1. MyPage List Read
    List<ResumeEntity> findByUser_UserId(Integer userId);

    // List<ResumeEntity> findByUser_UserIdAndProgress(Integer userId, String progress);   
    // List<ResumeEntity> findByUser_UserIdAndBookmarkTrue(Integer userId);
    // List<ResumeEntity> findByUser_UserIdAndCategory(Integer userId, String category);

}
