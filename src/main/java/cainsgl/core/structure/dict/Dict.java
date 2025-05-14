package cainsgl.core.structure.dict;

import cainsgl.core.config.MutConfiguration;
import cainsgl.core.data.MutObj;

public class Dict extends AbstractDict {

    public Dict(int capacity) {
        if( capacity > 0 && (capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("dict capacity must be 2^n");
        }
        super(capacity);
    }

    public Dict() {
        this(MutConfiguration.initial_capacity);
    }

    @Override
    public boolean isEmpty() {
        boolean empty = super.isEmpty();
        rehash();
        return empty;
    }

    @Override
    public int size() {
        int size = super.size();
        rehash();
        return size;
    }


    @Override
    public MutObj get(byte[] o) {
        MutObj mutObj = super.get(o);
        rehash();
        return mutObj;
    }

    @Override
    public MutObj put(byte[] o, MutObj o2) {
        MutObj put = super.put(o, o2);
        rehash();
        return put;
    }

    @Override
    public MutObj remove(byte[] o) {
        MutObj remove = super.remove(o);
        rehash();
        return remove;
    }

}
