package com.cyprinus.matrix;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.sun.deploy.security.SelectableSecurityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MatrixApplication.class)
class MatrixApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private Config config;

    @Autowired
    private MatrixUserRepository matrixUserRepo;

    @Test
    void testUser() {
        MatrixUser user = new MatrixUser();
        user.setName("test");
        user.setRole("student");
        //matrixUserRepo.save(user);
        System.out.println(matrixUserRepo.count());

    }

    @Test
    void testConfig() {
        System.out.println(config.getSecretKey());
    }

}
