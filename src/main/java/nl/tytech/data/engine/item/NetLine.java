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

import java.util.Map;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.NetFunction.NetLineFunctionAttribute;
import nl.tytech.data.engine.other.NetItem;
import nl.tytech.util.ItemIntersector;
import nl.tytech.util.ItemIntersector.IntersectorItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Net line that connects two nodes and can transport e.g. water
 *
 * @author Maxim Knepfle
 *
 */
public class NetLine extends SourcedAttributeItem implements GeometryItem<MultiPolygon>, NetItem, IntersectorItem {

    public enum NetLineAttribute implements ReservedAttribute {

        FLOW(Double.class),

        DIAMETER_M(Double.class),

        ALWAYS_ACTIVE(Boolean.class),

        ;

        private final Class<?> type;

        private NetLineAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return AttributeItem.ZERO;
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

    /**
     * Distribution Network Type.
     *
     * The type of network to which this NetLine belongs
     */
    public enum NetType {

        ELECTRICITY(new TColor(153, 153, 0), 0.05),

        GAS(new TColor(77, 102, 204), 0.1),

        HEAT(new TColor(153, 51, 0), 0.1),

        INTERNET(TColor.GRAY, 0.05),

        SEWER(TColor.BROWN, 0.2),

        ;

        public static final String NET_TYPE = "NET_TYPE";
        public static final NetType[] VALUES = values();

        private final TColor color;
        private final double diameter;

        private NetType(TColor color, double diameter) {
            this.color = color;
            this.diameter = diameter;
        }

        public TColor getDefaultColor() {
            return color;
        }

        public double getDefaultDiameter() {
            return diameter;
        }

        public double getDefaultPrice() {
            return 10.0;
        }
    }

    public static final double DEFAULT_DEPTH_M = 2;

    public static final double DEFAULT_MERGE_M = 1;

    public static final String TYPE_ID = "TYPE_ID";

    public static final String START_NODE_ID = "START_NODE_ID";
    public static final String END_NODE_ID = "END_NODE_ID";

    private static final long serialVersionUID = 5434684457934689387L;

    public static final double SELECTION_MARGIN = 20;

    @XMLValue
    @ItemIDField(MapLink.NET_NODES)
    private Integer startNodeID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.NET_NODES)
    private Integer endNodeID = Item.NONE;

    @JsonIgnore
    private transient LineSegment lineSegment;

    @JsonIgnore
    private transient MultiPolygon lineMP;

    @ItemIDField(MapLink.NET_FUNCTIONS)
    @XMLValue
    private Integer functionID = Item.NONE;

    @JsonIgnore
    public transient ItemIntersector intersector = null;

    public double distance3D(Point point) {

        LineSegment segment = getLineSegment();
        if (segment == null) {
            return Double.MAX_VALUE;
        }
        return JTSUtils.distance3D(segment, point);
    }

    @Override
    public double getAttribute(MapType mapType, String key, int index) {
        return this.getAttribute(mapType, key, index, true);
    }

    public double getAttribute(MapType mapType, String key, int index, boolean checkDef) {

        double[] array = getAttributeArray(mapType, key, checkDef);
        return index >= 0 && array.length > index ? array[index] : DEFAULT_VALUE;
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {
        return getAttributeArray(mapType, key, true);
    }

    public double[] getAttributeArray(MapType mapType, String key, boolean checkDef) {

        if (super.hasAttribute(mapType, key)) {
            return super.getAttributeArray(mapType, key);
        }
        // fallback to function
        if (checkDef) {
            NetFunction def = getNetFunction();
            if (def != null && getNetFunction().hasAttribute(mapType, key)) {
                return getNetFunction().getAttributeArray(mapType, key);
            }
        }
        return EMPTY;
    }

    @Override
    public Point getCenterPoint() {

        Point start = this.getStartPoint();
        Point end = this.getEndPoint();

        if (start == null || end == null) {
            return null;
        }

        double dx = start.getX() - end.getX();
        double dy = start.getY() - end.getY();

        return JTSUtils.createPoint(start.getX() - dx / 2d, start.getY() - dy / 2d, 0d);
    }

    public double getCosts() {

        NetFunction function = this.getNetFunction();
        if (function == null) {
            return 0;
        }
        return this.getLengthM() * function.getAttribute(NetLineFunctionAttribute.PRICE_M);
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return NetLineAttribute.values();
    }

    public double getDiameterM() {
        return this.getAttribute(NetLineAttribute.DIAMETER_M);
    }

    public NetNode getEndNode() {
        return this.getItem(MapLink.NET_NODES, getEndNodeID());
    }

    public Integer getEndNodeID() {
        return endNodeID;
    }

    public Point getEndPoint() {
        NetNode node = getEndNode();
        return node == null ? null : node.getCenterPoint();
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        if (inherited) {
            addInheritedAttributes(map, getNetFunction());
        }
        map.put(NetLine.TYPE_ID, getNetFunctionID());
        map.put(NetType.NET_TYPE, getNetType().name());
        map.put(NetLine.START_NODE_ID, getStartNodeID());
        map.put(NetLine.END_NODE_ID, getEndNodeID());
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        LineString geom = getLineString();
        return geom == null ? JTSUtils.EMPTY : geom;
    }

    @Override
    public GeometryCollection getIntersectorGeometies(MapType mapType) {
        return this.getLineMP();
    }

    public double getLengthM() {

        Point start = getStartPoint();
        Point end = getEndPoint();
        if (start == null || end == null) {
            return 0;
        }
        return JTSUtils.distance3D(start, end);
    }

    public MultiPolygon getLineMP() {
        if (lineMP == null) {
            LineString ls = getLineString();
            lineMP = ls == null ? JTSUtils.EMPTY : JTSUtils.createMP(JTSUtils.bufferSimple(ls, Math.max(0.005, this.getDiameterM() / 2)));
        }
        return lineMP;
    }

    public LineSegment getLineSegment() {

        if (lineSegment == null) {
            Point start = getStartPoint();
            Point end = getEndPoint();
            if (start == null || end == null) {
                return null;
            }
            lineSegment = new LineSegment(start.getCoordinate(), end.getCoordinate());
        }
        return lineSegment;
    }

    public LineString getLineString() {

        LineSegment segment = this.getLineSegment();
        return segment == null ? null : JTSUtils.createLine(segment.p0, segment.p1);
    }

    public NetFunction getNetFunction() {
        return getItem(MapLink.NET_FUNCTIONS, this.functionID);
    }

    public Integer getNetFunctionID() {
        return functionID;
    }

    public NetType getNetType() {

        NetFunction def = getNetFunction();
        if (def == null) {
            return null;
        }
        return getNetFunction().getNetType();
    }

    @Override
    public MultiPolygon[] getQTGeometries() {
        return new MultiPolygon[] { getLineMP() };
    }

    public NetNode getStartNode() {
        return this.getItem(MapLink.NET_NODES, getStartNodeID());
    }

    public Integer getStartNodeID() {
        return this.startNodeID;
    }

    public Point getStartPoint() {

        NetNode node = getStartNode();
        return node == null ? null : node.getCenterPoint();
    }

    public boolean hasMissingNodes() {
        return getStartNode() == null || getEndNode() == null;
    }

    public boolean isActive(String attribute) {

        if (this.getAttribute(NetLineAttribute.ALWAYS_ACTIVE) != 0.0) {
            return true;
        }
        return this.getAttribute(attribute) != 0.0;
    }

    public final boolean isCovers(MapType mapType, int rasterID, int rasters, double cellM, Geometry superCell, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isCovers(mapType, cellM, superCell, cacheID, rasterID);
    }

    public final boolean isIntersect(MapType mapType, int rasterID, int rasters, double cellM, Geometry cell, Point center, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isIntersect(mapType, cellM, cell, center, cacheID, rasterID);
    }

    @Override
    public boolean isPartOf(NetType netType) {
        return this.getNetType() == netType;
    }

    public NetNode removeEndNode() {

        NetNode node = getEndNode();
        if (node != null) {
            node.removeConnectedLine(this.getID());
        }
        this.endNodeID = Item.NONE;
        return node;
    }

    public NetNode removeStartNode() {

        NetNode node = getStartNode();
        if (node != null) {
            node.removeConnectedLine(this.getID());
        }
        this.startNodeID = Item.NONE;
        return node;
    }

    @Override
    public void reset() {
        super.reset();
        resetLineSegment();
    }

    private void resetLineSegment() {
        lineSegment = null;
        lineMP = null;
    }

    public void setAlwaysActive(boolean alwaysActive) {
        this.setAttribute(NetLineAttribute.ALWAYS_ACTIVE, alwaysActive ? 1d : 0d);
    }

    public void setDiameterM(double diameterM) {
        this.setAttribute(NetLineAttribute.DIAMETER_M, diameterM);
    }

    public void setEndNode(NetNode endNode) {

        NetNode oldNode = getEndNode();
        if (oldNode != null && !startNodeID.equals(oldNode.getID())) {
            oldNode.removeConnectedLine(this.getID());
        }
        this.endNodeID = endNode.getID();
        endNode.addConnectedLine(this.getID());
        resetLineSegment();
    }

    public void setNetFunctionID(Integer functionID) {
        this.functionID = functionID;
    }

    public void setStartNode(NetNode startNode) {

        NetNode oldNode = getStartNode();
        if (oldNode != null && !endNodeID.equals(oldNode.getID())) {
            oldNode.removeConnectedLine(this.getID());
        }
        this.startNodeID = startNode.getID();
        startNode.addConnectedLine(this.getID());
        resetLineSegment();
    }

    @Override
    public String toString() {
        return getName();
    }

    public void updateForEndPoints() {
        resetLineSegment();
    }

    @Override
    public boolean updateGeometry(String attribute) {
        if (NetLineAttribute.DIAMETER_M.name().equals(attribute)) {
            this.lineMP = null;
            return true;
        }

        return false;
    }

    @Override
    public String validated(boolean startNewSession) {

        if (Item.NONE.equals(this.getStartNodeID()) || Item.NONE.equals(this.getEndNodeID())) {
            return "\nNetLine: " + this + " must have a valid start and end node!";
        }
        return StringUtils.EMPTY;
    }
}
