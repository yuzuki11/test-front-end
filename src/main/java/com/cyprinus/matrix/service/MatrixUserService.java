package com.cyprinus.matrix.service;

import com.cyprinus.matrix.entity.Lesson;
import com.cyprinus.matrix.entity.MatrixUser;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.LessonRepository;
import com.cyprinus.matrix.repository.MatrixUserRepository;
import com.cyprinus.matrix.type.MatrixTokenInfo;
import com.cyprinus.matrix.util.BCrypt;
import com.cyprinus.matrix.util.JwtUtil;
import com.cyprinus.matrix.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
@Service
public class MatrixUserService {

    private final
    LessonRepository lessonRepository;

    private final
    MatrixUserRepository matrixUserRepository;

    private final
    JwtUtil jwtUtil;

    private final ObjectUtil objectUtil;

    @Autowired
    public MatrixUserService(MatrixUserRepository matrixUserRepository, JwtUtil jwtUtil, ObjectUtil objectUtil, LessonRepository lessonRepository) {
        this.matrixUserRepository = matrixUserRepository;
        this.jwtUtil = jwtUtil;
        this.objectUtil = objectUtil;
        this.lessonRepository = lessonRepository;
    }

    public Map<String, Object> loginCheck(HashMap content) throws EntityNotFoundException, ForbiddenException, ServerInternalException {
        try {
            MatrixUser user = matrixUserRepository.findByUserIdIs((String) content.get("userId"));
            if (!BCrypt.checkpw((String) content.get("password"), user.getPassword()))
                throw new ForbiddenException("用户名密码不匹配!");
            Map<String, String> signData = new HashMap<>();
            signData.put("userId", user.getUserId());
            signData.put("todo", "login");
            signData.put("role", user.getRole());
            signData.put("_id", user.get_id());
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            data.put("role", user.getRole());
            data.put("token", jwtUtil.sign(signData));
            return data;
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

    public void putPwd(String _id, String newPwd, String oldPwd) throws ForbiddenException, ServerInternalException {
        try {
            MatrixUser targetUser = matrixUserRepository.getOne(_id);
            if (!BCrypt.checkpw(oldPwd, targetUser.getPassword()))
                throw new ForbiddenException("旧密码错误");
            targetUser.setPassword(BCrypt.hashpw(newPwd, BCrypt.gensalt()));
            matrixUserRepository.save(targetUser);
        } catch (Throwable e) {
            if (e instanceof ForbiddenException) throw e;
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

}
