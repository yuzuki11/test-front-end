package com.cyprinus.matrix.entity;

import org.apache.catalina.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Table(name = "Lesson")
@Entity
public class Lesson implements Serializable {

    @Id
    @GenericGenerator(name="MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    private String _id;

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

    @Column(name = "students")
    @ElementCollection(targetClass = MatrixUser.class)
    private List<MatrixUser> students;

    @Column(name = "term")
    private String term;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    @ManyToMany
    @JoinColumn(name = "lessons_s")
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
