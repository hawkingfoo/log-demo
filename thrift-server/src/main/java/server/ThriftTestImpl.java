package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thrift.LogServer;

/**
 * Created by hawkingfoo on 2017/6/27 0027.
 */
public class ThriftTestImpl implements LogServer.Iface {
    private static final Logger logger = LogManager.getLogger(ThriftTestImpl.class);

    public String getLogRes(String request) {
        logger.info("get request:", request);
        System.out.println(request);
        return "success.";
    }
}
