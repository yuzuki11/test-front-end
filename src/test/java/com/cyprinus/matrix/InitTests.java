package com.cyprinus.matrix;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MatrixApplication.class)
class InitTests {

    @Autowired
    MatrixUserRepository matrixUserRepository;

    @Test
    void initSuperUser(){
        MatrixUser matrixUser = new MatrixUser();
        matrixUser.setUserId("123456");
        matrixUser.setPassword("123456");
        matrixUser.setRole("manager");
        matrixUser.setName("test");
        matrixUserRepository.save(matrixUser);
    }


}
