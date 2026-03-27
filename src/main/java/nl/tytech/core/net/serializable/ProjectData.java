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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Info about a project
 *
 * @author Maxim Knepfle
 */
public class ProjectData implements Comparable<ProjectData>, Serializable {

    public enum Permission {
        NONE, READ, WRITE;
    }

    public enum PermissionType {
        OWNER, DOMAIN, SUPPORT;
    }

    private static final long serialVersionUID = -4097764829254238078L;

    public static final int MIN_NAME_LENGTH = 3;

    public static final int MAX_NAME_LENGTH = 20;

    public static final String ROOT_DOMAIN_ONLY = "Root Domain Only";

    public static final long DELETE_TIME_MS = 3 * Moment.DAY;

    private static final String DEMO = "demo";

    private static final String INTRO = "intro";

    private static final double DETAILED_COST = 1.0;

    private static final double BASIC_COST = 1.0 / 25.0; // 5x5m cell is 1/25th cost

    private static final Pattern REGEX_NAME = Pattern.compile("[a-z0-9_-]{" + MIN_NAME_LENGTH + "," + MAX_NAME_LENGTH + "}");

    public static final String getName(String name) {
        return StringUtils.toLowerCaseUnderscore(name);
    }

    public static boolean isValidProjectName(String name) {
        return REGEX_NAME.matcher(name).matches();
    }

    /**
     * Language versions
     */

    private String[] languages = new String[] { TLanguage.NL.name() };// LANGUAGES

    /**
     * Permissions (defaults for old projects, new ones get WRITE READ NONE)
     */
    private String[] permissions = new String[] { Permission.WRITE.name(), Permission.NONE.name(), Permission.NONE.name() };// PERMISSIONS

    /**
     * Shared with all
     */
    private boolean universal = false;// UNIVERSAL

    /**
     * In Trash Bin when not null and > 0
     */
    private Long deleteDate = null;

    /**
     * In Archive when not null and > 0
     */
    private Long archiveDate = null;

    /**
     * Project will be remove from trash at this date
     */
    private Long restoreDate = null;

    /**
     * Last new/save actions
     */
    private Long lastActivity = null;

    /**
     * Project's last session ID
     */
    private Integer lastSessionID = null;

    private String lastUser = null;

    /**
     * Original username of the creator of this project.
     */
    private String owner = "?";// CREATOR

    /**
     * Project disclaimer when use as a template for a new project.
     */
    private String disclaimer = "";

    /**
     * When true project is being edited at this moment
     */
    private boolean editing = false;

    /**
     * When true project is a template project
     */
    private boolean template = false;// TEMPLATE

    /**
     * When true project is high detailed
     */
    private boolean detailed = true;

    private String description = "";// DESCRIPTION

    private String domain = "";

    private String subDomain = ROOT_DOMAIN_ONLY;

    private String fileName = "";

    @Deprecated
    private String[] versions = new String[] { "Base Version" };

    private HashMap<Integer, String> versionMap = new HashMap<>();

    private int activeVersion = 0;

    private int[] sizeM = new int[] { 0, 0 };

    private String bbox = ""; // 3857 BBOX

    private long byteSize = -1;

    public ProjectData() {

    }

    @Override
    public int compareTo(ProjectData o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

    public int getActiveVersion() {
        return activeVersion;
    }

    public Long getArchiveDate() {
        return archiveDate;
    }

    public String getBbox() {
        return bbox;
    }

    public long getByteSize() {
        return byteSize;
    }

    public Long getDeleteDate() {
        return deleteDate;
    }

    public String getDescription() {
        return description;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getDomain() {
        return domain;
    }

    public String getFileName() {
        return fileName;
    }

    public String[] getLanguages() {
        return languages;
    }

    public List<TLanguage> getLanguagesList() {

        List<TLanguage> langList = new ArrayList<>();
        for (String lang : this.languages) {
            langList.add(TLanguage.valueOf(lang.trim()));
        }

        // sort
        Collections.sort(langList);
        return langList;
    }

    public Long getLastActivity() {
        return lastActivity;
    }

    public Integer getLastSessionID() {
        return lastSessionID;
    }

    public String getLastUser() {
        return lastUser;
    }

    public double getLicenseCostKM2() {
        return sizeM[0] / 1000d * (sizeM[1] / 1000d) * (detailed ? DETAILED_COST : BASIC_COST);
    }

    public String getOwner() {
        return owner;
    }

    public Permission getPermission(PermissionType type) {
        return Permission.valueOf(this.permissions[type.ordinal()]);
    }

    public String[] getPermissions() {
        return permissions;
    }

    public Long getRestoreDate() {
        return restoreDate;
    }

    public int[] getSizeM() {
        return sizeM;
    }

    public long getSizeM2() {
        return (long) sizeM[0] * (long) sizeM[1];
    }

    public String getSubDomain() {
        return subDomain;
    }

    public Map<Integer, String> getVersionMap() {
        return versionMap;
    }

    public final boolean isActive() {
        return !isTrashed() && !isArchived();
    }

    public final boolean isArchived() {
        return this.archiveDate != null && this.archiveDate > 0l;
    }

    public boolean isDeleteable(User user) {

        if (user == null) {
            TLogger.severe("Checking deleteable for NULL user!");
            return false;
        }

        // universal template project (shared with all) not deleteable
        if (this.isUniversal()) {
            return false;
        }

        // admins of this domain may always delete it.
        if (user.getDomain().equals(this.getDomain()) && user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal()) {
            return true;
        }

        /**
         * Else only with write rights
         */
        return this.isWritable(user);
    }

    public boolean isDetailed() {
        return detailed;
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean isPermissionable(User user) {

        if (user == null) {
            TLogger.severe("Checking writable for NULL user!");
            return false;
        }

        // universal template project (shared with all) not permissionable
        if (this.isUniversal()) {
            return false;
        }
        /**
         * Owner may always change permission, templates etc.
         */
        if (user.getUserName().equals(owner)) {
            return true;
        }

        /**
         * Domain admin may also change this.
         */
        if (user.getDomain().equals(domain) && user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal()) {
            return true;
        }
        /**
         * Else only with write rights
         */
        return this.isWritable(user);
    }

    /**
     * When true this project is readable for this user
     */
    public boolean isReadable(User user) {

        if (user == null) {
            TLogger.severe("Checking readbility for NULL user!");
            return false;
        }
        // universal template project (shared with all)
        if (this.isUniversal()) {
            return true;
        }
        if (user.getUserName().equals(owner)) {
            return this.getPermission(PermissionType.OWNER) != Permission.NONE;
        }
        if (user.getDomain().equals(domain) && (user.isAllSubDomains() || user.getSubDomain().equals(subDomain))) {
            return this.getPermission(PermissionType.DOMAIN) != Permission.NONE;
        }
        if (user.isSuperUser()) {
            return this.getPermission(PermissionType.SUPPORT) != Permission.NONE;
        }
        return false;
    }

    public boolean isReadOnly() {

        if (isUniversal()) {
            return true;
        }

        for (PermissionType type : PermissionType.values()) {
            if (this.getPermission(type) == Permission.WRITE) {
                return false;
            }
        }
        return true;
    }

    public boolean isRootDomainOnly() {
        return ROOT_DOMAIN_ONLY.equals(subDomain);
    }

    public boolean isTemplate() {
        return this.template;
    }

    public final boolean isTrashed() {
        return this.deleteDate != null && this.deleteDate > 0l;
    }

    public boolean isUniversal() {
        return this.universal;
    }

    public boolean isUniversalDemo() {
        return isUniversal() && getFileName().toLowerCase().startsWith(DEMO);
    }

    public boolean isUniversalIntro() {
        return isUniversal() && getFileName().toLowerCase().startsWith(INTRO);
    }

    public boolean isVisible(User user) {

        if (user == null) {
            TLogger.severe("Checking visbility for NULL user!");
            return false;
        }
        // universals, superusers and owners may always see it
        if (this.isUniversal() || user.isSuperUser() || user.getUserName().equals(owner)) {
            return true;
        }

        if (user.getDomain().equals(domain)) {
            // domain admins may always see the project
            if (user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal()) {
                return true;
            }
            // other user only when permission allows so
            if (user.isAllSubDomains() || user.getSubDomain().equals(subDomain)) {
                return this.getPermission(PermissionType.DOMAIN) != Permission.NONE;
            }
        }
        return false;
    }

    public boolean isWritable(User user) {

        if (user == null) {
            TLogger.severe("Checking writable for NULL user!");
            return false;
        }
        // universal template project (shared with all) not writable
        if (this.isUniversal()) {
            return false;
        }
        if (user.getUserName().equals(owner)) {
            return this.getPermission(PermissionType.OWNER) == Permission.WRITE;
        }
        if (user.getDomain().equals(domain) && (user.isAllSubDomains() || user.getSubDomain().equals(subDomain))) {
            return this.getPermission(PermissionType.DOMAIN) == Permission.WRITE;
        }
        if (user.isSuperUser()) {
            return this.getPermission(PermissionType.SUPPORT) == Permission.WRITE;
        }
        return false;
    }

    public void setActiveVersion(int activeVersion) {
        this.activeVersion = activeVersion;
    }

    public void setArchiveDate(Long archiveDate) {
        this.archiveDate = archiveDate;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public void setByteSize(long byteSize) {
        this.byteSize = byteSize;
    }

    public void setDeleteDate(Long deleteDate) {
        this.deleteDate = deleteDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Deprecated
    public void setFullName(String deprecated) {
        // ignore
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public void setLastActivity(Long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void setLastSessionID(Integer lastSessionID) {
        this.lastSessionID = lastSessionID;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }

    public void setOwner(String creator) {
        this.owner = creator;
    }

    public void setPermission(PermissionType type, Permission permission) {
        this.permissions[type.ordinal()] = permission.name();
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setRestoreDate(Long restoreDate) {
        this.restoreDate = restoreDate;
    }

    public void setSizeM(int[] sizeM) {
        this.sizeM = sizeM;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public void setUniversal(boolean universal) {
        this.universal = universal;
    }

    public void setVersionMap(HashMap<Integer, String> versionMap) {
        this.versionMap = versionMap;
    }

    @Deprecated
    public void setVersions(String[] versions) {
        for (int i = 0; i < versions.length; ++i) {
            this.versionMap.put(i, versions[i]);
        }
    }

    @Override
    public String toString() {
        return StringUtils.capitalizeWithSpacedUnderScores(getFileName());
    }
}
