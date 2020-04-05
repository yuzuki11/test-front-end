package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, String> {

    List<Picture> findAllByOwnedByIs(String ownedBy);

}
