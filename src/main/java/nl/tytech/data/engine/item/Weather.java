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

import java.util.Arrays;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.WaterOverlay.WaterResult;
import nl.tytech.data.engine.other.ResultType;

/**
 * Weather: defines e.g. rain intensity.
 *
 * @author Maxim Knepfle
 */
public class Weather extends AttributeItem {

    public enum WeatherAttribute implements ReservedAttribute {

        EVAPORATION_M(Double[].class, new double[] { DEFAULT_SIMULATION_TIME, DEFAULT_EVAPORATION_MS * DEFAULT_SIMULATION_TIME }),

        RAIN_M(Double[].class, new double[] { HOUR_SECS, 0.030, DEFAULT_SIMULATION_TIME, 0d });

        private final Class<?> type;
        private final double[] defaultArray;

        private WeatherAttribute(Class<?> type, double defaultValue) {
            this(type, new double[] { defaultValue });
        }

        private WeatherAttribute(Class<?> type, double[] defaultArray) {
            this.type = type;
            this.defaultArray = defaultArray;
        }

        @Override
        public double[] defaultArray() {
            return this.defaultArray;
        }

        @Override
        public double defaultValue() {
            return this.defaultArray.length > 1 ? defaultArray[1] : defaultArray[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    public enum WeatherTypeEffect {

        NO_EFFECT(false),

        RAIN(true),

        SNOW(true),

        WIND(false),

        /**
         * Low water level
         */
        DROUGHT(false),

        /**
         * High water level, flooding areas are flooded.
         */
        FLOODING(true);

        private static final double[] DRY = new double[] { 0d, 0d, DEFAULT_SIMULATION_TIME, 0d };

        private boolean hasLighting;

        private WeatherTypeEffect(boolean hasLighting) {
            this.hasLighting = hasLighting;
        }

        public double[] getDefaultEvaporationMT() {
            return WeatherAttribute.EVAPORATION_M.defaultArray();
        }

        public double[] getDefaultRainMT() {
            return this == RAIN ? WeatherAttribute.RAIN_M.defaultArray() : DRY;
        }

        public boolean hasLighting() {
            return this.hasLighting;
        }
    }

    private static final long serialVersionUID = -578313169572826818L;

    public static final double HOUR_SECS = 60f * 60f;

    public static final double DAY_SECS = 24 * HOUR_SECS;

    // 3 hours
    public static final double DEFAULT_SIMULATION_TIME = 2d * HOUR_SECS;
    // Makkink value is 0.5 mm/day in winter and 3 mm/day in summer in De Bilt
    public static final double DEFAULT_EVAPORATION_MS = 1.5 / 1000d / DAY_SECS;

    public static final double DEFAULT_TRIGGER_SEC = 5 * Moment.MINUTE;

    /**
     * Copy array and
     * @param array Original array of key-values
     * @return positive only values in key-value array
     */
    public static final double[] validateWeatherArray(double simulationTime, double[] array) {

        if (array.length <= 1) {
            return new double[] { simulationTime, array.length == 0 ? 0.0 : Math.max(0, array[0]) };
        }
        double[] result = Arrays.copyOf(array, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = i % 2 == 1 ? Math.max(0, array[i]) : array[i];
        }
        return result;
    }

    public static final boolean validDirection(Overlay overlay) {

        if (overlay instanceof GridOverlay go) {
            return go.getResultType() == WaterResult.SURFACE_LAST_DIRECTION;
        }
        return false;
    }

    public static final boolean validFlood(Overlay overlay) {

        if (overlay instanceof ComboOverlay || overlay instanceof GeoTiffOverlay || overlay instanceof AvgOverlay) {
            return true;
        }
        if (overlay instanceof GridOverlay go) {
            ResultType resultType = go.getResultType();
            return resultType == WaterResult.SURFACE_LAST_VALUE || resultType == WaterResult.SURFACE_MAX_VALUE
                    || resultType == WaterResult.WATER_STRESS;
        }
        return false;
    }

    @XMLValue
    private WeatherTypeEffect effect = WeatherTypeEffect.RAIN;

    @XMLValue
    private double floodingM = 5;

    @XMLValue
    private double durationInSecs = 10;

    @XMLValue
    private double triggerSec = -1;

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private Integer floodOverlayID = Item.NONE;

    public Weather() {

    }

    public Weather(String name) {
        setName(name);
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return WeatherAttribute.values();
    }

    public double getDurationInSecs() {
        return durationInSecs;
    }

    public double[] getEvaporationValues(MapType mapType) {

        double[] array = getAttributeArray(mapType, WeatherAttribute.EVAPORATION_M);
        if (array.length >= 4) {
            return array;
        }
        double[] result = Arrays.copyOf(effect.getDefaultEvaporationMT(), effect.getDefaultEvaporationMT().length);
        // only rain attribute defined?
        if (array.length == 1) {
            result[1] = array[0];
        } else {
            // copy available array contents to result, assumes timekey-value sequence
            for (int i = 0; i < array.length; i++) {
                result[i] = array[i];
            }
        }
        return result;
    }

    public double getFloodingM() {
        return floodingM;
    }

    public Overlay getFloodOverlay() {
        return getItem(MapLink.OVERLAYS, floodOverlayID);
    }

    public Integer getFloodOverlayID() {
        return floodOverlayID;
    }

    public double[] getRainValues(MapType mapType) {
        double[] array = getAttributeArray(mapType, WeatherAttribute.RAIN_M);
        if (array.length >= 4) {
            return array;
        }
        double[] result = Arrays.copyOf(effect.getDefaultRainMT(), effect.getDefaultRainMT().length);
        // only rain attribute defined?
        if (array.length == 1) {
            result[1] = array[0];
        } else {
            // copy available array contents to result, assumes timekey-value sequence
            for (int i = 0; i < array.length; i++) {
                result[i] = array[i];
            }
        }
        return result;
    }

    public double getSimulationTimeSeconds() {
        return getSimulationTimeSeconds(getDefaultMap());
    }

    public double getSimulationTimeSeconds(MapType mapType) {

        double[] rainMT = getAttributeArray(mapType, WeatherAttribute.RAIN_M);
        if (rainMT.length >= 2) {
            for (int i = 2; i < rainMT.length; i += 2) {
                if (rainMT[i] < rainMT[i - 2]) {
                    return Math.max(0, rainMT[i - 2]);
                }
            }
            if (rainMT.length % 2 == 1) {
                return Math.max(0, rainMT[rainMT.length - 1]);
            } else {
                return Math.max(0, rainMT[rainMT.length - 2]);
            }
        } else {
            return DEFAULT_SIMULATION_TIME;
        }
    }

    public double getTriggerSec() {
        return triggerSec;
    }

    public double[] getValidatedEvaporationValues(MapType mapType) {
        return validateWeatherArray(getSimulationTimeSeconds(), getEvaporationValues(mapType));
    }

    public double[] getValidatedRainValues(MapType mapType) {
        return validateWeatherArray(getSimulationTimeSeconds(), getRainValues(mapType));
    }

    public WeatherTypeEffect getWeatherTypeEffect() {
        return effect;
    }

    public double getWindSpeed() {
        return Math.random();
    }

    public boolean isAutoTrigger() {
        return triggerSec > 0;
    }

    public void setDurationInSecs(double durationInSecs) {
        this.durationInSecs = durationInSecs;
    }

    public void setFloodingM(double floodingM) {
        this.floodingM = floodingM;
    }

    public void setFloodOverlayID(Integer overlayID) {
        this.floodOverlayID = overlayID;
    }

    public void setTriggerSec(double triggerSec) {
        this.triggerSec = triggerSec;
    }

    public void setWeatherTypeEffect(WeatherTypeEffect effect) {
        this.effect = effect;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
