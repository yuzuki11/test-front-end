package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MatrixUserService {

    private final
    MatrixUserRepository matrixUserRepository;

    private final
    JwtUtil jwtUtil;


    @Autowired
    public MatrixUserService(MatrixUserRepository matrixUserRepository, JwtUtil jwtUtil) {
        this.matrixUserRepository = matrixUserRepository;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> loginCheck(MatrixUser targetUser) throws EntityNotFoundException, ForbiddenException {
        targetUser.setUserId(targetUser.getUserId());
        Example<MatrixUser> example = Example.of(targetUser);
        MatrixUser matrixUser = matrixUserRepository.findOne(example).orElseThrow(() -> new EntityNotFoundException("用户名密码不匹配!"));
        if (!matrixUser.getPassword().equals(targetUser.getPassword()))
            throw new ForbiddenException("用户名密码不匹配!");
        Map<String, String> signData = new HashMap<>();
        signData.put("userId", targetUser.getUserId());
        signData.put("todo", "login");
        signData.put("role", matrixUser.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("userId", matrixUser.getUserId());
        data.put("role", matrixUser.getRole());
        data.put("token", jwtUtil.sign(signData));
        return data;
    }

    public MatrixTokenInfo verifyToken(String token) {
        return jwtUtil.decode(token);
    }

}
