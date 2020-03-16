package com.econage.core.web.extension.restproxy;

import com.econage.core.web.extension.restproxy.util.MapUtils;
import com.google.common.collect.Maps;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Map;

/*
* 维护一个请求可能需要使用的各个变量
* 会检查requestBody和formMap冲突
* */
public class RestRequestContext {
    private Map<String,Object> uirVariableMap;
    private MultiValueMap<String,Object> formParamMap;
    private Object requestBody;

    public static RestRequestContext create(){
        return new RestRequestContext();
    }
    private RestRequestContext() {}

    public void addUriVariable(String name,Object val){
        if(uirVariableMap==null){
            uirVariableMap = Maps.newHashMap();
        }
        uirVariableMap.put(name,val);
    }

    public Map<String, Object> getUirVariableMap() {
        if(uirVariableMap==null){
            return Collections.emptyMap();
        }
        return uirVariableMap;
    }

    public void addFormParam(String name,Object val){
        if(formParamMap == null){
            formParamMap = new LinkedMultiValueMap<>();
        }
        formParamMap.add(name,val);
    }
    public void addFormParam(Map<String,Object> kvMap){
        if(MapUtils.isEmpty(kvMap)){
            return;
        }
        kvMap.forEach(this::addFormParam);
    }

    public void setRequestBody(Object body){
        if(this.requestBody!=null){
            throw new IllegalArgumentException("more than one body found");
        }
        if(formParamMap!=null){
            throw new IllegalArgumentException("can't use body and form param together");
        }
        this.requestBody = body;
    }

    /*
    * 如果表单参数不为空，则返回表单参数。否则返回requestBody。
    * 两个不可共用。
    * */
    public Object getRequestBody(){
        if(this.requestBody!=null&&formParamMap!=null){
            throw new IllegalArgumentException("can't use body and form param together");
        }
        return formParamMap!=null?formParamMap:requestBody;
    }

}
