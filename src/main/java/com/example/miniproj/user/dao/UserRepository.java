package com.example.miniproj.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miniproj.user.domain.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
