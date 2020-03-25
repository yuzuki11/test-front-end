package com.cyprinus.matrix.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "Submit")
@Entity
@SQLDelete(sql = "update submit set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Submit implements Serializable {

    @Id
    @GenericGenerator(name = "MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    private String _id;

    //对应学生id，此处注意与学号不同！
    @ManyToOne
    @JoinColumn(name = "student", foreignKey = @ForeignKey)
    private MatrixUser student;

    //对应Quiz的id
    @ManyToOne
    @JoinColumn(name = "quiz", foreignKey = @ForeignKey)
    private Quiz quiz;

    /*//答案正文
    content: [String],*/

    //批阅状态
    @Transient
    private boolean remark;

    //评分
    @OneToOne
    @JoinColumn(name = "score")
    private Score score;


}
