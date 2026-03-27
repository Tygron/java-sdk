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

    @EventParamData(desc = "Add a new Overlay of the specified type.", params = { "OverlayType" }, response = "Overlay ID")
    ADD(OverlayType.class),

    @EventParamData(desc = "Add a Legend Entry to a specified Overlay. The Overlay must have a custom legend before entries can be added to it.", params = {
            "Overlay ID", "Add to custom difference legend (false adds the entry to the normal custom legend)", "Name (optional)",
            "Color (optional)", "Value (optional)" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 }, nullable = { 2, 3, 4 })
    ADD_LEGEND_ENTRY(Integer[].class, Boolean[].class, String[].class, TColor[].class, Float[].class),

    @EventParamData(desc = "Add a function to highlight to a specified FUNCTION Overlay.", params = {
            "Overlay ID (must relate to a FUNCTION Overlay)", "Function IDs" })
    @EventIDField(links = { OVERLAYS, FUNCTIONS }, params = { 0, 1 })
    ADD_OVERLAY_FUNCTION(Integer.class, Integer[].class),

    @EventParamData(desc = "Add a specified result type as a child Overlay to a specified grid Overlay. The child Overlay's result will be based on the parent's"
            + " calculation, allowing you to get multiple types of results from the same calculation.", params = {
                    "Overlay ID (must relate to a grid Overlay with result types)",
                    "Result type for new child Overlay (must be a valid result for the parent Overlay type)" }, response = "Overlay ID")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    ADD_RESULT_CHILD(Integer.class, String.class),

    @EventParamData(desc = "Add multiple new Overlays of the specified types.", params = { "Array of OverlayTypes" })
    ADD_TYPE(OverlayType[].class),

    @EventIDField(links = { OVERLAYS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @Deprecated
    @EventParamData(desc = "Deprecated functionality, entire map is always updated when UPDATE is called. "
            + "Note that when the \"Auto calculation\" is not active, an editor/update/ event is required to generate the new Overlay results. "
            + "Or for long-running updates use: editor/set_scheduled_update/ to schedule an update.", params = {})
    REFRESH_GRID(),

    @EventParamData(desc = "Remove the specified Overlays.", params = { "Overlay IDs" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove the specified attributes of the specified Overlay. The attributes, including attributes which are added to an Overlay by default, are removed entirely.", params = {
            "Overlay ID", "Attributes to remove" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove the specified legend entries from a specified Overlay. The Overlay must have a custom legend before entries can be removed from it.", params = {
            "Overlay ID", "Remove from custom difference legend (false removes the entry from the normal custom legend)",
            "Ids of entries which should be removed" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_LEGEND_ENTRY(Integer.class, Boolean.class, Integer[].class),

    @EventParamData(desc = "Remove a specified function to highlight from a specified FUNCTION_HIGHLIGHT Overlay.", params = {
            "Overlay ID (must relate to a FUNCTION_HIGHLIGHT Overlay)", "Function IDs" })
    @EventIDField(links = { OVERLAYS, FUNCTIONS }, params = { 0, 1 })
    REMOVE_OVERLAY_FUNCTION(Integer.class, Integer[].class),

    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_WATER_WEATHER(Integer.class),

    @EventParamData(desc = "Create a copy of each specified Overlay. The copies will have the same settings and attributes, but can be edited independently of the originals.", params = {
            "Overlay ID" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SAVE_GRID(Integer[].class),

    @EventParamData(params = { "Overlays", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Overlays", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Overlays", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { OVERLAYS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set which attribute a specified ATTRIBUTE Overlay should use to color the items it finds. "
            + "If present in the item, it will use that attribute's color to color the item in the 3D world. If absent, the Overlay's color will be used.", params = {
                    "Overlay ID (must relate to an ATTRIBUTE Overlay)", "Attribute to use to color a found item" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ATTRIBUTE_OVERLAY_COLOR_ATTRIBUTE(Integer.class, String.class),

    @EventParamData(desc = "Set which type of item and which attribute a specified ATTRIBUTE Overlay should highlight. The ATTRIBUTE Overlay will highlight each item it finds which is"
            + " both of the specified maptype as well as has the specified attribute", params = {
                    "Overlay ID (must relate to an ATTRIBUTE Overlay)",
                    "MapLink: AREAS, BUILDINGS, NEIGHBORHOODS, NET_LOADS, TERRAINS, ZONES", "Attribute to look for" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_ATTRIBUTE_OVERLAY_VALUES(Integer.class, MapLink.class, String.class),

    @EventParamData(desc = "Set the averaging distance for a specified AVG Overlay.", params = {
            "Overlay ID (must relate to an AVG Overlay)", "Averaging distance" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_AVG_DISTANCE(Integer.class, Double.class),

    @EventParamData(desc = "Set the type of rasterization a specified AVG or Distance Overlay should do.", params = {
            "Overlay ID (must relate to an AVG or Distance Overlay)", "Rasterization type: FIRST, MIN, MAX, SINGLE_LAYER, GRID",
            "MapLink (will only affect the SINGLE_LAYER rasterization type): BUILDINGS, TERRAINS, AREAS, NEIGHBORHOODS" })
    @EventIDField(links = { OVERLAYS }, params = { 0 }, nullable = { 2 })
    SET_RASTERIZATION(Integer.class, Rasterization.class, MapLink.class),

    @EventParamData(desc = "Set whether the specified Overlay has a custom legend. If the specified Overlay is reverted to use a default Overlay, any custom legend information for the Overlay is removed.", params = {
            "Overlay ID", "Has a custom legend (false sets the Overlay to use the default legend)" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_CUSTOM_LEGEND(Integer.class, Boolean.class),

    @EventParamData(desc = "Change the color of a specified legend entry of a custom legend of a specified Overlay.", params = {
            "Overlay ID", "Affect the custom difference legend (false means affect the normal custom legend)",
            "ID of entry which should be changed", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_LEGEND_ENTRY_COLOR(Integer.class, Boolean.class, Integer.class, TColor.class),

    @EventParamData(desc = "Change the name of a specified legend entry of a custom legend of a specified Overlay.", params = {
            "Overlay ID", "Affect the custom difference legend (false means affect the normal custom legend)",
            "ID of entry which should be changed", "New name for the entry" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_LEGEND_ENTRY_NAME(Integer.class, Boolean.class, Integer.class, String.class),

    @EventParamData(desc = "Change the value of a specified legend entry of a custom legend of a specified Overlay.", params = {
            "Overlay ID", "Affect the custom difference legend (false means affect the normal custom legend)",
            "ID of entry which should be changed", "New value for the entry" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_LEGEND_ENTRY_VALUE(Integer.class, Boolean.class, Integer.class, Float.class),

    @EventParamData(desc = "Change the GeoTIFF displayed by a specified GeoTIFF Overlay.", params = {
            "Overlay ID (must relate to a GeoTIFF Overlay)", "Unique GeoTIFF IDs" })
    @EventIDField(links = { OVERLAYS, GEO_TIFFS }, params = { 0, 1 })
    SET_GEOTIFF(Integer.class, Integer[].class),

    @EventParamData(desc = "Set the Neural Network for an Inference Overlay.", params = { "Overlay ID (must relate to a Inference Overlay)",
            "Neural Network ID" })
    @EventIDField(links = { OVERLAYS, NEURAL_NETWORKS }, params = { 0, 1 })
    SET_NEURAL_NETWORK(Integer.class, Integer.class),

    @EventParamData(desc = "Activates or deactivates an Overlay. Only active Overlays are recalculated by the simulation.", params = {
            "Overlay ID", "Activated" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_GRID_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Change the image displayed by a specified IMAGE Overlay. The image must be uploaded in the \"Overlays\" directory.", params = {
            "Overlay IDs (must relate to an IMAGE Overlay)", "Array of Project asset names, omitting the directory" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_OVERLAY_IMAGE(Integer[].class, String[][].class),

    @EventParamData(desc = "Change the icon displayed in the navigation window by the Overlay. Only existing overlay project assets are allowed.", params = {
            "Overlay ID", "Project asset name, omitting the directory" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventParamData(desc = "Visualizes or hides an Overlay in the navigation panel.", params = { "Overlay ID", "Visualized" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_VISIBLE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Change a key for a specified Overlay. The Overlay must be an Overlay which makes use of keys to read attributes of geographical items, as input for its calculation.", params = {
            "Overlay ID (must relate to a grid Overlay)", "Key name", "Attribute to look for" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_KEY_VALUE(Integer.class, String.class, String.class),

    @EventParamData(desc = "Set the name for a specified Overlay.", params = { "Overlay ID", "Name for the Overlay" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the visualization style for a specified network Overlay.", params = {
            "Overlay ID (must relate to a network Overlay)", "NetType",
            "Whether to show the network (false means no lines will be visualized)",
            "Whether only active connections should be visualized (false means both active and inactive connections are visualized)" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_NET_OVERLAY(Integer.class, NetType.class, Boolean.class, Boolean.class),

    @EventParamData(desc = "Add or remove specified areas to or from a specified AREAS Overlay. Unincluded areas which are already related to the Overlay will remain related."
            + " Unincluded areas which are not related to the Overlay will remain unrelated.", params = {
                    "Overlay ID (must relate to an AREAS Overlay)", "Array of area IDs",
                    "Should be added to the Overlay (false removes the areas from the Overlay)" })
    @EventIDField(links = { OVERLAYS, AREAS }, params = { 0, 1 })
    SET_OVERLAY_AREA(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Set the color a specified FUNCTION or ATTRIBUTE Overlay uses to highlight found data.", params = {
            "Overlay ID (must relate to a FUNCTION or ATTRIBUTE Overlay)", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_OVERLAY_PRIMARY_COLOR(Integer.class, TColor.class),

    @EventParamData(desc = "Set formula for a Combo Overlay.", params = { "Overlay ID", "Formula" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_COMBO_FORMULA(Integer.class, String.class),

    @EventParamData(desc = "Set prequel Overlay.", params = { "Overlay ID", "Prequel Overlay ID", "Prequel Type Name",
            "Prequel Timeframe ID (optional)", "Previous Iteration (optional)" })
    @EventIDField(links = { OVERLAYS, OVERLAYS }, params = { 0, 1 }, nullable = { 3, 4 })
    SET_PREQUEL(Integer.class, Integer.class, String.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the color a specified FUNCTION or ATTRIBUTE Overlay uses to color parts of the map where no relevant data is found.", params = {
            "Overlay ID (must relate to a FUNCTION or ATTRIBUTE Overlay)", "Color" })
    @EventIDField(links = { OVERLAYS }, params = { 0 }, nullable = { 1 })
    SET_OVERLAY_REMAINDER_COLOR(Integer.class, TColor.class),

    @EventParamData(desc = "Add or remove specified sources to or from a specified SOURCE Overlay. Unincluded sources which are already related to the Overlay will remain related."
            + " Unincluded sources which are not related to the Overlay will remain unrelated.", params = {
                    "Overlay ID (must relate to a SOURCES Overlay)", "Array of source IDs",
                    "Should be added to the Overlay (false removes the sources from the Overlay)" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 1 })
    SET_OVERLAY_SOURCE(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Set a parent Overlay for a specified Overlay. This will cause the specified Overlay to be a subselectable Overlay of the parent. It is not possible to set an Overlay which has a parent itself as a parent of another Overlay.", params = {
            "Overlay ID", "Overlay ID of parent (overlays without a parent should have their parent Overlay ID set to -1)" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_PARENT(Integer.class, Integer.class),

    @EventParamData(desc = "Set the result type of a specified grid Overlay. The same calculation will be performed, but a different aspect of the calculation will be tracked and output.", params = {
            "Overlay ID (must relate to a grid Overlay with result types)",
            "Result type for the Overlay (must be a valid result type of the Overlay type)" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_RESULT_TYPE(Integer[].class, String[].class),

    @EventParamData(desc = "Set whether a specified grid Overlay has a difference option. If an Overlay has a difference option, an addition output is available computed from the difference between the CURRENT and MAQUETTE outputs.", params = {
            "Overlay ID (must relate to a grid Overlay)",
            "Has a difference option (false means there is no difference option for this Overlay)" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_SHOW_DIFFERENCE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set whether a specified Overlay visualizes the graph of systems which dictates its calculation. Pipes, center points of areas, etc.", params = {
            "Overlay ID (must relate to a water Overlay)", "Visual Type", "Show" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_SHOW_SYSTEM_VISUALIZATION(Integer.class, WaterSystem.class, Boolean.class),

    @EventParamData(desc = "Set the Inference Prequel for an input tensor of an Inference Overlay.", params = { "Inference Overlay ID",
            "Tensor Link ID", "Inference Prequel" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_PREQUEL(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set whether an input tensor of an Inference Overlay should be normalized when provided to a neural network.", params = {
            "Inference Overlay ID", "Tensor Link ID", "Normalize" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_NORMALIZED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the Inference Result for an output tensor of an Inference Overlay.", params = { "Inference Overlay ID",
            "Tensor Link ID", "Output Result" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_RESULT_TYPE(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set the Value Type for an input tensor of an Inference Overlay.", params = { "Inference Overlay ID",
            "Tensor Link ID", "Value Type" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_TENSOR_VALUE_TYPE(Integer.class, Integer.class, String.class),

    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_WATER_MODE(Integer.class, WaterOverlay.Mode.class),

    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_DISCHARGE_METHOD(Integer.class, WatershedOverlay.DischargeMethod.class),

    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_HEAT_MODEL(Integer.class, HeatModel.class),

    @EventParamData(desc = "Set the Service layer names for a specified Service Overlay and timeframe.", params = { "Service Overlay ID ",
            "Layer name(s)" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SET_SERVICE_LAYER(Integer.class, String[].class),

    @EventParamData(desc = "Set the Service source for a specified Service Overlay.", params = { "Service Overlay ID ", "Source ID" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 0, 1 })
    SET_SERVICE_SOURCE(Integer.class, Integer.class),

    @EventParamData(desc = "Set the prefered CRS for the Service source of a specified Service Overlay.", params = { "Service Overlay ID ",
            "CRS name" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_SERVICE_CRS(Integer[].class, String[].class),

    @EventParamData(desc = "Set the prefered option to force the CRS order to longitude/latitude for the Service source of a specified Service Overlay.", params = {
            "Service Overlay ID ", "Longitude first (force XY)" })
    @EventIDField(sameLength = true, links = { OVERLAYS }, params = { 0 })
    SET_SERVICE_FORCE_XY(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set a specified weather to serve as input for a specified water Overlay.", params = { "Water Overlay ID ",
            "Weather ID" })
    @EventIDField(links = { OVERLAYS, WEATHERS }, params = { 0, 1 })
    SET_WATER_WEATHER(Integer.class, Integer.class),

    @EventParamData(desc = "Remove a Grid Overlay's prequel Overlay.", params = { "Grid Overlay ID", "Prequel Type Name" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    REMOVE_PREQUEL(Integer.class, String.class),

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

        if (this == ADD || this == ADD_RESULT_CHILD) {
            return Integer.class;
        }
        if (this == ADD_TYPE || this == DUPLICATE) {
            return Integer[].class;
        }
        if (this == REMOVE) {
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
            case REFRESH_GRID -> null; // already trigger indicators
            case SET_VISIBLE, SET_PARENT, SAVE_GRID, SET_SHOW_DIFFERENCE, SET_OVERLAY_SOURCE, SWAP_ORDER -> null; // ignore visuals only
            case ADD_OVERLAY_FUNCTION, REMOVE_OVERLAY_FUNCTION -> null;
            case SET_OVERLAY_PRIMARY_COLOR, SET_OVERLAY_REMAINDER_COLOR -> null;
            case ADD_LEGEND_ENTRY, REMOVE_LEGEND_ENTRY, SET_CUSTOM_LEGEND, SET_LEGEND_ENTRY_NAME, SET_LEGEND_ENTRY_COLOR, SET_LEGEND_ENTRY_VALUE -> null;
            default -> OVERLAYS;
        };
    }
}
