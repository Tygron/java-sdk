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
import nl.tytech.core.item.annotations.ClassDescription;
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
 * ParticipantEventType: These events can be called by participants playing the session and must always start with a Stakeholder ID.
 *
 * @author Maxim Knepfle
 */
@ClassDescription("Events for user actions (e.g. build building) in a testrun/impact Session.")
public enum ParticipantEventType implements SessionEventTypeEnum, StartWithMyStakeholderEvent {

    @EventParamData(editor = true, desc = "Set a category active for a stakeholder.", params = { "Stakeholder ID", "ActionMenu ID",
            "Active" })
    @EventIDField(links = { STAKEHOLDERS, ACTION_MENUS }, params = { 0, 1 })
    ACTION_MENU_SET_ACTIVE(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Apply an Attribute Action.", params = { "Stakeholder ID", "AttributeAction ID", "MultiPolygon",
            "Attribute names", "Attribute value arrays" })
    @EventIDField(links = { STAKEHOLDERS, ATTRIBUTE_ACTIONS }, params = { 0, 1 })
    ATTRIBUTE_ACTION_PLAN(Integer.class, Integer.class, MultiPolygon.class, String[].class, double[][].class),

    @EventIDField(links = { STAKEHOLDERS, PARAMETRIC_DESIGNS }, params = { 0, 1 })
    PARAMETRIC_DESIGN_PLAN(Integer.class, Integer.class, FunctionType[].class, Integer[].class, MultiPolygon[][].class),

    @EventParamData(desc = "Plan a new building in the MAQUETTE map.", params = { "Stakeholder ID", "Function ID", "Amount of floors",
            "MultiPolygon describing the build contour" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, FUNCTIONS }, params = { 0, 1 })
    BUILDING_PLAN_CONSTRUCTION(ClientTerms.ACTION_LOG_BUILD, Integer.class, Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Plan the demolition of a building in the MAQUETTE map.", params = { "Stakeholder ID (also owner)",
            "Building ID", }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_PLAN_DEMOLISH(ClientTerms.ACTION_LOG_DEMOLISH, Integer.class, Integer.class),

    @EventParamData(desc = "Plan the demolition of a buildings in the given polygon in the MAQUETTE map.", params = { "Stakeholder ID",
            "Multipolygon describing the demolition area", "Ground type: SURFACE or UNDERGROUND" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    BUILDING_PLAN_DEMOLISH_POLYGON(ClientTerms.ACTION_LOG_DEMOLISH, Integer.class, MultiPolygon.class, Building.Layer.class),

    @EventParamData(desc = "Plan the upgrade of buildings in the given polygon in the MAQUETTE map.", params = { "Stakeholder ID",
            "Upgrade Type ID", "Multipolygon describing the upgrade area" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, UPGRADE_TYPES }, params = { 0, 1 })
    BUILDING_PLAN_UPGRADE(ClientTerms.ACTION_LOG_UPGRADE, Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Revert polygon to orginal CURRENT map situation.", params = { "Stakeholder ID",
            "Multipolygon describing the to be reverted area" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    BUILDING_REVERT_POLYGON(ClientTerms.ACTION_LOG_REVERT, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Apply a bundle of events on the serverside for a particular stakeholder.", params = { "Stakeholder ID",
            "Eventbundle ID" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, EVENT_BUNDLES }, params = { 0, 1 })
    EVENT_BUNDLE_APPLY_SERVER_EVENTS(ClientTerms.ACTION_LOG_APPLIED, Integer.class, Integer.class),

    @EventParamData(desc = "Buy the land definied by the polygon for given price.", params = { "Stakeholder ID",
            "Multipolygon describing the area to be bought", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    MAP_BUY_LAND(Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Create a Levee with a given height and area.", params = { "Stakeholder ID", "Levee ID",
            "Multipolygon describing the surface area", "Height in meters", "Angle in degrees",
            "Is height relative" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, LEVEES }, params = { 0, 1 })
    MAP_LEVEE(ClientTerms.ACTION_LOG_LEVEE, Integer.class, Integer.class, MultiPolygon.class, Double.class, Double.class, Boolean.class),

    @EventParamData(desc = "Lower land to create open water.", params = { "Stakeholder ID",
            "Multipolygon describing the lowered area" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    MAP_LOWER_LAND(ClientTerms.ACTION_LOG_LOWER_LAND, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Raise land by one length unit.", params = { "Stakeholder ID",
            "Multipolygon describing the raised area" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    MAP_RAISE_LAND(ClientTerms.ACTION_LOG_RAISE_LAND, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Buy the land definied by the polygon for given price.", params = { "Land owner", "Proposed buyer of the land",
            "Multipolygon describing the area to be sold", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MAP_SELL_LAND(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(editor = true, desc = "Cancel a measure planned by an Stakeholder while in pre-construction phase.", params = {
            "Stakeholder ID", "Measure ID" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_CANCEL_CONSTRUCTION(ClientTerms.ACTION_LOG_MEASURE_CANCEL, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Plan construction of a measure by an Stakeholder that is not yet planned.", params = {
            "Stakeholder ID", "Measure ID" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_PLAN_CONSTRUCTION(ClientTerms.ACTION_LOG_MEASURE_BUILD, Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Give money from one stakeholder to another.", params = { "Initiating Stakeholder ID",
            "Money receiving Stakeholder ID", "Type of transfer", "Message text", "Amount" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    MONEY_TRANSFER_GIVE(Integer.class, Integer.class, MoneyTransfer.Type.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "For each NetLoad of a NetCluster, set the value of the attribute.", params = {
            "Initiating Stakeholder ID", "NetCluster ID", "Attribute name", "Value" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS, }, params = { 0, 1 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTE(Integer.class, Integer.class, String.class, Double.class),

    @EventParamData(editor = true, desc = "For each NetLoad of a NetCluster, set the value of the attribute.", params = {
            "Initiating Stakeholder ID", "NetCluster ID", "Attribute name", "Value" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS, }, params = { 0, 1 })
    NET_CLUSTER_SET_LOAD_ATTRIBUTES(Integer.class, Integer.class, String[].class, double[][].class),

    @EventParamData(editor = true, desc = "Set TimeState for all Net Loads of Net Cluster.", params = { "Initiating Stakeholder ID",
            "NetCluster ID", "Time State (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS }, params = { 0, 1 })
    NET_CLUSTER_SET_STATE(Integer.class, Integer.class, TimeState.class),

    @EventParamData(editor = true, desc = "Set TimeState for Net Loads of Net Cluster, based on NetType.", params = {
            "Initiating Stakeholder ID", "NetCluster ID", "Net Type",
            "TimeState (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS }, params = { 0, 1 })
    NET_CLUSTER_SET_STATE_FOR_NET_TYPE(Integer.class, Integer.class, NetType.class, TimeState.class),

    @EventParamData(editor = true, desc = "Set TimeState for all Net Loads of Net Cluster.", params = { "Initiating Stakeholder ID",
            "NetCluster ID", "NetType Array", "Time State Array (NOTHING, REQUEST_CONSTRUCTION_APPROVAL, REQUEST_ZONING_APPROVAL, READY)" })
    @EventIDField(links = { STAKEHOLDERS, NET_CLUSTERS }, params = { 0, 1 })
    NET_CLUSTER_SET_STATES(Integer.class, Integer.class, NetType[].class, TimeState[].class),

    @EventParamData(desc = "Stakeholder has selected an answer in a panel.", params = { "Stakeholder ID", "Panel ID", "Answer ID" })
    @EventIDField(links = { STAKEHOLDERS, PANELS }, params = { 0, 1 })
    PANEL_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder answers a popup.", params = { "Stakeholder ID", "Popup ID", "Answer ID" })
    @EventIDField(links = { STAKEHOLDERS, POPUPS }, params = { 0, 1 })
    POPUP_ANSWER(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder answers a popup with an additional date.", params = { "Stakeholder ID", "Message ID", "Answer ID",
            "Date in milliseconds long from epoch" })
    @EventIDField(links = { STAKEHOLDERS, POPUPS }, params = { 0, 1 })
    POPUP_ANSWER_WITH_DATE(Integer.class, Integer.class, Integer.class, Long.class),

    @EventParamData(desc = "Stakeholder restores land back to orginal state.", params = { "Stakeholder ID",
            "Area to be restored" }, response = "Action Log ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    RESTORE_LAND(ClientTerms.ACTION_LOG_RESTORED_LAND, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Set the location of a Stakeholder in the 3D world.", params = { "Stakeholder ID",
            "Location of the Stakeholder on the map as Point", "Ping others (show them my location)" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    STAKEHOLDER_SET_LOCATION(Integer.class, Point.class, Boolean.class),

    @EventParamData(desc = "Add a FunctionCategory that is allowed to be built within a zone.", params = { "Stakeholder ID", "Zone ID",
            "FunctionCategory" })
    @EventIDField(links = { STAKEHOLDERS, ZONES, }, params = { 0, 1 })
    ZONE_ADD_FUNCTION_CATEGORY(Integer.class, Integer.class, Category.class),

    @EventParamData(desc = "Remove a FunctionCategory so it cannot be build within the zone.", params = { "Stakeholder ID", "Zone ID",
            "FunctionCategory" })
    @EventIDField(links = { STAKEHOLDERS, ZONES, }, params = { 0, 1 })
    ZONE_REMOVE_FUNCTION_CATEGORY(Integer.class, Integer.class, Category.class),

    @EventParamData(desc = "Sets the allowed building categories for specified zones.", params = { "Stakeholder ID", "Zone IDs",
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

        if (this == PARAMETRIC_DESIGN_PLAN) {
            return Integer[].class;
        }
        return isAction() ? Integer.class : null;
    }

    private boolean isAction() {
        return this == BUILDING_PLAN_CONSTRUCTION || this == BUILDING_PLAN_DEMOLISH || this == BUILDING_PLAN_DEMOLISH_POLYGON
                || this == BUILDING_PLAN_UPGRADE || this == MEASURE_PLAN_CONSTRUCTION || this == MEASURE_CANCEL_CONSTRUCTION
                || this == MAP_RAISE_LAND || this == MAP_LOWER_LAND || this == EVENT_BUNDLE_APPLY_SERVER_EVENTS || this == RESTORE_LAND
                || this == BUILDING_REVERT_POLYGON || this == MAP_LEVEE;
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
            case NET_CLUSTER_SET_LOAD_ATTRIBUTE, NET_CLUSTER_SET_LOAD_ATTRIBUTES, NET_CLUSTER_SET_STATE, NET_CLUSTER_SET_STATE_FOR_NET_TYPE, NET_CLUSTER_SET_STATES -> NET_LOADS;
            case MONEY_TRANSFER_GIVE -> MONEY_TRANSFERS;
            default -> null;
        };
    }
}
