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

import static nl.tytech.core.net.serializable.MapLink.ADDRESSES;
import static nl.tytech.core.net.serializable.MapLink.BUILDINGS;
import static nl.tytech.core.net.serializable.MapLink.NET_FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.NET_LOADS;
import static nl.tytech.core.net.serializable.MapLink.NET_NODES;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.TimeState;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(NET_LOADS)
public enum EditorNetLoadEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new Net Load for a Net Type and optionally set as Root Load", params = { "Net Type",
            "Is Root Load" }, response = "Net Load ID")
    ADD(NetType.class, Boolean.class),

    @EventParamData(desc = "Assign an Address to a Net Load", params = { "Net Loads", "Addresses" })
    @EventIDField(sameLength = true, links = { NET_LOADS, ADDRESSES }, params = { 0, 1 })
    ADD_ADDRESS(Integer[].class, Integer[].class),

    @EventParamData(desc = "Add a new Net Node for a point and assign it to a Net Load", params = { "Net Load", "Point" })
    @EventIDField(links = { NET_LOADS }, params = { 0 })
    ADD_AND_SET_NODE(Integer.class, Point.class),

    @EventParamData(desc = "Generate Net Loads with a provided Net Type, " + "for Addresses of Buildings inside the provided polygons, "
            + "for Buildings of the provided category and Stakeholders and optionally only for Buildings without Net Loads. "
            + "Addresses are bundled into one Net Load given a bundle threshold. "
            + "Net Loads are set as Collective given another threshold. "
            + "Stakeholder owning the Net Load can be mapped from the Building owner to a different one using the mappingIDs", params = {
                    "Net Type", "Region", "Building Categories", "Bundle Threshold", "Collective Threshold",
                    "Stakeholders (building owners)", "Stakeholders (mapped)", "Skip Buildings with Net Loads" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 5, 6 })
    GENERATE_LOADS(NetType.class, MultiPolygon.class, Category[].class, Integer.class, Integer.class, Integer[].class, Integer[].class,
            Boolean.class),

    @EventParamData(desc = "Generate Net Loads and Net Lines given a Net Function, "
            + "for Addresses of Buildings inside the provided polygons, "
            + "for Buildings of the provided category and Stakeholders and optionally only for Buildings without Net Loads. "
            + "Addresses are bundled into one Net Load given a bundle threshold. "
            + "Net Loads are set as Collective given another threshold. "
            + "Stakeholder owning the Net Load can be mapped from the Building owner to a different one using the mappingIDs. "
            + "Net Lines are generated with a Search Range for paths to existing Net Nodes and a Merge Range to combine Net Nodes", params = {
                    "Net Function", "Region", "Building Categories", "Bundle Threshold", "Collective Threshold",
                    "Stakeholders (building owners)", "Stakeholders (mapped)", "Skip Buildings with Net Loads", "Search Range",
                    "Merge Range" })
    @EventIDField(links = { NET_FUNCTIONS, STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 5, 6 })
    GENERATE_LOADS_AND_PATHS(Integer.class, MultiPolygon.class, Category[].class, Integer.class, Integer.class, Integer[].class,
            Integer[].class, Boolean.class, Double.class, Double.class),

    @EventParamData(desc = "Connect Net Loads to an existing Net by adding Net Lines with the provided Net Function. "
            + "Net Lines are generated with a Search Range for paths to existing Net Nodes and a Merge Range to combine Net Nodes", params = {
                    "Net Function", "Net Loads", "Search Range", "Merge Range" })
    @EventIDField(links = { NET_FUNCTIONS, NET_LOADS }, params = { 0, 1 })
    GENERATE_PATHS(Integer.class, Integer[].class, Double.class, Double.class),

    /**
     * loadGeometries, loadNames, loadNetTypes, timeStates, concatenated loadAddresses, loadAttributeNames, loadAttributeValues, root
     * Geometry Index, buildingSelectRange, nodeSelectRange, sourceID.
     */
    @EventParamData(desc = "Import a Collection of Net Load geometries, names, Net Types, Net State, to an existing Net "
            + "by adding Net Lines with the provided Net Function. " + "A Building is assigned to a Net Load when within Building range. "
            + "A Net Load is assigned to a Net Node when within Net Node range", params = { "Net Load Geometries", "Names", "Net Types",
                    "Net State", "Concateneted Addresses", "Attributes", "Attribute Values array", "Index of Root Load Geometry",
                    "Building Range", "Net Node Range", "Source" })
    @EventIDField(links = { SOURCES }, params = { 10 })
    IMPORT(GeometryCollection.class, String[].class, NetType[].class, TimeState[].class, //
            String[].class, String[].class, double[][].class, Integer.class, Double.class, Double.class, Integer.class),

    /**
     * Import a net using:
     *
     * lineDefinitionGeometries, lineDefinitionNames, lineDefinitionNetTypes, ineDefinitionAttributeNames, lineDefinitionAttributeValues,
     *
     * lineGeometries, lineNames, lineNetTypes, lineAttributeNames, lineAttributeValues,
     *
     * loadGeometries, loadNames, loadNetTypes, timeStates, concatenated loadAddresses, LoadClusterIDs, loadAttributeNames,
     * loadAttributeValues, buildingSelectRange, nodeSelectRange.
     *
     */
    IMPORT_NET(//
            GeometryCollection.class, String[].class, NetType[].class, String[].class, double[][].class, //
            GeometryCollection.class, String[].class, NetType[].class, String[].class, double[][].class, //
            GeometryCollection.class, String[].class, NetType[].class, TimeState[].class, Boolean[].class, String[].class, Integer[].class,
            String[].class, double[][].class, Double.class, Double.class),

    @EventIDField(links = { NET_LOADS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove an Address from a Net Load", params = { "Net Loads", "Addresses" })
    @EventIDField(links = { NET_LOADS, ADDRESSES }, params = { 0, 1 })
    REMOVE_ADDRESS(Integer[].class, Integer[].class),

    @EventParamData(desc = "Remove all Attributes of a Net Load", params = { "Net Loads" })
    @EventIDField(links = { NET_LOADS }, params = { 0 })
    REMOVE_ALL_ATTRIBUTES(Integer[].class),

    @EventIDField(links = { NET_LOADS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Assign a Net Load to no particular Building", params = { "Net Load" })
    @EventIDField(links = { NET_LOADS }, params = { 0 })
    REMOVE_BUILDING(Integer.class),

    @EventParamData(desc = "Assign the Net Load to no particular Net Node", params = { "Net Load" })
    @EventIDField(links = { NET_LOADS }, params = { 0 })
    REMOVE_NODE(Integer.class),

    @EventParamData(params = { "Net Loads", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { NET_LOADS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Net Loads", "Attribute Name", "Numeric Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { NET_LOADS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Net Loads", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { NET_LOADS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Assign a Net Load to a particular Building", params = { "Net Load", "Building" })
    @EventIDField(links = { NET_LOADS, BUILDINGS }, params = { 0, 1 })
    SET_BUILDING(Integer.class, Integer.class),

    @EventParamData(desc = "Set the name of a Net Load", params = { "Net Loads", "Names" })
    @EventIDField(sameLength = true, links = { NET_LOADS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the Net Type of a Net Load", params = { "Net Load", "Net Type" })
    @EventIDField(links = { NET_LOADS }, params = { 0 })
    SET_NET_TYPE(Integer.class, NetType.class),

    @EventParamData(desc = "Set the Net Node of a Net Load", params = { "Net Load", "Net Node" })
    @EventIDField(links = { NET_LOADS, NET_NODES }, params = { 0, 1 })
    SET_NODE(Integer.class, Integer.class),

    @EventParamData(desc = "Set the Location of a Net Load", params = { "Net Load", "Point" })
    @EventIDField(links = { NET_LOADS }, params = { 0 }, nullable = { 1 })
    SET_POINT(Integer.class, Point.class),

    @EventParamData(desc = "Set the Net State of a Net Load", params = { "Net Loads", "Time States" })
    @EventIDField(sameLength = true, links = { NET_LOADS }, params = { 0 })
    SET_STATE(Integer[].class, TimeState[].class);

    private final List<Class<?>> classes;

    private EditorNetLoadEventType(Class<?>... classes) {
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

            case ADD -> Integer.class;

            case REMOVE, SET_NAME, SET_ATTRIBUTE -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return NET_LOADS;
    }
}
