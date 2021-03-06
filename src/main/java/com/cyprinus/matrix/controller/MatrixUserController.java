package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.*;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import com.cyprinus.matrix.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/user")
public class MatrixUserController {

    private final
    ObjectUtil objectUtil;

    private final
    MatrixUserService matrixUserService;

    @Autowired
    public MatrixUserController(MatrixUserService matrixUserService, ObjectUtil objectUtil) {
        this.matrixUserService = matrixUserService;
        this.objectUtil = objectUtil;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity login(MatrixHttpServletRequestWrapper request, @RequestBody HashMap<String, Object> matrixUser) throws UnauthorizedException, ServerInternalException {
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
    public ResponseEntity putPwd(MatrixHttpServletRequestWrapper request, @RequestBody Map<String, String> content) throws ForbiddenException, ServerInternalException, JsonProcessingException {
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

    @MustLogin
    @RequestMapping(path = "/me", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity putSelfProfile(MatrixHttpServletRequestWrapper request, @RequestBody MatrixUser content) throws ServerInternalException {
        matrixUserService.putProfile(content, request.getTokenInfo().get_id());
        return new ResEntity(HttpStatus.OK, "修改成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "/{userId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity putStudentProfile(MatrixHttpServletRequestWrapper request, @RequestBody MatrixUser content, @PathVariable String userId) throws ServerInternalException {
        matrixUserService.putProfile(content, userId);
        return new ResEntity(HttpStatus.OK, "修改成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_MANAGER)
    @RequestMapping(path = "/admin/teacher/{userId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity deleteTeacher(MatrixHttpServletRequestWrapper request, @PathVariable String userId) throws ServerInternalException, BadRequestException {
        matrixUserService.deleteTeacher(userId);
        return new ResEntity(HttpStatus.OK, "删除成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/student", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addStudents(MatrixHttpServletRequestWrapper request, @RequestBody HashMap<String, Object> content) throws MatrixBaseException {
        String lessonId = (String) content.get("lessonId");
        List<HashMap> students = (List<HashMap>) content.get("students");
        matrixUserService.addStudents(lessonId, request.getTokenInfo().get_id(), students);
        return new ResEntity(HttpStatus.OK, "导入成功！").getResponse();
    }

    @RequestMapping(path = "/verify", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity verifyOperate(MatrixHttpServletRequestWrapper request, @RequestParam String key, @RequestParam String token) throws NotFoundException, ServerInternalException, JsonProcessingException {
        if (key == null || token == null) throw new NotFoundException();
        matrixUserService.verifyOperate(key, token);
        return new ResEntity(HttpStatus.OK, "操作成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "/students", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllStudents(MatrixHttpServletRequestWrapper request,@RequestParam(required = false) String lessonId,@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int size) throws ServerInternalException {
        return new ResEntity(HttpStatus.OK, "查询成功！", matrixUserService.getAllStudents(lessonId, page, size)).getResponse();
    }


    @MustLogin
    @Permission(Permission.Privilege.MUST_MANAGER)
    @RequestMapping(path = "/count/{role}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, Object>> getMatrixUserCount(MatrixHttpServletRequestWrapper request, @PathVariable String role)throws ServerInternalException {
        MatrixUser matrixUser = new MatrixUser();
        HashMap<String, Object> result= new HashMap<>();
        result.put("count",matrixUserService.getMatrixUserCount(matrixUser,role));
            return new ResEntity(HttpStatus.OK, "查询成功！", result).getResponse();

    }


}
