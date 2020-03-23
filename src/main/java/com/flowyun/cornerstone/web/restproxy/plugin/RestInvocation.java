package com.flowyun.cornerstone.web.restproxy.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RestInvocation {

    private final Object target;
    private final Method method;
    private final Object[] args;

    public RestInvocation(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }

}
