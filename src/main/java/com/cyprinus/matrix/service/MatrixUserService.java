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
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    public MatrixUserService(MatrixUserRepository matrixUserRepository, JwtUtil jwtUtil, ObjectUtil objectUtil, LessonRepository lessonRepository) {
        this.matrixUserRepository = matrixUserRepository;
        this.jwtUtil = jwtUtil;
        this.objectUtil = objectUtil;
        this.lessonRepository = lessonRepository;
    }

    public Map<String, Object> loginCheck(HashMap content) throws EntityNotFoundException, UnauthorizedException, ServerInternalException {
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
                return data;
            } else {
                throw new UnauthorizedException("用户名密码不匹配!");
            }
        } catch (Throwable e) {
            if (e instanceof UnauthorizedException) throw e;
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
        } catch (MatrixBaseException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServerInternalException(e);
        }


    }

    @Transactional(rollbackOn = Throwable.class)
    public void addStudents(String lessonId, String operatorId, List<HashMap> students) throws MatrixBaseException {
        try {
            Lesson lesson = lessonRepository.getOne(lessonId);
            if (!Objects.equals(lesson.getTeacher().get_id(), operatorId)) throw new ForbiddenException();
            for (HashMap item : students) {
                MatrixUser student;
                if (!matrixUserRepository.existsByUserId((String) item.get("userId"))) {//判断是否是已存在用户
                    student = objectUtil.map2object(item, MatrixUser.class);
                    student.setRole("student");
                    student.setPassword(BCrypt.hashpw(student.getUserId(), BCrypt.gensalt()));
                    matrixUserRepository.saveAndFlush(student);
                } else {
                    student = matrixUserRepository.findByUserId((String) item.get("userId"));//已存在用户直接获取并修改用户记录
                }
                student.addLesson(lesson);
                lesson.addStudent(student);
                lessonRepository.save(lesson);
                matrixUserRepository.save(student);
            }
        } catch (MatrixBaseException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServerInternalException(e);
        }
    }

    public HashMap<String, Object> getAllStudents(String lesson, int page, int size) throws ServerInternalException {
        try {
            HashMap<String, Object> data = new HashMap<>();
            Lesson lesson1 = lessonRepository.getOne(lesson);
            List<MatrixUser> AllStudents = lesson1.getStudents();
            System.out.println(AllStudents.size());
            List<MatrixUser> AllStudentsPage = new ArrayList<>();
            int currentIndex = (page > 1 ? (page - 1) * size : 0);
            for (int i = 0; i < size && i < AllStudents.size() - currentIndex; i++) {
                MatrixUser temp = AllStudents.get(currentIndex + i);
                AllStudentsPage.add(temp);
            }
            data.put("students", AllStudentsPage);
            data.put("total", AllStudents.size());
            return data;
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }

    public long getMatrixUserCount(MatrixUser targetUser, String role) throws ServerInternalException {

        try {
            targetUser.setRole(role);
            Example<MatrixUser> example = Example.of(targetUser);
            return matrixUserRepository.count(example);
        } catch (Exception e) {
            throw new ServerInternalException(e);
        }
    }


}
