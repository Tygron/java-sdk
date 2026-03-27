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

    @EventParamData(desc = "Add polygons for Building Section. In case the building is empty, optionally set ownership to largest Plot Owner of provided MultiPolygon", params = {
            "Building ID", "Section ID", "MultiPolygon", "Use largest Owner" }, defaults = { "", "", "", "false" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    ADD_POLYGONS(Integer[].class, Integer[].class, MultiPolygon[].class, Boolean.class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_ROAD(Integer.class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    ADD_SECTION(Integer[].class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_STANDARD(Integer.class),

    @EventIDField(sameLength = true, links = { STAKEHOLDERS, FUNCTIONS }, params = { 0, 1 })
    ADD(Integer[].class, Integer[].class, String[].class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_UNDERGROUND(Integer.class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Duplicate a Building Section.", params = { "Building", "Section" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    DUPLICATE_SECTION(Integer[].class, Integer[].class),

    @Deprecated
    @EventParamData(desc = "Use DUPLICATE_SECTION instead for identical result.", params = { "Building", "Section" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    DUPLICATE_SECTIONS(Integer[].class, Integer[].class),

    @EventParamData(desc = "Calculation a rotation angle for buildings having the identifying attribute polygons, optionally only for specified buildings.", params = {
            "Identifying attribute", "Rotation attribute", "Specific buildingIDs only (optional)" }, defaults = { "", "", "" })
    @EventIDField(links = { BUILDINGS }, params = { 2 }, nullable = { 2 })
    GENERATE_ROTATION_ANGLES(String.class, String.class, Integer[].class),

    @EventParamData(desc = "Generate Water Connection.", params = { "Function", "Area", "Min Length", "Max Length", "Width",
            "Large Road Area", "Straight Distance", "Max Height Difference", "Ignore Upstream",
            "Include Water Bodies" }, response = "Amount of generated Objects")
    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    GENERATE_WATER_CONNECTION(Integer.class, MultiPolygon.class, Double.class, Double.class, Double.class, Double.class, Double.class,
            Double.class, Double.class, Boolean.class),

    @EventParamData(desc = "Import Building Collection", params = { "Collection of Building geometries", "Building Names",
            "Attribute Names", "Attribute Values", "Functions", "Owners", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, defaults = { "", "", "", "", "", "", "" + Setting.DEFAULT_PL_BUFFER,
                    "" }, exampleAmount = { 3, 3, 2, 6, 3, 3 })
    @EventIDField(links = { FUNCTIONS, SOURCES }, params = { 4, 7 }, nullable = { 6, 7 })
    IMPORT(GeometryCollection.class, String[].class, String[].class, double[][].class, Integer[].class, Integer[].class, Double.class,
            Integer.class),

    @EventIDField(links = { FUNCTIONS, BUILDINGS }, params = { 0, 3 })
    MULTI_SELECT(Integer.class, Integer.class, Boolean.class, Integer[].class),

    @EventParamData(desc = "Executes a single TQL query. Note: you can also use the session/query endpoint to execute multiple statements and get additional information on API usage.", params = {
            "TQL Query Statement", "Value for update queries (optional)" })
    @EventIDField(nullable = { 1 })
    QUERY(String.class, double[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Delete polygons for Building Section", params = { "Building ID", "Section ID", "MultiPolygon" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE_POLYGONS(Integer[].class, Integer[].class, MultiPolygon.class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    REMOVE_SECTIONS(Integer[].class, Integer[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    RESET_CATEGORY_VALUES(Integer.class, Category.class, CategoryValue[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    RESET_FUNCTION_VALUES(Integer.class, FunctionValue[].class),

    @EventParamData(desc = "Reset height of the slanting roof of a building section", params = { "Building ID", "Section ID" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    RESET_SLANTING_ROOF_HEIGHT(Integer.class, Integer.class),

    @EventParamData(params = { "Buildings", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { BUILDINGS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Buildings", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { BUILDINGS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Buildings", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { BUILDINGS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    SET_CATEGORY_VALUE(Integer.class, Category.class, CategoryValue.class, double[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 }, nullable = { 2 })
    SET_DECALS(Integer.class, FaceType.class, float[].class),

    @EventParamData(desc = "Change amount of floors", params = { "Building ID", "Section ID", "Number of floors", })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    SET_FLOORS(Integer[].class, Integer[].class, Integer[].class),

    @EventIDField(sameLength = true, links = { BUILDINGS, FUNCTIONS }, params = { 0, 1 })
    SET_FUNCTION(Integer[].class, Integer[].class),

    @EventIDField(links = { BUILDINGS }, params = { 0 })
    SET_FUNCTION_VALUE(Integer.class, FunctionValue.class, double[].class),

    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { BUILDINGS, STAKEHOLDERS }, params = { 0, 1 })
    SET_OWNER(Integer[].class, Integer[].class),

    @EventParamData(desc = "Add a custom building geometry", params = { "Building ID", "Custom Geometry ID",
            "Primary building for geometry", "Source (optional)" })
    @EventIDField(sameLength = true, links = { BUILDINGS, CUSTOM_GEOMETRIES, SOURCES }, params = { 0, 1, 3 }, nullable = { 3 })
    ADD_GEOMETRY(Integer[].class, Integer[].class, boolean[].class, Integer.class),

    @EventParamData(desc = "Remove all custom building geometries", params = { "Building ID" })
    @EventIDField(links = { BUILDINGS }, params = { 0, })
    REMOVE_GEOMETRIES(Integer[].class),

    @EventParamData(desc = "Change height of the slanting roof of a building section", params = { "Building ID", "Section ID",
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

        if (this == ADD_ROAD || this == ADD_STANDARD || this == ADD_SECTION || this == ADD_UNDERGROUND
                || this == GENERATE_WATER_CONNECTION) {
            return Integer.class;
        }
        if (this == QUERY) {
            return Object.class;
        }
        if (this == ADD || this == DUPLICATE || this == IMPORT) {
            return Integer[].class;
        }
        if (this == REMOVE || this == SET_NAME || this == SET_ATTRIBUTE) {
            return Boolean.class;
        }
        return null;
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
