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

import java.util.Collection;
import java.util.Map;
import org.locationtech.jts.geom.Envelope;
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
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.item.TerrainType.TerrainAttribute;
import nl.tytech.data.engine.other.AttributeQueryInterface;
import nl.tytech.data.engine.other.LayerQueryInterface;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.ItemIntersector;
import nl.tytech.util.ItemIntersector.IntersectorItem;
import nl.tytech.util.JTSUtils;

/**
 * @author Maxim Knepfle
 */
public class Terrain extends SourcedItem implements GeometryItem<MultiPolygon>, IntersectorItem, LayerQueryInterface {

    private static final long serialVersionUID = -5100582384433357166L;

    public static final String TYPE = "TERRAIN_TYPE";

    @JsonIgnore
    public transient ItemIntersector intersector = null;

    @XMLValue
    private MultiPolygon current = JTSUtils.EMPTY;

    @XMLValue
    private MultiPolygon maquette = null;

    @XMLValue
    private boolean showSatellite = true;

    @XMLValue
    @ItemIDField(MapLink.TERRAIN_TYPES)
    private Integer typeID = Item.NONE;

    @XMLValue
    private double[] heightValuesCurrent = null;

    @XMLValue
    private double[] heightValuesMaquette = null;

    @Override
    public double getAttribute(MapType mapType, String attribute) {
        return this.getType().getAttribute(attribute);
    }

    @Override
    public double getAttribute(MapType mapType, String key, int index) {
        return getType().getAttribute(mapType, key, index);
    }

    public double getAttribute(MapType mapType, TerrainAttribute attribute) {
        return this.getType().getAttribute(mapType, attribute);
    }

    @Override
    public double getAttribute(String key) {
        return getAttribute(getDefaultMap(), key);
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {
        return getType().getAttributeArray(mapType, key);
    }

    @Override
    public double[] getAttributeArray(String key) {
        return getAttributeArray(getDefaultMap(), key);
    }

    @Override
    public Collection<String> getAttributes() {
        return getAttributes(getDefaultMap());
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {
        return getType().getAttributes(mapType);
    }

    @Override
    public Point getCenterPoint() {
        return null; // TODO: Maxim: Not implemented
    }

    public Point getCenterPoint(MapType mapType) {
        MultiPolygon mp = this.getMultiPolygon(mapType);
        return JTSUtils.getCenterPoint(mp);
    }

    public Envelope getEnvelope(MapType mapType) {
        return getMultiPolygon(mapType).getEnvelopeInternal();
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        if (inherited) {
            addInheritedAttributes(map, getType());
        }
        map.put(TYPE, this.getType().getName());
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        return getMultiPolygon(MapType.CURRENT);
    }

    public double[] getHeightValues(MapType mapType) {
        return mapType == MapType.MAQUETTE && this.maquette != null ? heightValuesMaquette : heightValuesCurrent;
    }

    @Override
    public GeometryCollection getIntersectorGeometies(MapType mapType) {
        return getMultiPolygon(mapType);
    }

    @Override
    public Layer getLayer() {
        return getType().getLayer();
    }

    public MultiPolygon getMultiPolygon(MapType mapType) {

        /**
         * Return current when maquette is still null.
         */
        if (mapType == MapType.CURRENT) {
            return current;
        } else if (maquette == null) {
            return current;
        } else {
            return maquette;
        }
    }

    @Override
    public MultiPolygon[] getQTGeometries() {
        return new MultiPolygon[] { current, maquette };
    }

    @Override
    public AttributeQueryInterface getRelationAttribute(Relation relation) {
        return getType().getRelationAttribute(relation);
    }

    public TerrainType getType() {
        return getItem(MapLink.TERRAIN_TYPES, typeID);
    }

    public Integer getTypeID() {
        return typeID;
    }

    @Override
    public boolean hasAttribute(MapType mapType, String key) {
        return getType().hasAttribute(mapType, key);
    }

    @Override
    public boolean hasAttribute(String key) {
        return hasAttribute(getDefaultMap(), key);
    }

    public final boolean isCovers(MapType mapType, int rasterID, int rasters, double cellM, Geometry superCell, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isCovers(mapType, cellM, superCell, cacheID, rasterID);
    }

    /**
     * Checks if terrain is in this mapType (not if it intersects with geometry). Geometry is needed when current and maquette have a
     * different polygon. Both current and maquette polygons are part of the QuadTree.
     */
    public boolean isInMap(MapType mapType, Geometry g) {
        return this.maquette == null || JTSUtils.intersectsBorderIncluded(g, getMultiPolygon(mapType));
    }

    public final boolean isIntersect(MapType mapType, int rasterID, int rasters, double cellM, Geometry cell, Point center, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isIntersect(mapType, cellM, cell, center, cacheID, rasterID);
    }

    public boolean isShowSatellite() {
        return showSatellite;
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(current);
        JTSUtils.clearUserData(maquette);
    }

    public void setHeightValues(MapType mapType, double[] heightValues) {

        if (mapType == MapType.MAQUETTE) {
            this.heightValuesMaquette = heightValues;
        } else {
            this.heightValuesCurrent = heightValues;
        }
    }

    public void setMultiPolygon(MapType mapType, MultiPolygon mp) {

        if (mapType == MapType.CURRENT) {
            this.current = mp;
        } else {
            this.maquette = mp;
        }
    }

    public void setShowSatellite(boolean showSatellite) {
        this.showSatellite = showSatellite;
    }

    public void setTypeID(Integer typeID) {
        this.typeID = typeID;
    }
}
