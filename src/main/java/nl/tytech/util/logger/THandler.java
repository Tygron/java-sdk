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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Basic Tygron Console logger
 *
 * @author Alwin Lemstra & Maxim Knepfle
 *
 */
public final class THandler extends Handler {

    private static final class TFormatter extends Formatter {

        private static final String format = "{0,date} {0,time}";
        private final Date dat = new Date();
        private MessageFormat formatter;
        private final Object args[] = new Object[1];

        @Override
        public synchronized String format(LogRecord record) {

            StringBuilder sb = new StringBuilder();
            dat.setTime(record.getMillis());
            args[0] = dat;
            StringBuffer text = new StringBuffer();
            if (formatter == null) {
                formatter = new MessageFormat(format);
            }
            formatter.format(args, text, null);

            if (record.getSourceClassName() != null) {
                sb.append(record.getSourceClassName());
            } else {
                sb.append(record.getLoggerName());
            }
            if (record.getSourceMethodName() != null) {
                sb.append(" ");
                sb.append(record.getSourceMethodName());
            }

            String message = formatMessage(record);
            sb.append("\t" + message + "\n");
            if (record.getThrown() != null) {
                sb.append("Thread: " + Thread.currentThread().getName() + "\n");
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                    TLogger.exception(ex);
                }
            }
            return sb.toString();
        }
    }

    public THandler() {
        this.setFormatter(new TFormatter());
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 0;
        for (StackTraceElement ste : stackTrace) {
            if (Thread.class.getName().equals(ste.getClassName())//
                    || TLogger.class.getName().equals(ste.getClassName()) //
                    || THandler.class.getName().equals(ste.getClassName()) //
                    || Logger.class.getName().equals(ste.getClassName())) {
                index++;
            } else {
                break;
            }
        }

        record.setSourceClassName("(" + stackTrace[index].getFileName() + ":" + stackTrace[index].getLineNumber() + ")");
        record.setSourceMethodName(stackTrace[index].getMethodName());
        if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            System.err.print(getFormatter().format(record));
        } else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            System.err.print(getFormatter().format(record));
        } else {
            System.out.print(getFormatter().format(record));
        }
    }
}
