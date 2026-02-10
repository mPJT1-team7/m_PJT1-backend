package com.example.miniproj.interview.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.miniproj.interview.domain.entity.InterviewEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<InterviewEntity, Integer> {
}