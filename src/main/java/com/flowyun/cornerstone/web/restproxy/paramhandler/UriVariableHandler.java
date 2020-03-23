package com.flowyun.cornerstone.web.restproxy.paramhandler;

import com.flowyun.cornerstone.web.restproxy.RestRequestContext;
import com.flowyun.cornerstone.web.restproxy.annotations.RestUriVariable;

public class UriVariableHandler extends RestMethodParamHandler {

    private final RestUriVariable annotation;
    protected UriVariableHandler(
            int methodParamIdx,
            RestUriVariable annotation
    ) {
        super(methodParamIdx);
        this.annotation = annotation;
    }

    @Override
    void doParamHandle(RestRequestContext requestContext, Object param) {
        requestContext.addUriVariable(annotation.value(),param);
    }
}
