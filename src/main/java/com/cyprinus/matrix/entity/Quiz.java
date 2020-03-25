package com.cyprinus.matrix.entity;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Future;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name = "Quiz")
@TypeDefs({
        @TypeDef(name = "int-array", typeClass = IntArrayType.class)
})
@Entity
@SQLDelete(sql = "update demo set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Quiz implements Serializable {

    @Id
    @GenericGenerator(name = "MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    private String _id;

    //对应课程id
    @ManyToOne
    private Lesson lesson;

    @JoinColumn(name = "deleted")
    private Integer deleted = 0;

    //问题
    @ManyToMany
    @Column(name = "problems")
    //@ElementCollection(targetClass = Problem.class)
    private List<Problem> problems;

    /*//标签
    labels: [{
        type: Schema.Types.ObjectId,
                ref: 'Label',
    }],*/

    //标题
    @Column(name = "title")
    private String title;

    //问题分值
    @Column(name = "points", columnDefinition = "Int[]")
    @Type(type = "int-array")
    private List<Integer> points;

    //开始时间
    @Column(name = "startTime")
    private Date startTime;

    //截止时间
    @Future
    @Column(name = "deadline")
    private Date deadline;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public List<Integer> getPoints() {
        return points;
    }

    public void setPoints(List<Integer> points) {
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
