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
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.other.NetItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;

/**
 * End point of a network line that has a load (consumes or produces e.g. energy)
 * @author Maxim Knepfle
 *
 */
public class NetLoad extends SourcedAttributeItem implements NetItem, GeometryItem<Point> {

    public enum LoadAttribute implements ReservedAttribute {

        CONNECTION_COST(Double.class),

        FLOW(Double.class),

        POWER(Double.class),

        CONNECTION_COUNT(Double.class),

        FLOOR_SPACE_M2(Double.class),

        TYPE(Double.class),

        ROOT_LOAD(Boolean.class),

        LOAD_COUNT(Double.class),

        ADDRESS_COUNT(Double.class),

        CONNECTION_FRACTION(Double.class),

        PRESSURE_M_H2O_SUPPLY(Double.class),

        PRESSURE_M_H2O_RETURN(Double.class),

        TEMPERATURE_SUPPLY_DEGREES_CELSIUS(Double.class),

        ;

        public static final LoadAttribute[] VALUES = LoadAttribute.values();

        public static final LoadAttribute[] UPDATABLES;

        static {
            List<LoadAttribute> attributes = new ArrayList<>();
            for (LoadAttribute attribute : values()) {
                if (attribute == ROOT_LOAD || attribute == LOAD_COUNT || attribute == ADDRESS_COUNT || attribute == CONNECTION_FRACTION) {
                    continue;
                }
                attributes.add(attribute);
            }
            UPDATABLES = attributes.toArray(new LoadAttribute[attributes.size()]);
        }

        private final Class<?> type;

        private LoadAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return this == LOAD_COUNT ? AttributeItem.ONE : AttributeItem.ZERO;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }

    }

    private static class LoadCostSegment {

        private double baseCost;
        private double incrCost;
        private int minValue;

        public LoadCostSegment(double baseCost, double incrCost, int minValue) {
            this.baseCost = baseCost;
            this.incrCost = incrCost;
            this.minValue = minValue;
        }

        public double getCost(double value) {
            if (!isValidSegment(value)) {
                return 0;
            }
            return baseCost + incrCost * value;
        }

        public boolean isValidSegment(double value) {
            return value >= minValue;
        }
    }

    public enum LoadParameterType {

        FLOW(LoadAttribute.FLOW), //
        POWER(LoadAttribute.POWER), //
        CONNECTION_COST(LoadAttribute.CONNECTION_COST), //
        CONNECTION(LoadAttribute.CONNECTION_COUNT), //
        FLOOR_SPACE_M2(LoadAttribute.FLOOR_SPACE_M2), //

        ;

        public static final LoadParameterType[] VALUES = values();

        public static LoadParameterType getParamForAttribute(LoadAttribute loadAttribute) {
            for (LoadParameterType type : VALUES) {
                if (loadAttribute == type.attribute) {
                    return type;
                }
            }
            return null;
        }

        private LoadAttribute attribute;

        private LoadParameterType(LoadAttribute attribute) {
            this.attribute = attribute;
        }

        public LoadAttribute getAttribute() {
            return attribute;
        }

    }

    public enum LoadType {

        UNKNOWN("Onbekend", LoadAttribute.CONNECTION_COUNT, new LoadCostSegment[] { new LoadCostSegment(0, 2500, 1) }),

        // , meerdere (stijg)strangen per woning, individueel tapwater"),
        COLLECTIVE("Collectieve ketel", LoadAttribute.CONNECTION_COUNT,
                new LoadCostSegment[] { new LoadCostSegment(19250, 1100, 2), new LoadCostSegment(34500, 1100, 49),
                        new LoadCostSegment(41500, 1100, 62), new LoadCostSegment(48000, 1100, 139),
                        new LoadCostSegment(72000, 1100, 340) }),

        // , meerdere (stijg)strangen per woning, collectief tapwater"),
        COLLECTIVE_TAP("Collectieve ketel", LoadAttribute.CONNECTION_COUNT,
                new LoadCostSegment[] { new LoadCostSegment(19250, 500, 2), new LoadCostSegment(34500, 500, 49),
                        new LoadCostSegment(41500, 500, 62), new LoadCostSegment(48000, 500, 139), new LoadCostSegment(72000, 500, 340) }),

        // , verdeling naar radiatoren per woning, individueel tapwater"),
        COLLECTIVE_RADIATOR("Collectieve ketel", LoadAttribute.CONNECTION_COUNT,
                new LoadCostSegment[] { new LoadCostSegment(11250, 1800, 2), new LoadCostSegment(26500, 1800, 49),
                        new LoadCostSegment(33500, 1800, 62), new LoadCostSegment(40000, 1800, 139),
                        new LoadCostSegment(64000, 1800, 340) }),

        // , verdeling naar radiatoren per woning, collectief tapwater"),
        COLLECTIVE_RADIATOR_TAP("Collectieve ketel", LoadAttribute.CONNECTION_COUNT,
                new LoadCostSegment[] { new LoadCostSegment(11250, 1800, 2), new LoadCostSegment(26500, 1800, 49),
                        new LoadCostSegment(33500, 1800, 62), new LoadCostSegment(40000, 1800, 139),
                        new LoadCostSegment(64000, 1800, 340) }),

        INDIVIDUAL("Individuele (combi)ketel", LoadAttribute.CONNECTION_COUNT,
                new LoadCostSegment[] { new LoadCostSegment(2500, 0, 1), new LoadCostSegment(11250, 2200, 2),
                        new LoadCostSegment(26500, 2200, 49), new LoadCostSegment(33500, 2200, 62), new LoadCostSegment(40000, 2200, 139),
                        new LoadCostSegment(64000, 2200, 340) }),

        LARGE_SCALE_CONSUMER("Grootverbruiker", LoadAttribute.POWER, -1d,
                new LoadCostSegment[] { new LoadCostSegment(11250, 0, 45), new LoadCostSegment(26500, 0, 290),
                        new LoadCostSegment(33500, 0, 370), new LoadCostSegment(40000, 0, 830), new LoadCostSegment(64000, 0, 2040),
                        new LoadCostSegment(128000, 0, 3330) }),

        ;

        public static final LoadType[] VALUES = LoadType.values();

        private String text;

        private LoadCostSegment[] segments;
        private LoadAttribute attribute;
        private double multiplier;

        private LoadType(String text, LoadAttribute loadAttribute, double multiplier, LoadCostSegment[] loadCostSegments) {
            this.text = text;
            this.segments = loadCostSegments;
            this.attribute = loadAttribute;
            this.multiplier = multiplier;
        }

        private LoadType(String text, LoadAttribute loadAttribute, LoadCostSegment[] loadCostSegments) {
            this(text, loadAttribute, 1d, loadCostSegments);
        }

        public double getConnectionCost(NetLoad netLoad) {
            double value = netLoad.getAttribute(attribute) * multiplier;
            LoadCostSegment loadCostSegment = null;
            for (int i = 0; i < segments.length; i++) {
                if (segments[i].isValidSegment(value)) {
                    loadCostSegment = segments[i];
                } else {
                    break;
                }
            }
            if (loadCostSegment == null) {
                return 0;
            }
            return loadCostSegment.getCost(value);
        }

        public String getText() {
            return text;
        }
    }

    private static final long serialVersionUID = 7992398125463026542L;

    public static final double SELECTION_MARGIN = 40.0;

    public static final String ADDRESSCODE = "ADDRESSCODE";
    public static final String CLUSTER_ID = "CLUSTER_ID";
    public static final String NODE_ID = "NODE_ID";
    public static final String STATE = "STATE";

    public static final TimeState[] ALLOWED_TIME_STATES = new TimeState[] { TimeState.NOTHING, TimeState.REQUEST_ZONING_APPROVAL,
            TimeState.REQUEST_CONSTRUCTION_APPROVAL, TimeState.READY };

    @ItemIDField(MapLink.NET_NODES)
    @XMLValue
    private Integer nodeID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.BUILDINGS)
    private Integer buildingID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.NET_CLUSTERS)
    private Integer clusterID = Item.NONE;

    @XMLValue
    private NetType netType = null;

    @XMLValue
    private TimeState connectionState = TimeState.NOTHING;

    @XMLValue
    @ItemIDField(MapLink.ADDRESSES)
    private ArrayList<Integer> addressIDs = new ArrayList<>();

    @XMLValue
    private Point point = null;

    public NetLoad() {

    }

    public void addAddress(Integer addressID) {
        if (!addressIDs.contains(addressID)) {
            addressIDs.add(addressID);
        }
    }

    public List<Address> getAddresses() {
        return this.getItems(MapLink.ADDRESSES, addressIDs);
    }

    public Collection<Integer> getAddressIDs() {
        return addressIDs;
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String attribute) {

        double[] result = super.getAttributeArray(mapType, attribute);
        if (!MathUtils.equalsAll(result, 0.0) || hasAttribute(attribute)) {
            return result;
        }

        for (LoadAttribute loadAttribute : LoadAttribute.VALUES) {
            if (loadAttribute.name().equals(attribute)) {
                switch (loadAttribute) {
                    case POWER:
                    case FLOW:
                    case CONNECTION_COST:
                    case CONNECTION_COUNT:
                    case FLOOR_SPACE_M2:
                        return new double[] { getCalculatedParameter(mapType, LoadParameterType.getParamForAttribute(loadAttribute)) };
                    case TYPE:
                        return ZERO;
                    case LOAD_COUNT:
                        return ONE;
                    case ADDRESS_COUNT:
                        return new double[] { addressIDs.size() };
                    case CONNECTION_FRACTION:
                        NetCluster cluster = getCluster();
                        return cluster == null ? ZERO : new double[] { cluster.getFractionConnected() };
                    default:
                        return ZERO;
                }
            }
        }
        return EMPTY;
    }

    public Building getBuilding() {
        return this.getItem(MapLink.BUILDINGS, getBuildingID());
    }

    public Integer getBuildingID() {
        return buildingID;
    }

    public double getCalculatedConnectionCosts(MapType mapType) {
        return this.getType().getConnectionCost(this);
    }

    public int getCalculatedConnectionCount(MapType mapType) {

        Building building = getBuilding();
        if (building == null) {
            return 0;
        }
        int addressCount;
        if (!addressIDs.isEmpty()) {
            addressCount = addressIDs.size();
        } else {
            addressCount = (int) Math.round(building.getBuildingDetail(mapType, Detail.NUMBER_OF_HOUSES));
        }
        // always at least 1
        return Math.max(1, addressCount);
    }

    public double getCalculatedFlow(MapType mapType) {

        Building building = getBuilding();
        if (building == null) {
            return 0;
        }
        double flowPerm2 = 0;
        for (Category category : building.getCategories()) {
            double startValue = building.getValue(mapType, category, CategoryValue.HEAT_FLOW_M2_START_VALUE);
            double startYear = building.getValue(mapType, category, CategoryValue.HEAT_FLOW_M2_START_YEAR);
            if (startValue == 0 && startYear == 0) {
                continue;
            }

            int constructionYear = building.getConstructionYear() != null ? building.getConstructionYear() : 0;
            double yearDifference = MathUtils.clamp(constructionYear - startYear, 0, Double.MAX_VALUE);
            flowPerm2 += building.getCategoryFraction(category)
                    * (startValue + yearDifference * building.getValue(mapType, category, CategoryValue.HEAT_FLOW_M2_CHANGE_PER_YEAR));
        }
        double floorSizeM2 = getFloorspaceM2(mapType);
        return flowPerm2 * floorSizeM2;
    }

    public double getCalculatedParameter(LoadParameterType loadParameter) {
        return getCalculatedParameter(getDefaultMap(), loadParameter);
    }

    public double getCalculatedParameter(MapType mapType, LoadParameterType param) {

        switch (param) {
            case CONNECTION:
                return getCalculatedConnectionCount(mapType);
            case CONNECTION_COST:
                return getCalculatedConnectionCosts(mapType);
            case FLOOR_SPACE_M2:
                return getFloorspaceM2(mapType);
            case FLOW:
                return getCalculatedFlow(mapType);
            case POWER:
                return getCalculatedPower(mapType);
            default:
                return 0;
        }
    }

    public double getCalculatedPower(MapType mapType) {
        Building building = getBuilding();
        if (building == null) {
            return 0d;
        }
        double flowToPower = building.getValue(mapType, CategoryValue.HEAT_POWER_TO_FLOW_MULTIPLIER);
        return flowToPower != 0d ? this.getCalculatedFlow(mapType) / flowToPower : 0d;
    }

    @Override
    public Point getCenterPoint() {

        // try point first
        if (point != null) {
            return point;
        }

        // try center of buildings
        Building building = getBuilding();
        if (building != null) {
            Point center = building.getCenterPoint();
            if (center != null && JTSUtils.hasZ(center)) {
                return JTSUtils.clone(center);
            }
        }
        return JTSUtils.clone(getNodePoint());
    }

    public NetCluster getCluster() {
        return this.getItem(MapLink.NET_CLUSTERS, this.clusterID);
    }

    public Integer getClusterID() {
        return clusterID;
    }

    public TimeState getConnectionState() {
        return connectionState;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return LoadAttribute.values();
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        List<Address> adresses = getAddresses();
        if (!adresses.isEmpty()) {
            map.put(NetLoad.ADDRESSCODE, adresses.get(0).getExportCode());
        }
        map.put(NetType.NET_TYPE, getNetType().name());
        map.put(NetLoad.CLUSTER_ID, getClusterID());
        map.put(NetLoad.NODE_ID, getNodeID());
        map.put(NetLoad.STATE, getConnectionState().name());
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        return getCenterPoint();
    }

    public String getExportName() {
        return getName();
    }

    public double getFloorspaceM2(MapType mapType) {

        Building building = getBuilding();
        if (building == null) {
            return 0;
        }

        if (!addressIDs.isEmpty()) {
            double sumSurfaceSize = 0;
            for (Address address : getAddresses()) {
                sumSurfaceSize += address.getFloorSpaceM2();
            }
            return sumSurfaceSize;
        }

        return building.getBuildingDetail(mapType, Detail.SELLABLE_FLOORSPACE_M2);
    }

    public NetType getNetType() {
        return netType;
    }

    public NetNode getNode() {
        return this.getItem(MapLink.NET_NODES, getNodeID());
    }

    public Integer getNodeID() {
        return nodeID;
    }

    public Point getNodePoint() {

        NetNode node = this.getNode();
        return node == null ? null : node.getCenterPoint();
    }

    public Point getOverridePoint() {
        return point;
    }

    public Stakeholder getOwner() {
        return getItem(MapLink.STAKEHOLDERS, getOwnerID());
    }

    public Integer getOwnerID() {

        NetCluster cluster = this.getCluster();
        return cluster == null ? Item.NONE : cluster.getOwnerID();
    }

    @Override
    public Point[] getQTGeometries() {
        return new Point[] { getCenterPoint() };
    }

    @Override
    public Integer getRelationID(Relation relation) {

        if (relation == Relation.OWNER) {
            return getOwnerID();

        } else if (relation == Relation.BUILDING) {
            return getBuildingID();

        } else if (relation == Relation.NETOWNER) {

            NetType netType = getNetType();
            if (netType != null) {
                NetSetting netSetting = getItem(MapLink.NET_SETTINGS, NetSetting.Type.getNetOwnerTypeForNetType(netType));
                if (netSetting != null) {
                    return netSetting.getIntegerValue();
                }
            }
        }
        return Item.NONE;
    }

    public LoadType getType() {

        int typeOrdinal = (int) Math.round(getAttribute(LoadAttribute.TYPE));
        LoadType loadType = LoadType.UNKNOWN;
        if (typeOrdinal >= 0 && typeOrdinal < LoadType.VALUES.length) {
            loadType = LoadType.VALUES[typeOrdinal];
        }
        return loadType;
    }

    public boolean hasAddress(Integer addressID) {
        return addressIDs.contains(addressID);
    }

    public boolean hasCluster() {
        return !Item.NONE.equals(clusterID);
    }

    public boolean hasPoint() {
        return getCenterPoint() != null;
    }

    /**
     * Active when both owner and connecter agree.
     * @return
     */
    public boolean isActive() {
        return TimeState.READY.equals(getConnectionState());
    }

    public boolean isLocated() {
        return getNode() != null && getNodePoint() != null && getBuilding() != null;
    }

    @Override
    public boolean isPartOf(NetType netType) {
        return this.netType == netType;
    }

    public boolean isRoot() {
        return getAttribute(LoadAttribute.ROOT_LOAD) != 0;
    }

    public boolean removeAddress(Integer addressID) {
        return addressIDs.remove(addressID);
    }

    public NetNode removeNode() {

        NetNode node = getNode();
        if (node != null) {
            node.removeLoadID(this.getID());
        }
        this.nodeID = Item.NONE;
        return node;
    }

    public void setBuildingID(Integer buildingID) {
        this.buildingID = buildingID;
    }

    public void setClusterID(Integer clusterID) {
        this.clusterID = clusterID;
    }

    public void setConnectionState(TimeState newTimeState) {
        this.connectionState = newTimeState;
    }

    public void setMainAddress(Integer addressID) {
        addressIDs.remove(addressID);
        addressIDs.add(0, addressID);
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public void setNode(NetNode netNode) {
        NetNode oldNode = getNode();
        if (oldNode != null) {
            oldNode.removeLoadID(this.getID());
        }
        netNode.addLoadID(this.getID());
        this.nodeID = netNode.getID();
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (netType == null) {
            netType = NetType.HEAT;
        }
        return result;
    }
}
