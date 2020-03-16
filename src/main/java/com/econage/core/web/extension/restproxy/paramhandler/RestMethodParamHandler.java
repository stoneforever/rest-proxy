package com.econage.core.web.extension.restproxy.paramhandler;

import com.econage.core.web.extension.restproxy.RestRequestContext;
import com.econage.core.web.extension.restproxy.annotations.RestFormParam;
import com.econage.core.web.extension.restproxy.annotations.RestUriVariable;
import com.econage.core.web.extension.restproxy.util.ArrayUtils;

import java.lang.reflect.Parameter;
import java.util.Map;

public abstract class RestMethodParamHandler {

    public final static RestMethodParamHandler[] EMPTY_PARAM_HANDLER_ARRAY = new RestMethodParamHandler[0];
    public static RestMethodParamHandler parseParam(int paramIdx,Parameter parameter){
        if(parameter==null){
            throw new IllegalArgumentException("parameter info is null!");
        }
        RestUriVariable uriVariableAnnotation = parameter.getAnnotation(RestUriVariable.class);
        RestFormParam formParamAnnotation = parameter.getAnnotation(RestFormParam.class);
        Class<?> parameterType = parameter.getType();

        if(uriVariableAnnotation!=null){
            return new UriVariableHandler(paramIdx,uriVariableAnnotation);
        }else if(Map.class.isAssignableFrom(parameterType)){
            return new FormParamHandler(paramIdx);
        }else if(formParamAnnotation!=null){
            return new FormParamHandler(paramIdx,formParamAnnotation);
        }else{
            return new RequestBodyHandler(paramIdx);
        }

    }


    private final int methodParamIdx;

    protected RestMethodParamHandler(int methodParamIdx) {
        this.methodParamIdx = methodParamIdx;
    }

    public int getMethodParamIdx() {
        return methodParamIdx;
    }

    public final void paramHandle(RestRequestContext requestContext, Object[] param){
        if(requestContext==null){
            throw new IllegalArgumentException("no request content found");
        }
        if(ArrayUtils.isEmpty(param)){
            throw new IllegalArgumentException("method params is empty");
        }
        if(methodParamIdx<0){
            throw new IllegalArgumentException("methodParamIdx error:"+methodParamIdx);
        }
        doParamHandle(requestContext,param[methodParamIdx]);
    }

    abstract void doParamHandle(RestRequestContext requestContext, Object param);
}
