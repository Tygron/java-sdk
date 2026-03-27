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
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.SourcedItem;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.other.NetItem;
import nl.tytech.util.JTSUtils;

/**
 * Connecting point for netlines and netloads.
 *
 * @author Maxim Knepfle
 */
public class NetNode extends SourcedItem implements GeometryItem<Point>, NetItem {

    public static final double SELECTION_MARGIN = 20;

    public static final double MINIMUM_NODE_DISTANCE = 0.01;

    private static final long serialVersionUID = 2739744881310956349L;

    @XMLValue
    private Point point = null;

    /**
     * Note: Values are calculated on the fly (at start) and thus not stored in XML.
     */
    private ArrayList<Integer> connectedLineIDs = new ArrayList<>();
    private ArrayList<Integer> loadIDs = new ArrayList<>();

    public void addConnectedLine(Integer netLineID) {
        if (!connectedLineIDs.contains(netLineID)) {
            connectedLineIDs.add(netLineID);
        }
    }

    public void addLoadID(Integer loadID) {
        if (!loadIDs.contains(loadID)) {
            loadIDs.add(loadID);
        }
    }

    public int countConnectedLines(NetType netType) {
        int count = 0;
        for (int i = 0; i < connectedLineIDs.size(); i++) {
            NetLine line = this.<NetLine> getItem(MapLink.NET_LINES, connectedLineIDs.get(i));
            if (line != null && line.isPartOf(netType)) {
                count++;
            }
        }
        return count;
    }

    public double distance3D(Coordinate c2) {

        Point p1 = getCenterPoint();
        if (p1 == null || c2 == null) {
            return Double.MAX_VALUE;
        } else {
            return JTSUtils.distance3D(p1.getCoordinate(), c2);
        }
    }

    public double distance3D(NetNode other) {
        return distance3D(other.getCenterPoint());
    }

    public double distance3D(Point point) {
        return distance3D(point != null ? point.getCoordinate() : null);
    }

    @Override
    public Point getCenterPoint() {
        return point;
    }

    public List<Integer> getConnectedLineIDs() {
        return connectedLineIDs;
    }

    public List<NetLine> getConnectedLines() {
        return this.getItems(MapLink.NET_LINES, connectedLineIDs);
    }

    public List<NetLine> getConnectedLines(NetType netType) {

        List<NetLine> netLines = new ArrayList<>();
        for (int i = 0; i < connectedLineIDs.size(); i++) {
            NetLine netLine = this.<NetLine> getItem(MapLink.NET_LINES, connectedLineIDs.get(i));
            if (netLine != null && netLine.isPartOf(netType)) {
                netLines.add(netLine);
            }
        }
        return netLines;
    }

    @Override
    public Geometry getExportGeometry() {
        return point == null ? JTSUtils.EMPTY : point;
    }

    public NetLoad getLoad(NetType netType) {

        for (int i = 0; i < loadIDs.size(); i++) {
            NetLoad load = this.<NetLoad> getItem(MapLink.NET_LOADS, loadIDs.get(i));
            if (load != null && load.isPartOf(netType)) {
                return load;
            }
        }
        return null;
    }

    public List<Integer> getLoadIDs() {
        return loadIDs;
    }

    public List<NetLoad> getLoads() {
        return this.<NetLoad> getItems(MapLink.NET_LOADS, loadIDs);
    }

    @Override
    public Point[] getQTGeometries() {
        return new Point[] { getCenterPoint() }; // needs cloning, data is used double somwhere?
    }

    public boolean hasConnectedLine(NetType netType) {

        for (int i = 0; i < connectedLineIDs.size(); i++) {
            NetLine netLine = this.<NetLine> getItem(MapLink.NET_LINES, connectedLineIDs.get(i));
            if (netLine != null && netLine.isPartOf(netType)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasConnectedLines() {
        return !connectedLineIDs.isEmpty();
    }

    public boolean hasLoad(NetType netType) {
        return getLoad(netType) != null;
    }

    public boolean hasLoads() {
        return !loadIDs.isEmpty();
    }

    public boolean hasNetType() {
        return hasConnectedLines() || hasLoads();
    }

    public boolean isActive(Map<NetType, String> attributes) {

        for (int i = 0; i < connectedLineIDs.size(); i++) {
            NetLine line = this.<NetLine> getItem(MapLink.NET_LINES, connectedLineIDs.get(i));
            if (line != null) {
                String attribute = attributes.get(line.getNetType());
                if (attribute != null && line.isActive(attribute)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isActive(NetType netType, String attribute) {

        for (int i = 0; i < connectedLineIDs.size(); i++) {
            NetLine line = this.<NetLine> getItem(MapLink.NET_LINES, connectedLineIDs.get(i));
            if (line != null && line.isPartOf(netType) && line.isActive(attribute)) {
                return true;
            }
        }
        return false;
    }

    public boolean isConnectedTo(NetType netType, Integer otherNodeID) {

        for (int i = 0; i < connectedLineIDs.size(); i++) {
            NetLine line = this.<NetLine> getItem(MapLink.NET_LINES, connectedLineIDs.get(i));
            if (line != null && line.isPartOf(netType)) {
                if (line.getStartNodeID().equals(otherNodeID) || line.getEndNodeID().equals(otherNodeID)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isConnectedTo(NetType netType, NetNode otherNode) {
        for (int i = 0; i < connectedLineIDs.size(); i++) {
            if (otherNode.connectedLineIDs.contains(connectedLineIDs.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean isEndOfLine() {
        return this.connectedLineIDs.size() == 1;
    }

    public boolean isEndOfLine(NetType netType) {
        return this.countConnectedLines(netType) == 1;
    }

    public boolean isHead(NetType netType) {
        NetLoad netLoad = getLoad(netType);
        return netLoad != null ? netLoad.isRoot() : false;
    }

    @Override
    public boolean isPartOf(NetType netType) {
        return hasConnectedLine(netType) || getLoad(netType) != null;
    }

    public boolean removeConnectedLine(Integer lineID) {
        return connectedLineIDs.remove(lineID);
    }

    public boolean removeLoadID(Integer loadID) {
        return loadIDs.remove(loadID);
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(point);
    }

    public void setConnectedLines(ArrayList<Integer> connectedLineIDs) {
        this.connectedLineIDs = connectedLineIDs;
    }

    public void setConnectedLoads(ArrayList<Integer> loadIDs) {
        this.loadIDs = loadIDs;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
