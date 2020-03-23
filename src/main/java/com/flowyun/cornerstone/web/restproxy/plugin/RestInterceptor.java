package com.flowyun.cornerstone.web.restproxy.plugin;

public interface RestInterceptor {

    Object intercept(RestInvocation invocation) throws Throwable;

    default Object plugin(Object target,Class<?> restInterface) {
        return RestPlugin.wrap(target, this,restInterface);
    }

}
