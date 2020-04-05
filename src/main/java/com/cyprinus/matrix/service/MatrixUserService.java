package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@SuppressWarnings("unchecked")
@Service
public class MatrixUserService {

    private final
    MatrixUserRepository matrixUserRepository;

    private final
    JwtUtil jwtUtil;

    private final ObjectUtil objectUtil;

    @Autowired
    public MatrixUserService(MatrixUserRepository matrixUserRepository, JwtUtil jwtUtil, ObjectUtil objectUtil) {
        this.matrixUserRepository = matrixUserRepository;
        this.jwtUtil = jwtUtil;
        this.objectUtil = objectUtil;
    }

    public Map<String, Object> loginCheck(HashMap rawUser) throws EntityNotFoundException, ForbiddenException, ServerInternalException {
        MatrixUser targetUser;
        try {
            targetUser = objectUtil.map2object(rawUser, MatrixUser.class);
            targetUser.setPassword((String) rawUser.get("password"));
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(e);
        }
        Example<MatrixUser> example = Example.of(targetUser);
        MatrixUser matrixUser = matrixUserRepository.findOne(example).orElseThrow(() -> new EntityNotFoundException("用户名密码不匹配!"));
        if (!matrixUser.getPassword().equals(targetUser.getPassword()))
            throw new ForbiddenException("用户名密码不匹配!");
        Map<String, String> signData = new HashMap<>();
        signData.put("userId", targetUser.getUserId());
        signData.put("todo", "login");
        signData.put("role", matrixUser.getRole());
        signData.put("_id", matrixUser.get_id());
        Map<String, Object> data = new HashMap<>();
        data.put("userId", matrixUser.getUserId());
        data.put("role", matrixUser.getRole());
        data.put("token", jwtUtil.sign(signData));
        return data;
    }

    public void createUser(MatrixUser targetUser) throws BadRequestException {
        try {
            matrixUserRepository.save(targetUser);
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

    public void putPwd(String _id, String newPwd, String oldPwd) throws BadRequestException {
        try {
            MatrixUser targetUser = matrixUserRepository.getOne(_id);
            if (!targetUser.getPassword().equals(oldPwd))
                throw new BadRequestException("旧密码错");
            targetUser.setPassword(newPwd);
            matrixUserRepository.save(targetUser);
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

    public HashMap getProfile(String _id) throws ServerInternalException {
        try {
            MatrixUser targetUser = matrixUserRepository.getOne(_id);
            HashMap userInfo = this.objectUtil.object2map(targetUser);
            if ("manager".equals(targetUser.getRole()))
                userInfo.remove("lessons");
            return userInfo;

        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public List<MatrixUser> getManyProfiles(MatrixUser targetUser, int page, int size) throws ServerInternalException {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            targetUser.setPassword(null);
            Example<MatrixUser> example = Example.of(targetUser);
            return matrixUserRepository.findAll(example, pageRequest).getContent();
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public MatrixTokenInfo verifyToken(String token) {
        return jwtUtil.decode(token);
    }

    public void putProfile(MatrixUser content, String _id) throws ServerInternalException {
        try {
            content.setRole(null);
            content.setPassword(null);
            content.setUserId(null);
            MatrixUser user = matrixUserRepository.getOne(_id);
            objectUtil.copyNullProperties(content, user);
            matrixUserRepository.save(user);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }

    }

    public void deleteTeacher(String _id) throws BadRequestException, ServerInternalException {
        try {
            MatrixUser user = matrixUserRepository.getOne(_id);
            if (!Objects.equals(user.getRole(), "teacher")) throw new BadRequestException("非法删除对象！");
            if (user.getLessons().size() > 0) throw new BadRequestException("非法删除对象！");
            matrixUserRepository.delete(user);
        } catch (Exception e) {
            if (e instanceof BadRequestException) throw e;
            throw  new ServerInternalException(e);
        }

    }

}
