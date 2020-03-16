package com.econage.core.web.extension.restproxy.paramhandler;

import com.econage.core.web.extension.restproxy.RestRequestContext;

public class RequestBodyHandler extends RestMethodParamHandler {

    protected RequestBodyHandler(int methodParamIdx) {
        super(methodParamIdx);
    }

    @Override
    void doParamHandle(RestRequestContext requestContext, Object param) {
        requestContext.setRequestBody(param);
    }
}
