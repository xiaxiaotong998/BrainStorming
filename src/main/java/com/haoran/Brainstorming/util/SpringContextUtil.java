package com.haoran.Brainstorming.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;

    public SpringContextUtil() {
        super();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     *
     * @param beanName
     * @param clazz
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }

    /**
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        T t = null;
        Map<String, T> map = context.getBeansOfType(clazz);
        for (Map.Entry<String, T> entry : map.entrySet()) {
            t = entry.getValue();
        }
        return t;
    }

    /**
     *
     * @param beanName
     * @return
     */
    public static boolean containsBean(String beanName) {
        return context.containsBean(beanName);
    }

    /**
     *
     * @param beanName
     * @return
     */
    public static boolean isSingleton(String beanName) {
        return context.isSingleton(beanName);
    }

    /**
     *
     * @param beanName
     * @return
     */
    public static Class getType(String beanName) {
        return context.getType(beanName);
    }

}
