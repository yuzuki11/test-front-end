package com.cyprinus.matrix.service;


import com.cyprinus.matrix.entity.Label;
import com.cyprinus.matrix.entity.Problem;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.repository.LabelRepository;
import com.cyprinus.matrix.repository.ProblemRepository;
import com.cyprinus.matrix.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class ProblemService {

    private final
    LabelRepository labelRepository;

    private final
    ProblemRepository problemRepository;

    private final
    ObjectUtil objectUtil;

    @Autowired
    public ProblemService(ProblemRepository problemRepository, LabelRepository labelRepository, ObjectUtil objectUtil) {
        this.problemRepository = problemRepository;
        this.labelRepository = labelRepository;
        this.objectUtil = objectUtil;
    }

    @Transactional(rollbackOn = Throwable.class)
    public void createProblem(HashMap<String, Object> content) throws BadRequestException, JsonProcessingException {
        try {
            content.put("labels", labelRepository.findAllById((List<String>) content.get("labels")));
            int baseNum = 0;
            Label baseLabel = null;
            Problem problem = objectUtil.map2object(content, Problem.class);
            problemRepository.saveAndFlush(problem);
            for (Label label : (List<Label>) content.get("labels")) {
                if (label.isBase()) {
                    baseNum++;
                    baseLabel = label;
                }
                label.addProblem(problem);
                if (baseNum > 1) throw new BadRequestException("传入过多基标签！");
                labelRepository.saveAndFlush(label);
            }
            if (baseNum < 1) throw new BadRequestException("至少应有一个基标签！");
            problem.setNum(baseLabel.getAbbr() + String.format("%4d", baseLabel.getProblems().size()).replace(" ", "0"));
            problem.setLabels((List<Label>) content.get("labels"));
            problemRepository.save(problem);
        } catch (Exception e) {
            if (e instanceof BadRequestException) throw e;
            throw new BadRequestException(e);
        }
    }


}
