package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.service.LessonService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @MustLogin
    @Permission(Permission.Privilege.MUST_TEACHER)
    @RequestMapping(path = "/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createLesson(MatrixHttpServletRequestWrapper request, @RequestBody Lesson lesson) throws BadRequestException {
        lessonService.createLesson(request.getTokenInfo().get_id(), lesson);
        return new ResEntity(HttpStatus.OK, "创建成功！").getResponse();
    }

}