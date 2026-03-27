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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.MapMeasure;
import nl.tytech.data.engine.item.Overlay;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.TerrainType;
import nl.tytech.data.engine.other.AbstractSpatial;
import nl.tytech.naming.GeoNC;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 * GeoTiff that is part of measure.
 *
 * @author Frank Baars
 */
public class GeoTiffSpatial extends AbstractSpatial {

    private static final class GeoTiffFeature extends SpatialItem<GeoTiffSpatial> {

        private static final long serialVersionUID = 3526105202124338380L;

        public GeoTiffFeature(MapMeasure mapMeasure, GeoTiffSpatial spatial) {
            super(mapMeasure, spatial);
        }

        @Override
        public Map<String, Object> getExportAttributes(boolean inherited) {

            Map<String, Object> map = super.getExportAttributes(inherited);

            TerrainType type = mapMeasure.getItem(MapLink.TERRAIN_TYPES, spatial.getTerrainTypeID());
            if (type != null) {
                map.put(Terrain.TYPE, type.getName());
                map.put(TerrainSpatial.TERRAIN_TYPE_ID, spatial.getTerrainTypeID());
            }

            map.put(GEOTIFF_IDS, StringUtils.arrayToHumanString(spatial.getGeoTiffIDs(), StringUtils.HUMAN_STRING_SEPERATOR));
            map.put(GEOTIFFS, StringUtils.arrayToHumanString(spatial.getGeoTiffs(mapMeasure), StringUtils.HUMAN_STRING_SEPERATOR));
            map.put(AUTOMP, Boolean.toString(spatial.autoMP));

            map.put(SPATIAL_ID, spatial.getID());
            map.put(SPATIAL_TYPE, MeasureEditType.GEOTIFF);

            return map;
        }

    }

    public static final String GEOTIFF_IDS = "GEOTIFF_IDS";
    public static final String GEOTIFFS = "GEOTIFFS";

    private static final long serialVersionUID = 1261506340114164934L;

    @XMLValue
    @ItemIDField(MapLink.GEO_TIFFS)
    private ArrayList<Integer> geoTiffIDs = new ArrayList<>();

    @XMLValue
    private MultiPolygon multiPolygon = JTSUtils.EMPTY;

    @XMLValue
    private Integer terrainTypeID = Item.NONE;

    @XMLValue
    private boolean autoMP = true;

    public GeoTiffSpatial() {
        super(Item.NONE);
    }

    public GeoTiffSpatial(Integer id) {
        super(id);
    }

    public void addGeoTiffID(Integer tiffID) {
        geoTiffIDs.remove(tiffID);
        geoTiffIDs.add(tiffID);
    }

    public void addMultiPolygon(MultiPolygon add) {
        this.multiPolygon = JTSUtils.union(this.multiPolygon, add);
    }

    public SpatialItem<GeoTiffSpatial> getFeature(MapMeasure mapMeasure) {
        return new GeoTiffFeature(mapMeasure, this);
    }

    public List<Integer> getGeoTiffIDs() {
        return geoTiffIDs;
    }

    public List<Overlay> getGeoTiffs(MapMeasure mapMeasure) {

        List<Overlay> overlays = new ArrayList<>();
        for (Integer overlayID : geoTiffIDs) {
            overlays.add(mapMeasure.getItem(MapLink.GEO_TIFFS, overlayID));
        }
        return overlays;
    }

    @Override
    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public String getName() {
        return GeoNC.GEOTIFF + " " + getID();
    }

    public Integer getTerrainTypeID() {
        return terrainTypeID;
    }

    public boolean hasGeoTiffID(Integer tiffID) {
        return geoTiffIDs.contains(tiffID);
    }

    public boolean isAutoMP() {
        return autoMP;
    }

    public void removeGeoTiffID(Integer tiffID) {
        geoTiffIDs.remove(tiffID);
    }

    public void removeMultiPolygon(MultiPolygon remove) {
        this.multiPolygon = JTSUtils.difference(this.multiPolygon, remove);
    }

    public void setAutoMP(boolean automatic) {
        this.autoMP = automatic;
    }

    public void setGeoTiffIDs(Integer[] tiffIDs) {
        this.geoTiffIDs.clear();
        Collections.addAll(this.geoTiffIDs, tiffIDs);
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public void setTerrainTypeID(Integer type) {
        this.terrainTypeID = type;
    }
}
