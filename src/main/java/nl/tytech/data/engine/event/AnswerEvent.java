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

import static nl.tytech.core.net.serializable.MapLink.BUILDINGS;
import static nl.tytech.core.net.serializable.MapLink.MEASURES;
import static nl.tytech.core.net.serializable.MapLink.NET_CLUSTERS;
import static nl.tytech.core.net.serializable.MapLink.NET_LOADS;
import static nl.tytech.core.net.serializable.MapLink.POPUPS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.Description;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.serializable.MapLink;

/**
 * AnswerEvent: It is used to trigger events that are given within the popup's answer.
 *
 * @author Maxim Knepfle
 */
@Description("Endpoints related to answering popups in a TestRun or impact Session.")
public enum AnswerEvent implements SessionEventTypeEnum {

    @EventParamData(desc = "Stakeholder planned the construction date for a building via a popup.", params = {
            "Stakeholder ID", "Building ID", "Popup ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, POPUPS }, params = { 0, 1, 2 })
    BUILDING_ASK_CONSTRUCTION_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder planned the demolition date for a building via a popup.", params = {
            "Stakeholder ID", "Building ID", "Popup ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, POPUPS }, params = { 0, 1, 2 })
    BUILDING_ASK_DEMOLISH_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves the construction of a building.", params = { "Stakeholder ID", "Building ID", "Approves" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms the continuation of the planned construction", params = { "Stakeholder ID",
            "Building ID", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms that the planned construction is not approved", params = { "Stakeholder ID",
            "Building ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves the demolition of a building.", params = { "Stakeholder ID", "Building ID", "Approves" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_DEMOLISH_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms the continuation of the planned demolition", params = { "Stakeholder ID",
            "Building ID", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_DEMOLISH_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of building confirms that the planned demolition is not approved", params = { "Stakeholder ID",
            "Building ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_DEMOLISH_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Owner of buildings confirms the continuation of the planned construction", params = { "Stakeholder ID",
            "Building IDs", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDINGS_CONSTRUCTION_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Owner of buildings confirms the continuation of the planned demolition", params = { "Stakeholder ID",
            "Building IDs", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDINGS_DEMOLISH_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Deletes a popup; if the ID is invalid, it is ignored.", params = { "Popup ID" })
    DELETE_POPUP(Integer[].class),

    @EventParamData(desc = "Selling Stakeholder accepted selling the specified land at a given price per square meter to Buying Stakeholder.", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_BUY_APROVED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Selling Stakeholder refused selling the specified land at a given price per square meter to Buying Stakeholder.", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_BUY_REFUSED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buying Stakeholder accepted buying the specified land at a given price per square meter from Selling Stakeholder.", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_SELL_APROVED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buying Stakeholder refused buying the specified land at a given price per square meter from Selling Stakeholder.", params = {
            "Buying Stakeholder ID", "Selling Stakeholder ID", "MultiPolygon describing the land contour" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_SELL_REFUSED(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder planned the construction date for a measure via a popup.", params = {
            "Stakeholder ID", "Measure ID", "Popup ID" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES, POPUPS }, params = { 0, 1, 2 })
    MEASURE_ASK_CONSTRUCTION_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves the construction of a measure.", params = { "Stakeholder ID", "Measure ID",
            "Approving Stakeholder ID", "Approves" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES, STAKEHOLDERS }, params = { 0, 1, 2 })
    MEASURE_CONSTRUCTION_APPROVAL(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of measure confirms the continuation of the planned construction", params = { "Stakeholder ID",
            "Measure ID", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_CONSTRUCTION_APPROVAL_CONFIRM(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of measure confirms that the planned measure is not approved", params = { "Stakeholder ID",
            "Measure ID" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES, }, params = { 0, 1 })
    MEASURE_CONSTRUCTION_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Owner confirms the zoning permit for a measure.", params = { "Measure ID" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    MEASURE_CONSTRUCTION_ZONING_APPROVAL_CONFIRM(Integer.class),

    @EventParamData(desc = "Zoning plan permitter denied the measure", params = { "Stakeholder ID", "Measure ID" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_ZONING_PERMIT_DENIED(Integer.class, Integer.class),

    @EventParamData(desc = "Accepts the connection of a network cluster.", params = { "NetCluster ID" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_ACCEPT_CONNECT(Integer.class),

    @EventParamData(desc = "Cancels the connection of a network cluster.", params = { "NetCluster ID" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_CANCEL_CONNECT(Integer.class),

    @EventParamData(desc = "Connects a network cluster as a consumer.", params = { "NetCluster ID" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_CONSUMER_CONNECT(Integer.class),

    @EventParamData(desc = "Connects a network cluster as a producer.", params = { "NetCluster ID" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_PRODUCER_CONNECT(Integer.class),

    @EventParamData(desc = "Rejects the connection of a network cluster.", params = { "NetCluster ID" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_REJECT_CONNECT(Integer.class),

    @EventParamData(desc = "Stakeholder approves the upgrade construction of a building.", params = { "Stakeholder ID", "Building ID",
            "Approves" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    UPGRADE_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Upgrading Stakeholder confirms the continuation of the planned upgrade", params = { "Stakeholder ID",
            "Building ID", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADE_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Stakeholder planned the upgrade date for a building via a popup.", params = {
            "Stakeholder ID", "Building ID", "Popup ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, POPUPS }, params = { 0, 1, 2 })
    UPGRADE_ASK_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder's upgrade has received a zoning permit.", params = { "Stakeholder ID", "Building ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADE_ZONING_PERMIT_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder's upgrade is denied based on zoning.", params = { "Stakeholder ID", "Building ID" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADE_ZONING_PERMIT_DENIED(Integer.class, Integer.class),

    @EventParamData(desc = "Upgrading Stakeholder confirms the continuation of the planned upgrades", params = { "Stakeholder ID",
            "Building IDs", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADES_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class);

    private final List<Class<?>> classes;

    private AnswerEvent(Class<?>... classes) {
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
        return null;
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
            case LAND_SELL_APROVED, LAND_BUY_APROVED, LAND_BUY_REFUSED, LAND_SELL_REFUSED -> STAKEHOLDERS;
            // note: forces load update not cluster
            case NET_ACCEPT_CONNECT, NET_CANCEL_CONNECT, NET_CONSUMER_CONNECT, NET_PRODUCER_CONNECT, NET_REJECT_CONNECT -> NET_LOADS;
            case MEASURE_CONSTRUCTION_APPROVAL, MEASURE_ASK_CONSTRUCTION_DATE, MEASURE_CONSTRUCTION_APPROVAL_CONFIRM, MEASURE_CONSTRUCTION_DENIED_CONFIRMED, MEASURE_CONSTRUCTION_ZONING_APPROVAL_CONFIRM, MEASURE_ZONING_PERMIT_DENIED -> MEASURES;
            case DELETE_POPUP -> null;
            default -> BUILDINGS; // includes all remaining building and upgrade events
        };
    }

}
