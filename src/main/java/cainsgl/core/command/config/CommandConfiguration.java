package cainsgl.core.command.config;

import cainsgl.core.command.base.manager.*;
import cainsgl.core.command.manager.CommandManager;
import cainsgl.core.command.manager.shunt.CommandShunt;
import cainsgl.core.command.manager.shunt.ShuntCommandManager;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.network.response.impl.BulkStringResponse;
import cainsgl.core.network.response.impl.LazyArrayResponse;

import cainsgl.core.persistence.serializer.MutSerializable;

import cainsgl.core.system.thread.ThreadManager;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CommandConfiguration
{
    public CommandConfiguration()
    {
        new StringManager();
        new SimpleCommandManager();
        new ExecuteManager();
        new HashManager();
        new ListManager();
        new BigMapManager();
        //释放序列化数据
        DESERIALIZER_MAP = null;
    }

    private static final Map<String, MutSerializable> COMMAND_MANAGERS = new HashMap<>();
    private static Map<String, byte[]> DESERIALIZER_MAP = new HashMap<>();
    private static final Map<MutSerializable, EventLoop> SERIALIZER_EVENT_LOOP_MAP = new HashMap<>();
    private static final List<CommandManager> commandManagers = new ArrayList<>();
    //注册所有的commandManger，用于执行scan
    public static void register(CommandManager commandManager)
    {
        commandManagers.add(commandManager);
    }

    public static int dbSize() throws ExecutionException, InterruptedException
    {
        int i=0;

        for (CommandManager commandManager : commandManagers)
        {
            if(commandManager.getClass()==SimpleCommandManager.class)
            {
                continue;
            }
            EventLoop loopByManager = ThreadManager.getLoopByManager(commandManager);
            if (loopByManager != null)
            {
                Promise<List<ElementResponse>> promise = loopByManager.newPromise();
                loopByManager.submit(() -> {
                    List<ElementResponse> resp2Responses = commandManager.scanData();
                    promise.setSuccess(resp2Responses);
                });
                List<ElementResponse> resp2Responses = promise.get();
                if(resp2Responses!=null)
                {
                    i+=resp2Responses.size();
                }

            } else
            {
                List<ElementResponse> resp2Responses = commandManager.scanData();
                if(resp2Responses!=null)
                {
                    i+=resp2Responses.size();
                }
            }
        }

        return i;
    }

    public static List<ElementResponse> scanData() throws  ExecutionException, InterruptedException
    {
        List<ElementResponse> res = new ArrayList<>();
        res.add(new BulkStringResponse("0"));
        LazyArrayResponse arrayResponse = new LazyArrayResponse();
        for (CommandManager commandManager : commandManagers)
        {
            if(commandManager.getClass()==SimpleCommandManager.class)
            {
                continue;
            }
            EventLoop loopByManager = ThreadManager.getLoopByManager(commandManager);
            if (loopByManager != null)
            {
                Promise<List<ElementResponse>> promise = loopByManager.newPromise();
                loopByManager.submit(() -> {
                    List<ElementResponse> resp2Responses = commandManager.scanData();
                    promise.setSuccess(resp2Responses);
                });
                List<ElementResponse> resp2Responses = promise.get();
                if(resp2Responses!=null)
                {
                    for(ElementResponse resp2Response : resp2Responses)
                    {
                        arrayResponse.addElement(resp2Response);
                    }
                }

            } else
            {
                List<ElementResponse> resp2Responses = commandManager.scanData();
                if(resp2Responses!=null)
                {
                    for(ElementResponse resp2Response : resp2Responses)
                    {
                        arrayResponse.addElement(resp2Response);
                    }
                }
            }
        }
        res.add(arrayResponse);
        return res;
    }
    //注册序列化，用于RDB
    public static void register(MutSerializable manager, EventLoop eventLoop)
    {
        String className = manager.getClass().getName();
        COMMAND_MANAGERS.put(className, manager);
        SERIALIZER_EVENT_LOOP_MAP.put(manager, eventLoop);
        byte[] byteValue = DESERIALIZER_MAP.get(className);
        if (byteValue == null)
        {
            MutConfiguration.log.info("{} 该manager无序列化数据跳过", className);
            return;
        }
        eventLoop.submit(() -> {
            manager.deSerializer(byteValue);
        });
    }
    //注册分流器，用于RDB
    public static void register(CommandShunt shunt, ShuntCommandManager<?> manager)
    {
        String className = manager.getClass().getName();
        COMMAND_MANAGERS.put(className, shunt);
        byte[] byteValue = DESERIALIZER_MAP.get(className);
        if (byteValue == null)
        {
            MutConfiguration.log.info("{} ，该shunt无序列化数据，直接跳过", className);
            return;
        }
        shunt.deSerializer(byteValue);
    }

    //RDB
    public static Map<String, byte[]> getData()
    {

        Map<String, byte[]> data = new HashMap<>();
        for (Map.Entry<String, MutSerializable> entry : COMMAND_MANAGERS.entrySet())
        {
            MutSerializable value = entry.getValue();
            EventLoop eventExecutors = SERIALIZER_EVENT_LOOP_MAP.get(value);
            Promise<byte[]> promise = eventExecutors.newPromise();
            eventExecutors.submit(() -> {
                byte[] serialization = value.serialization();
                promise.setSuccess(serialization);
            });
            try
            {
                data.put(entry.getKey(), promise.get());
            } catch (Exception e)
            {
                MutConfiguration.log.error("序列化过程出现异常", e);
            }
        }
        return data;
    }

}
