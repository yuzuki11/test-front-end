package com.cyprinus.matrix.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    enum Privilege {NOT_TEACHER, NOT_STUDENT, NOT_MANAGER, MUST_STUDENT, MUST_TEACHER, MUST_MANAGER, NONE}

    Privilege privilege() default Privilege.NONE;

}
