package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.postgresql.util.PSQLException;
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
    public ResponseEntity login(MatrixHttpServletRequestWrapper request, @RequestBody MatrixUser matrixUser) {
        try {
            Map<String, Object> data = matrixUserService.loginCheck(matrixUser);
            return new ResEntity(HttpStatus.OK, "登录成功！", data).getResponse();
        } catch (Exception e) {
            return new ResEntity(HttpStatus.FORBIDDEN, e.getMessage()).getResponse();
        }
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_MANAGER)
    @RequestMapping(path = "/teacher", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createTeacher(MatrixHttpServletRequestWrapper request, @RequestBody MatrixUser matrixUser) throws BadRequestException {
        matrixUser.setRole("teacher");
        if (matrixUserService.createUser(matrixUser))
            return new ResEntity(HttpStatus.OK, "教师账户创建成功！").getResponse();
        else throw new BadRequestException();
    }

}
