package com.emc.xtest.analyzer.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.emc.xtest.analyzer.web.HomeController;

/*
 * Convenience class for centralized access to services... easier to remember
 * one factory.
 */
public class ModuleFactory
{
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static ModuleFactory instance = new ModuleFactory();

    /**
     * Returns a singleton instance of XMPPServer.
     * 
     * @return an instance.
     */
    public static ModuleFactory getInstance()
    {

        return instance;
    }

    /**
     * All modules loaded by this server
     */

    private static Map<Class<? extends Object>, ApplicationContextModule> modules = new LinkedHashMap<Class<? extends Object>, ApplicationContextModule>();

    /**
     * Loads a module.
     * 
     * @param module
     *            the name of the class that implements the Module interface.
     */
    protected static void loadModule(String module)
    {
        try
        {
            Class<? extends Object> modClass = ApplicationContextModule.getBean(module).getClass();
            ApplicationContextModule mod = (ApplicationContextModule) modClass.newInstance();
            modules.put(modClass, mod);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("load Module Exception:", e);
        }
    }

    protected static void initModules(ApplicationContext applicationContext)
    {
        for(ApplicationContextModule module : modules.values())
        {
            boolean isInitialized = false;
            try
            {
                module.initialize(applicationContext);
                isInitialized = true;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                // Remove the failed initialized module
                modules.remove(module.getClass());
                if(isInitialized)
                {
                    module.stop();
                    module.destroy();
                }
                logger.error("init Modules Exception:", e);
            }
        }
    }

    /**
     * <p>
     * Following the loading and initialization of all the modules this method
     * is called to iterate through the known modules and start them.
     * </p>
     */
    protected static void startModules()
    {
        for(ApplicationContextModule module : modules.values())
        {
            boolean started = false;
            try
            {
                module.start();
            }
            catch(Exception e)
            {
                if(started && module != null)
                {
                    module.stop();
                    module.destroy();
                }
                logger.error("start Modules Exception:", e);
            }
        }
    }

    /**
     * <p>
     * Following the loading and initialization of all the modules this method
     * is called to iterate through the known modules and start them.
     * </p>
     */
    protected static void stopModules()
    {
        if(modules.isEmpty())
        {
            return;
        }
        for(ApplicationContextModule module : modules.values())
        {
            module.stop();
            module.destroy();
        }
        modules.clear();
    }

    public P4ConnectionService getP4ConnectionService()
    {
        return (P4ConnectionService) ApplicationContextModule.getBean(P4ConnectionService.class.getSimpleName());
    }
}