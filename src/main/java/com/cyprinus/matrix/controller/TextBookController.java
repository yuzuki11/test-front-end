package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.LessonService;
import com.cyprinus.matrix.service.MatrixUserService;
import com.cyprinus.matrix.service.ProblemService;
import com.cyprinus.matrix.service.TextBookService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cyprinus.matrix.entity.TextBook;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Lesson;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;


@RestController
@RequestMapping("/textbook")
public class TextBookController {


    private final TextBookService textbookService;



    @Autowired
    public TextBookController(TextBookService textbookService) {

        this.textbookService=textbookService;
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_STUDENT)
    @RequestMapping(path = "/{userId}/{lessonId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getTextbooks(MatrixHttpServletRequestWrapper request, @PathVariable String userId, @PathVariable String lessonId,@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int size) throws ServerInternalException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("textbook", textbookService.getTextbook(userId,lessonId,page,size));
        return new ResEntity(HttpStatus.OK, "查询成功！", data).getResponse();
    }

}
