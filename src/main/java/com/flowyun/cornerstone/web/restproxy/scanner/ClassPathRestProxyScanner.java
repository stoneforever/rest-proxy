package com.flowyun.cornerstone.web.restproxy.scanner;

import com.flowyun.cornerstone.web.restproxy.annotations.RestProxy;
import com.flowyun.cornerstone.web.restproxy.util.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ClassPathRestProxyScanner extends ClassPathBeanDefinitionScanner {
    protected final Log logger = LogFactory.getLog(getClass());

    private boolean lazyInitialization;

    private Class<?> markerInterface;

    private boolean findAnnotationInHierarchy = true;

    private Class<? extends Annotation> annotationClass = RestProxy.class;
    private String annotationClassName = RestProxy.class.getName();

    public ClassPathRestProxyScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public void setLazyInitialization(boolean lazyInitialization) {
        this.lazyInitialization = lazyInitialization;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    public void setFindAnnotationInHierarchy(boolean findAnnotationInHierarchy) {
        this.findAnnotationInHierarchy = findAnnotationInHierarchy;
    }

    public void registerFilters() {
        /*
        * 只识别带了RestProxy注解的
        * */
        if(findAnnotationInHierarchy){
            addIncludeFilter(new AnnotationTypeInHierarchyFilter(annotationClass));
        }else{
            addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        }

        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No Rest proxy was found in '" + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            logger.debug("Creating RestProxyFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName
                    + "' proxyInterface");

            // 原始类信息是接口类信息
            // 实际bean的类信息是RestProxyFactoryBean
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.setBeanClass(RestProxyFactoryBean.class);

            AnnotationMetadata metadata = ((AnnotatedBeanDefinition)definition).getMetadata();
            Map<String, Object> restProxyInfo = metadata.getAnnotationAttributes(annotationClassName);

            definition.getPropertyValues().add(
                    "restProxyTarget",
                    new RuntimeBeanReference((String) restProxyInfo.get("value"))
            );

            String[] interceptors = (String[])restProxyInfo.get("interceptors");
            if(ArrayUtils.isNotEmpty(interceptors)){
                ManagedList<Object> interceptorList = new ManagedList<>();
                for(String interceptorName : interceptors){
                    interceptorList.add(new RuntimeBeanReference(interceptorName));
                }
                definition.getPropertyValues().add("interceptors", interceptorList);
            }

            definition.setLazyInit(lazyInitialization);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping RestProxyFactoryBean with name '" + beanName + "' and '"
                    + beanDefinition.getBeanClassName() + "' mapperInterface" + ". Bean already defined with the same name!");
            return false;
        }
    }
}
