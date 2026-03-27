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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.IndexSortedItem;
import nl.tytech.data.engine.item.AvgOverlay.AvgModelAttribute;
import nl.tytech.data.engine.item.ComboOverlay.ComboModelAttribute;
import nl.tytech.data.engine.item.DistanceOverlay.SightDistanceAttribute;
import nl.tytech.data.engine.item.DistanceOverlay.ZoneDistanceAttribute;
import nl.tytech.data.engine.item.GeoTiffOverlay.GeoTiffAttribute;
import nl.tytech.data.engine.item.HeatOverlay.HeatDpraAttribute;
import nl.tytech.data.engine.item.HeightOverlay.HeightAttribute;
import nl.tytech.data.engine.item.InferenceOverlay.InferenceAttribute;
import nl.tytech.data.engine.item.IterationOverlay.IterationAttribute;
import nl.tytech.data.engine.item.NO2Overlay.TrafficNO2Attribute;
import nl.tytech.data.engine.item.NoiseOverlay.TrafficNoiseAttribute;
import nl.tytech.data.engine.item.Setting.Type;
import nl.tytech.data.engine.item.ShadowOverlay.ShadowAttribute;
import nl.tytech.data.engine.item.SubsidenceOverlay.SubsidenceAttribute;
import nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute;
import nl.tytech.data.engine.item.TravelDistanceOverlay.TravelDistanceAttribute;
import nl.tytech.data.engine.item.WaterOverlay.WaterModelAttribute;
import nl.tytech.data.engine.item.WatershedOverlay.WatershedModelAttribute;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.naming.GeoNC;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * Overlay: Overlays are put over the map and give extra details.
 *
 * @author Maxim Knepfle
 */
public class Overlay extends AttributeItem implements IndexSortedItem, ImageItem {

    public enum OverlayCategory {

        ADMINISTRATIVE,

        ENVIRONMENTAL,

        WATER,

        GRID_CALCULATION,

        GEO_DATA,

        TOPOGRAPHY,

        NETWORKS;

    }

    public enum OverlayType {

        /**
         * ADMINISTRATIVE
         */

        OWNERSHIP(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        OWNERSHIP_GRID(OverlayCategory.ADMINISTRATIVE, DefaultOverlay.class, (FunctionValue) null),

        ZIP_CODES(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        ZONING(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        NEIGHBORHOODS(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        MUNICIPALITIES(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        SCENARIO(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        @Deprecated(since = "To be non functional in LTS 2028, for LTS 2026 mark as deprecated")
        VACANCY(OverlayCategory.ADMINISTRATIVE, Overlay.class),

        /**
         * ENVIRONMENTAL
         */

        HEAT_STRESS(OverlayCategory.ENVIRONMENTAL, HeatOverlay.class, FunctionValue.HEAT_EFFECT),

        SHADOW(OverlayCategory.ENVIRONMENTAL, ShadowOverlay.class, (FunctionValue) null),

        TRAFFIC_DENSITY(OverlayCategory.ENVIRONMENTAL, TrafficDensityOverlay.class),

        TRAFFIC_NO2(OverlayCategory.ENVIRONMENTAL, NO2Overlay.class, (FunctionValue) null),

        TRAFFIC_NOISE(OverlayCategory.ENVIRONMENTAL, NoiseOverlay.class, (FunctionValue) null),

        LIVABILITY(OverlayCategory.ENVIRONMENTAL, AvgOverlay.class, FunctionValue.LIVABILITY_EFFECT),

        @Deprecated(since = "Non functional, only keep reference for conversion to deprecated overlay")
        AERIUS(OverlayCategory.ENVIRONMENTAL, AeriusOverlay.class),

        /**
         * WATER
         */

        RAINFALL(OverlayCategory.WATER, RainOverlay.class, (FunctionValue) null),

        FLOODING(OverlayCategory.WATER, FloodingOverlay.class, (FunctionValue) null),

        GROUNDWATER(OverlayCategory.WATER, GroundwaterOverlay.class, (FunctionValue) null),

        WATERSHED(OverlayCategory.WATER, WatershedOverlay.class, (FunctionValue) null),

        @Deprecated(since = "To be non functional in LTS 2028, for LTS 2026 mark as deprecated")
        SUBSIDENCE(OverlayCategory.WATER, SubsidenceOverlay.class, (FunctionValue) null),

        @Deprecated(since = "Non functional, only keep reference for conversion to deprecated overlay")
        WATERWIJZER(OverlayCategory.WATER, WWOverlay.class),

        /**
         * GRID CALCULATION
         */

        AVG(OverlayCategory.GRID_CALCULATION, AvgOverlay.class, (FunctionValue) null),

        COMBO(OverlayCategory.GRID_CALCULATION, ComboOverlay.class, (FunctionValue) null),

        DISTANCE(OverlayCategory.GRID_CALCULATION, DistanceOverlay.class, FunctionValue.DISTANCE_ZONE_M),

        SIGHT_DISTANCE(OverlayCategory.GRID_CALCULATION, DistanceOverlay.class, FunctionValue.DISTANCE_ZONE_M),

        TRAVEL_DISTANCE(OverlayCategory.GRID_CALCULATION, TravelDistanceOverlay.class, (FunctionValue) null),

        INFERENCE(OverlayCategory.GRID_CALCULATION, InferenceOverlay.class, (FunctionValue) null),

        ITERATION(OverlayCategory.GRID_CALCULATION, IterationOverlay.class, (FunctionValue) null),

        /**
         * GEO DATA
         */

        GEO_TIFF(OverlayCategory.GEO_DATA, GeoTiffOverlay.class, (FunctionValue) null),

        WMS(OverlayCategory.GEO_DATA, WMSOverlay.class, (FunctionValue) null),

        WCS(OverlayCategory.GEO_DATA, WCSOverlay.class, (FunctionValue) null),

        IMAGE(OverlayCategory.GEO_DATA, ImageOverlay.class),

        AREA(OverlayCategory.GEO_DATA, AreaOverlay.class),

        ATTRIBUTE(OverlayCategory.GEO_DATA, AttributeOverlay.class),

        FUNCTION(OverlayCategory.GEO_DATA, FunctionOverlay.class),

        /**
         * TOPOGRAPHY
         */

        SATELLITE(OverlayCategory.TOPOGRAPHY, SatelliteOverlay.class, (FunctionValue) null),

        HEIGHTMAP(OverlayCategory.TOPOGRAPHY, HeightOverlay.class, (FunctionValue) null),

        UNDERGROUND(OverlayCategory.TOPOGRAPHY, Overlay.class),

        SOURCE(OverlayCategory.TOPOGRAPHY, SourceOverlay.class),

        /**
         * NETWORK
         */

        NETWORK_OWNERSHIP(OverlayCategory.NETWORKS, NetOverlay.class),

        NETWORK_OVERVIEW(OverlayCategory.NETWORKS, NetOverlay.class),

        NETWORK_DISTANCE(OverlayCategory.NETWORKS, DefaultOverlay.class, (FunctionValue) null),

        /**
         * Deprecated and others
         */

        RESULT_CHILD(null, ResultChildOverlay.class, (FunctionValue) null),

        TEST(null, DefaultOverlay.class, (FunctionValue) null),

        @Deprecated(since = "To be non functional in LTS 2028, for LTS 2026 mark as deprecated")
        DISTURBANCE_DISTANCE(OverlayCategory.GRID_CALCULATION, DistanceOverlay.class, FunctionValue.DISTURBANCE_DISTANCE_M),

        @Deprecated(since = "To be non functional in LTS 2028, for LTS 2026 mark as deprecated")
        SAFETY_DISTANCE(OverlayCategory.GRID_CALCULATION, DistanceOverlay.class, FunctionValue.SAFETY_DISTANCE_M),

        @Deprecated
        DEPRECATED(OverlayCategory.GEO_DATA, Overlay.class);

        public static final OverlayType[] getActiveValues() {
            return getActiveValues(null);
        }

        public static final OverlayType[] getActiveValues(OverlayCategory myCat) {

            List<OverlayType> types = new ArrayList<>();
            for (OverlayType type : OverlayType.values()) {
                Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
                if (depAnno != null || type.cat == null || type == TEST) {
                    continue;
                }
                if (myCat == null || myCat == type.cat) {
                    types.add(type);
                }
            }
            if (myCat == null) { // sort when not asking specific category, beta overlays go last
                types.sort((o1, o2) -> o1.isBeta() ? 1 : o1.name().compareTo(o2.name()));
            }
            return types.toArray(new OverlayType[types.size()]);
        }

        private final OverlayCategory cat;

        private final Class<? extends Overlay> clasz;

        private ReservedAttribute[] defaultAttributes = new ReservedAttribute[0];

        private FunctionValue effect = null;

        private boolean grid = false;

        /**
         * Normal overlay
         */
        private OverlayType(OverlayCategory cat, Class<? extends Overlay> clasz) {
            this.cat = cat;
            this.clasz = clasz;
        }

        /**
         * Grid overlay
         */
        private <A extends GridOverlay<?, ?>> OverlayType(OverlayCategory cat, Class<A> clasz, FunctionValue effect) {
            this(cat, clasz);
            this.grid = true;
            this.effect = effect;
        }

        public final ReservedAttribute[] getDefaultAttributes() {

            return switch (this) {
                case RAINFALL, FLOODING, GROUNDWATER -> WaterModelAttribute.values();
                case TRAFFIC_DENSITY -> TrafficAttribute.values();
                case TRAFFIC_NO2 -> ObjectUtils.toArray(ReservedAttribute.class, TrafficAttribute.values(), TrafficNO2Attribute.values());
                case TRAFFIC_NOISE -> ObjectUtils.toArray(ReservedAttribute.class, TrafficAttribute.values(),
                        TrafficNoiseAttribute.values());
                case HEAT_STRESS -> HeatDpraAttribute.values();
                case SHADOW -> ShadowAttribute.values();
                case AVG -> AvgModelAttribute.values();
                case COMBO -> ComboModelAttribute.values();
                case SUBSIDENCE -> SubsidenceAttribute.values();
                case INFERENCE -> InferenceAttribute.values();
                case ITERATION -> IterationAttribute.values();
                case WATERSHED -> WatershedModelAttribute.values();
                case TRAVEL_DISTANCE -> TravelDistanceAttribute.values();
                case DISTANCE -> ZoneDistanceAttribute.values();
                case SIGHT_DISTANCE -> SightDistanceAttribute.values();
                case HEIGHTMAP -> HeightAttribute.values();
                case GEO_TIFF -> GeoTiffAttribute.values();
                default -> defaultAttributes;
            };
        }

        public final FunctionValue getEffect() {
            return effect;
        }

        public final Overlay getNewInstance() {
            return ObjectUtils.newInstanceForArgs(clasz, new Object[0]);
        }

        public final Class<? extends Overlay> getOverlayClass() {
            return clasz;
        }

        public final boolean isBeta() {
            return this == AERIUS || this == WATERWIJZER;
        }

        public final boolean isDeprecated() {
            return ObjectUtils.getEnumAnnotation(this, Deprecated.class) != null;
        }

        public final boolean isGrid() {
            return grid;
        }

        @Override
        public String toString() {

            return switch (this) {
                case WMS -> "WMS";
                case WCS -> "WCS";
                case GEO_TIFF -> GeoNC.GEOTIFF;
                case AVG -> "Avg & Interpolation";
                case TRAFFIC_NO2 -> "Traffic NO2";
                case INFERENCE -> "AI Inference";
                case ITERATION -> "Iteration Collector";
                default -> StringUtils.capitalizeWithSpacedUnderScores(this);
            };
        }
    }

    /**
     * Sort on parent, index and otherwise alphabetic
     */
    public static final Comparator<Overlay> PARENT_INDEX_NAME_SORT = (a, b) -> {

        // same or no parent (-1)
        if (a.getParentID().equals(b.getParentID())) {
            return INDEX_NAME_SORT.compare(a, b);
        }

        // use parent as index or myself
        Overlay pa = a.getParent();
        int ai = pa != null ? pa.getSortIndex() : a.getSortIndex();
        Overlay pb = b.getParent();
        int bi = pb != null ? pb.getSortIndex() : b.getSortIndex();

        return Integer.compare(ai, bi);
    };

    public static final String OVERLAY_IMAGE_LOCATION = "Gui/Images/Panels/MapPanel/Icons/";

    private static final long serialVersionUID = 3663268509035999152L;

    private static final String NORMAL = "normal" + DOT_EXTENSION;

    @XMLValue
    private double colorMultiplier = 1;

    @XMLValue
    private int colorOffset = 127;

    @XMLValue
    private boolean customLegend = false;

    @XMLValue
    @ListOfClass(LegendEntry.class)
    private ArrayList<LegendEntry> legend = new ArrayList<>();

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private Integer parentID = Item.NONE;

    @XMLValue
    private int sortIndex = 50;

    @XMLValue
    private boolean visible = true;

    @XMLValue
    @NoDefaultText
    @AssetDirectory(OVERLAY_IMAGE_LOCATION)
    private String imageName = NORMAL;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @XMLValue
    private OverlayType type = null;

    @JsonIgnore
    private transient Integer maxEntryID = null;

    @JsonIgnore
    private transient Integer diffMaxEntryID = null;

    public void addLegendEntry(boolean diff, LegendEntry entry) {
        setEntryID(diff, entry);
        getLegend(diff).add(entry);
    }

    public boolean calcSelfPrequel() {
        return true; // override when needed
    }

    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return 1; // by default only one
    }

    public void clearLegend() {
        clearLegend(false);
    }

    public void clearLegend(boolean diff) {
        getLegend(diff).clear();
        if (diff) {
            diffMaxEntryID = null;
        } else {
            maxEntryID = null;
        }
    }

    private Integer findMaxEntryID(boolean difference) {
        int maxID = 0;
        List<LegendEntry> entries = getLegend(difference);
        for (int i = 0; i < entries.size(); i++) {
            maxID = Math.max(maxID, entries.get(i).getID());
        }
        return maxID;
    }

    public String getAbstract() {
        return StringUtils.capitalizeWithSpacedUnderScores(getType());
    }

    public LegendEntry getBestEntry(double value) {
        LegendEntry best = null;
        for (LegendEntry entry : getLegend()) {
            if (best == null || entry.hasValue() && Math.abs(entry.getValue() - value) < Math.abs(best.getValue() - value)) {
                best = entry;
            }
        }
        return best;
    }

    public long getCalcTimeMS() {
        return 0; // by default no time
    }

    public int getChildCount() {

        int count = 0;
        ItemMap<Overlay> overlays = getMap(MapLink.OVERLAYS);
        for (Overlay overlay : overlays) {
            if (overlay.getParentID().equals(this.getID())) {
                count++;
            }
        }
        return count;
    }

    public List<? extends Overlay> getChildren() {

        List<Overlay> result = new ArrayList<>();
        ItemMap<Overlay> overlays = getMap(MapLink.OVERLAYS);
        for (Overlay overlay : overlays) {
            if (overlay.getParentID().equals(this.getID())) {
                result.add(overlay);
            }
        }
        return result;
    }

    public double getColorMultiplier() {
        return colorMultiplier;
    }

    public int getColorOffset() {
        return colorOffset;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return getType() != null ? getType().getDefaultAttributes() : new ReservedAttribute[0];
    }

    public String getDefaultImageName() {
        return type == null ? NORMAL : type.name().toLowerCase() + DOT_EXTENSION;
    }

    @Override
    public String getImageLocation() {
        return OVERLAY_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public final int getImageVersion() {
        return imageVersion;
    }

    public final int getLastTimeframe() {
        return getTimeframes() - 1;
    }

    public List<LegendEntry> getLegend() {
        return legend;
    }

    public List<LegendEntry> getLegend(boolean showGridDifference) {
        return getLegend();
    }

    public LegendEntry getLegendEntry(boolean diff, Integer id) {

        for (LegendEntry entry : getLegend(diff)) {
            if (entry.getID().equals(id)) {
                return entry;
            }
        }
        return null;
    }

    public Overlay getParent() {
        return getItem(MapLink.OVERLAYS, getParentID());
    }

    @Override
    public Integer getParentID() {
        return parentID;
    }

    @Override
    public int getSortIndex() {
        return sortIndex;
    }

    public String getStateMessage() {

        if (type != null) {
            if (type.isDeprecated()) {
                return "DEPRECATED";
            }
            if (type.isBeta()) {
                return "BETA";
            }
        }
        return StringUtils.EMPTY;
    }

    public final int getTimeframes() {
        return getTimeframes(new TreeMap<>());
    }

    public final int getTimeframes(Map<Integer, Integer> cache) {

        Integer timeframes = cache.get(getID());
        if (timeframes == null) {
            timeframes = calcTimeframes(cache);
            cache.put(getID(), timeframes);
        }
        return timeframes;
    }

    public final String getTimeframeText(int timeframe) {

        // lord maybe null for e.g. ShareOverlay
        String format = getLord() == null ? Type.TIMESTAMP_FORMAT.getDefaultValue()
                : ((Setting) getItem(MapLink.SETTINGS, Type.TIMESTAMP_FORMAT)).getValue();
        return getTimeframeText(timeframe, format);
    }

    public String getTimeframeText(int timeframe, String format) {
        return Integer.toString(timeframe); // default override with specific
    }

    public long getTimeframeTimeSec(int timeframe) {
        return 0; // default only one
    }

    public OverlayType getType() {
        return type;
    }

    public int getValidTimeframe(Integer inputFrame) {

        if (inputFrame == null) {
            return getLastTimeframe();
        } else {
            return MathUtils.clamp(inputFrame, 0, getLastTimeframe());
        }
    }

    public boolean hasCustomLegend() {

        if (isCustomLegendAllowed()) {
            return customLegend;
        } else {
            return false;// others are always without custom legend
        }
    }

    public boolean hasValueLegend() {
        return false;
    }

    protected final void incrementImageVersion() {
        this.imageVersion++;
    }

    public boolean isActive() {
        return true; // always active, grid may override this
    }

    public boolean isCustomLegendAllowed() {
        return getType() != null && (getType().isGrid() || getType() == OverlayType.AREA || getType() == OverlayType.ATTRIBUTE
                || getType() == OverlayType.IMAGE || getType() == OverlayType.ZONING || getType() == OverlayType.TRAFFIC_DENSITY);
    }

    public boolean isShowDifference() {
        return false;
    }

    /**
     * Check if this overlay is a valid prequel to the given parent PrequeledOverlay.
     */
    public boolean isValidPrequelTo(GridOverlay<?, ?> parent) {
        return isValidPrequelTo(parent, false);
    }

    /**
     * Check if this overlay is a valid prequel to the given parent PrequeledOverlay.
     */
    public boolean isValidPrequelTo(GridOverlay<?, ?> parent, boolean previousIteration) {

        // parent cannot be null
        if (parent == null) {
            return false;
        }
        // prequel must be grid and parent not a child
        if (!(this instanceof GridOverlay) || parent instanceof ResultChildOverlay<?, ?>) {
            return false;
        }
        // sometimes I can prequel myself or my children, except with timeframe calculation (e.g. combo, avg and (travel) distance)
        if (parent.getID().equals(getID()) || parent instanceof ResultParentOverlay<?, ?> rp && rp.hasResultChild(getID())) {
            return parent.calcSelfPrequel();
        }
        // when doing previous iteration this is allowed
        if (previousIteration && parent.isPreviousIterationAllowed()) {
            return true;
        }
        // otherwise prequel may not already have the parent
        if (this instanceof GridOverlay<?, ?> go && go.hasDeepPrequel(parent)) {
            return false;
        }
        // this overlay is valid prequel for the parent
        return true;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isVisibleForStakeholder(Stakeholder stakeholder) {
        return stakeholder != null && visible;
    }

    public LegendEntry removeLegendEntry(boolean diff, Integer id) {
        LegendEntry entry = getLegendEntry(diff, id);
        if (entry != null) {
            getLegend(diff).remove(entry);

            if (diff && entry.getID().equals(diffMaxEntryID)) {
                diffMaxEntryID = null;
            } else if (!diff && entry.getID().equals(maxEntryID)) {
                maxEntryID = null;
            }
        }
        return entry;
    }

    public void setCustomLegend(boolean customLegend) {
        this.customLegend = customLegend;
    }

    private void setEntryID(boolean difference, LegendEntry entry) {

        Integer maxID = difference ? diffMaxEntryID : maxEntryID;
        if (maxID == null) {
            maxID = findMaxEntryID(difference);
        }

        maxID += 1;

        entry.setID(maxID);

        if (difference) {
            diffMaxEntryID = maxID;
        } else {
            maxEntryID = maxID;
        }

    }

    @Override
    public void setImageName(String iconName) {
        this.imageName = iconName;
        this.incrementImageVersion();
    }

    public void setParentID(Integer parentOverlayID) {
        this.parentID = parentOverlayID;
    }

    @Override
    public final void setSortIndex(int index) {
        this.sortIndex = index;
    }

    public void setType(OverlayType overlayType) {
        this.type = overlayType;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        for (boolean diff : new boolean[] { Boolean.FALSE, Boolean.TRUE }) {
            for (LegendEntry entry : getLegend(diff)) {
                if (entry.getID().equals(Item.NONE)) {
                    setEntryID(diff, entry);
                }
            }
        }
        if (NORMAL.equals(imageName) && type != null) {
            imageName = getDefaultImageName();
        }
        return result;
    }
}
