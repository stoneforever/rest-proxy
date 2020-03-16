package com.econage.core.web.extension.restproxy.scanner;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

public class AnnotationTypeInHierarchyFilter extends AnnotationTypeFilter {
    public AnnotationTypeInHierarchyFilter(Class<? extends Annotation> annotationType) {
        super(annotationType);
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        return metadata.isAnnotated(getAnnotationType().getName());
    }

}
