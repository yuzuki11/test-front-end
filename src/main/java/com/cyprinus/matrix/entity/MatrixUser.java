package com.cyprinus.matrix.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.List;

@Table(name = "MatrixUser")
@Entity
public class MatrixUser implements Serializable {

    @Id
    @GenericGenerator(name="MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    private String _id;

    //学号/工号
    @Column(name = "userId", unique = true)
    private String userId;

    //角色
    @Column(name = "role", unique = true, nullable = false, length = 20)
    private String role;

    //密码
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
    @OneToMany
    @Column(name = "lessons_t")
    //@ElementCollection(targetClass = Lesson.class)
    private List<Lesson> lessons_t;

    //所选课程
    @ManyToMany
    @Column(name = "lessons_s")
    //@ElementCollection(targetClass = Lesson.class)
    private List<Lesson> lessons_s;

    //@OneToMany
    //@JoinColumn(name = "teacher")
    public List<Lesson> getLessons_t() {
        return lessons_t;
    }

    public void setLessons_t(List<Lesson> lessons_t) {
        this.lessons_t = lessons_t;
    }

    //@ManyToMany
    //@JoinColumn(name = "students")
    public List<Lesson> getLessons_s() {
        return lessons_s;
    }

    public void setLessons_s(List<Lesson> lessons_s) {
        this.lessons_s = lessons_s;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
