package logger;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by hawkingfoo on 2017/6/29 0029.
 */
@Plugin(name = "FileAppender", category = "Core", elementType = "appender", printObject = true)
public class FileAppender extends AbstractAppender {
    private String fileName;

    /* 构造函数 */
    public FileAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, String fileName) {
        super(name, filter, layout, ignoreExceptions);
        this.fileName = fileName;
    }

    @Override
    public void append(LogEvent event) {
        final byte[] bytes = getLayout().toByteArray(event);
        writerFile(bytes);

    }

    /*  接收配置文件中的参数 */
    @PluginFactory
    public static FileAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("fileName") String fileName,
                                              @PluginElement("Filter") final Filter filter,
                                              @PluginElement("Layout") Layout<? extends Serializable> layout,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {
        if (name == null) {
            LOGGER.error("no name defined in conf.");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        // 创建文件
        if (!createFile(fileName)) {
            return null;
        }
        return new FileAppender(name, filter, layout, ignoreExceptions, fileName);
    }

    private static boolean createFile(String fileName) {
        Path filePath = Paths.get(fileName);
        try {
            // 每次都重新写文件，不追加
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Files.createFile(filePath);
        } catch (IOException e) {
            LOGGER.error("create file exception", e);
            return false;
        }
        return true;
    }

    private void writerFile(byte[] log) {
        try {
            Files.write(Paths.get(fileName), log, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("write file exception", e);
        }
    }
}

