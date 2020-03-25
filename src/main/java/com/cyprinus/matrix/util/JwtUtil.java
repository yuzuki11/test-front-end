package com.cyprinus.matrix.util;

import com.cyprinus.matrix.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Config config;

    private SecretKey signKey;

    @Autowired
    public JwtUtil(Config config) {
        this.config = config;
    }

    @PostConstruct
    private void generalKey() {
        String secretKey = config.getSecretKey();
        byte[] encodedKey = Base64.decodeBase64(secretKey);
        signKey =  new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public String sign(Map<String, String> data) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder builder = Jwts.builder()
                //.setId(id)                                      // JWT_ID
                .setAudience(data.get("userId"))                                // 接受者
                //.setClaims(null)                                // 自定义属性
                .setSubject(data.get("todo"))                                 // 主题
                .setIssuer("")                                  // 签发者
                .setIssuedAt(new Date())                        // 签发时间
                //.setExpiration(new Date())                       // 失效时间
                .signWith(signatureAlgorithm, signKey);           // 签名算法以及密匙
        return builder.compact();
    }

    public Claims decode(String jwt) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt).getBody();
    }

}
