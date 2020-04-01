package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class StudentService {
    private final
    MatrixUserRepository userRepository;

    private final
    LessonRepository lessonRepository;

    @Autowired
    public StudentService(LessonRepository lessonRepository, MatrixUserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackOn = Throwable.class)
    public Set<Lesson> getLessons(String _id) throws ServerInternalException {
        try {
            MatrixUser student = userRepository.getOne(_id);
            Set<Lesson> lessons = student.getLessons_s();
            return lessons;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }
}
