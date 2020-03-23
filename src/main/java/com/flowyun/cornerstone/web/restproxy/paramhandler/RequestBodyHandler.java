package com.flowyun.cornerstone.web.restproxy.paramhandler;

import com.flowyun.cornerstone.web.restproxy.RestRequestContext;

public class RequestBodyHandler extends RestMethodParamHandler {

    protected RequestBodyHandler(int methodParamIdx) {
        super(methodParamIdx);
    }

    @Override
    void doParamHandle(RestRequestContext requestContext, Object param) {
        requestContext.setRequestBody(param);
    }
}
