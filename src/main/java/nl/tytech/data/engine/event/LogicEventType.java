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
import nl.tytech.core.item.annotations.ClassDescription;
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
 * @author Maxim Knepfle
 */
@ClassDescription("Events changing functionality in a testrun/impact Session.")
public enum LogicEventType implements SessionEventTypeEnum {

    @EventParamData(editor = true, desc = "Export history of actions taken by participants.", params = {})
    ACTION_LOG_EXPORT,

    @EventParamData(editor = true, desc = "Add or remove a function from a particular Action Menu.", params = { "Action Menu ID",
            "Function ID", "Available" })
    @EventIDField(links = { ACTION_MENUS, FUNCTIONS }, params = { 0, 1 })
    ACTION_MENU_SET_FUNCTION_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add or remove a measure from a particular Action Menu.", params = { "Action Menu ID",
            "Measure ID", "Available" })
    @EventIDField(links = { ACTION_MENUS, MEASURES }, params = { 0, 1 })
    ACTION_MENU_SET_MEASURE_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add or remove an upgrade from a particular Action Menu.", params = { "Action Menu ID",
            "Upgrade ID", "Available" })
    @EventIDField(links = { ACTION_MENUS, UPGRADE_TYPES }, params = { 0, 1 })
    ACTION_MENU_SET_UPGRADE_AVAILABLE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Activate/deactivate a Area.", params = { "Area ID", "Activated" })
    @EventIDField(sameLength = true, links = { AREAS }, params = { 0, })
    AREA_SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(editor = true, desc = "Set Area Attribute to given number Value.", params = { "Area ID", "Attribute Name", "Value",
            "Update Indicators (optional)" }, defaults = { "", "", "", "true" })
    @EventIDField(links = { AREAS }, params = { 0 }, nullable = { 3 })
    AREA_SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Boolean.class),

    @EventParamData(editor = true, desc = "Per entry in the array, set the value of the attribute for the Area.", params = {
            "Array of Area IDs", "Array of Attributes", "Array of Values", "Update Indicators (optional)" })
    @EventIDField(sameLength = true, links = { AREAS, }, params = { 0 }, nullable = { 3 })
    AREAS_SET_ATTRIBUTES(Integer[].class, String[].class, double[].class, Boolean.class),

    @EventParamData(editor = true, desc = "Set Building Attribute to given number Value.", params = { "Building ID", "Attribute Name",
            "Value" })
    @EventIDField(links = { BUILDINGS }, params = { 0 })
    BUILDING_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Per entry in the array, set the values of the attribute for the Building.", params = {

            "Array of Building IDs", "Attribute Names", "Array of values" })
    @EventIDField(sameLength = true, links = { BUILDINGS, }, params = { 0 })
    BUILDING_SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class),

    @EventParamData(desc = "Flightpoint reached for a specific cinematic.", params = { "Stakeholder ID", "Cinematic ID",
            "ID of point in cinematic reached" })
    @EventIDField(links = { STAKEHOLDERS, CINEMATIC_DATAS }, params = { 0, 1 })
    CINEMATIC_REACHED_POINT(Integer.class, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Start a cinematic for a specific stakeholder.", params = { "Stakeholder ID", "Cinematic ID",
            "Animate to cinematic starting point" })
    @EventIDField(links = { STAKEHOLDERS, CINEMATIC_DATAS }, params = { 0, 1 })
    CINEMATIC_STAKEHOLDER_START(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Stop any cinematic for a specific Stakeholder.", params = { "Stakeholder ID" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    CINEMATIC_STAKEHOLDER_STOP(Integer.class),

    @EventParamData(editor = false, desc = "Dismiss error log.", params = { "ErrorLog ID" })
    @EventIDField(links = { ERROR_LOGS }, params = { 0 })
    DISMISS_ERROR(Integer[].class),

    @EventParamData(editor = true, desc = "Set Global Variable to given number Value.", params = { "Global ID", "Value" })
    @EventIDField(links = { GLOBALS }, params = { 0 })
    GLOBAL_SET_VALUE(Integer[].class, double[].class),

    @EventParamData(editor = true, desc = "Set Global Variable to given number Values.", params = { "Global ID", "Values" })
    @EventIDField(sameLength = true, links = { GLOBALS }, params = { 0 })
    GLOBAL_SET_VALUES(Integer[].class, double[][].class),

    @EventParamData(editor = true, desc = "Activate a level.", params = { "Level ID" })
    @EventIDField(links = { SCENARIOS }, params = { 0 })
    SCENARIO_SET_ACTIVE(Integer.class),

    @EventParamData(editor = true, desc = "Set Scenario Attribute to given number Value.", params = { "Scenario ID", "Attribute Name",
            "Value", "Update Indicators (optional)" }, defaults = { "", "", "", "true" })
    @EventIDField(links = { SCENARIOS }, params = { 0 }, nullable = { 3 })
    SCENARIO_SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Boolean.class),

    @EventParamData(desc = "Direct sale of land, no approval asked.", params = { "Selling Stakeholder ID", "Buying Stakeholder ID",
            "Area of land being sold", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MAP_DIRECT_SELL_LAND(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(editor = true, desc = "Add a LineMeasurement for an Overlay for a specific timeframe.", params = { "Overlay IDs",
            "Point start", "Point end", "Name", "Timeframe (-1 for automatic last)", "Save Measurement",
            "Sum" }, defaults = { "", "", "", "", "-1", "true", "true" }, response = "Measurement ID")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    MEASUREMENT_LINE_ADD(Integer[].class, Point.class, Point.class, String.class, Integer.class, Boolean.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add a PointMeasurement for an Overlay.", params = { "Overlay IDs", "Point", "Name",
            "Save Measurement", "Sum" }, defaults = { "", "", "", "true", "true" }, response = "Measurement ID")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    MEASUREMENT_POINT_ADD(Integer[].class, Point.class, String.class, Boolean.class, Boolean.class),

    @EventParamData(editor = true, desc = "Add a ItemMeasurement for an Overlay.", //
            params = { "Overlay ID", "MapLink ID", "Item ID", "Identifying Key", "Name", "Save Measurement" }, //
            defaults = { "", "", "", "", "Item measurement", "true" }, response = "Measurement ID")
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    MEASUREMENT_ITEM_ADD(Integer.class, MapLink.class, Integer.class, String.class, String.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set a Measurement to add its base Overlays to itself.", params = { "Measurement ID",
            "Add base Overlays" }, defaults = { "", "true" })
    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    MEASUREMENT_SET_SUM(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Remove a measurement.", params = { "Measurement ID" })
    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    MEASUREMENT_REMOVE(Integer[].class),

    @EventParamData(editor = true, desc = "Save a measurement.", params = { "Measurement ID" })
    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    MEASUREMENT_SAVE(Integer.class),

    @EventParamData(editor = true, desc = "Set base overlays for an existing Measurement.", params = { "Measurement ID",
            "Overlay ID" }, defaults = { "", "" })
    @EventIDField(links = { MEASUREMENTS, OVERLAYS }, params = { 0, 1 })
    MEASUREMENT_SET_OVERLAYS(Integer.class, Integer[].class),

    @EventParamData(desc = "Create a new money transfer between two Stakeholders.", params = { "Stakeholder From ID", "Stakeholder To ID",
            "Money transfer Type", "Provided motivation", "Transfer amount" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MONEY_TRANSFER_ADD(Integer.class, Integer.class, MoneyTransfer.Type.class, String.class, Double.class),

    @EventParamData(desc = "Approve a money transfer", params = { "Money transfer ID", "Approved" })
    @EventIDField(links = { MONEY_TRANSFERS }, params = { 0 })
    MONEY_TRANSFERS_SET_APPROVED(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set an attribute for a neighborhood.", params = { "Neighborhood ID", "Valid attribute name",
            "Attribute value", "Update Indicators (optional)" }, defaults = { "", "", "", "true" })
    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 }, nullable = { 3 })
    NEIGHBORHOOD_SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Boolean.class),

    @EventParamData(editor = true, desc = "Set the fraction of connected loads with a cluster.", params = { "NetCluster ID",
            "Fraction connected, between 0 and 1" })
    @EventIDField(links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTER_SET_FRACTION_CONNECTED(Integer.class, Double.class),

    @EventParamData(editor = true, desc = "For each NetLoad of a NetCluster, set the value of the attribute.", params = { "NetCluster ID",
            "Attribute name", "Value" })
    @EventIDField(links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "For each NetLoad of a NetCluster, set the values of the provided attributes.", params = {
            "NetCluster ID", "Array with attribute names", "Array with values" })
    @EventIDField(links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTES(Integer.class, String[].class, double[].class),

    @EventParamData(editor = true, desc = "Per NetCluster, set all its Net Loads of the provided NetType to the given TimeState.", params = {
            "NetCluster IDs", "NetType of loads to be changed.",
            "Time State (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(sameLength = true, links = { NET_CLUSTERS }, params = { 0 })
    NET_CLUSTERS_SET_STATE(Integer[].class, NetType[].class, TimeState[].class),

    @EventParamData(editor = true, desc = "Per NetCluster, for each NetLoad of that NetCluster, set the values of the provided attributes.", params = {
            "NetCluster IDs", "Array with attribute names", "Array with values" })
    @EventIDField(sameLength = true, links = { NET_CLUSTERS, }, params = { 0 })
    NET_CLUSTERS_SET_LOAD_ATTRIBUTES(Integer[].class, String[].class, double[][].class),

    @EventParamData(editor = true, desc = "Set the default fraction of connected loads with a cluster.", params = {
            "Fraction connected, between 0 and 1" })
    NET_CLUSTERS_SET_DEFAULT_FRACTION_CONNECTED(Double.class),

    @EventParamData(editor = true, desc = "Set the attribute of a NetLine to the provided value.", params = { "NetLine ID", "Attribute",
            "Value" })
    @EventIDField(links = { NET_LINES, }, params = { 0 })
    NET_LINE_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "For each NetLine, set the value of the attribute.", params = { "Array of NetLine IDs",
            "Attribute", "Value" })
    @EventIDField(links = { NET_LINES, }, params = { 0 })
    NET_LINES_SET_ATTRIBUTE(Integer[].class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Per entry in the array, set the value of the attribute for the Netline.", params = {
            "Array of NetLine IDs", "Array of Attributes", "Array of Values" })
    @EventIDField(sameLength = true, links = { NET_LINES, }, params = { 0 })
    NET_LINES_SET_ATTRIBUTES(Integer[].class, String[].class, double[].class),

    @EventParamData(editor = true, desc = "Search for Neighborhood, Building or Address.", params = { "Map", "Query" })
    SEARCH(MapType.class, String.class),

    @EventParamData(desc = "Release a client from a session using its client token.", params = { "ClientToken" })
    SESSION_RELEASE(String.class),

    @EventParamData(editor = true, desc = "For boolean valued NetSettings (ELECTRICITY_ACTIVE, GAS_ACTIVE, HEAT_ACTIVE, INTERNET_ACTIVE, SEWER_ACTIVE, REQUIRE_UTILITY_CORPORATION_APPROVAL, CLUSTER_MODELS_ENABLED, LOAD_TO_NODE_LINES_ENABLED, FIRST_CONNECT_ACCEPT, RESTRICT_TO_NET_OVERLAY), set the value to true or false.", params = {
            "NetSetting Type", "True or false" })
    SET_NET_SETTING_BOOLEAN(NetSetting.Type[].class, Boolean[].class),

    @EventParamData(editor = true, desc = "Multiply visual (only) traffic density with this factor.", params = { "Factor, default 4.0" })
    SET_TRAFFIC_MULTIPLIER(Double.class),

    @EventParamData(editor = true, desc = "Activate/deactivate a special effect.", params = { "Special Effect ID", "Activated" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    SPECIAL_EFFECT_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(desc = "Release a stakeholder from a session.", params = { "Stakeholder ID" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    STAKEHOLDER_RELEASE(Integer.class),

    @EventParamData(desc = "Select Stakeholder with preferred ID, when not available fallback to next best.", params = {
            "Preferred Stakeholder ID or empty", "Client Token (from Join Session event)" }, response = "Selected Stakeholder ID")
    STAKEHOLDER_SELECT(Integer.class, String.class),

    @EventParamData(desc = "Undo the last building action of a stakeholder.", params = { "Stakeholder ID" })
    @EventIDField(links = { STAKEHOLDERS, }, params = { 0 })
    UNDO_LAST_BUILDING_ACTION(Integer.class),

    @EventParamData(editor = true, desc = "Activate/deactivate units of a specific type.", params = { "UnitDataOverride ID", "Activated" })
    @EventIDField(links = { UNIT_DATA_OVERRIDES }, params = { 0 })
    UNIT_TYPE_SET_ACTIVE(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set an attribute for a zone.", params = { "Zone ID", "Valid attribute name", "Attribute value" })
    @EventIDField(links = { ZONES }, params = { 0 })
    ZONE_SET_ATTRIBUTE(Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Set attributes to values for zones.", params = { "Zone IDs", "Valid attribute names",
            "Attribute values" })
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

        if (this == STAKEHOLDER_SELECT || this == MEASUREMENT_POINT_ADD || this == MEASUREMENT_LINE_ADD || this == MEASUREMENT_ITEM_ADD) {
            return Integer.class;
        } else if (this == SEARCH) {
            return HashMap.class;
        } else if (this == ACTION_LOG_EXPORT) {
            return byte[].class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public boolean triggerTestRun() {

        if (this == STAKEHOLDER_SELECT || this == STAKEHOLDER_RELEASE || this == SET_TRAFFIC_MULTIPLIER || this == DISMISS_ERROR
                || this == MEASUREMENT_LINE_ADD || this == MEASUREMENT_POINT_ADD || this == MEASUREMENT_ITEM_ADD
                || this == MEASUREMENT_REMOVE || this == MEASUREMENT_SAVE || this == MEASUREMENT_SET_OVERLAYS || this == MEASUREMENT_SET_SUM
                || this == SEARCH || this == SCENARIO_SET_ACTIVE) {
            return false;
        }
        return true;
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
