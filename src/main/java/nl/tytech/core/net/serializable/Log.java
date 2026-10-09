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

import java.io.File;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.Description;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Base class for logging
 *
 * @author Maxim Knepfle
 *
 */
public abstract class Log implements Serializable, Comparable<Log> {

    public enum Type {

        @Description("Session related logging, e.g. startup time, session type.")
        SESSIONS,

        @Description("User related logging, e.g. user or domain add/remove.")
        USERS,

        @Description("Security related logging, e.g. invalid logins, unlawful access.")
        SECURITY,

        @Description("Backup related logging, e.g. successful or failed backup of GeoShare and Projects.")
        BACKUP,

        @Description("Daily User logins from all IP addresses, updated at midnight.")
        LOGINS,

        @Description("Daily Domain simulation jobs, updated at midnight.")
        JOBS;

        public final String getDescription() {
            return ObjectUtils.getDescription(this);
        }

        public final String getDirectory() {
            return LOGS_DIR + StringUtils.capitalizeWithSpacedUnderScores(this) + File.separator;
        }

        public final Class<? extends Log> getLogClass() {

            switch (this) {
                case SECURITY:
                    return SecurityLog.class;
                case USERS:
                    return UserLog.class;
                case SESSIONS:
                    return SessionLog.class;
                case BACKUP:
                    return BackupLog.class;
                case LOGINS:
                    return LoginLog.class;
                case JOBS:
                    return JobLog.class;
                default:
                    TLogger.severe("Unknown log type: " + this);
                    return null;
            }
        }
    }

    public static final int MIN_RETENTION = 6;

    public static final int DEFAULT_RETENTION = 5 * 12;

    public static final int MAX_RETENTION = 10 * 12;

    private static final String LOGS_DIR = "Logs" + File.separator;

    public static final String EXTENSION = ".def";

    private static final long serialVersionUID = 4910833050598168089L;

    private String domainName = StringUtils.EMPTY;

    @JsonIgnore
    private Long domainID = -2l;

    private String token = StringUtils.EMPTY;

    public Log() {
    }

    public Log(Long domainID, String domainName, String token) {

        this.domainID = domainID;
        this.domainName = domainName;
        this.token = token;
    }

    @Override
    public int compareTo(Log other) {
        return Long.compare(other.getLogTime(), this.getLogTime()); // higher value first
    }

    public Long getDomainID() {
        return domainID;
    }

    public final String getDomainName() {
        return domainName;
    }

    public abstract String getLogDescription();

    public abstract long getLogTime();

    public abstract String getLogTitle();

    public abstract Type getLogType();

    public final String getToken() {
        return token;
    }

    public final boolean isDomainID(Long domainID) {
        return this.domainID != null && this.domainID.equals(domainID);
    }

    public final void setDomainID(Long domainID) {
        this.domainID = domainID;
    }

    public final void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public final void setToken(String token) {
        this.token = token;
    }
}
