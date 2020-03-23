package com.flowyun.cornerstone.web.restproxy.annotations;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestPath {
    String value();
}
