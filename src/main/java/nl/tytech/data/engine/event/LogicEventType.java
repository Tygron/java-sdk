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
package nl.tytech.data.engine.event;

import static nl.tytech.core.net.serializable.MapLink.ACTION_MENUS;
import static nl.tytech.core.net.serializable.MapLink.AREAS;
import static nl.tytech.core.net.serializable.MapLink.BUILDINGS;
import static nl.tytech.core.net.serializable.MapLink.CINEMATIC_DATAS;
import static nl.tytech.core.net.serializable.MapLink.ERROR_LOGS;
import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.GLOBALS;
import static nl.tytech.core.net.serializable.MapLink.MEASUREMENTS;
import static nl.tytech.core.net.serializable.MapLink.MEASURES;
import static nl.tytech.core.net.serializable.MapLink.MONEY_TRANSFERS;
import static nl.tytech.core.net.serializable.MapLink.NEIGHBORHOODS;
import static nl.tytech.core.net.serializable.MapLink.NET_CLUSTERS;
import static nl.tytech.core.net.serializable.MapLink.NET_LINES;
import static nl.tytech.core.net.serializable.MapLink.NET_LOADS;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.SCENARIOS;
import static nl.tytech.core.net.serializable.MapLink.SPECIAL_EFFECTS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import static nl.tytech.core.net.serializable.MapLink.UNIT_DATA_OVERRIDES;
import static nl.tytech.core.net.serializable.MapLink.UPGRADE_TYPES;
import static nl.tytech.core.net.serializable.MapLink.ZONES;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.Description;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.MoneyTransfer;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.item.NetSetting;
import nl.tytech.data.engine.serializable.TimeState;

/**
 * LogicEventType: These server events trigger functionality in test runs.
 *
 * @author Maxim Knepfle, Frank Baars
 */
@Description("Endpoints that change functionality in a testrun or impact Session")
public enum LogicEventType implements SessionEventTypeEnum {

    @EventParamData(editor = true, desc = "Export history of actions taken by Stakeholders", params = {})
    ACTION_LOG_EXPORT,

    @EventParamData(editor = true, desc = "Add or remove a Function from an Action Menu", params = { "Action Menu", "Function",
            "Add (false = remove)" })
    @EventIDField(links = { ACTION_MENUS, FUNCTIONS }, params = { 0, 1 })
    ACTION_MENU_SET_FUNCTION_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add or remove a Measure from an Action Menu", params = { "Action Menu", "Measure",
            "Add (false = remove)" })
    @EventIDField(links = { ACTION_MENUS, MEASURES }, params = { 0, 1 })
    ACTION_MENU_SET_MEASURE_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add or remove an Upgrade Type from an Action Menu", params = { "Action Menu", "Upgrade Type",
            "Add (false = remove)" })
    @EventIDField(links = { ACTION_MENUS, UPGRADE_TYPES }, params = { 0, 1 })
    ACTION_MENU_SET_UPGRADE_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Activate/deactivate an Area", params = { "Areas", "Active Array" })
    @EventIDField(sameLength = true, links = { AREAS }, params = { 0, })
    AREA_SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(editor = true, desc = "Set an Area Attribute to the provided values", params = { "Areas", "Attribute", "Values",
            "Update Indicators (optional)" }, defaults = { "", "", "", "true" })
    @EventIDField(links = { AREAS }, params = { 0 }, nullable = { 3 })
    AREA_SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Boolean.class),

    @EventParamData(editor = true, desc = "Per entry in the array, set the value of the Attribute for the Area", params = { "Areas",
            "Attributes", "Value Array", "Update Indicators (optional)" })
    @EventIDField(sameLength = true, links = { AREAS, }, params = { 0 }, nullable = { 3 })
    AREAS_SET_ATTRIBUTES(Integer[].class, String[].class, double[].class, Boolean.class),

    @EventParamData(editor = true, desc = "Set a Building Attribute to the specified value", params = { "Building", "Attribute", "Value" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    BUILDING_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Per entry in the array, set the values of the Attribute for the Building", params = {
            "Buildings", "Attributes", "Array of values" })
    @EventIDField(sameLength = true, links = { BUILDINGS, }, params = { 0 })
    BUILDING_SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class),

    @EventParamData(desc = "Notify a Key point is reached for a Cinematic", params = { "Stakeholder", "Cinematic Data", "Key Point ID" })
    @EventIDField(links = { STAKEHOLDERS, CINEMATIC_DATAS }, params = { 0, 1 })
    CINEMATIC_REACHED_POINT(Integer.class, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Start a cinematic for a Stakeholder", params = { "Stakeholder", "Cinematic Data",
            "Animate to Cinematic starting point" })
    @EventIDField(links = { STAKEHOLDERS, CINEMATIC_DATAS }, params = { 0, 1 })
    CINEMATIC_STAKEHOLDER_START(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Stop a playing Cinematic for a Stakeholder", params = { "Stakeholder" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    CINEMATIC_STAKEHOLDER_STOP(Integer.class),

    @EventParamData(editor = false, desc = "Dismiss an Error Log", params = { "Error Logs" })
    @EventIDField(links = { ERROR_LOGS }, params = { 0 })
    DISMISS_ERROR(Integer[].class),

    @EventParamData(editor = true, desc = "Set the Global to the specified value", params = { "Globals", "Value Array" })
    @EventIDField(links = { GLOBALS }, params = { 0 })
    GLOBAL_SET_VALUE(Integer[].class, double[].class),

    @EventParamData(editor = true, desc = "Set the Global to the specified values", params = { "Globals", "Array of Values" })
    @EventIDField(sameLength = true, links = { GLOBALS }, params = { 0 })
    GLOBAL_SET_VALUES(Integer[].class, double[][].class),

    @EventParamData(editor = true, desc = "Activate a Scenario", params = { "Scenario" })
    @EventIDField(links = { SCENARIOS }, params = { 0 })
    SCENARIO_SET_ACTIVE(Integer.class),

    @EventParamData(editor = true, desc = "Set the Scenario Attribute to the specified numeric value", params = { "Scenarios", "Attribute",
            "Value Array", "Update Indicators (optional)" }, defaults = { "", "", "", "true" })
    @EventIDField(links = { SCENARIOS }, params = { 0 }, nullable = { 3 })
    SCENARIO_SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Boolean.class),

    @EventParamData(desc = "Directly sell land without requiring approval", params = { "Stakeholder (selling)", "Stakeholder (buying)",
            "MultiPolygon of land", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MAP_DIRECT_SELL_LAND(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(editor = true, desc = "Add a Line Measurement for an Overlay for a specific timeframe", params = { "Overlays",
            "Point start", "Point end", "Name", "Timeframe (-1 for automatic last)", "Save Measurement",
            "Sum" }, defaults = { "", "", "", "", "-1", "true", "true" }, response = "Measurement")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    MEASUREMENT_LINE_ADD(Integer[].class, Point.class, Point.class, String.class, Integer.class, Boolean.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add a Point Measurement for an Overlay", params = { "Overlays", "Point", "Name",
            "Save Measurement", "Sum" }, defaults = { "", "", "", "true", "true" }, response = "Measurement")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    MEASUREMENT_POINT_ADD(Integer[].class, Point.class, String.class, Boolean.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add an Item Measurement for an Overlay", //
            params = { "Overlay", "Item MapLink", "Item ID", "Identifying Key", "Name", "Save Measurement" }, //
            defaults = { "", "", "", "", "Item measurement", "true" }, response = "Measurement")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    MEASUREMENT_ITEM_ADD(Integer.class, MapLink.class, Integer.class, String.class, String.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set a Measurement to add its base Overlays to itself", params = { "Measurement",
            "Add base Overlays" }, defaults = { "", "true" })
    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    MEASUREMENT_SET_SUM(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Remove a Measurement", params = { "Measurements" })
    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    MEASUREMENT_REMOVE(Integer[].class),

    @EventParamData(editor = true, desc = "Save a Measurement", params = { "Measurement" })
    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    MEASUREMENT_SAVE(Integer.class),

    @EventParamData(editor = true, desc = "Set base Overlays for a Measurement", params = { "Measurement", "Overlays" })
    @EventIDField(links = { MEASUREMENTS, OVERLAYS }, params = { 0, 1 })
    MEASUREMENT_SET_OVERLAYS(Integer.class, Integer[].class),

    @EventParamData(desc = "Create a Money Transfer between two Stakeholders", params = { "Stakeholder (from)", "Stakeholder (to)",
            "Money transfer Type", "Provided motivation", "Transfer amount" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MONEY_TRANSFER_ADD(Integer.class, Integer.class, MoneyTransfer.Type.class, String.class, Double.class),

    @EventParamData(desc = "Approve a Money Transfer", params = { "Money Transfer", "Approved" })
    @EventIDField(links = { MONEY_TRANSFERS }, params = { 0 })
    MONEY_TRANSFERS_SET_APPROVED(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set an Attribute for a Neighborhood", params = { "Neighborhoods", "Attribute", "Value Array",
            "Update Indicators (optional)" }, defaults = { "", "", "", "true" })
    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 }, nullable = { 3 })
    NEIGHBORHOOD_SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Boolean.class),

    @EventParamData(editor = true, desc = "Set the fraction of connected loads with a Net Cluster", params = { "Net Cluster",
            "Fraction connected, between 0 and 1" })
    @EventIDField(links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTER_SET_FRACTION_CONNECTED(Integer.class, Double.class),

    @EventParamData(editor = true, desc = "For each Net Load of a Net Cluster, set the value of the Attribute", params = { "Net Cluster",
            "Attribute", "Value" })
    @EventIDField(links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "For each Net Load of a Net Cluster, set the values of the provided Attributes", params = {
            "Net Cluster", "Attributes", "Array with values" })
    @EventIDField(links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTES(Integer.class, String[].class, double[].class),

    @EventParamData(editor = true, desc = "Per Net Cluster, set all its Net Loads of the provided NetType to the given TimeState", params = {
            "Net Clusters", "NetType of loads to be changed",
            "Time State (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(sameLength = true, links = { NET_CLUSTERS }, params = { 0 })
    NET_CLUSTERS_SET_STATE(Integer[].class, NetType[].class, TimeState[].class),

    @EventParamData(editor = true, desc = "Set the provided Attribute values for each Net Load within the specified Net Clusters", params = {
            "Net Clusters", "Array with Attributes", "Array with values" })
    @EventIDField(sameLength = true, links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTERS_SET_LOAD_ATTRIBUTES(Integer[].class, String[].class, double[][].class),

    @EventParamData(editor = true, desc = "Set the default fraction of connected loads with a cluster", params = {
            "Fraction connected, between 0 and 1" })
    NET_CLUSTERS_SET_DEFAULT_FRACTION_CONNECTED(Double.class),

    @EventParamData(editor = true, desc = "Set the Attribute of a Net Line to a value", params = { "Net Line", "Attribute", "Value" })
    @EventIDField(links = { NET_LINES, }, params = { 0 })
    NET_LINE_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "For each Net Line, set the value of the Attribute", params = { "Net Lines", "Attribute",
            "Value" })
    @EventIDField(links = { NET_LINES, }, params = { 0 })
    NET_LINES_SET_ATTRIBUTE(Integer[].class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Per entry in the array, set the value of the Attribute for the Net Line", params = { "Net Lines",
            "Attributes", "Values" })
    @EventIDField(sameLength = true, links = { NET_LINES, }, params = { 0 })
    NET_LINES_SET_ATTRIBUTES(Integer[].class, String[].class, double[].class),

    @EventParamData(editor = true, desc = "Search for Neighborhood, Building or Address", params = { "Map Type", "Search Query" })
    SEARCH(MapType.class, String.class),

    @EventParamData(desc = "Release a client from a session using its client token", params = { "ClientToken" })
    SESSION_RELEASE(String.class),

    @EventParamData(editor = true, desc = "Set a boolean value for the specified NetSetting", params = { "NetSetting Types",
            "Array of Booleans" })
    SET_NET_SETTING_BOOLEAN(NetSetting.Type[].class, Boolean[].class),

    @EventParamData(editor = true, desc = "Multiply the visual traffic density by a factor", params = { "Factor" }, defaults = { "4.0" })
    SET_TRAFFIC_MULTIPLIER(Double.class),

    @EventParamData(editor = true, desc = "Activate/deactivate a Special Effect", params = { "Special Effect", "Activate" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    SPECIAL_EFFECT_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(desc = "Release a Stakeholder from a session", params = { "Stakeholder" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    STAKEHOLDER_RELEASE(Integer.class),

    @EventParamData(desc = "Select the preferred Stakeholder or the next available option if unavailable", params = {
            "Preferred Stakeholder or empty", "Client Token (from Join Session event)" }, response = "Selected Stakeholder ID")
    STAKEHOLDER_SELECT(Integer.class, String.class),

    @EventParamData(desc = "Undo the last Building action of a Stakeholder", params = { "Stakeholder" })
    @EventIDField(links = { STAKEHOLDERS, }, params = { 0 })
    UNDO_LAST_BUILDING_ACTION(Integer.class),

    @EventParamData(editor = true, desc = "Activate/deactivate units of a specific type", params = { "Unit Data Override", "Activate" })
    @EventIDField(links = { UNIT_DATA_OVERRIDES }, params = { 0 })
    UNIT_TYPE_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set an Attribute for a Zone", params = { "Zone", "Attribute", "Value" })
    @EventIDField(links = { ZONES }, params = { 0 })
    ZONE_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Set Attributes values for Zones", params = { "Zones", "Attributes", "Array of Values" })
    @EventIDField(sameLength = true, links = { ZONES }, params = { 0 })
    ZONES_SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class);

    private final List<Class<?>> classes;

    private LogicEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return true;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {

        return switch (this) {

            case STAKEHOLDER_SELECT, MEASUREMENT_POINT_ADD, MEASUREMENT_LINE_ADD, MEASUREMENT_ITEM_ADD -> Integer.class;

            case SEARCH -> HashMap.class;

            case ACTION_LOG_EXPORT -> byte[].class;

            default -> null;
        };

    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public boolean triggerTestRun() {

        return switch (this) {

            case STAKEHOLDER_SELECT, STAKEHOLDER_RELEASE, SET_TRAFFIC_MULTIPLIER -> false;

            case MEASUREMENT_LINE_ADD, MEASUREMENT_POINT_ADD, MEASUREMENT_ITEM_ADD -> false;

            case MEASUREMENT_REMOVE, MEASUREMENT_SAVE, MEASUREMENT_SET_OVERLAYS, MEASUREMENT_SET_SUM -> false;

            case DISMISS_ERROR, SEARCH, SCENARIO_SET_ACTIVE -> false;

            default -> true;
        };
    }

    @Override
    public MapLink triggerUpdate(Event event) {

        return switch (this) {

            // depending on event argument trigger update
            case SCENARIO_SET_ATTRIBUTE -> Boolean.TRUE.equals(event.getContent(3)) ? SCENARIOS : null;
            case AREA_SET_ATTRIBUTE -> Boolean.TRUE.equals(event.getContent(3)) ? AREAS : null;
            case AREAS_SET_ATTRIBUTES -> Boolean.TRUE.equals(event.getContent(3)) ? AREAS : null;
            case NEIGHBORHOOD_SET_ATTRIBUTE -> Boolean.TRUE.equals(event.getContent(3)) ? NEIGHBORHOODS : null;

            // always update
            case AREA_SET_ACTIVE -> AREAS;
            case GLOBAL_SET_VALUE, GLOBAL_SET_VALUES -> GLOBALS;
            case BUILDING_SET_ATTRIBUTE, BUILDING_SET_ATTRIBUTES, UNDO_LAST_BUILDING_ACTION -> BUILDINGS;

            // note: forces load update not cluster
            case NET_CLUSTER_SET_LOAD_ATTRIBUTE, NET_CLUSTERS_SET_LOAD_ATTRIBUTES, NET_CLUSTERS_SET_STATE -> NET_LOADS;
            case NET_CLUSTER_SET_FRACTION_CONNECTED, NET_CLUSTERS_SET_DEFAULT_FRACTION_CONNECTED -> NET_CLUSTERS;
            case NET_LINE_SET_ATTRIBUTE, NET_LINES_SET_ATTRIBUTE, NET_LINES_SET_ATTRIBUTES -> NET_LINES;
            case MAP_DIRECT_SELL_LAND -> STAKEHOLDERS;
            case MONEY_TRANSFER_ADD, MONEY_TRANSFERS_SET_APPROVED -> MONEY_TRANSFERS;
            case ZONE_SET_ATTRIBUTE, ZONES_SET_ATTRIBUTES -> ZONES;
            default -> null;
        };
    }
}
