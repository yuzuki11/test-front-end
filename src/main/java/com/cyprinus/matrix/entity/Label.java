package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
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
    private  String abbr;

    //标签名称
    @Column(name = "name",unique = true)
    private String name;

    //包含的问题
    @ManyToMany
    @Column(name = "problems")
    private List<Problem> problems;

    //包含的quiz
    @ManyToMany
    @Column(name = "quizzes")
    private List<Quiz>quizzes;

}
