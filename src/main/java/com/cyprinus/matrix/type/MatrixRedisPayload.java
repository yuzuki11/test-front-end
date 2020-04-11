package com.cyprinus.matrix.type;

import java.io.Serializable;

public class MatrixRedisPayload implements Serializable {

    private String userId;

    private String todo;

    private String value;

    private String token;

    public MatrixRedisPayload() {
    }

    public MatrixRedisPayload(String userId, String todo, String value, String token) {
        this.userId = userId;
        this.todo = todo;
        this.value = value;
        this.token = token;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
