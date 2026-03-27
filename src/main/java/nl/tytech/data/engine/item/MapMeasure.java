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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.event.InputException;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.other.AbstractSpatial;
import nl.tytech.data.engine.serializable.GeoTiffSpatial;
import nl.tytech.data.engine.serializable.GridSpatial;
import nl.tytech.data.engine.serializable.LeveeSpatial;
import nl.tytech.data.engine.serializable.MeasureEditType;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.data.engine.serializable.Section;
import nl.tytech.data.engine.serializable.TerrainSpatial;
import nl.tytech.data.engine.serializable.UpgradeSpatial;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 * A Measure with map interaction; buildings, water, etc.
 *
 * @author Maxim Knepfle
 */
public final class MapMeasure extends Measure {

    private static final long serialVersionUID = -1432031812486067993L;

    @XMLValue
    private Point center = null;

    @XMLValue
    private double constructionCostsM3 = 0;

    @XMLValue
    @ItemIDField(MapLink.BUILDINGS)
    private ArrayList<Integer> buildingIDs = new ArrayList<>();

    @XMLValue
    @ListOfClass(LeveeSpatial.class)
    private ArrayList<LeveeSpatial> leveeSpatials = new ArrayList<>(0);

    @XMLValue
    @ListOfClass(UpgradeSpatial.class)
    private ArrayList<UpgradeSpatial> upgradeSpatials = new ArrayList<>(0);

    @XMLValue
    @ListOfClass(TerrainSpatial.class)
    private ArrayList<TerrainSpatial> terrainSpatials = new ArrayList<>();

    @XMLValue
    @ListOfClass(GeoTiffSpatial.class)
    private ArrayList<GeoTiffSpatial> geoTiffSpatials = new ArrayList<>();

    @XMLValue
    @ListOfClass(GridSpatial.class)
    private ArrayList<GridSpatial> gridSpatials = new ArrayList<>();

    @XMLValue
    private Double actualHeightChangeM3 = null;

    // (Frank) no need to save this, only used at runtime. Remove when popups support list of items
    private Set<Integer> buildingPermitProcessed = null;

    @JsonIgnore
    private final Map<String, MultiPolygon> polygonCache = new ConcurrentHashMap<>(); // client cache

    @JsonIgnore
    private final Map<Layer, MultiPolygon> demolishCache = new ConcurrentHashMap<>(); // client cache

    @JsonIgnore
    private transient MultiPolygon landPolygonCache = null;

    public MapMeasure() {
        super();
    }

    private final List<MultiPolygon> _getPolygons(MeasureEditType[] editTypes, Layer... layer) {

        if (layer == null || layer.length == 0) {
            return new ArrayList<>();
        }

        List<MultiPolygon> combinedMPs = new ArrayList<>();
        for (Layer type : layer) {
            for (MeasureEditType editType : editTypes) {
                addMeasureEditTypePolygonsToList(combinedMPs, editType, type);
            }
        }
        return combinedMPs;
    }

    public final void addBuilding(Building building) {
        buildingIDs.add(building.getID());
    }

    public GeoTiffSpatial addGeoTiffSpatial() {

        GeoTiffSpatial spatial = new GeoTiffSpatial(getHeighestID(MeasureEditType.GEOTIFF));
        this.geoTiffSpatials.add(spatial);
        return spatial;
    }

    public GridSpatial addGridSpatial() {

        GridSpatial spatial = new GridSpatial(getHeighestID(MeasureEditType.GRID));
        this.gridSpatials.add(spatial);
        return spatial;
    }

    public LeveeSpatial addLeveeSpatial(Levee levee) {

        LeveeSpatial leveeSpatial = new LeveeSpatial(getHeighestID(MeasureEditType.LEVEE), levee.getID());
        this.leveeSpatials.add(leveeSpatial);
        return leveeSpatial;

    }

    public void addMeasureEditTypePolygonsToList(List<MultiPolygon> mps, MeasureEditType measureEditType, Layer layer) {

        if (measureEditType != MeasureEditType.BUILDING && layer != Layer.SURFACE) {
            // Other MeasureEditType polygons are only added for Layer Surface
            return;
        }

        switch (measureEditType) {
            case BUILDING:
                List<Building> buildings = getBuildings();
                for (Building building : buildings) {
                    if (building.getLayer() == layer) {
                        for (Section section : building.getSections()) {
                            mps.add(JTSUtils.clone(section.getPolygons()));
                        }
                    }
                }
                break;
            case UPGRADE:
                for (UpgradeSpatial upgradeSpatial : upgradeSpatials) {
                    mps.add(upgradeSpatial.getMultiPolygon());
                }
                break;
            case LEVEE:
                for (LeveeSpatial leveeSpatial : leveeSpatials) {
                    mps.add(leveeSpatial.getMultiPolygon());
                }
                break;
            case GEOTIFF:
                for (GeoTiffSpatial tiffSpatial : geoTiffSpatials) {
                    mps.add(tiffSpatial.getMultiPolygon());
                }
                break;
            case GRID:
                for (GridSpatial gridSpatial : gridSpatials) {
                    mps.add(gridSpatial.getMultiPolygon());
                }
                break;
            case RAISE:
            case FLATTEN:
            case WATER:
                for (TerrainSpatial terrainSpatial : getTerrainSpatialsForEditType(measureEditType)) {
                    mps.add(terrainSpatial.getOuterMultiPolygon());
                }
                break;
            default:
        }
    }

    public TerrainSpatial addTerrainSpatial(MeasureEditType editType) {
        return addTerrainSpatial(editType, editType.getDefaultTerrainTypeID());
    }

    public TerrainSpatial addTerrainSpatial(MeasureEditType editType, Integer terrainTypeID) {

        TerrainSpatial spatial = new TerrainSpatial(editType, getHeighestID(editType), terrainTypeID);
        this.terrainSpatials.add(spatial);
        return spatial;
    }

    public UpgradeSpatial addUpgradeSpatial() {

        ItemMap<UpgradeType> upgrades = this.getMap(MapLink.UPGRADE_TYPES);
        for (UpgradeType upgrade : upgrades) {
            UpgradeSpatial upgradeSpatial = new UpgradeSpatial(getHeighestID(MeasureEditType.UPGRADE), upgrade.getID());
            this.upgradeSpatials.add(upgradeSpatial);
            return upgradeSpatial;
        }
        throw new InputException("No upgrades were found in this project.");
    }

    public final boolean areBuildingPermitsProcessed() {

        if (buildingPermitProcessed != null) {
            for (Integer buildingID : getBuildingIDs()) {
                if (!buildingPermitProcessed.contains(buildingID)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Integer> getBuildingIDs() {
        return buildingIDs;
    }

    /**
     * @return the buildings
     */
    public final List<Building> getBuildings() {
        return getItems(MapLink.BUILDINGS, getBuildingIDs());
    }

    @Override
    public Point getCenterPoint() {

        if (center == null) { // lazy init
            center = JTSUtils.getCenterPoint(getPolygons());
        }
        return center;
    }

    public GeometryCollection getCombinedCollection(MeasureEditType editType) {
        return getCombinedCollection(editType, null);
    }

    public GeometryCollection getCombinedCollection(MeasureEditType editType, Layer layer) {
        return getCombinedCollection(editType, layer, null);
    }

    public GeometryCollection getCombinedCollection(MeasureEditType editType, Layer layer, Geometry g) {

        List<Geometry> geoms = new ArrayList<>();
        switch (editType) {
            case BUILDING:
                for (Building building : getBuildings()) {
                    if (layer == null || building.getLayer() == layer) {
                        List<Section> sections = building.getSections();
                        for (int i = 0; i < sections.size(); i++) {
                            if (g == null || JTSUtils.intersectsBorderIncluded(g, sections.get(i).getPolygons())) {
                                geoms.add(sections.get(i).getPolygons());
                            }
                        }
                    }
                }
                break;
            case UPGRADE:
                for (UpgradeSpatial upgradeSpatial : upgradeSpatials) {
                    if (g == null || JTSUtils.intersectsBorderIncluded(g, upgradeSpatial.getMultiPolygon())) {
                        geoms.add(upgradeSpatial.getMultiPolygon());
                    }
                }
                break;
            case LEVEE:
                for (LeveeSpatial leveeSpatial : leveeSpatials) {
                    if (g == null || JTSUtils.intersectsBorderIncluded(g, leveeSpatial.getMultiPolygon())) {
                        geoms.add(leveeSpatial.getMultiPolygon());
                    }
                }
                break;
            case GEOTIFF:
                for (GeoTiffSpatial geoTiffSpatial : geoTiffSpatials) {
                    if (g == null || JTSUtils.intersectsBorderIncluded(g, geoTiffSpatial.getMultiPolygon())) {
                        geoms.add(geoTiffSpatial.getMultiPolygon());
                    }
                }
                break;
            case GRID:
                for (GridSpatial gridSpatial : gridSpatials) {
                    if (g == null || JTSUtils.intersectsBorderIncluded(g, gridSpatial.getMultiPolygon())) {
                        geoms.add(gridSpatial.getMultiPolygon());
                    }
                }
                break;
            default:
                for (TerrainSpatial spatial : getTerrainSpatialsForEditType(editType)) {
                    if (g == null || JTSUtils.intersectsBorderIncluded(g, spatial.getOuterMultiPolygon())) {
                        geoms.add(spatial.getOuterMultiPolygon());
                    }
                }
                break;
        }
        return JTSUtils.createCollection(geoms);
    }

    @Override
    public final double getConstructionCosts() {

        if (this.constructionCostsFixed > 0.0) {
            return this.constructionCostsFixed;
        } else if (this.constructionCostsM3 > 0.0) {
            return getHeightChangeCostM3(this.constructionCostsM3);
        } else {
            return 0.0;
        }
    }

    @Override
    public double getConstructionCostsFixed() {
        return constructionCostsFixed;
    }

    public double getConstructionCostsM3() {
        return constructionCostsM3;
    }

    public final MultiPolygon getDemolishPolygons(Layer layer) {

        if (isServerSide() || !demolishCache.containsKey(layer)) {
            List<MultiPolygon> mps = new ArrayList<>();
            for (MeasureEditType editType : MeasureEditType.VALUES) {
                if (editType != MeasureEditType.UPGRADE) {
                    addMeasureEditTypePolygonsToList(mps, editType, layer);
                }
            }
            if (isServerSide()) { // no caching
                return JTSUtils.createMP(mps);
            } else {
                demolishCache.put(layer, JTSUtils.createMP(mps));
            }
        }
        return demolishCache.get(layer);
    }

    public final Envelope getEnvelope() {

        Envelope envelope = new Envelope();
        for (MultiPolygon mp : _getPolygons(MeasureEditType.VALUES, Layer.VALUES)) {
            envelope.expandToInclude(mp.getEnvelopeInternal());
        }
        return envelope;
    }

    @Override
    public Geometry getExportGeometry() {
        return getPolygons();
    }

    public Item[] getFeatures(MeasureEditType editType) {

        switch (editType) {
            case BUILDING:
                return getBuildings().toArray(Item[]::new);
            case FLATTEN:
            case WATER:
            case RAISE:
                return getTerrainSpatialsForEditType(editType).stream().mapMulti((s, c) -> {
                    for (Item f : s.getFeatures(this)) {
                        c.accept(f);
                    }
                }).toArray(Item[]::new);
            case UPGRADE:
                return upgradeSpatials.stream().map(s -> s.getFeature(this)).toArray(Item[]::new);
            case LEVEE:
                return leveeSpatials.stream().map(s -> s.getFeature(this)).toArray(Item[]::new);
            case GEOTIFF:
                return geoTiffSpatials.stream().map(s -> s.getFeature(this)).toArray(Item[]::new);
            case GRID:
                return gridSpatials.stream().map(s -> s.getFeature(this)).toArray(Item[]::new);
        }
        return new Item[0];
    }

    public Collection<? extends GeoTiff> getGeoTIFFs(Integer spatialID) {
        GeoTiffSpatial spatial = getGeoTiffSpatial(spatialID);
        List<Integer> ids = spatial == null ? new ArrayList<>() : spatial.getGeoTiffIDs();
        return getItems(MapLink.GEO_TIFFS, ids);
    }

    public GeoTiffSpatial getGeoTiffSpatial(Integer spatial) {

        for (int i = 0; i < geoTiffSpatials.size(); i++) {
            if (geoTiffSpatials.get(i).getID().equals(spatial)) {
                return geoTiffSpatials.get(i);
            }
        }
        return null;
    }

    public List<GeoTiffSpatial> getGeoTiffSpatials() {
        return geoTiffSpatials;
    }

    public GridSpatial getGridSpatial(Integer spatial) {

        for (int i = 0; i < gridSpatials.size(); i++) {
            if (gridSpatials.get(i).getID().equals(spatial)) {
                return gridSpatials.get(i);
            }
        }
        return null;
    }

    public List<GridSpatial> getGridSpatials() {
        return gridSpatials;
    }

    private int getHeighestID(MeasureEditType editType) {
        int heighestID = 0;
        for (AbstractSpatial spatial : getList(editType)) {
            heighestID = Math.max(heighestID, spatial.getID());
        }
        return heighestID + 1;
    }

    public double getHeightChange() {
        return actualHeightChangeM3;
    }

    private double getHeightChangeCostM3(double costM3) {

        if (actualHeightChangeM3 != null) {
            // cost is always positive for removal and addition the its same
            return Math.abs(actualHeightChangeM3) * costM3;
        }
        return 0.0;
    }

    public MultiPolygon getLandPolygons() {

        if (landPolygonCache == null || isServerSide()) {

            List<MultiPolygon> mps = new ArrayList<>();
            for (TerrainSpatial terrainSpatial : terrainSpatials) {
                mps.add(terrainSpatial.getOuterMultiPolygon());
            }
            for (LeveeSpatial leveeSpatial : leveeSpatials) {
                mps.add(leveeSpatial.getMultiPolygon());
            }
            for (GeoTiffSpatial spatial : geoTiffSpatials) {
                mps.add(spatial.getMultiPolygon());
            }
            for (GridSpatial gridSpatial : gridSpatials) {
                mps.add(gridSpatial.getMultiPolygon());
            }
            landPolygonCache = JTSUtils.createMP(mps);

        }
        return landPolygonCache;
    }

    public LeveeSpatial getLeveeSpatial(Integer measureParamID) {
        for (LeveeSpatial leveeSpatial : leveeSpatials) {
            if (leveeSpatial.getID().equals(measureParamID)) {
                return leveeSpatial;
            }
        }
        return null;
    }

    public List<LeveeSpatial> getLeveeSpatials() {
        return leveeSpatials;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractSpatial> List<S> getList(MeasureEditType editType) {

        switch (editType) {
            case BUILDING:
                return null;
            case UPGRADE:
                return (List<S>) upgradeSpatials;
            case LEVEE:
                return (List<S>) leveeSpatials;
            case GEOTIFF:
                return (List<S>) geoTiffSpatials;
            case GRID:
                return (List<S>) gridSpatials;
            case FLATTEN:
            case WATER:
            case RAISE:
                return (List<S>) terrainSpatials;
            default:
                return null;
        }
    }

    public Collection<? extends Overlay> getOverlays(Integer spatialID) {

        GridSpatial spatial = getGridSpatial(spatialID);
        List<Integer> ids = spatial == null ? new ArrayList<>() : spatial.getOverlayIDs();
        return getItems(MapLink.OVERLAYS, ids);
    }

    /**
     * Returns all polygons related to this measure.
     *
     * @return
     */
    public final MultiPolygon getPolygons() {
        return getPolygons(MeasureEditType.VALUES, Layer.VALUES);
    }

    /**
     * Returns all polygons related to this measure for the specified layers.
     *
     * @return
     */
    public final MultiPolygon getPolygons(Layer... layers) {
        return getPolygons(MeasureEditType.VALUES, layers);
    }

    /**
     * Returns the polygons related to this measure for the specified edit types and layers.
     *
     * @return
     */
    public final MultiPolygon getPolygons(MeasureEditType[] editTypes, Layer... layer) {

        if (isServerSide()) { // server side always create new map
            return JTSUtils.createMP(_getPolygons(editTypes, layer));

        } else { // client caches
            String key = StringUtils.arrayToHumanString(editTypes, StringUtils.EMPTY)
                    + StringUtils.arrayToHumanString(layer, StringUtils.EMPTY);
            return polygonCache.computeIfAbsent(key, k -> JTSUtils.createMP(_getPolygons(editTypes, layer)));
        }
    }

    @Override
    public final MultiPolygon[] getQTGeometries() {
        List<MultiPolygon> mps = _getPolygons(MeasureEditType.VALUES, Layer.VALUES);
        return mps.toArray(new MultiPolygon[mps.size()]);
    }

    @Override
    public Integer getRelationID(Relation relation) {
        if (relation == Relation.BUILDING) {
            return buildingIDs.size() == 0 ? Item.NONE : buildingIDs.get(0);
        } else {
            return super.getRelationID(relation);
        }

    }

    public TerrainSpatial getTerrainSpatial(Integer terrainSpatialID) {

        for (TerrainSpatial terrainSpatial : terrainSpatials) {
            if (terrainSpatial.getID().equals(terrainSpatialID)) {
                return terrainSpatial;
            }
        }
        return null;
    }

    public List<TerrainSpatial> getTerrainSpatials() {
        return terrainSpatials;
    }

    public List<TerrainSpatial> getTerrainSpatialsForEditType(MeasureEditType editType) {

        List<TerrainSpatial> spatials = new ArrayList<>();
        for (TerrainSpatial terrainSpatial : terrainSpatials) {
            if (editType == terrainSpatial.getMeasureEditType()) {
                spatials.add(terrainSpatial);
            }
        }
        return spatials;
    }

    public UpgradeSpatial getUpgradeSpatial(Integer spatialID) {

        for (UpgradeSpatial upgradeSpatial : upgradeSpatials) {
            if (upgradeSpatial.getID().equals(spatialID)) {
                return upgradeSpatial;
            }
        }
        return null;
    }

    public List<UpgradeSpatial> getUpgradeSpatials() {
        return upgradeSpatials;
    }

    public boolean hasGeoTiffID(Integer tiffID) {

        for (int i = 0; i < geoTiffSpatials.size(); i++) {
            if (geoTiffSpatials.get(i).hasGeoTiffID(tiffID)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGridID(Integer overlayID) {
        for (int i = 0; i < gridSpatials.size(); i++) {
            if (gridSpatials.get(i).hasOverlayID(overlayID)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMeasureEditType(MeasureEditType editType) {
        switch (editType) {
            case BUILDING:
                return !buildingIDs.isEmpty();
            case GRID:
                return !gridSpatials.isEmpty();
            case GEOTIFF:
                return !geoTiffSpatials.isEmpty();
            case LEVEE:
                return !leveeSpatials.isEmpty();
            case UPGRADE:
                return !upgradeSpatials.isEmpty();
            case FLATTEN:
            case RAISE:
            case WATER:
                for (int i = 0; i < terrainSpatials.size(); i++) {
                    if (terrainSpatials.get(i).getMeasureEditType() == editType) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    public boolean hasTerrainSpatial(Integer terrainSpatialID) {
        return getTerrainSpatial(terrainSpatialID) == null;
    }

    public boolean hasTerrainSpatialForEditType(MeasureEditType editType) {

        for (TerrainSpatial terrainSpatial : terrainSpatials) {
            if (editType == terrainSpatial.getMeasureEditType() && JTSUtils.hasArea(terrainSpatial.getOuterMultiPolygon())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPhysical() {

        for (MultiPolygon mp : _getPolygons(MeasureEditType.VALUES, Layer.VALUES)) {
            if (JTSUtils.hasArea(mp)) {
                return true;
            }
        }
        return false;
    }

    private boolean isServerSide() {
        return this.getLord().getSessionType() == Network.SessionType.EDITOR && this.getLord().isServerSide();
    }

    public boolean isWaterMeasure() {
        return hasTerrainSpatialForEditType(MeasureEditType.WATER);
    }

    public boolean removeBuildingID(Integer id) {
        return buildingIDs.remove(id);
    }

    public boolean removeGeoTiffSpatial(Integer tiffSpatialID) {

        for (GeoTiffSpatial tiffSpatial : geoTiffSpatials) {
            if (tiffSpatial.getID().equals(tiffSpatialID)) {
                return geoTiffSpatials.remove(tiffSpatial);
            }
        }
        return false;
    }

    public boolean removeGridSpatial(Integer gridSpatialID) {

        for (GridSpatial gridSpatial : gridSpatials) {
            if (gridSpatial.getID().equals(gridSpatialID)) {
                return gridSpatials.remove(gridSpatial);
            }
        }
        return false;
    }

    public boolean removeLeveeSpatial(Integer spatialID) {

        for (LeveeSpatial leveeSpatial : leveeSpatials) {
            if (leveeSpatial.getID().equals(spatialID)) {
                return leveeSpatials.remove(leveeSpatial);
            }
        }
        return false;
    }

    public boolean removeTerrainSpatial(Integer terrainSpatialID) {

        for (TerrainSpatial terrainSpatial : terrainSpatials) {
            if (terrainSpatial.getID().equals(terrainSpatialID)) {
                return terrainSpatials.remove(terrainSpatial);
            }
        }
        return false;
    }

    public boolean removeUpgradeSpatial(Integer upgradeSpatialID) {

        for (UpgradeSpatial upgradeSpatial : upgradeSpatials) {
            if (upgradeSpatial.getID().equals(upgradeSpatialID)) {
                return upgradeSpatials.remove(upgradeSpatial);
            }
        }
        return false;
    }

    @Override
    public void reset() {

        super.reset();
        center = null;
        polygonCache.clear();
        demolishCache.clear();
        landPolygonCache = null;
        List<MultiPolygon> mps = new ArrayList<>();

        for (MeasureEditType editType : MeasureEditType.VALUES) {
            switch (editType) {
                case BUILDING:
                    break;
                default:
                    for (Layer layer : Layer.VALUES) {
                        addMeasureEditTypePolygonsToList(mps, editType, layer);
                    }
                    break;
            }
        }

        for (MultiPolygon mp : mps) {
            JTSUtils.clearUserData(mp);
        }
    }

    public final void setBuildingPermitProcessed(Integer buildingID) {
        if (buildingPermitProcessed == null) {
            buildingPermitProcessed = new HashSet<>();
        }
        buildingPermitProcessed.add(buildingID);
    }

    public void setConstructionCostsM3(double costs) {
        this.constructionCostsM3 = costs;
    }

    public void setCostM3(CostType costType, double value) {
        switch (costType) {
            case CONSTRUCTION:
                this.constructionCostsM3 = value;
                break;
        }
    }

    public void setHeightChange(double heightChangeM3) {
        this.actualHeightChangeM3 = heightChangeM3;
    }

    @Override
    protected void updateInternalVersion(int version) {
        this.center = null; // reset center point after updates
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = StringUtils.EMPTY;

        // lazy validate center
        getCenterPoint();

        /**
         * Validate building linkage and timestate.
         */
        for (Building building : this.getBuildings()) {
            if (!building.getMeasureID().equals(this.getID())) {
                result += "\nMeasure: " + this.getName() + " links to building: " + building.getName()
                        + " that does not have a valid measure link back.";
            }
        }
        /**
         * Validate links in children
         */
        for (LeveeSpatial s : leveeSpatials) {
            result += validFields(s);
        }
        for (GeoTiffSpatial s : geoTiffSpatials) {
            result += validFields(s);
        }
        for (GridSpatial s : gridSpatials) {
            result += validFields(s);
        }

        for (UpgradeSpatial s : upgradeSpatials) {
            result += validFields(s);
        }
        for (TerrainSpatial s : terrainSpatials) {
            if (Item.NONE.equals(s.getTerrainTypeID())) {
                s.setTerrainTypeID(s.getMeasureEditType().getDefaultTerrainTypeID());
            }
            result += validFields(s);
        }
        return result + super.validated(startNewSession);
    }
}
