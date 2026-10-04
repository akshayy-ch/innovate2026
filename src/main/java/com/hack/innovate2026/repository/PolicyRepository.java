package com.hack.innovate2026.repository;

import com.hack.innovate2026.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByCategory(String category);

    List<Policy> findByActiveTrue();
}