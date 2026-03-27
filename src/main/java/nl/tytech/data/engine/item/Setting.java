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

import java.io.Serializable;
import cjava.FrameVar;
import nl.tytech.core.net.serializable.KeepAlive;
import nl.tytech.core.net.serializable.SettingType;
import nl.tytech.core.net.serializable.TLicense;
import nl.tytech.core.util.SettingsManager.TextureSize;
import nl.tytech.data.core.item.AbstractSetting;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.SimState;
import nl.tytech.data.engine.item.Building.ModelStyle;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.GridOverlay.RasterizationMethod;
import nl.tytech.data.engine.serializable.GridData;
import nl.tytech.data.engine.serializable.PanelEnum;
import nl.tytech.data.engine.serializable.WebBaseMap;
import nl.tytech.data.engine.serializable.WebModelStyle;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Setting: keeps a set of general purpose project settings.
 *
 * @author Maxim Knepfle
 */
public class Setting extends AbstractSetting<Setting.Type> {

    public static final class Box implements Serializable {

        private static final long serialVersionUID = 6425050489881715396L;

        public static final Box createBox(String bbox) {

            // has data?
            if (!StringUtils.containsData(bbox)) {
                return null;
            }

            // correct split?
            String[] split = bbox.split(",");
            if (split.length < 4) {
                return null;
            }

            try {
                Double minX = Double.valueOf(split[0]);
                Double minY = Double.valueOf(split[1]);
                Double maxX = Double.valueOf(split[2]);
                Double maxY = Double.valueOf(split[3]);
                if (Double.isNaN(minX) || Double.isNaN(minY) || Double.isNaN(maxX) || Double.isNaN(maxY)) {
                    return null;
                }
                return new Box(minX.intValue(), minY.intValue(), maxX.intValue(), maxY.intValue());

            } catch (NumberFormatException e) {
                // invalid numbers
                return null;
            }
        }

        public final int minX;
        public final int minY;
        public final int maxX;
        public final int maxY;

        public Box(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public final long getArea() {
            return getSize().getArea();
        }

        public final int getHeight() {
            return Math.max(0, maxY - minY);
        }

        public final Size getSize() {
            return new Size(getWidth(), getHeight());
        }

        public final int getWidth() {
            return Math.max(0, maxX - minX);
        }

        @Override
        public String toString() {
            return minX + "," + minY + "," + maxX + "," + maxY;
        }
    }

    public static final class Size implements Serializable {

        private static final long serialVersionUID = 1239077210126483403L;

        public static final Size NULL = new Size(-1, -1);

        public static final Size ZERO = new Size(0, 0);

        public static final Size SINGLE = new Size(1, 1);

        public static final Size floor(final double sizeX, final double sizeY) {
            return new Size((int) Math.floor(sizeX), (int) Math.floor(sizeY));
        }

        public static final Size round(final double sizeX, final double sizeY) {
            return new Size((int) Math.round(sizeX), (int) Math.round(sizeY));
        }

        public final int x;
        public final int y;

        public Size(final int sizeX, final int sizeY) {
            x = sizeX;
            y = sizeY;
        }

        public Size(final int[] array) {
            x = array[Item.X];
            y = array[Item.Y];
        }

        public Size(final String fromString) {
            String[] values = fromString.split("x");
            x = Integer.parseInt(values[0]);
            y = Integer.parseInt(values[1]);
        }

        public final Size clamp(Size other) {
            return new Size(MathUtils.clamp(x, 0, other.x), MathUtils.clamp(y, 0, other.y));
        }

        public final Size div(double div) {
            return div(div, false);
        }

        public final Size div(double div, boolean round) {
            if (round) {
                return new Size((int) Math.round(x / div), (int) Math.round(y / div));
            } else {
                return new Size((int) (x / div), (int) (y / div));
            }
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof Size other && this.x == other.x && this.y == other.y;
        }

        public final long getArea() {
            return (long) x * (long) y;
        }

        public final Box getBox() {
            return new Box(0, 0, x, y);
        }

        public final double getDiagonalM() {
            return Math.sqrt((long) x * (long) x + (long) y * (long) y);
        }

        public final boolean isDimension(float[][] matrix) {
            return MathUtils.isDimension(matrix, x, y);
        }

        public final boolean isDimension(GridData matrix) {
            return matrix.getWidth() == x && matrix.getHeight() == y;
        }

        public final int largestDimension() {
            return Math.max(x, y);
        }

        public final Size mult(double mult) {
            return new Size((int) Math.round(x * mult), (int) Math.round(y * mult));
        }

        public final int smallestDimension() {
            return Math.min(x, y);
        }

        public final int[] toArray() {
            return new int[] { x, y };
        }

        @Override
        public final String toString() {
            return x + "x" + y;
        }
    }

    // (Frank) This enumerator is used by an EnumOrderedItem. Please add new
    // enumerator-values at the end of this enumerator.
    public enum Type implements SettingType {

        AREA_GROUPS(String.class, ""),

        /**
         * Currently active scenario.
         */
        ACTIVE_SCENARIO(Integer.class, "0"),

        /**
         * Maximum amount of cells per project
         */
        MAX_TOTAL_GRIDCELLS(Long.class, "0"),

        /**
         * Minimum cell size in meters
         */
        MIN_CELL_M(Double.class, "0"),

        /**
         * License support level
         */
        SUPPORT(String.class, TLicense.Support.PREMIUM.name()),

        /**
         * Decimal accuracy used in attributes and function values
         */
        DECIMALS(Integer.class, "6"),

        /** Basic panels that are not triggered by special functionality. */
        BASE_PANELS(PanelEnum.class, "HOVER_PANEL NAVIGATION_PANEL TOPBAR_PANEL LEFT_MENU_PANEL FEEDBACK_PANEL"),

        /**
         * High detail project
         */
        DETAILED(Boolean.class, "true"),

        /** Default Geo Plugins in Wizard */
        DEFAULT_GEOPLUGINS(String.class, ""),

        /** Default price in EUR to lower one m3 ground */
        DEFAULT_GROUND_LOWER_PRICE_M3(Double.class, "50"),

        /** Default price in EUR to raise one m3 ground */
        DEFAULT_GROUND_RAISE_PRICE_M3(Double.class, "50"),

        /** Default price of one m2 ground in euro */
        DEFAULT_GROUNDPRICE_M2(Double.class, "400"),

        /**
         * Project name
         */
        PROJECT_NAME(String.class, "Project Name"),

        /** When a building is placed an interaction popup needs to be shown. */
        SHOW_BUILDING_AND_MEASURE_POPUP(Boolean.class, "false"),

        /**
         * When a waterway is placed an interaction popup (for the water authority) needs to be shown.
         */
        SHOW_WATER_POPUP(Boolean.class, "false"),

        /**
         * Name of the sat file
         */
        SATELLITE_FILE_NAME(String.class, "default"),

        /**
         * Grid cell size in meters, default is 2
         */
        GRID_CELL_SIZE_M(Double.class, "2"),

        /**
         * Map size in X and Y in meters, default 0 (no map)
         */
        MAP_SIZE_M(Integer[].class, "0 0"),

        /**
         * Super user message to all clients
         */
        SUPER_USER_MESSAGE(String.class, StringUtils.EMPTY),

        STATE(SimState.class, "NOTHING"),

        /**
         * Serverside handled editor setting for auto recalculation of indicators and overlays on data changes.
         */
        AUTO_CALCULATION(Boolean.class, "false"),

        /**
         * Howto rasterize polygon into the simulation raster
         */
        RASTERIZATION(RasterizationMethod.class, "COMBINED"),

        /**
         * Serverside handled editor setting for required indicators and overlays recalculation. Only used when not auto recalculating.
         */
        RECALCULATION_REQUIRED(Boolean.class, "false"),

        /**
         * Project Region.
         */
        REGION(Region.class, "NORTHWESTERN_EUROPE"),

        /**
         * Timestamp format for exporting/importing date/time values in e.g. csv files.
         */
        TIMESTAMP_FORMAT(String.class, "dd/MM HH:mm:ss"),

        WATER_TYPE(String.class, "CANAL"),

        SKY_TYPE(String.class, "DEFAULT"),

        TRAFFIC_VISUAL_MULTIPLIER(Double.class, "4.0"),

        WIND_DIRECTION(Integer.class, "135"), // default south-east wind

        WIND_SPEED_M_PER_S(Double.class, "5.0"), // default 5 m/s for west NL

        CURRENCY(TCurrency.class, TCurrency.EURO.name()),

        MEASUREMENT_SYSTEM_TYPE(UnitSystemType.class, UnitSystemType.SI.name()),

        RESERVED_LAND(String.class, "MULTIPOLYGON EMPTY"),

        SURROUNDING_MAP_EXTEND_M(Integer[].class, "0 0"),

        /*
         * Extend of the loaded map
         */
        WORLD_REFERENCE_POINT(Double[].class, "0 0"),

        WATER_HEIGHT(Double.class, "" + NO_WATER_HEIGHT_SET),

        MODEL_STYLE(ModelStyle.class, "TEXTURED"),

        SOLAR_PANEL_POSITION(Double[].class, "0.0 -0.5 -1.0"),

        SATELLITE_BRIGHTNESS(Double.class, "-1"),

        SATELLITE_COLOR(Integer[].class, ""),

        EXPORT_CRS(String.class, "EPSG:3857"),

        RECENT_CRSS(String.class, "EPSG:3857 EPSG:4326"),

        SHOW_ACTION_LOG(Boolean.class, "true"),

        WEB_FRONT_PANEL(Integer.class, "-1"),

        WIZARD_TIME_MINUTES(Integer.class, "-1"),

        WEB_MAP_CUSTOM_PANEL(Integer.class, "-1"),

        WEB_BASE_MAP(WebBaseMap.class, WebBaseMap.SATELLITE.name()),

        WEB_ALLOW_PARTICIPANT_EVENT(Boolean.class, "true"),

        WEB_ALLOW_LOGIC_EVENT(Boolean.class, "true"),

        WEB_DEFAULT_APP(String.class, "2d"), // backwards compatibility, after release 2024 default to 3d

        WEB_MODEL_STYLE(String.class, WebModelStyle.DEFAULT),

        WEB_MODEL_SHADOW(Boolean.class, "true"),

        WEB_MODEL_TEXTURE(TextureSize.class, TextureSize.MEDIUM.name()),

        WEB_MODEL_SPACING(Double.class, "2.0"),

        // TODO: implement...
        WEB_ALLOW_QUERIES(Boolean.class, "false"),

        SCHEDULED_UPDATE(Long.class, ""),

        SCHEDULED_UPDATE_MAIL(String.class, ""),

        SCHEDULED_UPDATE_SMS(String.class, ""),

        TIME_ZONE(int.class, ""),

        SUN_DATES(long[].class, ""),

        SECTOR_SIZE_M(int.class, "500"), // 500 for backwards compatibility (8th feb 2022)

        LIMIT_MAP(String.class, "MULTIPOLYGON EMPTY"),

        IMPORT_CRS(String.class, ""),

        IMPORT_CRS_FORCE_XY(Boolean.class, "true"),

        KEEP_ALIVE(KeepAlive.class, "NEVER"),

        ITERATIONS(Integer.class, "1"),

        ;

        private final String defaultValue;
        private final Class<?> valueType;

        private Type(Class<?> valueType, String defaultValue) {
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return getDefaultValue(null);
        }

        @Override
        public String getDefaultValue(Boolean detailed) {

            if (Boolean.FALSE.equals(detailed)) {
                if (this == GRID_CELL_SIZE_M) {
                    return Double.toString(GridOverlay.MIN_BASIC_CELL_M);
                } else if (this == MODEL_STYLE) {
                    return ModelStyle.PLAIN.name();
                }
            }
            return defaultValue;
        }

        @Override
        public Class<?> getValueType() {
            return valueType;
        }

        public final boolean isWeb() {

            switch (this) {
                case PROJECT_NAME:
                case WEB_BASE_MAP:
                case WEB_ALLOW_LOGIC_EVENT:
                case WEB_ALLOW_PARTICIPANT_EVENT:
                case WEB_ALLOW_QUERIES:
                case WEB_MODEL_SHADOW:
                case WEB_MODEL_SPACING:
                case WEB_MODEL_TEXTURE:
                case WEB_MODEL_STYLE:
                case SUN_DATES:
                case WIND_DIRECTION:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static final double NO_WATER_HEIGHT_SET = -20000;

    public static final String INTRO_IMAGE_LOCATION = "Gui/Images/Intro/";

    public static final String WEB_LOCATION = "Web/";

    public static final String WEB_IMAGE_LOCATION = WEB_LOCATION + "Images/";

    public static final String WEB_CSS_LOCATION = WEB_LOCATION + "Css/";

    public static final String WEB_LIB_LOCATION = WEB_LOCATION + "Lib/";

    public static final String SATELLITE_IMAGE_LOCATION = "Satellite/";

    public static final long BIG_MAP_SIZE_M2 = 10_000l * 10_000l; // from this size maps are considered big

    public static final long MIN_MAP_SIZE_M2 = 250l * 250l; // min viable visualization possible

    public static final long MAX_DEV_MAP_SIZE_M2 = 500_000l * 500_000l; // only used for private testing

    public static final long MAX_CELLS_MAP_SIZE_M2 = 30_000l * 30_000l; // from this size maps reach the max cells variable

    public static final long BASIC_MIN_CELL_M = 5; // smallest cell (m) in basic mode

    public static final long BASIC_MULTIPLIER = BASIC_MIN_CELL_M * BASIC_MIN_CELL_M; // multiply basic max map area with this value

    public static final double MAX_MAP_RATIO = 4.0;

    public static final int MIN_DECIMALS = 3;

    public static final int MAX_DECIMALS = 12;

    public static final int MAX_ITERATIONS = FrameVar.MAX_FRAMES; // identical to max time frames

    private static final long serialVersionUID = 3730370813978282986L;

    public static final long MAX_AUTO_CALC_TIME = 20_000;

    /**
     * Default buffer value for importing lines and point into the project
     */
    public static final double DEFAULT_PL_BUFFER = 0.5;

    public static final long getTotalMaxGridCells(int[] mapSizeM, long minProjectCells, long maxProjectCells, boolean detailed) {

        // interpolate from 0x0 to max cells map size (30x30km)
        long maxMap = MAX_CELLS_MAP_SIZE_M2 * (detailed ? 1l : BASIC_MULTIPLIER);
        long variable = Math.max(0, maxProjectCells - minProjectCells);
        long mapM2 = (long) mapSizeM[0] * (long) mapSizeM[1];
        double fraction = MathUtils.clamp(mapM2 / (double) maxMap, 0.0, 1.0);
        return minProjectCells + (long) (fraction * variable);
    }

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }

    public final boolean isDefault() {

        try {
            return getType().getDefaultValue().equals(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return false;
        }
    }

    @Override
    public String validated(boolean startSession) {

        /**
         * Convert from single dimension to X and Y
         */
        if (this.getType() == Type.MAP_SIZE_M && this.getIntArrayValue().length == 1) {
            int[] value = new int[] { this.getIntValue(), this.getIntValue() };
            this.setValue(value);
        }
        return super.validated(startSession);
    }

}
