package com.cyprinus.matrix.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "Problem")
@Entity
public class Problem implements Serializable {

    @Id
    @GenericGenerator(name = "MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    private String _id;

    //题目序号
    @Column(name = "num")
    private String num;

    //内容
    @Column(name = "content", length = 1024)
    private String content;

    /*//问题图片
    ProblemPictures: [{
        type: Schema.Types.ObjectId,
                ref: 'Picture',
    }],*/

    //题目类型
    @Column(name = "problemType")
    private String problemType;

    //是否为客观题
    @Column(name = "ifObjective")
    private Boolean ifObjective;

    //答案
    @Column(name = "answer", length = 1024)
    private String answer;

    //建议分值
    @Column(name = "point")
    private Integer point;


    /*//答案图片
    AnswerPictures: [{
        type: Schema.Types.ObjectId,
                ref: 'Picture',
    }],*/

    /*//标签
    labels: [{
        type: Schema.Types.ObjectId,
                ref: 'Label',
    }],*/
}
