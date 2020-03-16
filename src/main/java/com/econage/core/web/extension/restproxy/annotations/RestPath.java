package com.econage.core.web.extension.restproxy.annotations;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestPath {
    String value();
}
