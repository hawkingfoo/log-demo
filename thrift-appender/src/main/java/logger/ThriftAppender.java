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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import thrift.LogServer;

import java.io.Serializable;

/**
 * Created by hawkingfoo on 2017/6/29 0029.
 */
@Plugin(name = "ThriftAppender", category = "Core", elementType = "appender", printObject = true)
public class ThriftAppender extends AbstractAppender {

    private LogServer.Client client;
    private TTransport transport;

    /* 构造函数 */
    public ThriftAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, String host) {
        super(name, filter, layout, ignoreExceptions);
        // 创建客户端
        createThriftClient(host);
    }

    @Override
    public void append(LogEvent event) {
        final byte[] bytes = getLayout().toByteArray(event);
        try {
            String response = client.getLogRes(new String(bytes));
            System.out.println(response);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    /*  接收配置文件中的参数 */
    @PluginFactory
    public static ThriftAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("host") String host,
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
        return new ThriftAppender(name, filter, layout, ignoreExceptions, host);
    }

    @Override
    public void stop() {
        if (transport != null) {
            transport.close();
        }
    }

    private void createThriftClient(String host) {
        try {
            transport = new TFramedTransport(new TSocket(host, 9000));
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new LogServer.Client(protocol);
            LOGGER.info("create client success");
        } catch (Exception e) {
            LOGGER.error("create file exception", e);
        }
    }
}
