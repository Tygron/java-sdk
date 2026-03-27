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
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.SourcedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.util.ItemIntersector;
import nl.tytech.util.ItemIntersector.IntersectorItem;
import nl.tytech.util.JTSUtils;

/**
 * Piece of Land that can be owned by a stakeholder.
 *
 * @author Maxim Knepfle
 */
public class Plot extends SourcedItem implements GeometryItem<MultiPolygon>, IntersectorItem {

    private static final long serialVersionUID = -1347763356998229249L;

    public static String OWNER = "OWNER";

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer ownerID = Item.NONE;

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    @XMLValue
    private Point center = null;

    @JsonIgnore
    public transient ItemIntersector intersector = null;

    @Override
    public Point getCenterPoint() {
        if (center == null) {
            center = JTSUtils.getCenterPoint(polygons);
        }
        return center;
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        map.put(OWNER, this.getOwner().getName());
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        return getMultiPolygon();
    }

    @Override
    public GeometryCollection getIntersectorGeometies(MapType mapType) {
        return getMultiPolygon();
    }

    public MultiPolygon getMultiPolygon() {
        return polygons;
    }

    public Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getOwnerID());
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    @Override
    public MultiPolygon[] getQTGeometries() {
        return new MultiPolygon[] { getMultiPolygon() };
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
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(polygons);
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    public void setPolygons(MultiPolygon polygons) {
        this.polygons = polygons;
        this.center = JTSUtils.getCenterPoint(polygons);
    }

}
