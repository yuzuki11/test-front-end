package com.cyprinus.matrix.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "MatrixUser")
@Entity
public class MatrixUser implements Serializable {

    @Id
    @GeneratedValue
    private String _id;

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
    private String email;

    //所选课程
    @Column(name = "lessons")
    @ElementCollection(targetClass = Lesson.class)
    private List<Lesson> lessons;

    @OneToMany
    @JoinColumn(name = "teacher")
    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
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
}
