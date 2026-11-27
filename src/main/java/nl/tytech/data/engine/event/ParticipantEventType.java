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
import static nl.tytech.core.net.serializable.MapLink.ATTRIBUTE_ACTIONS;
import static nl.tytech.core.net.serializable.MapLink.BUILDINGS;
import static nl.tytech.core.net.serializable.MapLink.EVENT_BUNDLES;
import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.LEVEES;
import static nl.tytech.core.net.serializable.MapLink.MEASURES;
import static nl.tytech.core.net.serializable.MapLink.MONEY_TRANSFERS;
import static nl.tytech.core.net.serializable.MapLink.NET_CLUSTERS;
import static nl.tytech.core.net.serializable.MapLink.NET_LOADS;
import static nl.tytech.core.net.serializable.MapLink.PANELS;
import static nl.tytech.core.net.serializable.MapLink.PARAMETRIC_DESIGNS;
import static nl.tytech.core.net.serializable.MapLink.POPUPS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import static nl.tytech.core.net.serializable.MapLink.UPGRADE_TYPES;
import static nl.tytech.core.net.serializable.MapLink.ZONES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.event.Event.StartWithMyStakeholderEvent;
import nl.tytech.core.item.annotations.Description;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.MoneyTransfer;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.item.ParametricDesign.FunctionType;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.TimeState;

/**
 * ParticipantEventType
 *
 * These events can be called by Stakeholders participating in a session and must always start with their Stakeholder ID.
 *
 * @author Maxim Knepfle
 */
@Description("Endpoints for user actions (e.g., building construction) in a test run/impact Session")
public enum ParticipantEventType implements SessionEventTypeEnum, StartWithMyStakeholderEvent {

    @EventParamData(editor = true, desc = "Set an Action Menu active for a Stakeholder", params = { "Stakeholder", "Action Menu",
            "Active" })
    @EventIDField(links = { STAKEHOLDERS, ACTION_MENUS }, params = { 0, 1 })
    ACTION_MENU_SET_ACTIVE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Stakeholder applies an Attribute Action within a MultiPolygon", params = { "Stakeholder",
            "Attribute Action", "MultiPolygon", "Attributes", "Array of value arrays" })
    @EventIDField(links = { STAKEHOLDERS, ATTRIBUTE_ACTIONS }, params = { 0, 1 })
    ATTRIBUTE_ACTION_PLAN(Integer.class, Integer.class, MultiPolygon.class, String[].class, double[][].class),

    @EventParamData(editor = true, desc = "Stakeholder plans a generated Parametric Design", params = { "Stakeholder", "Parametric Design",
            "Function Types", "Plot Design IDs", "Array of MultiPolygons per Plot Design" }, response = "Building IDs")
    @EventIDField(links = { STAKEHOLDERS, PARAMETRIC_DESIGNS }, params = { 0, 1 })
    PARAMETRIC_DESIGN_PLAN(Integer.class, Integer.class, FunctionType[].class, Integer[].class, MultiPolygon[][].class),

    @EventParamData(desc = "Stakeholder plans a new Building of a Function with an amount of floors and a MultiPolygon", params = {
            "Stakeholder", "Function", "Amount of floors", "MultiPolygon" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, FUNCTIONS }, params = { 0, 1 })
    BUILDING_PLAN_CONSTRUCTION(ClientTerms.ACTION_LOG_BUILD, Integer.class, Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder plan the demolition of an owned Building", params = { "Stakeholder",
            "Building", }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_PLAN_DEMOLISH(ClientTerms.ACTION_LOG_DEMOLISH, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder plans the demolition of Building parts intersecting a MultiPolygon in a particular Layer", params = {
            "Stakeholder", "MultiPolygon", "Layer" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    BUILDING_PLAN_DEMOLISH_POLYGON(ClientTerms.ACTION_LOG_DEMOLISH, Integer.class, MultiPolygon.class, Building.Layer.class),

    @EventParamData(desc = "Stakeholder plans the upgrade of Buildings within a MultiPolygon", params = { "Stakeholder", "Upgrade Type",
            "MultiPolygon" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, UPGRADE_TYPES }, params = { 0, 1 })
    BUILDING_PLAN_UPGRADE(ClientTerms.ACTION_LOG_UPGRADE, Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder reverts demolished, changed and planned Buildings within a MultiPolygon back to their original state", params = {
            "Stakeholder", "MultiPolygon" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    BUILDING_REVERT_POLYGON(ClientTerms.ACTION_LOG_REVERT, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder applies server side events of an Event Bundle", params = { "Stakeholder",
            "Event Bundle" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, EVENT_BUNDLES }, params = { 0, 1 })
    EVENT_BUNDLE_APPLY_SERVER_EVENTS(ClientTerms.ACTION_LOG_APPLIED, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder plans a land purchase with in the MultiPolygon for a given price", params = { "Stakeholder",
            "MultiPolygon", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    MAP_BUY_LAND(Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Stakeholder plans a Levee for a MultiPolygon, an absolute or relative height and an angle", params = {
            "Stakeholder", "Levee", "MultiPolygon", "Height in meters", "Angle in degrees",
            "Height is relative" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, LEVEES }, params = { 0, 1 })
    MAP_LEVEE(ClientTerms.ACTION_LOG_LEVEE, Integer.class, Integer.class, MultiPolygon.class, Double.class, Double.class, Boolean.class),

    @EventParamData(desc = "Stakeholder lowers land and adds a waterway", params = { "Stakeholder",
            "MultiPolygon" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    MAP_LOWER_LAND(ClientTerms.ACTION_LOG_LOWER_LAND, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder raises land within the MultiPolygon by one meter and removes water", params = { "Stakeholder",
            "MultiPolygon" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    MAP_RAISE_LAND(ClientTerms.ACTION_LOG_RAISE_LAND, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder plans to sell owned land within the MultiPolygon to another Stakeholder for a given price", params = {
            "Stakeholder (seller)", "Stakeholder (receiver)", "MultiPolygon", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MAP_SELL_LAND(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(editor = true, desc = "Stakeholder cancels a planned Measure", params = { "Stakeholder",
            "Measure" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_CANCEL_CONSTRUCTION(ClientTerms.ACTION_LOG_MEASURE_CANCEL, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Stakeholder plans a Measure", params = { "Stakeholder", "Measure" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_PLAN_CONSTRUCTION(ClientTerms.ACTION_LOG_MEASURE_BUILD, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Stakeholder transfers money to another Stakeholder", params = { "Stakeholder (seller)",
            "Stakeholder (receiver)", "Type of transfer", "Transfer Message", "Amount" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MONEY_TRANSFER_GIVE(Integer.class, Integer.class, MoneyTransfer.Type.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Stakeholder sets an Attribute to a value, for each Net Load of a Net Cluster", params = {
            "Stakeholder", "Net Cluster", "Attribute", "Value" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS, }, params = { 0, 1 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTE(Integer.class, Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "Stakeholder sets set the value of the Attributes for each Net Load of a Net Cluster", params = {
            "Stakeholder", "Net Cluster", "Attributes", "Array of Value arrays" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS, }, params = { 0, 1 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTES(Integer.class, Integer.class, String[].class, double[][].class),

    @EventParamData(editor = true, desc = "Stakeholder sets the Net State for all Net Loads of a Net Cluster", params = { "Stakeholder",
            "Net Cluster", "Net State (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS }, params = { 0, 1 })
    NET_CLUSTER_SET_STATE(Integer.class, Integer.class, TimeState.class),

    @EventParamData(editor = true, desc = "Stakeholder sets the Net State for Net Loads of a Net Cluster and Net Type", params = {
            "Stakeholder", "Net Cluster", "Net Type",
            "Net State (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS }, params = { 0, 1 })
    NET_CLUSTER_SET_STATE_FOR_NET_TYPE(Integer.class, Integer.class, NetType.class, TimeState.class),

    @EventParamData(editor = true, desc = "Stakeholder sets the Net State for all Net Loads of a Net Cluster, per Net Type", params = {
            "Stakeholder", "Net Cluster", "Net Types",
            "Net States (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS }, params = { 0, 1 })
    NET_CLUSTER_SET_STATES(Integer.class, Integer.class, NetType[].class, TimeState[].class),

    @EventParamData(desc = "Stakeholder selects an answer in a Panel", params = { "Stakeholder", "Panel", "Answer ID" })
    @EventIDField(links = { STAKEHOLDERS, PANELS }, params = { 0, 1 })
    PANEL_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder answers a Popup", params = { "Stakeholder", "Popup", "Answer ID" })
    @EventIDField(links = { STAKEHOLDERS, POPUPS }, params = { 0, 1 })
    POPUP_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder answers a Popup with an additional date", params = { "Stakeholder", "Popup", "Answer ID",
            "Time in milliseconds long from epoch" })
    @EventIDField(links = { STAKEHOLDERS, POPUPS }, params = { 0, 1 })
    POPUP_ANSWER_WITH_DATE(Integer.class, Integer.class, Integer.class, Long.class),

    @EventParamData(desc = "Stakeholder creates a Measure that removes water Terrains and flatten its land using a MultiPolygon", params = {
            "Stakeholder", "MultiPolygon" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    RESTORE_LAND(ClientTerms.ACTION_LOG_RESTORED_LAND, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder's view is moved to the location in the 3D Visualization", params = { "Stakeholder", "Point",
            "Ping others (show them my location)" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    STAKEHOLDER_SET_LOCATION(Integer.class, Point.class, Boolean.class),

    @EventParamData(desc = "Stakeholder adds an allowed Function Category to a Zone", params = { "Stakeholder", "Zone", "Category" })
    @EventIDField(links = { STAKEHOLDERS, ZONES, }, params = { 0, 1 })
    ZONE_ADD_FUNCTION_CATEGORY(Integer.class, Integer.class, Category.class),

    @EventParamData(desc = "Stakeholder removes an allowed Function Category from a Zone", params = { "Stakeholder", "Zone", "Category" })
    @EventIDField(links = { STAKEHOLDERS, ZONES, }, params = { 0, 1 })
    ZONE_REMOVE_FUNCTION_CATEGORY(Integer.class, Integer.class, Category.class),

    @EventParamData(desc = "Stakeholder sets the allowed Function Categories per specified Zone", params = { "Stakeholder", "Zones",
            "Array of Category Arrays" })
    @EventIDField(links = { STAKEHOLDERS, ZONES, }, params = { 0, 1 })
    ZONES_SET_CATEGORIES(Integer.class, Integer[].class, Category[][].class);

    private final List<Class<?>> classes;

    private final ClientTerms term;

    private ParticipantEventType(Class<?>... classes) {
        this.term = null;
        this.classes = Arrays.asList(classes);
    }

    private ParticipantEventType(ClientTerms term, Class<?>... classes) {
        this.term = term;
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

    public ClientTerms getClientTerm() {
        return term;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {

        return switch (this) {

            case PARAMETRIC_DESIGN_PLAN -> Integer[].class;

            case BUILDING_PLAN_CONSTRUCTION, BUILDING_PLAN_DEMOLISH, BUILDING_PLAN_DEMOLISH_POLYGON -> Integer.class;

            case BUILDING_PLAN_UPGRADE, BUILDING_REVERT_POLYGON -> Integer.class;

            case MEASURE_PLAN_CONSTRUCTION, MEASURE_CANCEL_CONSTRUCTION -> Integer.class;

            case MAP_LEVEE, MAP_LOWER_LAND, MAP_RAISE_LAND, RESTORE_LAND -> Integer.class;

            case EVENT_BUNDLE_APPLY_SERVER_EVENTS -> Integer.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public boolean triggerTestRun() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {

        return switch (this) {

            case MEASURE_PLAN_CONSTRUCTION, MEASURE_CANCEL_CONSTRUCTION, RESTORE_LAND, MAP_LEVEE, MAP_RAISE_LAND, MAP_LOWER_LAND -> MEASURES;

            case ZONE_ADD_FUNCTION_CATEGORY, ZONE_REMOVE_FUNCTION_CATEGORY, ZONES_SET_CATEGORIES -> ZONES;

            case BUILDING_PLAN_CONSTRUCTION, BUILDING_PLAN_DEMOLISH, BUILDING_PLAN_DEMOLISH_POLYGON, BUILDING_PLAN_UPGRADE, BUILDING_REVERT_POLYGON -> BUILDINGS;

            case PARAMETRIC_DESIGN_PLAN -> PARAMETRIC_DESIGNS;

            // note: forces load update not cluster
            case NET_CLUSTER_SET_LOAD_ATTRIBUTE, NET_CLUSTER_SET_LOAD_ATTRIBUTES, NET_CLUSTER_SET_STATE, //
                    NET_CLUSTER_SET_STATE_FOR_NET_TYPE, NET_CLUSTER_SET_STATES -> NET_LOADS;

            case MONEY_TRANSFER_GIVE -> MONEY_TRANSFERS;

            default -> null;
        };
    }
}
