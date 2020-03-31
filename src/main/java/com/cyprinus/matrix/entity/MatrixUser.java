package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Set;

@Table(name = "MatrixUser")
@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
@SQLDelete(sql = "update matrix_user set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class MatrixUser extends MatrixBaseEntity {

    //学号/工号
    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    //角色
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    //密码
    @JsonIgnore
    @Column(name = "password")
    private String password;

    //姓名
    @Column(name = "name", nullable = false)
    private String name;

    //其他
    @Column(name = "others")
    private String others;

    //头像
    @Column(name = "avatar")
    private String avatar;

    //电子邮箱
    @Column(name = "email")
    @Email
    private String email;

    //所教课程
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @Column(name = "lessons_t")
    //@ElementCollection(targetClass = Lesson.class)
    private Set<Lesson> lessons_t;

    //所选课程
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @Column(name = "lessons_s")
    //@ElementCollection(targetClass = Lesson.class)
    private Set<Lesson> lessons_s;

    public Set<Lesson> getLessons() {
        if ("student".equals(this.role))
            return this.lessons_s;
        if ("teacher".equals(this.role))
            return this.lessons_t;
        else return null;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Transient
    private Set<Lesson> lessons;

    //@OneToMany
    //@JoinColumn(name = "teacher")
    public Set<Lesson> getLessons_t() {
        return lessons_t;
    }

    public void setLessons_t(Set<Lesson> lessons_t) {
        this.lessons_t = lessons_t;
    }

    //@ManyToMany
    //@JoinColumn(name = "students")
    public Set<Lesson> getLessons_s() {
        return lessons_s;
    }

    public void setLessons_s(Set<Lesson> lessons_s) {
        this.lessons_s = lessons_s;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
