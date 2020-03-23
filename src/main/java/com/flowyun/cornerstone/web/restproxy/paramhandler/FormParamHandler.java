package com.flowyun.cornerstone.web.restproxy.paramhandler;


import com.flowyun.cornerstone.web.restproxy.RestRequestContext;
import com.flowyun.cornerstone.web.restproxy.annotations.RestFormParam;

import java.util.Map;

public class FormParamHandler extends RestMethodParamHandler {
    private final RestFormParam formParam;
    protected FormParamHandler(
            int methodParamIdx
    ) {
        this(methodParamIdx,null);
    }
    protected FormParamHandler(
            int methodParamIdx,
            RestFormParam formParam
    ) {
        super(methodParamIdx);
        this.formParam = formParam;
    }

    @Override
    void doParamHandle(RestRequestContext requestContext, Object param) {
        if(param instanceof Map){
            requestContext.addFormParam((Map<String,Object>)param);
        }else{
            requestContext.addFormParam(formParam.value(),param);
        }
    }
}
