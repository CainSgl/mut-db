package cainsgl.core.structure.dict;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.MutObj;
import cainsgl.core.structure.dict.entry.MNode;
import cainsgl.core.utils.HashUtil;

public abstract class AbstractDict
{
    DictHashTable[] dts = new DictHashTable[2];
    int rehash = -1;

    protected void startRehash()
    {
        float loadFactor = (float) dts[0].used / (float) dts[0].entries.length;
        if (loadFactor > MutConfiguration.MAX_Load_Factor)
        {
            dts[1] = new DictHashTable((dts[0].mask + 1) * 2);
            rehash = 0;
            lastStart = 0;
        } else if (loadFactor < MutConfiguration.MIN_Load_Factor)
        {
            dts[1] = new DictHashTable((dts[0].mask + 1) / 2);
            rehash = 0;
            lastStart = 0;
        }
    }

    public AbstractDict(int capacity)
    {
        dts[0] = new DictHashTable(capacity);
    }

    public int size()
    {
        int len = 0;
        if (dts[0] != null)
        {
            len = dts[0].used;
        }
        if (dts[1] != null)
        {
            len += dts[1].used;
        }
        return len;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    int lastStart;

    public void rehash()
    {
        if (rehash == -1)
        {
            return;
        }
        int i = MutConfiguration.rehashNum;
        while (true)
        {
            //必须删除至少一个，除非全部都没删了，size为0
            if (dts[0].used == 0)
            {
                rehashEnd();
                return;
            }
            MNode mNode = dts[0].removeLast(lastStart);
            if (mNode == null)
            {
                lastStart ++;
            } else
            {
                dts[1].put(mNode);
                i--;
                if (i < 1)
                {
                    //下次再来
                    return;
                }
            }
        }
    }

    public void rehashEnd()
    {
        dts[0] = dts[1];
        dts[1] = null;
        rehash = -1;

    }

    public MutObj get(byte[] o)
    {
        if (rehash == -1)
        {
            return dts[0].get(o);
        } else
        {
            if (dts[0].used < dts[1].used)
            {
                return getByAllDt(dts[1], dts[0], o);
            } else
            {
                return getByAllDt(dts[0], dts[1], o);
            }

        }
    }

    private MutObj getByAllDt(DictHashTable mainTable, DictHashTable viceTable, byte[] o)
    {
        int code = HashUtil.fastHash(o);
        MutObj mutObj = mainTable.get(o, code);
        if (mutObj != null)
        {
            return mutObj;
        }
        return viceTable.get(o, code);
    }

    public MutObj put(byte[] o, MutObj o2)
    {
        if (rehash == -1)
        {
            MutObj put = dts[0].put(o, o2);
            if (put == null)
            {
                startRehash();
            }
            return put;
        } else
        {
            return dts[1].put(o, o2);
        }
    }

    public MutObj remove(byte[] o)
    {
        if (rehash == -1)
        {
            return dts[0].remove(o);
        } else
        {
            if (dts[0].used < dts[1].used)
            {
                return removeAllDt(dts[1], dts[0], o);
            } else
            {
                return removeAllDt(dts[0], dts[1], o);
            }
        }
    }

    private MutObj removeAllDt(DictHashTable mainTable, DictHashTable viceTable, byte[] o)
    {
        int code = HashUtil.fastHash(o);
        MutObj mutObj = mainTable.remove(o, code);
        if (mutObj != null)
        {
            return mutObj;
        }
        return viceTable.remove(o, code);
    }

    public void clear()
    {
        dts = null;
    }

}
