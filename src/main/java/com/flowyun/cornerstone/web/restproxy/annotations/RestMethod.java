package com.flowyun.cornerstone.web.restproxy.annotations;

import org.springframework.http.HttpMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestMethod {
    HttpMethod value();
}
