package com.cyprinus.matrix.controller;


import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.service.TeacherService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private final
    MatrixUserService matrixUserService;

    private final
    TeacherService teacherService;

    @Autowired
    public TeacherController(MatrixUserService matrixUserService, TeacherService teacherService) {
        this.matrixUserService = matrixUserService;
        this.teacherService = teacherService;
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/lessons", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getLessons(MatrixHttpServletRequestWrapper request) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("lessons", teacherService.getLessons(request.getTokenInfo().get_id()));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/quiz/{lessonId}/apart", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuizzes(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId) throws ServerInternalException {
        HashMap<String, Object> data =  teacherService.getQuizzes(request.getTokenInfo().get_id(), lessonId);
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    //TODO 尚未测试 待完成添加学生之后再测
    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/{lessonId}/{userId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity removeStudentFromLesson(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @PathVariable String userId) throws ForbiddenException, ServerInternalException {
        matrixUserService.removeStudentFromLesson(lessonId, userId, request.getTokenInfo().get_id());
        return new ResEntity(HttpStatus.OK, "删除成功！").getResponse();
    }

}
