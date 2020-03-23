package com.cyprinus.matrix.entity;

import javax.persistence.*;
import java.io.Serializable;


@Table(name = "Lesson")
@Entity
public class Lesson implements Serializable {

    @Id
    @GeneratedValue
    private String _id;

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

    /*
                students: [{
        type: Schema.Types.ObjectId,
                ref: 'MatrixUser',
    }],

        //开课学期
        term: String,
    }*/
}
