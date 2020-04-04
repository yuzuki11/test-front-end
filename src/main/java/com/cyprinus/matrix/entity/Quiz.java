package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.fasterxml.jackson.annotation.*;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Future;
import java.util.Date;
import java.util.List;

@Table(name = "Quiz")
@TypeDefs({
        @TypeDef(name = "int-array", typeClass = IntArrayType.class)
})
@Entity
@SQLDelete(sql = "update quiz set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Quiz extends MatrixBaseEntity {

    //对应课程id
    @ManyToOne
    @JsonBackReference
    private Lesson lesson;

    //问题
    @ManyToMany
    @Column(name = "problems")
    @JsonManagedReference
    //@ElementCollection(targetClass = Problem.class)
    private List<Problem> problems;

    //标签
    @ManyToMany(mappedBy = "quizzes")
    @Column(name = "labels")
    private List<Label> labels;

    //标题
    @Column(name = "title")
    private String title;

    //问题分值
    @Column(name = "points", columnDefinition = "Int[]")
    @Type(type = "int-array")
    private Integer[] points;

    //开始时间
    @Column(name = "startTime")
    private Date startTime;

    //截止时间
    @Future
    @Column(name = "deadline")
    private Date deadline;

    /*public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }*/

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public Integer[] getPoints() {
        return points;
    }

    public void setPoints(Integer[] points) {
        this.points = points;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
