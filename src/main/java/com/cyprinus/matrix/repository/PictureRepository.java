package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, String> {
}
