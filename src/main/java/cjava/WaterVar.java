package cjava;

public class WaterVar {

    public enum BArea {
        CENTER_X, CENTER_Y, U, V, WIDTH,
    };

    public enum EArea {
        BOTTOM_AREA_M2, ORIGINAL_DEPTH_M,
    };

    public enum Feedback {
        STEPS, TIMEFRAMES, GPU_COUNT, TOTAL_RAIN_M
    };

    public enum WObject {

        // TYPE
        OBJECT_TYPE,

        // AREA
        AREA_ONE_ID, AREA_TWO_ID, AREA_THR_ID,

        AREA_ONE_HEIGHT, AREA_TWO_HEIGHT, AREA_THR_HEIGHT,

        // VARS
        OBJECT_THRESHOLD, OBJECT_WIDTH, OBJECT_HEIGHT, OBJECT_COEFFICIENT, OBJECT_N, OBJECT_SPEED
    };

    public static final double TRIGGER_SPEED = 3.0;
    public static final double TRIGGER_AVG = 2.0;
    public static final double TRIGGER_ACCURACY = 1.0;

    public static final byte TYPE_WATER_STRESS = 0;
    public static final byte TYPE_SURFACE_LAST_VALUE = 1;
    public static final byte TYPE_SURFACE_MAX_VALUE = 2;
    public static final byte TYPE_SURFACE_FLOW = 3;
    public static final byte TYPE_SURFACE_DURATION = 4;
    public static final byte TYPE_SEWER_LAST_VALUE = 5;
    public static final byte TYPE_SEWER_MAX_VALUE = 6;
    public static final byte TYPE_GROUND_LAST_VALUE = 7;
    public static final byte TYPE_GROUND_MAX_VALUE = 8;
    public static final byte TYPE_EVAPOTRANSPIRATION = 9;
    public static final byte TYPE_GROUND_WATERTABLE = 10;
    public static final byte TYPE_BASE_TYPES = 11;
    public static final byte TYPE_IMPACTED_BUILDINGS = 12;
    public static final byte TYPE_SURFACE_MAX_SPEED = 13;
    public static final byte TYPE_SURFACE_LAST_DIRECTION = 14;
    public static final byte TYPE_SURFACE_AVG_DIRECTION = 15;
    public static final byte TYPE_SURFACE_LAST_SPEED = 16;
    public static final byte TYPE_GPU_OVERVIEW = 17;
    public static final byte TYPE_GROUND_LAST_STORAGE = 18;
    public static final byte TYPE_GROUND_MAX_STORAGE = 19;
    public static final byte TYPE_TRACER_A = 20;
    public static final byte TYPE_TRACER_B = 21;
    public static final byte TYPE_TRACER_C = 22;
    public static final byte TYPE_TRACER_D = 23;
    public static final byte TYPE_SURFACE_ELEVATION = 24;
    public static final byte TYPE_DEBUG_WAVE_SPEED = 25;
    public static final byte TYPE_SURFACE_DIFFERENCE = 26;
    public static final byte TYPE_GROUND_BOTTOM_FLOW = 27;
    public static final byte TYPE_BUILDING_LAST_STORAGE = 28;
    public static final byte TYPE_SURFACE_LAST_DATUM = 29;
    public static final byte TYPE_DEBUG_AVG_AREAS = 30;
    public static final byte TYPE_GROUND_LAST_DIRECTION = 31;
    public static final byte TYPE_GROUND_FLOW = 32;
    public static final byte TYPE_LAST_RAIN = 33;
    public static final byte TYPE_RAIN = 34;
    public static final byte TYPE_UV_DIRECTION = 35;
    public static final byte TYPE_FLOOD_ARRIVAL_TIME = 36;
    public static final byte TYPE_FLOOD_RISE_RATE = 37;
    public static final byte TYPE_LAST_EVAPOTRANSPIRATION = 38;
    public static final byte TYPE_GROUND_LAST_UNSAT_STORAGE = 39;
    public static final byte TYPE_GROUND_LAST_UNSAT_FRACTION = 40;
    public static final byte TYPE_DEBUG_WATERWAYS = 41;
    public static final byte TYPE_SURFACE_LAST_U = 42;
    public static final byte TYPE_SURFACE_LAST_V = 43;
    public static final byte TYPE_SURFACE_LAST_FLOW = 44;
    public static final byte TYPE_SURFACE_EVAPORATION = 45;
    public static final byte TYPE_GROUND_TRANSPIRATION = 46;
    public static final byte TYPE_SURFACE_LAST_EVAPORATION = 47;
    public static final byte TYPE_GROUND_LAST_TRANSPIRATION = 48;

    public static final byte CELL_LEVEL_LAND = 0;
    public static final byte CELL_LAND = 1;
    public static final byte CELL_WATER = 2;
    public static final byte CELL_SHORELINE = 3;
    public static final byte CELL_SEWER_AREA = 4;
    public static final byte CELL_CULVERT = 5;
    public static final byte CELL_WEIR = 6;
    public static final byte CELL_SURFACE_INLET = 7;
    public static final byte CELL_UNDERGROUND_INLET = 8;
    public static final byte CELL_PUMP = 9;
    public static final byte CELL_DRAINAGE = 10;
    public static final byte CELL_DRAINAGE_OVERFLOW = 11;
    public static final byte CELL_SEWER_OVERFLOW = 12;
    public static final byte CELL_BREACH_AREA = 13;
    public static final byte CELL_ACTIVE_BREACH = 14;
    public static final byte CELL_BREACH_INPUT_AREA = 15;
    public static final byte CELL_INACTIVE_INPUT_AREA = 16;
    public static final byte CELL_BORDER = 17;
    public static final byte CELL_BREACH_LEVEL_AREA = 18;
    public static final byte CELL_HYBRID_AVG_OFFSET = 32; // must be larger then the rest

    public static final int SCOPE_MAP = 0;
    public static final int SCOPE_WATER_AREAS = 1;
    public static final int SCOPE_LIMIT_AREAS = 2;
    public static final int SCOPE_OVERRIDE_RAIN_AREAS = 3;

    public static final int OBJECT_PUMP = 0;
    public static final int OBJECT_INLET = 1;
    public static final int OBJECT_SEWER_OVERFLOW = 2;
    public static final int OBJECT_WEIR = 3;
    public static final int OBJECT_BREACH = 4;
    public static final int OBJECT_CULVERT = 5;
    public static final int OBJECT_DRAINAGE = 6;
    public static final int OBJECT_INACTIVE_AREA = 7;

    public static final byte FLOW_FREE = 0;
    public static final byte BLOCK_U = 1;
    public static final byte BLOCK_V = 2;
    public static final byte UNBLOCK_U = -1;
    public static final byte UNBLOCK_V = -2;

    // Extended reconstruction result
    public static final byte ER_NONE = 0;
    public static final byte ER_DISTURBANCE = 1;

    // Extended reconstruction directions
    public static final byte ER_NORTH = 1;
    public static final byte ER_EAST = 2;
    public static final byte ER_SOUTH = 4;
    public static final byte ER_WEST = 8;

    // Ground water modes of operation
    public static final byte GW_NONE = 0;
    public static final byte GW_COMPLETE = 1;
    public static final byte GW_INFILTRATION_ONLY = 2;

    // Soil Water Retention Curve mode
    public static final byte SWRC_NONE = 0;
    public static final byte SWRC_VAN_GENUCHTEN = 1;

    public static final byte SW_NONE = 0; // none
    public static final byte SW_SHALLOW = 1; // shallow water equations
    public static final byte SW_AVG = 2; // avg areas
    public static final byte SW_HYBRID = 3; // both shallow and avg

    public static final int HUV_LENGTH = 4;

    public static final float NO_VALUE = -10000.0f;

    public static final float TERMINAL_VELOCITY_MS = 10.0f; // 10m/sec at >=5cm

    /**
     * Threshold factor for selecting a Free or min(Free, Submerged) Weir formula
     */
    public static final double WEIR_THRESHOLD = 0.5;

    /**
     * When weir is submerged use a loss coeffcient between 0.8-1.0
     */
    public static final float WEIR_SUBMERGED_LOSS = 0.9f;

    public static final int OBJECT_MULT = 13; // WObject.size()

    public static final int BREACH_MULT = 5; // MAX(EArea.size(), BArea.size())

    public static final float PI = 3.141592654f;

    public static final int MARGIN = 3;

    public static final double MAX_CFL = 0.25;

    /**
     * Minimum amount of water (in meters) for flow to occur
     */
    public static final float FLOW_THRESHOLD = 0.0005f;

    /**
     * Earths Gravity constant
     */
    public static final float GRAVITY = 9.80665f;

    public static final int MIN_BYTE_VALUE = -128; // equals Byte.MIN_VALUE

    public static final double DEFAULT_ODT = 1.0;

    public static final double DEFAULT_VDT = 2.0;

    public static final double DEFAULT_GDT = 5.0;

    public static final double MAX_GDT = 120.0;

    /**
     * Calculate Ground water DT
     */
    public static double get1GDT(double dx, double mode) {
        double dt = DEFAULT_GDT * dx * mode;
        return dt < MAX_GDT ? dt : MAX_GDT;
    }

    /**
     * Calculate Vertical DT
     */
    public static double get2VDT(byte surfaceWater, double dx, double mode) {
        return surfaceWater == SW_SHALLOW || surfaceWater == SW_HYBRID ? DEFAULT_VDT * mode : get1GDT(dx, mode);
    }

    /**
     * Calculate Object DT
     */
    public static double get3ODT(byte surfaceWater, double dx, double mode) {
        return (surfaceWater == SW_AVG ? 0.5 : 1.0) * get2VDT(surfaceWater, dx, mode);
    }

    /**
     * Calculate Average direction DT based on ~1000 samples
     */
    public static double get4ADT(double tStart, double tEnd, double mode) {
        return mode == 0.0 ? 0.0 : (tEnd - tStart) / 1000.0f / mode;
    }
}
