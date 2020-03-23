package com.flowyun.cornerstone.web.restproxy;

import com.flowyun.cornerstone.web.restproxy.paramhandler.RestMethodParamHandler;
import com.flowyun.cornerstone.web.restproxy.util.ArrayUtils;
import com.google.common.primitives.Primitives;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;

/*
* 维护某个方法具体的请求信息
* 包括路径、请求方式、内容格式、参数处理方式
* */
public class RestRequestHandler {

    private final RestTemplate restTemplate;

    private String uriTemplate;
    private HttpMethod method;
    private String consume;
    private Class<?> responseType;

    /*
    * 参数解析器
    * */
    private RestMethodParamHandler[] paramHandlers;

    public RestRequestHandler(RestTemplate restTemplate,String requestTarget, RestRequestMapping baseMapping, Method proxyMethod) {
        this.restTemplate = restTemplate;
        parseResponseType(proxyMethod);
        parseRequestMapping(requestTarget,baseMapping,proxyMethod);
        parseMethodParams(proxyMethod);
    }
    private void parseResponseType(Method proxyMethod){
        Class<?> responseTypeTmp = proxyMethod.getReturnType();
        if(responseTypeTmp==void.class){
            responseTypeTmp = Void.class;
        }
        //如果是原型，则改为包装类
        this.responseType = Primitives.wrap(responseTypeTmp);
    }
    private void parseRequestMapping(String requestTarget,RestRequestMapping baseMapping, Method proxyMethod){
        RestRequestMapping methodMapping = RestRequestMapping.parseRequestMapping(proxyMethod);
        //路径解析,如果有基础路由，则拼接路径
        StringBuilder pathBuf = new StringBuilder();
        if(!StringUtils.isEmpty(requestTarget)){
            pathBuf.append(requestTarget);
        }
        if(baseMapping!=null&&!StringUtils.isEmpty(baseMapping.getPath())){
            pathBuf.append(baseMapping.getPath());
        }
        if(methodMapping!=null&&!StringUtils.isEmpty(methodMapping.getPath())){
            pathBuf.append(methodMapping.getPath());
        }
        uriTemplate = pathBuf.toString();
        //http方式解析，方法注解优先
        if(methodMapping != null) {
            method = methodMapping.getMethod();
        }else if(baseMapping!=null){
            method = baseMapping.getMethod();
        }else{
            throw new IllegalArgumentException("can't find http method in parse proxy method");
        }
        //传输格式解析，方法注解优先，get方式不需要解析consume
        if(method!=HttpMethod.GET){
            if(methodMapping != null&& !StringUtils.isEmpty(methodMapping.getConsume())) {
                consume = methodMapping.getConsume();
            }else if(baseMapping!=null&& !StringUtils.isEmpty(baseMapping.getConsume())){
                consume = baseMapping.getConsume();
            }
        }
    }
    private void parseMethodParams(Method proxyMethod){

        Parameter[] methodParameters = proxyMethod.getParameters();
        if(ArrayUtils.isEmpty(methodParameters)){
            paramHandlers = RestMethodParamHandler.EMPTY_PARAM_HANDLER_ARRAY;
            return;
        }
        paramHandlers = new RestMethodParamHandler[methodParameters.length];
        for(int i=0,l=methodParameters.length;i<l;i++) {
            paramHandlers[i] = RestMethodParamHandler.parseParam(i,methodParameters[i]);
        }
    }


    public final Object execute(Object[] param){
        if(ArrayUtils.isEmpty(param)){
            return doExecuteWithoutParams();
        }else{
            return doExecuteWithParams(param);
        }
    }
    private Object doExecuteWithoutParams(){
        ResponseEntity<?> responseEntity;
        if(method==HttpMethod.GET){
            responseEntity = restTemplate.getForEntity(uriTemplate,responseType);
        }else if(method==HttpMethod.DELETE){
            responseEntity = restTemplate.exchange(
                    uriTemplate,
                    method,
                    HttpEntity.EMPTY,
                    responseType,
                    Collections.emptyMap()
            );
        }else{
            throw new UnsupportedOperationException("can't executor method:"+method);
        }
        return unwrapResponseEntity(responseEntity);
    }
    private Object doExecuteWithParams(Object[] param){
        RestRequestContext restRequestContext = RestRequestContext.create();
        for(RestMethodParamHandler paramHandler : paramHandlers){
            paramHandler.paramHandle(restRequestContext,param);
        }
        ResponseEntity<?> responseEntity;
        if(method==HttpMethod.GET){
            responseEntity = restTemplate.getForEntity(uriTemplate,responseType,restRequestContext.getUirVariableMap());
        }else{
            responseEntity = restTemplate.exchange(
                    uriTemplate,
                    method,
                    new HttpEntity<>(restRequestContext.getRequestBody(),getHeaders()),
                    responseType,
                    restRequestContext.getUirVariableMap()
            );
        }
        return unwrapResponseEntity(responseEntity);
    }
    private Object unwrapResponseEntity(ResponseEntity<?> responseEntity){
        if(ResponseEntity.class == responseType){
            return responseEntity;
        }else if(responseEntity!=null){
            return responseEntity.getBody();
        }
        return null;
    }

    private MultiValueMap<String, String> getHeaders(){
        if(!StringUtils.isEmpty(consume)){
            MultiValueMap<String, String> requestHeaders = new HttpHeaders();
            requestHeaders.add(HttpHeaders.CONTENT_TYPE,consume);
            return requestHeaders;
        }else{
            return null;
        }
    }

}
