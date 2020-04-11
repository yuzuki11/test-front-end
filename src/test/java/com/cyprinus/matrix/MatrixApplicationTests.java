package com.cyprinus.matrix;


import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.OSSUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MatrixApplication.class)
class MatrixApplicationTests {

    @Autowired
    private Config config;

    @Autowired
    private OSSUtil ossUtil;

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
        user.setUserId("11111");
        matrixUserRepo.save(user);
        System.out.print("Current user count:");
        System.out.println(matrixUserRepo.count());

    }

    @Test
    void testConfig() {
        System.out.println(config.getSecretKey());
    }

    @Test
    void testJWT() {
        Map<String, String> data = new HashMap<>();
        data.put("todo", "test");
        data.put("userId", "test");
        String token = jwt.sign(data);
        System.out.println(token);
        Claims decoded = jwt.decode(token).getRaw();
        System.out.println(decoded);
        assert decoded.get("aud").equals("test") && decoded.get("sub").equals("test");
    }

    @Test
    void testObjectMapper() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(matrixUserRepo.findAll().get(0));
        System.out.println(json);
        HashMap map = objectMapper.readValue(json, HashMap.class);
        map.remove("password");
        System.out.println(map);
    }

    @Test
    void testSSOUtil() throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidArgumentException {
        System.out.println(config.getOSSUrl());
        MinioClient minioClient = new MinioClient(config.getOSSUrl(), config.getOSSAccessKey(), config.getOSSSecretKey());
        minioClient.putObject("matrix", "test", "38321534.jpg");
        System.out.println(minioClient.bucketExists("matrix"));
    }

}
