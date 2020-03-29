package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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


}
