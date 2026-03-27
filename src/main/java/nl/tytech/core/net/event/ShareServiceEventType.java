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

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.item.annotations.ClassDescription;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.serializable.Shareable;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.naming.EngineNC;

/**
 * Service events related to geo-data sharing via the GeoShare.
 *
 * @author Maxim Knepfle
 */
@ClassDescription("Events related to the GeoShare.")
public enum ShareServiceEventType implements ServiceEventType {

    @EventParamData(desc = "Add new Sharable for given path, name and type, returns unique path with primary extension. For files larger than 2GB, use the Stream API.", params = {
            Doc.PATH, "Type", "Content Bytes (< 2GB) (optional)" })
    @EventIDField(nullable = { 2 })
    ADD(String.class, AccessLevel.EDITOR, String.class, Shareable.Type.class, byte[].class),

    @EventParamData(desc = "Add new Sharable for given path, name and type, returns unique path with primary extension.", params = {
            Doc.PATH, "Type", "URL with Content (optional)" })
    @EventIDField(nullable = { 2 })
    ADD_URL(String.class, AccessLevel.EDITOR, String.class, Shareable.Type.class, String.class),

    @EventParamData(desc = "Get the all shareables in given path. Only when account is EDITOR+ tokenized shareables are included.", params = {
            Doc.PATH, "Types (optional)" })
    @EventIDField(nullable = { 1 })
    GET_SHAREABLES(Shareable[].class, AccessLevel.NONE, String.class, Shareable.Type[].class),

    @EventParamData(desc = "Move the Sharable to a new path.", params = { "Old " + Doc.PATH, "New " + Doc.PATH })
    MOVE(String.class, AccessLevel.EDITOR, String.class, String.class),

    @EventParamData(desc = "Remove Sharable for given paths.", params = { Doc.PATH })
    REMOVE(Boolean.class, AccessLevel.EDITOR, String[].class),

    @EventParamData(desc = "Set token for given path.", params = { Doc.PATH, "Token" })
    @EventIDField(sameLength = true)
    SET_TOKEN(Boolean.class, AccessLevel.EDITOR, String[].class, String[].class),

    @EventParamData(desc = "Set icon for given path.", params = { Doc.PATH, "Icon Name" })
    @EventIDField(sameLength = true)
    SET_ICON(Boolean.class, AccessLevel.EDITOR, String[].class, String[].class),

    @EventParamData(desc = "Set disclaimer for given path.", params = { Doc.PATH, "Disclaimer text" })
    @EventIDField(sameLength = true)
    SET_DISCLAIMER(Boolean.class, AccessLevel.EDITOR, String[].class, String[].class),

    @EventParamData(desc = "Set description for given path.", params = { Doc.PATH, "Description text" })
    @EventIDField(sameLength = true)
    SET_DESCRIPTION(Boolean.class, AccessLevel.EDITOR, String[].class, String[].class),

    @EventParamData(desc = "Set viewer content array for given path.", params = { Doc.VIEWER_PATH,
            "Matrix of viewer input shareables (e.g. [[\"mydir/features.geojson\", \"mydir/overlay-0.tiff\"]], note: adding a tiff ending with \"-0.tiff\" will automatically add additional timeframes like \"-1.tiff\")" })
    @EventIDField(sameLength = true)
    SET_VIEWER_INPUT(Boolean.class, AccessLevel.EDITOR, String[].class, String[][].class),

    @EventParamData(desc = "Get viewer content array for given path.", params = {
            Doc.VIEWER_PATH }, response = "Array of viewer input shareables.")
    GET_VIEWER_INPUT(String[].class, AccessLevel.EDITOR, String.class),

    @EventParamData(desc = "Set " + EngineNC.CERTIFICATE + " to validate the originality of the shareable in a Project.", params = {
            Doc.PATH, "Certificate author name" })
    @EventIDField(sameLength = true)
    SET_CERTIFICATE_AUTHOR(Boolean.class, AccessLevel.EDITOR, String[].class, String[].class),

    @EventParamData(desc = "Update Sharable content.", params = { Doc.PATH, "Content Bytes" })
    UPDATE(String.class, AccessLevel.EDITOR, String.class, byte[].class),

    ;

    private static class Doc {

        private static final String PATH = "Path (e.g. \"mydir/myname.tiff\", note: viewers and directories end with a slash e.g. \"mydir/viewer/\")";
        private static final String VIEWER_PATH = "Path (note: viewers end with a slash e.g. \"mydir/viewer/\")";
    }

    private final Class<?> responseClass;

    private final AccessLevel level;

    private final List<Class<?>> classes;

    private ShareServiceEventType(AccessLevel level, Class<?>... c) {
        this(null, level, c);
    }

    private ShareServiceEventType(Class<?> responseClass, AccessLevel level, Class<?>... classes) {

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
        return true;
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
