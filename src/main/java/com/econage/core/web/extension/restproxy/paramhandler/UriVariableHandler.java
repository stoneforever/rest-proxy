package com.econage.core.web.extension.restproxy.paramhandler;

import com.econage.core.web.extension.restproxy.RestRequestContext;
import com.econage.core.web.extension.restproxy.annotations.RestUriVariable;

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
