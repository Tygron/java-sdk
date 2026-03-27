package cjava;

public class HeatVar {

    public static final byte TYPE_SHADE = 0;
    public static final byte TYPE_FOLIAGE = 1;
    public static final byte TYPE_SKY_VIEW = 2;
    public static final byte TYPE_UHI = 3;
    public static final byte TYPE_TEMP_ATMOSPHERE = 4;
    public static final byte TYPE_PET = 5;
    public static final byte TYPE_PET_RELATIVE = 6;
    public static final byte TYPE_VEGETATION = 7;
    public static final byte TYPE_AVG_SKY_VIEW = 8;
    public static final byte TYPE_AVG_VEGETATION = 9;
    public static final byte TYPE_BOWEN_RATIO = 10;
    public static final byte TYPE_WIND_SPEED = 11;

    public static final byte CELL_OTHER = 0;
    public static final byte CELL_BUILDING = 1;
    public static final byte CELL_WATER = 2;

    public static final float AIR_PRESSURE_PA = 101325.0f; // Pa;
    public static final float AIR_HEAT_CAPACITY_J = 1007.0f; // J
    public static final float C_IN_K = 273.15f; // K
    public static final float AIR_R_SPECIFIC = 287.058f;// in J / (kg * K)

    public static final float FOLIAGE_BOWEN = 0.4f;
    public static final float U60_FACTOR = 1.3084f;

    public static final float LONG_WIND_DISTANCE_M = 210.0f;
    public static final float SHORT_WIND_DISTANCE_M = 70.0f;
    public static final float SQUARE_WIND_DISTANCE_M = 87.5f;

    public static final float LONG_AVG_DISTANCE_M = 850.0f; // bit shorter then spec due to short distance
    public static final float SHORT_AVG_DISTANCE_M = 250.0f;
    public static final float SQUARE_AVG_DISTANCE_M = 350.0f; // (half of 700x700m window)

    public static final float MAX_FOLIAGE_HEIGHT_M = 100.0f;

    public static final float MIN_WIND_FACTOR = 0.0796f; // see PET calc in DPRA recept page 96
    public static final float MAX_WIND_FACTOR = 0.6350f;

    public static final float MIN_RESULT_WIND = 0.5f; // see PET calc in DPRA recept page 98
    public static final float MAX_RESULT_WIND = 100.0f;

    public static final float AVG_M = 25.0f;
}
