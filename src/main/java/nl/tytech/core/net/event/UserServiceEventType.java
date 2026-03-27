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
package nl.tytech.core.net.event;

import static nl.tytech.core.net.event.RemoteServicesEvent.OPTIONAL_DOMAIN_NAME;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.item.annotations.ClassDescription;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.serializable.Creation;
import nl.tytech.core.net.serializable.Domain;
import nl.tytech.core.net.serializable.Log;
import nl.tytech.core.net.serializable.TLicense;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;

/**
 * Service events related to user management.
 *
 * @author Maxim Knepfle
 */
@ClassDescription("Events related to Domain & User management (add, remove, etc).")
public enum UserServiceEventType implements ServiceEventType {

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Create a new user.", params = { OPTIONAL_DOMAIN_NAME, "Username" })
    ADD_USER(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get the Domain settings data.", params = { OPTIONAL_DOMAIN_NAME }, hidden = 0)
    GET_DOMAIN(Domain.class, AccessLevel.JOIN_ONLY, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get the domain logo. Returns a .png file.", params = { OPTIONAL_DOMAIN_NAME }, hidden = 0)
    GET_DOMAIN_ICON(byte[].class, AccessLevel.JOIN_ONLY, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get a list of all Projects created in the Domain, during the past license year.", params = {
            OPTIONAL_DOMAIN_NAME }, hidden = 0)
    GET_DOMAIN_NEW_PROJECTS_FOR_LICENSE_YEAR(Creation[].class, AccessLevel.HOST_SESSION, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get the names of users in the Domain. Returns just the names of the users.", params = {
            OPTIONAL_DOMAIN_NAME }, hidden = 0)
    GET_DOMAIN_USER_NAMES(String[].class, AccessLevel.DOMAIN_ADMIN, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get all users in the Domain. Returns both names and additional data of the users.", params = {
            OPTIONAL_DOMAIN_NAME }, hidden = 0)
    GET_DOMAIN_USERS(User[].class, AccessLevel.DOMAIN_ADMIN, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get specific log.", params = { OPTIONAL_DOMAIN_NAME, "Type", "Log Token" }, defaults = { "", "SESSIONS", "" })
    GET_LOG(Log.class, AccessLevel.DOMAIN_ADMIN, String.class, Log.Type.class, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get logs since a date.", params = { OPTIONAL_DOMAIN_NAME, "Type", "Date in millis" }, defaults = { "",
            "SESSIONS", "0" })
    GET_LOGS(Log[].class, AccessLevel.DOMAIN_ADMIN, String.class, Log.Type.class, Long.class),

    @Deprecated
    @EventParamData(desc = "Use GET_DOMAIN instead for identical result.", params = {})
    GET_MY_DOMAIN(Domain.class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get your login key for continued authentication.", params = {})
    GET_MY_LOGIN_KEY(String.class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get the general data of your user account.", params = {})
    GET_MY_USER(User.class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Remove a user. Returns whether the user was deleted.", params = { "Username" })
    REMOVE_USER(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class),

    @EventParamData(desc = "Reset the password for the specified user. The user will receive a new temporary password in their email.", params = {
            "Username" })
    RESET_PASSWD(Boolean.class, AccessLevel.NONE, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Update the Domain contact information.", params = { OPTIONAL_DOMAIN_NAME, "Address", "Zipcode", "City",
            "Country" })
    SET_DOMAIN_ADRESS_INFO(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, String.class, String.class, String.class, String.class),

    @EventParamData(desc = "Set Domain Security Policy.", params = { "Two Factor Level", "Min password length",
            "Login Key Expiration (in milliseconds)", "Support access to GeoShare" })
    SET_DOMAIN_SECURITY_POLICY(Boolean.class, AccessLevel.DOMAIN_ADMIN, AccessLevel.class, Integer.class, Long.class, Boolean.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Change the License of a Domain to a different level.", params = { OPTIONAL_DOMAIN_NAME, "License" })
    SET_DOMAIN_LICENSE(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, TLicense.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Set the Domain icon used in (web) viewers. The image must be in .png format, must be square, and must be at most 100KB.", params = {
            OPTIONAL_DOMAIN_NAME, ".png file" })
    SET_DOMAIN_ICON(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, byte[].class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Set whether a specified user account is active. Returns whether the user was successfully updated.", params = {
            OPTIONAL_DOMAIN_NAME, "Username", "Whether account should be active" })
    SET_USER_ACTIVE(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, String.class, Boolean.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Update the values for the given user.", params = { OPTIONAL_DOMAIN_NAME, "Username", "Nickname", "First name",
            "Last name", "Phone number", "User access level" })
    SET_USER_DATA(Boolean.class, AccessLevel.HOST_SESSION, String.class, String.class, String.class, String.class, String.class,
            String.class, AccessLevel.class),

    @EventParamData(desc = "Change the password for a given user. Returns the user's login key for continued authentication, or null if you are unauthorized to change that user's password.", params = {
            "Username", "Password" })
    SET_USER_PASSWD(String.class, AccessLevel.JOIN_ONLY, String.class, String.class);

    private final Class<?> responseClass;

    private final AccessLevel level;

    private final List<Class<?>> classes;

    private UserServiceEventType(AccessLevel level, Class<?>... c) {
        this(null, level, c);
    }

    private UserServiceEventType(Class<?> responseClass, AccessLevel level, Class<?>... classes) {

        if (level == null || level == AccessLevel.SUPER_USER) {
            throw new IllegalArgumentException("Invalid access level: " + level);
        }
        this.responseClass = responseClass;
        this.level = level;
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public boolean canDomainOverride() {
        return this == GET_DOMAIN_ICON;
    }

    @Override
    public AccessLevel getAccessLevel() {
        return level;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {

        if ((this == GET_LOG || this == GET_LOGS) && args[1] instanceof Log.Type logType) {
            return logType.getArrayClass();
        } else {
            return responseClass;
        }
    }

    @Override
    public Integer getTimeoutOverride() {
        return null;
    }

    @Override
    public boolean isRoutingServerOnly() {
        return false;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
