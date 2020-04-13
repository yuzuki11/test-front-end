package com.cyprinus.matrix.service;

import com.cyprinus.matrix.dto.MatrixUserDTO;
import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.dto.QuizDTO2;
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

    public HashMap<String, Object> getScore(String lessonId) throws ServerInternalException {
        try {
            HashMap<String, Object> data = new HashMap<>();
            Lesson lesson =lessonRepository.getOne(lessonId);
            List<MatrixUser> student=lesson.getStudents();
            List<Quiz> quizzes =quizRepository.findByLesson(lesson);
            List<Submit> submits=new ArrayList<Submit>();
            for (Quiz q:quizzes)
            {
                submits.addAll(submitRepository.findAllByQuiz(q));
            }
            //List<Submit> submits=submitRepository.findAllByQuiz(quizzes);
            int [][]score=new int[student.size()][quizzes.size()];
            for (int x=0;x<student.size();x++)//赋初值
                for (int y=0;y<quizzes.size();y++)
                    score[x][y]=-1;
            int i=-1,j=-1;//有新的同学是i++ 有新的quiz是j++  找学生的话对应找i 找quiz的话对应找j
            List<MatrixUser> studentByOrder = new ArrayList<MatrixUser>();
            List<Quiz> quizByOrder = new ArrayList<Quiz>();
            for (Submit sub:submits)
            {
                int stu_location=-1,quiz_location=-1;
            if(studentByOrder.contains(sub.getStudent()))//该学生存在
                //找到学生的位置
                { stu_location=studentByOrder.indexOf(sub.getStudent());}
            else {//如果没有该学生存在的话那么添加到student表里
                i++;studentByOrder.add(sub.getStudent()); }
            if (quizByOrder.contains(sub.getQuiz()))//该问题已经存在
                {  quiz_location=quizByOrder.indexOf(sub.getQuiz());}
            else{//如果没有该问题存在的话那么添加到quiz表里
                j++;quizByOrder.add(sub.getQuiz()); }
            int sum=0;
            if (sub.getScore().length!=0)//批改了
                {
                    for (int x=0;x<sub.getScore().length;x++)//计算总分
                sum+=sub.getScore()[x];
                }
            else sum=-2;//未批改
            //sum存放到数组中 判断stu quiz是否存在
            if (stu_location==-1)
            {
                if (quiz_location==-1)
                    score[i][j]=sum;
                else
                score[i][quiz_location]=sum;
            }
            else
            {
                if (quiz_location==-1)
                    score[stu_location][j]=sum;
                else
                score[stu_location][quiz_location]=sum;
            }
            }//循环list-submit完毕
            //查找是否有-1 学生未提交
            for (int x=0;x<student.size();x++) {
                for (int y = 0; y < quizzes.size(); y++)
                {
                    if (score[x][y]==-1)//判断quiz里面有没有这个quiz student里面有没有这个student
                  {
                    if (!quizByOrder.contains(quizzes.get(y)))
                            quizByOrder.add(quizzes.get(y));
                    if (!studentByOrder.contains(student.get(x)))
                        studentByOrder.add(student.get(x));
                  }
                }
            }
            List<QuizDTO2> quiz=new ArrayList<QuizDTO2>();
            List<MatrixUserDTO> stu=new ArrayList<MatrixUserDTO>();
            for (Quiz q:quizByOrder)
                quiz.add(quizRepository.findBy_id(q.get_id()));
            for (MatrixUser s:studentByOrder)
                stu.add(userRepository.findBy_id(s.get_id()));
            data.put("quizList",quiz);
            data.put("studentList",stu);
            data.put("scores",score);
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
            //下面开始提交错题（没测过非主观题）
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
                        //加错题
                        wrongproblems.add(problem);
                }
            }
            textBookService.addProblemInTextbook(textBook,_id,lessonId,wrongproblems);

        }catch (Exception e) {
            if(e instanceof MatrixBaseException) throw e;
            throw new ServerInternalException(e);
        }
    }

}
