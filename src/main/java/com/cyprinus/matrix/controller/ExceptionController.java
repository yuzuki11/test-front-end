package com.cyprinus.matrix.controller;


import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionController {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handleForbidden(ForbiddenException e, MatrixHttpServletRequestWrapper request) {
        return new ResEntity(HttpStatus.FORBIDDEN, e.getMessage(), null).getResponse();
    }
}
