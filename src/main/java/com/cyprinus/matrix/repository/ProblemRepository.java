package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Label;
import com.cyprinus.matrix.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, String> {

    List<Problem> findAllByNumIs(String num);

    List<Problem> findAllByContentLike(String content);

    List<Problem> findAllByContentLikeAndLabelsInAndProblemTypeIn(String content, List<Label> labels, List<String> problemType);

    List<Problem> findAllByContentLikeAndLabelsIn(String content, List<Label> labels);

    List<Problem> findAllByContentLikeAndProblemTypeIn(String content, List<String> problemType);

}
