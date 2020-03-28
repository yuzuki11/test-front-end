package com.cyprinus.matrix.type;

import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResEntity {
    private ResponseEntity<Map<String, Object>> response;

    public ResEntity(HttpStatus status, String msg, Map<String, Object> data, Boolean fatal) {
        Map<String, Object> body = data;
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("success", (status == HttpStatus.OK));
        defaultData.put("msg", msg);
        //noinspection DoubleNegation
        defaultData.put("fatal", fatal == null ? false : fatal);
        if (body != null) body.putAll(defaultData);
        else body = defaultData;
        this.response = new ResponseEntity<>(body, status);

    }

    public ResEntity(HttpStatus status, String msg, Map<String, Object> data) {
        Map<String, Object> body = data;
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("success", (status == HttpStatus.OK));
        defaultData.put("msg", msg);
        if (body != null) body.putAll(defaultData);
        else body = defaultData;
        this.response = new ResponseEntity<>(body, status);

    }

    public ResponseEntity<Map<String, Object>> getResponse() {
        return response;
    }
}
