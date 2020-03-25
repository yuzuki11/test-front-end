package com.cyprinus.matrix.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "Label")
@Entity
@SQLDelete(sql = "update label set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Label implements Serializable {

    @Id
    @GenericGenerator(name = "MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    private String _id;

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
