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

import java.io.Serializable;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.StringUtils;

/**
 * Contains info about the session.
 *
 * @author Maxim Knepfle
 *
 */
public class SessionInfo implements Serializable {

    private static final long serialVersionUID = -8634240612067574617L;

    private Integer id;

    private String name = StringUtils.EMPTY;

    private TLanguage language;

    private String groupToken;

    private SessionType sessionType;

    private boolean wizardFinished = true;

    private boolean detailed = true;

    private int[] mapSizeM = new int[] { 0, 0 };

    private long timeoutMS = 0;

    /**
     * Client App Type for continue session
     */
    private AppType appType;

    /**
     * Client Token for continue session
     */
    private TokenPair tokenPair;

    public SessionInfo() {

    }

    public Network.AppType getAppType() {
        return appType;
    }

    public String getGroupToken() {
        return groupToken;
    }

    public Integer getID() {
        return id;
    }

    public TLanguage getLanguage() {
        return language;
    }

    public int[] getMapSizeM() {
        return mapSizeM;
    }

    public long getMapSizeM2() {
        return (long) mapSizeM[0] * (long) mapSizeM[1];
    }

    public String getName() {
        return name;
    }

    public Network.SessionType getSessionType() {
        return sessionType;
    }

    public long getTimeoutMS() {
        return timeoutMS;
    }

    public TokenPair getTokenPair() {
        return tokenPair;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public boolean isWizardFinished() {
        return wizardFinished;
    }

    public void setAppType(Network.AppType appType) {
        this.appType = appType;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    public void setGroupToken(String groupToken) {
        this.groupToken = groupToken;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setLanguage(TLanguage language) {
        this.language = language;
    }

    public void setMapSizeM(int[] mapSizeM) {
        this.mapSizeM = mapSizeM;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSessionType(Network.SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public void setTimeoutMS(long timeoutMS) {
        this.timeoutMS = timeoutMS;
    }

    public void setTokenPair(TokenPair tokenPair) {
        this.tokenPair = tokenPair;
    }

    public void setWizardFinished(boolean wizardFinished) {
        this.wizardFinished = wizardFinished;
    }

    @Override
    public String toString() {

        String type = sessionType != null ? StringUtils.capitalizeWithSpacedUnderScores(sessionType.name()) + ": " : "";
        String wizard = wizardFinished ? type : "Map Creation: ";

        String hName = StringUtils.capitalizeWithSpacedUnderScores(name) + " ";
        String info = "(" + language.name() + (sessionType != SessionType.EDITOR ? " " + id : "") + ")";
        String keep = timeoutMS > KeepAlive.NEVER.getTimeoutMS() ? "\nKeep Alive" : "";

        return wizard + hName + info + keep;
    }
}
