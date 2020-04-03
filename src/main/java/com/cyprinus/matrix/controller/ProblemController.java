package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.service.ProblemService;
import com.cyprinus.matrix.type.ResEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/problem")
public class ProblemController {

    private final ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @RequestMapping(path = "/old", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createProblem(@RequestBody HashMap<String, Object> content) throws BadRequestException, JsonProcessingException {
        problemService.createProblem(content);
        return new ResEntity(HttpStatus.OK, "创建成功！").getResponse();
    }

}
