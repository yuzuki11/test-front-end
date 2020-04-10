package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.*;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.type.MatrixRedisPayload;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@SuppressWarnings("unchecked")
@Service
public class MatrixUserService {

    private final
    LessonRepository lessonRepository;

    private final
    MatrixUserRepository matrixUserRepository;

    private final
    JwtUtil jwtUtil;

    private final
    ObjectUtil objectUtil;

    private final
    RedisUtil redisUtil;

    private final
    KafkaUtil kafkaUtil;



    @Autowired
    public MatrixUserService(MatrixUserRepository matrixUserRepository, JwtUtil jwtUtil, ObjectUtil objectUtil, LessonRepository lessonRepository, KafkaUtil kafkaUtil, RedisUtil redisUtil) {
        this.matrixUserRepository = matrixUserRepository;
        this.jwtUtil = jwtUtil;
        this.objectUtil = objectUtil;
        this.lessonRepository = lessonRepository;
        this.kafkaUtil = kafkaUtil;
        this.redisUtil = redisUtil;
    }

    public Map<String, Object> loginCheck(HashMap content) throws EntityNotFoundException, ForbiddenException, ServerInternalException, JsonProcessingException {
        try {
            MatrixUser user = matrixUserRepository.findByUserId((String) content.get("userId"));
            if ((user.getPassword() == null && content.get("password").equals(content.get("userId"))) || BCrypt.checkpw((String) content.get("password"), user.getPassword())) {
                Map<String, String> signData = new HashMap<>();
                signData.put("userId", user.getUserId());
                signData.put("todo", "login");
                signData.put("role", user.getRole());
                signData.put("_id", user.get_id());
                Map<String, Object> data = new HashMap<>();
                data.put("userId", user.getUserId());
                data.put("role", user.getRole());
                String token = jwtUtil.sign(signData);
                data.put("token", token);
                //正常登录
                String tokenKey = "TOKEN" + token;
                redisUtil.set(tokenKey, signData, 7, TimeUnit.DAYS);
                String userKey = "USER" + user.get_id();
                //检验是否重复登录
                String oldTokenKey = redisUtil.get(userKey,String.class);
                if (oldTokenKey != null) redisUtil.getRedis().delete(oldTokenKey);//如果是则作废之前token
                redisUtil.set(userKey, tokenKey, 7, TimeUnit.DAYS);
                return data;
            } else {
                throw new ForbiddenException("用户名密码不匹配!");
            }
        } catch (Throwable e) {
            if (e instanceof ForbiddenException) throw e;
            throw new ServerInternalException(e);
        }
    }

    public void createUser(MatrixUser targetUser) throws BadRequestException {
        try {
            targetUser.setPassword(BCrypt.hashpw(targetUser.getUserId(), BCrypt.gensalt()));
            matrixUserRepository.save(targetUser);
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

    public void putPwd(String _id, String newPwd, String oldPwd) throws ForbiddenException, ServerInternalException, JsonProcessingException {
        try {
            MatrixUser targetUser = matrixUserRepository.getOne(_id);
            if (!BCrypt.checkpw(oldPwd, targetUser.getPassword()))
                throw new ForbiddenException("旧密码错误");
            targetUser.setPassword(BCrypt.hashpw(newPwd, BCrypt.gensalt()));
            if (targetUser.getEmail() != null && !targetUser.getEmail().equals("")) {
                String token = JwtUtil.getRandomString(10);
                String todo = "UPDATE-PASSWORD";
                String key = todo + _id;
                MatrixRedisPayload payload = new MatrixRedisPayload(_id, todo, targetUser.getPassword(), token);
                redisUtil.set(key, payload, 5, TimeUnit.MINUTES);
                HashMap<String, String> values = new HashMap<>();
                String link = "base?key=" + key + "&token=" + token;
                values.put("link", link);
                kafkaUtil.sendMail("UPDATE", "Matrix修改密码确认邮件", targetUser.getEmail(), values);
            } else matrixUserRepository.save(targetUser);

        } catch (Throwable e) {
            if (e instanceof MatrixBaseException) throw e;
            throw new ServerInternalException(e);
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
            if (content.getEmail() != null && !Objects.equals(user.getEmail(), content.getEmail())) {
                String token = JwtUtil.getRandomString(10);
                String todo = "UPDATE-EMAIL";
                String key = todo + _id;
                MatrixRedisPayload payload = new MatrixRedisPayload(_id, todo, content.getEmail(), token);
                redisUtil.set(key, payload, 5, TimeUnit.MINUTES);
                HashMap<String, String> values = new HashMap<>();
                String link = "base?key=" + key + "&token=" + token;
                values.put("link", link);
                kafkaUtil.sendMail("UPDATE", "Matrix修改绑定邮箱确认邮件", payload.getValue(), values);
                content.setEmail(null);
            }
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
            throw new ServerInternalException(e);
        }

    }

    @Transactional(rollbackOn = Throwable.class)
    public void removeStudentFromLesson(String lessonId, String studentId, String operatorId) throws ServerInternalException, ForbiddenException {
        try {
            MatrixUser student = matrixUserRepository.getOne(studentId);
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!Objects.equals(lesson.getTeacher().get_id(), operatorId)) throw new ForbiddenException();
            lesson.removeStudent(student);
            student.removeLesson(lesson);
            matrixUserRepository.save(student);
            lessonRepository.save(lesson);
        } catch (Exception e) {
            if (e instanceof ForbiddenException) throw e;
            throw new ServerInternalException(e);
        }


    }

    @Transactional(rollbackOn = Throwable.class)
    public void addStudents(String lessonId, String operatorId, List<HashMap> students) throws ServerInternalException {
        try {
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!Objects.equals(lesson.getTeacher().get_id(), operatorId)) throw new ForbiddenException();
            for (HashMap item : students) {
                MatrixUser student = objectUtil.map2object(item, MatrixUser.class);
                student.setRole("student");
                student.setPassword(BCrypt.hashpw(student.getUserId(), BCrypt.gensalt()));
                matrixUserRepository.save(student);
            }
        } catch (Throwable e) {
            throw new ServerInternalException(e);
        }
    }

    public void verifyOperate(String key, String token) throws NotFoundException, ServerInternalException, JsonProcessingException {
        try {
            MatrixRedisPayload payload = redisUtil.get(key);
            if (payload == null) throw new NotFoundException();
            if (!Objects.equals(token, payload.getToken())) throw new NotFoundException();
            switch (payload.getTodo()) {
                case "UPDATE-EMAIL": {
                    MatrixUser user = matrixUserRepository.getOne(payload.getUserId());
                    user.setEmail(payload.getValue());
                    matrixUserRepository.save(user);
                    redisUtil.getRedis().delete(key);
                    return;
                }
                case "UPDATE-PASSWORD": {
                    MatrixUser user = matrixUserRepository.getOne(payload.getUserId());
                    user.setPassword(payload.getValue());
                    matrixUserRepository.save(user);
                    redisUtil.getRedis().delete(key);
                    return;
                }
            }
            throw new NotFoundException();
        } catch (Throwable e) {
            if (e instanceof MatrixBaseException) throw e;
            else throw new ServerInternalException(e);
        }

    }

    public List<MatrixUser> getAllStudents(MatrixUser targetUser,String lesson,int page, int size) throws ServerInternalException {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Lesson lesson1 = lessonRepository.getOne(lesson);
            List<MatrixUser> AllStudents =lesson1.getStudents();
            List<MatrixUser> AllStudentsPage = new ArrayList<MatrixUser>();
            int currIdx = (page > 1 ? (page -1) * size : 0);
            for (int i = 0; i < size && i < AllStudents.size() - currIdx; i++) {
                MatrixUser temp = AllStudents.get(currIdx + i);
                AllStudentsPage.add(temp);}
            return AllStudentsPage;
        } catch (Exception e) {
            throw new ServerInternalException(e.getMessage());
        }
    }

    public long getMatrixUserCount(MatrixUser targetUser,String role)throws ServerInternalException{

        try {
            targetUser.setRole(role);
            Example<MatrixUser> example = Example.of(targetUser);
            return matrixUserRepository.count(example);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }


}
