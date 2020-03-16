package com.econage.core.web.extension.restproxy.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestFormParam {
    String value();
}
