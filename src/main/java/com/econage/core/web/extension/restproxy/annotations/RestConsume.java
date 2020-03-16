package com.econage.core.web.extension.restproxy.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestConsume {
    String value();
}
