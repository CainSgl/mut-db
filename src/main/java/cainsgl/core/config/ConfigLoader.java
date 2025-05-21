package cainsgl.core.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
    public static void loadConfig(String configPath) throws Exception {
        Document doc = parseXml(configPath);

        // 加载各模块配置
        loadModuleConfig(doc, "base", MutConfiguration.class);
        loadModuleConfig(doc, "rdb", MutConfiguration.RDB.class);
        loadModuleConfig(doc, "aof", MutConfiguration.AOF.class);
        loadModuleConfig(doc, "gc", MutConfiguration.GC.class);
    }

    private static Document parseXml(String configPath) throws Exception {
        File file = new File(configPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    // 加载各个模块的配置
    private static void loadModuleConfig(Document doc, String tagName, Class<?> configClass)
            throws Exception {
        Element element = (Element) doc.getElementsByTagName(tagName).item(0);
        if (element == null) return;

        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element child = (Element) nodes.item(i);
                String xmlTag = child.getTagName();
                String value = child.getTextContent().trim();

                // 关键转换：XML小驼峰 → Java大写下划线
                String fieldName = camelToSnake(xmlTag).toUpperCase();

                setFieldValue(configClass, fieldName, value);
            }
        }
    }

    // 小驼峰转大写下划线
    private static String camelToSnake(String camel) {
        return camel.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
    }

    // 为字段设置值
    private static void setFieldValue(Class<?> clazz, String fieldName, String value)
            throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            Class<?> type = field.getType();
            if (type == int.class || type == Integer.class) {
                field.set(null, Integer.parseInt(value));
            } else if (type == long.class || type == Long.class) {
                field.set(null, Long.parseLong(value));
            } else if (type == float.class || type == Float.class) {
                field.set(null, Float.parseFloat(value));
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(null, Boolean.parseBoolean(value));
            } else {
                field.set(null, value);
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(
                    "配置字段缺失: " + clazz.getSimpleName() + "." + fieldName
            );
        }
    }

    // 加载配置的managers；返回List集合
    public static List<String> loadManagers(String configPath) throws Exception {
        Document doc = parseXml(configPath);
        List<String> managers = new ArrayList<>();
        NodeList managerNodes = doc.getElementsByTagName("manager");
        for (int i = 0; i < managerNodes.getLength(); i++) {
            managers.add(managerNodes.item(i).getTextContent().trim());
        }
        return managers;
    }
}