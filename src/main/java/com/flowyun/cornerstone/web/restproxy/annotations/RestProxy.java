package com.flowyun.cornerstone.web.restproxy.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface RestProxy {

    /*
    * 服务目标Bean名称，一般由某个业务模块的配置类解答目标地址，提供业务模块目标地址
    * */
    String value();

    /*
    * 某些业务场景，可能需要额外逻辑，例如钉钉服务，需要限速，此处可以解决
    * */
    String[] interceptors() default {};

}
