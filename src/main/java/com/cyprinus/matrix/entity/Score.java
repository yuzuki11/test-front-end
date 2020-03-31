package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.*;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import java.util.List;

@Table(name = "Score")
@TypeDefs({
        @TypeDef(name = "int-array", typeClass = IntArrayType.class)
})
@Entity
@SQLDelete(sql = "update score set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Score extends MatrixBaseEntity {

    //对应的quiz
    @ManyToOne
    @JoinColumn(name = "quiz", foreignKey = @ForeignKey)
    private Quiz quiz;

    //对应的学生
    @ManyToOne
    @JoinColumn(name = "student", foreignKey = @ForeignKey)
    private MatrixUser student;

    //分数
    @Column(name = "score", columnDefinition = "Int[]")
    @Type(type = "int-array")
    private List<Integer> score;

    //原答案
    @OneToOne(mappedBy = "score")
    @JoinColumn(name = "submit")
    private Submit submit;

    //总分
    @Transient
    private Integer total = 0;

    @PostConstruct
    void calc_total(){
        total = score.stream().mapToInt(Integer::intValue).sum();
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public MatrixUser getStudent() {
        return student;
    }

    public void setStudent(MatrixUser student) {
        this.student = student;
    }

    public List<Integer> getScore() {
        return score;
    }

    public void setScore(List<Integer> score) {
        this.score = score;
    }

    public Submit getSubmit() {
        return submit;
    }

    public void setSubmit(Submit submit) {
        this.submit = submit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}