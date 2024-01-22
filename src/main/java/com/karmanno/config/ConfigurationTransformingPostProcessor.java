package com.karmanno.config;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationTransformingPostProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory beanFactory;
    private final Map<Class<?>, PropertiesDictionary> propertiesDictionaryMap;

    public ConfigurationTransformingPostProcessor(ConfigurableListableBeanFactory beanFactory, List<PropertiesDictionary> dictionaries) {
        this.beanFactory = beanFactory;
        this.propertiesDictionaryMap = dictionaries.stream()
                .collect(Collectors.toMap(
                        PropertiesDictionary::getConfigurationPropertiesClass,
                        dictionary -> dictionary
                ));
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isConfigurationPropertiesBean(beanFactory, beanName)) {
            PropertiesDictionary propertiesDictionary = propertiesDictionaryMap.get(bean.getClass());

            if (propertiesDictionary != null) {
                String fieldName = propertiesDictionary.getPropertyField();

                try {
                    Map<String, String> propertiesApplied = (Map<String, String>) FieldUtils.readField(bean, fieldName, true);
                    for (String key : propertiesApplied.keySet()) {

                        String value = propertiesApplied.get(key);
                        String mappedKey = propertiesDictionary.getPropertyKey(key);
                        if (!key.equals(mappedKey) && mappedKey != null) {
                            propertiesApplied.put(
                                    mappedKey,
                                    value
                            );
                            propertiesApplied.remove(key);
                        }
                    }
                    FieldUtils.writeField(bean, fieldName, propertiesApplied, true);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Couldn't transform the config properties for " + beanName, e);
                }
            }
        }
        return bean;
    }

    private static boolean isConfigurationPropertiesBean(ConfigurableListableBeanFactory beanFactory, String beanName) {
        try {
            if (beanFactory.getBeanDefinition(beanName).isAbstract()) {
                return false;
            }
            if (beanFactory.findAnnotationOnBean(beanName, ConfigurationProperties.class) != null) {
                return true;
            }
            Method factoryMethod = findFactoryMethod(beanFactory, beanName);
            return findMergedAnnotation(factoryMethod, ConfigurationProperties.class).isPresent();
        }
        catch (NoSuchBeanDefinitionException ex) {
            return false;
        }
    }

    private static Method findFactoryMethod(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
            if (beanDefinition instanceof RootBeanDefinition rootBeanDefinition) {
                return rootBeanDefinition.getResolvedFactoryMethod();
            }
        }
        return null;
    }

    private static <A extends Annotation> MergedAnnotation<A> findMergedAnnotation(AnnotatedElement element,
                                                                                   Class<A> annotationType) {
        return (element != null) ? MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType)
                : MergedAnnotation.missing();
    }

}
