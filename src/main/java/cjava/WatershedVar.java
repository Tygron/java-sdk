package cjava;

public class WatershedVar {

    public static final int MARGIN = 2; // needed for: WatershedKernel -> ConnectPlateauDirectional

    public static final byte TYPE_WATERSHEDS = 0;
    public static final byte TYPE_DISCHARGE_AREAS = 1;
    public static final byte TYPE_DIRECTION = 2;
    public static final byte TYPE_BASE_TYPES = 3;

    public static final byte CELL_NONE = 0;
    public static final byte CELL_ENTRY = 1;
    public static final byte CELL_EXIT = 2;
    public static final byte CELL_TWOWAY = 3;
    public static final byte CELL_ROAD = 4;
    public static final byte CELL_BORDER = 5;

    public static final int PLATEAU_Y = -1;
    public static final int PLATEAU_X = 0;

    public static final int TOP_PRIO_Y = -3;
    public static final int SUB_PRIO_Y = -2;

    public static final float LABELED = 1.0f;
    public static final float UNLABELED = 0.0f;

}
