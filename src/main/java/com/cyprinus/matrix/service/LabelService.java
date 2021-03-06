package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Label;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LabelRepository;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
public class LabelService {

    private final ObjectUtil objectUtil;

    private final LabelRepository labelRepository;

    @Autowired
    public LabelService(LabelRepository labelRepository, ObjectUtil objectUtil) {
        this.labelRepository = labelRepository;
        this.objectUtil = objectUtil;
    }

    public Label createLabel(Label label) throws ServerInternalException, BadRequestException {
        try {
            if(label.isBase() && (label.getAbbr() == null || label.getAbbr().isEmpty())) throw new BadRequestException("基标签必须具有缩写！");
            labelRepository.saveAndFlush(label);
            return label;
        } catch (Exception e) {
            if(e instanceof BadRequestException) throw e;
            throw new ServerInternalException(e);
        }
    }

    public List<Label> getAllLabels(int page, int size) throws ServerInternalException {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            return labelRepository.findAll(pageRequest).getContent();
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

}
