package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.GenericArrayUserType;
import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;

@Table(name = "Submit")
@TypeDefs({
        @TypeDef(name = "array", typeClass = GenericArrayUserType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class)
})
@Entity
@SQLDelete(sql = "update submit set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Submit extends MatrixBaseEntity {

    //对应学生id，此处注意与学号不同！
    @ManyToOne
    @JoinColumn(name = "student", foreignKey = @ForeignKey)
    private MatrixUser student;

    //对应Quiz的id
    @ManyToOne
    @JoinColumn(name = "quiz", foreignKey = @ForeignKey)
    private Quiz quiz;

    //答案正文
    @Column(name = "content", columnDefinition = "character varying(2048) []")
    @Type(type = "array")
    private String[] content;

    //批阅状态
    @Transient
    private boolean remark;

    //分数
    @Column(name = "score", columnDefinition = "Int[]")
    @Type(type = "int-array")
    private Integer[] score;

    public MatrixUser getStudent() {
        return student;
    }

    public void setStudent(MatrixUser student) {
        this.student = student;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public boolean isRemark() {
        return remark;
    }

    public void setRemark(boolean remark) {
        this.remark = remark;
    }

    public boolean getRemark() {
        return score != null;
    }

    public Integer[] getScore() {
        return score;
    }

    public void setScore(Integer[] score) {
        this.score = score;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

}
