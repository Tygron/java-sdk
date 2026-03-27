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
package nl.tytech.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Date conversion Utils
 *
 * @author Maxim Knepfle
 *
 */
public final class DateUtils {

    public static final String UTC_NAME = "Etc/UTC";

    private static final String ZONE_IDENTIFIER = "Z";

    public static final TimeZone UTC = TimeZone.getTimeZone(UTC_NAME);

    public static final String FILE_DATETIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    public static final String DATE_FORMAT = "dd MMM yyyy";

    public static final String TIME_FORMAT = "HH:mm";

    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    public static final String format(TimeZone timeZone, String format, double timeMillis) {
        return format(timeZone, format, toTimeMillis(timeMillis));
    }

    public static final String format(TimeZone timeZone, String format, long timeMillis) {
        if (format.endsWith(ZONE_IDENTIFIER)) {
            return ZonedDateTime.of(toLocalDateTime(timeZone, timeMillis), timeZone.toZoneId()).format(DateTimeFormatter.ofPattern(format));
        }
        return toLocalDateTime(timeZone, timeMillis).format(DateTimeFormatter.ofPattern(format));
    }

    public static final String formatLocal(long timeMillis) {
        return formatLocal(DATE_TIME_FORMAT, timeMillis);
    }

    public static final String formatLocal(String format, long timeMillis) {
        return format(TimeZone.getDefault(), format, timeMillis);
    }

    public static final String formatNow() {
        return formatNow(DATE_TIME_FORMAT);
    }

    public static final String formatNow(String format) {
        return formatLocal(format, Instant.now().toEpochMilli());
    }

    public static final String formatNow(TimeZone timeZone, String format) {
        return format(timeZone, format, Instant.now().toEpochMilli());
    }

    public static final <T> T parse(CharSequence format, DateTimeFormatter formatter, TemporalQuery<T> query) {
        return formatter.parse(format, query);
    }

    public static final LocalDateTime parseLocal(CharSequence format, DateTimeFormatter formatter) {
        return parse(format, formatter, LocalDateTime::from);
    }

    public static final LocalDate parseLocalDate(CharSequence format, DateTimeFormatter formatter) {
        return parse(format, formatter, LocalDate::from);
    }

    public static final LocalTime parseLocalTime(CharSequence format, DateTimeFormatter formatter) {
        return parse(format, formatter, LocalTime::from);
    }

    public static final Calendar toCalendar(long timeMillis) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        return cal;
    }

    public static final LocalDate toLocalDate(long timeMillis) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        return LocalDate.ofYearDay(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_YEAR));
    }

    public static final LocalDateTime toLocalDate(TimeZone timeZone, int year, int month, int day) {

        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(year, month, day, 0, 0, 0);
        return LocalDateTime.ofInstant(cal.toInstant(), timeZone.toZoneId());
    }

    public static final LocalDateTime toLocalDateTime(TimeZone timeZone, double timeMillis) {
        return toLocalDateTime(timeZone, toTimeMillis(timeMillis));
    }

    public static final LocalDateTime toLocalDateTime(TimeZone timeZone, long timeMillis) {

        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(timeMillis);
        return LocalDateTime.ofInstant(cal.toInstant(), timeZone.toZoneId());
    }

    public static LocalDateTime[] toLocalDateTimes(TimeZone timeZone, double[] dates) {
        return Arrays.stream(dates).mapToObj(date -> toLocalDateTime(timeZone, date)).toArray(LocalDateTime[]::new);
    }

    public static final long toLong(LocalDate localDate) {
        return toLong(UTC, localDate.atStartOfDay());
    }

    public static final long toLong(TimeZone timeZone, LocalDateTime ldt) {
        return toLong(ldt.atZone(timeZone.toZoneId()));
    }

    public static final long toLong(ZonedDateTime zdt) {
        return GregorianCalendar.from(zdt).getTimeInMillis();
    }

    public static final long toTimeMillis(double timeMillis) {
        return (long) timeMillis;
    }
}
