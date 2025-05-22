package cainsgl.core.storge.converter;

public abstract class Converter<T>
{
    public Converter()
    {
        ConverterRegister.register(this);
    }

    public abstract byte[] toBytes(T obj);

    public abstract T fromBytes(byte[] bytes);
}
