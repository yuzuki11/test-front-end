package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Quiz;
import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.entity.Submit;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.QuizRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.repository.SubmitRepository;
import com.cyprinus.matrix.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class StudentService {
    private final
    MatrixUserRepository userRepository;

    private final
    QuizRepository quizRepository;

    private final
    LessonRepository lessonRepository;

    private final SubmitRepository submitRepository;

    @Autowired
    public StudentService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository,SubmitRepository submitRepository) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.submitRepository = submitRepository;
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

    public List<QuizDTO> getQuizzesAll(String _id, String lessonId) throws ServerInternalException, ForbiddenException {
        try {
            MatrixUser stu = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(stu))
                throw new ForbiddenException("你不是这门课的学生！");
            List<QuizDTO> quizzes = quizRepository.findByLessonIs(lesson);
            return quizzes;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public HashMap<String, List<QuizDTO>> getQuizzesApart(String _id, String lessonId) throws ServerInternalException, ForbiddenException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(student))
                throw new ForbiddenException("你不是这门课的学生！");
            List<QuizDTO> quizzes = quizRepository.findByLessonIs(lesson);
            List<QuizDTO> todo = new ArrayList<>(), done = new ArrayList<>(), remarked = new ArrayList<>();
            for (QuizDTO quiz: quizzes){
                Submit submits = submitRepository.findByQuizAndStudent(quizRepository.findBy_id(quiz.get_id()), student);
                if (submits == null){
                    todo.add(quiz);
                }else{
                    if(submits.isRemark() == true)
                        remarked.add(quiz);
                    else
                        done.add(quiz);
                }
            }
            HashMap<String, List<QuizDTO>> quizzesApart = new HashMap<>();
            quizzesApart.put("todo", todo);
            quizzesApart.put("done", done);
            quizzesApart.put("remarked", remarked);
            return quizzesApart;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public Quiz getQuizInfo(String _id, String lessonId, String quizId) throws ServerInternalException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(student))
                throw new ForbiddenException("你不是这门课的学生！");
            Quiz quiz = quizRepository.findBy_id(quizId);
            return quiz;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }
}
