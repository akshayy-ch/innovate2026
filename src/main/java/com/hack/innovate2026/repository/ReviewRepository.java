package com.hack.innovate2026.repository;

import com.hack.innovate2026.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByExceptionId(Long exceptionId);

    List<Review> findByReviewerId(Long reviewerId);
}