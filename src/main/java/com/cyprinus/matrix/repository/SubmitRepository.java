package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Quiz;
import com.cyprinus.matrix.entity.Submit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubmitRepository extends JpaRepository<Submit, String> {
    Submit findByQuizAndStudent(Quiz quiz, MatrixUser student);
    Integer countByQuiz(Quiz quiz);
    Integer countByQuizAndScoreIsNull(Quiz quiz);
    Integer countByQuizAndScoreIsNotNull(Quiz quiz);
    List<Submit> findByQuiz(Quiz quiz,  Pageable pageable);
    List<Submit> findByQuizAndScoreIsNull(Quiz quiz,  Pageable pageable);
    List<Submit> findByQuizAndScoreIsNotNull(Quiz quiz,  Pageable pageable);
}
