package gr.aueb.cf.springapp.service.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        SLF4JBridgeHandler.install(); // Installs the bridge (JUL-to_SLF) Handler
        Handler fileHandler;
        try {
            fileHandler = new FileHandler("logfile.log", true);  // true for update the file
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.setLevel(Level.ALL);
        logger.addHandler(fileHandler);
    }

    private LoggerUtil() {}

    public static Logger getCurrentLogger() {
        return logger;
    }
}
