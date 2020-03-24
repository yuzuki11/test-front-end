package com.cyprinus.matrix;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.util.JwtUtil;
import com.sun.deploy.security.SelectableSecurityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = MatrixApplication.class)
class MatrixApplicationTests {

    @Autowired
    private Config config;

    @Autowired
    private MatrixUserRepository matrixUserRepo;

    @Autowired
    private JwtUtil jwt;

    @Test
    void contextLoads() {
    }

    @Test
    void testUser() {
        MatrixUser user = new MatrixUser();
        user.setName("test");
        user.setRole("student");
        //matrixUserRepo.save(user);
        System.out.print("Current user count:");
        System.out.println(matrixUserRepo.count());

    }

    @Test
    void testConfig() {
        System.out.println(config.getSecretKey());
    }

    @Test
    void  testJWT(){
        Map<String,String> data = new HashMap<String,String>();
        data.put("todo","test");
        data.put("userId","test");
        String token = jwt.sign(data);
        System.out.println(token);
        Map decoded = jwt.decode(token);
        System.out.println(jwt.decode(token));
        assert decoded.get("aud").equals("test") && decoded.get("sub").equals("test");
    }

}
