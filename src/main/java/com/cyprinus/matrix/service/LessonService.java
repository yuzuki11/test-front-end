package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class LessonService {


    private final
    MatrixUserRepository userRepository;

    private final
    LessonRepository lessonRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository, MatrixUserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }


    @Transactional(rollbackOn = Throwable.class)
    public void createLesson(String _id, Lesson lesson) throws BadRequestException {
        try {
            MatrixUser matrixUser = userRepository.getOne(_id);
            lesson.setTeacher(matrixUser);
            lessonRepository.saveAndFlush(lesson);
            Set<Lesson> tmp = matrixUser.getLessons_t();
            tmp.add(lesson);
            matrixUser.setLessons_t(tmp);
            userRepository.save(matrixUser);
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

    @Transactional(rollbackOn = Throwable.class)
    public void updateTeacher(String newTeacherId, String lessonId) throws ServerInternalException, BadRequestException {
        try {
            MatrixUser newTeacher = userRepository.getOne(newTeacherId);
            Lesson lesson = lessonRepository.getOne(lessonId);
            MatrixUser oldTeacher = lesson.getTeacher();
            if (oldTeacher.equals(newTeacher)) return;
            if (newTeacher == null || !"teacher".equals(newTeacher.getRole()))
                throw new BadRequestException("目标用户不存在或身份不合法！");
            oldTeacher.removeLesson(lesson);
            newTeacher.addLesson(lesson);
            lesson.setTeacher(newTeacher);
            lessonRepository.save(lesson);
            userRepository.save(oldTeacher);
            userRepository.save(newTeacher);
        } catch (Exception e) {
            if (e instanceof BadRequestException) throw e;
            throw new ServerInternalException(e);
        }

    }

}
