package cainsgl.core.command;


import cainsgl.core.config.MutConfiguration;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class JavaScriptExecute
{

    static final Engine engine;

    public static class console
    {
        @HostAccess.Export
        public void log(Object... messages)
        {
            StringBuilder sb = new StringBuilder();
            for (Object msg : messages)
            {
                String str = Value.asValue(msg).toString();
                sb.append(str).append(" ");
            }
            MutConfiguration.log.info("jsInfo: {}", sb);
        }

        @HostAccess.Export
        public void error(Object... messages)
        {
            StringBuilder sb = new StringBuilder();
            for (Object msg : messages)
            {
                String str = Value.asValue(msg).toString();
                sb.append(str).append(" ");
            }
            MutConfiguration.log.error("jsInfo: {}", sb);
        }

        @HostAccess.Export
        public void test()
        {
            MutConfiguration.log.error("jsInfo: ");
        }
    }

    public static class mut
    {
        @HostAccess.Export
        private final BlockingQueue<String> resultQueue = new ArrayBlockingQueue<>(1);

        @HostAccess.Export
        public String cmd(Object cmd) throws InterruptedException
        {
            String string = cmd.toString();
            MUT.execute(string, resp2Response -> {
                byte[] bytes = resp2Response.getBytes();
                try
                {
                    resultQueue.put(new String(bytes));
                } catch (InterruptedException e)
                {
                    MutConfiguration.log.error("jsError:", e);
                }
            });
            return resultQueue.take();
        }
        @HostAccess.Export
        public boolean eq(Object type,Object value,Object target) throws InterruptedException
        {
            String s1 = type.toString();
            if(s1.equals("string"))
           {
               //和响应结果比较
               String s = target.toString();
               String s2 = "$" + s.length() + "\r\n" + s + "\r\n";
               return value.equals(s2);
           }
           if(type.equals("number"))
           {
               String s = target.toString();
               return value.equals(":"+s+"\r\n");
           }
           return value.equals(target);
        }



    }

    public static Object eval(String script) throws ScriptException
    {
        Context ctx = Context.newBuilder("js").allowHostClassLookup(s -> true).allowHostAccess(HostAccess.ALL).engine(engine).build();
        Value globalBindings = ctx.getBindings("js");
        globalBindings.putMember("console", new console());
        globalBindings.putMember("mut", new mut());
        Value js = ctx.eval("js", script);
        return js;
    }

    static
    {
        engine = Engine.newBuilder()
                       .option("engine.WarnInterpreterOnly", "false")
                       .build();

    }
}
