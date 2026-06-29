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
import nl.tytech.core.item.annotations.Description;
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

        @Description("Group Areas based on these Attribute names in Tygron Client")
        AREA_GROUPS(String.class, ""),

        @Description("Currently active Scenario")
        ACTIVE_SCENARIO(Integer.class, "0"),

        @Description("Maximum amount of Overlay grid cells per Project")
        MAX_TOTAL_GRIDCELLS(Long.class, "0"),

        @Description("Minimum Overlay grid cell size in meters")
        MIN_CELL_M(Double.class, "0"),

        @Description("License support level")
        SUPPORT(String.class, TLicense.Support.PREMIUM.name()),

        @Description("Decimal accuracy used in attributes and function values")
        DECIMALS(Integer.class, "6"),

        @Description("Basic panels set (that are not triggered by special functionality)")
        BASE_PANELS(PanelEnum.class, "HOVER_PANEL NAVIGATION_PANEL TOPBAR_PANEL LEFT_MENU_PANEL FEEDBACK_PANEL"),

        @Description("High resolution project")
        DETAILED(Boolean.class, "true"),

        @Description("Default Geo Plugins in new Project Wizard. Stored as pairs of Maplink and Geo Plugin ID, separated by spaces.")
        DEFAULT_GEOPLUGINS(String.class, ""),

        @Description("Default price to lower one m3 ground")
        DEFAULT_GROUND_LOWER_PRICE_M3(Double.class, "50"),

        @Description("Default price to raise one m3 ground")
        DEFAULT_GROUND_RAISE_PRICE_M3(Double.class, "50"),

        @Description("Default price of one m2 ground")
        DEFAULT_GROUNDPRICE_M2(Double.class, "400"),

        @Description("Project name")
        PROJECT_NAME(String.class, "Project Name"),

        @Description("When a building is placed an interaction popup needs to be shown")
        SHOW_BUILDING_AND_MEASURE_POPUP(Boolean.class, "false"),

        @Description("When a waterway is placed an interaction popup (for the water authority) needs to be shown")
        SHOW_WATER_POPUP(Boolean.class, "false"),

        @Description("Name of the satellite background asset files")
        SATELLITE_FILE_NAME(String.class, "default"),

        @Description("Overlay grid cell size in meters")
        GRID_CELL_SIZE_M(Double.class, "2"),

        @Description("Project map size in X (longitude) and Y (latitude) in meters")
        MAP_SIZE_M(Integer[].class, "0 0"),

        @Description("Super user message to all clients")
        SUPER_USER_MESSAGE(String.class, StringUtils.EMPTY),

        @Description("Simulation Time State")
        STATE(SimState.class, "NOTHING"),

        @Description("When true indicators, panels and overlays are automatically recalculated on data changes")
        AUTO_CALCULATION(Boolean.class, "false"),

        @Description("Selected rasterization method to convert polygon into the Overlay grid raster")
        RASTERIZATION(RasterizationMethod.class, "COMBINED"),

        @Description("When true this indicates that a recalculation update is needed")
        RECALCULATION_REQUIRED(Boolean.class, "false"),

        @Description("Project Region: used to define the default functions")
        REGION(Region.class, "NORTHWESTERN_EUROPE"),

        @Description("Timestamp format for exporting/importing date/time values in e.g. csv files")
        TIMESTAMP_FORMAT(String.class, "dd/MM HH:mm:ss"),

        @Description("Water visualisation type in Tygron Client")
        WATER_TYPE(String.class, "CANAL"),

        @Description("Sky visualisation type in Tygron Client")
        SKY_TYPE(String.class, "DEFAULT"),

        @Description("Traffic visualisation multiplier in Tygron Client (not used in calculations)")
        TRAFFIC_VISUAL_MULTIPLIER(Double.class, "4.0"),

        @Description("Wind direction angle in degrees")
        WIND_DIRECTION(Integer.class, "135"), // default south-east wind

        @Description("Wind speed in meters per second")
        WIND_SPEED_M_PER_S(Double.class, "5.0"), // default 5 m/s for west NL

        @Description("Project currency")
        CURRENCY(TCurrency.class, TCurrency.EURO.name()),

        @Description("Unit system for Measurements")
        MEASUREMENT_SYSTEM_TYPE(UnitSystemType.class, UnitSystemType.SI.name()),

        @Description("MultiPolygon for reserved land used in sale transations")
        RESERVED_LAND(String.class, "MULTIPOLYGON EMPTY"),

        @Description("Surrounding map extend for visualisation of Sattelite and Elevation around Project map")
        SURROUNDING_MAP_EXTEND_M(Integer[].class, "0 0"),

        @Description("World reference point (upper left corner) used to convert local coordinates to EPSG 3857 world coordinates")
        WORLD_REFERENCE_POINT(Double[].class, "0 0"),

        @Description("Average water elevation reference point used for visualisation in Tygron Client")
        WATER_HEIGHT(Double.class, "" + NO_WATER_HEIGHT_SET),

        @Description("Model (buildings, bridges, tress, etc) style used for visualisation in Tygron Client")
        MODEL_STYLE(ModelStyle.class, "TEXTURED"),

        @Description("Solar panel positioning used for visualisation in Tygron Client")
        SOLAR_PANEL_POSITION(Double[].class, "0.0 -0.5 -1.0"),

        @Description("Satellite backgrounds can be dark, this values compensates for that in Tygron Client visualisation")
        SATELLITE_BRIGHTNESS(Double.class, "-1"),

        @Description("Instead of Satellite imagery this color (RGBA) can be used for visualisation in Tygron Client")
        SATELLITE_COLOR(Integer[].class, ""),

        @Description("Default export CRS for polygon data in Tygron Client")
        EXPORT_CRS(String.class, "EPSG:3857"),

        @Description("Recently used export CRS values in Tygron Client")
        RECENT_CRSS(String.class, "EPSG:3857 EPSG:4326"),

        @Description("Show Action log panel in Tygron Client")
        SHOW_ACTION_LOG(Boolean.class, "true"),

        @Description("Open Web Viewers with this Panel ID instead of default map")
        WEB_FRONT_PANEL(Integer.class, "-1"),

        @Description("Project map generation time in minutes")
        WIZARD_TIME_MINUTES(Integer.class, "-1"),

        @Description("Panel ID used to retrieve and replace custom content for the 2D and 3D Viewers")
        WEB_MAP_CUSTOM_PANEL(Integer.class, "-1"),

        @Description("Background type used Web Viewers")
        WEB_BASE_MAP(WebBaseMap.class, WebBaseMap.SATELLITE.name()),

        @Description("Allow participant endpoint execution for Web Viewers")
        WEB_ALLOW_PARTICIPANT_EVENT(Boolean.class, "true"),

        @Description("Allow logic endpoint execution for Web Viewers")
        WEB_ALLOW_LOGIC_EVENT(Boolean.class, "true"),

        @Description("Default Web Viewer type (2D or 3D)")
        WEB_DEFAULT_APP(String.class, "2d"), // backwards compatibility, after release 2024 default to 3d

        @Description("Web Viewer Model style (COLORED or WHITE)")
        WEB_MODEL_STYLE(String.class, WebModelStyle.DEFAULT),

        @Description("3D Web Viewer shadow visualisation enabled")
        WEB_MODEL_SHADOW(Boolean.class, "true"),

        @Description("3D Web Viewer texture quality/size (SMALL, MEDIUM, LARGE)")
        WEB_MODEL_TEXTURE(TextureSize.class, TextureSize.MEDIUM.name()),

        @Description("3D Web Viewer model spacing, distance between e.g. trees")
        WEB_MODEL_SPACING(Double.class, "2.0"),

        // TODO: implement...
        WEB_ALLOW_QUERIES(Boolean.class, "false"),

        @Description("Scheduled Update moment in millis")
        SCHEDULED_UPDATE(Long.class, ""),

        @Description("When the Scheduled Update has finished email this address")
        SCHEDULED_UPDATE_MAIL(String.class, ""),

        @Description("When the Scheduled Update has finished SMS this phone number")
        SCHEDULED_UPDATE_SMS(String.class, ""),

        @Description("Project timezone offset")
        TIME_ZONE(int.class, ""),

        @Description("Project sun dates used for Sun visualisation in Tygron Client")
        SUN_DATES(long[].class, ""),

        @Description("Height Sector tile size in meters")
        SECTOR_SIZE_M(int.class, "500"), // 500 for backwards compatibility (8th feb 2022)

        @Description("Limits Project map generation and later calculation to this MultiPolygon")
        LIMIT_MAP(String.class, "MULTIPOLYGON EMPTY"),

        @Description("Default import CRS for polygon data in Tygron Client")
        IMPORT_CRS(String.class, ""),

        @Description("Default import CRS (force XY) for polygon data in Tygron Client")
        IMPORT_CRS_FORCE_XY(Boolean.class, "true"),

        @Description("Keep Project alive (active) base on this setting")
        KEEP_ALIVE(KeepAlive.class, "NEVER"),

        @Description("Amount of Iterations during a recalculation update. Min Value: 1 and Max Value: " + MAX_ITERATIONS)
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
