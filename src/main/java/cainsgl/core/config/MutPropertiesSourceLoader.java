package cainsgl.core.config;

import cainsgl.core.network.server.MutServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MutPropertiesSourceLoader
{

    public static final Logger log= LoggerFactory.getLogger(MutPropertiesSourceLoader.class);

    private static  MutPropertiesSourceLoader instance = new MutPropertiesSourceLoader();
    private static int count=4;
    public static MutPropertiesSourceLoader getInstance()
    {

        if(count==0)
        {
            throw new IllegalArgumentException("无法再次获取instance");
        }
        count--;
        if(count==0)
        {
            instance = null;
        }
        return instance;
    }

    public void loadConfigByEnvrioment() throws IOException
    {
        URL resource = MutConfiguration.class
                .getClassLoader()
                .getResource("mut.properties");
        if (resource == null)
        {
            log.error("未读取到配置文件");
            throw new IllegalArgumentException("未读取到配置文件");
        }
        String file = resource.getFile();
        loadConfig(file);
    }

    public void loadConfig(String filePath) throws IOException
    {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath))
        {
            properties.load(fis);
            Map<String, Object> rootMap = new HashMap<>();
            // 将Properties的键值对转换为嵌套Map（支持点分隔符，如"db.url"转换为{"db": {"url": "xxx"}}）
            for (String key : properties.stringPropertyNames())
            {
                Map<String, Object> current = rootMap;
                String[] keyParts = key.split("\\.");
                for (int i = 0; i < keyParts.length; i++)
                {
                    String part = keyParts[i];
                    if (i == keyParts.length - 1)
                    {
                        current.put(part, properties.getProperty(key));
                    } else
                    {
                        if (!current.containsKey(part) || !(current.get(part) instanceof Map))
                        {
                            current.put(part, new HashMap<String, Object>());
                        }
                        current = (Map<String, Object>) current.get(part);
                    }
                }
            }
            initMap(rootMap);
        } catch (IOException e)
        {
            throw new IOException("加载properties文件失败: " + e.getMessage(), e);
        }


    }




    private Map<String, Object> baseMap;
    private Map<String, Object> rdbMap;
    private Map<String, Object> aofMap;
    private Map<String, Object> gcMap;

    public void initMap(Map<String, Object> config)
    {
        baseMap = (Map<String, Object>) config.get("base");
        rdbMap = (Map<String, Object>) config.get("rdb");
        aofMap = (Map<String, Object>) config.get("aof");
        gcMap = (Map<String, Object>) config.get("gc");
    }
    public int getBaseInfoByDefault(String key,int defaultValue)
    {
        if(baseMap==null)
        {
            return defaultValue;
        }
        Object o = baseMap.get(key);
        if(o == null)
        {
            log.warn("该Number配置无值,base.{}",key);
            return defaultValue;
        }
        log.info("load config base.{}>{}",key,o);
        return Integer.parseInt(o.toString());
    }
    public boolean getBaseInfoByDefault(String key,boolean defaultValue)
    {
        if(baseMap==null)
        {
            return defaultValue;
        }

        Object o = baseMap.get(key);
        if(o == null)
        {
            log.warn("该Number配置无值,base.{}",key);
            return defaultValue;
        }

        return Boolean.parseBoolean(o.toString());
    }

    public float getBaseInfoByDefault(String key,float defaultValue)
    {
        if(baseMap==null)
        {
            return defaultValue;
        }

        Object o = baseMap.get(key);
        if(o == null)
        {
            log.warn("该Number配置无值,base.{}",key);
            return defaultValue;
        }
        log.info("load config base.{}>{}",key,o);
        return Float.parseFloat(o.toString());
    }
    public String getBaseInfoByDefault(String key,String defaultValue)
    {
        if(baseMap==null)
        {
            return defaultValue;
        }
        Object o = baseMap.get(key);
        if(o == null)
        {
            log.warn("该String配置无值,base.{}",key);
            return defaultValue;
        }
        log.info("load config base.{}>{}",key,o);
        return o.toString();
    }

    public int getRDBInfoByDefault(String key,int defaultValue)
    {
        if(rdbMap==null)
        {
            return defaultValue;
        }
        Object o = rdbMap.get(key);
        if(o == null)
        {
            log.warn("该Number配置无值,rdb.{}",key);
            return defaultValue;
        }
        log.info("load config rdb.{}>{}",key,o);
        return Integer.parseInt(o.toString());
    }


    public String getRDBInfoByDefault(String key,String defaultValue)
    {
        if(rdbMap==null)
        {
            return defaultValue;
        }
        Object o = rdbMap.get(key);
        if(o == null)
        {
            log.warn("该String配置无值,rdb.{}",key);
            return defaultValue;
        }
        log.info("load config rdb.{}>{}",key,o);
        return o.toString();
    }
    public int getGCInfoByDefault(String key,int defaultValue)
    {
        if(gcMap==null)
        {
            return defaultValue;
        }
        Object o = gcMap.get(key);
        if(o == null)
        {
            log.warn("该Number配置无值,gc.{}",key);
            return defaultValue;
        }
        log.info("load config gc.{}>{}",key,o);
        return Integer.parseInt(o.toString());
    }


    public String getGCInfoByDefault(String key,String defaultValue)
    {
        if(gcMap==null)
        {
            return defaultValue;
        }
        Object o = gcMap.get(key);
        if(o == null)
        {
            log.warn("该String配置无值,gc.{}",key);
            return defaultValue;
        }
        log.info("load config gc.{}>{}",key,o);
        return o.toString();
    }
    public int getAOFInfoByDefault(String key,int defaultValue)
    {
        if(aofMap==null)
        {
            return defaultValue;
        }
        Object o = aofMap.get(key);
        if(o == null)
        {
            log.warn("该Number配置无值,aof.{}",key);
            return defaultValue;
        }
        log.info("load config aof.{}>{}",key,o);
        return Integer.parseInt(o.toString());
    }


    public String getAOFInfoByDefault(String key,String defaultValue)
    {
        if(aofMap==null)
        {
            return defaultValue;
        }
        Object o = aofMap.get(key);
        if(o == null)
        {
            log.warn("该String配置无值,aof.{}",key);
            return defaultValue;
        }
        log.info("load config aof.{}>{}",key,o);
        return o.toString();
    }

}
