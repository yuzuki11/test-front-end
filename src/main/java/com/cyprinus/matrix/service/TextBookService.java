package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.Problem;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.TextBook;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.*;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.BCrypt;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.ObjectUtil;
import org.hibernate.dialect.Ingres9Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.xml.soap.Text;
import java.util.*;
@Service
public class TextBookService {

    private final TextBookRepository textBookRepository;
    private final LessonRepository lessonRepository;
    private final MatrixUserRepository matrixUserRepository;


    //private final JwtUtil jwtUtil;

    //  private final ObjectUtil objectUtil;


    @Autowired
    public TextBookService(TextBookRepository textBookRepository, LessonRepository lessonRepository, MatrixUserRepository matrixUserRepository) {
        this.textBookRepository = textBookRepository;
        this.lessonRepository = lessonRepository;
        this.matrixUserRepository = matrixUserRepository;
        //this.jwtUtil = jwtUtil;
    }

    @Transactional(rollbackOn = Throwable.class)
    public List<Problem> getTextbook(String userId, String lessonId, int page, int size) throws ServerInternalException {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            MatrixUser student = matrixUserRepository.getOne(userId);
            Lesson lesson = lessonRepository.getOne(lessonId);
            TextBook textbook = textBookRepository.findByLessonAndStudent(lesson, student);
            List<Problem> problem = textbook.getProblems();
            List<Problem> problemPage = new ArrayList<Problem>();
            int currIdx = (page > 1 ? (page - 1) * size : 0);
            for (int i = 0; i < size && i < problem.size() - currIdx; i++) {
                Problem temp = problem.get(currIdx + i);
                problemPage.add(temp);
            }
            return problemPage;
        } catch (Exception e) {
            throw new ServerInternalException(e.getMessage());
        }
    }
    public long getTextbooknum(String userId, String lessonId) throws ServerInternalException {
        try {
            MatrixUser student = matrixUserRepository.getOne(userId);
            Lesson lesson = lessonRepository.getOne(lessonId);
            TextBook textbook = textBookRepository.findByLessonAndStudent(lesson, student);
            List<Problem> problem = textbook.getProblems();
            return problem.size();
        } catch (Exception e) {
            throw new ServerInternalException(e.getMessage());
        }
    }


    public void addProblemInTextbook(TextBook textBook, String userId, String lessonId, List<Problem> problem) throws ServerInternalException {
        try {
            //康康他是不是已经有textbook了&&problem是否重复
            MatrixUser student = matrixUserRepository.getOne(userId);
            Lesson lesson = lessonRepository.getOne(lessonId);

            if ((textBookRepository.findByStudent(student))!=null)//学生的错题本存在
            {
                textBook=textBookRepository.findByStudent(student);
                List<Problem> oldProblems=textBook.getProblems();
                    //查重
                for (Problem pro : problem) {
                    if (oldProblems.contains(pro))
                        oldProblems.remove(pro);
                }
                        problem.addAll(oldProblems);
            }
            textBook.setLesson(lesson);
            textBook.setStudent(student);
            textBook.setProblems(problem);

            textBookRepository.save(textBook);
        } catch (Exception e) {
            System.out.println(e);
            throw new ServerInternalException(e.getMessage());

        }
    }
}
