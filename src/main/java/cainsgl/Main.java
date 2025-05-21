package cainsgl;

import cainsgl.core.command.config.CommandConfiguration;
import cainsgl.core.config.ConfigLoader;
import cainsgl.core.config.MutConfiguration;
import cainsgl.core.network.server.MutServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        // 加载配置文件
        try {
            ConfigLoader.loadConfig("D:\\Code\\mut-db\\src\\main\\java\\cainsgl\\core\\config\\mut-config.xml");
        } catch (Exception e) {
            log.error("Error loading config", e);
        }

        // 打印配置验证信息
        log.info("port: {}", MutConfiguration.PORT);
        log.info("MutConfig类加载器: {}", MutConfiguration.class.getClassLoader());

        MutServerBuilder.build().start();
    }

}
