package com.cyprinus.matrix.controller;


import com.cyprinus.matrix.annotation.MustLogin;
import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.entity.Label;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.service.LabelService;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import com.cyprinus.matrix.type.ResEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/label")
public class LabelController {

    private final LabelService labelService;

    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createLabel(MatrixHttpServletRequestWrapper matrixHttpServletRequestWrapper, @RequestBody Label label) throws ServerInternalException, ForbiddenException, BadRequestException {
        if (label.isBase() && !"manager".equals(matrixHttpServletRequestWrapper.getTokenInfo().getRole()))
            throw new ForbiddenException("只有管理员可以创建基标签！");
        HashMap<String, Object> result = new HashMap<>();
        result.put("label", labelService.createLabel(label));
        return new ResEntity(HttpStatus.OK, "创建成功！", result).getResponse();
    }

    @MustLogin
    @Permission(Permission.Privilege.NOT_STUDENT)
    @RequestMapping(path = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllLabels(MatrixHttpServletRequestWrapper request, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "20") int size) throws ServerInternalException {
        HashMap<String, Object> result = new HashMap<>();
        result.put("labels", labelService.getAllLabels(page, size));
        return new ResEntity(HttpStatus.OK, "查询成功！", result).getResponse();
    }

}
