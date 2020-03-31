package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class MatrixUserController {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final
    MatrixUserService matrixUserService;

    @Autowired
    public MatrixUserController(MatrixUserService matrixUserService) {
        this.matrixUserService = matrixUserService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity login(MatrixHttpServletRequestWrapper request, @RequestBody HashMap<String, Object> matrixUser) throws ForbiddenException, ServerInternalException {
        Map<String, Object> data = matrixUserService.loginCheck(matrixUser);
        return new ResEntity(HttpStatus.OK, "登录成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_MANAGER)
    @RequestMapping(path = "/teacher", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createTeacher(MatrixHttpServletRequestWrapper request, @RequestBody MatrixUser matrixUser) throws BadRequestException {
        matrixUser.setRole("teacher");
        matrixUserService.createUser(matrixUser);
        return new ResEntity(HttpStatus.OK, "教师账户创建成功！").getResponse();
    }

    @MustLogin
    @RequestMapping(path = "/pwd", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity putPwd(MatrixHttpServletRequestWrapper request, @RequestBody Map<String, String> content) throws BadRequestException {
        matrixUserService.putPwd(request.getTokenInfo().get_id(), content.get("password"), content.get("old"));
        return new ResEntity(HttpStatus.OK, "修改成功！").getResponse();
    }

    @MustLogin
    @RequestMapping(path = "/me", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSelfProfile(MatrixHttpServletRequestWrapper request) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("inf", matrixUserService.getProfile(request.getTokenInfo().get_id()));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_MANAGER)
    @RequestMapping(path = "/teachers", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllTeachers(MatrixHttpServletRequestWrapper request, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int size) throws ServerInternalException {
        MatrixUser matrixUser = new MatrixUser();
        matrixUser.setRole("teacher");
        HashMap<String, Object> result = new HashMap<>();
        result.put("teachers", matrixUserService.getManyProfiles(matrixUser, page, size));
        return new ResEntity(HttpStatus.OK, "查询成功！", result).getResponse();
    }

}
