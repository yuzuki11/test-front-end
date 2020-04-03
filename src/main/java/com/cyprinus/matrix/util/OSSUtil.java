package com.cyprinus.matrix.util;


import com.cyprinus.matrix.Config;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.policy.PolicyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class OSSUtil {

    private final Config config;

    private MinioClient minioClient;

    @Autowired
    public OSSUtil(Config config) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, RegionConflictException, InvalidObjectPrefixException {
        this.config = config;
        minioClient = new MinioClient(config.getOSSUrl(), config.getOSSAccessKey(), config.getOSSSecretKey());
        if(!minioClient.bucketExists("matrix")){
            minioClient.makeBucket("matrix");
            minioClient.setBucketPolicy("matrix","*", PolicyType.READ_ONLY);
        }
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }
}
