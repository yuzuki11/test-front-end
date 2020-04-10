package com.cyprinus.matrix.service;


import com.cyprinus.matrix.entity.Label;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.entity.Picture;
import com.cyprinus.matrix.entity.Problem;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LabelRepository;
import com.cyprinus.matrix.repository.PictureRepository;
import com.cyprinus.matrix.repository.ProblemRepository;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class ProblemService {

    private final
    PictureRepository pictureRepository;

    private final
    LabelRepository labelRepository;

    private final
    ProblemRepository problemRepository;

    private final
    ObjectUtil objectUtil;

    @Autowired
    public ProblemService(ProblemRepository problemRepository, LabelRepository labelRepository, ObjectUtil objectUtil, PictureRepository pictureRepository) {
        this.problemRepository = problemRepository;
        this.labelRepository = labelRepository;
        this.objectUtil = objectUtil;
        this.pictureRepository = pictureRepository;
    }

    @Transactional(rollbackOn = Throwable.class)
    public void createProblem(HashMap<String, Object> content) throws BadRequestException {
        try {
            List<Label> labels = labelRepository.findAllById((List<String>) content.get("labels"));
            int baseNum = 0;
            Label baseLabel = null;
            content.put("labels",null);
            Problem problem = objectUtil.map2object(content, Problem.class);
            problemRepository.saveAndFlush(problem);//刷进数据库用于获取_id
            //处理图片
            if (content.containsKey("pictures")) {
                List<Picture> pictures = pictureRepository.findAllById((Iterable<String>) content.get("pictures"));
                for (Picture picture : pictures) {
                    picture.setOwnedBy(problem.get_id());
                    picture.setUsage("problem");
                }
                pictureRepository.saveAll(pictures);
            }
            //处理标签
            for (Label label : labels) {
                if (label.isBase()) {
                    baseNum++;
                    baseLabel = label;
                }
                label.addProblem(problem);
                if (baseNum > 1) throw new BadRequestException("传入过多基标签！");
            }
            if (baseNum < 1) throw new BadRequestException("至少应有一个基标签！");
            labelRepository.saveAll(labels);
            problem.setNum(baseLabel.getAbbr() + String.format("%04d", baseLabel.getProblems().size()));
            problem.setLabels(labels);
            problemRepository.save(problem);
        } catch (Exception e) {
            if (e instanceof BadRequestException) throw new BadRequestException(e.getMessage());
            throw new BadRequestException(e);
        }
    }

    @Transactional(rollbackOn = Throwable.class)
    public void putProblem(String problemId, HashMap<String, Object> content) throws BadRequestException {
        try {
            ObjectUtil objectUtil = new ObjectUtil();
            Problem problem = problemRepository.getOne(problemId);
            List<Label> labels = labelRepository.findAllById((Iterable<String>) content.get("labels"));
            int baseNum = 0;
            content.put("labels",null);
            Problem new_problem = objectUtil.map2object(content, Problem.class);
            //处理图片
            if (content.containsKey("pictures")) {
                System.out.println(content.get("pictures"));
                List<Picture> pictures = pictureRepository.findAllById((Iterable<String>) content.get("pictures"));
                for (Picture picture : pictures) {
                    picture.setOwnedBy(problem.get_id());
                    picture.setUsage("problem");
                }
                pictureRepository.saveAll(pictures);
            }
            //处理标签
            //删除原标签下对应的problem
            for (Label label : problem.getLabels()) {
                label.removeProblem(problem);
            }
            for (Label label : labels) {
                if (label.isBase()) {
                    baseNum++;
                }
                if(!label.getProblems().contains(problem))
                    label.addProblem(problem);
                if (baseNum > 1) throw new BadRequestException("传入过多基标签！");
            }
            if (baseNum < 1) throw new BadRequestException("至少应有一个基标签！");
            labelRepository.saveAll(labels);
            new_problem.setNum(null);
            new_problem.setLabels(labels);
            objectUtil.copyNullProperties(new_problem, problem);
            problemRepository.save(problem);
        } catch (Exception e) {
            if (e instanceof BadRequestException) throw new BadRequestException(e.getMessage());
            throw new BadRequestException(e.getMessage());
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
            if (labels.size() == 0 || problemType.size() == 0) {
                if (labels.size() != 0) return problemRepository.findAllByContentLikeAndLabelsIn(query, labels);
                if (problemType.size() != 0)
                    return problemRepository.findAllByContentLikeAndProblemTypeIn(query, problemType);
                return problemRepository.findAllByContentLike(query);
            }
            return problemRepository.findAllByContentLikeAndLabelsInAndProblemTypeIn(query, labels, problemType);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }

    }

    public List<Problem> getAllProblems(int page, int size) throws ServerInternalException {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            return problemRepository.findAll(pageRequest).getContent();
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public long getProblemCount() {
        return problemRepository.count();
    }


}
