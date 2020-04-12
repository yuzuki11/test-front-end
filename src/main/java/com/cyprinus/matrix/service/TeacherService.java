package com.cyprinus.matrix.service;

import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.entity.*;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.MatrixBaseException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.*;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.BCrypt;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
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

    private final SubmitRepository submitRepository;

    private final TextBookRepository textBookRepository;

    @Autowired
    public TeacherService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository, SubmitRepository submitRepository,TextBookRepository textBookRepository) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.submitRepository = submitRepository;
        this.textBookRepository=textBookRepository;
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

    public List<Submit> getSubmits(String _id, String lessonId, String quizId,int page, int size, String remark) throws ServerInternalException {
        try {
            Lesson lesson = lessonRepository.getOne(lessonId);
            MatrixUser teacher = userRepository.getOne(_id);
            if (!lesson.getTeacher().equals(teacher))
                throw new ForbiddenException("你不是这门课的老师！");
            Quiz quiz = quizRepository.getOne(quizId);
            Pageable pageable = PageRequest.of(page - 1, size);
            List<Submit> submit;
            if (remark.equals("all"))
                submit = submitRepository.findByQuiz(quiz, pageable);
            else if (remark.equals("true"))
                submit = submitRepository.findByQuizAndScoreIsNotNull(quiz, pageable);
            else
                submit = submitRepository.findByQuizAndScoreIsNull(quiz, pageable);
            return submit;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public Integer getSubmitsCount(String _id, String lessonId, String quizId, String remark) throws ServerInternalException {
        try {
            Quiz quiz = quizRepository.getOne(quizId);
            Integer count;
            System.out.println(remark);
            if (remark.equals("all"))
                count = submitRepository.countByQuiz(quiz);
            else if(remark.equals("true"))
                count = submitRepository.countByQuizAndScoreIsNotNull(quiz);
            else
                count = submitRepository.countByQuizAndScoreIsNull(quiz);
            return count;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public HashMap<String, Object> getScore(String _id, String lessonId) throws ServerInternalException {
        try {
            HashMap<String, Object> data = new HashMap<>();
            return data;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    @Transactional(rollbackOn = Throwable.class)
    public void remark(String _id, String lessonId, String quizId,  String submitId, ArrayList scores) throws ServerInternalException, ForbiddenException {
        try {

            Lesson lesson = lessonRepository.getOne(lessonId);
            MatrixUser teacher = userRepository.getOne(_id);
            if (!lesson.getTeacher().equals(teacher))
                throw new ForbiddenException("你不是这门课的老师！");
            Quiz quiz = quizRepository.getOne(quizId);
            Submit submit = submitRepository.getOne(submitId);
            submit.setScore((Integer[])scores.toArray(new Integer[scores.size()]));
            submitRepository.save(submit);
            //屎 从此开始
            List<Problem> problems = quiz.getProblems();
            Boolean ifObjective = true;
            List<Problem> wrongproblems=new ArrayList<Problem>();
            for (Problem problem : problems) {
                if (!problem.getIfObjective()) {
                    ifObjective =false;
                    break;
                }
            }
            TextBookService textBookService=new TextBookService(textBookRepository,lessonRepository,userRepository);
            TextBook textBook=new TextBook();
            //非客观题
            if (!ifObjective) {
                for (int i = 0; i < problems.size(); i++) {
                    Problem problem = problems.get(i);
                    if (scores.get(i)!=quiz.getPoints()[i])
                        //加错题wrongproblems
                        wrongproblems.add(problem);
                }
            }
            //我扔
            textBookService.addProblemInTextbook(textBook,_id,lessonId,wrongproblems);

        }catch (Exception e) {
            if(e instanceof MatrixBaseException) throw e;
            throw new ServerInternalException(e);
        }
    }

}
