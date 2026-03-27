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

import nl.tytech.core.net.Network;
import nl.tytech.util.DateUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Logging of sessions
 * @author Maxim Knepfle
 *
 */
public class SessionLog extends Log {

    private static final long serialVersionUID = 8405302259594226584L;

    private long startupTime = System.currentTimeMillis();

    private String projectName = StringUtils.EMPTY;

    private String userName = StringUtils.EMPTY;

    private String sessionType = StringUtils.EMPTY;

    @Deprecated
    private boolean publicSession = false;

    private long shutdownTime = -1;

    public SessionLog() {
        super();
    }

    public SessionLog(Long domainID, String domainName, String token, String userName, String projectName, String sessionType) {

        super(domainID, domainName, token);
        this.userName = userName;
        this.projectName = projectName;
        this.sessionType = sessionType;
    }

    @Override
    public String getLogDescription() {
        return getSessionType();
    }

    @Override
    public long getLogTime() {
        return getStartupTime();
    }

    @Override
    public String getLogTitle() {
        return getProjectName();
    }

    @Override
    public Type getLogType() {
        return Type.SESSIONS;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSessionType() {
        return sessionType;
    }

    public Network.SessionType getSessionTypeEnum() {

        if (!StringUtils.containsData(sessionType)) {
            TLogger.severe("SessionType cannot be null in session log of project " + projectName + " in domain " + getDomainName());
            return null;
        }
        for (Network.SessionType aSessionType : Network.SessionType.values()) {
            if (aSessionType.name().equals(sessionType.trim())) {
                return aSessionType;
            }
        }
        TLogger.severe("SessionType cannot be null in session log");
        return null;
    }

    public long getShutdownTime() {
        return shutdownTime;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setPublicSession(boolean publicSession) {
        this.publicSession = publicSession;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public void setShutdownTime(long shutdownTime) {
        this.shutdownTime = shutdownTime;
    }

    public void setStartupTime(long startupTime) {
        this.startupTime = startupTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {

        String result = projectName + " (" + (this.publicSession ? "Public " : "Private ")
                + StringUtils.capitalizeWithSpacedUnderScores(sessionType) + "): " + DateUtils.formatLocal(startupTime);
        if (shutdownTime > 0) {
            result += " --- " + DateUtils.formatLocal(shutdownTime);
        }
        if (StringUtils.containsData(userName)) {
            result += " started by: " + userName;
        }
        return result;
    }
}
