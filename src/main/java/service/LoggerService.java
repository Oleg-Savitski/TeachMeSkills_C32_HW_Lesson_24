package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerService {
    private static final String LOG_DIR = "C:\\Java-job\\TeachMeSkills_C32_HW_Lesson_24\\source";
    private static final String LOG_FILE = LOG_DIR + "\\logs.txt";
    private static final int MAX_LOG_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    public enum LogLevel {
        SERVLET_INIT(0),
        SERVLET_REQUEST(1),
        SERVLET_DESTROY(2),
        INFO(3),
        WARNING(4),
        ERROR(5),
        DEBUG(6);

        private final int priority;

        LogLevel(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    private static LogLevel CURRENT_LEVEL = LogLevel.DEBUG;

    private static final Object LOCK = new Object();

    public static void servletInit(String servletName) {
        log(String.format("Initializing servlet: %s", servletName), LogLevel.SERVLET_INIT);
    }

    public static void servletRequest(String servletName, String requestDetails) {
        log(String.format("Processing request in servlet %s: %s", servletName, requestDetails),
                LogLevel.SERVLET_REQUEST);
    }

    public static void servletDestroy(String servletName) {
        log(String.format("Destroying servlet: %s", servletName), LogLevel.SERVLET_DESTROY);
    }

    public static void log(String message, LogLevel level) {
        if (level.getPriority() <= CURRENT_LEVEL.getPriority()) {
            synchronized (LOCK) {
                try {
                    checkAndRotateLogFile();

                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                    String threadName = Thread.currentThread().getName();
                    String logEntry = String.format("%s | %s | %s | %s",
                            timestamp, level, threadName, message);

                    try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                        writer.println(logEntry);
                    }

                } catch (IOException e) {

                }
            }
        }
    }

    private static void checkAndRotateLogFile() throws IOException {
        File logFile = new File(LOG_FILE);
        logFile.getParentFile().mkdirs();

        if (logFile.exists() && logFile.length() > MAX_LOG_FILE_SIZE) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = LOG_FILE + "." + timestamp + ".bak";

            Files.move(
                    logFile.toPath(),
                    new File(backupFileName).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        if (!logFile.exists()) {
            logFile.createNewFile();
        }
    }

    public static void info(String message) {
        log(message, LogLevel.INFO);
    }

    public static void warning(String message) {
        log(message, LogLevel.WARNING);
    }

    public static void error(String message) {
        log(message, LogLevel.ERROR);
    }

    public static void error(String message, Throwable throwable) {
        log(message + " | Exception: " + throwable.getMessage(), LogLevel.ERROR);
    }

    public static void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    public static void setLogLevel(LogLevel level) {
        CURRENT_LEVEL = level;
    }

    public static LogLevel getLogLevel() {
        return CURRENT_LEVEL;
    }
}