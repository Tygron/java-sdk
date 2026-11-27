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

import static nl.tytech.core.net.serializable.MapLink.BUILDINGS;
import static nl.tytech.core.net.serializable.MapLink.CUSTOM_GEOMETRIES;
import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
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
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FaceType;
import nl.tytech.data.engine.serializable.FunctionValue;

/**
 * Events related to editing buildings.
 * @author Maxim Knepfle
 *
 */
@Linked(BUILDINGS)
public enum EditorBuildingEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add polygons for Building Section. If the building is empty, optionally set ownership to the largest Plot Owner of the provided MultiPolygon", params = {
            "Buildings", "Section ID", "MultiPolygon", "Use largest Owner" }, defaults = { "", "", "", "false" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    ADD_POLYGONS(Integer[].class, Integer[].class, MultiPolygon[].class, Boolean.class),

    @EventParamData(desc = "Add a new Building, owned by a Stakeholder, with a default road Function", params = {
            "Stakeholder" }, response = "Building ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_ROAD(Integer.class),

    @EventParamData(desc = "Add a new Section to a Building", params = { "Buildings" }, response = "Section ID")
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    ADD_SECTION(Integer[].class),

    @EventParamData(desc = "Add a new Building, owned by a Stakeholder, with a default housing Function", params = {
            "Stakeholder" }, response = "Building ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_STANDARD(Integer.class),

    @EventParamData(desc = "Add a new Building, owned by a Stakeholder, with a provided Function and name", params = { "Stakeholders",
            "Functions", "Names" }, response = "Building IDs")
    @EventIDField(sameLength = true, links = { STAKEHOLDERS, FUNCTIONS }, params = { 0, 1 })
    ADD(Integer[].class, Integer[].class, String[].class),

    @EventParamData(desc = "Add a new Building, owned by a Stakeholder, with a default underground Function", params = {
            "Stakeholder" }, response = "Building ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_UNDERGROUND(Integer.class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Duplicate a Building Section", params = { "Buildings", "Section IDs" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    DUPLICATE_SECTION(Integer[].class, Integer[].class),

    @Deprecated
    @EventParamData(desc = "Use DUPLICATE_SECTION instead for identical result", params = { "Buildings", "Section IDs" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    DUPLICATE_SECTIONS(Integer[].class, Integer[].class),

    @EventParamData(desc = "Calculate a rotation angle for Buildings with polygons for the identifying Attribute, optionally only for specified Buildings", params = {
            "Identifying Attribute", "Rotation Attribute", "Buildings (optional)" }, defaults = { "", "", "" })
    @EventIDField(links = { BUILDINGS }, params = { 2 }, nullable = { 2 })
    GENERATE_ROTATION_ANGLES(String.class, String.class, Integer[].class),

    @EventParamData(desc = "Generate Water Connections, with a Function, within a provided MultiPolygon. Connections are only taken into account when within the provided parameter bounds", params = {
            "Function", "MultiPolygon", "Min Length", "Max Length", "Width", "Large Road Area", "Straight Distance",
            "Max Height Difference", "Ignore Upstream", "Include Water Bodies" }, response = "Number of generated objects")
    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    GENERATE_WATER_CONNECTION(Integer.class, MultiPolygon.class, Double.class, Double.class, Double.class, Double.class, Double.class,
            Double.class, Double.class, Boolean.class),

    @EventParamData(desc = "Import Building Collection", params = { "Collection of Building geometries", "Building Names",
            "Attribute Names", "Numeric Attribute Values", "Functions", "Owners", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, defaults = { "", "", "", "", "", "", "" + Setting.DEFAULT_PL_BUFFER,
                    "" }, exampleAmount = { 3, 3, 2, 6, 3, 3 })
    @EventIDField(links = { FUNCTIONS, SOURCES }, params = { 4, 7 }, nullable = { 6, 7 })
    IMPORT(GeometryCollection.class, String[].class, String[].class, double[][].class, Integer[].class, Integer[].class, Double.class,
            Integer.class),

    @EventParamData(desc = "Change the Function of multiple Buildings. Optionally adjust the floors (>0). Optionally merge the Buildings into one", params = {
            "Function", "Floors", "Merge", "Buildings" })
    @EventIDField(links = { FUNCTIONS, BUILDINGS }, params = { 0, 3 })
    MULTI_SELECT(Integer.class, Integer.class, Boolean.class, Integer[].class),

    @EventParamData(desc = "Execute a single TQL query. Use the Session/query endpoint for multiple statements or detailed API usage information", params = {
            "TQL Query Statement", "Value for update queries (optional)" }, response = "Query result")
    @EventIDField(nullable = { 1 })
    QUERY(String.class, double[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove polygons from a Building Section", params = { "Buildings", "Section ID", "MultiPolygon" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE_POLYGONS(Integer[].class, Integer[].class, MultiPolygon.class),

    @EventParamData(desc = "Remove a section from a Building", params = { "Buildings", "Section IDs" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE_SECTIONS(Integer[].class, Integer[].class),

    @EventParamData(desc = "Reset values of Category Values of a Building for a Category", params = { "Building", "Category",
            "Category Values" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    RESET_CATEGORY_VALUES(Integer.class, Category.class, CategoryValue[].class),

    @EventParamData(desc = "Reset the values of Function Values for a Building", params = { "Building", "Function Values" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    RESET_FUNCTION_VALUES(Integer.class, FunctionValue[].class),

    @EventParamData(desc = "Reset height of the slanting roof of a Building Section", params = { "Building", "Section ID" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    RESET_SLANTING_ROOF_HEIGHT(Integer.class, Integer.class),

    @EventParamData(params = { "Buildings", "Attribute Name", "Numeric Attribute Values", "Source (optional)" }, response = "Updated")
    @EventIDField(links = { BUILDINGS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Buildings", "Attribute Name", "Numeric Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { BUILDINGS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Buildings", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { BUILDINGS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set the values of a Category Value for a Building and a Category", params = { "Building", "Category",
            "Category Value", "Values" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    SET_CATEGORY_VALUE(Integer.class, Category.class, CategoryValue.class, double[].class),

    @EventParamData(desc = "Set the decal values (door, window etc) for a Face Type of a Building", params = { "Building", "Face Type",
            "Decal values", })
    @EventIDField(links = { BUILDINGS }, params = { 0 }, nullable = { 2 })
    SET_DECALS(Integer.class, FaceType.class, float[].class),

    @EventParamData(desc = "Set the number of floors of a Building's Section", params = { "Buildings", "Section IDs",
            "Array of number of floors", })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    SET_FLOORS(Integer[].class, Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the Function of a Building", params = { "Buildings", "Functions" })
    @EventIDField(sameLength = true, links = { BUILDINGS, FUNCTIONS }, params = { 0, 1 })
    SET_FUNCTION(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the values of a Function Value for a Building", params = { "Building", "Function Value", "Values" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    SET_FUNCTION_VALUE(Integer.class, FunctionValue.class, double[].class),

    @EventParamData(desc = "Set the name of a Building", params = { "Buildings", "Names" }, response = "Updated")
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the owner of a Building", params = { "Buildings", "Stakeholders" })
    @EventIDField(sameLength = true, links = { BUILDINGS, STAKEHOLDERS }, params = { 0, 1 })
    SET_OWNER(Integer[].class, Integer[].class),

    @EventParamData(desc = "Add a Custom Geometry to a Building", params = { "Buildings", "Custom Geometries",
            "Primary Building for the Custom Geometry, per Building", "Source" })
    @EventIDField(sameLength = true, links = { BUILDINGS, CUSTOM_GEOMETRIES, SOURCES }, params = { 0, 1, 3 }, nullable = { 3 })
    ADD_GEOMETRY(Integer[].class, Integer[].class, boolean[].class, Integer.class),

    @EventParamData(desc = "Remove all Custom Geometries from a Building", params = { "Buildings" })
    @EventIDField(links = { BUILDINGS }, params = { 0, })
    REMOVE_GEOMETRIES(Integer[].class),

    @EventParamData(desc = "Change height of the slanting roof of a section of a Building", params = { "Building", "Section ID",
            "Slanting roof height", })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    SET_SLANTING_ROOF_HEIGHT(Integer.class, Integer.class, Double.class);

    private final List<Class<?>> classes;

    private EditorBuildingEventType(Class<?>... classes) {
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

            case ADD_ROAD, ADD_STANDARD, ADD_SECTION, ADD_UNDERGROUND, GENERATE_WATER_CONNECTION -> Integer.class;

            case QUERY -> Object.class;

            case ADD, DUPLICATE, IMPORT -> Integer[].class;

            case REMOVE, SET_NAME, SET_ATTRIBUTE -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {

        return switch (this) {
            case QUERY -> null; // has own update indicators call
            case SET_DECALS -> null; // ignore visuals only
            default -> BUILDINGS;
        };
    }
}
