package com.cyprinus.matrix.type;

import io.jsonwebtoken.Claims;

import java.io.Serializable;

public class MatrixTokenInfo implements Serializable {

    private String _id;

    private String userId;

    private String todo;

    private String sid;

    private String role;

    private Claims raw;

    public MatrixTokenInfo(Claims claims) {
        this.raw = claims;
        this.sid = claims.getId();
        this.todo = claims.getSubject();
        this.userId = claims.getAudience();
        this.role = (String) claims.get("role");
        this._id = (String) claims.get("_id");
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Claims getRaw() {
        return raw;
    }

    public void setRaw(Claims raw) {
        this.raw = raw;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String get_id() {
        return _id;
    }

}
