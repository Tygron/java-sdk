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
 * Logging of user noticeable actions
 *
 * @author Maxim Knepfle
 *
 */
public class UserLog extends Log {

    private static final long serialVersionUID = 8505302259584226584L;

    private long time = System.currentTimeMillis();

    private String userName = StringUtils.EMPTY;

    private String description = StringUtils.EMPTY;

    private AccessLevel level = null;

    public UserLog() {
        super();
    }

    public UserLog(Long domainID, String domainName, String userName, AccessLevel level, String description) {

        super(domainID, domainName, StringUtils.randomTimeHex());
        this.userName = userName;
        this.level = level;
        this.description = description;
    };

    public String getDescription() {
        return description;
    }

    public AccessLevel getLevel() {
        return level;
    }

    @Override
    public String getLogDescription() {
        return getDescription();
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
        return Type.USERS;
    }

    public long getTime() {
        return time;
    }

    public String getUserName() {
        return userName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(DateUtils.formatLocal(time));
        builder.append(StringUtils.WHITESPACE);
        builder.append(userName);
        builder.append(StringUtils.WHITESPACE);
        builder.append(level);
        builder.append(StringUtils.WHITESPACE);
        builder.append(getDomainName());
        builder.append(StringUtils.WHITESPACE);
        builder.append(description);
        return builder.toString();
    }
}
