package com.econage.core.web.extension.restproxy.plugin;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class RestPlugin extends AbstractInvocationHandler {

    public static Object wrap(Object restProxyTarget, RestInterceptor interceptor,Class<?> restInterface) {
        return Reflection.newProxy(
                restInterface,
                new RestPlugin(restProxyTarget, interceptor)
        );
    }


    private final Object restProxyTarget;
    private final RestInterceptor interceptor;

    private RestPlugin(Object restTarget, RestInterceptor interceptor) {
        this.restProxyTarget = restTarget;
        this.interceptor = interceptor;
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return interceptor.intercept(new RestInvocation(restProxyTarget, method, args));
        } catch (Exception e) {
            throw unwrapThrowable(e);
        }
    }

    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

}
