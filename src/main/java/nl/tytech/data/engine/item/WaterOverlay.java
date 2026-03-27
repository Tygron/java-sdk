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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import cjava.WaterVar;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.engine.item.WaterOverlay.WaterPrequel;
import nl.tytech.data.engine.item.WaterOverlay.WaterResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.DateUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Base water overlay
 *
 * @author Maxim Knepfle
 */
public abstract class WaterOverlay extends ResultParentOverlay<WaterResult, WaterPrequel> {

    public enum BridgeElevation {

        /**
         * Lowest elevation point from Terrain below bridge
         */
        LOWEST,

        /**
         * Use standard elevation from Terrain
         */
        TERRAIN,

        /**
         * Use bridge deck as elevation
         */
        BRIDGE;

        public static BridgeElevation fromValue(double attribute) {

            for (BridgeElevation b : BridgeElevation.values()) {
                if (b.ordinal() == attribute) {
                    return b;
                }
            }
            return BridgeElevation.TERRAIN;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }

    }

    public enum MinMax {

        /**
         * Never min max, always use average heightmap value
         */
        NEVER,

        /**
         * Min Max for large cells
         */
        LARGE_CELL,

        /**
         * Always use min max for heightmap
         */
        ALWAYS;

        public static final MinMax fromValue(double attribute) {

            for (MinMax b : MinMax.values()) {
                if (b.ordinal() == attribute) {
                    return b;
                }
            }
            return MinMax.LARGE_CELL;
        }

        public static final boolean isActive(double attribute, float cellSizeM) {

            return switch (fromValue(attribute)) {
                case ALWAYS -> true;
                case NEVER -> false;
                case LARGE_CELL -> cellSizeM >= WaterOverlay.MIN_MAX_THRESHOLD_M;
                default -> cellSizeM >= WaterOverlay.MIN_MAX_THRESHOLD_M;
            };
        }

        @Override
        public String toString() {

            if (this == LARGE_CELL) {
                return "Cell >= " + MIN_MAX_THRESHOLD_M + "m";
            } else {
                return StringUtils.capitalizeWithSpacedUnderScores(this);
            }
        }
    }

    public enum Mode {

        SPEED(WaterVar.TRIGGER_SPEED),

        AVERAGE(WaterVar.TRIGGER_AVG),

        ACCURACY(WaterVar.TRIGGER_ACCURACY);

        private final double triggerMode;

        private Mode(double triggerMode) {
            this.triggerMode = triggerMode;
        }

        public double getTriggerMode() {
            return triggerMode;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeUnderScores(name());
        }
    }

    public enum Scope {

        MAP("Entire Map", WaterVar.SCOPE_MAP),

        WATER_AREAS("Limit to Water Level Areas", WaterVar.SCOPE_WATER_AREAS),

        LIMIT_AREAS("Limit to specified Areas", WaterVar.SCOPE_LIMIT_AREAS),

        OVERRIDE_RAIN_AREAS("Override specified Areas", WaterVar.SCOPE_OVERRIDE_RAIN_AREAS);

        public static Scope fromAreaValue(double value) {

            for (Scope l : getAreaValues()) {
                if (l.index == value) {
                    return l;
                }
            }
            return MAP;
        }

        public static Scope fromRainValue(double value) {

            for (Scope l : getRainValues()) {
                if (l.index == value) {
                    return l;
                }
            }
            return MAP;
        }

        public static final Scope[] getAreaValues() {
            return new Scope[] { MAP, WATER_AREAS, LIMIT_AREAS };
        }

        public static final Scope[] getRainValues() {
            return new Scope[] { MAP, OVERRIDE_RAIN_AREAS };
        }

        private final String humanName;

        private final int index;

        private Scope(String name, int index) {
            this.humanName = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String getLimitAreaAttribute(WaterOverlay overlay) {

            return switch (this) {
                case WATER_AREAS -> overlay.getKeyOrDefault(WaterKey.WATER_LEVEL);
                case LIMIT_AREAS -> overlay.getKeyOrDefault(WaterKey.LIMIT_AREA);
                default -> null;
            };
        }

        public double getValue() {
            return index;
        }

        @Override
        public String toString() {
            return humanName;
        }
    }

    public enum Shoreline {

        /**
         * No Shoreline
         */
        NONE,

        /**
         * Single cell, direct neighbor
         */
        ONE_CELL,

        /**
         * Two cells, diagonal neighbor too
         */
        TWO_CELLS;

        public static final Shoreline fromValue(double attribute) {

            for (Shoreline b : Shoreline.values()) {
                if (b.ordinal() == attribute) {
                    return b;
                }
            }
            return Shoreline.ONE_CELL;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum SurfaceMode {

        NONE(0, WaterVar.SW_NONE),

        SHALLOW(1, WaterVar.SW_SHALLOW),

        AVG_WATERWAY(2, WaterVar.SW_AVG),

        HYBRID(3, WaterVar.SW_HYBRID),

        AVG_TERRAIN(4, WaterVar.SW_AVG),

        ;

        public static SurfaceMode valueOf(double attributeValue) {

            for (SurfaceMode s : values()) {
                if (s.getAttributeValue() == attributeValue) {
                    return s;
                }
            }
            return SurfaceMode.SHALLOW; // default value
        }

        private final double attributeValue;

        private final byte var;

        private SurfaceMode(int index, byte var) {
            this.attributeValue = index;
            this.var = var;
        }

        public boolean doAvg() {
            return this == AVG_WATERWAY || this == AVG_TERRAIN || this == HYBRID;
        }

        public boolean doShallow() {
            return this == SHALLOW || this == HYBRID;
        }

        /**
         * Attribute Index value
         */
        public double getAttributeValue() {
            return attributeValue;
        }

        /**
         * GPU WaterVar
         */
        public byte getSurfaceVar() {
            return var;
        }
    }

    public enum SWRC {

        NONE("None", WaterVar.SWRC_NONE),

        VAN_GENUCHTEN("Van Genuchten-Mualem", WaterVar.SWRC_VAN_GENUCHTEN);

        public static final SWRC fromValue(double attribute) {

            for (SWRC b : SWRC.values()) {
                if (b.var == attribute) {
                    return b;
                }
            }
            return SWRC.NONE;
        }

        private final double var;

        private final String humanName;

        private SWRC(String humanName, byte var) {
            this.humanName = humanName;
            this.var = var;
        }

        public double getVar() {
            return var;
        }

        @Override
        public String toString() {
            return humanName;
        }
    }

    public enum WaterKey implements Key {

        // water areas
        WATER_LEVEL, WATER_RELATIVE(true, false, 0d, UnitType.BOOLEAN),

        // inlets
        INLET_Q(true, false, 0d, UnitType.FLOW_RATE), INLET_CAPACITY(true, false, 0d, UnitType.VOLUME), INLET_AREA(true, false, 0d,
                UnitType.BOOLEAN),

        LOWER_THRESHOLD(true, false, WaterVar.NO_VALUE), UPPER_THRESHOLD(true, false, WaterVar.NO_VALUE), UNDERGROUND(true, false, 0,
                UnitType.BOOLEAN),

        // inundation areas
        INUNDATION_LEVEL,

        // Aquifer areas
        AQUIFER_KD(0, UnitType.SURFACE), AQUIFER_DATUM,

        // sewers
        SEWER_STORAGE, SEWER_PUMP_SPEED(true, false, 0d, UnitType.FLOW_RATE),

        // weirs
        WEIR_HEIGHT, WEIR_WIDTH(true, false, 5d, UnitType.LENGTH), WEIR_COEFFICIENT(true, false, 1.1d, UnitType.NONE), WEIR_N(true, false,
                3d / 2d, UnitType.NONE), WEIR_ANGLE(true, false, 0d,
                        UnitType.ANGLE), WEIR_TARGET_LEVEL(true, false, WaterVar.NO_VALUE, UnitType.HEIGHT),

        // culverts
        CULVERT_DIAMETER(1d), CULVERT_N(0.014,
                UnitType.NONE), CULVERT_THRESHOLD(WaterVar.NO_VALUE), CULVERT_RECTANGULAR_HEIGHT(WaterVar.NO_VALUE),

        // pumps
        PUMP_Q(10d, UnitType.FLOW_RATE), PUMP_ANGLE(true, false, 0d, UnitType.ANGLE), PUMP_AREA(true, false, 0d, UnitType.BOOLEAN),

        // Drainage
        DRAINAGE_Q(10d, UnitType.FLOW_RATE), DRAINAGE_DATUM(true, false, WaterVar.NO_VALUE), DRAINAGE_OVERFLOW_THRESHOLD(true, false,
                WaterVar.NO_VALUE), DRAINAGE_ACTIVE(true, false, 0, UnitType.BOOLEAN),

        // Levee breaches
        BREACH_SPEED(true, false, WaterVar.NO_VALUE, UnitType.NONE), BREACH_HEIGHT(0d), BREACH_WIDTH(true, false, 2, UnitType.LENGTH),

        BREACH_ANGLE(true, false, WaterVar.NO_VALUE, UnitType.ANGLE), BREACH_COEFFICIENT(true, false, 1.37, UnitType.NONE), // dakvormig

        //
        BREACH_INPUT_AREA(true, false, -1, UnitType.NONE), BREACH_LEVEL_AREA(true, false, -1, UnitType.NONE),

        //
        EXTERNAL_SURFACE_LEVEL(true, false, 0d), EXTERNAL_WATER_LEVEL(true, false, 0d), EXTERNAL_AREA(true, false, 0, UnitType.SURFACE),

        // sewer overflows (default is very hi speed)
        SEWER_OVERFLOW(), SEWER_OVERFLOW_SPEED(true, false, 10d, UnitType.FLOW_RATE),

        // object flow
        OBJECT_FLOW_OUTPUT(true, true, 0d, UnitType.FLOW_RATE),

        // object source datum at A
        OBJECT_DATUM_OUTPUT_A(true, true, 0d),

        // object source datum at B
        OBJECT_DATUM_OUTPUT_B(true, true, 0d),

        // object width for breaches
        OBJECT_WIDTH_OUTPUT(true, true, 0d, UnitType.LENGTH),

        // object height for weirs
        OBJECT_HEIGHT_OUTPUT(true, true, 0d, UnitType.HEIGHT),

        // object water level area output: (2 or 3 IDs, then 2 or 3 area sizes, then 2 or 3 area elevation datums)
        OBJECT_WATER_AREA_OUTPUT(true, true, 0d, UnitType.NONE),

        // weir dam dimensions (width, height)
        WEIR_DAM_OUTPUT(true, true, 0d, UnitType.LENGTH),

        // sources
        TRACER_A(0.001d, UnitType.SURFACE), TRACER_B(0.001d, UnitType.SURFACE), TRACER_C(0.001d, UnitType.SURFACE), TRACER_D(0.001d,
                UnitType.SURFACE),

        // limitations
        LIMIT_AREA(0, UnitType.NONE), RAIN_M;

        private final boolean output;
        private final boolean optional;
        private final double[] defaultArray;
        private final UnitType unitType;

        private WaterKey() {
            this(0d, UnitType.HEIGHT);
        }

        private WaterKey(boolean optional, boolean output, double defaultValue) {
            this(optional, output, defaultValue, UnitType.HEIGHT);
        }

        private WaterKey(boolean optional, boolean output, double defaultValue, UnitType unitType) {
            this.optional = optional;
            this.output = output;
            this.defaultArray = new double[] { defaultValue };
            this.unitType = unitType;
        }

        private WaterKey(double defaultValue) {
            this(false, false, defaultValue);
        }

        private WaterKey(double defaultValue, UnitType unitType) {
            this(false, false, defaultValue, unitType);
        }

        public double[] getDefaultArray() {
            return defaultArray;
        }

        public double getDefaultValue() {
            return defaultArray[0];
        }

        public float getDefaultValueF() {
            return (float) defaultArray[0];
        }

        @Override
        public UnitType getUnitType() {
            return unitType;
        }

        public boolean isKeyFor(OverlayType type) {
            if (type == OverlayType.FLOODING) {
                return true;
            } else if (type == OverlayType.RAINFALL || type == OverlayType.GROUNDWATER) {
                return switch (this) {
                    case BREACH_ANGLE, BREACH_COEFFICIENT, BREACH_HEIGHT, BREACH_INPUT_AREA, BREACH_LEVEL_AREA, //
                            BREACH_SPEED, BREACH_WIDTH, EXTERNAL_AREA, EXTERNAL_SURFACE_LEVEL, EXTERNAL_WATER_LEVEL -> false;
                    default -> true;
                };
            }
            return false;
        }

        @Override
        public boolean isOptional() {
            return optional;
        }

        @Override
        public boolean isOutput() {
            return output;
        }
    }

    public enum WaterModelAttribute implements ReservedAttribute {

        ALLOWED_WATER_INCREASE_M(Double.class, 0.4f, 0, 0),

        GROUND_WATERTABLE_WITH_SURFACE(Boolean.class, 1, 1, 1),

        DESIGN_FLOOD_ELEVATION_M(Double.class, 1.0),

        GROUND_BOTTOM_DISTANCE_M(Double.class, 10.0),

        GROUND_BOTTOM_PRESSURE_M(Double.class, 0.0, 0.0, 0.0),

        GROUND_WATER(Integer.class, 2, 2, 1),

        GROUND_WATER_DEPTH_M(Double.class, 1, 1, 1),

        HYDRAULIC_CONDUCTIVITY_WITH_THICKNESS(Boolean.class, 0, 0, 0),

        SOIL_WATER_RETENTION_CURVE(Double.class, 0),

        UNSATURATED_FRACTION(Double.class, 0.0),

        IMPACT_FLOOD_THRESHOLD_M(Double.class, 0.1, 0.5, 0.1),

        IMPACT_RANGE_M(Double.class, 3, 3, 3),

        CULVERT_DEM_THRESHOLD_M(Double.class, 10.0),

        QCELL(Boolean.class, true, 1),

        INCREASED_RESOLUTION(Boolean.class, 0),

        SHORELINE(Integer.class, 1),

        OBJECT_ENTRY_CORRECTION(Integer.class, 2),

        SEWER_OVERFLOW_THRESHOLD(Double.class, 0.9),

        DURATION_MIN_LEVEL_M(Double.class, 0.1, 0.5, 0.1),

        RISE_RATE_DISTANCE_M(Double.class, 1.5),

        SURFACE_WATER_EVAPORATION_FACTOR(Double.class, 1.3),

        TIMEFRAMES(Integer.class, 10, 10, 10),

        TIMEFRAME_TIMES(Double.class, 0, 0, 0),

        LIMIT_AREA(Boolean.class, 0, 0, 0),

        LIMIT_RAIN(Boolean.class, 0, 0, 0),

        MAX_WATER_BOTTOM_M(Float.class, 10_000, 10_000, 10_000),

        START_DATE_MS(Long.class, 0.0),

        AVG_SHORE_WIDTH_M(Double.class, 3.0, 3.0, 3.0),

        AVG_TERRAIN_WIDTH_M(Double.class, 1000.0, 1000.0, 1000.0),

        AVG_TERRAIN_STORAGE_M(Double.class, 0.05, 0.05, 0.05),

        SURFACE_WATER(Integer.class, 1, 1, 1),

        PREQUEL_SURFACE_LEVEL_RELATIVE(Boolean.class, 0, 0, 0),

        MICRORELIEF_STORAGE_FRACTION(Integer.class, 1.0, 1.0, 1.0),

        MIN_MAX_ELEVATION(Boolean.class, 1, 1, 1),

        BRIDGE_ELEVATION(BridgeElevation.class, 0, 0, 0),

        MAX_INFILTRATION_M(Double.class, 1, 1, 1),

        MIN_DRAIN_SURFACE_DISTANCE_M(Double.class, 0.25),

        INFILTRATION_FACTOR_S(Double.class, 1, 1, 1),

        WEIR_DAM_MULTIPLIER(Double.class, 6),

        WEIR_DAM_SURPLUS_HEIGHT_M(Double.class, 0.25),

        BREACH_MEASUREMENT_DISTANCE_M(Double.class, 100, 100, 100),

        STABILIZER_ANGLE(Double.class, 10, 10, 10),

        WEIR_MOVE_INTERVAL_S(Double.class, 10),

        WEIR_MOVE_STEP_M(Double.class, 0.01),

        WEIR_MOVE_RANGE_M(Double.class, 0.5),

        PARTIAL_START_SEC(Double.class, 0.0),

        PARTIAL_END_SEC(Double.class, Float.MAX_VALUE),

        TRIM_MANNING(Double.class, 1.0),

        TRIM_EVAPOTRANSPIRATION(Double.class, 1.0),

        TRIM_ROOT_DEPTH(Double.class, 1.0),

        TRIM_GROUND_INFILTRATION(Double.class, 1.0),

        TRIM_GROUND_CONDUCTIVITY(Double.class, 1.0),

        TRIM_SHORELINE_CONDUCTIVITY(Double.class, 1.0),

        TRIM_GROUND_STORAGE_FRACTION(Double.class, 1.0);

        private final double[] defaultValue;
        private final Class<?> type;
        private final boolean hidden;

        private WaterModelAttribute(Class<?> type, boolean hidden, double... defaultValue) {
            this.type = type;
            this.defaultValue = defaultValue;
            this.hidden = hidden;
        }

        private WaterModelAttribute(Class<?> type, double... defaultValue) {
            this(type, false, defaultValue);
        }

        @Override
        public double[] defaultArray() {
            return new double[] { defaultValue() };
        }

        public double[] defaultArray(OverlayType type) {
            return new double[] { defaultValue(type) };
        }

        @Override
        public double defaultValue() {
            return defaultValue(OverlayType.RAINFALL);
        }

        public double defaultValue(OverlayType type) {

            return switch (type) {
                case RAINFALL -> defaultValue[0];
                case FLOODING -> defaultValue[defaultValue.length > 1 ? 1 : 0];
                case GROUNDWATER -> defaultValue[defaultValue.length > 2 ? 2 : 0];
                default -> 0.0;
            };
        }

        public final Double getLoopValue(double groundwater) {

            return switch (this) {
                case TIMEFRAMES -> 1.0;
                case PREQUEL_SURFACE_LEVEL_RELATIVE -> 1.0;
                case GROUND_WATERTABLE_WITH_SURFACE -> groundwater == WaterVar.GW_COMPLETE ? 0.0 : null;
                default -> null;
            };
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        public boolean isHidden() {
            return hidden;
        }

        public final boolean isTrim() {

            return switch (this) {
                case TRIM_MANNING -> true;
                case TRIM_EVAPOTRANSPIRATION -> true;
                case TRIM_GROUND_CONDUCTIVITY -> true;
                case TRIM_SHORELINE_CONDUCTIVITY -> true;
                case TRIM_GROUND_INFILTRATION -> true;
                case TRIM_GROUND_STORAGE_FRACTION -> true;
                case TRIM_ROOT_DEPTH -> true;
                default -> false;
            };
        }
    }

    public enum WaterPrequel implements PrequelType {

        TERRAIN_ELEVATION, //
        SURFACE_LEVEL, //
        SURFACE_U, //
        SURFACE_V, //
        SURFACE_INFILTRATION, //
        GROUND_WATER_DEPTH, //
        GROUND_WATER_DATUM, //
        GROUND_CONDUCTIVITY, //
        GROUND_INFILTRATION, //
        GROUND_STORAGE_FRACTION, //
        UNSATURATED_FRACTION, //
        BOTTOM_DISTANCE, //
        BOTTOM_RESISTANCE, //
        BOTTOM_PRESSURE, //
        MAX_INFILTRATION, //
        MANNING, //
        MICRORELIEF, //
        SEWER_LEVEL, //
        ;

        public static final WaterPrequel[] VALUES = WaterPrequel.values();

        public final WaterResult getLoopResult(double groundwater, boolean sewer) {

            boolean infil = groundwater == WaterVar.GW_INFILTRATION_ONLY || groundwater == WaterVar.GW_COMPLETE;
            boolean complete = groundwater == WaterVar.GW_COMPLETE;

            return switch (this) {
                case SURFACE_LEVEL -> WaterResult.SURFACE_LAST_VALUE;
                case SURFACE_U -> WaterResult.SURFACE_LAST_U;
                case SURFACE_V -> WaterResult.SURFACE_LAST_V;
                case UNSATURATED_FRACTION -> infil ? WaterResult.GROUND_LAST_UNSATURATED_FRACTION : null;
                case GROUND_WATER_DATUM -> complete ? WaterResult.GROUND_WATERTABLE : null;
                case SEWER_LEVEL -> sewer ? WaterResult.SEWER_LAST_VALUE : null;
                default -> null;
            };
        }
    }

    public enum WaterResult implements ResultType {

        /**
         * Surface Overlays
         */
        WATER_STRESS(WaterVar.TYPE_WATER_STRESS),

        SURFACE_LAST_DIRECTION(WaterVar.TYPE_SURFACE_LAST_DIRECTION),

        SURFACE_AVG_DIRECTION(WaterVar.TYPE_SURFACE_AVG_DIRECTION),

        SURFACE_DURATION(WaterVar.TYPE_SURFACE_DURATION),

        SURFACE_ELEVATION(WaterVar.TYPE_SURFACE_ELEVATION),

        SURFACE_FLOW(WaterVar.TYPE_SURFACE_FLOW),

        SURFACE_LAST_FLOW(WaterVar.TYPE_SURFACE_LAST_FLOW),

        SURFACE_LAST_SPEED(WaterVar.TYPE_SURFACE_LAST_SPEED),

        SURFACE_LAST_VALUE(WaterVar.TYPE_SURFACE_LAST_VALUE),

        SURFACE_LAST_DATUM(WaterVar.TYPE_SURFACE_LAST_DATUM),

        SURFACE_MAX_SPEED(WaterVar.TYPE_SURFACE_MAX_SPEED),

        SURFACE_MAX_VALUE(WaterVar.TYPE_SURFACE_MAX_VALUE),

        SURFACE_EVAPORATION(WaterVar.TYPE_SURFACE_EVAPORATION),

        SURFACE_LAST_EVAPORATION(WaterVar.TYPE_SURFACE_LAST_EVAPORATION),

        BUILDING_LAST_STORAGE(WaterVar.TYPE_BUILDING_LAST_STORAGE),

        FLOOD_ARRIVAL_TIME(WaterVar.TYPE_FLOOD_ARRIVAL_TIME),

        FLOOD_RISE_RATE(WaterVar.TYPE_FLOOD_RISE_RATE),

        LAST_RAIN(WaterVar.TYPE_LAST_RAIN),

        RAIN(WaterVar.TYPE_RAIN),

        /**
         * Ground Overlays
         */
        GROUND_LAST_STORAGE(WaterVar.TYPE_GROUND_LAST_STORAGE),

        GROUND_LAST_VALUE(WaterVar.TYPE_GROUND_LAST_VALUE),

        GROUND_LAST_DIRECTION(WaterVar.TYPE_GROUND_LAST_DIRECTION),

        GROUND_FLOW(WaterVar.TYPE_GROUND_FLOW),

        GROUND_MAX_STORAGE(WaterVar.TYPE_GROUND_MAX_STORAGE),

        GROUND_MAX_VALUE(WaterVar.TYPE_GROUND_MAX_VALUE),

        GROUND_WATERTABLE(WaterVar.TYPE_GROUND_WATERTABLE),

        GROUND_BOTTOM_FLOW(WaterVar.TYPE_GROUND_BOTTOM_FLOW),

        GROUND_LAST_UNSATURATED_STORAGE(WaterVar.TYPE_GROUND_LAST_UNSAT_STORAGE),

        GROUND_LAST_UNSATURATED_FRACTION(WaterVar.TYPE_GROUND_LAST_UNSAT_FRACTION),

        GROUND_TRANSPIRATION(WaterVar.TYPE_GROUND_TRANSPIRATION),

        GROUND_LAST_TRANSPIRATION(WaterVar.TYPE_GROUND_LAST_TRANSPIRATION),

        /**
         * Sewer
         */
        SEWER_LAST_VALUE(WaterVar.TYPE_SEWER_LAST_VALUE),

        SEWER_MAX_VALUE(WaterVar.TYPE_SEWER_MAX_VALUE),

        /**
         * Other
         */
        @Deprecated(since = "July 2025: Replaced by LAST_EVAPOTRANSPIRATION")
        LAST_EVAPORATED(WaterVar.TYPE_LAST_EVAPOTRANSPIRATION),

        LAST_EVAPOTRANSPIRATION(WaterVar.TYPE_LAST_EVAPOTRANSPIRATION),

        @Deprecated(since = "July 2025: Replaced by EVAPOTRANSPIRATION")
        EVAPORATED(WaterVar.TYPE_EVAPOTRANSPIRATION),

        EVAPOTRANSPIRATION(WaterVar.TYPE_EVAPOTRANSPIRATION),

        IMPACTED_BUILDINGS(WaterVar.TYPE_IMPACTED_BUILDINGS),

        /**
         * Tracers
         */
        TRACER_A(WaterVar.TYPE_TRACER_A),

        TRACER_B(WaterVar.TYPE_TRACER_B),

        TRACER_C(WaterVar.TYPE_TRACER_C),

        TRACER_D(WaterVar.TYPE_TRACER_D),

        /**
         * Debug Overlays
         */
        BASE_TYPES(WaterVar.TYPE_BASE_TYPES),

        SURFACE_DIFFERENCE(WaterVar.TYPE_SURFACE_DIFFERENCE),

        DEBUG_UV(WaterVar.TYPE_DEBUG_WAVE_SPEED),

        GPU_OVERVIEW(WaterVar.TYPE_GPU_OVERVIEW),

        DEBUG_AVG_AREAS(WaterVar.TYPE_DEBUG_AVG_AREAS),

        DEBUG_UV_DIRECTION(WaterVar.TYPE_UV_DIRECTION),

        DEBUG_WATERWAYS(WaterVar.TYPE_DEBUG_WATERWAYS),

        SURFACE_LAST_U(WaterVar.TYPE_SURFACE_LAST_U),

        SURFACE_LAST_V(WaterVar.TYPE_SURFACE_LAST_V),

        ;

        private static final WaterResult[] TRACERS = new WaterResult[] { TRACER_A, TRACER_B, TRACER_C, TRACER_D };

        private final byte index;

        private WaterResult(byte index) {
            this.index = index;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        @Override
        public boolean isStatic() {
            return this == SURFACE_ELEVATION;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum WaterSystem {
        OBJECTS, AREAS
    }

    public static final int[] EVAP_MONTHS = new int[] { 2, 3, 6, 8, 9 };

    public static final int[] EVAP_DAYOFMONTHS = new int[] { 15, 15, 15, 15, 1 };

    public static final int INCREASED_RESOLUTION_THRESHOLD_M = 1;

    public static final int MIN_MAX_THRESHOLD_M = 2;

    public static final int MAX_OBJECT_STEPS = 10_000;

    public static final float MAX_TRIM = 1_000;

    public static final float MIN_GEN_DAM_WIDTH = 20;

    public static final int MAX_RAIN_AREAS = 32_000; // lower then Short.MAX_VALUE

    private static final long serialVersionUID = -2572949484529750427L;

    @XMLValue
    @NoDefaultText
    private String calcInfo = StringUtils.EMPTY;

    @XMLValue
    private Mode mode = Mode.ACCURACY;

    @XMLValue
    private boolean showSystem = true;

    @XMLValue
    private boolean showAreas = false;

    @XMLValue
    @ItemIDField(MapLink.WEATHERS)
    private Integer weatherID = Item.NONE;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return MathUtils.clamp((int) getAttributeOrDefault(WaterModelAttribute.TIMEFRAMES), 1, getMaxTimeFrames());
    }

    public final double[] generateTimeframeArray() {
        return generateTimeframeArray(getTimeframes());
    }

    public final double[] generateTimeframeArray(int timeframes) {

        Weather weather = getWeather();
        double simTime = weather == null ? Weather.DEFAULT_SIMULATION_TIME : weather.getSimulationTimeSeconds();
        double end = MathUtils.clamp(getOrDefault(WaterModelAttribute.PARTIAL_END_SEC), 0, simTime);
        double start = MathUtils.clamp(getOrDefault(WaterModelAttribute.PARTIAL_START_SEC), 0, end);

        double distance = end - start;
        double[] times = new double[timeframes];
        for (int i = 0; i < timeframes; i++) {
            times[i] = start + distance / timeframes * (i + 1);
        }
        return times;
    }

    public double getAttributeOrDefault(WaterModelAttribute surfaceWater) {
        return hasAttribute(surfaceWater) ? getAttribute(surfaceWater) : surfaceWater.defaultValue(getType());
    }

    public String getCalcInfo() {
        return calcInfo;
    }

    @Override
    public int getDecimals() {

        return switch (this.getResultType()) {
            case SURFACE_LAST_SPEED, SURFACE_MAX_SPEED, SURFACE_LAST_DIRECTION, SURFACE_AVG_DIRECTION, GROUND_LAST_DIRECTION -> 2; // More
                                                                                                                                   // detail
                                                                                                                                   // is
                                                                                                                                   // useless
            default -> -1; // Default, no roundoffs
        };
    }

    @Override
    public double[] getDefaultArray(String attribute) {
        // default item specific attributes
        ReservedAttribute ra = getReservedAttribute(attribute);
        return ra instanceof WaterModelAttribute wa ? wa.defaultArray(getType()) : ra != null ? ra.defaultArray() : null;
    }

    @Override
    protected String getDefaultImagePrefix() {
        return "water";
    }

    @Override
    protected WaterResult getDefaultResult() {
        return WaterResult.SURFACE_LAST_VALUE;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public WaterPrequel[] getPrequelTypes() {
        return WaterPrequel.values();
    }

    @Override
    public Integer getRelationID(Relation relation) {
        return relation == Relation.WEATHER ? getWeatherID() : super.getRelationID(relation);
    }

    @Override
    protected Class<WaterResult> getResultClass() {
        return WaterResult.class;
    }

    public long getStartDateMS() {
        return DateUtils.toTimeMillis(getAttributeOrDefault(WaterModelAttribute.START_DATE_MS));
    }

    public String getStartDateText() {
        return getTimeframeText(-1);
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {

        long startTimeMS = getStartDateMS();
        long timeMS = getTimeframeTimeSec(timeframe) * Moment.SECOND;

        TimeZone zone = startTimeMS == 0 ? DateUtils.UTC
                : this.<Setting, Setting.Type> getItem(MapLink.SETTINGS, Setting.Type.TIME_ZONE).getTimeZone();

        return DateUtils.format(zone, format, startTimeMS + timeMS);
    }

    @Override
    public long getTimeframeTimeSec(int timeframe) {

        int timeframes = getTimeframes();
        Weather weather = getWeather();
        double simTime = weather == null ? Weather.DEFAULT_SIMULATION_TIME : weather.getSimulationTimeSeconds();
        double end = MathUtils.clamp(getOrDefault(WaterModelAttribute.PARTIAL_END_SEC), 0, simTime);
        double start = MathUtils.clamp(getOrDefault(WaterModelAttribute.PARTIAL_START_SEC), 0, end);

        double[] timeframeTimes = GridOverlay.getVerifiedTimeframeTimes(timeframes, start, end,
                getOrDefaultArray(WaterModelAttribute.TIMEFRAME_TIMES));
        return timeframe < 0 ? 0l : (long) timeframeTimes[Math.min(timeframe, timeframes - 1)];
    }

    public List<String> getTracerSources() {

        List<String> sources = new ArrayList<>();
        List<WaterResult> resultTypes = getResultTypes();

        for (WaterResult resultType : WaterResult.TRACERS) {
            if (resultTypes.contains(resultType)) {
                switch (resultType) {
                    case TRACER_A:
                        sources.add(getKeyOrDefault(WaterKey.TRACER_A));
                        break;
                    case TRACER_B:
                        sources.add(getKeyOrDefault(WaterKey.TRACER_B));
                        break;
                    case TRACER_C:
                        sources.add(getKeyOrDefault(WaterKey.TRACER_C));
                        break;
                    case TRACER_D:
                        sources.add(getKeyOrDefault(WaterKey.TRACER_D));
                        break;
                    default:
                        break;
                }
            }
        }
        return sources;
    }

    public Weather getWeather() {
        return this.getItem(MapLink.WEATHERS, getWeatherID());
    }

    public Integer getWeatherID() {
        return weatherID;
    }

    @Override
    public boolean isAttributeOverride(String attribute, double[] values) {

        for (WaterModelAttribute ra : WaterModelAttribute.values()) {
            if (ra.name().equals(attribute)) {
                return !Arrays.equals(ra.defaultArray(getType()), values);
            }
        }
        return super.hasAttribute(attribute); // custom user attributes
    }

    public boolean isQuadCell() {

        // increased heightmap resolution may also disable Quad cells
        if (getOrDefaultArray(WaterModelAttribute.INCREASED_RESOLUTION)[0] > 0.0 && getCellSizeM() <= INCREASED_RESOLUTION_THRESHOLD_M) {
            return false;
        }
        return getOrDefaultArray(WaterModelAttribute.QCELL)[0] > 0.0;
    }

    public boolean isShowSystem(WaterSystem type) {

        return switch (type) {
            case AREAS -> showAreas;
            case OBJECTS -> showSystem;
            default -> showSystem;
        };
    }

    public void setCalcInfo(String calcInfo) {
        this.calcInfo = calcInfo;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setShowSystem(WaterSystem type, boolean showSystem) {

        if (type == WaterSystem.AREAS) {
            this.showAreas = showSystem;
        } else if (type == WaterSystem.OBJECTS) {
            this.showSystem = showSystem;
        }
    }

    public void setWeatherID(Integer weatherID) {
        this.weatherID = weatherID;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        // add defaults
        for (WaterModelAttribute type : WaterModelAttribute.values()) {
            if (!type.isHidden() && !this.hasAttribute(type)) {
                if (type == WaterModelAttribute.OBJECT_ENTRY_CORRECTION && this.hasAttribute("OBJECT_CORRECTION")) {
                    double value = this.getAttribute("OBJECT_CORRECTION");
                    this.setAttribute(type, value > 0 ? type.defaultValue(getType()) : 0.0);
                    TLogger.warning("Convert Object to Object Entry correction: " + value + " -> " + this.getAttribute(type));
                    this.removeAttribute("OBJECT_CORRECTION", true);
                } else if (type == WaterModelAttribute.DURATION_MIN_LEVEL_M && this.hasAttribute("SHOW_DURATION_FLOOD_LEVEL_M")) {
                    double value = this.getAttribute("SHOW_DURATION_FLOOD_LEVEL_M");
                    this.setAttribute(type, value);
                    TLogger.warning("Convert SHOW_DURATION_FLOOD_LEVEL_M to DURATION_MIN_LEVEL_M: " + value);
                    this.removeAttribute("SHOW_DURATION_FLOOD_LEVEL_M", true);
                } else {
                    this.setAttribute(type, type.defaultValue(getType()));
                }
            }
        }
        return result;
    }
}
