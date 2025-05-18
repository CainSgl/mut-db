package cainsgl.core.data;

import cainsgl.core.data.key.ByteFastKey;

public abstract class AbstractByteObj implements ByteObj
{
    private static int id = -1;

    private static void setId(int i)
    {
        if (id != -1)
        {
            throw new UnsupportedOperationException("id already set");
        }
        id = i;
    }

    int getId()
    {
        return id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {return false;}
        if (obj == this) {return true;}
        if (obj instanceof ByteObj other)
        {
            byte[] otherValue = other.getBytes();
            byte[] thisValue = getBytes();

            if (otherValue == thisValue) {return true;}
            if (otherValue.length != thisValue.length) {return false;}
            for (int i = 0; i < thisValue.length; i++)
            {
                if (otherValue[i] != thisValue[i])
                {
                    return false;
                }
            }
            return true;

        }
        return false;
    }
}