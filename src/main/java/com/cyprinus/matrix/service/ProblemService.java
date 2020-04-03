package com.cyprinus.matrix.service;


import com.cyprinus.matrix.entity.Label;
import com.cyprinus.matrix.entity.Problem;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LabelRepository;
import com.cyprinus.matrix.repository.ProblemRepository;
import com.cyprinus.matrix.util.ObjectUtil;
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
    public void createProblem(HashMap<String, Object> content) throws BadRequestException {
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
            if (e instanceof BadRequestException) throw new BadRequestException(e.getMessage());
            throw new BadRequestException(e);
        }
    }

    public List<Problem> getProblemByNum(String problemNum) throws ServerInternalException {
        try {

            return problemRepository.findAllByNumIs(problemNum);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public Problem getProblemById(String problemId) throws ServerInternalException {
        try {
            return problemRepository.getOne(problemId);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public List<Problem> searchProblem(HashMap<String, Object> condition) throws ServerInternalException {
        try {
            List<String> problemType = (List<String>) condition.get("problemTypes");
            List<Label> labels = labelRepository.findAllById((List<String>) condition.get("labels"));
            String query = "%" + condition.get("q") + "%";
            if(labels.size()==0 || problemType.size()==0) {
                if(labels.size() != 0) return problemRepository.findAllByContentLikeAndLabelsIn(query,labels);
                if(problemType.size() != 0) return problemRepository.findAllByContentLikeAndProblemTypeIn(query,problemType);
                return problemRepository.findAllByContentLike(query);
            }
            return problemRepository.findAllByContentLikeAndLabelsInAndProblemTypeIn(query, labels, problemType);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }

    }


}
