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

import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.util.DateUtils;
import nl.tytech.util.StringUtils;

/**
 * Logging of security noticeable incidents
 *
 * @author Maxim Knepfle
 *
 */
public class SecurityLog extends Log {

    public enum Priority {

        LOW,

        MEDIUM,

        HIGH,

    }

    private static final long serialVersionUID = 8405302259584226584L;

    private long time = System.currentTimeMillis();

    private Priority priority = null;

    private String userName = StringUtils.EMPTY;

    private String description = StringUtils.EMPTY;

    private String userAgent = StringUtils.EMPTY;

    private String address = StringUtils.EMPTY;

    private AccessLevel level = null;

    private int count = 1;

    public SecurityLog() {
        super();
    }

    public SecurityLog(Long domainID, String domainName, String userName, AccessLevel level, Priority priority, String userAgent,
            String address, String description) {

        super(domainID, domainName, StringUtils.randomTimeHex());
        this.userName = userName;
        this.level = level;
        this.priority = priority;
        this.userAgent = userAgent;
        this.address = address;
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public AccessLevel getLevel() {
        return level;
    }

    @Override
    public String getLogDescription() {
        return getPriority() + ": " + getDescription() + (count > 1 ? " (" + count + "x)" : "");
    }

    @Override
    public long getLogTime() {
        return getTime();
    }

    @Override
    public String getLogTitle() {
        return getUserName();
    }

    @Override
    public Type getLogType() {
        return Type.SECURITY;
    }

    public Priority getPriority() {
        return priority;
    }

    public long getTime() {
        return time;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUserName() {
        return userName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(priority);
        builder.append(StringUtils.WHITESPACE);
        builder.append(DateUtils.formatLocal(time));
        builder.append(StringUtils.WHITESPACE);
        builder.append(userName);
        builder.append(StringUtils.WHITESPACE);
        builder.append(level);
        builder.append(StringUtils.WHITESPACE);
        builder.append(getDomainName());
        builder.append(StringUtils.WHITESPACE);
        builder.append(description);
        if (count > 1) {
            builder.append(StringUtils.WHITESPACE);
            builder.append("(" + count + "x)");
        }
        return builder.toString();
    }
}
