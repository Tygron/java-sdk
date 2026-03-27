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
package nl.tytech.data.core.item;

import java.util.Calendar;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * Moment: This class keeps track of simulation time moments.
 *
 * @author Maxim Knepfle
 */
public class Moment extends Item {

    /**
     * Second in milliseconds.
     */
    public static final long SECOND = 1000L;

    /**
     * Minute in milliseconds.
     */
    public static final long MINUTE = 60L * Moment.SECOND;

    /**
     * Hour in milliseconds.
     */
    public static final long HOUR = 60L * Moment.MINUTE;

    /**
     * Day in milliseconds.
     */
    public static final long DAY = 24L * Moment.HOUR;

    /**
     * Week in milliseconds.
     */
    public static final long WEEK = 7L * Moment.DAY;

    /**
     * Year in milliseconds. (~365.24219 days around the sun)
     */
    public static final long YEAR_AVG = 365l * Moment.DAY + 5l * Moment.HOUR + 48l * Moment.MINUTE + 45l * Moment.SECOND;

    /**
     * Month in milliseconds (avg approximation)
     */
    public static final long MONTH_AVG = YEAR_AVG / 12L;

    /**
     * Current sim time.
     */
    public static final Integer CURRENT_POSTION = 0;

    /**
     * The session is started with this sim time moment.
     */
    public static final Integer SIMULATION_START_POSTION = 1;

    private static final long serialVersionUID = 5776008764383172107L;

    private static final int EPOCH = 1970;

    public static long getMillis(int year) {
        return (year - EPOCH) * Moment.YEAR_AVG;
    }

    public static long getMillis(int year, int month, int day, int hour) {

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, 0); // note: months start at 0 in Calendar
        return cal.getTimeInMillis();
    }

    public static int getYear() {
        return getYear(System.currentTimeMillis());
    }

    public static int getYear(long now) {
        return EPOCH + (int) (now / Moment.YEAR_AVG);
    }

    @XMLValue
    private long moment = 0;

    public Moment() {
    }

    /**
     * @return the moment time as UTC milliseconds from the epoch.
     */
    public long getMillis() {
        return moment;
    }

    public void setMillis(long timeMillis) {
        this.moment = timeMillis;
    }

    @Override
    public String toString() {
        return StringUtils.dateToHumanString(this.getMillis(), false);
    }
}
