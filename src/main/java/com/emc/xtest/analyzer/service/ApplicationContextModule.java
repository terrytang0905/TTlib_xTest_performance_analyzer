package com.emc.xtest.analyzer.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhenjt
 * 
 */

//@Configuration 
//@ImportResource("classpath:application-context.xml") 
public class ApplicationContextModule 
{

    private static ConfigurableApplicationContext s_appContext = null;

    static
    {
        s_appContext = new  AnnotationConfigApplicationContext(ApplicationContextModule.class);
    }


    public static Object getBean(String p_bean)
    {
        return s_appContext.getBean(p_bean);
    }

    public static ApplicationContext getContext()
    {
        return s_appContext;
    }


    public void initialize(ApplicationContext applicationContext)
    {
        // TODO Auto-generated method stub

    }

 
    public void start()
    {
        // TODO Auto-generated method stub
        s_appContext.start();
    }


    public void stop()
    {
        // TODO Auto-generated method stub
        s_appContext.stop();
    }

    public void destroy()
    {
        // TODO Auto-generated method stub
        s_appContext.close();
    }

}
