package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.Quiz;
import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.dto.QuizDTO2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, String> {

    List<QuizDTO> findByLessonIs(Lesson lesson);

    List<Quiz> findByLesson(Lesson lesson);

    QuizDTO2 findBy_id(String _id);
}
