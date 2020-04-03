package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Quiz;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.QuizRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class StudentService {
    private final
    MatrixUserRepository userRepository;

    private final
    QuizRepository quizRepository;

    private final
    LessonRepository lessonRepository;

    @Autowired
    public StudentService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public Set<Lesson> getLessons(String _id) throws ServerInternalException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Set<Lesson> lessons = student.getLessons_s();
            return lessons;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public List<Quiz> getAllQuiz(String lessonId) throws ServerInternalException {
        try {
            Lesson lesson = lessonRepository.getOne(lessonId);
            return quizRepository.findByLessonIs(lesson);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }
}
