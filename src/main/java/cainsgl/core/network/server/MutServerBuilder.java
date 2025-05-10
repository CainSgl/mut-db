package cainsgl.core.network.server;

import cainsgl.core.system.loader.MutClassLoader;

public class MutServerBuilder
{
    Object mutServer;
    ClassLoader mutClassLoader;
    Class<?> mutServerClass;
    //基础信息
    public MutServerBuilder(Object o,ClassLoader cl)
    {
        mutServer = o;
        mutServerClass=o.getClass();
        mutClassLoader=cl;
    }

    public static MutServerBuilder build() throws Exception
    {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        MutClassLoader mutClassLoader = new MutClassLoader(originalClassLoader);
        Class<?> aClass = mutClassLoader.loadClass("cainsgl.core.network.server.MutServer");
        Object o = aClass.getConstructor().newInstance();
        return new MutServerBuilder(o, mutClassLoader);
    }
    public void start() throws Exception
    {
        mutServerClass.getMethod("start").invoke(mutServer);
    }

    public void stop() throws Exception
    {
        mutServerClass.getMethod("stop").invoke(mutServer);
    }

}
