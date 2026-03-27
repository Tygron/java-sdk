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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.tytech.core.net.Network;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.CurrencyOrder;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * String manipulation utilities not found elsewhere or placed here to avoid linking to jars unnecessarily
 *
 * @author Alexander Hofstede, Frank Baars, Maxim Knepfle
 */
public abstract class StringUtils {

    public static class AlphanumericalStringComparator<T> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return ObjectUtils.ALPHANUMERICAL_ORDER.compare(o1.toString(), o2.toString());
        }
    }

    private static class Holder { // random number generator

        private static final SecureRandom secureRandom = new SecureRandom();
    }

    /**
     * Search match
     *
     */
    public enum Match {
        EXACT, START, CLOSE, CONTAINS
    }

    /**
     * Separator is a pattern used to separate the values in for example arrays. These are separated by one or more Whitespaces.
     */
    private static final Pattern SEPARATOR = Pattern.compile("\\s+");

    /**
     * Empty String.
     */
    public static final String EMPTY = "";

    public static final String LANG_SPLIT = ":;:";

    /**
     * Whitespace is a String with one whitespace.
     */
    public static final String WHITESPACE = " ";

    /**
     * New line characters: "\n";
     */
    public static final String NEW_LINE = "\n";

    /**
     * Under score "_"
     */
    public static final String UNDER_SCORE = "_";

    /**
     * Colon ":"
     */
    public static final String COLON = ":";

    public static final String ATTRIBUTE_SPLIT = COLON + WHITESPACE;

    public static final String GRAPH = "GRAPH";

    public static final String XGRAPH = "XGRAPH";

    public static final String YGRAPH = "YGRAPH";

    public static final String AND = "and";

    /**
     * Default World Wide Web Consortium Recommendation is UTF-8 encoding
     */
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * Amount of Hexadecimal chars needed for 32-bit Integer value
     */
    public static final int INTEGER_HEX_CHARS_LENGTH = 8;

    /**
     * An array with chars.
     */
    private static final char[] CHARACTERS = new char[62];

    static {
        for (int idx = 0; idx < 10; ++idx) {
            CHARACTERS[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            CHARACTERS[idx] = (char) ('a' + idx - 10);
        }
        for (int idx = 36; idx < 62; ++idx) {
            CHARACTERS[idx] = (char) ('A' + idx - 36);
        }
    }

    /**
     * An array with chars easily readable by humans
     */
    private static final char[] HUMAN_CHARACTERS = new char[CHARACTERS.length - 8];

    static {
        List<String> invalid = Arrays.asList("i", "I", "l", "L", "o", "O", "0", "1");
        int i = 0;
        for (char c : CHARACTERS) {
            if (!invalid.contains(String.valueOf(c))) {
                HUMAN_CHARACTERS[i] = c;
                i++;
            }
        }
    }

    public static final String HUMAN_STRING_SEPERATOR = ", ";

    public static final String PHONE_DONT_CALL = "PLEASE DONT CALL ME";

    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * From xml spec valid chars: #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF] any Unicode character, excluding
     * the surrogate blocks, FFFE, and FFFF.
     */
    private static final String XML_REGEX = "[^\\x09\\x0A\\x0D\\x20-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]";

    /**
     * The replacement character is used to indicate problems when a system is unable to render a stream of data to a correct symbol. It is
     * usually seen when the data is invalid and does not match any character.
     */
    public static final String REPLACEMENT_CHAR = "�";

    public static final String DATE_FORMAT = "dd MMM yyyy";

    public static final String TIME_FORMAT = "HH:mm";

    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    private static final Pattern REGEX_CONTAINS_NUMBERS = Pattern.compile(".*[0-9].*");

    private static final Pattern REGEX_CAPS = Pattern.compile("([^A-Z]*)([A-Z])");

    private static final Pattern REGEX_DIGITS = Pattern.compile("([^0-9]*)([0-9])");

    private static final String _toHumanPositiveTime(long timeMS, boolean firstOnly) {

        List<String> list = firstOnly ? null : new ArrayList<>();

        // YEARS
        long years = timeMS / Moment.YEAR_AVG;
        if (years >= 1) {
            list.add(years + (years > 1 ? " years" : " year"));
            timeMS -= years * Moment.YEAR_AVG;
        }

        long weeks = timeMS / Moment.WEEK;
        if (weeks >= 1) {
            list.add(weeks + (weeks > 1 ? " weeks" : " week"));
            timeMS -= weeks * Moment.WEEK;
        }

        long days = timeMS / Moment.DAY;
        if (days >= 1) {
            list.add(days + (days > 1 ? " days" : " day"));
            timeMS -= days * Moment.DAY;
        }

        long hours = timeMS / Moment.HOUR;
        if (hours >= 1) {
            list.add(hours + (hours > 1 ? " hours" : " hour"));
            timeMS -= hours * Moment.HOUR;
        }

        long minutes = timeMS / Moment.MINUTE;
        if (minutes >= 1) {
            list.add(minutes + (minutes > 1 ? " minutes" : " minute"));
            timeMS -= minutes * Moment.MINUTE;
        }

        long seconds = timeMS / Moment.SECOND;
        if (seconds >= 1) {
            list.add(seconds + (seconds > 1 ? " seconds" : " second"));
            timeMS -= seconds * Moment.SECOND;
        }

        long ms = timeMS / Moment.DAY;
        if (ms >= 1) {
            list.add(ms + " ms");
        }

        return list.isEmpty() ? "0 seconds" : arrayToHumanString(list);
    }

    private static final String _toSimplePositiveTime(long timeMS) {

        // YEARS
        long years = Math.round(timeMS / (double) Moment.YEAR_AVG);
        if (years > 1) {
            return years + " years";
        }
        years = timeMS / Moment.YEAR_AVG;
        if (years == 1) {
            return "1 year";
        }

        // MONTHS
        long months = Math.round(timeMS / (double) Moment.MONTH_AVG);
        if (months > 1) {
            return months + " months";
        }
        months = timeMS / Moment.MONTH_AVG;
        if (months == 1) {
            return "1 month";
        }

        // WEEKS
        long weeks = Math.round(timeMS / (double) Moment.WEEK);
        if (weeks > 1) {
            return weeks + " weeks";
        }
        weeks = timeMS / Moment.WEEK;
        if (weeks == 1) {
            return "1 week";
        }

        // DAYS
        long days = Math.round(timeMS / (double) Moment.DAY);
        if (days > 1) {
            return days + " days";
        }
        days = timeMS / Moment.DAY;
        if (days == 1) {
            return "1 day";
        }

        // HOURS
        long hours = Math.round(timeMS / (double) Moment.HOUR);
        if (hours > 1) {
            return hours + " hours";
        }
        hours = timeMS / Moment.HOUR;
        if (hours == 1) {
            return "1 hour";
        }

        // MINUTES
        long minutes = Math.round(timeMS / (double) Moment.MINUTE);
        if (minutes > 1) {
            return minutes + " minutes";
        }
        minutes = timeMS / Moment.MINUTE;
        if (minutes == 1) {
            return "1 minute";
        }

        // SECONDS
        long seconds = Math.round(timeMS / (double) Moment.SECOND);
        if (seconds > 1) {
            return seconds + " seconds";
        }
        seconds = timeMS / Moment.SECOND;
        if (seconds == 1) {
            return "1 second";
        }

        // MILLIS
        return timeMS + " ms";
    }

    public static final String arrayToHumanString(Object array) {
        return arrayToHumanString(array, AND);
    }

    public static final String arrayToHumanString(Object array, String andWord) {

        // convert collections to array format
        if (array instanceof Collection<?> collection) {
            array = collection.toArray(new Object[collection.size()]);
        }

        // validate as valid array or fallback
        if (array == null) {
            return StringUtils.EMPTY;
        } else if (!array.getClass().isArray()) {
            return array.toString();
        }

        // ok, array get length
        int length = Array.getLength(array);
        if (length == 0) {
            return StringUtils.EMPTY;
        } else if (length == 1) {
            return String.valueOf(Array.get(array, 0));
        }

        // walk by values
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            result.append(Array.get(array, i));
            if (i < length - 2) {
                result.append(HUMAN_STRING_SEPERATOR);
            } else if (i < length - 1) {
                if (StringUtils.containsData(andWord)) {
                    result.append(WHITESPACE).append(andWord).append(WHITESPACE);
                } else {
                    result.append(HUMAN_STRING_SEPERATOR);
                }
            }
        }
        return result.toString();
    }

    public static final String arrayToString(String[] array) {

        StringBuilder result = new StringBuilder();
        result.append("{ ");
        for (String data : array) {
            result.append(data);
            result.append(", ");
        }
        result.append(" }");
        return result.toString();
    }

    /**
     * Make a String's first letter upper case and the rest lower case.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static final String capitalize(String s) {

        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Make a String?s first letter upper case.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static final String capitalizeFirstLetter(String s) {

        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Make a String?s first and all letters after the underscore(_) upper case and the rest lower case.
     *
     * @param text The String to manipulate
     * @return The modified String
     */
    public static final String capitalizeUnderScores(String text) {

        String[] data = text.split(UNDER_SCORE);
        StringBuilder builder = new StringBuilder(text.length());
        for (String part : data) {
            builder.append(StringUtils.capitalize(part));
        }
        return builder.toString();
    }

    public static final String capitalizeWithSpacedUnderScores(Enum<?> enhum) {
        return enhum == null ? null : capitalizeWithSpacedUnderScores(enhum.name());
    }

    public static final String capitalizeWithSpacedUnderScores(String text) {

        if (text == null) {
            return null;
        }
        String[] data = text.split(UNDER_SCORE);
        StringBuilder result = new StringBuilder(text.length());
        for (String part : data) {
            result.append(StringUtils.capitalize(part));
            result.append(StringUtils.WHITESPACE);
        }
        return result.toString().trim();
    }

    public static final String clamp(String text, int maxChars) {

        if (containsData(text) && text.length() > maxChars) {
            text = text.substring(0, maxChars) + "...";
        }
        return text;
    }

    /**
     * Remove all fonts and span from HTML text
     * @param inputHTML
     * @return
     */
    public static final String cleanHTML(String inputHTML) {

        if (!containsData(inputHTML)) {
            return EMPTY;
        }

        inputHTML = inputHTML.replaceAll("style=\"[^\"]*\"", EMPTY);
        inputHTML = inputHTML.replaceAll("<font[^>]*>", EMPTY);
        inputHTML = inputHTML.replaceAll("</font>", EMPTY);
        inputHTML = inputHTML.replaceAll("<span[^>]*>", EMPTY);
        inputHTML = inputHTML.replaceAll("</span>", EMPTY);

        return inputHTML;
    }

    /**
     * Compact e.g. js code
     */
    public static final String compactCode(String code) {

        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        int lineLenght = 0;

        for (String line : code.split(lineSeparator)) {

            // remove comments
            int index = line.indexOf("//");
            if (index >= 0) {
                line = line.substring(0, index);
            }

            // trim whitespace
            line = StringUtils.internalTrim(line);
            builder.append(line);

            // keep line length below 150
            lineLenght += line.length();
            if (lineLenght > 150) {
                builder.append(lineSeparator);
                lineLenght = 0;
            }
        }
        return builder.toString();
    }

    /**
     * Test is the given String is not NULL and if it contains more then one character.
     *
     * @param string
     * @return
     */
    public static final boolean containsData(String string) {
        return string != null && string.length() > 0;
    }

    public static final boolean containsNumbers(String string) {
        return containsData(string) && REGEX_CONTAINS_NUMBERS.matcher(string.trim()).matches();
    }

    public static final int count(String text, String needle) {
        return containsData(needle) ? (text.length() - text.replace(needle, EMPTY).length()) / needle.length() : 0;
    }

    public static final String dateToHumanString() {
        return dateToHumanString(System.currentTimeMillis());
    }

    public static final String dateToHumanString(Calendar cal, Boolean date) {
        return new SimpleDateFormat(date == null ? DATE_TIME_FORMAT : date ? DATE_FORMAT : TIME_FORMAT).format(cal.getTime());
    }

    public static final String dateToHumanString(long millis) {
        return dateToHumanString(millis, true);
    }

    public static final String dateToHumanString(long timeMillis, Boolean date) {
        return dateToHumanString(TimeZone.getDefault(), timeMillis, date);
    }

    public static final String dateToHumanString(TimeZone zone, long timeMillis, Boolean date) {

        Calendar cal = Calendar.getInstance(zone);
        cal.setTimeInMillis(timeMillis);
        return dateToHumanString(cal, date);
    }

    public static final String formatEnumString(String data, Enum<?> term, Object... args) {

        if (term == null) {
            TLogger.severe("Cannot get empty term.");
            return null;
        }

        String result;
        if (StringUtils.EMPTY.equals(data)) {
            result = "{" + term + "}";
            TLogger.warning(term + " is not in dictionary. Replaced by " + result + ".");
        } else {
            result = data;
        }

        // replace with args if available
        try {
            if (args != null && args.length > 0) {
                result = result.formatted(args);
            }
        } catch (Exception e) {
            TLogger.exception(e, "Bad formatting for: " + term);
        }
        return result;
    }

    private static final String formatNumberToLocalCurrency(double amount, UnitSystem unitSystem, String currencySign,
            CurrencyOrder order) {

        StringBuilder builder = new StringBuilder();
        builder.append(currencySign);
        builder.append(StringUtils.WHITESPACE);
        builder.append(order.getNumberWithPostFix(amount, unitSystem));
        return builder.toString();
    }

    public static final byte[] getBytes(String text) {

        try {
            return text == null ? null : text.getBytes(DEFAULT_ENCODING);
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    public static final String getFirstLine(String txt) {

        if (txt != null) {
            txt = txt.split(NEW_LINE)[0];
            txt = txt.contains(".") ? txt.substring(0, txt.indexOf(".") + 1) : txt;
        }
        return txt;
    }

    public static final String getHTMLColorFontOpeningTag(TColor color) {
        return "<font color=\"" + color.toHTML() + "\">";
    }

    public static final String implode(Object[] data) {
        return implode(data, ", ");
    }

    public static final String implode(Object[] data, String delimiter) {

        if (data.length == 0) {
            return EMPTY;
        }

        StringBuilder builder = new StringBuilder(data[0].toString());
        for (int i = 1; i < data.length; i++) {
            builder.append(delimiter);
            builder.append(data[i]);
        }
        return builder.toString();
    }

    public static final String increaseLenghtWithSpaces(String value, int lenght) {

        StringBuilder builder = new StringBuilder(value);
        while (builder.length() < lenght) {
            builder.append(WHITESPACE);
        }
        return builder.toString();
    }

    /**
     * Trims all spaces in a given string.
     *
     * @param text
     * @return trimmed version
     */
    public static final String internalTrim(String text) {
        return internalTrim(text, false);
    }

    public static final String internalTrim(String text, boolean fullTrim) {

        // null check
        if (text == null) {
            return null;
        }
        // regex that splits on 1 or more spaces
        String[] splits = text.split("\\s+");
        StringBuilder builder = new StringBuilder(text.length());

        for (String split : splits) {
            builder.append(split);
            if (!fullTrim) {
                builder.append(WHITESPACE);
            }
        }
        return builder.toString().trim();
    }

    public static final boolean isInteger(String text) {

        // empty never has a number
        if (!containsData(text)) {
            return false;
        }
        // parse text for valid number
        // note: must also check for MAX value, thus simple regular expression is not enough
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Make a lowercase String with underscores ast spaces.
     *
     * @param text The String to manipulate
     * @return The modified String
     */
    public static final String lowerCaseWithUnderScores(String text) {

        // null check
        if (text == null) {
            return null;
        }

        String[] data = text.toLowerCase().split(WHITESPACE);
        StringBuilder builder = new StringBuilder(text.length());

        for (int i = 0; i < data.length; i++) {
            builder.append(data[i].trim());
            // add under scorres, except the last one.
            if (i + 1 != data.length) {
                builder.append(UNDER_SCORE);
            }
        }
        return builder.toString();
    }

    /**
     * Close matching string, allow some typos, always low caps
     */
    public static final boolean matches(String name, String search, Match match) {

        if (name == null || search == null) {
            return false;
        }
        name = name.toLowerCase();
        search = search.toLowerCase();

        switch (match) {
            case EXACT:
                return name.equals(search);
            case START:
                return name.startsWith(search);
            case CONTAINS:
                return name.contains(search);
            case CLOSE: // (start close)
            default:
                int i = 0;
                for (; i < search.length() && i < name.length(); i++) {
                    char c = search.charAt(i);
                    if (name.charAt(i) == c) {
                        continue;
                    }
                    if (i > 0 && name.charAt(i - 1) == c) {
                        continue;
                    }
                    if (i < name.length() - 1 && name.charAt(i + 1) == c) {
                        continue;
                    }
                    return false;
                }
                return i > 0; // at least one hit
        }
    }

    public static final int parseHex(String value) {
        return (int) Long.parseLong(value, 16);
    }

    /**
     * Removes empty String at the end of the list.
     * @param input Array of Strings
     * @return
     */
    public static String[] pruneTrailing(String[] input) {

        int last = input.length - 1;
        for (; last >= 0 && !StringUtils.containsData(input[last]); last--) {
        }
        if (last == input.length - 1) {
            return input;
        }
        return Arrays.copyOf(input, last + 1);
    }

    /**
     * Generates a secure random string of given length with only human readable characters
     *
     */
    public static final String randomHumanString(int length) {

        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = HUMAN_CHARACTERS[Holder.secureRandom.nextInt(HUMAN_CHARACTERS.length)];
        }
        return new String(chars);
    }

    public static final int randomNumber(int length) {
        int maxValue = (int) Math.pow(10, length);
        return Holder.secureRandom.nextInt(maxValue);
    }

    public static final int randomPositiveInt() {
        return Holder.secureRandom.nextInt(Integer.MAX_VALUE);
    }

    /**
     * Generates a secure random string of given length
     *
     */
    public static final String randomString(int length) {
        return randomString(length, false);
    }

    public static final String randomString(int length, boolean hexadecimal) {

        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = CHARACTERS[Holder.secureRandom.nextInt(hexadecimal ? 16 : CHARACTERS.length)];
        }
        return new String(chars);
    }

    /**
     * Generates a secure random HEX String of length 16 and based on current time
     */
    public static final String randomTimeHex() {

        String time = Long.toHexString(System.currentTimeMillis());
        return time + StringUtils.randomString(16 - time.length(), true);
    }

    /**
     * Generate secure random char Token
     * @return
     */
    public static final String randomToken() {
        return randomString(Network.TOKEN_LENGTH);
    }

    public static final String removeHTMLTags(String html) {
        return removeHTMLTags(html, false);
    }

    public static final String removeHTMLTags(String html, boolean addLineBreaks) {

        if (!containsData(html)) {
            return EMPTY;
        }
        if (addLineBreaks) {
            html = html.replaceAll("<p>", EMPTY);
            html = html.replaceAll("</p>", "\n\n");
            html = html.replaceAll("<\\/br>", "\n");
            html = html.replaceAll("<br>", "\n");
        }
        html = html.replaceAll("\\<[^>]*>", EMPTY).trim();

        /**
         * Replace common HTML entities: https://www.w3schools.com/html/html_entities.asp
         */
        if (html.contains("&#")) {
            html = html.replaceAll("&#160;", WHITESPACE);
            html = html.replaceAll("&#60;", "<");
            html = html.replaceAll("&#62;", ">");
            html = html.replaceAll("&#38;", "&");
            html = html.replaceAll("&#34;", "\"");
            html = html.replaceAll("&#8220;", "\"");
            html = html.replaceAll("&#8221;", "\"");
            html = html.replaceAll("&#39;", "\'");
            html = html.replaceAll("&#8364;", "€");
        }
        if (html.contains("&")) {
            html = html.replaceAll("&nbsp;", WHITESPACE);
            html = html.replaceAll("&lt;", "<");
            html = html.replaceAll("&gt;", ">");
            html = html.replaceAll("&amp;", "&");
            html = html.replaceAll("&quot;", "\"");
            html = html.replaceAll("&apos;", "\'");
            html = html.replaceAll("&euro;", "€");
        }
        return html;
    }

    /**
     * Split String into a String array based on whitespace
     */
    public static final String[] split(String value) {

        // null == null
        if (value == null) {
            return null;
        }
        // remove trailing and starting white
        value = value.trim();

        // empty strings must return length 0
        if (value.isEmpty()) {
            return EMPTY_ARRAY;
        }
        // split with seperator
        return StringUtils.SEPARATOR.split(value);
    }

    /**
     * Split String into a double array based on whitespace
     */
    public static final double[] splitDouble(String value) throws NumberFormatException {

        String[] split = split(value);
        if (split == null) {
            return null;
        }
        double[] array = new double[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Double.parseDouble(split[i]);
        }
        return array;
    }

    /**
     * Split String into a int array based on whitespace
     */
    public static final int[] splitInt(String value) throws NumberFormatException {

        String[] split = split(value);
        if (split == null) {
            return null;
        }
        int[] array = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Integer.parseInt(split[i]);
        }
        return array;
    }

    public static final String toEuroSI(Number value) {
        return toMoney(value != null ? value.doubleValue() : 0, UnitSystemType.SI.getImpl(), TCurrency.EURO.getCurrencyCharacter());
    }

    public static final String toHex(int value) {

        StringBuilder builder = new StringBuilder(Integer.toHexString(value));
        while (builder.length() < INTEGER_HEX_CHARS_LENGTH) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    public static final String toHTMLBody(String text) {

        // Replace and strip everything to be sure we start clean.
        text = text.replaceAll("<p>", EMPTY);
        text = text.replaceAll("</p>", "\n\n");
        text = text.replaceAll("<\\/br>", "\n");
        text = text.replaceAll("<br>", "\n");

        text = text.replaceAll("<(/){0,1}(html|body|p)[^>]*>", WHITESPACE).trim();
        text = text.replace("\\n", "\n");
        text = text.replace("[b]", "<strong>");
        text = text.replace("[/b]", "</strong>");

        String[] split = text.split("\n");
        StringBuilder pBuilder = new StringBuilder();
        for (String nextToken : split) {
            // String nextToken = paragraphSplitter.nextToken();
            if (!StringUtils.containsData(nextToken)) {
                pBuilder.append("</p><p>");
            } else {
                pBuilder.append(nextToken);
                pBuilder.append("<br>");
            }
        }

        return toParagraph(pBuilder.toString());
    }

    public static final String toHumanTime(long timeMS) {
        return (timeMS >= 0 ? "" : "-") + _toHumanPositiveTime(Math.abs(timeMS), false);
    }

    public static final String toLowerCaseUnderscore(String text) {
        return text != null ? text.toLowerCase().trim().replace(WHITESPACE, UNDER_SCORE) : null;
    }

    public static final String toMethodName(String text) {

        if (text == null) {
            return null;
        }
        String[] data = text.split(UNDER_SCORE);
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < data.length; i++) {
            result.append(i == 0 ? data[i].toLowerCase() : StringUtils.capitalize(data[i]));
        }
        return result.toString().trim();
    }

    public static final String toMoney(double currencyValue, UnitSystem unitSystem, String currencySign) {
        return toMoney(currencyValue, unitSystem, currencySign, CurrencyOrder.WHOLE_NUMBERS);
    }

    public static final String toMoney(double currencyValue, UnitSystem unitSystem, String currencySign, CurrencyOrder significantOrder) {
        currencyValue = significantOrder.getNumberWithAdjustedNotation(currencyValue);
        return formatNumberToLocalCurrency(currencyValue, unitSystem, currencySign, significantOrder);
    }

    public static final String toMoney(double currencyValue, UnitSystem unitSystem, TCurrency currency) {
        return toMoney(currencyValue, unitSystem, currency, CurrencyOrder.WHOLE_NUMBERS);
    }

    public static final String toMoney(double currencyValue, UnitSystem unitSystem, TCurrency currency, CurrencyOrder significantOrder) {
        return toMoney(currencyValue, unitSystem, currency.getCurrencyCharacter(), significantOrder);
    }

    public static final String toNumberLength(int number, int length) {

        String text = Integer.toString(number);
        while (text.length() < length) {
            text = "0" + text;
        }
        return text;
    }

    public static final String toParagraph(String word) {
        return "<p>" + word + "</p>";
    }

    public static final String toPercentage(double fraction) {
        return Math.round(100.0 * fraction) + "%";
    }

    /**
     * Format as default SI Value
     */
    public static final String toSI(Number number) {
        return UnitSystemType.SI.getImpl().formatLocalValue(number);
    }

    public static final String toSimpleTime(long timeMS) {
        return (timeMS >= 0 ? "" : "-") + _toSimplePositiveTime(Math.abs(timeMS));
    }

    public static final String toSimpleTimePast(long start) {
        return toSimpleTime(System.currentTimeMillis() - start);
    }

    public static final String toString(double[] array) {
        return toString(array, null);
    }

    public static final String toString(double[] array, Integer decimals) {

        if (array == null) {
            return StringUtils.EMPTY;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (decimals != null) {
                builder.append(MathUtils.round(array[i], decimals));
            } else {
                builder.append(Double.toString(array[i]));
            }
            if (i < array.length - 1) {
                builder.append(WHITESPACE);
            }
        }
        return builder.toString();
    }

    public static final String toUpperCaseUnderscore(String text) {
        return text != null ? text.toUpperCase().trim().replace(WHITESPACE, UNDER_SCORE) : null;
    }

    public static final String toValidFileName(String name) {

        // replace all invalid chars with single under score
        name = name.toLowerCase().trim();
        name = name.replaceAll("[^a-z0-9._-]+", UNDER_SCORE);
        name = name.replaceAll("[_]+", UNDER_SCORE);

        // remove starting, before dot and final under score
        name = name.startsWith(UNDER_SCORE) ? name.substring(1, name.length()) : name;
        name = name.replaceAll("_\\.", ".");
        name = name.endsWith(UNDER_SCORE) ? name.substring(0, name.length() - 1) : name;

        // make sure its not empty
        return name.isEmpty() ? UNDER_SCORE : name;
    }

    public static final String toValidFileName(String name, String extension) {

        name = name.toLowerCase();
        extension = extension.toLowerCase();
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }

        if (!name.endsWith(extension)) {
            name += extension;
        }
        return toValidFileName(name);
    }

    public static final String underScoreBeforeCapitalDigit(String text) {
        return underScoreBeforeRegex(REGEX_DIGITS, underScoreBeforeRegex(REGEX_CAPS, text));
    }

    public static final String underScoreBeforeRegex(Pattern pattern, String text) {

        if (text == null) {
            return null;
        }

        int last = 0;
        StringBuilder result = new StringBuilder(text.length());
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            result.append(m.group(1));
            if (!m.group(1).isBlank()) {
                result.append(StringUtils.UNDER_SCORE);
            }
            result.append(m.group(2));
            last = m.end();
        }
        result.append(text.substring(last));
        return result.toString().trim();
    }

    public static final boolean validEmail(String name) {

        if (name == null) {
            return false;
        }
        /**
         * Valid chars and lenght
         */
        if (!name.matches("[a-zA-Z0-9@._-]{6,50}")) {
            return false;
        }
        /**
         * only one @
         */
        if (name.split("\\@").length != 2) {
            return false;
        }
        /**
         * At least one dot
         */
        return name.split("\\.").length > 1;
    }

    public static final boolean validFilename(String fileName, String extension) {

        if (fileName == null) {
            return false;
        }
        /**
         * Trim
         */
        fileName = fileName.trim();

        /**
         * check chars
         */
        if (!fileName.matches("[a-z0-9._-]{5,100}")) {
            return false;
        }

        /**
         * Check extension
         */
        String[] parts = fileName.split("\\.");
        if (parts.length <= 1) {
            return false;
        }

        /**
         * Finally check extension
         */
        if (extension != null) {
            return parts[parts.length - 1].equalsIgnoreCase(extension);
        }

        /**
         * Extension should be at least 3 char
         */
        return parts[parts.length - 1].length() >= 3;
    }

    public static final boolean validPhone(String number) {
        return validPhone(number, true);
    }

    public static final boolean validPhone(String number, boolean allowDontCall) {

        if (number == null) {
            return false;
        }
        if (allowDontCall && number.trim().equals(PHONE_DONT_CALL)) {
            return true;
        }

        number = internalTrim(number, true);
        if (number.startsWith("+")) {
            return number.matches("[0-9+()]{11,50}");
        } else {
            return number.matches("[0-9]{10,10}");
        }
    }

    /**
     * Filter out invalid chars that are not XML supported.
     */
    public static final String validXML(String value) {
        return value != null ? value.replaceAll(XML_REGEX, REPLACEMENT_CHAR) : null;
    }

    public static final File writeToFile(long value, String filePathName) {
        return writeToFile(Long.toString(value), filePathName);
    }

    public static final File writeToFile(String text, String filePathName) {

        File file = new File(filePathName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(text);
            return file;

        } catch (IOException e) {
            TLogger.exception(e, "Error writing to file: " + filePathName);
            return null;
        }
    }
}
