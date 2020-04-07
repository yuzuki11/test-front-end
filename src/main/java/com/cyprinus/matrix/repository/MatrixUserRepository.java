package com.cyprinus.matrix.repository;

import com.cyprinus.matrix.entity.MatrixUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatrixUserRepository extends JpaRepository<MatrixUser, String> {

    MatrixUser findByUserId(String userId);

}
