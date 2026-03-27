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

import java.util.Map;
import java.util.TimeZone;
import cjava.HeatVar;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.HeatOverlay.HeatPrequel;
import nl.tytech.data.engine.item.HeatOverlay.HeatResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.DateUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * Heat Overlay
 *
 * @author Frank Baars
 */
public class HeatOverlay extends ResultParentOverlay<HeatResult, HeatPrequel> {

    public enum HeatDpraAttribute implements ReservedAttribute {

        /**
         * Moments in Time in UTC millis
         */
        DATES(Long.class, 1375444800000d),

        /**
         * Avg daily radionation in W m2
         */
        DAILY_AVG_RADIATION(Double.class, 340),

        /**
         * Max Temp at weather station in degrees
         */
        DAILY_TEMPERATURE_MAX(Double.class, 34),

        /**
         * Min Temp at weather station in degrees
         */
        DAILY_TEMPERATURE_MIN(Double.class, 19),

        /**
         * Exclude buildings in avg sky view and PET
         */
        EXCLUDE_BUILDINGS(Double.class, 1.0),

        /**
         * Exclude water in avg sky view and PET
         */
        EXCLUDE_WATER(Double.class, 1.0),

        /**
         * Exclude foliage height in sky view above this value, not PET
         */
        EXCLUDE_FOLIAGE_HEIGHT_M(Double.class, 1.2),

        FOLIAGE_AREAS(Double.class, 0.0),

        /**
         * Hourly Radiation in W m2
         */
        HOURLY_RADIATION(Double.class, 700),

        /**
         * Hourly Temp in Degrees
         */
        HOURLY_TEMPERATURE(Double.class, 33),

        /**
         * Hourly Humidty 0-100%
         */
        HOURLY_HUMIDITY(Double.class, 60),

        /**
         * Altitude is the angle up from the horizon. Zero degrees altitude means exactly on your local horizon, and 90 degrees is "straight
         * up". Hence, "directly underfoot" is -90 degrees altitude.
         */
        SUN_ALTITUDE(Double.class, 49.71),

        /**
         * Azimuth is the angle along the horizon, with zero degrees corresponding to North, and increasing in a clockwise fashion. Thus, 90
         * degrees is East, 180 degrees is South, and 270 degrees is West.
         */
        SUN_AZIMUTH(Double.class, 140.03),

        SUN_DAILY_MOTION(Double.class, 0.11),

        /**
         * Wind direction in degrees 0-360
         */
        WIND_DIRECTION(Integer.class, 0),

        /**
         * Wind speed a 10 meter
         */
        WIND_SPEED(Double.class, 3.0), // default: 2013-08-02

        /**
         * Avg daily wind speed a 10 meter
         */
        DAILY_AVG_WIND_SPEED(Double.class, 3.0),

        /**
         * Max distance for shadows in meters
         */
        MAX_SHADOW_DISTANCE(Double.class, 500.0),

        /**
         * Max distance for tree trunk leaves in meters
         */
        MAX_TRUNK_DISTANCE(Double.class, 100.0),

        /**
         * Decline in wind angle after objects, 90 is less windy, 0 is more windy
         */
        WIND_DECLINE_ANGLE(Double.class, DPRA_IMPROVED_WIND),

        ;

        private final double[] defaultValues;
        private final Class<?> type;

        private HeatDpraAttribute(Class<?> type, double... defaultValue) {
            this.type = type;
            this.defaultValues = defaultValue;
        }

        @Override
        public double[] defaultArray() {
            return defaultValues;
        }

        @Override
        public double defaultValue() {
            return defaultValues[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    public enum HeatKey implements Key {

        FOLIAGE_HEIGHT(new double[] { 0.0 }, 0f, HeatVar.MAX_FOLIAGE_HEIGHT_M),

        ;

        private final double[] defaultArray;
        private final float min, max;

        private HeatKey(double[] defaultArray, float min, float max) {
            this.defaultArray = defaultArray;
            this.min = min;
            this.max = max;
        }

        public float clampValue(float value) {
            return MathUtils.clamp(value, min, max);
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

        public float getMax() {
            return max;
        }

        public float getMin() {
            return min;
        }

        @Override
        public UnitType getUnitType() {
            return UnitType.NONE;
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public boolean isOutput() {
            return false;
        }
    }

    public enum HeatModel {
        UNESCO, DPRA
    }

    public enum HeatPrequel implements PrequelType {

        FOLIAGE_HEIGHT, BUILDING_ELEVATION, TERRAIN_ELEVATION;

        public static final HeatPrequel[] VALUES = HeatPrequel.values();

    }

    public enum HeatResult implements ResultType {

        BOWEN_RATIO(HeatVar.TYPE_BOWEN_RATIO),

        FOLIAGE(HeatVar.TYPE_FOLIAGE),

        PET(HeatVar.TYPE_PET, "Physiological Eqv Temp"),

        PET_RELATIVE(HeatVar.TYPE_PET_RELATIVE, "Physiological Eqv Temp (relative)"),

        SHADE(HeatVar.TYPE_SHADE),

        SKY_VIEW(HeatVar.TYPE_SKY_VIEW),

        SKY_VIEW_AVG(HeatVar.TYPE_AVG_SKY_VIEW, "Sky View (average)"),

        TEMPERATURE_ATMOSPHERE(HeatVar.TYPE_TEMP_ATMOSPHERE),

        UHI(HeatVar.TYPE_UHI, "Urban Heat Island"),

        VEGETATION(HeatVar.TYPE_VEGETATION),

        VEGETATION_AVG(HeatVar.TYPE_AVG_VEGETATION, "Vegetation (average)"),

        WIND_SPEED(HeatVar.TYPE_WIND_SPEED);

        private final byte index;

        private final String humanReadable;

        private HeatResult(byte index) {
            this.index = index;
            this.humanReadable = StringUtils.capitalizeWithSpacedUnderScores(this);
        }

        private HeatResult(byte index, String humanReadable) {
            this.index = index;
            this.humanReadable = humanReadable;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        @Override
        public boolean isStatic() {
            return this == BOWEN_RATIO || this == FOLIAGE;
        }

        @Override
        public String toString() {
            return humanReadable;
        }
    }

    public static final String HEAT_DATE_TIME_FORMAT = StringUtils.DATE_FORMAT + ": " + StringUtils.TIME_FORMAT;

    private static final long serialVersionUID = 8322868349883734531L;

    public static final double DPRA_DEFAULT_WIND = 90;

    public static final double DPRA_IMPROVED_WIND = 45;

    public static final int DPRA_DESIGN_CELL_SIZE_M = 1;

    @XMLValue
    @NoDefaultText
    private String calcInfo = StringUtils.EMPTY;

    @XMLValue
    private HeatModel model = HeatModel.DPRA;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        if (model == HeatModel.UNESCO) {
            return 1; // single result
        }
        double[] array = HeatDpraAttribute.DATES.defaultValues;
        if (hasAttribute(HeatDpraAttribute.DATES)) {
            array = getAttributeArray(HeatDpraAttribute.DATES);
        }
        return MathUtils.clamp(array.length, 1, getMaxTimeFrames());
    }

    public String getCalcInfo() {
        return calcInfo;
    }

    @Override
    public int getDecimals() {

        return switch (this.getResultType()) {
            case FOLIAGE, BOWEN_RATIO -> -1; // Input values, no roundoff
            case SHADE -> 0;
            default -> 2; // Output at 2
        };
    }

    @Override
    protected String getDefaultImagePrefix() {
        return "heat";
    }

    @Override
    protected HeatResult getDefaultResult() {
        return HeatResult.PET;
    }

    public HeatModel getModel() {
        return model;
    }

    @Override
    public HeatPrequel[] getPrequelTypes() {
        return HeatPrequel.values();
    }

    @Override
    protected Class<HeatResult> getResultClass() {
        return HeatResult.class;
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {
        double[] array = getAttributeArray(HeatDpraAttribute.DATES);
        TimeZone zone = this.<Setting, Setting.Type> getItem(MapLink.SETTINGS, Setting.Type.TIME_ZONE).getTimeZone();
        return timeframe < array.length ? DateUtils.format(zone, format, array[timeframe]) : "";
    }

    @Override
    public String getWarnings() {

        // allow 10% margin around design size
        if (model == HeatModel.DPRA && (getCellSizeM() < 0.9 * DPRA_DESIGN_CELL_SIZE_M || getCellSizeM() > 1.1 * DPRA_DESIGN_CELL_SIZE_M)) {
            return "WARNING: DPRA is designed for " + DPRA_DESIGN_CELL_SIZE_M + " m cell size.";
        }
        return super.getWarnings();
    }

    public void setCalcInfo(String calcInfo) {
        this.calcInfo = calcInfo;
    }

    public void setModel(HeatModel model) {
        this.model = model;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (hasAttribute("SUN_ANGLES") && (!hasAttribute(HeatDpraAttribute.SUN_ALTITUDE) || !hasAttribute(HeatDpraAttribute.SUN_AZIMUTH))) {
            double[] sunAngles = getAttributeArray("SUN_ANGLES");
            double[] sunAltitude = new double[sunAngles.length / 2];
            double[] sunAzimuth = new double[sunAngles.length / 2];
            for (int i = 0; i < sunAngles.length / 2; i++) {
                sunAltitude[i] = sunAngles[2 * i];
                sunAzimuth[i] = sunAngles[2 * i + 1];
            }
            if (!hasAttribute(HeatDpraAttribute.SUN_ALTITUDE)) {
                setAttributeArray(HeatDpraAttribute.SUN_ALTITUDE, sunAltitude);
            }
            if (!hasAttribute(HeatDpraAttribute.SUN_AZIMUTH)) {
                setAttributeArray(HeatDpraAttribute.SUN_AZIMUTH, sunAzimuth);
            }
            this.removeAttribute("SUN_ANGLES");
        }
        return result;
    }
}
