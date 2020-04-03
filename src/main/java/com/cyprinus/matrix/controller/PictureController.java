package com.cyprinus.matrix.controller;


import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.PictureService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
@RequestMapping("/picture")
public class PictureController {

    private final
    PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }


    //@MustLogin
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseEntity uploadNewPicture(MatrixHttpServletRequestWrapper request, @RequestParam("file") MultipartFile file) throws BadRequestException, ServerInternalException {
        HashMap<String, Object> picture = pictureService.uploadNewPicture(file);
        return new ResEntity(HttpStatus.OK, "上传成功!", picture).getResponse();

    }

}
