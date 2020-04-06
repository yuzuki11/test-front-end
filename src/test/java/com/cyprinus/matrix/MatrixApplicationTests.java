package com.cyprinus.matrix;


import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.ServerInternalException;
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
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
        //matrixUserRepo.save(user);
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




    @Test
    void send() throws ServerInternalException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("cyprinus");
        producer.setNamesrvAddr("localhost:9876");
        producer.setInstanceName("rmq-instance");
        producer.start();
        try {
            for (int i=0;i<100;i++){

                Message message = new Message("log-topic", "user-tag","test".getBytes());
                System.out.println("生产者发送消息:"+"test");
                producer.send(message);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }




}
