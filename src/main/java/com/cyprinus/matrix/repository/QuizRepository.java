package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, String> {
}
