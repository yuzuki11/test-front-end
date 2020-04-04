package com.cyprinus.matrix.controller;

import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.LessonService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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

    @MustLogin
    @Permission(Permission.Privilege.MUST_MANAGER)
    @RequestMapping(path = "/teacher/{lessonId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateTeacher(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @RequestBody HashMap<String, String> content) throws ServerInternalException, BadRequestException {
        lessonService.updateTeacher(content.get("newTeacher"), lessonId);
        return new ResEntity(HttpStatus.OK, "变更成功！").getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "/{lessonId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateLesson(MatrixHttpServletRequestWrapper request, @PathVariable String lessonId, @RequestBody Lesson content) throws BadRequestException, ForbiddenException {
        if (!request.getTokenInfo().getRole().equals("manager"))
            lessonService.updateLesson(content, lessonId, request.getTokenInfo().get_id());
        else
            lessonService.updateLesson(content, lessonId, null);
        return new ResEntity(HttpStatus.OK, "变更成功！").getResponse();
    }

}
