package cainsgl.core.excepiton;

public class MutServerStartException extends RuntimeException
{
    public MutServerStartException(Exception e)
    {
        super(e);
    }
    public MutServerStartException(String message)
    {
        super(message);
    }
}
