package com.cyprinus.matrix.service;

import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.entity.*;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.*;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.BCrypt;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class TeacherService {
    private final
    MatrixUserRepository userRepository;

    private final
    QuizRepository quizRepository;

    private final
    LessonRepository lessonRepository;

    private final LabelRepository labelRepository;

    private final ProblemRepository problemRepository;

    @Autowired
    public TeacherService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository, LabelRepository labelRepository, ProblemRepository problemRepository) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.problemRepository = problemRepository;
    }

    public Set<Lesson> getLessons(String _id) throws ServerInternalException {
        try {
            MatrixUser teacher = userRepository.getOne(_id);
            return teacher.getLessons_t();
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public HashMap<String, Object> getQuizzes(String _id, String lessonId) throws ServerInternalException {
        try {
            MatrixUser teacher = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            List<QuizDTO> quizzes = quizRepository.findByLessonIs(lesson);
            List<QuizDTO> starting = new ArrayList<>(), running = new ArrayList<>(), ended = new ArrayList<>();
            for (QuizDTO quiz: quizzes){
                Date now = new Date();
                if (now.compareTo(quiz.getStartTime()) < 0){
                    starting.add(quiz);
                }else if (now.compareTo(quiz.getDeadline()) > 0){
                    ended.add(quiz);
                }else{
                    running.add(quiz);
                }
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("starting", starting);
            data.put("running", running);
            data.put("ended", ended);
            return data;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }


}
