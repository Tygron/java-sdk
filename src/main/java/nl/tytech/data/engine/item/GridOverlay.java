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

import static nl.tytech.data.core.serializable.MapType.CURRENT;
import static nl.tytech.data.core.serializable.MapType.MAQUETTE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cjava.FrameVar;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GPUJob;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.LargeCloneItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.InferenceOverlay.InferenceResult;
import nl.tytech.data.engine.item.Setting.Size;
import nl.tytech.data.engine.item.TravelDistanceOverlay.TravelDistanceResult;
import nl.tytech.data.engine.item.WMSOverlay.WMSResult;
import nl.tytech.data.engine.item.WaterOverlay.WaterResult;
import nl.tytech.data.engine.item.WatershedOverlay.WatershedResult;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.data.engine.serializable.GridData;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.data.engine.serializable.PrequelLink;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.EnumUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Overlays based on calculated grid data
 *
 * @author Maxim Knepfle
 */
public abstract class GridOverlay<R extends ResultType, P extends PrequelType> extends Overlay implements LargeCloneItem, ActiveItem {

    public enum GridModelAttribute implements ReservedAttribute {

        /**
         * Active for given iterations, negative is ignored
         */
        ITERATION_ACTIVE(Double.class, -1),

        /**
         * Skips no data values when updating the Overlay grid
         */
        SKIP_NO_DATA(Double.class, 0),

        /**
         * Run overlay on specific GPU cluster determined by index, negatives are ignored
         */
        GPU_CLUSTER_INDEX(Integer.class, -1)

        ;

        private final Class<?> type;
        private final double[] defaultArray;

        private GridModelAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
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

    public interface Key {

        public static String getMultiOutputKey(GridOverlay<?, ?> overlay, Key value) {
            return value.name() + overlay.getID();
        }

        public UnitType getUnitType();

        public boolean isOptional();

        public boolean isOutput();

        public String name();
    }

    public enum NoPrequel implements PrequelType {

        ;

        public static final NoPrequel[] VALUES = NoPrequel.values();
    }

    /**
     * Rasterization options: https://hackaday.com/2023/12/26/game-graphics-rasterization/
     */
    public enum RasterizationMethod {

        COMBINED("Combined: Center for large and Outline for small polygons (default)"),

        OUTLINE("Outline: Overshoot results in more cells then polygon area"),

        CENTER("Center: WARNING: fastest but missing small polygons"),

        ;

        private String fullName;

        private RasterizationMethod(String fullName) {
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }

    public enum Style {

        INTERPOLATED, NEAREST, ARROW, COLOR, FLOAT32, DIFFERENCE;

        public static boolean hasLegend(Style[] styles) {

            if (styles != null) {
                for (Style style : styles) {
                    if (style == COLOR || style == FLOAT32) {
                        return false;
                    }
                }
            }
            return true;
        }

        public static boolean isArrow(Style[] styles) {

            if (styles != null) {
                for (Style style : styles) {
                    if (style == ARROW) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isColor(Style[] styles) {

            if (styles != null) {
                for (Style style : styles) {
                    if (style == COLOR) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isDifference(Style[] styles) {

            if (styles != null) {
                for (Style style : styles) {
                    if (style == DIFFERENCE) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isInterpolated(Style[] styles) {

            if (styles != null) {
                for (Style style : styles) {
                    if (style == NEAREST) {
                        return false;
                    }
                }
                for (Style style : styles) {
                    if (style == INTERPOLATED || style == FLOAT32) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static final String toString(Style[] styles) {

            StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
            if (styles != null) {
                for (int i = 0; i < styles.length; i++) {
                    builder.append(styles[i].name());
                    if (i < styles.length - 1) {
                        builder.append(",");
                    }
                }
            }
            return builder.toString();
        }
    }

    public static final float NO_DATA = -Float.MAX_VALUE; // Lowest 32-bit signed value, same as ESRI

    public static final TColor NO_DATA_COLOR = TColor.TRANSPARENT; // Default NO_DATA color

    public static final int GRID_BYTE_RANGE = 250;

    private static final double MIN_GRID_CELLS = 100;

    private static final int MAX_TIMEFRAMES = FrameVar.MAX_FRAMES; // -> 1000 frames;

    public static final double MIN_DETAILED_CELL_M = 0.25;

    public static final double MIN_BASIC_CELL_M = 5.0;

    public static final int CELL_DECIMALS = 2;

    public static final double DEFAULT_PLAYTIME_MS = 10_000;

    private static final long serialVersionUID = 3137271350161025072L;

    public static final String DEFAULT_KEY = "KEY";

    public static final Stream<GridOverlay<?, ?>> filter(Collection<Overlay> overlays, Integer overlayID, String attributeName,
            OverlayType overlayType, String resultType) {

        return overlays.stream()
                // Grids only
                .filter(o -> o instanceof GridOverlay<?, ?>).map(o -> (GridOverlay<?, ?>) o)
                // ID
                .filter(o -> overlayID == null || overlayID.equals(o.getID()))
                // Overlay Type
                .filter(o -> overlayType == null || overlayType.equals(o.getType()))
                // Attribute
                .filter(o -> attributeName == null || o.getAttribute(attributeName) != 0.0)
                // Filter result type or map to result child
                .map(o -> resultType == null || resultType.equals(o.getResultType().name()) ? o
                        : o instanceof ResultParentOverlay<?, ?> p ? p.getResultChild(resultType) : null)
                // filter when not null
                .filter(o -> o != null).distinct();
    }

    public static final double[] generateRemainingTimeframeTimes(double[] timeframeTimes, int indexOffset, double start, double end) {

        double remainingStart = Math.max(start, timeframeTimes[indexOffset]);
        double remainingTime = Math.max(0, end - remainingStart);
        double remainingTimeframes = timeframeTimes.length - indexOffset;
        for (int i = indexOffset; i < timeframeTimes.length; i++) {
            timeframeTimes[i] = remainingStart + remainingTime / remainingTimeframes * (i + 1);
        }
        return timeframeTimes;
    }

    public static final double getMaxCellM(long dimX, long dimY) {
        return Math.max(dimX, dimY) / Math.sqrt(MIN_GRID_CELLS);
    }

    private static final GridData getUnused(MapType mapType, Item unusedItem, int index, int width, int height, int blockSize) {

        if (unusedItem instanceof GridOverlay<?, ?> unusedGrid) {
            List<GridData> list = mapType == MapType.MAQUETTE ? unusedGrid.maquette : unusedGrid.current;
            if (list != null && index >= 0 && index < list.size()) {
                GridData unusedArray = list.get(index);
                if (MathUtils.isDimension(unusedArray, width, height, blockSize)) {
                    return unusedArray;
                }
            }
        }
        return null;
    }

    public static final double[] getVerifiedTimeframeTimes(int timeframes, double start, double end, double[] timeframeTimes) {

        double[] result = new double[timeframes];

        if (Arrays.equals(AttributeItem.ZERO, timeframeTimes)) {
            timeframeTimes = AttributeItem.EMPTY;
        }
        // verify timeframetimes.
        for (int i = 0; i < timeframeTimes.length && i < result.length; i++) {
            if (i == 0) {
                result[i] = MathUtils.clamp(timeframeTimes[i], start, end);
            } else {
                result[i] = MathUtils.clamp(timeframeTimes[i], result[i - 1], end);
            }

        }
        if (timeframeTimes.length < result.length) {
            result = GridOverlay.generateRemainingTimeframeTimes(result, timeframeTimes.length, start, end);
        }
        return result;
    }

    @XMLValue
    private long calcTimeMS = 0;

    @XMLValue
    private boolean active = true;

    @XMLValue
    private boolean showDifference = false;

    @XMLValue
    @ListOfClass(LegendEntry.class)
    private ArrayList<LegendEntry> diffLegend = new ArrayList<>();

    /**
     * 32-bit accurate version, store only on server and save to XML
     */
    @JsonIgnore
    @XMLValue
    private transient ArrayList<GridData> current = new ArrayList<>();

    /**
     * 32-bit accurate version, store only on server and save to session XML
     */
    @JsonIgnore
    @XMLValue
    @DoNotSaveToInit
    private transient ArrayList<GridData> maquette = new ArrayList<>();

    @XMLValue
    private float[] maxValue = new float[] { 0f, 1f, 0f, 1f };

    @XMLValue
    private float[] minValue = new float[] { 0f, 0f, 0f, 0f };

    @XMLValue
    private Double inActiveCellSize = null;

    @XMLValue
    private Integer inActiveTimeframes = null;

    @XMLValue
    @NoDefaultText
    private String warnings = StringUtils.EMPTY;

    @XMLValue
    @NoDefaultText
    private String resultType = StringUtils.EMPTY;

    @XMLValue
    private HashMap<String, PrequelLink> prequels = new HashMap<>();

    @XMLValue
    private HashMap<String, String> keys = new HashMap<>();

    public GridOverlay() {

    }

    protected boolean calcPrequelTimeframes() {
        return false;
    }

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return isActive() || inActiveTimeframes == null ? super.calcTimeframes(cache) : inActiveTimeframes;
    }

    @Override
    public GridOverlay<R, P> cloneItem(Item unusedItem) {

        // clone base first: note do not allow clone item method
        GridOverlay<R, P> cloneOverlay = ObjectUtils.deepCopy(this, false);

        // copy arrays
        cloneOverlay.current = new ArrayList<>();
        for (int i = 0; i < current.size(); i++) {
            GridData original = this.current.get(i);
            GridData clone = getUnused(CURRENT, unusedItem, i, original.getWidth(), original.getHeight(), original.getBlockSize());
            if (clone != null) {
                // recycle old Data Object
                original.toGridData(clone);
            } else {
                // create clone of the original
                clone = original.toGridData();
            }
            cloneOverlay.current.add(clone);
        }

        cloneOverlay.maquette = new ArrayList<>();
        for (int i = 0; i < maquette.size(); i++) {
            GridData original = this.maquette.get(i);
            GridData clone = getUnused(MAQUETTE, unusedItem, i, original.getWidth(), original.getHeight(), original.getBlockSize());
            if (clone != null) {
                // recycle old Data Object
                original.toGridData(clone);
            } else {
                // create clone of the original
                clone = original.toGridData();
            }
            cloneOverlay.maquette.add(clone);
        }
        return cloneOverlay;
    }

    @Override
    public String getAbstract() {
        return StringUtils.capitalizeWithSpacedUnderScores(getType()) + ": " + getResultType();
    }

    @Override
    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    public double getCellSizeM() {

        if (isActive() || inActiveCellSize == null) {
            Setting cellSetting = this.getItem(MapLink.SETTINGS, Setting.Type.GRID_CELL_SIZE_M);
            return cellSetting.getDoubleValue();
        } else {
            return inActiveCellSize;
        }
    }

    public int getDecimals() {
        return -1; // ignored
    }

    @Override
    public String getDefaultImageName() {
        return getDefaultImageName(getResultType());
    }

    public String getDefaultImageName(R resultType) {
        return getDefaultImagePrefix() + "_" + resultType.name().toLowerCase() + DOT_EXTENSION;
    }

    /**
     * Override prefix when needed otherwise Overlay Type
     */
    protected String getDefaultImagePrefix() {
        return getType().name().toLowerCase();
    }

    protected abstract R getDefaultResult();

    /**
     * Default Style of grid data
     */
    public Style[] getDefaultStyles() {

        R r = getResultType();
        if (r == WMSResult.COLOR || getType() == OverlayType.SATELLITE) {
            return new Style[] { Style.COLOR };

        } else if (r == WaterResult.SURFACE_LAST_DIRECTION || r == WaterResult.GROUND_LAST_DIRECTION
                || r == WaterResult.SURFACE_AVG_DIRECTION || r == WatershedResult.DIRECTION) {
            return new Style[] { Style.ARROW };

        } else if (r == WatershedResult.DISCHARGE_AREAS || r == WaterResult.DEBUG_AVG_AREAS || r == WatershedResult.WATERSHEDS
                || r == TravelDistanceResult.DESTINATIONS || r == TravelDistanceResult.BASE_TYPES || r == WaterResult.BASE_TYPES
                || r == WatershedResult.BASE_TYPES || r == WaterResult.GPU_OVERVIEW || r == WaterResult.IMPACTED_BUILDINGS
                || r == WaterResult.DEBUG_UV_DIRECTION || r == WaterResult.DEBUG_WATERWAYS || r == InferenceResult.LABELS
                || r == InferenceResult.BOXES) {
            return new Style[] { Style.NEAREST };
        }
        return new Style[] { getType() == OverlayType.OWNERSHIP_GRID ? Style.NEAREST : Style.INTERPOLATED };
    }

    public GridData getGridData(MapType mapType, int timeframe) {

        GridData data = mapType == MapType.CURRENT || getRawData(MapType.MAQUETTE, timeframe).isEmpty()
                ? getRawData(MapType.CURRENT, timeframe)
                : getRawData(MapType.MAQUETTE, timeframe);

        // check if size is correct
        Size gridSize = this.getGridSize();
        if (!gridSize.isDimension(data)) {
            data = new GridData.Zero(gridSize);
            // do not update maquette
            if (mapType == MapType.CURRENT || !getRawData(MapType.MAQUETTE, timeframe).isEmpty()) {
                setRawData(mapType, timeframe, gridSize, data);
            }
        }
        return data;
    }

    public float getGridDataValue(MapType mapType, int timeframe, int x, int y) {

        GridData data = this.getGridData(mapType, timeframe);
        if (data.isEmpty() || y < 0 || y >= data.getHeight() || x < 0 || x >= data.getWidth()) {
            return 0;
        }
        return data.get(x, y);
    }

    public Size getGridSize() {

        Setting mapSetting = this.getItem(MapLink.SETTINGS, Setting.Type.MAP_SIZE_M);
        int[] mapSize = mapSetting.getIntArrayValue();
        return new Size(mapSize[Item.X], mapSize[Item.Y]).div(getCellSizeM());
    }

    public String getKey() {
        return getKey(GridOverlay.DEFAULT_KEY);
    }

    public String getKey(Key key) {
        return getKey(key.name());
    }

    public String getKey(String keyName) {
        return keys.get(keyName);
    }

    public String getKeyOrDefault(Key key) {
        return getKeyOrDefault(key.name());
    }

    public String getKeyOrDefault(String key) {
        String result = getKey(key);
        return result == null ? key : result;
    }

    public Set<Entry<String, String>> getKeyValues() {
        return keys.entrySet();
    }

    @Override
    public List<LegendEntry> getLegend(boolean showGridDifference) {
        return showGridDifference ? diffLegend : super.getLegend();
    }

    public int getMaxTimeFrames() {

        // grid size (X * Y)
        long gridArea = Math.max(1, getGridSize().getArea());

        // limit frames over parent and all children
        long overlays = 1 + getChildCount();

        // calc max frames
        return (int) MathUtils.clamp(getLord().getTotalMaxGridCells() / gridArea / overlays, 1, MAX_TIMEFRAMES);
    }

    public float getMaxValue(MapType mapType) {
        return maxValue[mapType.ordinal() * 2 + 1];
    }

    public int getMaxValueTimeframe(MapType mapType) {
        return (int) maxValue[mapType.ordinal() * 2];
    }

    public float getMinValue(MapType mapType) {
        return minValue[mapType.ordinal() * 2 + 1];
    }

    public int getMinValueTimeframe(MapType mapType) {
        return (int) minValue[mapType.ordinal() * 2];
    }

    public final GridOverlay<?, ?> getPrequel(P prequelType) {
        return getItem(MapLink.OVERLAYS, getPrequelID(prequelType));
    }

    public final int getPrequelDepth() {

        if (prequels.isEmpty()) {
            return 0;
        }
        return getPrequelDepth(new ArrayList<>());
    }

    private final int getPrequelDepth(Collection<Integer> visited) {

        if (prequels.isEmpty() || visited.contains(this.getID())) {
            return 0;
        }
        visited.add(this.getID());
        int childDepth = 0;
        for (PrequelLink p : prequels.values()) {
            Overlay o = getItem(MapLink.OVERLAYS, p.getOverlayID());
            if (o instanceof GridOverlay<?, ?> po) {
                childDepth = Math.max(childDepth, po.getPrequelDepth(visited));
            }
        }
        return 1 + childDepth; // myself + max child depth
    }

    public String getPrequelDescription(P prequelType) {
        return null; // override if needed
    }

    public final Integer getPrequelID(P prequelType) {
        PrequelLink link = getPrequelLink(prequelType);
        return link != null ? link.getOverlayID() : Item.NONE;
    }

    public final Collection<Integer> getPrequelIDs() {
        return getPrequelIDs(true, false);
    }

    public final Collection<Integer> getPrequelIDs(boolean includeIteration, boolean valid) {

        Set<Integer> prequelIDs = new HashSet<>();
        for (PrequelLink p : prequels.values()) {
            if (includeIteration || !(p.isPreviousIteration() && (!valid || hasSequal(p.getOverlayID())))) {
                prequelIDs.add(p.getOverlayID());
            }
        }
        return prequelIDs;
    }

    public final PrequelLink getPrequelLink(P prequelType) {

        for (Entry<String, PrequelLink> entry : prequels.entrySet()) {
            if (entry.getKey().equals(prequelType.name())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public final List<GridOverlay<?, ?>> getPrequels(boolean includeIteration) {
        return this.getItems(MapLink.OVERLAYS, getPrequelIDs(includeIteration, false).stream().toList());
    }

    /**
     * Only for prequels with a fixed time frame value Note: time frame is clamped to a valid value
     */
    public final int getPrequelTimeframe(P prequelType) {
        return getPrequelTimeframe(prequelType, false);
    }

    /**
     * Only for prequels with a fixed time frame value Note: time frame is clamped to a valid value or when allow all
     */
    public final int getPrequelTimeframe(P prequelType, boolean allowAll) {

        PrequelLink link = getPrequelLink(prequelType);
        if (link == null) {
            return 0;
        }
        // all timeframes in certain cases are allowed
        if (allowAll && link.isAllTimeframes()) {
            return link.getTimeframe();
        }
        // get specific clamped frame or fallback to last
        GridOverlay<?, ?> p = getItem(MapLink.OVERLAYS, link.getOverlayID());
        int lastFrame = p == null ? 0 : p.getLastTimeframe();
        return link.getTimeframe() != null ? MathUtils.clamp(link.getTimeframe(), 0, lastFrame) : lastFrame;
    }

    public final int getPrequelTimeframes(P type, Map<Integer, Integer> cache) {

        PrequelLink link = getPrequelLink(type);
        if (link == null) {
            return 1;
        }
        GridOverlay<?, ?> prequel = getItem(MapLink.OVERLAYS, link.getOverlayID());
        if (prequel == null) {
            return 1;
        }
        return link.isPreviousIteration() && prequel.calcPrequelTimeframes() ? 1 : prequel.getTimeframes(cache);
    }

    public final P getPrequelType(String prequelName) {

        for (P p : getPrequelTypes()) {
            if (p.name().equals(prequelName)) {
                return p;
            }
        }
        return null;
    }

    public abstract P[] getPrequelTypes();

    public GridData getRawData(MapType mapType, int timeframe) {

        // static overlays have only 1 frame stored internally
        timeframe = getResultType().isStatic() ? 0 : timeframe;

        if (getLord() != null && !getLord().isServerSide()) {
            throw new IllegalArgumentException("Only Server is allowed to access the grid data directly!");
        }
        List<GridData> dataList = mapType == MapType.MAQUETTE ? maquette : current;
        if (dataList.size() <= timeframe) {
            return new GridData.Zero();
        } else {
            return dataList.get(Math.max(0, timeframe));
        }
    }

    protected abstract Class<R> getResultClass();

    public R getResultForType(String string) {

        try {
            return EnumUtils.get(getResultClass(), string);
        } catch (Exception e) {
            return null;
        }
    }

    public R getResultType() {

        if (StringUtils.containsData(resultType)) {
            return EnumUtils.get(getResultClass(), resultType);
        } else {
            return getDefaultResult();
        }
    }

    @Override
    public String getStateMessage() {

        if (calcTimeMS == GPUJob.ERROR) {
            return "ERROR";
        }
        if (calcTimeMS == GPUJob.CANCELED) {
            return "CANCELED";
        }
        if (calcTimeMS == GPUJob.TIMEOUT) {
            return "TIMEOUT";
        }
        if (calcTimeMS == GPUJob.INSUFFICIENT_MEMORY) {
            return "MEMORY";
        }
        if (calcTimeMS == GPUJob.INVALID_CLUSTER) {
            return "INVALID";
        }
        if (getResultType() instanceof Enum e && ObjectUtils.getEnumAnnotation(e, Deprecated.class) != null) {
            return "DEPRECATED";
        }
        return super.getStateMessage();
    }

    public long getTotalByteCount() {

        long total = 0;
        for (GridData data : current) {
            total += data.getCount(true);
        }
        for (GridData data : maquette) {
            total += data.getCount(true);
        }
        return total * Float.BYTES;
    }

    public long getTotalCellCount() {
        return getTimeframes() * getGridSize().getArea();
    }

    public List<LegendEntry> getTransparentLegend(boolean showGridDifference, float transparency) {

        List<LegendEntry> legend = this.getLegend(showGridDifference);
        List<LegendEntry> transparent = new ArrayList<>(legend.size());

        for (int i = 0; i < legend.size(); i++) {
            LegendEntry old = legend.get(i);
            transparent.add(new LegendEntry(old.getEntryName(), old.getColor().multAlpha(transparency), old.getValue()));
        }
        return transparent;
    }

    public String getWarnings() {
        return warnings;
    }

    protected final boolean hasDeepPrequel(Overlay selected) {

        // myself
        if (this.getID().equals(selected.getID())) {
            return true;
        }

        // now also search in prequels of my prequels (when they are not children)
        GridOverlay<?, ?> me = this instanceof ResultChildOverlay<?, ?> c ? c.getParent() : this;

        for (GridOverlay<?, ?> p : me.getPrequels(false)) {
            if (!me.getID().equals(p.getID())) {
                if (p instanceof ResultChildOverlay<?, ?> c && !selected.getID().equals(p.getID())) {
                    return !c.getParentID().equals(me.getID()) && c.getParent().hasDeepPrequel(selected);
                } else {
                    return p.hasDeepPrequel(selected);
                }
            }
        }
        // and the result children of the selected
        if (selected instanceof ResultParentOverlay<?, ?> selectedParent) {
            for (ResultChildOverlay<?, ?> selectedChild : selectedParent.getResultChildren()) {
                for (GridOverlay<?, ?> p : me.getPrequels(false)) {
                    if (!me.getID().equals(p.getID()) && p.hasDeepPrequel(selectedChild)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public final boolean hasPrequel(Integer prequelID) {

        for (Entry<String, PrequelLink> entry : prequels.entrySet()) {
            if (entry.getValue().getOverlayID().equals(prequelID)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasPrequel(P prequelType) {
        return prequels.containsKey(prequelType.name());
    }

    public final boolean hasSequal(Integer overlayID) {
        return hasSequal(overlayID, new TreeSet<>());
    }

    private final boolean hasSequal(Integer overlayID, Set<Integer> visited) {

        for (Overlay overlay : this.<Overlay> getMap(MapLink.OVERLAYS)) {
            if (overlay instanceof GridOverlay<?, ?> other && !other.getID().equals(this.getID()) && other.hasPrequel(this.getID())) {
                if (other.getID().equals(overlayID)) {
                    return true;
                }
                if (other instanceof ResultParentOverlay<?, ?> p) {
                    for (GridOverlay<?, ?> c : p.getResultChildren()) {
                        if (c.getID().equals(overlayID)) {
                            return true;
                        }
                    }
                }
                if (visited.add(other.getID()) && other.hasSequal(overlayID, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasValueLegend() {
        return true;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public boolean isActivePrequel() {

        ItemMap<Overlay> overlays = this.getMap(MapLink.OVERLAYS);
        for (Overlay overlay : overlays) {
            if (overlay instanceof GridOverlay<?, ?> go && go.hasPrequel(this.getID()) && overlay.isActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPreviousIterationAllowed() {
        return false;
    }

    @Override
    public boolean isShowDifference() {
        return showDifference;
    }

    public String removeKey(String keyName) {
        return keys.remove(keyName);
    }

    public final boolean removePrequel(P prequelType) {
        return prequels.remove(prequelType.name()) != null;
    }

    public final boolean removePrequels(Integer prequelID) {

        boolean removed = false;
        for (Entry<String, PrequelLink> entry : new ArrayList<>(prequels.entrySet())) {
            if (entry.getValue().getOverlayID().equals(prequelID)) {
                prequels.remove(entry.getKey());
                removed = true;
            }
        }
        return removed;
    }

    public void setActive(boolean active) {

        if (this.active == active) {
            return; // do not update
        }
        if (active) {
            // reset when active
            this.inActiveCellSize = null;
            this.inActiveTimeframes = null;
        } else {
            // store cell and time frames, note: state is active
            this.inActiveCellSize = getCellSizeM();
            this.inActiveTimeframes = getTimeframes();
        }
        // finally update status
        this.active = active;
    }

    public void setCalcTimeMS(long calcTimeMS) {
        this.calcTimeMS = calcTimeMS;
    }

    public void setKey(Key keyEnum, String keyValue) {
        setKey(keyEnum.name(), keyValue);
    }

    public void setKey(String keyValue) {
        setKey(GridOverlay.DEFAULT_KEY, keyValue);
    }

    public void setKey(String keyName, String keyValue) {
        keys.put(keyName, keyValue);
    }

    public void setMaxValue(MapType mapType, int timeframe, float value) {
        maxValue[mapType.ordinal() * 2 + 0] = timeframe;
        maxValue[mapType.ordinal() * 2 + 1] = value;
    }

    public void setMinValue(MapType mapType, int timeframe, float value) {
        minValue[mapType.ordinal() * 2 + 0] = timeframe;
        minValue[mapType.ordinal() * 2 + 1] = value;
    }

    public final boolean setPrequel(P prequelType, Integer prequelID, Integer timeframe, boolean previousIteration) {

        PrequelLink previous = this.getPrequelLink(prequelType);
        if (previous == null || !prequelID.equals(previous.getOverlayID()) //
                || timeframe != null && !timeframe.equals(previous.getTimeframe()) //
                || previous.getTimeframe() != null && !previous.getTimeframe().equals(timeframe) //
                || previous.isPreviousIteration() != previousIteration) {

            // update with new value
            this.prequels.put(prequelType.name(), new PrequelLink(prequelID, timeframe, previousIteration));
            return true;
        }
        return false;
    }

    public void setRawData(MapType mapType, int timeframe, Size gridSize, GridData data) {

        // static overlays have only 1 frame stored internally
        timeframe = getResultType().isStatic() ? 0 : timeframe;

        List<GridData> dataList = mapType == MapType.MAQUETTE ? maquette : current;

        // multiple threads can read/write data from this object
        synchronized (dataList) {

            // remove remainder timeframes
            while (dataList.size() > getTimeframes()) {
                dataList.removeLast();
            }
            // override existing timeframe?
            if (dataList.size() > timeframe) {
                dataList.set(timeframe, data);
                return;
            }
            // fill with empty arrays when needed
            while (dataList.size() < timeframe) {
                dataList.add(new GridData.Zero(gridSize));
            }
            // set frame
            dataList.add(data);
        }
    }

    public void setResultType(R resultType) {
        this.resultType = resultType != null ? resultType.name() : StringUtils.EMPTY;
    }

    public void setShowDifference(boolean show) {
        this.showDifference = show;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public final void updatePrequels(Map<Integer, Integer> replacementMap) {

        for (Entry<String, PrequelLink> entry : new ArrayList<>(prequels.entrySet())) {
            PrequelLink old = entry.getValue();
            Integer replacementID = old != null ? replacementMap.get(old.getOverlayID()) : null;
            if (replacementID != null) {
                prequels.put(entry.getKey(), new PrequelLink(replacementID, old.getTimeframe(), old.isPreviousIteration()));
            }
        }
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        for (PrequelLink link : prequels.values()) {
            result += validFields(link);
        }

        if (minValue.length == 2) { // convert to 4 values
            minValue = new float[] { 0, minValue[0], 0, minValue[1] };
        }
        if (maxValue.length == 2) { // convert to 4 values
            maxValue = new float[] { 0, maxValue[0], 0, maxValue[1] };
        }

        // force default result type to be set
        if (!StringUtils.containsData(resultType)) {
            setResultType(getResultType());
        }

        if (startSession) {
            // (Frank) Temporary code to remove empty key-values
            List<String> removables = new ArrayList<>();
            for (Entry<String, String> entry : getKeyValues()) {
                if (!StringUtils.containsData(entry.getValue())) {
                    removables.add(entry.getKey());
                }
            }
            for (String key : removables) {
                removeKey(key);
            }
        }
        return result;
    }
}
