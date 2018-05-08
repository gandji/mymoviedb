package org.gandji.mymoviedb;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 13/02/2018.
 *
 * hack to get access to application context from non-spring managed objects!
 * see:
 * https://stackoverflow.com/questions/19302030/how-to-access-spring-beans-from-objects-not-created-by-spring
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() { return applicationContext;}
}
