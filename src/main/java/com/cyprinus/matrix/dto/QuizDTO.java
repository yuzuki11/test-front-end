package com.cyprinus.matrix.dto;

import com.cyprinus.matrix.entity.Label;

import java.util.Date;
import java.util.List;

public interface QuizDTO {

    public String get_id();

    public String getTitle();

    public List<Label> getLabels();

    public Integer[] getPoints();

    public Date getStartTime();

    public Date getDeadline();

}
