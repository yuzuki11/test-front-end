package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.Problem;
import com.cyprinus.matrix.entity.TextBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextBookRepository extends JpaRepository<TextBook, String> {
    //TextBook findByLessonAndStudent(Lesson lesson,MatrixUser student,Pageable pageable);

    TextBook findByLessonAndStudent(Lesson lesson,MatrixUser student);
    TextBook findByStudent(MatrixUser student);

}
