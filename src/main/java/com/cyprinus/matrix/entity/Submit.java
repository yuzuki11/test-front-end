package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.GenericArrayUserType;
import com.cyprinus.matrix.type.MatrixBaseEntity;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import java.util.List;

@Table(name = "Submit")
@TypeDefs({
        @TypeDef(name = "array", typeClass = GenericArrayUserType.class)
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
    private List<String> content;

    //批阅状态
    @Transient
    private boolean remark;

    //评分
    @OneToOne
    @JoinColumn(name = "score")
    private Score score;

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

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

}