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
import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.GEO_TIFFS;
import static nl.tytech.core.net.serializable.MapLink.NEURAL_NETWORKS;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.WEATHERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.HeatOverlay.HeatModel;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.item.Overlay.OverlayType;
import nl.tytech.data.engine.item.RasterizationOverlay.Rasterization;
import nl.tytech.data.engine.item.WaterOverlay;
import nl.tytech.data.engine.item.WaterOverlay.WaterSystem;
import nl.tytech.data.engine.item.WatershedOverlay;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(OVERLAYS)
public enum EditorOverlayEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new Overlay of the specified type", params = { "Overlay Type" }, response = "Overlay ID")
    ADD(OverlayType.class),

    @Deprecated
    @EventParamData(desc = "Add an entry to the (difference) legend an Overlay", params = { "Overlays", "Difference Legend", "Name",
            "Color", "Value" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 }, nullable = { 2, 3, 4 })
    ADD_LEGEND_ENTRY(Integer[].class, Boolean[].class, String[].class, TColor[].class, Float[].class),

    @EventParamData(desc = "Add an entry to the legend an Overlay", params = { "Overlays", "Names", "Colors", "Values" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 }, nullable = { 2, 3 })
    ADD_ENTRY(Integer[].class, String[].class, TColor[].class, Float[].class),

    @EventParamData(desc = "Add an entry to the difference legend an Overlay", params = { "Overlays", "Names", "Colors", "Values" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 }, nullable = { 2, 3 })
    ADD_DIFFERENCE_ENTRY(Integer[].class, String[].class, TColor[].class, Float[].class),

    @EventParamData(desc = "Add a Function to a Function Overlay", params = { "Function Overlay", "Functions" })
    @EventIDField(links = { OVERLAYS, FUNCTIONS }, params = { 0, 1 })
    ADD_OVERLAY_FUNCTION(Integer.class, Integer[].class),

    @EventParamData(desc = "Add a specified result type as a Result Child Overlay to a Result Parent Overlay", params = {
            "Result Parent Overlay", "Result type" }, response = "Overlay ID")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    ADD_RESULT_CHILD(Integer.class, String.class),

    @EventParamData(desc = "Add an Overlay with the specified type", params = { "Overlay Types" }, response = "Overlay IDs")
    ADD_TYPE(OverlayType[].class),

    @EventIDField(links = { OVERLAYS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @Deprecated
    @EventParamData(desc = "Deprecated functionality, entire map is always updated when UPDATE is called. "
            + "Note that when the \"Auto calculation\" is not active, an editor/update/ event is required to generate the new Overlay results. "
            + "Or for long-running updates use: editor/set_scheduled_update/ to schedule an update", params = {})
    REFRESH_GRID(),

    @EventParamData(desc = "Remove the specified Overlays", params = { "Overlays" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove the specified Attributes of the specified Overlay. The Attributes, including Attributes which are added to an Overlay by default, are removed entirely", params = {
            "Overlays", "Attributes to remove" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @Deprecated
    @EventParamData(desc = "Remove entries from an Overlay's legend", params = { "Overlay",
            "Remove from custom difference legend (false removes the entry from the normal custom legend)", "Entry IDs" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_LEGEND_ENTRY(Integer.class, Boolean.class, Integer[].class),

    @EventParamData(desc = "Remove entries from an Overlay's legend", params = { "Overlay", "Entry IDs" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_ENTRY(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove entries from an Overlay's difference legend", params = { "Overlay", "Entry IDs" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_DIFFERENCE_ENTRY(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove a Function from a Function Highlight Overlay", params = { "Function Overlay", "Functions" })
    @EventIDField(links = { OVERLAYS, FUNCTIONS }, params = { 0, 1 })
    REMOVE_OVERLAY_FUNCTION(Integer.class, Integer[].class),

    @EventParamData(desc = "Remove the Weather from a Water Overlay", params = { "Water Overlay" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_WATER_WEATHER(Integer.class),

    @EventParamData(desc = "Create a deactivated copy of each specified Overlay", params = { "Overlays" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SAVE_GRID(Integer[].class),

    @EventParamData(params = { "Overlays", "Attribute Name", "Numeric Attribute Values", "Source" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Overlays", "Attribute Name", "Numeric Attribute Values appended to existing values", "Source" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Overlays", "Attribute Names", "Numeric Attribute Values", "Source" })
    @EventIDField(sameLength = true, links = { OVERLAYS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set which attribute an Attribute Overlay uses to obtain the color from an Item to paint it with. If absent, the Overlay's color will be used", params = {
            "Attribute Overlay", "Color Attribute" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ATTRIBUTE_OVERLAY_COLOR_ATTRIBUTE(Integer.class, String.class),

    @EventParamData(desc = "Set which type of Item and which attribute an Attribute Overlay should highlight", params = {
            "Attribute Overlay", "MapLink: AREAS, BUILDINGS, NEIGHBORHOODS, NET_LOADS, TERRAINS, ZONES", "Attribute" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ATTRIBUTE_OVERLAY_VALUES(Integer.class, MapLink.class, String.class),

    @EventParamData(desc = "Set the averaging distance for an Average Overlay", params = { "Avg Overlay", "Averaging distance" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_AVG_DISTANCE(Integer.class, Double.class),

    @EventParamData(desc = "Set the type of rasterization used by a Average, Combo or Distance Overlay", params = {
            "Overlay (Average, Combo or Distance)", "Rasterization type",
            "MapLink (will only affect the SINGLE_LAYER rasterization type): BUILDINGS, TERRAINS, AREAS, NEIGHBORHOODS" })
    @EventIDField(links = { OVERLAYS }, params = { 0 }, nullable = { 2 })
    SET_RASTERIZATION(Integer.class, Rasterization.class, MapLink.class),

    @EventParamData(desc = "Set whether an Overlay has a custom legend. If set to false, its legends are reverted to a generated default", params = {
            "Overlay", "Has a custom legend" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_CUSTOM_LEGEND(Integer.class, Boolean.class),

    @Deprecated
    @EventParamData(desc = "Set the color of an entry of an Overlay's legend", params = { "Overlay",
            "Affect the custom difference legend (false means affect the normal custom legend)", "Entry ID", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_LEGEND_ENTRY_COLOR(Integer.class, Boolean.class, Integer.class, TColor.class),

    @EventParamData(desc = "Set the color of an entry of an Overlay's legend", params = { "Overlay", "Entry ID", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ENTRY_COLOR(Integer.class, Integer.class, TColor.class),

    @EventParamData(desc = "Set the color of an entry of an Overlay's difference legend", params = { "Overlay", "Entry ID", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_DIFFERENCE_ENTRY_COLOR(Integer.class, Integer.class, TColor.class),

    @Deprecated
    @EventParamData(desc = "Set the value of an entry of a custom legend of an Overlay", params = { "Overlay",
            "Affect the custom difference legend (false means affect the normal custom legend)", "Entry ID", "Name" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_LEGEND_ENTRY_NAME(Integer.class, Boolean.class, Integer.class, String.class),

    @EventParamData(desc = "Set the name of an entry of an Overlay's legend", params = { "Overlay", "Legend Entry ID", "Name" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ENTRY_NAME(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set the name of an entry of an Overlay's difference legend", params = { "Overlay", "Entry ID", "Name" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_DIFFERENCE_ENTRY_NAME(Integer.class, Integer.class, String.class),

    @Deprecated
    @EventParamData(desc = "Set the value of an entry of a custom legend of an Overlay", params = { "Overlay",
            "Affect the custom difference legend (false means affect the normal custom legend)", "Entry ID", "Value" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_LEGEND_ENTRY_VALUE(Integer.class, Boolean.class, Integer.class, Float.class),

    @EventParamData(desc = "Set the value of an entry of an Overlay's legend", params = { "Overlay", "Entry ID", "Value" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ENTRY_VALUE(Integer.class, Integer.class, Float.class),

    @EventParamData(desc = "Set the value of an entry of an Overlay's difference legend", params = { "Overlay", "Entry ID", "Value" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_DIFFERENCE_ENTRY_VALUE(Integer.class, Integer.class, Float.class),

    @EventParamData(desc = "Set the GeoTIFF displayed by a specified GeoTIFF Overlay", params = { "GeoTIFF Overlay", "GeoTIFFs" })
    @EventIDField(links = { OVERLAYS, GEO_TIFFS }, params = { 0, 1 })
    SET_GEOTIFF(Integer.class, Integer[].class),

    @EventParamData(desc = "Set the Neural Network for an Inference Overlay", params = { "Inference Overlay", "Neural Network" })
    @EventIDField(links = { OVERLAYS, NEURAL_NETWORKS }, params = { 0, 1 })
    SET_NEURAL_NETWORK(Integer.class, Integer.class),

    @EventParamData(desc = "Set a Grid Overlay active or inactive. Only active Overlays are recalculated by the simulation", params = {
            "Grid Overlays", "Active" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_GRID_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Change the image displayed by an Image Overlay", params = { "Image Overlays", "Image Names" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_OVERLAY_IMAGE(Integer[].class, String[][].class),

    @EventParamData(desc = "Change the icon displayed in the navigation window by the Overlay", params = { "Overlays", "Image Names" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventParamData(desc = "Set whether an Overlay is visualized in the Navigation Panel", params = { "Overlays", "Visualized" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_VISIBLE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set a key to an Attribute for a Grid Overlay", params = { "Grid Overlay", "Key", "Attribute" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_KEY_VALUE(Integer.class, String.class, String.class),

    @EventParamData(desc = "Set the name of an Overlay", params = { "Overlays", "Names" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the visualization style for a Network Overlay", params = {
            "Overlay (Network Ownership or Network Overview)", "NetType", "Whether to show the network",
            "Whether only active connections should be visualized (false means both active and inactive connections are visualized)" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_NET_OVERLAY(Integer.class, NetType.class, Boolean.class, Boolean.class),

    @EventParamData(desc = "Add or remove Areas to or from an Area Overlay", params = { "Area Overlay", "Areas",
            "Should be added to the Area Overlay (false removes the areas from the Area Overlay)" })
    @EventIDField(links = { OVERLAYS, AREAS }, params = { 0, 1 })
    SET_OVERLAY_AREA(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Set the color of a Function or Attribute Overlay, used to highlight features", params = {
            "Overlay (Function or Attribute)", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_OVERLAY_PRIMARY_COLOR(Integer.class, TColor.class),

    @EventParamData(desc = "Set formula for a Combo Overlay", params = { "Combo Overlay", "Formula" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_COMBO_FORMULA(Integer.class, String.class),

    @EventParamData(desc = "Set Prequel Overlay for a Grid Overlay and a Prequel Type supported by the Grid Overlay. Optionally a specific Timeframe is used to obtain Grid values, with the last timeframe of the Prequel Overlay as default"
            + "Optionally Grid Values are obtain from a previous Iteration, allowing cyclic dependencies between Grid Overlays", params = {
                    "Grid Overlay", "Prequel Overlay", "Prequel Type Name", "Prequel Timeframe ID (optional)",
                    "Previous Iteration (optional)" })
    @EventIDField(links = { OVERLAYS, OVERLAYS }, params = { 0, 1 }, nullable = { 3, 4 })
    SET_PREQUEL(Integer.class, Integer.class, String.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the color a specified Function or Attribute Overlay uses to color parts of the map where no relevant data is found", params = {
            "Overlay (Function or Attribute)", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 }, nullable = { 1 })
    SET_OVERLAY_REMAINDER_COLOR(Integer.class, TColor.class),

    @EventParamData(desc = "Add or remove specific Sources to or from a Source Overlay", params = { "Source Overlay", "Sources",
            "Add to the Overlay (false removes the sources from the Overlay)" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 1 })
    SET_OVERLAY_SOURCE(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Set a parent Overlay for an Overlay. An Overlay with a parent cannot be assigned as a parent to another Overlay", params = {
            "Overlay", "Parent Overlay" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_PARENT(Integer.class, Integer.class),

    @EventParamData(desc = "Set the result type of a Grid Overlay. The same calculation will be performed, but a different aspect of the calculation will be tracked and output", params = {
            "Grid Overlays", "Result type for the Overlay (must be a valid result type of the Overlay type)" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_RESULT_TYPE(Integer[].class, String[].class),

    @EventParamData(desc = "Set whether a Grid Overlay has a difference option. If an Overlay has a difference option, an addition output is available computed from the difference between the CURRENT and MAQUETTE outputs", params = {
            "Grid Overlays", "Has a difference option (false means there is no difference option for this Overlay)" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_SHOW_DIFFERENCE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set whether a Water Overlay visualizes the graph of systems which dictates its calculation. Pipes, center points of Areas, etc", params = {
            "Water Overlay", "Water System", "Visualized" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_SHOW_SYSTEM_VISUALIZATION(Integer.class, WaterSystem.class, Boolean.class),

    @EventParamData(desc = "Set the Inference Prequel for an input tensor of an Inference Overlay", params = { "Inference Overlay",
            "Tensor Link ID", "Inference Prequel" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_PREQUEL(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set whether an input tensor of an Inference Overlay should be normalized when provided to a Neural Network", params = {
            "Inference Overlay", "Tensor Link ID", "Normalize" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_NORMALIZED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the Inference Result for an output tensor of an Inference Overlay", params = { "Inference Overlay",
            "Tensor Link ID", "Output Result" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_RESULT_TYPE(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set the Value Type for an input tensor of an Inference Overlay", params = { "Inference Overlay",
            "Tensor Link ID", "Value Type" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_VALUE_TYPE(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set the Water Mode of a Water Overlay", params = { "Water Overlay", "Water Mode" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_WATER_MODE(Integer.class, WaterOverlay.Mode.class),

    @EventParamData(desc = "Set the Discharge Method of a Watershed Overlay", params = { "Watershed Overlay", "Discharge Method" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_DISCHARGE_METHOD(Integer.class, WatershedOverlay.DischargeMethod.class),

    @EventParamData(desc = "Set the Heat Model of a Heat Stress Overlay", params = { "Heat Stress Overlay", "Heat Model" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_HEAT_MODEL(Integer.class, HeatModel.class),

    @EventParamData(desc = "Set the Service layer names for a specified Service Overlay and timeframe", params = { "Overlay (WMS or WCS)",
            "Layer names" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_SERVICE_LAYER(Integer.class, String[].class),

    @EventParamData(desc = "Set the Service Source for a specified Service Overlay", params = { "Overlay (WMS or WCS)", "Source" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 1 })
    SET_SERVICE_SOURCE(Integer.class, Integer.class),

    @EventParamData(desc = "Set the preferred CRS for the Service Source of a specified Service Overlay", params = {
            "Overlays (WMS or WCS)", "CRS name" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_SERVICE_CRS(Integer[].class, String[].class),

    @EventParamData(desc = "Set the preferred option to force the CRS order to longitude/latitude for the Service Source of a specified Service Overlay", params = {
            "Overlays (WMS or WCS)", "Longitude first (force XY)" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_SERVICE_FORCE_XY(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set a specified Weather to serve as input for a specified Water Overlay", params = { "Water Overlay",
            "Weather" })
    @EventIDField(links = { OVERLAYS, WEATHERS }, params = { 0, 1 })
    SET_WATER_WEATHER(Integer.class, Integer.class),

    @EventParamData(desc = "Remove a Grid Overlay's prequel Overlay", params = { "Grid Overlay", "Prequel Type Name" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_PREQUEL(Integer.class, String.class),

    @EventParamData(desc = "Swap the order of paired Overlays", params = { "Overlays (main)", "Overlays (other)" })
    @EventIDField(sameLength = true, links = { OVERLAYS, OVERLAYS }, params = { 0, 1 })
    SWAP_ORDER(Integer[].class, Integer[].class);

    private final List<Class<?>> classes;

    private EditorOverlayEventType(Class<?>... classes) {
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

            case ADD, ADD_RESULT_CHILD -> Integer.class;

            case ADD_TYPE, DUPLICATE -> Integer[].class;

            case REMOVE, REMOVE_LEGEND_ENTRY, REMOVE_ENTRY, REMOVE_DIFFERENCE_ENTRY -> Boolean.class;

            case SET_LEGEND_ENTRY_NAME, SET_ENTRY_NAME, SET_DIFFERENCE_ENTRY_NAME -> Boolean.class;

            case SET_LEGEND_ENTRY_VALUE, SET_ENTRY_VALUE, SET_DIFFERENCE_ENTRY_VALUE -> Boolean.class;

            case SET_LEGEND_ENTRY_COLOR, SET_ENTRY_COLOR, SET_DIFFERENCE_ENTRY_COLOR -> Boolean.class;

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
            case REFRESH_GRID -> null; // already trigger indicators
            case SET_VISIBLE, SET_PARENT, SAVE_GRID, SET_SHOW_DIFFERENCE, SET_OVERLAY_SOURCE, SWAP_ORDER -> null; // ignore visuals only
            case ADD_OVERLAY_FUNCTION, REMOVE_OVERLAY_FUNCTION -> null;
            case SET_OVERLAY_PRIMARY_COLOR, SET_OVERLAY_REMAINDER_COLOR -> null;
            case ADD_ENTRY, ADD_DIFFERENCE_ENTRY, ADD_LEGEND_ENTRY -> null;
            case SET_CUSTOM_LEGEND -> null;
            case REMOVE_ENTRY, REMOVE_DIFFERENCE_ENTRY, REMOVE_LEGEND_ENTRY -> null;
            case SET_ENTRY_NAME, SET_DIFFERENCE_ENTRY_NAME, SET_LEGEND_ENTRY_NAME -> null;
            case SET_ENTRY_COLOR, SET_DIFFERENCE_ENTRY_COLOR, SET_LEGEND_ENTRY_COLOR -> null;
            case SET_ENTRY_VALUE, SET_DIFFERENCE_ENTRY_VALUE, SET_LEGEND_ENTRY_VALUE -> null;
            default -> OVERLAYS;
        };
    }
}
