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
package nl.tytech.data.engine.serializable;

import java.util.ArrayList;
import java.util.Map;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.MapMeasure;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.TerrainType;
import nl.tytech.data.engine.other.AbstractSpatial;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 * Terrain spatial party of measure
 *
 * @author Frank Baars
 */
public class TerrainSpatial extends AbstractSpatial {

    private class TerrainFeature extends SpatialItem<TerrainSpatial> {

        private static final long serialVersionUID = 358041263417489712L;

        @JsonIgnore
        private boolean inner;

        public TerrainFeature(MapMeasure mapMeasure, TerrainSpatial spatial, boolean inner) {
            super(mapMeasure, spatial);
            this.inner = inner;
        }

        @Override
        public Map<String, Object> getExportAttributes(boolean inherited) {

            Map<String, Object> map = super.getExportAttributes(inherited);
            if (inner) {
                map.put(INNER, inner);
            }

            TerrainType type = mapMeasure.getItem(MapLink.TERRAIN_TYPES, spatial.getTerrainTypeID());
            if (type != null) {
                map.put(Terrain.TYPE, type.getName());
                map.put(TERRAIN_TYPE_ID, spatial.getTerrainTypeID());
            }
            map.put(HEIGHT, spatial.innerHeight);
            map.put(RELATIVE, spatial.isRelative());

            map.put(SPATIAL_ID, spatial.getID());
            map.put(SPATIAL_TYPE, spatial.getMeasureEditType());

            return map;
        }

        @Override
        public Geometry getExportGeometry() {
            return inner ? spatial.getInnerMultiPolygon() : spatial.getOuterMultiPolygon();
        }

    }

    private static final long serialVersionUID = -272099094225816319L;

    public static final double MIN_ANGLE = 0.001; // degrees

    public static final double MAX_ANGLE = 90; // degrees

    public static final String INNER = "INNER";
    public static final String TERRAIN_TYPE_ID = "TERRAIN_TYPE_ID";

    @XMLValue
    private MultiPolygon innerPolygon = JTSUtils.EMPTY;

    @XMLValue
    private MultiPolygon outerPolygon = JTSUtils.EMPTY;

    @XMLValue
    private double innerHeight;

    @XMLValue
    private Integer terrainTypeID = Item.NONE;

    @XMLValue
    private boolean relative = false;

    @XMLValue
    private MeasureEditType measureEditType = MeasureEditType.RAISE;

    private transient PreparedGeometry innerPrep = null;

    private transient PreparedGeometry outerPrep = null;

    private transient ArrayList<LineString> outerRings = null;

    public TerrainSpatial() {
        super(Item.NONE);
    }

    public TerrainSpatial(MeasureEditType measureEditType, Integer id, Integer terrainTypeID) {
        super(id);
        this.measureEditType = measureEditType;
        this.terrainTypeID = terrainTypeID;
        this.innerHeight = measureEditType.getDefaultHeightAdjustment();
    }

    public void addInnerPolygon(Geometry add) {
        this.innerPolygon = JTSUtils.createMP(innerPolygon, add);
        this.outerPolygon = JTSUtils.createMP(outerPolygon, innerPolygon);
        resetPreps();
    }

    public void addOuterPolygon(Geometry outer) {
        this.outerPolygon = JTSUtils.createMP(this.outerPolygon, outer);
        resetPreps();
    }

    public Item[] getFeatures(MapMeasure measure) {
        return new Item[] { getOuterFeature(measure), getInnerFeature(measure) };
    }

    public double getHeight(Point point, double originalHeight) {
        if (innerPrep == null) {
            innerPrep = JTSUtils.prepare(innerPolygon);
        }

        if (JTSUtils.covers(innerPrep, point)) {
            return innerHeight(originalHeight);
        }

        if (!JTSUtils.hasArea(outerPolygon) || !JTSUtils.hasArea(innerPolygon)) {
            return originalHeight;
        }

        if (outerPrep == null) {
            outerPrep = JTSUtils.prepare(outerPolygon);
            outerRings = new ArrayList<>();
            for (Polygon polygon : JTSUtils.getPolygons(outerPolygon)) {
                outerRings.add(polygon.getExteriorRing());
            }
        }

        if (JTSUtils.covers(outerPrep, point)) {
            if (outerRings.isEmpty()) {
                return originalHeight;
            }

            double distInner = point.distance(innerPolygon);
            double distOut = Double.MAX_VALUE;
            for (LineString lineString : outerRings) {
                distOut = Math.min(distOut, point.distance(lineString));
            }

            return (distOut * innerHeight(originalHeight) + distInner * originalHeight) / (distInner + distOut);

        }

        return originalHeight;
    }

    public Item getInnerFeature(MapMeasure measure) {
        return new TerrainFeature(measure, this, true);
    }

    public double getInnerHeight() {
        return innerHeight;
    }

    public MultiPolygon getInnerMultiPolygon() {
        return innerPolygon;
    }

    public MeasureEditType getMeasureEditType() {
        return measureEditType;
    }

    @Override
    public MultiPolygon getMultiPolygon() {
        return outerPolygon;
    }

    public String getName() {
        if (measureEditType == null) {
            return TerrainSpatial.class.getSimpleName() + StringUtils.WHITESPACE + getID();
        }
        return measureEditType.name() + StringUtils.WHITESPACE + getID();
    }

    public Item getOuterFeature(MapMeasure measure) {
        return new TerrainFeature(measure, this, false);
    }

    public MultiPolygon getOuterMultiPolygon() {
        return outerPolygon;
    }

    public MultiPolygon getSideMultiPolygon() {
        return JTSUtils.difference(getOuterMultiPolygon(), getInnerMultiPolygon(), true);
    }

    public Integer getTerrainTypeID() {
        return terrainTypeID;
    }

    private double innerHeight(double originalHeight) {
        return relative ? innerHeight + originalHeight : innerHeight;
    }

    public boolean isRelative() {
        return relative;
    }

    public boolean isSpecialHeightChange() {
        return !JTSUtils.isEmpty(innerPolygon);
    }

    public void removeInnerPolygon(MultiPolygon remove) {
        this.innerPolygon = JTSUtils.difference(innerPolygon, remove);
        resetPreps();
    }

    public void removeOuterPolygon(MultiPolygon remove) {
        this.outerPolygon = JTSUtils.difference(outerPolygon, remove);
        this.innerPolygon = JTSUtils.intersection(outerPolygon, innerPolygon);
        resetPreps();
    }

    private void resetPreps() {
        innerPrep = null;
        outerPrep = null;
        outerRings = null;
    }

    public void setInnerHeight(double height) {
        this.innerHeight = height;
    }

    public void setMeasureEditType(MeasureEditType type) {
        this.measureEditType = type;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    public void setTerrainTypeID(Integer type) {
        this.terrainTypeID = type;
    }
}
