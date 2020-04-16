package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.dto.MatrixUserDTO;
import com.cyprinus.matrix.entity.MatrixUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatrixUserRepository extends JpaRepository<MatrixUser, String> {

    MatrixUser findByUserId(String userId);

    boolean existsByUserId(String userId);

    MatrixUserDTO findBy_id(String _id);

}
