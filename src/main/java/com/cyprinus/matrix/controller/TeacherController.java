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
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
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

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/quiz/{lessonId}/{quizId}/submits", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSubmits(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @PathVariable String quizId, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int size, @RequestParam(required = false, defaultValue = "all") String remark) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        System.out.println(remark);
        data.put("submit", teacherService.getSubmits(request.getTokenInfo().get_id(), lessonId, quizId, page, size, remark));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/quiz/{lessonId}/{quizId}/submits/count", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSubmitsCount(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @PathVariable String quizId, @RequestParam(required = false, defaultValue = "all") String remark) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("count", teacherService.getSubmitsCount(request.getTokenInfo().get_id(), lessonId, quizId, remark));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/quiz/{lessonId}/{quizId}/remark", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity remark(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @PathVariable String quizId, @RequestBody HashMap<String, Object> content) throws ServerInternalException, ForbiddenException {
        teacherService.remark(request.getTokenInfo().get_id(), lessonId, quizId, (String)content.get("submitId"), (ArrayList) content.get("scores"));
        return new ResEntity(HttpStatus.OK, "评分成功！").getResponse();
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/score/{lessonId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getScore(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId) throws ServerInternalException {
        HashMap<String, Object> data = teacherService.getScore(request.getTokenInfo().get_id(), lessonId);
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
