package cainsgl.core.utils.adapter;

import cainsgl.core.command.processor.CommandProcessor;

import cainsgl.core.network.config.NetWorkConfig;


public class CommandAdapter
{
    int dataNumber;
    //这里是记录上次读到什么位置了
    int offset;
    byte[][] Args;
    int argCount = 0;
    byte[] cmd;
    RESP2CMDAdapter cmdAdapter;
    RESP2InfoAdapter argInfoAdapter;
    RESP2NumberAdapter argLenAdapter;

    public CommandAdapter(int num, int offset)
    {
        dataNumber = num;
        Args = new byte[num - 1][];
        cmdAdapter = new RESP2CMDAdapter();
        this.offset = offset;
    }

    interface RESP2Adapter
    {
        int tryDecode(int offset, byte[] data);

        int getExpectedOffset();
    }

    static class RESP2NumberAdapter implements RESP2Adapter
    {
        int expectedOffset;

        int num;

        @Override
        public int tryDecode(int offset, byte[] data)
        {
            if (data[offset] == '$')
            {
                offset++;
            }
            while (true)
            {
                if (offset == data.length)
                {
                    expectedOffset = offset;
                    return -1;
                }
                if (data[offset] == '\r')
                {
                    if (offset + 1 == data.length)
                    {
                        expectedOffset = data.length;
                        return -1;
                    }
                    if (data[offset + 1] == '\n')
                    {
                        expectedOffset = offset + 2;
                        return num;
                    }
                }
                num = num * 10 + data[offset] - '0';

                offset++;
            }
        }

        @Override
        public int getExpectedOffset()
        {
            return expectedOffset;
        }
    }

    static class RESP2InfoAdapter implements RESP2Adapter
    {

        int expectedOffset;
        byte[] infoBytes;

        private RESP2InfoAdapter(int len, int offset)
        {
            infoBytes = new byte[len];
            expectedOffset = offset + len + 2;
        }

        @Override
        public int tryDecode(int offset, byte[] data)
        {
            if (offset + infoBytes.length < data.length)
            {
                //可读
                System.arraycopy(data, offset, infoBytes, 0, infoBytes.length);
                return 1;
            } else
            {
                return -1;
            }
        }

        @Override
        public int getExpectedOffset()
        {
            return expectedOffset;
        }

        public byte[] getInfoBytes()
        {
            return infoBytes;
        }
    }

    static class RESP2CMDAdapter implements RESP2Adapter
    {
        RESP2InfoAdapter infoAdapter;
        RESP2NumberAdapter numberAdapter = new RESP2NumberAdapter();

        @Override
        public int tryDecode(int offset, byte[] data)
        {
            if (numberAdapter == null)
            {
                return infoAdapter.tryDecode(offset, data);
            }
            int i = numberAdapter.tryDecode(offset, data);
            if (i == -1)
            {
                return -1;
            } else
            {
                infoAdapter = new RESP2InfoAdapter(i, numberAdapter.getExpectedOffset());
                int i1 = infoAdapter.tryDecode(numberAdapter.getExpectedOffset(), data);
                numberAdapter = null;
                return i1;
            }
        }

        @Override
        public int getExpectedOffset()
        {
            if (numberAdapter == null)
            {
                return infoAdapter.getExpectedOffset();
            } else
            {
                return numberAdapter.getExpectedOffset();
            }
        }

        public byte[] getCmd()
        {
            return infoAdapter.getInfoBytes();
        }
    }

    public boolean decode(byte[] data)
    {
        if (offset >= data.length)
        {
            return false;
        }
        while (true)
        {
            if (cmdAdapter != null)
            {
                //尝试解析命令
                int i = cmdAdapter.tryDecode(offset, data);
                offset = cmdAdapter.getExpectedOffset();
                if (i == 1)
                {
                    cmd = cmdAdapter.getCmd();
                    cmdAdapter = null;
                    if (dataNumber == 1)
                    {
                        return true;
                    }
                    argLenAdapter = new RESP2NumberAdapter();
                } else
                {
                    //解析失败，看是否是想要继续读数据
                    if (offset < data.length)
                    {
                        continue;
                    }
                    return false;
                }
            } else if (argLenAdapter != null)
            {
                int i = argLenAdapter.tryDecode(offset, data);
                offset = argLenAdapter.getExpectedOffset();
                if (i == -1)
                {
                    if (offset < data.length)
                    {
                        continue;
                    }
                    return false;
                } else
                {
                    argInfoAdapter = new RESP2InfoAdapter(i, offset);
                    argLenAdapter = null;
                }
            } else
            {
                int i = argInfoAdapter.tryDecode(offset, data);
                offset = argInfoAdapter.getExpectedOffset();
                if (i != -1)
                {
                    Args[argCount++] = argInfoAdapter.getInfoBytes();
                    if (argCount == Args.length)
                    {
                        //解析完毕
                        return true;
                    }
                    //换数字解析
                    argInfoAdapter = null;
                    argLenAdapter = new RESP2NumberAdapter();
                }
                if (offset < data.length)
                {
                    continue;
                }
                return false;
            }

        }
    }

    public byte[] getCmd()
    {
        return cmd;
    }

    public CommandProcessor getExecutor()
    {
        return NetWorkConfig.getCmd(cmd);
    }

    public byte[][] getArgs()
    {
        return Args;
    }
}
