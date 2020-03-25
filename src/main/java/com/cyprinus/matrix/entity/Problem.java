package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "Problem")
@Entity
@SQLDelete(sql = "update problem set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Problem extends MatrixBaseEntity {

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

    //被那些Quiz引用
    @ManyToMany(mappedBy = "problems")
    @Column(name = "quizRefers")
    private List<Quiz> quizRefers;

    /*//答案图片
    AnswerPictures: [{
        type: Schema.Types.ObjectId,
                ref: 'Picture',
    }],*/

    //标签
    @ManyToMany(mappedBy = "problems")
    @Column(name = "labels")
    private List<Label> labels;
}
