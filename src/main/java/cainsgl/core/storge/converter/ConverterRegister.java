package cainsgl.core.storge.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConverterRegister
{
    private static final Map<Class,Converter> CONVERTER_MAP = new HashMap<>();
    @SuppressWarnings("unchecked")
    public static  <T> Converter<T> getConverter(Class<T> clazz)
    {
        return CONVERTER_MAP.get(clazz);
    }
    public static <T> void register(Converter<T> converter) {
        Objects.requireNonNull(converter, "Converter cannot be null");
        Type typeArg = getType(converter);
        if (!(typeArg instanceof Class)) {
            throw new IllegalArgumentException("Unsupported type parameter: must be a Class");
        }
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) typeArg;
        if (CONVERTER_MAP.containsKey(targetClass)) {
            throw new IllegalArgumentException("Converter for " + targetClass.getName() + " already registered");
        }
        CONVERTER_MAP.put(targetClass, converter);
    }

    private static <T> Type getType(Converter<T> converter)
    {
        // 获取当前对象的类的泛型父类（即 Converter<T>）
        Type superclassType = converter.getClass().getGenericSuperclass();

        // 检查是否是参数化类型（例如 Converter<String>）
        if (!(superclassType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Converter subclass must specify generic type parameter");
        }

        // 获取实际类型参数（第一个参数是 T）
        ParameterizedType parameterizedType = (ParameterizedType) superclassType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return typeArguments[0];
    }
}
