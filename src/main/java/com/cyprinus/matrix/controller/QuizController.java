package com.cyprinus.matrix.controller;


import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.exception.MatrixBaseException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.service.QuizService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final
    MatrixUserService matrixUserService;

    private final
    QuizService quizService;

    @Autowired
    public QuizController(MatrixUserService matrixUserService, QuizService quizService) {
        this.matrixUserService = matrixUserService;
        this.quizService = quizService;
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/{quizId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getQuiz(MatrixHttpServletRequestWrapper request, @PathVariable String quizId) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("quiz", quizService.getQuiz(quizId));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createQuiz(MatrixHttpServletRequestWrapper request, @RequestBody HashMap<String, Object> content) throws MatrixBaseException {
        quizService.createQuiz(request.getTokenInfo().get_id(), content);
        return new ResEntity(HttpStatus.OK, "添加成功！").getResponse();
    }

    @MustLogin
    @Permission(value = Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/{quizId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity deleteQuiz(MatrixHttpServletRequestWrapper request, @PathVariable String quizId) throws ServerInternalException {
        quizService.deleteQuiz(quizId);
        return new ResEntity(HttpStatus.OK, "删除成功！").getResponse();
    }

}
