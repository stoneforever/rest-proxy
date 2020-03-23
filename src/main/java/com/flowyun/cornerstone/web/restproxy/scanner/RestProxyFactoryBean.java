package com.flowyun.cornerstone.web.restproxy.scanner;

import com.flowyun.cornerstone.web.restproxy.RestRequestHandler;
import com.flowyun.cornerstone.web.restproxy.RestRequestMapping;
import com.flowyun.cornerstone.web.restproxy.plugin.RestInterceptor;
import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.util.Assert.notNull;

public class RestProxyFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private RestTemplate restTemplate;
    private final Class<T> restInterface;
    private RestProxyTarget restProxyTarget;
    //private RestInterceptorChain interceptorChain = new RestInterceptorChain();
    private List<RestInterceptor> interceptors = Collections.emptyList();

    public RestProxyFactoryBean(Class<T> restInterface) {
        this.restInterface = restInterface;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public T getObject() throws Exception {
        Object restTargetProxy = Reflection.newProxy(
                restInterface,
                new RestProxyImpl(restTemplate, restProxyTarget.serviceTarget(),restInterface)
        );

        for (RestInterceptor interceptor : interceptors) {
            restTargetProxy = interceptor.plugin(restTargetProxy,restInterface);
        }

        return (T)restTargetProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return restInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    //由运行时应用动态插入
    //服务目标，一般由协议、地址、端口、固定路径组成；例如：http://127.0.0.1:8080
    public void setRestProxyTarget(RestProxyTarget restProxyTarget) {
        this.restProxyTarget = restProxyTarget;
    }

    //某些模块可能希望对访问做一些控制，例如钉钉访问云服务器，多一个频率控制，一个异常调整
    public void setInterceptors(List<RestInterceptor> interceptors) {
        if(CollectionUtils.isEmpty(interceptors)){
            return;
        }
        this.interceptors = interceptors;
    }

    /*public void AddInterceptor(List<RestInterceptor> restInterceptors){
        if(CollectionUtils.isNotEmpty(restInterceptors)){
            for(RestInterceptor interceptor : restInterceptors){
                interceptorChain.addInterceptor(interceptor);
            }
        }
    }*/

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.restProxyTarget, "Property 'restProxyTarget' are required");
    }

    protected static class RestProxyImpl extends AbstractInvocationHandler {

        private final RestTemplate restTemplate;
        private final String target;
        private final RestRequestMapping baseMapping;
        private final ConcurrentMap<Method,RestRequestHandler> restRequestHandlerMap = Maps.newConcurrentMap();

        RestProxyImpl(RestTemplate restTemplate, String target, Class<?> proxyInterface) {
            this.restTemplate = restTemplate;
            this.target = target;
            this.baseMapping = RestRequestMapping.parseRequestMapping(proxyInterface);
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
            RestRequestHandler restRequestHandler = restRequestHandlerMap.computeIfAbsent(
                    method,
                    m->new RestRequestHandler(restTemplate,target,baseMapping,m)
            );
            return restRequestHandler.execute(args);
        }

    }

}
