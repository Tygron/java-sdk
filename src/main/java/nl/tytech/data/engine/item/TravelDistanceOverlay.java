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
import java.util.List;
import java.util.Map;
import cjava.WatershedVar;
import nl.tytech.data.engine.item.TravelDistanceOverlay.TravelDistancePrequel;
import nl.tytech.data.engine.item.TravelDistanceOverlay.TravelDistanceResult;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;

/**
 * Overlay that calculates the Travel Distance
 *
 * @author Maxim Knepfle
 */
public class TravelDistanceOverlay extends ResultParentOverlay<TravelDistanceResult, TravelDistancePrequel> {

    public enum TravelDistanceAttribute implements ReservedAttribute {

        MAX_TRAVEL_DISTANCE(Double.class, 10_000.0),

        ROAD_DISTANCE(Double.class, 20.0),

        ROUTE_PEDESTRIANS(Boolean.class, 0.0),

        ROUTE_BICYCLES(Boolean.class, 0.0),

        ROUTE_CARS(Boolean.class, 1.0),

        ROUTE_TRUCKS(Boolean.class, 0.0),

        ROUTE_TRAMS(Boolean.class, 0.0),

        ROUTE_TRAINS(Boolean.class, 0.0),

        ROUTE_SHIPS(Boolean.class, 0.0),

        DESTINATION_THRESHOLD(Double.class, 1.0),

        BLOCKING_THRESHOLD(Double.class, 0.3),

        TRAVERSABLE_THRESHOLD(Double.class, 1.0),

        ;

        public static final TravelDistanceAttribute[] VALUES = values();
        private final double[] defaultValues;
        private final Class<?> type;

        private TravelDistanceAttribute(Class<?> type, double... defaultValue) {
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

        public boolean isTrafficType() {
            return switch (this) {
                case ROUTE_BICYCLES, ROUTE_CARS, ROUTE_PEDESTRIANS, ROUTE_SHIPS, ROUTE_TRAINS, ROUTE_TRAMS, ROUTE_TRUCKS -> true;
                default -> false;
            };
        }

    }

    public enum TravelDistanceKey implements Key {

        DESTINATION_AREA(1.0),

        BLOCKED(1.0);

        private double[] defaultArray;

        private TravelDistanceKey(double value) {
            this(new double[] { value });
        }

        private TravelDistanceKey(double[] value) {
            defaultArray = value;
        }

        public double[] getDefaultArray() {
            return defaultArray;
        }

        public double getDefaultValue() {
            return defaultArray[0];
        }

        @Override
        public UnitType getUnitType() {
            return UnitType.NONE;
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public boolean isOutput() {
            return false;
        }

    }

    public enum TravelDistancePrequel implements PrequelType {

        TRAVERSABLE, //
        BLOCKING, //
        DESTINATION //
        ;

        public static final TravelDistancePrequel[] VALUES = TravelDistancePrequel.values();

    }

    public enum TravelDistanceResult implements ResultType {

        DESTINATIONS(WatershedVar.TYPE_DISCHARGE_AREAS),

        BASE_TYPES(WatershedVar.TYPE_BASE_TYPES),

        ;

        private final byte index;

        private TravelDistanceResult(byte index) {
            this.index = index;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    private static final long serialVersionUID = -4100431020014120031L;

    public static final boolean isRoute(Building building, TrafficType[] trafficTypes) {

        if (building.isRoadSystem()) {
            for (TrafficType type : trafficTypes) {
                if (building.getValue(type.getNumValue()) > 0.0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final boolean isRoute(Function function, TrafficType[] trafficTypes) {

        if (function.isRoadSystem()) {
            for (TrafficType type : trafficTypes) {
                if (function.getValue(type.getNumValue()) > 0.0) {
                    return true;
                }
            }
        }
        return false;
    }

    public TravelDistanceOverlay() {

    }

    @Override
    protected final boolean calcPrequelTimeframes() {
        return isActive();
    }

    @Override
    public final boolean calcSelfPrequel() {
        return false; // due to possible Prequel INPUT getTimeframes() loop
    }

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        int timeframes = super.calcTimeframes(cache);
        if (calcPrequelTimeframes()) { // only when active == calcPrequelTimeframes()
            for (TravelDistancePrequel type : getPrequelTypes()) {
                timeframes = Math.max(timeframes, getPrequelTimeframes(type, cache));
            }
        }
        return timeframes;
    }

    @Override
    protected TravelDistanceResult getDefaultResult() {
        return TravelDistanceResult.DESTINATIONS;
    }

    @Override
    public TravelDistancePrequel[] getPrequelTypes() {
        return TravelDistancePrequel.VALUES;
    }

    @Override
    protected Class<TravelDistanceResult> getResultClass() {
        return TravelDistanceResult.class;
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {

        GridOverlay<?, ?> destination = getPrequel(TravelDistancePrequel.DESTINATION);
        if (destination != null && timeframe < destination.getTimeframes()) {
            return destination.getTimeframeText(timeframe);
        }
        GridOverlay<?, ?> traversable = getPrequel(TravelDistancePrequel.TRAVERSABLE);
        if (traversable != null && timeframe < traversable.getTimeframes()) {
            return traversable.getTimeframeText(timeframe);
        }
        GridOverlay<?, ?> blocker = getPrequel(TravelDistancePrequel.BLOCKING);
        if (blocker != null && timeframe < blocker.getTimeframes()) {
            return blocker.getTimeframeText(timeframe);
        }
        return super.getTimeframeText(timeframe, format);
    }

    public TrafficType[] getTrafficTypes() {

        List<TrafficType> list = new ArrayList<>();
        if (getOrDefault(TravelDistanceAttribute.ROUTE_PEDESTRIANS) > 0.0) {
            list.add(TrafficType.PEDESTRIAN);
        }
        if (getOrDefault(TravelDistanceAttribute.ROUTE_BICYCLES) > 0.0) {
            list.add(TrafficType.BICYCLE);
        }
        if (getOrDefault(TravelDistanceAttribute.ROUTE_CARS) > 0.0) {
            list.add(TrafficType.CAR);
            list.add(TrafficType.VAN);
        }
        if (getOrDefault(TravelDistanceAttribute.ROUTE_TRUCKS) > 0.0) {
            list.add(TrafficType.TRUCK);
            list.add(TrafficType.BUS);
        }
        if (getOrDefault(TravelDistanceAttribute.ROUTE_TRAMS) > 0.0) {
            list.add(TrafficType.TRAM);
        }
        if (getOrDefault(TravelDistanceAttribute.ROUTE_TRAINS) > 0.0) {
            list.add(TrafficType.TRAIN);
        }
        if (getOrDefault(TravelDistanceAttribute.ROUTE_SHIPS) > 0.0) {
            list.add(TrafficType.SHIP);
        }
        return list.toArray(new TrafficType[list.size()]);
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        // add defaults
        for (TravelDistanceAttribute type : TravelDistanceAttribute.values()) {
            if (!hasAttribute(type)) {
                setAttribute(type, type.defaultValue());
            }
        }
        return result;
    }
}
