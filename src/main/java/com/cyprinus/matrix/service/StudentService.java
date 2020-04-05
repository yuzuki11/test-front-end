package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Quiz;
import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.QuizRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private final ObjectUtil objectUtil;

    @Autowired
    public StudentService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository, ObjectUtil objectUtil) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.objectUtil = objectUtil;
    }

    public Set<Lesson> getLessons(String _id) throws ServerInternalException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            return student.getLessons_s();
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public List<QuizDTO> getAllQuizzes(String _id, String lessonId) throws ServerInternalException {
        try {
            MatrixUser stu = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(stu))
                throw new ForbiddenException("你不是这门课的学生！");
            return quizRepository.findByLessonIs(lesson);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public Quiz getQuizInfo(String _id, String lessonId, String quizId) throws ServerInternalException {
        try {
            MatrixUser stu = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(stu))
                throw new ForbiddenException("你不是这门课的学生！");
            return quizRepository.getOne(quizId);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }
}
