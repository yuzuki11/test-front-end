package com.cyprinus.matrix.aspect;

import com.cyprinus.matrix.exception.ForbiddenException;
import com.cyprinus.matrix.type.MatrixHttpServletRequestWrapper;
import net.bytebuddy.implementation.bytecode.Throw;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Aspect
@Component
public class AuthorizationAspect {
    @Pointcut("execution(* com.cyprinus.matrix.controller.MatrixUserController.*(..)) && @annotation(com.cyprinus.matrix.annotation.MustLogin)")
    private void allControllerHandler() {
    }

    @Before("allControllerHandler()")
    private void checkAuthorization(JoinPoint point) throws ForbiddenException {
        List<Object> args = Arrays.asList(point.getArgs());
        MatrixHttpServletRequestWrapper request = (MatrixHttpServletRequestWrapper) (args.get(0));
        if (request.getTokenInfo() == null) throw new ForbiddenException("请先登录！");
    }
}
