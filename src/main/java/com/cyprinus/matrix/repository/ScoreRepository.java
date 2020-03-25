package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, String> {
}
