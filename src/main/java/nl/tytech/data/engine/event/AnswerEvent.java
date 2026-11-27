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

    @EventParamData(desc = "Stakeholder planned the construction date for a Building via a Popup", params = { "Stakeholder", "Building",
            "Popup" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, POPUPS }, params = { 0, 1, 2 })
    BUILDING_ASK_CONSTRUCTION_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder planned the demolition date for a Building via a Popup", params = { "Stakeholder", "Building",
            "Popup" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, POPUPS }, params = { 0, 1, 2 })
    BUILDING_ASK_DEMOLISH_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves the construction of a Building", params = { "Stakeholder", "Building", "Approves" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of Building confirms the continuation of the planned construction", params = { "Stakeholder", "Building",
            "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of Building confirms that the planned construction is not approved", params = { "Stakeholder",
            "Building" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDING_CONSTRUCTION_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves the demolition of a Building", params = { "Stakeholder", "Building", "Approves" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_DEMOLISH_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of Building confirms the continuation of the planned demolition", params = { "Stakeholder", "Building",
            "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_DEMOLISH_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of Building confirms that the planned demolition is not approved", params = { "Stakeholder", "Building" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDING_DEMOLISH_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Owner of Buildings confirms the continuation of the planned construction", params = { "Stakeholder",
            "Buildings", "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    BUILDINGS_CONSTRUCTION_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Owner of Buildings confirms the continuation of the planned demolition", params = { "Stakeholder", "Buildings",
            "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    BUILDINGS_DEMOLISH_APPROVAL_CONFIRMED(Integer.class, Integer[].class, Boolean.class),

    @EventParamData(desc = "Deletes a Popup; invalid ID are ignored", params = { "Popups" })
    DELETE_POPUP(Integer[].class),

    @EventParamData(desc = "Selling Stakeholder accepted selling the specified land at a given price per square meter to Buying Stakeholder", params = {
            "Stakeholder (buying)", "Stakeholder (selling)", "MultiPolygon", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_BUY_APROVED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Selling Stakeholder refused selling the specified land at a given price per square meter to Buying Stakeholder", params = {
            "Stakeholder (buying)", "Stakeholder (selling)", "MultiPolygon", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_BUY_REFUSED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buying Stakeholder accepted buying the specified land at a given price per square meter from Selling Stakeholder", params = {
            "Stakeholder (buying)", "Stakeholder (selling)", "MultiPolygon", "Price per square meter" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_SELL_APROVED(Integer.class, Integer.class, MultiPolygon.class, Double.class),

    @EventParamData(desc = "Buying Stakeholder refused buying the specified land at a given price per square meter from Selling Stakeholder", params = {
            "Stakeholder (buying)", "Stakeholder (selling)", "MultiPolygon" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    LAND_SELL_REFUSED(Integer.class, Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Stakeholder planned the construction date for a Measure via a Popup", params = { "Stakeholder", "Measure",
            "Popup" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES, POPUPS }, params = { 0, 1, 2 })
    MEASURE_ASK_CONSTRUCTION_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder approves the construction of a Measure", params = { "Stakeholder", "Measure",
            "Stakeholder (approving)", "Approves" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES, STAKEHOLDERS }, params = { 0, 1, 2 })
    MEASURE_CONSTRUCTION_APPROVAL(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of Measure confirms the continuation of the planned construction", params = { "Stakeholder", "Measure",
            "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_CONSTRUCTION_APPROVAL_CONFIRM(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Owner of Measure confirms that the planned Measure is not approved", params = { "Stakeholder", "Measure" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES, }, params = { 0, 1 })
    MEASURE_CONSTRUCTION_DENIED_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Owner confirms the zoning permit for a Measure", params = { "Measure" })
    @EventIDField(links = { MEASURES }, params = { 0 })
    MEASURE_CONSTRUCTION_ZONING_APPROVAL_CONFIRM(Integer.class),

    @EventParamData(desc = "Zoning plan permitter denied the Measure", params = { "Stakeholder", "Measure" })
    @EventIDField(links = { STAKEHOLDERS, MEASURES }, params = { 0, 1 })
    MEASURE_ZONING_PERMIT_DENIED(Integer.class, Integer.class),

    @EventParamData(desc = "Accepts the connection of a Net Cluster", params = { "Net Cluster" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_ACCEPT_CONNECT(Integer.class),

    @EventParamData(desc = "Cancels the connection of a Net Cluster", params = { "Net Cluster" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_CANCEL_CONNECT(Integer.class),

    @EventParamData(desc = "Connects a Net Cluster as a consumer", params = { "Net Cluster" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_CONSUMER_CONNECT(Integer.class),

    @EventParamData(desc = "Connects a Net Cluster as a producer", params = { "Net Cluster" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_PRODUCER_CONNECT(Integer.class),

    @EventParamData(desc = "Rejects the connection of a Net Cluster", params = { "Net Cluster" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    NET_REJECT_CONNECT(Integer.class),

    @EventParamData(desc = "Stakeholder approves the Upgrade construction of a Building", params = { "Stakeholder", "Building",
            "Approves" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, }, params = { 0, 1 })
    UPGRADE_APPROVAL(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Upgrading Stakeholder confirms the continuation of the planned Upgrade", params = { "Stakeholder", "Building",
            "Confirms" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADE_APPROVAL_CONFIRMED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Stakeholder planned the Upgrade date for a Building via a Popup", params = { "Stakeholder", "Building",
            "Popup" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS, POPUPS }, params = { 0, 1, 2 })
    UPGRADE_ASK_DATE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder's planned Upgrade has received a zoning permit", params = { "Stakeholder", "Building" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADE_ZONING_PERMIT_CONFIRMED(Integer.class, Integer.class),

    @EventParamData(desc = "Stakeholder's planned Upgrade is denied based on zoning", params = { "Stakeholder", "Building" })
    @EventIDField(links = { STAKEHOLDERS, BUILDINGS }, params = { 0, 1 })
    UPGRADE_ZONING_PERMIT_DENIED(Integer.class, Integer.class),

    @EventParamData(desc = "Upgrading Stakeholder confirms the continuation of the planned Upgrades", params = { "Stakeholder", "Buildings",
            "Confirms" })
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

            case MEASURE_CONSTRUCTION_APPROVAL, MEASURE_ASK_CONSTRUCTION_DATE, MEASURE_CONSTRUCTION_APPROVAL_CONFIRM, //
                    MEASURE_CONSTRUCTION_DENIED_CONFIRMED, MEASURE_CONSTRUCTION_ZONING_APPROVAL_CONFIRM, MEASURE_ZONING_PERMIT_DENIED -> MEASURES;

            case DELETE_POPUP -> null;

            default -> BUILDINGS; // includes all remaining Building and Upgrade events
        };
    }

}
