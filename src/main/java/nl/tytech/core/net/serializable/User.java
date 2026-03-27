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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.util.StringUtils;

/**
 * User definition class
 * @author Maxim Knepfle
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    public enum AccessLevel {

        NONE("User can do almost nothing except passwd reset and trial."),

        JOIN_ONLY("User can only join or get invited to sessions hosted by others."),

        HOST_SESSION("User is allowed to host single or multi user sessions."),

        EDITOR("User is allowed to host or join a edit sessions."),

        DOMAIN_ADMIN("User can manage projects and other users in his/her domain."),

        SUPER_USER("Super user can also manage projects and users of other domains.");

        private String explaination;

        private AccessLevel(String explaination) {
            this.explaination = explaination;
        }

        public String getExplaination() {
            return explaination;
        }
    }

    public static final int NAME_MIN_LENGHT = 1;

    public static final int NAME_MAX_LENGHT = 50;

    /**
     * Human names include special Latin chars for e.g.: Hernán
     */
    private static final String NAME_CHARS = "a-zA-ZÀ-ȕ0-9 -";

    public static final int PASSWD_MIN_LENGHT = 8;

    public static final int PASSWD_DEFAULT_LENGHT = 16;

    public static final int PASSWD_MAX_LENGHT = 128;

    public static final long ADMIN_RESET_PERIOD = Moment.WEEK;

    public static final long USER_RESET_PERIOD = Moment.HOUR;

    private static final long serialVersionUID = -6386648876639626665L;

    public static final String DEFAULT_FIRST_NAME = "First Name";

    public static final String DEFAULT_LAST_NAME = "Family Name";

    public static final String VALID_NAME_TXT = "%s is invalid, please use between: " + NAME_MIN_LENGHT + "-" + NAME_MAX_LENGHT
            + " characters.";

    public static final String VALID_PASSWD_TXT = "Password is invalid, please use between %s-" + PASSWD_MAX_LENGHT
            + " characters and/or numbers!";

    public static final String toValidName(String name) {

        name = name != null ? name.replaceAll("[^" + NAME_CHARS + "]+", "") : "";
        name = name.length() >= NAME_MIN_LENGHT ? name : StringUtils.randomHumanString(NAME_MIN_LENGHT);
        name = name.length() <= NAME_MAX_LENGHT ? name : name.substring(0, NAME_MAX_LENGHT);
        return name;
    }

    public static final boolean validName(String name) {
        return name != null && name.trim().matches("[" + NAME_CHARS + "]{" + NAME_MIN_LENGHT + "," + NAME_MAX_LENGHT + "}");
    }

    public static final boolean validPasswd(String password) {
        return validPasswd(password, PASSWD_MIN_LENGHT);
    }

    public static final boolean validPasswd(String password, int minLength) {
        return password != null //
                && password.length() >= Math.max(PASSWD_MIN_LENGHT, minLength) //
                && password.length() <= PASSWD_MAX_LENGHT //
                && StandardCharsets.US_ASCII.newEncoder().canEncode(password);
    }

    private String userName = StringUtils.EMPTY;

    private String nickName = StringUtils.EMPTY;

    private String phone = StringUtils.EMPTY;

    private long lastLogin = Item.NONE;

    private long passwdExpiryDate = Long.MAX_VALUE;

    private String domain = StringUtils.EMPTY;

    private String subDomain = Domain.ALL_SUB_DOMAINS;

    private String firstName = DEFAULT_FIRST_NAME;

    private String lastName = DEFAULT_LAST_NAME;

    private String maxOption = StringUtils.EMPTY;

    private ArrayList<String> recentProjects = new ArrayList<>();

    private boolean active = true;

    public User() {

    }

    public String getDomain() {
        return domain;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return Domain.PUBLIC.equals(domain) ? this.getFirstName() : this.getFirstName() + " " + this.getLastName();
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getLastName() {
        return lastName;
    }

    public AccessLevel getMaxAccessLevel() {
        return StringUtils.containsData(maxOption) ? AccessLevel.valueOf(maxOption) : null;
    }

    public String getMaxOption() {
        return maxOption;
    }

    public String getNickName() {
        return nickName;
    }

    public long getPasswdExpiryDate() {
        return passwdExpiryDate;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<String> getRecentProjects() {
        return recentProjects;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isActive() {
        return active;
    }

    public final boolean isAllSubDomains() {
        return Domain.ALL_SUB_DOMAINS.equals(this.subDomain);
    }

    public final boolean isInfoComplete() {

        if (!StringUtils.containsData(this.userName)) {
            return false;
        }
        if (!StringUtils.containsData(this.domain)) {
            return false;
        }
        if (Domain.PUBLIC.equals(domain)) {
            return true;
        }
        if (!validName(this.firstName) || DEFAULT_FIRST_NAME.equals(this.firstName.trim())) {
            return false;
        }
        if (!validName(this.lastName) || DEFAULT_LAST_NAME.equals(this.lastName.trim())) {
            return false;
        }
        if (!StringUtils.containsData(this.phone) || !StringUtils.validPhone(this.phone)) {
            return false;
        }
        return true;
    }

    public final boolean isSuperUser() {
        return this.getMaxAccessLevel() == AccessLevel.SUPER_USER;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public void setMaxOption(String maxOption) {
        this.maxOption = maxOption;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPasswdExpiryDate(long passwdExpiryDate) {
        this.passwdExpiryDate = passwdExpiryDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRecentProjects(ArrayList<String> recentProjects) {
        this.recentProjects = recentProjects;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    @Override
    public String toString() {
        return this.getUserName();
    }
}
