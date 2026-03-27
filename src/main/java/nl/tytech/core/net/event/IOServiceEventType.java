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
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.serializable.Domain;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.KeepAlive;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.ProjectData.Permission;
import nl.tytech.core.net.serializable.ProjectData.PermissionType;
import nl.tytech.core.net.serializable.SaveData;
import nl.tytech.core.net.serializable.SessionData;
import nl.tytech.core.net.serializable.SessionInfo;
import nl.tytech.core.net.serializable.TokenPair;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.TLanguage;

/**
 * Service events related to IO, like starting a Project Session.
 *
 * @author Maxim Knepfle
 */
@ClassDescription("Events related to Project & Session management (start, close, etc).")
public enum IOServiceEventType implements ServiceEventType {

    @EventParamData(desc = "Create a new Project with a given name and language, Returns the new Project´s data.", params = {
            "Project Name", "Language (NL, EN)", "High Detail (<10m DEM, <5m grid)" }, defaults = { "", "EN", "true" })
    @EventIDField(nullable = { 1, 2 })
    ADD_PROJECT(ProjectData.class, AccessLevel.EDITOR, String.class, TLanguage.class, Boolean.class),

    @EventParamData(desc = "Create a new version for this Project.", params = { "Session ID", "Version Name" })
    ADD_PROJECT_VERSION(AccessLevel.EDITOR, Integer.class, String.class),

    @EventParamData(desc = "Disconnect your Application from a running Session, optionally closing the Session if your Application was the last connected application.", params = {
            "Session ID", "Client Session token", "Keep Session running, even if this was the last client" })
    CLOSE(Boolean.class, AccessLevel.JOIN_ONLY, Integer.class, String.class, Boolean.class),

    @EventParamData(desc = "Get all Sessions that you can continue, given your token pairs.", params = { "TokenPair array", })
    GET_CONTINUABLE_SESSIONS(SessionInfo[].class, AccessLevel.JOIN_ONLY, TokenPair[].class),

    @EventParamData(desc = "Get the Domain messages. This usually lists the most recent Session starts in that domain.", params = {})
    GET_INTRO_MESSAGE(String.class, AccessLevel.JOIN_ONLY),

    @Deprecated
    @EventParamData(desc = "Use GET_INTRO_MESSAGE instead for identical result.", params = {})
    GET_DOMAIN_MESSAGE(String.class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get all active Sessions in my Domain.")
    GET_SESSIONS(SessionData[].class, AccessLevel.HOST_SESSION),

    @Deprecated
    @EventParamData(desc = "Use GET_SESSIONS instead for identical result.", params = {})
    GET_DOMAIN_SESSIONS(SessionData[].class, AccessLevel.HOST_SESSION),

    @EventParamData(desc = "Get all Projects in the Domain which can start a Session.", params = {})
    GET_STARTABLE_PROJECTS(ProjectData[].class, AccessLevel.JOIN_ONLY),

    @Deprecated
    @EventParamData(desc = "Use GET_STARTABLE_PROJECTS instead for identical result.", params = {})
    GET_DOMAIN_STARTABLE_PROJECTS(ProjectData[].class, AccessLevel.JOIN_ONLY),

    @Deprecated
    @EventParamData(desc = "Use GET_STARTABLE_PROJECTS instead and filter on owner.", params = {})
    GET_MY_STARTABLE_PROJECTS(ProjectData[].class, AccessLevel.EDITOR),

    @EventParamData(desc = "Get all Projects in the Domain which can be used as a template.", params = {})
    GET_STARTABLE_TEMPLATES(ProjectData[].class, AccessLevel.EDITOR),

    @Deprecated
    @EventParamData(desc = "Use GET_STARTABLE_TEMPLATES instead for identical result.", params = {})
    GET_DOMAIN_STARTABLE_TEMPLATES(ProjectData[].class, AccessLevel.EDITOR),

    @EventIDField(nullable = { 0, 1 })
    @EventParamData(desc = "Get the License usage for a given domain.", params = { OPTIONAL_DOMAIN_NAME,
            "Subdomain Name (optional)" }, defaults = { "", Domain.ALL_SUB_DOMAINS }, //
            response = "Returns an array with: New Projects per day, Project count, Domain Total Area (km2), Subdomain Total Area (km2) and GeoShare storage (MB).")
    GET_DOMAIN_USAGE(Integer[].class, AccessLevel.HOST_SESSION, String.class, String.class),

    @EventParamData(desc = "Get all Sessions in your Domain that you can join.", params = {})
    GET_JOINABLE_SESSIONS(SessionInfo[].class, AccessLevel.JOIN_ONLY),

    @Deprecated
    @EventParamData(desc = "Use GET_JOINABLE_SESSIONS instead for identical result.", params = {})
    GET_MY_JOINABLE_SESSIONS(SessionInfo[].class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get the meta data of a specific Project.", params = { "Project Name" })
    GET_PROJECT(ProjectData.class, AccessLevel.JOIN_ONLY, String.class),

    @Deprecated
    @EventParamData(desc = "Use GET_PROJECT instead for identical result.", params = { "Project Name" })
    GET_PROJECT_DATA(ProjectData.class, AccessLevel.JOIN_ONLY, String.class),

    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Get all saved Domain Sessions for a specified Project.", params = { OPTIONAL_DOMAIN_NAME, "Project Name", })
    GET_PROJECT_SAVED_SESSIONS(SaveData[].class, AccessLevel.HOST_SESSION, String.class, String.class),

    @EventParamData(desc = "Get the current server time in millisecond, useful for synchronizing client-server clocks.", params = {}, response = "Current server time in millisecond since epoch 1970")
    GET_SERVER_TIME(Long.class, AccessLevel.JOIN_ONLY),

    @EventParamData(desc = "Get all Domain Projects that you are allowed to manage.", params = {})
    GET_DOMAIN_PROJECTS(ProjectData[].class, AccessLevel.DOMAIN_ADMIN),

    @Deprecated
    @EventParamData(desc = "Use GET_DOMAIN_PROJECTS instead for identical result.", params = {})
    GET_VISIBLE_DOMAIN_PROJECTS(ProjectData[].class, AccessLevel.DOMAIN_ADMIN),

    @EventParamData(desc = "Returns whether the Session identified by the Session ID is set to Keep Alive. Value can also be found in Setting KEEP_ALIVE.", params = {
            "Session ID" })
    GET_SESSION_KEEP_ALIVE(KeepAlive.class, AccessLevel.HOST_SESSION, Integer.class),

    @EventParamData(desc = "Join a currently running Session", params = { "Session ID", "Application type",
            "Your computer name, for easy identification (optional)", "Your client token, for rejoining (optional)" })
    @EventIDField(nullable = { 2, 3 })
    JOIN(JoinReply.class, AccessLevel.JOIN_ONLY, Integer.class, AppType.class, String.class, String.class),

    @EventParamData(desc = "Kill without saving a running Session.", params = { "Session ID" })
    KILL(Boolean.class, AccessLevel.HOST_SESSION, Integer.class),

    @EventParamData(desc = "Loads a previously saved SINGLE or MULTI Session.", params = { "Session Type", "Project Name",
            "Session name (e.g. Session on date X", "Save Name (e.g. AUTOSAVE or specific moment in the Session)",
            "Group token (only for MULTI user Sessions which share the same group) (optional)",
            "Preferred Session ID (optional)" }, response = "Session ID")
    @EventIDField(nullable = { 4, 5 })
    LOAD_SAVED_SESSION(Integer.class, AccessLevel.HOST_SESSION, Network.SessionType.class, String.class, String.class, String.class,
            String.class, Integer.class),

    @EventParamData(desc = "Delete an inactive version of a Project currently running in an EDITOR Session.", params = { "Session ID",
            "Version of the Project" })
    REMOVE_PROJECT_VERSION(Boolean.class, AccessLevel.EDITOR, Integer.class, Integer.class),

    @Deprecated
    @EventIDField(nullable = { 0 })
    @EventParamData(desc = "Delete a Sub Domain.", params = { OPTIONAL_DOMAIN_NAME, "Sub Domain Name" })
    REMOVE_SUB_DOMAIN(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, String.class),

    @EventParamData(desc = "Save the current state of an EDITOR Session as the current version of the running Project. This kind of save changes the initial state of a Project for future times that the Project is started as a Session.", params = {
            "Session ID" })
    SAVE_PROJECT(Boolean.class, AccessLevel.EDITOR, Integer.class),

    @EventIDField(nullable = { 1 })
    @EventParamData(desc = "Save the current state of an EDITOR Session as a new, separate Project. This kind of save sets the initial state of a Project for future times that that Project is started as a Session. The currnetly running Session will continue as a Session working with the newly created Project.", params = {
            "Session ID", OPTIONAL_DOMAIN_NAME, "New Project Name",
            "Save all data (set to false if you want to use the running Session as a template for a new Project)" })
    SAVE_PROJECT_AS(Boolean.class, AccessLevel.EDITOR, Integer.class, String.class, String.class, Boolean.class),

    @EventParamData(desc = "Save the current state of a SINGLE or MULTI as a Session save. This kind of save does not change the data of the Project, and allows you to reload the current state of the Session at a later time.", params = {
            "Session ID", "Randomly generated save token" })
    SAVE_SESSION(Boolean.class, AccessLevel.HOST_SESSION, Integer.class, String.class),

    @EventParamData(desc = "Set which version of a Project is the currently active version. A Project cannot be running when the version is changed.", params = {
            "Project Name", "Version of the Project" })
    SET_PROJECT_ACTIVE_VERSION(Boolean.class, AccessLevel.EDITOR, String.class, Integer.class),

    @EventParamData(desc = "Set whether a specific language is available for a Project.", params = { "Project Name",
            "Project language: NL, EN", "Whether the language is available" })
    SET_PROJECT_LANGUAGE(Boolean.class, AccessLevel.EDITOR, String.class, TLanguage.class, Boolean.class),

    @EventParamData(desc = "Change the name of a Project.", params = { "Name of the Project to rename", "New name for the Project" })
    SET_PROJECT_NAME(Boolean.class, AccessLevel.EDITOR, String.class, String.class),

    @EventParamData(desc = "Change the owner of a Project.", params = { "Project Name", "New owner", })
    SET_PROJECT_OWNER(Boolean.class, AccessLevel.EDITOR, String.class, String.class),

    @EventParamData(desc = "Change the disclaimer text when using the Project as a Template.", params = { "Project Name", "Disclaimer", })
    SET_PROJECT_DISCLAIMER(Boolean.class, AccessLevel.EDITOR, String.class, String.class),

    @EventParamData(desc = "Set an access permission of a specified type for a Project.", params = { "Project Name",
            "Permission type: OWNER, DOMAIN, SUPPORT", "Permission: NONE, READ, WRITE" })
    SET_PROJECT_PERMISSION(Boolean.class, AccessLevel.EDITOR, String.class, PermissionType.class, Permission.class),

    @EventParamData(desc = "Set a Project to be available to a sub Domain of the Domain the Project is currently in. A Subdomain can only access this Project if this property matches the name of the Subdomain exactly.", params = {
            "Project Name", "Subdomain name" })
    SET_PROJECT_SUB_DOMAIN(Boolean.class, AccessLevel.EDITOR, String.class, String.class),

    @EventParamData(desc = "Set whether a specified Project is a template, marking it as a Project to base other Projects on.", params = {
            "Project Name", "Whether the Project is a template" })
    SET_PROJECT_TEMPLATE(Boolean.class, AccessLevel.DOMAIN_ADMIN, String.class, Boolean.class),

    @EventParamData(desc = "Set the description of a specified version of a Project.", params = { "Session ID", "Version of the Project",
            "New version description", })
    SET_PROJECT_VERSION_DESCRIPTION(Boolean.class, AccessLevel.EDITOR, Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set Keep Alive on or off for a running Session, meaning the Session should or should not keep running when no clients are connected.", params = {
            "Session ID", "How long the Session must be kept alive", })
    SET_SESSION_KEEP_ALIVE(Boolean.class, AccessLevel.HOST_SESSION, Integer.class, KeepAlive.class),

    @EventParamData(desc = "Start a new Session", params = { "SessionType: SINGLE, MULTI, EDITOR", "Project Name",
            "Language: NL, EN (optional)", "Preferred Session ID (optional)",
            "Group token (optional)" }, defaults = { "EDITOR", "", "", "", "" }, response = "Session ID")
    @EventIDField(nullable = { 2, 3, 4 })
    START(Integer.class, AccessLevel.HOST_SESSION, Network.SessionType.class, String.class, TLanguage.class, Integer.class, String.class),

    @Deprecated
    @EventParamData(desc = "Use SET_PROJECT_TRASHED", params = { "Project Name", "Trashed" })
    TRASH_PROJECT(Boolean.class, AccessLevel.EDITOR, String.class, Boolean.class),

    @EventParamData(desc = "Mark project as trashed, to be deleted later.", params = { "Project Name", "Trashed" })
    SET_PROJECT_TRASHED(Boolean.class, AccessLevel.EDITOR, String.class, Boolean.class),

    @EventParamData(desc = "Mark project as archived.", params = { "Project Name", "Archived" })
    SET_PROJECT_ARCHIVED(Boolean.class, AccessLevel.EDITOR, String.class, Boolean.class),

    @EventParamData(desc = "Validate that a given string would be a valid Project file name. Returns an empty string if the Project name is valid and not in use. Otherwise it returns the reason why it is not valid.", params = {
            "Project Name" })
    VALIDATE_PROJECT_NAME(String.class, AccessLevel.EDITOR, String.class);

    public static final String NAME_USED_ERROR = "Project name is already used, please select another one.";

    private final Class<?> responseClass;

    private final AccessLevel level;

    private final List<Class<?>> classes;

    private IOServiceEventType(AccessLevel level, Class<?>... c) {
        this(null, level, c);
    }

    private IOServiceEventType(Class<?> responseClass, AccessLevel level, Class<?>... classes) {

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

        switch (this) {
            case GET_INTRO_MESSAGE:
            case GET_DOMAIN_MESSAGE:
            case GET_SESSIONS:
            case GET_DOMAIN_SESSIONS:
            case GET_STARTABLE_PROJECTS:
            case GET_DOMAIN_STARTABLE_PROJECTS:
            case GET_STARTABLE_TEMPLATES:
            case GET_DOMAIN_STARTABLE_TEMPLATES:
            case GET_PROJECT_SAVED_SESSIONS:
            case GET_DOMAIN_PROJECTS:
            case GET_VISIBLE_DOMAIN_PROJECTS:
                return true;
            default:
                return false;
        }
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
        return this.responseClass;
    }

    @Override
    public Integer getTimeoutOverride() {

        // take a long time for certain Projects
        if (this == START || this == LOAD_SAVED_SESSION) {
            return (int) (10 * Moment.MINUTE);
        }
        return null;
    }

    @Override
    public boolean isRoutingServerOnly() {
        return true;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
