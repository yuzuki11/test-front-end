package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.StudentService;
import com.cyprinus.matrix.service.TextBookService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    private final TextBookService textbookService;

    @Autowired
    public StudentController(StudentService studentService,TextBookService textbookService) {
        this.studentService = studentService;
        this.textbookService=textbookService;
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
    public ResponseEntity getQuizzesAll(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("quizzes", studentService.getQuizzesAll(request.getTokenInfo().get_id(), lessonId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/quiz/{lessonId}/apart", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuizzesApart(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId) throws ServerInternalException {
        HashMap<String, Object> data = (studentService.getQuizzesApart(request.getTokenInfo().get_id(), lessonId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/quiz/{lessonId}/{quizId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuizInfo(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @PathVariable String quizId) throws ServerInternalException, EntityNotFoundException,ForbiddenException {
        HashMap<String, Object> data = studentService.getQuizInfo(request.getTokenInfo().get_id(), lessonId, quizId);
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/quiz/{lessonId}/{quizId}/answer", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity submitAnswer(MatrixHttpServletRequestWrapper request, @RequestBody HashMap<String, String[]> content, @PathVariable String lessonId, @PathVariable String quizId) throws BadRequestException, ForbiddenException, ServerInternalException {
        studentService.submitAnswer(request.getTokenInfo().get_id(), lessonId, quizId, content.get("content"));
        return new ResEntity(HttpStatus.OK, "提交成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/score/quiz/{quizId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getScore(MatrixHttpServletRequestWrapper request, @PathVariable String quizId) throws ServerInternalException, EntityNotFoundException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("scores", studentService.getScore(request.getTokenInfo().get_id(), quizId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/textbook/{lessonId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getTextbooks(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId,@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int size) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("textbook", textbookService.getTextbook(request.getTokenInfo().get_id(),lessonId,page,size));
        data.put("total",textbookService.getTextbooknum(request.getTokenInfo().get_id(),lessonId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }
}
