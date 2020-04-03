package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, String> {
    List<Quiz> findByLessonIs(Lesson lesson);
}
