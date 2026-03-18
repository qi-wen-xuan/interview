package com.example.interview.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class FactoryBeanObjectTypeFixPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] names = beanFactory.getBeanDefinitionNames();
        for (String name : names) {
            BeanDefinition bd = beanFactory.getBeanDefinition(name);
            Object attr = bd.getAttribute("factoryBeanObjectType");
            if (attr instanceof String) {
                String className = (String) attr;
                try {
                    Class<?> cls = Class.forName(className);
                    bd.setAttribute("factoryBeanObjectType", cls);
                } catch (ClassNotFoundException e) {
                    // 忽略：如果类无法解析则跳过（保持原样），并可记录日志
                    System.err.println("FactoryBeanObjectTypeFixPostProcessor: cannot resolve " + className + " for bean " + name);
                }
            }
        }
    }
}