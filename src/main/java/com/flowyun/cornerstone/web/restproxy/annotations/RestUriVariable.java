package com.flowyun.cornerstone.web.restproxy.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestUriVariable {
    String value();
}
