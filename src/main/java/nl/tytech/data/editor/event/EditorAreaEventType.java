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

import static nl.tytech.core.net.serializable.MapLink.AREAS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(AREAS)
public enum EditorAreaEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add one or more Areas", params = { "Amount (optional)" }, response = "Area IDs", defaults = { "1" })
    @EventIDField(nullable = { 0 })
    ADD(Integer.class),

    @EventParamData(desc = "Add a group Attribute for Areas", params = { "Attribute name to group Areas by" })
    ADD_GROUP(String.class),

    @EventParamData(params = { "Areas", "Area MultiPolygons" })
    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    ADD_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    ADD_RELATION(Integer.class, Relation.class),

    @EventParamData(desc = "Add an Area with the provided Attribute and value", params = { "Attribute",
            "Attribute value" }, response = "Area ID")
    ADD_WITH_ATTRIBUTE(String.class, Double.class),

    @EventIDField(links = { AREAS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Generate new Areas with a specified Border Attribute and a particular width, at borders of nearby Areas, identified by a Filter Attribute", params = {
            "Filter Attribute", "Border Attribute", "Border Width" })
    GENERATE_BORDER_AREAS(String.class, String.class, Double.class),

    @EventParamData(desc = "Generate Water Inundation Area(s) with the Inundation Level Attribute set to the provided value", params = {
            "Inundation Level (m)" })
    GENERATE_INUNDATION_AREAS(Double.class),

    @EventParamData(desc = "Generate Sewer Areas for Neighborhoods, with a Sewer Storage depending on the Neighborhood's degree of Urbanization. "
            + "A Sewer Overflow is generated for an overlapping Water Level Area identified by the provided Water Level Attribute", params = {
                    "Degree of Urbanization", "Sewer Storage (M) for neighborhood older than 1965",
                    "Sewer Storage (M) for neighborhood younger than 1965", "Pump speed (m/H)",
                    "Water Level Attribute" }, response = "Number of generated Sewer Areas")
    GENERATE_SEWER_AREAS(Integer.class, Double.class, Double.class, Double.class, String.class),

    @EventParamData(desc = "Generate Water Level Area(s) with the Water Level Attribute set to the provided value", params = {
            "Water Level (m)" })
    GENERATE_WATER_AREAS(Double.class),

    @EventParamData(desc = "Import Area Collection", params = { "Collection of Area geometries", "Area Names", "Attribute Names",
            "Numeric Attribute Values", "Polygon Buffer for points and lines (optional)",
            "Source (optional)" }, defaults = { "", "", "", "", "" + Setting.DEFAULT_PL_BUFFER, "" }, exampleAmount = { 3, 3, 2, 6 })
    @EventIDField(links = { SOURCES }, params = { 5 }, nullable = { 4, 5 })
    IMPORT(GeometryCollection.class, String[].class, String[].class, double[][].class, Double.class, Integer.class),

    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove all Attributes of an Area", params = { "Areas" })
    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE_ALL_ATTRIBUTES(Integer[].class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    REMOVE_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @Deprecated
    @EventParamData(desc = "Use REMOVE_GROUP for the same result", params = { "Group name" })
    REMOVE_FILTER(String[].class),

    @EventParamData(desc = "Remove the provided group Attributes", params = { "Group Attributes" })
    REMOVE_GROUP(String[].class),

    @EventParamData(desc = "Remove Relations from an Area", params = { "Area", "Relations" })
    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE_RELATIONS(Integer.class, Relation[].class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(params = { "Areas", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { AREAS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Areas", "Attribute Name", "Additional Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { AREAS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Areas", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { AREAS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(params = { "Areas", "Area Colors (in ARGB single value format)" })
    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_COLOR(Integer[].class, TColor[].class),

    @EventParamData(params = { "Areas", "Area Names" })
    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(params = { "Areas", "Area MultiPolygons" })
    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventParamData(desc = "Set an Area Relation to an Item ID", params = { "Area", "Relation", "Item ID" })
    @EventIDField(links = { AREAS }, params = { 0 })
    SET_RELATION(Integer.class, Relation.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorAreaEventType(Class<?>... classes) {
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

            case ADD, DUPLICATE, IMPORT -> Integer[].class;

            case ADD_WITH_ATTRIBUTE, GENERATE_SEWER_AREAS -> Integer.class;

            case REMOVE, SET_NAME, SET_ATTRIBUTE, SET_POLYGONS -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        // add group does not change the map
        return this != ADD_GROUP ? AREAS : null;
    }
}
