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
import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.GEO_TIFFS;
import static nl.tytech.core.net.serializable.MapLink.LEVEES;
import static nl.tytech.core.net.serializable.MapLink.MEASURES;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import static nl.tytech.core.net.serializable.MapLink.TERRAIN_TYPES;
import static nl.tytech.core.net.serializable.MapLink.UPGRADE_TYPES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.engine.item.Measure.ActionType;
import nl.tytech.data.engine.item.Measure.CostType;
import nl.tytech.data.engine.serializable.MeasureEditType;
import nl.tytech.naming.GeoNC;

/**
 * @author Frank Baars
 *
 */
@Linked(MEASURES)
public enum EditorMeasureEventType implements EventTypeEnum {

    @EventParamData(desc = "Add Measure for Stakeholder", params = { "Stakeholder" }, response = "Measure ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD(Integer.class),

    @EventParamData(desc = "Add a new Building to specified Measures", params = { "Measures" }, response = "Building IDs")
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_BUILDING(Integer[].class),

    @EventParamData(desc = "Add a new event to a Measure for a specific Action Type", params = { "Measures",
            "Server Side (false = Client Side)", "Action Type" }, response = "Coded Event ID")
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_EVENT(Integer[].class, Boolean[].class, ActionType[].class),

    @EventParamData(desc = "Add a new " + GeoNC.GEOTIFF + " Spatial to a Measure", params = { "Measure" }, response = GeoNC.GEOTIFF
            + " Spatial ID")
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_GEOTIFF(Integer.class),

    @EventParamData(desc = "Add polygons to pre-existing polygons of a " + GeoNC.GEOTIFF + " Spatial of a Measure", params = { "Measure",
            GeoNC.GEOTIFF + " Spatial ID", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_GEOTIFF_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Add a new Grid Spatial to a Measure", params = { "Measure" }, response = "Grid Spatial ID")
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_GRID(Integer.class),

    @EventParamData(desc = "Add polygons to pre-existing polygons of a Grid Spatial of a Measure", params = { "Measure", "Grid Spatial ID",
            "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_GRID_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Add a new Levee Spatial of a specified Levee to a Measure", params = { "Measure",
            "Levee" }, response = "Levee Spatial ID")
    @EventIDField(links = { MEASURES, LEVEES }, params = { 0, 1 })
    ADD_LEVEE(Integer.class, Integer.class),

    @EventParamData(desc = "Add polygons to pre-existing outer (or inner) polygons of a Levee Spatial of a Measure", params = { "Measure",
            "Levee Spatial ID", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_LEVEE_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Add a new Terrain Spatial to a Measure", params = { "Measure" }, response = "Terrain Spatial ID")
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_TERRAIN(Integer.class),

    @EventParamData(desc = "Add polygons to pre-existing outer (or inner) polygons of a Terrain Spatial of a Measure", params = { "Measure",
            "Terrain Spatial ID", "Is Outer (false = inner)", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_TERRAIN_POLYGONS(Integer.class, Integer.class, Boolean.class, MultiPolygon.class),

    @EventParamData(desc = "Add a new Upgrade Spatial to a Measure", params = { "Measure" }, response = "Upgrade Spatial ID")
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_UPGRADE(Integer.class),

    @EventParamData(desc = "Add polygons to pre-existing polygons of an Upgrade Spatial of a Measure", params = { "Measure",
            "Upgrade Spatial ID", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    ADD_UPGRADE_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Duplicate specified Measures", params = { "Measures", })
    @EventIDField(links = { MEASURES }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Duplicate specified Measure Buildings", params = { "Buildings", })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    DUPLICATE_BUILDING(Integer[].class),

    @EventParamData(desc = "Import Measure Building Collection", params = { "Collection of Building geometries", "Building Names",
            "Attribute Names", "Numeric Attribute Values", "Functions", "Stakeholders (owners) (optional)",
            "Measure (pre-existing) (optional)", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, response = "Building IDs")
    @EventIDField(links = { FUNCTIONS, STAKEHOLDERS, MEASURES, SOURCES }, params = { 4, 5, 6, 8 }, nullable = { 5, 6, 7, 8 })
    IMPORT_BUILDINGS(GeometryCollection.class, String[].class, String[].class, double[][].class, Integer[].class, Integer[].class,
            Integer.class, Double.class, Integer.class),

    @Deprecated
    @EventParamData(desc = "Use IMPORT_BUILDINGS, which provides the option to specify an pre-existing Measure", params = {
            "Collection of Building Geometries", "Building Names", "Attribute Names", "Numeric Attribute Values", "Functions",
            "Stakeholders (owners)", "Buffer for Points and Lines to make Polygons (optional)", "Source (optional)" })
    @EventIDField(links = { FUNCTIONS, STAKEHOLDERS, SOURCES }, params = { 4, 5, 7 }, nullable = { 6, 7 })
    IMPORT_BUILDING_MEASURE(GeometryCollection.class, String[].class, String[].class, double[][].class, Integer[].class, Integer[].class,
            Double.class, Integer.class),

    @EventParamData(desc = "Import Measure Grid Collection", params = { "Collection of Grid Geometries where the Grid will be applied",
            "Terrain Types", "Grid Overlays", "Measure (pre-existing) (optional)",
            "Buffer for Points and Lines to make Polygons (optional)", "Source (optional)" }, response = "Measure ID")
    @EventIDField(links = { TERRAIN_TYPES, OVERLAYS, MEASURES, SOURCES }, params = { 1, 2, 3, 5 }, nullable = { 3, 4, 5 })
    IMPORT_GRIDS(GeometryCollection.class, Integer[].class, Integer[][].class, Integer.class, Double.class, Integer.class),

    @EventParamData(desc = "Import Measure GeoTIFF Collection", params = {
            "Collection of GeoTIFF Geometries where the GeoTIFF will be applied", "Terrain Types", "GeoTIFFs",
            "Measure (pre-existing) (optional)", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, response = "Measure ID")
    @EventIDField(links = { TERRAIN_TYPES, GEO_TIFFS, MEASURES, SOURCES }, params = { 1, 2, 3, 5 }, nullable = { 3, 4, 5 })
    IMPORT_GEOTIFFS(GeometryCollection.class, Integer[].class, Integer[][].class, Integer.class, Double.class, Integer.class),

    @EventParamData(desc = "Import Measure Levee Collection", params = { "Collection of Levee Geometries", "Levees", "Override Angles",
            "Override Heights", "Override relative height", "Measure (pre-existing) (optional)",
            "Buffer for Points and Lines to make Polygons (optional)", "Source (optional)" }, response = "Measure ID")
    @EventIDField(links = { LEVEES, MEASURES, SOURCES }, params = { 1, 5, 7 }, nullable = { 5, 6, 7 })
    IMPORT_LEVEES(GeometryCollection.class, Integer[].class, Double[].class, Double[].class, Boolean[].class, Integer.class, Double.class,
            Integer.class),

    @EventParamData(desc = "Import Measure Terrain Collection", params = { "Collection of Terrain Geometries", "Measure Edit Type",
            "Is Inner", "Terrain Types", "Override Inner Heights", "Height Relative", "Measure (pre-existing) (optional)",
            "Buffer for Points and Lines to make Polygons (optional)", "Source (optional)" }, response = "Measure ID")
    @EventIDField(links = { TERRAIN_TYPES, MEASURES, SOURCES }, params = { 3, 6, 8 }, nullable = { 6, 7, 8 })
    IMPORT_TERRAINS(GeometryCollection.class, MeasureEditType.class, Boolean[].class, Integer[].class, Double[].class, Boolean[].class,
            Integer.class, Double.class, Integer.class),

    @EventParamData(desc = "Import Measure Upgrade Collection", params = { "Collection of Upgrade Geometries", "Upgrade Types",
            "Measure (pre-existing) (optional)", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, response = "Measure ID")
    @EventIDField(links = { UPGRADE_TYPES, MEASURES, SOURCES }, params = { 1, 2, 4 }, nullable = { 2, 3, 4 })
    IMPORT_UPGRADES(GeometryCollection.class, Integer[].class, Integer.class, Double.class, Integer.class),

    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove a specific Server or Client side Coded Event from a Measure for a specific Action Type", params = {
            "Measures", "Server Side (false = Client Side)", "Action Types", "Coded Event IDs" })
    @EventIDField(sameLength = true, links = { MEASURES }, params = { 0 })
    REMOVE_EVENT(Integer[].class, Boolean[].class, ActionType[].class, Integer[].class),

    @EventParamData(desc = "Remove specified " + GeoNC.GEOTIFF + " Spatial from a Measure", params = { "Measure",
            GeoNC.GEOTIFF + " Spatial IDs" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_GEOTIFF(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove polygons from pre-existing polygons of a " + GeoNC.GEOTIFF + " Spatial of a Measure", params = {
            "Measure", GeoNC.GEOTIFF + " Spatial ID", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_GEOTIFF_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Remove the Terrain Type of a " + GeoNC.GEOTIFF + " Spatial of a Measure", params = { "Measure",
            GeoNC.GEOTIFF + " Spatial ID" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_GEOTIFF_TERRAIN_TYPE(Integer.class, Integer.class),

    @EventParamData(desc = "Remove specified Grid Spatial from a Measure", params = { "Measure", "Grid Spatial IDs" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_GRID(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove polygons from pre-existing polygons of a Grid Spatial of a Measure", params = { "Measure",
            "Grid Spatial ID", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_GRID_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Remove the Terrain Type of a Grid Spatial of a Measure", params = { "Measure", "Grid Spatial ID" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_GRID_TERRAIN_TYPE(Integer.class, Integer.class),

    @EventParamData(desc = "Remove specified Levee Spatials from a Measure", params = { "Measure", "Levee Spatial IDs" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_LEVEE(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove polygons from pre-existing polygons of a Levee Spatial of a Measure", params = { "Measure",
            "Levee Spatial ID", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_LEVEE_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Remove specified Terrain Spatials from a Measure", params = { "Measure", "Terrain Spatial IDs" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_TERRAIN(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove polygons from pre-existing outer (or inner) polygons of a Terrain Spatial of a Measure", params = {
            "Measure", "Terrain Spatial ID", "Is Outer (false = inner)", "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_TERRAIN_POLYGONS(Integer.class, Integer.class, Boolean.class, MultiPolygon.class),

    @EventParamData(desc = "Remove specified Upgrade Spatials from a Measure", params = { "Measure", "Upgrade Spatial IDs" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_UPGRADE(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove polygons from an Upgrade Spatial of a Measure", params = { "Measure", "Upgrade Spatial ID",
            "MultiPolygon" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    REMOVE_UPGRADE_POLYGONS(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Set the cost of a Measure for a specific Cost type (construction, demolition) and whether it is fixed or per cubic meter", params = {
            "Measure", "Cost Type", "Cost in valuata", "Fixed (if false, cost per cubic meter)" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_COST(Integer.class, CostType.class, Double.class, Boolean.class),

    @EventParamData(desc = "Set the description of a Measure", params = { "Measure", "Description" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventParamData(desc = "Change an pre-existing event of a specific Action Type of a Measure", params = { "Measures",
            "Server Side (false = Client Side)", "Action Types", "CodedEvents" })
    @EventIDField(sameLength = true, links = { MEASURES }, params = { 0 })
    SET_EVENT(Integer[].class, Boolean[].class, ActionType[].class, CodedEvent[].class),

    @EventParamData(desc = "Change the " + GeoNC.GEOTIFFS + " applied by a specified Measure", params = { "Measure",
            GeoNC.GEOTIFF + " Spatial ID", GeoNC.GEOTIFF + "s" })
    @EventIDField(links = { MEASURES, GEO_TIFFS }, params = { 0, 2 })
    SET_GEOTIFF(Integer.class, Integer.class, Integer[].class),

    @EventParamData(desc = "Let the polygons be calculated automatically based on the " + GeoNC.GEOTIFFS + " specified in the "
            + GeoNC.GEOTIFF + " spatial of a Measure", params = { "Measure", GeoNC.GEOTIFF + " Spatial ID", "Automatic calculation" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_GEOTIFF_AUTOMATIC_POLYGONS(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the Terrain Type for a " + GeoNC.GEOTIFF + " Spatial of a Measure", params = { "Measure",
            GeoNC.GEOTIFF + " Spatial ID", "Terrain Type" })
    @EventIDField(links = { MEASURES, TERRAIN_TYPES }, params = { 0, 2 })
    SET_GEOTIFF_TERRAIN_TYPE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Change the Grids applied by a specified Measure", params = { "Measure", "Grid Spatial ID", "Grid Overlays" })
    @EventIDField(links = { MEASURES, OVERLAYS }, params = { 0, 2 })
    SET_GRID(Integer.class, Integer.class, Integer[].class),

    @EventParamData(desc = "Automatically calculate polygons based on the Overlay specified in the Grid spatial of a Measure", params = {
            "Measure", "Grid Spatial ID", "Automatic calculation" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_GRID_AUTOMATIC_POLYGONS(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the Terrain Type for a Grid Spatial of a Measure", params = { "Measure", "Grid Spatial ID",
            "Terrain Type" })
    @EventIDField(links = { MEASURES, TERRAIN_TYPES }, params = { 0, 2 })
    SET_GRID_TERRAIN_TYPE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Set the image of a Measure", params = { "Measures", "Image names" })
    @EventIDField(sameLength = true, links = { MEASURES }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventParamData(desc = "Set the angle of a Levee Spatial of a Measure", params = { "Measure", "Levee Spatial ID", "Angle" })
    @EventIDField(links = { MEASURES }, params = { 0 }, nullable = { 2 })
    SET_LEVEE_ANGLE(Integer.class, Integer.class, Double.class),

    @EventParamData(desc = "Set the Levee for a specified Levee Spatial of a Measure", params = { "Measure", "Levee Spatial ID", "Levee" })
    @EventIDField(links = { MEASURES, LEVEES }, params = { 0, 2 })
    SET_LEVEE_TYPE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Set the inner height of a Levee Spatial of a Measure", params = { "Measure", "Levee Spatial ID",
            "Height value" })
    @EventIDField(links = { MEASURES }, params = { 0 }, nullable = { 2 })
    SET_LEVEE_HEIGHT(Integer.class, Integer.class, Double.class),

    @EventParamData(desc = "Set whether the inner height of a Levee Spatial of a Measure is considered relative", params = { "Measure",
            "Levee Spatial ID", "Boolean" })
    @EventIDField(links = { MEASURES }, params = { 0 }, nullable = { 2 })
    SET_LEVEE_HEIGHT_RELATIVE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the name of a Measure", params = { "Measures", "Names" })
    @EventIDField(sameLength = true, links = { MEASURES }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the owner of a Measure", params = { "Measure", "Stakeholder" })
    @EventIDField(links = { MEASURES, STAKEHOLDERS }, params = { 0, 1 })
    SET_OWNER(Integer.class, Integer.class),

    @EventParamData(desc = "Set whether the Measure should be confirmed by other Stakeholders", params = { "Measure",
            "Confirmation Required Boolean" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_REQUIRES_CONFIRMATION(Integer.class, Boolean.class),

    @EventParamData(desc = "Set the Edit Type of a Terrain Spatial of a Measure. This changes the way the terrain height is adjusted: lowered, flattened or raised", params = {
            "Measure", "Terrain Spatial ID", "Edit Type" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_TERRAIN_EDIT_TYPE(Integer.class, Integer.class, MeasureEditType.class),

    @EventParamData(desc = "Set the inner height of a Terrain Spatial of a Measure", params = { "Measure", "Terrain Spatial ID",
            "Height value" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_TERRAIN_HEIGHT(Integer.class, Integer.class, Double.class),

    @EventParamData(desc = "Set whether the inner height of a Terrain Spatial of a Measure is considered relative", params = { "Measure",
            "Terrain Spatial ID", "Boolean" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    SET_TERRAIN_HEIGHT_RELATIVE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the Terrain Type for a Terrain Spatial of a Measure", params = { "Measure", "Terrain Spatial ID",
            "Terrain Type" })
    @EventIDField(links = { MEASURES, TERRAIN_TYPES }, params = { 0, 2 })
    SET_TERRAIN_TYPE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Set the Upgrade Type for an Upgrade Spatial of a Measure", params = { "Measure", "Upgrade Spatial ID",
            "Upgrade Type" })
    @EventIDField(links = { MEASURES, UPGRADE_TYPES }, params = { 0, 2 })
    SET_UPGRADE_TYPE(Integer.class, Integer.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorMeasureEventType(Class<?>... classes) {
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

            case ADD, ADD_GEOTIFF, ADD_LEVEE, ADD_UPGRADE, ADD_TERRAIN -> Integer.class;

            case IMPORT_TERRAINS, IMPORT_UPGRADES, IMPORT_LEVEES, IMPORT_GRIDS, IMPORT_GEOTIFFS -> Integer.class;

            case ADD_BUILDING, IMPORT_BUILDINGS, IMPORT_BUILDING_MEASURE, DUPLICATE, ADD_EVENT, DUPLICATE_BUILDING -> Integer[].class;

            case REMOVE -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
