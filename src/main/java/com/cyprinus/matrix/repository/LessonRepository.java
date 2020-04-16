package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, String> {

}
