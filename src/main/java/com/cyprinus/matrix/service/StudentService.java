package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.*;
import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
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

    private final ScoreRepository scoreRepository;

    @Autowired
    public StudentService(LessonRepository lessonRepository, QuizRepository quizRepository, MatrixUserRepository userRepository,SubmitRepository submitRepository, ScoreRepository scoreRepository) {
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.submitRepository = submitRepository;
        this.scoreRepository = scoreRepository;
    }

    public Set<Lesson> getLessons(String _id) throws ServerInternalException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            return student.getLessons_s();
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public List<QuizDTO> getQuizzesAll(String _id, String lessonId) throws ServerInternalException {
        try {
            MatrixUser stu = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(stu))
                throw new ForbiddenException("你不是这门课的学生！");
            return quizRepository.findByLessonIs(lesson);
        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) throw new EntityNotFoundException("查询不到该课程!");
            throw new ServerInternalException(e);
        }
    }

    public HashMap<String, Object> getQuizzesApart(String _id, String lessonId) throws ServerInternalException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(student))
                throw new ForbiddenException("你不是这门课的学生！");
            List<QuizDTO> quizzes = quizRepository.findByLessonIs(lesson);
            List<QuizDTO> todo = new ArrayList<>(), done = new ArrayList<>(), remarked = new ArrayList<>();
            for (QuizDTO quiz: quizzes){
                Submit submit = submitRepository.findByQuizAndStudent(quizRepository.getOne(quiz.get_id()), student);
                if (submit == null){
                    todo.add(quiz);
                }else{
                    if(submit.getScore() != null)
                        remarked.add(quiz);
                    else
                        done.add(quiz);
                }
            }
            HashMap<String, Object> quizzesApart = new HashMap<>();
            quizzesApart.put("todo", todo);
            quizzesApart.put("done", done);
            quizzesApart.put("remarked", remarked);
            return quizzesApart;
        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) throw new EntityNotFoundException("查询不到该课程!");
            throw new ServerInternalException(e);
        }
    }

    public HashMap<String, Object> getQuizInfo(String _id, String lessonId, String quizId) throws ServerInternalException, ForbiddenException, EntityNotFoundException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!lesson.getStudents().contains(student))
                throw new ForbiddenException("你不是这门课的学生！");
            Quiz quiz = quizRepository.getOne(quizId);
            Submit submit = submitRepository.findByQuizAndStudent(quiz, student);
            for (Problem problem : quiz.getProblems()){
                problem.setAnswer(null);
            }
            String status;
            String[] myAnswer = null;
            Score score = null;
            if (submit == null){
                status = "todo";
            }else{
                myAnswer = submit.getContent();
                score = submit.getScore();
                if (score == null)
                    status = "done";
                else {
                    status = "remarked";
                    // 不知道为什么PostConstruct注解没用，只能写个丑丑的循环凑合了orz
                    Integer total = 0;
                    for(int i : score.getScore()){total += i;}
                    score.setTotal(total);
                }
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("quiz", quiz);
            data.put("status", status);
            data.put("myAnswer",myAnswer);
            data.put("score",score);
            return data;
        } catch (Exception e) {
            if (e instanceof ForbiddenException) throw e;
            if (e instanceof EntityNotFoundException) throw new EntityNotFoundException("查询失败!");
            throw new ServerInternalException(e);
        }
    }

    // 总觉得错误处理得非常蠢萌orz
    @Transactional(rollbackOn = Throwable.class)
    public void submitAnswer(String _id, String lessonId, String quizId, String[] content) throws BadRequestException, ForbiddenException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Lesson lesson = lessonRepository.getOne(lessonId);
            Quiz quiz = quizRepository.getOne(quizId);
            Submit submit = new Submit();
            if (!lesson.getStudents().contains(student))
                throw new ForbiddenException("你不是这门课的学生！");
            if(submitRepository.findByQuizAndStudent(quiz, student) != null)
                throw new ForbiddenException("不可重复作答！");
            Date now = new Date();
            if( now.compareTo(quiz.getDeadline()) > 0 || now.compareTo(quiz.getStartTime()) < 0 )
                throw new ForbiddenException("不在答题时间之内！");
            List<Problem> problems = quiz.getProblems();
            if( content.length != problems.size() )
                throw new ForbiddenException("回答和问题数目不一致！");
            Boolean ifPureObjective = true;
            for (Problem problem : problems) {
                if (!problem.getIfObjective()) {
                    ifPureObjective =false;
                    break;
                }
            }
            // 客观题自动判分
            if (ifPureObjective) {
                Integer[] scores = new Integer[problems.size()];
                for (int i = 0; i < problems.size(); i++) {
                    Problem problem = problems.get(i);
                    scores[i] = (content[i].equals(problem.getAnswer()) ? quiz.getPoints()[i] : 0);
                }
                Score score = new Score();
                score.setQuiz(quiz);
                score.setScore(scores);
                score.setStudent(student);
                score.setSubmit(submit);
                submit.setScore(score);
                scoreRepository.save(score);
            }
            submit.setContent(content);
            submit.setStudent(student);
            submit.setQuiz(quiz);
            submit.setRemark(ifPureObjective);
            submitRepository.save(submit);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e instanceof ForbiddenException) throw e;
            throw new BadRequestException(e);
        }
    }
}
