package com.econage.core.web.extension.restproxy;

import com.econage.core.web.extension.restproxy.annotations.RestConsume;
import com.econage.core.web.extension.restproxy.annotations.RestMethod;
import com.econage.core.web.extension.restproxy.annotations.RestPath;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;

public class RestRequestMapping {

    public static RestRequestMapping parseRequestMapping(Class<?> cls){
        if(cls==null){
            return null;
        }
        RestRequestMapping restRequestMapping = new RestRequestMapping();
        RestPath path = AnnotationUtils.findAnnotation(cls,RestPath.class);
        if(path!=null){
            restRequestMapping.path = path.value();
        }
        RestMethod method = AnnotationUtils.findAnnotation(cls,RestMethod.class);
        if(method!=null){
            restRequestMapping.method = method.value();
        }
        RestConsume consume = AnnotationUtils.findAnnotation(cls,RestConsume.class);
        if(consume!=null){
            restRequestMapping.consume = consume.value();
        }
        return restRequestMapping;
    }

    public static RestRequestMapping parseRequestMapping(Method proxyMethod){
        if(proxyMethod==null){
            return null;
        }
        RestRequestMapping restRequestMapping = new RestRequestMapping();
        RestPath path = AnnotationUtils.findAnnotation(proxyMethod,RestPath.class);
        if(path!=null){
            restRequestMapping.path = path.value();
        }
        RestMethod method = AnnotationUtils.findAnnotation(proxyMethod,RestMethod.class);
        if(method!=null){
            restRequestMapping.method = method.value();
        }
        RestConsume consume = AnnotationUtils.findAnnotation(proxyMethod,RestConsume.class);
        if(consume!=null){
            restRequestMapping.consume = consume.value();
        }
        return restRequestMapping;
    }


    private String path;
    private HttpMethod method;
    private String consume;

    private RestRequestMapping() {
    }

    public String getPath() {
        return path;
    }
    public HttpMethod getMethod() {
        return method;
    }
    public String getConsume() {
        return consume;
    }
}
