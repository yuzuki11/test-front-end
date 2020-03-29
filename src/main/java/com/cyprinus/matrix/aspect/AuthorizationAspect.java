package com.cyprinus.matrix.aspect;

import com.cyprinus.matrix.annotation.Permission;
import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Aspect
@Component
public class AuthorizationAspect {

    @Pointcut("execution(* com.cyprinus.matrix.controller.MatrixUserController.*(..)) && @annotation(com.cyprinus.matrix.annotation.MustLogin)")
    private void mustLoginPointCut() {
    }

    @Pointcut("execution(* com.cyprinus.matrix.controller.MatrixUserController.*(..)) && @annotation(com.cyprinus.matrix.annotation.Permission)")
    private void checkPrivilegePointCut() {
    }

    @Before("mustLoginPointCut()")
    private void checkLogin(JoinPoint point) throws ForbiddenException {
        List<Object> args = Arrays.asList(point.getArgs());
        MatrixHttpServletRequestWrapper request = (MatrixHttpServletRequestWrapper) (args.get(0));
        if (request.getTokenInfo() == null) throw new ForbiddenException("请先登录！", true);
    }

    @Before(value = "checkPrivilegePointCut()")
    private void checkAuthorization(JoinPoint point) throws ForbiddenException {
        List<Object> args = Arrays.asList(point.getArgs());
        MatrixHttpServletRequestWrapper request = (MatrixHttpServletRequestWrapper) (args.get(0));
        if (request.getTokenInfo() == null) throw new ForbiddenException("请先登录！", true);
        String role = request.getTokenInfo().getRole();
        Permission.Privilege privilege = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(Permission.class).value();
        switch (privilege) {
            case NONE:
                return;
            case MUST_STUDENT:
                if (!"student".equals(role)) throw new ForbiddenException();
                break;
            case MUST_TEACHER:
                if (!"teacher".equals(role)) throw new ForbiddenException();
                break;
            case MUST_MANAGER:
                if (!"manager".equals(role)) throw new ForbiddenException();
                break;
            case NOT_STUDENT:
                if ("student".equals(role)) throw new ForbiddenException();
                break;
            case NOT_TEACHER:
                if ("teacher".equals(role)) throw new ForbiddenException();
                break;
            case NOT_MANAGER:
                if ("manager".equals(role)) throw new ForbiddenException();
                break;
        }

    }

}
