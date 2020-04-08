package com.cyprinus.matrix;


import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.OSSUtil;
import com.cyprinus.matrix.util.RedisUtil;
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


    @Autowired
    private KafkaTemplate template;

    @Test
    void sendJson() {


        ListenableFuture<SendResult<String, String>> future = template.send("test", "test");
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("msg OK." + result.toString());
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("msg send failed: ");
            }
        });

    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testRedisGet() throws Exception {
        stringRedisTemplate.opsForValue().set("aaa", "111");
        assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

    @Test
    void testObj() throws Exception {
        String user = "123";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set("com.neox", user);
        operations.set("com.neo.f", user);
        Thread.sleep(1000);
        //redisTemplate.delete("com.neo.f");
        boolean exists = redisTemplate.hasKey("com.neo.f");
        if (exists) {
            System.out.println("exists is true");
            System.out.println(operations.get("com.neo.f"));
        } else {
            System.out.println("exists is false");
        }
        // Assert.assertEquals("aa", operations.get("com.neo.f").getUserName());
    }

    @Autowired
    RedisUtil redisUtil;

    @Test
    void testRedisUtil() throws JsonProcessingException {
        System.out.println(redisUtil.getRedis().hasKey("test"));
        redisUtil.set("test", "test");
        redisUtil.set("test1", "test", 5, TimeUnit.DAYS);
        System.out.println(redisUtil.getRedis().hasKey("test"));
        System.out.println(redisUtil.getRedis().hasKey("test"));

    }

}
