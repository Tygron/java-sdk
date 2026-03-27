/*******************************************************************************************************************************************
 * Copyright 2006-2026 TyTech B.V., Lange Vijverberg 4, 2513 AC, The Hague, The Netherlands. All rights reserved under the copyright laws of
 * The Netherlands and applicable international laws, treaties, and conventions. TyTech B.V. is a subsidiary company of Tygron Group B.V..
 *
 * This software is proprietary information of TyTech B.V.. You may freely redistribute and use this SDK code, with or without modification,
 * provided you include the original copyright notice and use it in compliance with your Tygron Platform License Agreement.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************************************************************************/
package nl.tytech.util.logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.tytech.util.OSUtils;
import nl.tytech.util.RestManager.TWebApplicationException;
import nl.tytech.util.StringUtils;
import nl.tytech.util.TStatus;
import nl.tytech.util.ThreadUtils;

/**
 * TLogger handles log and exception messages.
 *
 * @author Jeroen Warmerdam, Alwin Lemstra, Maxim Knepfle
 */
public final class TLogger {

    private static final class SingletonHolder {

        private static final TLogger INSTANCE = new TLogger();
    }

    public static enum TRecord {

        SUMMARY("Summary"),

        DATE("Date"),

        LEVEL("Log Level"),

        CAUSE("Cause"),

        MESSAGE("Message"),

        ERROR_STACK("Error StackTrace"),

        REPORT_STACK("Report StackTrace"),

        DETAILS("Additional Info"),

        USER("Client User"),

        LOG("Restored Log"),

        CLIENT("Client Details");

        public static final String format(String text) {
            return StringUtils.containsData(text) ? text : "?";
        }

        private final String formattedName;

        private TRecord(String name) {
            this.formattedName = "\n\n- %s:\n".formatted(name);
        }

        public final int length() {
            return this.formattedName.length();
        }

        @Override
        public final String toString() {
            return this.formattedName;
        }

        public final String toString(String text) {
            return toString() + format(text);
        }
    }

    private static final Object LOCK = new Object();

    /**
     * Max amount a identical log maybe repeated
     */
    private static final int MAX_REPEAT = 10;

    /**
     * Log directory
     */
    private static final String WORK_DIRECTORY = OSUtils.STORAGE_DIRECTORY + "Logs" + File.separator;

    private static final Pattern LEVEL_REGEX = Pattern.compile("(" + TRecord.LEVEL.toString().trim() + ")\\s*(\\w+)",
            Pattern.CASE_INSENSITIVE);

    public static final void addHandler(Handler handler) {
        SingletonHolder.INSTANCE._addHandler(handler);
    }

    public static final void debug(final String log) {
        log(TLevel.DEBUG, log);
    }

    public static final void exception(final Level level, final Throwable exp) {
        exception(level, exp, null);
    }

    /**
     * Log an exception with an additional message and level
     */
    public static final void exception(final Level level, final Throwable exp, final String log) {
        SingletonHolder.INSTANCE._log(level, exp, log);
    }

    /**
     * Log an exception
     */
    public static final void exception(final Throwable exp) {
        exception(exp, null);
    }

    /**
     * Log an exception with an additional message
     */
    public static final void exception(final Throwable exp, final String log) {
        exception(Level.SEVERE, exp, log);
    }

    public static final Level getMessageLevel(String message) {

        // find level from message
        if (StringUtils.containsData(message)) {
            Matcher matcher = LEVEL_REGEX.matcher(message);
            while (matcher.find()) {
                try {
                    return TLevel.parse(matcher.group(2).toUpperCase());
                } catch (Exception e) {
                    // ignore, try next
                }
            }
        }

        // default fallback
        return Level.SEVERE;
    }

    /**
     * Log an info message
     */
    public static final void info(final String log) {
        log(Level.INFO, log);
    }

    public static final boolean isAll() {
        return SingletonHolder.INSTANCE.logger.getLevel() == Level.ALL;
    }

    /**
     * Log a message of level TLevel. If the system is running in debug mode, it quits the system on a >= Level.SEVERE log.
     */
    public static final void log(final Level level, final String log) {
        SingletonHolder.INSTANCE._log(level, log);
    }

    public static final void networkNotification(final String message) {
        networkNotification(new TWebApplicationException(TStatus.CONNECTION_FAILED, message));
    }

    public static final void networkNotification(final TWebApplicationException we) {
        networkNotification(we, null);
    }

    public static final void networkNotification(final TWebApplicationException we, String additionalInfo) {
        log(TLevel.NETWORK_NOTIFICATION, "Error connecting to: " + we.getUrlInfo() + " Status Code: " + we.getStatusCode() + " Message: "
                + we.getMessage() + (additionalInfo != null ? " Additional Info: " + additionalInfo : ""));
    }

    public static final void removeHandler(Handler handler) {
        SingletonHolder.INSTANCE._removeHandler(handler);
    }

    public static final void securityNotification(final String log) {
        log(TLevel.SECURITY_NOTIFICATION, log);
    }

    /**
     * Set the Level that this Logger should display information from
     */
    public static final void setLevel(final Level level) {
        synchronized (LOCK) {
            SingletonHolder.INSTANCE.logger.setLevel(level);
        }
    }

    public static final void setLogToFile(boolean logToFile) {
        synchronized (LOCK) {
            SingletonHolder.INSTANCE.setLogToFileInner(logToFile);
        }
    }

    public static final void setSimpleLogger(boolean simpleLogging) {
        synchronized (LOCK) {
            SingletonHolder.INSTANCE._setSimpleLogger(simpleLogging);
        }
    }

    /**
     * Log a severe log
     *
     */
    public static final void severe(final String log) {
        log(Level.SEVERE, log);
    }

    /**
     * Log a showstopper log
     */
    public static final void showstopper(final String log) {
        log(TLevel.SHOWSTOPPER, log);
    }

    /**
     * Log a warning message
     */
    public static final void warning(final String log) {
        log(Level.WARNING, log);
    }

    private final Logger logger = Logger.getLogger("global");

    private FileHandler fileHandler;

    private String lastLog = null;

    private int lastLogCounter = 0;

    /**
     * Simple logging has no handler , just system out, handlers seem to create an issue with JET in combination with Runtime.Exe()
     */
    private boolean simpleLogging = false;

    /**
     * Private constructor for singleton pattern
     */
    private TLogger() {

        // do default logging
        logger.setLevel(Level.ALL);

        try {
            // remove old handlers, we don't want the console logging of java
            Logger root = Logger.getLogger(StringUtils.EMPTY);
            while (root.getHandlers().length != 0) {
                root.removeHandler(root.getHandlers()[0]);
            }

            // set our handler, the tlogger will fallthrough to this logger
            root.addHandler(new THandler());
            logger.setFilter(null);

        } catch (SecurityException e) {
            logger.warning("Can't remove default logger in a secure environment.");
        }
    }

    private final void _addHandler(Handler handler) {
        logger.addHandler(handler);
    }

    private final void _log(final Level level, String log) {

        // already shutdown or non active?
        if (logger.getLevel() == null && !simpleLogging) {
            return;
        }

        // ignore all below my level
        if (logger.getLevel() != null && logger.getLevel().intValue() > Level.WARNING.intValue()) {
            return;
        }

        if (log.equals(lastLog)) {
            lastLog = log;
            lastLogCounter++;
            if (lastLogCounter == MAX_REPEAT) {
                log = ".... truncated after " + MAX_REPEAT + " duplicate logs.";
            }
            if (lastLogCounter > MAX_REPEAT) {
                return;
            }
        } else {
            lastLog = log;
            lastLogCounter = 0;
        }

        // Log the message
        if (simpleLogging) {
            System.out.println("[" + Thread.currentThread().getName() + ": id:" + Thread.currentThread().threadId() + " p:"
                    + Thread.currentThread().getPriority() + "]\t" + log);
        } else {
            logger.log(level, "[" + Thread.currentThread().getName() + ": id:" + Thread.currentThread().threadId() + " p:"
                    + Thread.currentThread().getPriority() + "]\t" + log);
        }

        // Possible stop of JVM
        if (level.equals(TLevel.SHOWSTOPPER)) {
            // show where it went wrong
            Thread.dumpStack();
            // let user know what going on, before directly closing the JVM
            ThreadUtils.sleepInterruptible(5000);
            System.exit(1);
        }
    }

    private final void _log(final Level level, final Throwable exp, final String log) {
        this._log(level, convertThrowableToMessage(exp, log));
    }

    private final void _removeHandler(Handler handler) {
        logger.removeHandler(handler);
    }

    private final void _setSimpleLogger(boolean simpleLogging) {
        this.simpleLogging = simpleLogging;
    }

    /**
     * Convert the Throwable to a human readable string
     */
    private final String convertThrowableToMessage(final Throwable exp, String details) {

        StringBuilder builder = new StringBuilder();

        // Read the cause of the exception
        String cause = null;
        try {
            cause = exp.getCause().toString();
        } catch (Exception e) {
        }

        // Read the message of the exception
        String message = null;
        try {
            message = exp.getMessage();
        } catch (Exception e) {
        }

        // Read the complete text of the exception
        String text = null;
        try {
            text = exp.toString();
        } catch (Exception e) {
        }

        // add URL info?
        try {
            if (exp instanceof TWebApplicationException wexp) {
                details = details + " URL: " + wexp.getUrlInfo();
            }
        } catch (Exception e) {
        }

        // Read the file line
        String location = "";

        if (exp.getStackTrace().length > 0) {
            StackTraceElement first = exp.getStackTrace()[0];
            if (first.getFileName() != null) {
                location = first.getFileName().split("\\.")[0] + ": ";
            } else if (first.getClassName() != null) {
                String[] splitName = first.getClassName().split("\\.");
                location = splitName[splitName.length - 1] + ": ";
            }
            if (first.getLineNumber() >= 0) {
                location += first.getLineNumber() + ": ";
            }
        }

        System.err.println("\nERROR: " + location + "check Error Report in the log dir.\n");

        builder.append(TRecord.SUMMARY);
        builder.append(location);
        builder.append(exp.getClass().getSimpleName());
        if (StringUtils.containsData(message)) {
            builder.append(": " + message);
        }
        builder.append(TRecord.DATE);
        builder.append(getDate());
        builder.append(TRecord.CAUSE);
        builder.append(TRecord.format(cause));
        builder.append(TRecord.MESSAGE);
        builder.append(TRecord.format(text));
        builder.append(TRecord.ERROR_STACK);
        builder.append(ThreadUtils.getStackTrace(exp));

        if (StringUtils.containsData(details)) {
            builder.append(TRecord.DETAILS);
            builder.append(details);
        }
        return builder.toString();
    }

    /**
     * Get timestamp Y-M-D-H-M-S-L formatted
     */
    private final String getDate() {
        return "%1$tY-%1$tm-%1$te-%1$tH-%1$tM-%1$tS-%1$tL".formatted(Calendar.getInstance());
    }

    /**
     * The non-static inner method for setting whether the handler should write to file
     */
    protected final void setLogToFileInner(boolean logToFile) {

        if (logToFile) {
            // add file handler
            try {
                if (fileHandler == null) {
                    // Check if the correct directory exists
                    File file = new File(WORK_DIRECTORY);
                    if (!file.exists()) {
                        file.mkdirs();
                        file.createNewFile();
                    }
                    fileHandler = new FileHandler(WORK_DIRECTORY + "log-" + getDate() + ".txt", true);
                    fileHandler.setFormatter(new SimpleFormatter());
                    logger.addHandler(fileHandler);
                }
            } catch (IOException | SecurityException e) {
                // halt the system
                System.err.println(e);
                throw new RuntimeException();
            }
        } else if (fileHandler != null) {
            // remove file handler
            logger.removeHandler(fileHandler);
            fileHandler = null;
        }
    }
}
