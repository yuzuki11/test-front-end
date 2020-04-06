package com.cyprinus.matrix.controller;


import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

//TODO 有一些Service层异常无法被捕获，后期要重构异常处理机制，为防止出现合并冲突，应在其他模块基本完成之后处理本TODO
@ControllerAdvice(annotations = Controller.class)
public class ExceptionController {

    @ExceptionHandler(ServerInternalException.class)
    public ResponseEntity handleServerInternal(ServerInternalException e, MatrixHttpServletRequestWrapper request) {
        return new ResEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getFatal()).getResponse();
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handleForbidden(ForbiddenException e, MatrixHttpServletRequestWrapper request) {
        return new ResEntity(HttpStatus.FORBIDDEN, e.getMessage(), e.getFatal()).getResponse();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequest(BadRequestException e, MatrixHttpServletRequestWrapper request) {
        return new ResEntity(HttpStatus.BAD_REQUEST, e.getMessage(), e.getFatal()).getResponse();
    }
}
