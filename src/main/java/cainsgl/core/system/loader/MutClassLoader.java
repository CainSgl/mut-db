package cainsgl.core.system.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class MutClassLoader extends URLClassLoader
{

    public MutClassLoader(ClassLoader parent) throws MalformedURLException
    {
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++)
        {
            File file = new File(paths[i]);
            if (file.exists())
            {
                urls[i] = file.toURI().toURL();
            }
        }
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
//        // NOTE (简单方案，存在问题) 排除该类加载器对MutConfig的加载，委托给父加载器加载
//        if (className.startsWith("cainsgl.core.config.MutConfiguration")) {
//            return super.loadClass(className, resolve); // 使用父加载器
//        }

        synchronized (getClassLoadingLock(className))
        {
            Class<?> loadedClass = findLoadedClass(className);
            if (loadedClass != null)
            {
                if (resolve) {resolveClass(loadedClass);}
                return loadedClass;
            }
            if (className.startsWith("cainsgl"))
            {
                try
                {
                    Class<?> clazz = findClass(className);
                    if (resolve) {resolveClass(clazz);}
                    return clazz;
                } catch (ClassNotFoundException e)
                {
                    throw new ClassNotFoundException("类 " + className + " 未在当前加载器路径中找到");
                }
            }
            try
            {
                return super.loadClass(className, resolve);
            } catch (ClassNotFoundException e)
            {
                throw e;
            }
        }
    }
}
