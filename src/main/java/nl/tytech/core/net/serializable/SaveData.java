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

/**
 * SaveData: contains data about a saved session
 *
 * @author Maxim Knepfle
 */
public class SaveData implements Comparable<SaveData>, Serializable {

    private static final long serialVersionUID = 6874618008696400543L;

    private String saveName;

    private String projectDomain;

    // Note: NOT project domain but session domain, e.g. customer domain saving universal project
    private String sessionDomain;

    private String projectName;

    @Deprecated
    private int projectVersion;

    private String info;

    private String language;

    private String token;

    private String sessionName;

    private long sessionTime = System.currentTimeMillis();

    private long saveTime = System.currentTimeMillis();

    private Integer sessionID = 0;// default session ID

    public SaveData() {

    }

    @Override
    public int compareTo(SaveData other) {
        return Long.compare(this.saveTime, other.saveTime);
    }

    public String getInfo() {
        return info;
    }

    public String getLanguage() {
        return language;
    }

    public String getProjectDomain() {
        return projectDomain;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSaveName() {
        return saveName;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public String getSessionDomain() {
        return sessionDomain;
    }

    public Integer getSessionID() {
        return sessionID;
    }

    public String getSessionName() {
        return sessionName;
    }

    public long getSessionTime() {
        return sessionTime;
    }

    public String getToken() {
        return token;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setProjectDomain(String projectDomain) {
        this.projectDomain = projectDomain;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public void setSessionDomain(String sessionDomain) {
        this.sessionDomain = sessionDomain;
    }

    public void setSessionID(Integer sessionID) {
        this.sessionID = sessionID;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setSessionTime(long sessionTime) {
        this.sessionTime = sessionTime;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
