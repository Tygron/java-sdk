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
package nl.tytech.data.editor.event;

import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.GEO_LINKS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.data.editor.serializable.GeoLinkType;
import nl.tytech.data.editor.serializable.GeometryMode;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(GEO_LINKS)
public enum EditorGeoLinkEventType implements EventTypeEnum {

    @EventParamData(desc = "Add a new CustomGeoLink with a name and a Geo Link Type", params = { "Geo Link Types",
            "Names" }, response = "CustomGeoLinks")
    @EventIDField(sameLength = true)
    ADD(GeoLinkType[].class, String[].class),

    @EventIDField(links = { GEO_LINKS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { GEO_LINKS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Map original Attribute name to a new name in the created feature", params = { "Geo Links (custom)",
            "Original Attribute Names", "New Attribute Names" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    SET_MAPPING(Integer[].class, String[].class, String[].class),

    @EventParamData(desc = "Remove mapping of original Attribute name to a new name in the created feature", params = {
            "Geo Links (custom)", "Attribute Names" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    REMOVE_MAPPING(Integer[].class, String[].class),

    @EventParamData(desc = "Match incoming features against this Attribute name and value", params = { "Geo Links (custom)",
            "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { GEO_LINKS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_MATCHING(Integer[].class, String[].class, String[][].class, Integer.class),

    @EventParamData(desc = "Remove matching of incoming features against Attributes", params = { "Geo Links (custom)", "Attribute Names" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    REMOVE_MATCHING(Integer[].class, String[].class),

    @EventParamData(desc = "Inject additional Attributes into the created feature", params = { "Geo Links (custom)", "Attribute Names",
            "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { GEO_LINKS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ADDITIONAL(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Remove additional Attribute injection from the created feature", params = { "Geo Links (custom)",
            "Attribute Names" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    REMOVE_ADDITIONAL(Integer[].class, String[].class),

    @EventParamData(desc = "Set the name of a CustomGeoLink", params = { "Geo Links (custom)", "Names" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the priority of a CustomGeoLink", params = { "Geo Links (custom)", "Priority" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    SET_PRIORITY(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the Function of a CustomGeoLink", params = { "Geo Links (custom)", "Functions" })
    @EventIDField(sameLength = true, links = { GEO_LINKS, FUNCTIONS }, params = { 0, 1 })
    SET_FUNCTION(Integer[].class, Integer[].class),

    @EventParamData(desc = "Remove the Function of a CustomGeoLink", params = { "Geo Links (custom)" })
    @EventIDField(links = { GEO_LINKS }, params = { 0 })
    REMOVE_FUNCTION(Integer[].class),

    @EventParamData(desc = "Set the distance used by a CustomGeoLink to buffer Point Geometries", params = { "Geo Links (custom)",
            "Buffer Distance" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    SET_POINT_BUFFER(Integer[].class, double[].class),

    @EventParamData(desc = "Set the distance used by a CustomGeoLink to buffer Line Geometries", params = { "Geo Links (custom)",
            "Buffer Distance" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    SET_LINE_BUFFER(Integer[].class, double[].class),

    @EventParamData(desc = "Set the Geometry Mode used by a CustomGeoLink to interpret Feature Geometries", params = { "Geo Links (custom)",
            "Geometry Modes" })
    @EventIDField(sameLength = true, links = { GEO_LINKS }, params = { 0 })
    SET_GEOMETRY_MODE(Integer[].class, GeometryMode[].class),;

    private final List<Class<?>> classes;

    private EditorGeoLinkEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {

        return switch (this) {

            case ADD, DUPLICATE -> Integer[].class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
