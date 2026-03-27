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
import nl.tytech.data.engine.item.ShadowOverlay.ShadowPrequel;
import nl.tytech.data.engine.item.ShadowOverlay.ShadowResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.DateUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * Shadow Overlay
 *
 * @author Maxim Knepfle
 *
 */
public class ShadowOverlay extends ResultParentOverlay<ShadowResult, ShadowPrequel> {

    public enum ShadowAttribute implements ReservedAttribute {

        /**
         * Moments in Time in UTC millis
         */
        DATES(Long.class, 1375444800000d),

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

        /**
         * Max distance for shadows in meters
         */
        MAX_SHADOW_DISTANCE(Double.class, 500.0),

        /**
         * Max radius for leaves around the tree trunk in meters
         */
        MAX_FOLIAGE_RADIUS(Double.class, 100.0),

        ;

        private final double[] defaultValues;
        private final Class<?> type;

        private ShadowAttribute(Class<?> type, double... defaultValue) {
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

    public enum ShadowPrequel implements PrequelType {

        FOLIAGE_HEIGHT,

        BUILDING_ELEVATION,

        TERRAIN_ELEVATION;

    }

    public enum ShadowResult implements ResultType {

        SHADE(HeatVar.TYPE_SHADE),

        FOLIAGE(HeatVar.TYPE_FOLIAGE);

        private final byte index;

        private final String humanReadable;

        private ShadowResult(byte index) {
            this.index = index;
            this.humanReadable = StringUtils.capitalizeWithSpacedUnderScores(this);
        }

        @Override
        public byte getIndex() {
            return index;
        }

        @Override
        public boolean isStatic() {
            return this == FOLIAGE;
        }

        @Override
        public String toString() {
            return humanReadable;
        }
    }

    private static final long serialVersionUID = 8322868349883733531L;

    public static final double DPRA_DEFAULT_WIND = 90;

    public static final double DPRA_IMPROVED_WIND = 45;

    @XMLValue
    @NoDefaultText
    private String calcInfo = StringUtils.EMPTY;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        double[] array = ShadowAttribute.DATES.defaultValues;
        if (hasAttribute(ShadowAttribute.DATES)) {
            array = getAttributeArray(ShadowAttribute.DATES);
        }
        return MathUtils.clamp(array.length, 1, getMaxTimeFrames());
    }

    public String getCalcInfo() {
        return calcInfo;
    }

    @Override
    public int getDecimals() {

        return switch (this.getResultType()) {
            case FOLIAGE -> -1; // Input values, no roundoff
            case SHADE -> 0;
            default -> 2; // Output at 2
        };
    }

    @Override
    protected String getDefaultImagePrefix() {
        return "shadow";
    }

    @Override
    protected ShadowResult getDefaultResult() {
        return ShadowResult.SHADE;
    }

    @Override
    public ShadowPrequel[] getPrequelTypes() {
        return ShadowPrequel.values();
    }

    @Override
    protected Class<ShadowResult> getResultClass() {
        return ShadowResult.class;
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {

        double[] array = getAttributeArray(ShadowAttribute.DATES);
        TimeZone zone = this.<Setting, Setting.Type> getItem(MapLink.SETTINGS, Setting.Type.TIME_ZONE).getTimeZone();
        return timeframe < array.length ? DateUtils.format(zone, format, array[timeframe]) : "";
    }

    public void setCalcInfo(String calcInfo) {
        this.calcInfo = calcInfo;
    }
}
