package cainsgl.core.network.server;

import cainsgl.core.system.loader.MutClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MutServerBuilder
{
    Object mutServer;
    ClassLoader mutClassLoader;
    Class<?> mutServerClass;

    //基础信息
    public MutServerBuilder(Object o, ClassLoader cl)
    {
        mutServer = o;
        mutServerClass = o.getClass();
        mutClassLoader = cl;
    }

    public static MutServerBuilder build() throws Exception
    {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        MutClassLoader mutClassLoader = new MutClassLoader(originalClassLoader);
        Class<?> aClass = mutClassLoader.loadClass("cainsgl.core.network.server.MutServer");
        Constructor<?> constructor = aClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object o = constructor.newInstance();
        return new MutServerBuilder(o, mutClassLoader);
    }

    public MutServerBuilder start() throws Exception
    {
        Method method = mutServerClass.getMethod("start");
        method.setAccessible(true);
        method.invoke(mutServer);
        return this;
    }

    public void stop() throws Exception
    {
        Method method = mutServerClass.getMethod("stop");
        method.setAccessible(true);
        method.invoke(mutServer);
    }

}
