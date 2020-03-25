package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;


@Table(name = "Lesson")
@Entity
@SQLDelete(sql = "update lesson set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Lesson extends MatrixBaseEntity {

    //选课编号
    @Column(name = "lessonNum")
    private Integer lessonNum;

    //组班号
    @Column(name = "classNum")
    private String classNum;


    //任课教师
    @ManyToOne
    @JoinColumn(name = "teacher")
    private MatrixUser teacher;

    //课程名称
    @Column(name = "name")
    private String name;

    //学生
    @ManyToMany
    @Column(name = "students")
    //@ElementCollection(targetClass = MatrixUser.class)
    private List<MatrixUser> students;

    //学期
    @Column(name = "term")
    private String term;

    public Integer getLessonNum() {
        return lessonNum;
    }

    public void setLessonNum(Integer lessonNum) {
        this.lessonNum = lessonNum;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MatrixUser getTeacher() {
        return teacher;
    }

    public void setTeacher(MatrixUser teacher) {
        this.teacher = teacher;
    }

    public List<MatrixUser> getStudents() {
        return students;
    }

    //@ManyToMany
    //@JoinColumn(name = "lessons_s")
    public void setStudents(List<MatrixUser> students) {
        this.students = students;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
