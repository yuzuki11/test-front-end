package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.dto.QuizDTO;
import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.StudentService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/lessons", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getLessons(MatrixHttpServletRequestWrapper request) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("lessons", studentService.getLessons(request.getTokenInfo().get_id()));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/quiz/{lessonId}/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuizzesAll(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId) throws ServerInternalException, ForbiddenException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("quizzes", studentService.getQuizzesAll(request.getTokenInfo().get_id(), lessonId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/quiz/{lessonId}/apart", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuizzesApart(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId) throws ServerInternalException, ForbiddenException {
        HashMap<String, Object> data = new HashMap<>();
        HashMap<String, List<QuizDTO>> quizzesApart = (studentService.getQuizzesApart(request.getTokenInfo().get_id(), lessonId));
        Set<String> set = quizzesApart.keySet();
        for (String string : set) {
            data.put(string, quizzesApart.get(string));
        }
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/quiz/{lessonId}/{quizId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuizInfo(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @PathVariable String quizId) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("quiz", studentService.getQuizInfo(request.getTokenInfo().get_id(), lessonId, quizId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }
}
