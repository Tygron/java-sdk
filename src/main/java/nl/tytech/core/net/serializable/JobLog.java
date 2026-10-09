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
package nl.tytech.core.net.serializable;

import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.util.DateUtils;
import nl.tytech.util.StringUtils;

/**
 * Logging of daily simulation jobs
 *
 * @author Maxim Knepfle
 *
 */
public class JobLog extends Log {

    private static final long serialVersionUID = 8505302259584226566L;

    public static final String getLogDescription(int count, long simTime, double kwh) {
        return "Count: " + count + " (total sim time: " + StringUtils.toSimpleTime(simTime)
                + (kwh > 0 ? " ~" + (kwh < 0.001 ? UnitSystemType.SI.getImpl().formatLocalValue(kwh, 0, 6) : StringUtils.toSI(kwh)) + " KwH"
                        : "")
                + ")";
    }

    private String name = StringUtils.EMPTY;

    private String type = StringUtils.EMPTY;

    private long time = 0;

    private int count = 0;

    private double kwh = 0;

    private long simTime = 0;

    public JobLog() {

    }

    public JobLog(Long domainID, String domainName, String jobName, String type, long time, int count, long totalTime, double kwh) {

        super(domainID, domainName, StringUtils.randomTimeHex());
        this.name = jobName;
        this.type = type;
        this.time = time;
        this.count = count;
        this.simTime = totalTime;
        this.kwh = kwh;
    }

    public int getCount() {
        return count;
    }

    public double getKwh() {
        return kwh;
    }

    @Override
    public String getLogDescription() {
        return getLogDescription(count, simTime, kwh);
    }

    @Override
    public long getLogTime() {
        return getTime();
    }

    @Override
    public String getLogTitle() {
        return StringUtils.capitalizeFirstLetter(getName()) + " " + getType();
    }

    @Override
    public Type getLogType() {
        return Type.JOBS;
    }

    public String getName() {
        return name;
    }

    public long getSimTime() {
        return simTime;
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(DateUtils.formatLocal(getTime()));
        builder.append(StringUtils.WHITESPACE);
        builder.append(getDomainName());
        builder.append(StringUtils.WHITESPACE);
        builder.append(getLogDescription());
        return builder.toString();
    }
}
