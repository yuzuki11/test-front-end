package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Set;

@Table(name = "TextBook")
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@SQLDelete(sql = "update textbook set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class TextBook extends MatrixBaseEntity {

    @ManyToOne
    @JoinColumn(name = "student", foreignKey = @ForeignKey)
    private MatrixUser student;

    @ManyToMany
    @JoinColumn(name = "Problem", foreignKey = @ForeignKey)
    private List<Problem> problem;

    //对应课程id
    @ManyToOne
    @JoinColumn(name = "lesson", foreignKey = @ForeignKey)
    private Lesson lesson;

    public MatrixUser getStudent() {
        return student;
    }

    public List<Problem> getProblems() { return problem; }

    public Lesson getLesson() {
        return lesson;
    }

    public void setStudent(MatrixUser student) {
        this.student = student;
    }

    public void setProblems(List<Problem> problem) {
        this.problem = problem;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

}
