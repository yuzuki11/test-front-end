package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, String> {
}
