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
package nl.tytech.data.engine.serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.data.engine.item.AttributeItem.ReservedAttribute;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.FunctionValueGroup;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * Values specific to functions
 *
 * @author Maxim Knepfle
 *
 */
public enum FunctionValue implements Value, ReservedAttribute {

    /**
     * Effect on the QOL Environment.
     */
    LIVABILITY_EFFECT(FunctionValueGroup.ENVIRONMENT, ClientTerms.ENVIRONMENT_EFFECT, -10, 10),

    /**
     * Priority
     */
    CRITICAL_INFRASTRUCTURE(FunctionValueGroup.ENVIRONMENT, ClientTerms.CRITICAL_INFRASTRUCTURE, 0, 10, false),

    /**
     * Number cars (light vehicles) per hour
     */
    NUM_CARS(FunctionValueGroup.CAR_TRAFFIC, "Cars / hour", ClientTerms.NUM_CARS, 0, Double.MAX_VALUE, false),

    /**
     * Number Vans (heavier vehicles) per hour
     */
    NUM_VANS(FunctionValueGroup.CAR_TRAFFIC, "Vans / hour", ClientTerms.NUM_VANS, 0, Double.MAX_VALUE, false),

    /**
     * Number Trucks (heaviest vehicles) per hour
     */
    NUM_TRUCKS(FunctionValueGroup.CAR_TRAFFIC, "Trucks / hour", ClientTerms.NUM_TRUCKS, 0, Double.MAX_VALUE, false),

    /**
     * Number Buses (heaviest vehicles) per hour
     */
    NUM_BUSES(FunctionValueGroup.CAR_TRAFFIC, "Buses / hour", ClientTerms.NUM_BUSES, 0, Double.MAX_VALUE, false),

    /**
     * Number Bicycles per hour
     */
    NUM_BICYCLES(FunctionValueGroup.OTHER_TRAFFIC, "Bicycles / hour", ClientTerms.NUM_BICYCLES, 0, Double.MAX_VALUE, false),

    /**
     * Number Pedestrians per hour
     */
    NUM_PEDESTRIANS(FunctionValueGroup.OTHER_TRAFFIC, "Pedestrians / hour", ClientTerms.NUM_PEDESTRIANS, 0, Double.MAX_VALUE, false),

    /**
     * Number Trams (light rail vehicles) per hour
     */
    NUM_TRAMS(FunctionValueGroup.OTHER_TRAFFIC, "Trams / hour", ClientTerms.NUM_TRAMS, 0, Double.MAX_VALUE, false),

    /**
     * Number Trains (heavy rail) per hour
     */
    NUM_TRAINS(FunctionValueGroup.OTHER_TRAFFIC, "Trains / hour", ClientTerms.NUM_TRAINS, 0, Double.MAX_VALUE, false),

    /**
     * Number Ships (all types) per hour
     */
    NUM_SHIPS(FunctionValueGroup.OTHER_TRAFFIC, "Ships / hour", ClientTerms.NUM_SHIPS, 0, Double.MAX_VALUE, false),

    /**
     * Number Airplanes (all types) per hour
     */
    NUM_AIRPLANES(FunctionValueGroup.OTHER_TRAFFIC, "Airplanes / hour", ClientTerms.NUM_AIRPLANES, 0, Double.MAX_VALUE, false),

    /**
     * Jam Factor Cars (light vehicles) 0-1
     */
    JAM_FACTOR_CARS(FunctionValueGroup.CAR_TRAFFIC, "Car Jam Factor (0-1)", ClientTerms.JAM_FACTOR_CARS, 0, 1, false),

    /**
     * Jam Factor Vans (heavier vehicles) 0-1
     */
    JAM_FACTOR_VANS(FunctionValueGroup.CAR_TRAFFIC, "Van Jam Factor (0-1)", ClientTerms.JAM_FACTOR_VANS, 0, 1, false),

    /**
     * Jam Factor Trucks (heaviest vehicles) 0-1
     */
    JAM_FACTOR_TRUCKS(FunctionValueGroup.CAR_TRAFFIC, "Truck Jam Factor (0-1)", ClientTerms.JAM_FACTOR_TRUCKS, 0, 1, false),

    /**
     * Jam Factor Buses (heaviest vehicles) 0-1
     */
    JAM_FACTOR_BUSES(FunctionValueGroup.CAR_TRAFFIC, "Bus Jam Factor (0-1)", ClientTerms.JAM_FACTOR_BUSES, 0, 1, false),

    /**
     * Traffic speed in kmph
     */
    TRAFFIC_SPEED(FunctionValueGroup.CAR_TRAFFIC, "Speedlimit (kmph)", ClientTerms.TRAFFIC_SPEED, 0, Double.MAX_VALUE, false),

    /**
     * Traffic Lanes
     */
    TRAFFIC_LANES(FunctionValueGroup.CAR_TRAFFIC, "Lanes", ClientTerms.TRAFFIC_LANES, -Double.MAX_VALUE, Double.MAX_VALUE, false),

    /**
     * Road sigma: Constante per weg type: verschil in dB(A) bij de referentiesnelheid V0
     */
    TRAFFIC_NOISE_SIGMA(FunctionValueGroup.CAR_TRAFFIC, "Noise Sigma", ClientTerms.TRAFFIC_NOISE_SIGMA, -Double.MAX_VALUE, Double.MAX_VALUE,
            false),

    /**
     * Road tau: Constante per weg type: snelheidsindex in dB(A) per decade snelheidstoename
     */
    TRAFFIC_NOISE_TAU(FunctionValueGroup.CAR_TRAFFIC, "Noise Tau", ClientTerms.TRAFFIC_NOISE_TAU, -Double.MAX_VALUE, Double.MAX_VALUE,
            false),

    /**
     * Height M (for e.g. bridges and custom geometries)
     */
    HEIGHT_OFFSET_M(FunctionValueGroup.CONSTRUCTION, "Height Offset", ClientTerms.HEIGHT_OFFSET, -Double.MAX_VALUE, Double.MAX_VALUE, false,
            UnitType.LENGTH),

    /**
     * Effect on the heat in degrees.
     */
    HEAT_EFFECT(FunctionValueGroup.ENVIRONMENT, ClientTerms.TILE_HEAT_EFFECT_UNIT, -10, 10, UnitType.TEMPERATURE_RELATIVE),

    /**
     * Amount of meters around this function that is part of safe zone.
     */
    SAFETY_DISTANCE_M(FunctionValueGroup.ENVIRONMENT, "Safety Distance", ClientTerms.SAFETY_DISTANCE, 0, Double.MAX_VALUE, false,
            UnitType.LENGTH),

    /**
     * Amount of meters around this function that is part of disturbance zone.
     */
    DISTURBANCE_DISTANCE_M(FunctionValueGroup.ENVIRONMENT, "Disturbance Distance", ClientTerms.DISTURBANCE_DISTANCE, 0, Double.MAX_VALUE,
            false, UnitType.LENGTH),

    /**
     * Amount of meters around this function that is part of a self defined zone.
     */
    DISTANCE_ZONE_M(FunctionValueGroup.ENVIRONMENT, "Distance Zone", ClientTerms.DISTANCE_ZONE, 0, Double.MAX_VALUE, false,
            UnitType.LENGTH),

    /**
     * Water storage in M3 on the roof per M2 kavel.
     */
    WATER_STORAGE_M2(FunctionValueGroup.WATER, "Water Storage", ClientTerms.FUNCTION_WATER_STORAGE, 0, Double.MAX_VALUE, false,
            UnitType.VOLUME, UnitType.SURFACE),

    WATER_MANNING(FunctionValueGroup.WATER, "Manning Value", ClientTerms.FUNCTION_WATER_MANNING, 0.01, 0.255, false),

    WATER_MICRORELIEF_M(FunctionValueGroup.WATER, "Microrelief Value", ClientTerms.FUNCTION_WATER_MICRORELIEF, 0.0, 0.255, false),

    WATER_TRANSPIRATION_FACTOR(FunctionValueGroup.WATER, "Plant Transpiration Factor", ClientTerms.FUNCTION_WATER_TRANSPIRATION, 0,
            Double.MAX_VALUE, false),

    SEWERED(FunctionValueGroup.WATER, "Connected to Sewer Area", ClientTerms.SEWER_AREA, 0, 1, false, UnitType.BOOLEAN),

    GROUND_INFILTRATION_MD(FunctionValueGroup.WATER, "Surface Infiltration per Day", ClientTerms.FUNCTION_GROUND_INFILTRATION, 0,
            Double.MAX_VALUE, false, UnitType.LENGTH),

    ROOT_DEPTH_M(FunctionValueGroup.WATER, "Depth of plant roots", ClientTerms.FUNCTION_ROOT_DEPTH, 0, Double.MAX_VALUE, false,
            UnitType.LENGTH),

    VEGETATION_FRACTION(FunctionValueGroup.ENVIRONMENT, "Vegetation Fraction (0-1)", ClientTerms.GREEN, 0, 1, false),

    BASEMENT_HEIGHT_M(FunctionValueGroup.CONSTRUCTION, ClientTerms.DEFAULT_BASEMENT_HEIGHT, 0, Double.MAX_VALUE),

    FLOOR_HEIGHT_M(FunctionValueGroup.CONSTRUCTION, ClientTerms.DEFAULT_FLOOR_HEIGHT, 0, Double.MAX_VALUE),

    /**
     * Minimal amount of floors
     */
    MIN_FLOORS(FunctionValueGroup.CONSTRUCTION, ClientTerms.MIN_FLOORS, Function.MIN_ALLOWED_FLOORS, Function.MAX_ALLOWED_FLOORS),

    /**
     * Default amount of floors
     */
    DEFAULT_FLOORS(FunctionValueGroup.CONSTRUCTION, ClientTerms.DEFAULT_FLOORS, Function.MIN_ALLOWED_FLOORS, Function.MAX_ALLOWED_FLOORS),

    /**
     * Align elevantion: 0 flat, 1 floating, 2 surface
     */
    ALIGN_ELEVATION(FunctionValueGroup.CONSTRUCTION, ClientTerms.ALIGN_ELEVATION, Function.ALIGN_FLAT, Function.ALIGN_SURFACE),

    /**
     * Solar panels on the roof?
     */
    SOLAR_PANELS(FunctionValueGroup.CONSTRUCTION, ClientTerms.SOLAR_PANELS, 0, 1, UnitType.BOOLEAN),

    /**
     * Solid walls? Concrete is solid, bushes not
     */
    SOLID(FunctionValueGroup.CONSTRUCTION, ClientTerms.SOLID, 0, 1, UnitType.BOOLEAN),

    /**
     * Maximum amount of floors
     */
    MAX_FLOORS(FunctionValueGroup.CONSTRUCTION, ClientTerms.MAX_FLOORS, Function.MIN_ALLOWED_FLOORS, Function.MAX_ALLOWED_FLOORS),

    /**
     * Drainage influence;
     */
    DRAINAGE(FunctionValueGroup.WATER, "Drainage (Subsidence)", ClientTerms.DRAINAGE, -10, 10, false, UnitType.LENGTH),

    /**
     * When true function requires a zoning permit to be build
     */
    ZONING_PERMIT_REQUIRED(FunctionValueGroup.CONSTRUCTION, ClientTerms.ZONING_PERMIT_REQUIRED, 0, 1, UnitType.BOOLEAN),

    /**
     * How high is the slanting roof, 0 = flat roof 1m gives a small slanting roof.
     */
    SLANTING_ROOF_HEIGHT(FunctionValueGroup.CONSTRUCTION, ClientTerms.SLANTING_ROOF_HEIGHT, 0, 10, UnitType.LENGTH),

    /**
     * When true function allows pipe items to be constructed under it.
     */
    PIPES_PERMITTED(FunctionValueGroup.NETWORK, ClientTerms.PIPES_PERMITTED, 0, 1, UnitType.BOOLEAN),

    /**
     * Color of roof
     */
    ROOF_COLOR(FunctionValueGroup.VISUALISATION, ClientTerms.ROOFCOLOR, Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * Color of Basement wall
     */
    BASEMENT_COLOR(FunctionValueGroup.VISUALISATION, ClientTerms.BASEMENTCOLOR, Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * Color of Ground wall
     */
    GROUND_COLOR(FunctionValueGroup.VISUALISATION, ClientTerms.GROUNDCOLOR, Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * Transparency of terrain below 1 == No Terrain Visible
     */
    TERRAIN_MIX(FunctionValueGroup.VISUALISATION, ClientTerms.TERRAIN_MIX, 0, 1),

    /**
     * Color of Extra wall
     */
    EXTRA_COLOR(FunctionValueGroup.VISUALISATION, ClientTerms.EXTRACOLOR, Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * Color of Top wall
     */
    TOP_COLOR(FunctionValueGroup.VISUALISATION, ClientTerms.TOPCOLOR, Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * When true function is monumental and will require additional permission
     */
    MONUMENTAL(FunctionValueGroup.CONSTRUCTION, ClientTerms.MONUMENTAL, 0, 1, UnitType.BOOLEAN),

    FOLIAGE_CROWN_FACTOR(FunctionValueGroup.ENVIRONMENT, ClientTerms.FOLIAGE_CROWN_FACTOR, 0, 10),

    BOWEN_RATIO(FunctionValueGroup.ENVIRONMENT, null, ClientTerms.BOWEN_RATIO, 0.001, 10, new double[] { 3.0 }, false),

    ;

    public static final Map<String, FunctionValue> VALUE_MAP = new HashMap<>();
    public static final FunctionValue[] ACTIVE_VALUES;
    static {
        List<FunctionValue> types = new ArrayList<>();
        for (FunctionValue type : FunctionValue.values()) {
            Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
            if (depAnno == null) {
                types.add(type);
                VALUE_MAP.put(type.name(), type);
            }
        }
        types.sort(ObjectUtils.ALPHANUMERICAL_ORDER);
        ACTIVE_VALUES = types.toArray(new FunctionValue[types.size()]);
    }

    public static double[] clampValues(ValueItem function, FunctionValue functionValue, double[] values) {
        double min = functionValue.getMinValue();
        double max = functionValue.getMaxValue();
        if (functionValue == FunctionValue.DEFAULT_FLOORS) {

            max = function.getValue(FunctionValue.MAX_FLOORS);
            min = function.getValue(FunctionValue.MIN_FLOORS);
            values = MathUtils.round(values);
        } else if (functionValue == FunctionValue.MIN_FLOORS) {

            min = Function.MIN_ALLOWED_FLOORS;
            max = function.getValue(FunctionValue.DEFAULT_FLOORS);
            values = MathUtils.round(values);
        } else if (functionValue == FunctionValue.MAX_FLOORS) {

            min = function.getValue(FunctionValue.DEFAULT_FLOORS);
            max = Function.MAX_ALLOWED_FLOORS;
            values = MathUtils.round(values);
        }

        return MathUtils.clamp(values, min, max);
    }

    private final double minValue;
    private final double maxValue;
    private final double[] defaultValues;
    private final ClientTerms term;
    private final UnitType[] unitTypes;
    private final String editorName;
    private final boolean monetary;
    private final FunctionValueGroup group;

    private FunctionValue(FunctionValueGroup group, ClientTerms term, double minValue, double maxValue, boolean monetary,
            UnitType... unitTypes) {
        this(group, null, term, minValue, maxValue, monetary, unitTypes);
    }

    private FunctionValue(FunctionValueGroup group, ClientTerms term, double minValue, double maxValue, UnitType... unitTypes) {
        this(group, term, minValue, maxValue, false, unitTypes);
    }

    private FunctionValue(FunctionValueGroup group, String editorName, ClientTerms term, double minValue, double maxValue, boolean monetary,
            UnitType... unitTypes) {
        this(group, editorName, term, minValue, maxValue, new double[] { minValue > 0.0 ? minValue : 0.0 }, monetary, unitTypes);
    }

    private FunctionValue(FunctionValueGroup group, String editorName, ClientTerms term, double minValue, double maxValue,
            double[] defaultValues, boolean monetary, UnitType... unitTypes) {

        this.term = term;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.defaultValues = defaultValues;
        this.unitTypes = unitTypes.length == 0 ? new UnitType[] { UnitType.NONE } : unitTypes;
        this.monetary = monetary;
        this.editorName = StringUtils.containsData(editorName) ? editorName : StringUtils.capitalizeWithSpacedUnderScores(this);
        this.group = group;
    }

    public double clampValue(double value) {
        return MathUtils.clamp(value, minValue, maxValue);
    }

    public float clampValue(float value) {
        return MathUtils.clamp(value, (float) minValue, (float) maxValue);
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
    public ClientTerms getClientTerm() {
        return term;
    }

    @Override
    public String getEditorName() {
        return editorName;
    }

    @Override
    public FunctionValueGroup getGroup() {
        return group;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double[] getSIUnitValue(double[] value, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toSIValues(value, getUnitTypes());
    }

    @Override
    public Class<?> getType() {
        return Double.class;
    }

    @Override
    public String getUnit(TCurrency currency, UnitSystemType unitSystem) {

        if (!isMonetary()) {
            return unitSystem.getImpl().getUnitAbbreviation(getUnitTypes());
        }
        return unitSystem.getImpl().getUnitAbbreviation(currency, getUnitTypes());
    }

    @Override
    public UnitType[] getUnitTypes() {
        return unitTypes;
    }

    @Override
    public double getUnitValue(double value, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValue(value, getUnitTypes());
    }

    @Override
    public String getUnitValueFormatted(double[] value, int decimals, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValueWithFormatting(value, decimals, getUnitTypes());
    }

    @Override
    public String getUnitValueFormatted(double[] siValue, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValueWithFormatting(siValue, getUnitTypes());
    }

    public final boolean isModelUpdate() {
        return group == FunctionValueGroup.CONSTRUCTION || group == FunctionValueGroup.VISUALISATION
                || group == FunctionValueGroup.CAR_TRAFFIC || group == FunctionValueGroup.OTHER_TRAFFIC
                || this == FunctionValue.FOLIAGE_CROWN_FACTOR;
    }

    public boolean isMonetary() {
        return monetary;
    }
}
