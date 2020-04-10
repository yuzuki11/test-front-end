package com.cyprinus.matrix.service;

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
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class QuizService {
    private final
    MatrixUserRepository userRepository;

    private final
    QuizRepository quizRepository;

    private final
    LessonRepository lessonRepository;

    private final LabelRepository labelRepository;

    private final ProblemRepository problemRepository;

    private final PictureRepository pictureRepository;

    @Autowired
    public QuizService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository, LabelRepository labelRepository, ProblemRepository problemRepository, PictureRepository pictureRepository) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.problemRepository = problemRepository;
        this.pictureRepository = pictureRepository;
    }

    public Quiz getQuiz(String _id) throws ServerInternalException {
        try {
            return quizRepository.getOne(_id);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    @Transactional(rollbackOn = Throwable.class)
    public void deleteQuiz(String _id) throws ServerInternalException{
        try {
            quizRepository.deleteById(_id);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    @Transactional(rollbackOn = Throwable.class)
    public void createQuiz(String _id, HashMap<String, Object> quizInfo) throws ServerInternalException, BadRequestException, Exception {
        try {
            int baseNum = 0;
            Quiz quiz = new Quiz();
            String lessonId = (String)quizInfo.get("lessonId");
            Lesson lesson = lessonRepository.getOne(lessonId);
            quiz.setLesson(lesson);
            quiz.setProblems(problemRepository.findAllById((List<String>)quizInfo.get("problems")));
            quizRepository.saveAndFlush(quiz); //不知道啥意思总之复读一下
            List<Label> labels = labelRepository.findAllById((List<String>) quizInfo.get("labels"));
            for (Label label : labels) {
                if (label.isBase()) {
                    baseNum++;
                }
                label.addQuiz(quiz);
                if (baseNum > 1) throw new BadRequestException("传入过多基标签！");
            }
            if (baseNum < 1) throw new BadRequestException("至少应有一个基标签！");
            labelRepository.saveAll(labels);
            quiz.setLabels(labels);
            quiz.setTitle((String)quizInfo.get("title"));
            List points = (List)quizInfo.get("points");
            int size = points.size();
            quiz.setPoints((Integer[])points.toArray(new Integer[size]));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            quiz.setDeadline(df.parse((String)quizInfo.get("deadline")));
            quiz.setStartTime(df.parse((String)quizInfo.get("startTime")));
            quizRepository.save(quiz);
        } catch (Exception e) {
            throw e;
            //System.out.println(e.getMessage());
           // throw new ServerInternalException(e);
        }
    }

}
