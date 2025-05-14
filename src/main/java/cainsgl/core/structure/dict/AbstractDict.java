package cainsgl.core.structure.dict;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.MutObj;
import cainsgl.core.structure.dict.entry.MNode;
import cainsgl.core.utils.HashUtil;

public abstract class AbstractDict
{
    DictHashTable[] dts = new DictHashTable[2];
    int rehash = -1;

    //
    protected void startRehash() {
        // 计算负载
        float loadFactor = (float) dts[0].used / (float) dts[0].entries.length;
        // 检查负载；创建第二张哈希表，启动再哈希
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

    public AbstractDict(int capacity) {
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

    // 再哈希逻辑；旧表数据迁移到新表
    public void rehash() {
        if (rehash == -1) {
            return;
        }
        //
        int i = MutConfiguration.rehashNum;
        while (true) {
            // 必须删除至少一个，除非全部都没删了，size为0
            if (dts[0].used == 0) {
                rehashEnd();
                return;
            }
            MNode mNode = dts[0].removeLast(lastStart);
            if (mNode == null) {
                lastStart ++;
            } else {
                dts[1].put(mNode);
                i--;
                if (i < 1) {
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

    public MutObj get(byte[] o) {
        // 未启动再哈希；从主表中拿数据
        if (rehash == -1) {
            return dts[0].get(o);
        } else {
            // 根据 used 判断先从那一张表拿数据；节省时间
            if (dts[0].used < dts[1].used) {
                return getByAllDt(dts[1], dts[0], o);
            } else {
                return getByAllDt(dts[0], dts[1], o);
            }
        }
    }

    // 从所有表中查找数据；因为启动了再哈希，所以存在两张表，渐进式哈希，数据分散在两张表中
    private MutObj getByAllDt(DictHashTable mainTable, DictHashTable viceTable, byte[] o) {
        // 先尝试从主表中拿数据
        int code = HashUtil.fastHash(o);
        MutObj mutObj = mainTable.get(o, code);
        if (mutObj != null) {
            return mutObj;
        }
        return viceTable.get(o, code);
    }

    public MutObj put(byte[] o, MutObj o2) {
        // 1. 未开启再哈希；尝试向主表存数据
        if (rehash == -1) {
            MutObj put = dts[0].put(o, o2);
            // 向主表存数据失败；尝试开启再哈希
            if (put == null) {
                startRehash();
            }
            return put;
        }
        // 2. 已经开启再哈希；向新表中存数据
        else {
            return dts[1].put(o, o2);
        }
    }

    // 删除数据；与获取数据同理
    public MutObj remove(byte[] key)
    {
        if (rehash == -1) {
            return dts[0].remove(key);
        } else {
            if (dts[0].used < dts[1].used)
            {
                return removeAllDt(dts[1], dts[0], key);
            } else
            {
                return removeAllDt(dts[0], dts[1], key);
            }
        }
    }

    // 从两张表中删除数据；与从两张表中获取数据同理
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

    public void clear() {
        dts = null;
    }
}
