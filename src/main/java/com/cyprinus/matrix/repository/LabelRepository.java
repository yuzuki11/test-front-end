package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, String> {
}
