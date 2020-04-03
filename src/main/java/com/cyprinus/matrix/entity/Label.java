package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "Label")
@Entity
@SQLDelete(sql = "update label set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Label extends MatrixBaseEntity {

    //是否是科目标签
    private boolean base;

    //科目缩写
    @JsonIgnore
    @Column(name = "abbr")
    private  String abbr;

    //标签名称
    @Column(name = "name",unique = true)
    private String name;

    //包含的问题
    @JsonIgnore
    @ManyToMany
    @Column(name = "problems")
    private List<Problem> problems;

    //包含的quiz
    @JsonIgnore
    @ManyToMany
    @Column(name = "quizzes")
    private List<Quiz>quizzes;

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public void addProblem(Problem problem) {
        this.problems.add(problem);
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

}
