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
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.AttributeItem.ReservedAttribute;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.item.NetLoad.LoadAttribute;
import nl.tytech.data.engine.item.NetLoad.LoadType;
import nl.tytech.data.engine.other.AttributeQueryInterface;
import nl.tytech.data.engine.other.NetItem;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Cluster of net loads that can be activated together.
 *
 * @author Maxim Knepfle
 *
 */
public class NetCluster extends Item implements NetItem, GeometryItem<Point>, NamedItem {

    public static final TColor SUPPLY = TColor.ORANGE;
    public static final TColor DEMAND = TColor.YELLOW;
    public static final TColor NOT_CONNECTED = TColor.WHITE;

    private static final long serialVersionUID = 7992398125463026642L;

    @ItemIDField(MapLink.STAKEHOLDERS)
    @XMLValue
    private Integer ownerID = Item.NONE;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private boolean firstTimeConnect = true;

    @ItemIDField(MapLink.SCENARIOS)
    @XMLValue
    private Integer scenarioID = Item.NONE;

    @XMLValue
    private Double fractionConnectedOverride = null;

    @XMLValue
    @ItemIDField(MapLink.NET_LOADS)
    private ArrayList<Integer> loadIDs = new ArrayList<>();

    public NetCluster() {

    }

    public boolean addLoadID(Integer loadID) {
        if (!this.loadIDs.contains(loadID)) {
            this.loadIDs.add(loadID);
            return true;
        }
        return false;

    }

    public List<NetLoad> getActiveLoads(NetType netType) {
        TimeState timeState = getConnectedState(netType);
        List<NetLoad> activeLoads = new ArrayList<>();
        for (NetLoad load : this.getLoads(netType)) {
            if (load.getConnectionState() == timeState) {
                activeLoads.add(load);
            }
        }
        return activeLoads;
    }

    public List<Integer> getBuildingIDs(NetType netType) {

        List<Integer> buildings = new ArrayList<>();
        for (NetLoad load : this.getLoads(netType)) {
            buildings.add(load.getBuildingID());
        }

        return buildings;
    }

    public List<Building> getBuildings(NetType netType) {

        List<Building> buildings = new ArrayList<>();
        for (NetLoad load : this.getLoads(netType)) {
            if (load.getBuilding() != null) {
                buildings.add(load.getBuilding());
            }
        }

        return buildings;
    }

    @Override
    public Point getCenterPoint() {

        Collection<NetLoad> loads = getLoads();
        if (!loads.isEmpty()) {
            List<Geometry> geoms = new ArrayList<>();
            for (NetLoad load : loads) {
                if (load.hasPoint()) {
                    geoms.add(load.getCenterPoint());
                }
            }
            return JTSUtils.getCenterPoint(geoms);
        }
        return null;
    }

    public TimeState getConnectedState(NetType netType) {
        TimeState bestTimeState = TimeState.NOTHING;
        for (NetLoad load : this.getLoads(netType)) {
            if (load.getConnectionState().after(bestTimeState)) {
                bestTimeState = load.getConnectionState();
            }
        }
        return bestTimeState;
    }

    public double getFractionConnected() {
        if (fractionConnectedOverride != null) {
            return fractionConnectedOverride;
        } else {
            NetSetting netSetting = getItem(MapLink.NET_SETTINGS, NetSetting.Type.CLUSTER_FRACTION_CONNECTED);
            return netSetting.getDoubleValue();
        }
    }

    public String getImageLocation() {

        Stakeholder stakeholder = getOwner();
        if (stakeholder == null) {
            return StringUtils.EMPTY;
        }
        return stakeholder.getImageLocation();
    }

    public Scenario getLevel() {
        return this.getItem(MapLink.SCENARIOS, this.getScenarioID());
    }

    public List<NetLoad> getLoads() {
        return this.getItems(MapLink.NET_LOADS, this.loadIDs);
    }

    public List<NetLoad> getLoads(NetType netType) {
        if (netType == null) {
            return getLoads();
        }

        List<NetLoad> loads = new ArrayList<>();
        for (NetLoad load : getLoads()) {
            if (load.isPartOf(netType)) {
                loads.add(load);
            }
        }
        return loads;
    }

    @Override
    public String getName() {

        // set name
        if (StringUtils.containsData(name)) {
            return name;
        }

        // else take first building
        for (Building building : this.getBuildings(null)) {
            return building.getName();
        }

        // return id name
        return this.getClass().getSimpleName() + StringUtils.WHITESPACE + this.getID();
    }

    public Collection<NetType> getNetTypes() {
        Set<NetType> netTypes = new HashSet<>();
        for (NetLoad load : getLoads()) {
            netTypes.add(load.getNetType());
        }
        return netTypes;
    }

    public Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getOwnerID());
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public GeometryCollection getPolygons(NetType netType, MapType mapType) {

        List<Geometry> geoms = new ArrayList<>();
        for (Building building : getBuildings(netType)) {
            geoms.add(building.getPolygons(mapType));
        }
        return JTSUtils.createCollection(geoms);
    }

    @Override
    public Point[] getQTGeometries() {
        return new Point[] { this.getCenterPoint() };
    }

    public Integer getRelationID(Relation relation, NetType netType) {

        if (relation == Relation.OWNER) {
            return getOwnerID();
        } else if (relation == Relation.NETOWNER) {
            if (netType == null) {
                for (NetLoad netLoad : getLoads()) {
                    netType = netLoad.getNetType();
                    break;
                }
            }
            if (netType != null) {
                NetSetting netSetting = getItem(MapLink.NET_SETTINGS, NetSetting.Type.getNetOwnerTypeForNetType(netType));
                if (netSetting != null) {
                    return netSetting.getIntegerValue();
                }
            }
        }
        return Item.NONE;
    }

    /**
     * Return name of related item, if relation does not exist return empty
     */
    public String getRelationName(Relation relation, NetType netType) {

        Integer linkID = this.getRelationID(relation, netType);
        Item item = this.getItem(relation.getMapLink(), linkID);
        return item instanceof UniqueNamedItem uni ? uni.getName() : StringUtils.EMPTY;
    }

    public Integer getScenarioID() {
        return scenarioID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Source> getSources() {
        return Collections.EMPTY_LIST;
    }

    public double getSumAttribute(MapType mapType, NetType netType, ReservedAttribute attribute) {
        return getSumAttribute(mapType, netType, attribute.name());
    }

    public double getSumAttribute(MapType mapType, NetType netType, String attribute) {
        return getSumAttribute(mapType, netType, attribute, null, 0);
    }

    public double getSumAttribute(MapType mapType, NetType netType, String attribute, Relation relation, int index) {

        double sum = 0;
        for (NetLoad load : this.getActiveLoads(netType)) {
            if (relation != null) {
                AttributeQueryInterface item = load.getRelationAttribute(relation);
                if (item != null) {
                    sum += item.getAttribute(mapType, attribute, index);
                }
            } else {
                sum += load.getAttribute(mapType, attribute, index);
            }
        }
        return sum;
    }

    public double[] getSumAttributeArray(MapType mapType, NetType netType, String attribute) {
        return getSumAttributeArray(mapType, netType, attribute, null);
    }

    public double[] getSumAttributeArray(MapType mapType, NetType netType, String attribute, Relation relation) {

        List<double[]> arrays = new ArrayList<>();

        for (NetLoad load : this.getActiveLoads(netType)) {
            if (relation != null) {
                AttributeQueryInterface item = load.getRelationAttribute(relation);
                if (item != null) {
                    arrays.add(item.getAttributeArray(mapType, attribute));
                }
            } else {
                arrays.add(load.getAttributeArray(mapType, attribute));
            }
        }
        int maxSize = 0;
        for (double[] array : arrays) {
            maxSize = Math.max(array.length, maxSize);
        }
        if (maxSize == 0) {
            return AttributeItem.ZERO;
        }

        double[] result = new double[maxSize];
        for (double[] array : arrays) {
            for (int i = 0; i < array.length; i++) {
                result[i] += array[i];
            }
        }
        return result;
    }

    public LoadType getType(NetType netType) {
        for (NetLoad load : this.getLoads(netType)) {
            return load.getType();
        }
        return LoadType.UNKNOWN;
    }

    public boolean hasAttribute(String attribute) {
        for (NetLoad load : this.getLoads()) {
            if (load.hasAttribute(attribute)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFractionConnectedOverride() {
        return fractionConnectedOverride != null;
    }

    public boolean isActive(NetType netType) {
        return TimeState.READY.equals(getConnectedState(netType));
    }

    public boolean isFirstTimeConnect() {
        return firstTimeConnect;
    }

    @Override
    public boolean isPartOf(NetType netType) {
        for (NetLoad netLoad : getLoads()) {
            if (netLoad.isPartOf(netType)) {
                return true;
            }
        }
        return false;
    }

    public boolean isProducer(MapType mapType, NetType netType) {
        return getSumAttribute(mapType, netType, LoadAttribute.FLOW) > 0;
    }

    public boolean removeLoadID(Integer loadID) {
        return this.loadIDs.remove(loadID);
    }

    public void setFirstTimeConnect(boolean firstTimeConnect) {
        this.firstTimeConnect = firstTimeConnect;
    }

    public void setFractionConnected(Double fractionConnected) {
        this.fractionConnectedOverride = fractionConnected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    public void setScenarioID(Integer scenarioID) {
        this.scenarioID = scenarioID;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
