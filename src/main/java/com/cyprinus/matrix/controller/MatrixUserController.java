package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class MatrixUserController {

    private final
    MatrixUserService matrixUserService;

    @Autowired
    public MatrixUserController(MatrixUserService matrixUserService) {
        this.matrixUserService = matrixUserService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity login(@RequestBody MatrixUser matrixUser) {
        try {
            Map<String, Object> data = matrixUserService.loginCheck(matrixUser);
            return new ResEntity(HttpStatus.OK, "登录成功！", data).getResponse();
        } catch (Exception e) {
            return new ResEntity(HttpStatus.FORBIDDEN, e.getMessage(), null).getResponse();
        }
    }

}
