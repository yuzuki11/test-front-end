package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.Problem;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.ProblemService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/problem")
public class ProblemController {

    private final ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "/old", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createProblem(MatrixHttpServletRequestWrapper request, @RequestBody HashMap<String, Object> content) throws BadRequestException {
        problemService.createProblem(content);
        return new ResEntity(HttpStatus.OK, "创建成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "/num/{num}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getProblemByNum(MatrixHttpServletRequestWrapper request, @PathVariable String num) throws ServerInternalException {
        Problem problem = new Problem();
        problem.setNum(num);
        HashMap<String, Object> result = new HashMap<>();
        result.put("problems", problemService.getProblem(problem));
        return new ResEntity(HttpStatus.OK, "查询成功！", result).getResponse();
    }

}
