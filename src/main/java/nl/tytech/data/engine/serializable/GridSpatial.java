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

import java.io.Serializable;
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
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public class GridSpatial extends AbstractSpatial implements Serializable {

    private static final class GridFeature extends SpatialItem<GridSpatial> {

        private static final long serialVersionUID = -7970114229933725155L;

        public GridFeature(MapMeasure mapMeasure, GridSpatial spatial) {
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

            map.put(GRID_IDS, StringUtils.arrayToHumanString(spatial.getOverlayIDs(), StringUtils.HUMAN_STRING_SEPERATOR));
            map.put(GRIDS, StringUtils.arrayToHumanString(spatial.getOverlays(mapMeasure), StringUtils.HUMAN_STRING_SEPERATOR));
            map.put(AUTOMP, Boolean.toString(spatial.autoMP));

            map.put(SPATIAL_ID, spatial.getID());
            map.put(SPATIAL_TYPE, MeasureEditType.GRID);

            return map;
        }

    }

    private static final long serialVersionUID = 238560304198855837L;

    public static final String GRID_IDS = "GRID_IDS";
    public static final String GRIDS = "GRIDS";

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private ArrayList<Integer> overlayIDs = new ArrayList<>();

    @XMLValue
    private MultiPolygon multiPolygon = JTSUtils.EMPTY;

    @XMLValue
    private Integer terrainTypeID = Item.NONE;

    @XMLValue
    private boolean autoMP = true;

    public GridSpatial() {
        super(Item.NONE);
    }

    public GridSpatial(Integer id) {
        super(id);
    }

    public void addMultiPolygon(MultiPolygon add) {
        this.multiPolygon = JTSUtils.union(this.multiPolygon, add);
    }

    public void addOverlayID(Integer overlayID) {
        overlayIDs.remove(overlayID);
        overlayIDs.add(overlayID);
    }

    public SpatialItem<GridSpatial> getFeature(MapMeasure mapMeasure) {
        return new GridFeature(mapMeasure, this);
    }

    @Override
    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public String getName() {
        return "GridSpatial " + getID();
    }

    public List<Integer> getOverlayIDs() {
        return overlayIDs;
    }

    public List<Overlay> getOverlays(MapMeasure mapMeasure) {
        List<Overlay> overlays = new ArrayList<>();
        for (Integer overlayID : overlayIDs) {
            overlays.add(mapMeasure.getItem(MapLink.OVERLAYS, overlayID));
        }
        return overlays;
    }

    public Integer getTerrainTypeID() {
        return terrainTypeID;
    }

    public boolean hasOverlayID(Integer overlayID) {
        return overlayIDs.contains(overlayID);
    }

    public boolean isAutoMP() {
        return autoMP;
    }

    public void removeMultiPolygon(MultiPolygon remove) {
        this.multiPolygon = JTSUtils.difference(this.multiPolygon, remove);
    }

    public void removeOverlayID(Integer overlayID) {
        overlayIDs.remove(overlayID);
    }

    public void setAutoMP(boolean automatic) {
        this.autoMP = automatic;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public void setOverlayIDs(Integer[] overlayIDs) {
        this.overlayIDs.clear();
        Collections.addAll(this.overlayIDs, overlayIDs);
    }

    public void setTerrainTypeID(Integer type) {
        this.terrainTypeID = type;
    }
}
