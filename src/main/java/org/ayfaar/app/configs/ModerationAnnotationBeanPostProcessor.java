package org.ayfaar.app.configs;

import org.apache.commons.collections.map.HashedMap;
import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.moderation.ModerationService1;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModerationAnnotationBeanPostProcessor implements BeanPostProcessor{

    @Autowired
    ModerationService moderationService;
    private Map<String,Class> map = new HashMap<>();
    private List<Method> methods = new ArrayList<Method>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(Moderated.class)) {
                map.put(beanName, beanClass);
                methods.add(declaredMethod);
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) {
            ClassLoader classLoader = beanClass.getClassLoader();
            Class[] interfaces = beanClass.getInterfaces();
            Object proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    Object retVal = null;

                    for (Method method1 : methods) {
                        if (method1.getName().equals(method.getName()))
                        moderationService.checkMethod(method1.getAnnotation(Moderated.class), method1.getParameters());
                        //retVal = method1.invoke(bean, args);
                    }
                    retVal = method.invoke(bean, args);
                    return retVal;
                }
            });
            return proxyInstance;
        }
        return bean;
    }

}
