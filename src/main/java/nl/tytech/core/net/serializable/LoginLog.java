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

import java.util.Collection;
import nl.tytech.util.DateUtils;
import nl.tytech.util.StringUtils;

/**
 * Logging of daily user logins from different addresses during the day
 *
 * @author Maxim Knepfle
 *
 */
public class LoginLog extends Log {

    private static final long serialVersionUID = 8505302259584226522L;

    private String userName = StringUtils.EMPTY;

    private long firstLoginTime = 0;

    private String[] addresses = new String[0];

    public LoginLog() {

    }

    public LoginLog(Long domainID, String domainName, String userName, long firstLoginTime, Collection<String> addresses) {

        super(domainID, domainName, StringUtils.randomTimeHex());
        this.userName = userName;
        this.firstLoginTime = firstLoginTime;
        this.addresses = addresses.toArray(String[]::new);
    }

    public String[] getAddresses() {
        return addresses;
    }

    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    @Override
    public String getLogDescription() {
        return "User first login: " + StringUtils.dateToHumanString(firstLoginTime, null) + " (IP addresses: " + String.join(",", addresses)
                + ")";
    }

    @Override
    public long getLogTime() {
        return getFirstLoginTime();
    }

    @Override
    public String getLogTitle() {
        return getUserName();
    }

    @Override
    public Type getLogType() {
        return Type.LOGINS;
    }

    public String getUserName() {
        return userName;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(DateUtils.formatLocal(getFirstLoginTime()));
        builder.append(StringUtils.WHITESPACE);
        builder.append(getDomainName());
        builder.append(StringUtils.WHITESPACE);
        builder.append(getLogDescription());
        return builder.toString();
    }
}
