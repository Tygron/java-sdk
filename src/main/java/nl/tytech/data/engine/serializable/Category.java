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

import static nl.tytech.data.engine.serializable.FunctionValue.CRITICAL_INFRASTRUCTURE;
import java.util.HashMap;
import java.util.Map;
import nl.tytech.data.engine.item.AttributeItem.BaseAttribute;
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Main categories to group building types.
 * @author Maxim Knepfle
 *
 */
public enum Category {

    /**
     * Social housing.
     */
    SOCIAL(ClientTerms.FUNCTION_CATEGORY_SOCIAL,
            // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, -5,
            // color
            new TColor(219, 59, 108),
            // sell build demolish (costs per M2)
            3000, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            80, -0.03f, 0, 2.5f, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86,
            // safety and disturbance distance m
            0, 0),

    /**
     * Mid-range homes and apartments.
     */
    NORMAL(ClientTerms.FUNCTION_CATEGORY_NORMAL,
            // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, 0,
            // color
            new TColor(219, 111, 0),
            // sell build demolish (costs per M2)
            4000, 3000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            150, -0.017f, 0, 3, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86,
            // safety and disturbance distance m
            0, 0),
    /**
     * Luxurious villa's and penthouses.
     */
    LUXE(ClientTerms.FUNCTION_CATEGORY_LUXE,
            // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            4, 7,
            // color
            new TColor(219, 16, 23),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            250, -0.009f, 0, 3, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86,
            // safety and disturbance distance m
            0, 0),
    /**
     * Roads (both small and large)
     */
    ROAD(ClientTerms.FUNCTION_CATEGORY_ROAD,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(100, 100, 100),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_road.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),
    /**
     * Paved areas like squares, parking lots
     */
    PAVED_AREA(ClientTerms.FUNCTION_CATEGORY_PAVED_AREA,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            8, -2,
            // color
            new TColor(161, 157, 165),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_watersquare.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),
    /**
     * Educational building.
     */
    EDUCATION(ClientTerms.FUNCTION_CATEGORY_EDUCATION,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(208, 233, 250),
            // sell build demolish (costs per M2)
            0, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.06f, 0, 4, "category_apartments.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.39783, 0.00174, 3.78,
            // safety and disturbance distance m
            0, 30),
    /**
     * Healthcare building.
     */
    HEALTHCARE(ClientTerms.FUNCTION_CATEGORY_HEALTHCARE,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, 2,
            // color
            new TColor(168, 0, 255),
            // sell build demolish (costs per M2)
            0, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.08f, 0, 4, "category_office.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.4296, 0.001, 6.3,
            // safety and disturbance distance m
            0, 30),
    /**
     * Public cultivated Park
     */
    PARK(ClientTerms.FUNCTION_CATEGORY_PARK,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            -8, 4,
            // color
            new TColor(137, 176, 41),
            // sell build demolish (costs per M2)
            0, 500, 10,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 1f, 1, "category_park.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),
    /**
     * Raw nature (mostly trees)
     */
    NATURE(ClientTerms.FUNCTION_CATEGORY_NATURE,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            -10, 3,
            // color
            new TColor(94, 170, 79),
            // sell build demolish (costs per M2)
            0, 0, 10,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 1f, 1, "category_park.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),
    /**
     * Industry (both heavy and normal)
     */
    INDUSTRY(ClientTerms.FUNCTION_CATEGORY_INDUSTRY,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, -7,
            // color
            new TColor(209, 208, 205),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.02f, 0, 4, "category_industry.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.14738564, 0, 4.5,
            // safety and disturbance distance m
            0, 30),
    /**
     * Offices
     */
    OFFICES(ClientTerms.FUNCTION_CATEGORY_OFFICES,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            7, 0,
            // color
            new TColor(243, 176, 46),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.02f, 0, 4, "category_office.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.32340, 0.00066, 4.5,
            // safety and disturbance distance m
            0, 10),
    /**
     * The rest
     */
    OTHER(ClientTerms.FUNCTION_CATEGORY_OTHER,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, 0,
            // color
            new TColor(83, 117, 71),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 0, 3, "category_actor.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),
    /**
     * Senior/Elderly people housing.
     */
    SENIOR(ClientTerms.FUNCTION_CATEGORY_SENIOR,
            // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, 1,
            // color
            new TColor(194, 94, 49),
            // sell build demolish (costs per M2)
            3000, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            100, -0.015f, 0, 2, "category_house.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86,
            // safety and disturbance distance m
            0, 0),

    /**
     * Underground without a building on top
     */
    UNDERGROUND(ClientTerms.FUNCTION_CATEGORY_UNDERGROUND,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            0, 0,
            // color
            new TColor(135, 60, 0),
            // sell build demolish (costs per M2)
            0, 1, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_sewers.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),

    /**
     * Shops including restaurants and bars
     */
    SHOPPING(ClientTerms.FUNCTION_CATEGORY_SHOPPING,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            7, -2,
            // color
            new TColor(243, 200, 46),
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.033f, 0, 4, "category_office.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.318158, 0.00106, 4.32,
            // safety and disturbance distance m
            0, 10),

    /**
     * Agriculture
     */
    AGRICULTURE(ClientTerms.FUNCTION_CATEGORY_AGRICULTURE,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            4, 0,
            // color
            TColor.WHITE,
            // sell build demolish (costs per M2)
            5000, 4000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 0, 3, "category_roof.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 30),
    /**
     * Recreation, sports, culture
     */
    LEISURE(ClientTerms.FUNCTION_CATEGORY_LEISURE,
            // type is housing and is part of AllocationPlan
            false, true,
            // heat, Quality of Life
            6, 0,
            // color
            new TColor(255, 255, 71),
            // sell build demolish (costs per M2)
            5000, 4000, 500,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, -0.013f, 0, 3, "category_heart.png", true,
            // heat flow start year, start value, change per year, power multiplier
            1900, -0.41016, 0.0022, 5.04,
            // safety and disturbance distance m
            0, 50),
    /**
     * Student housing. (form of social housing)
     */
    STUDENT(ClientTerms.FUNCTION_CATEGORY_STUDENT,
            // type is housing and is part of AllocationPlan
            true, true,
            // heat, Quality of Life
            5, -5,
            // color
            new TColor(219, 200, 0),
            // sell build demolish (costs per M2)
            3000, 2000, 250,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            20, -0.008f, 0, 2.5f, "category_apartments.png", false,
            // heat flow start year, start value, change per year, power multiplier
            1978, -0.322532, 0.004685, 4.86,
            // safety and disturbance distance m
            0, 0),
    /**
     * Gardens around houses
     */
    GARDEN(ClientTerms.FUNCTION_CATEGORY_GARDEN,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            0, 0,
            // color
            new TColor(191, 210, 155),
            // sell build demolish (costs per M2)
            0, 500, 10,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0f, 1f, 1, "category_park.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),

    /**
     * Roads (both small and large)
     */
    INTERSECTION(ClientTerms.FUNCTION_CATEGORY_INTERSECTION,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(101, 100, 100),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_road.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0),
    /**
     * Roads (both small and large)
     */
    BRIDGE(ClientTerms.FUNCTION_CATEGORY_BRIDGE,
            // type is housing and is part of AllocationPlan
            false, false,
            // heat, Quality of Life
            6, -2,
            // color
            new TColor(100, 100, 101),
            // sell build demolish (costs per M2)
            0, 100, 20,
            // unit size, parking lots/m2, green m2/m2 and buildtime in months
            1, 0, 0, 2, "category_road.png", true,
            // heat flow start year, start value, change per year, power multiplier
            0, 0, 0, 0,
            // safety and disturbance distance m
            0, 0);

    public static final Category[] VALUES = Category.values();

    private static final Map<FaceType, float[]> defaultDecals = new HashMap<>();
    static {
        defaultDecals.put(FaceType.BASEMENT, new float[] {});
        defaultDecals.put(FaceType.GROUND, new float[] { 10, 1, 2.0f, 2.4f, 4.0f, 0.0f });
        defaultDecals.put(FaceType.EXTRA, new float[] { 5, 0, 2.0f, 2.4f, 4.0f, 1.2f });
        defaultDecals.put(FaceType.TOP, new float[] { 2.5f, 0, 1.0f, 2.4f, 2.0f, 0.6f });
        defaultDecals.put(FaceType.ROOF, new float[] {});
    }
    private static final float[] EMPTY_ARRAY = new float[0];
    private static final String DEFAULT_NATURE_ROOF_TEXTURE = "grass";
    private static final String DEFAULT_BUILDING_ROOF_TEXTURE = "gravel";
    public static final String DEFAULT_BUILDING_BASEMENT_TEXTURE = "concrete";

    private final boolean residence;
    private final ClientTerms term;
    private final String iconName;
    private final boolean single;
    private final boolean road;

    private final HashMap<String, double[]> attributes = new HashMap<>();
    private final Map<CategoryValue, String> attributeKeys = new HashMap<>();

    private Category(ClientTerms term, boolean residence, boolean zoningPermitRequired, double heat, double qol, TColor color,
            double sellPriceM2, double buildCostM2, double demolishCostM2, double unitSizeM2, double parkingLotsDemandPerM2,
            double vegetationFraction, double buildTimeMonths, String iconName, boolean single, double heatFlowStartYear,
            double heatFlowGJm2StartValue, double heatFlowGJm2ChangePerYear, double heatFlowPowerMultiplier, double safetyDistance,
            double disturbanceDistance) {

        this.term = term;
        this.residence = residence;
        this.iconName = iconName;
        this.single = single;
        this.road = term == ClientTerms.FUNCTION_CATEGORY_ROAD || term == ClientTerms.FUNCTION_CATEGORY_BRIDGE
                || term == ClientTerms.FUNCTION_CATEGORY_INTERSECTION;
        boolean paved = term == ClientTerms.FUNCTION_CATEGORY_PAVED_AREA;
        boolean infitratesWater = term == ClientTerms.FUNCTION_CATEGORY_AGRICULTURE || term == ClientTerms.FUNCTION_CATEGORY_NATURE
                || term == ClientTerms.FUNCTION_CATEGORY_GARDEN || term == ClientTerms.FUNCTION_CATEGORY_PARK;

        attributes.put(BaseAttribute.COLOR.name(), new double[] { color.getARGB() });
        put(FunctionValue.BASEMENT_COLOR, new TColor(0.3, 0.3, 0.3).getARGB());
        put(FunctionValue.GROUND_COLOR, TColor.LIGHT_GRAY.getARGB());
        put(FunctionValue.EXTRA_COLOR, TColor.LIGHT_GRAY.getARGB());
        put(FunctionValue.TOP_COLOR, TColor.LIGHT_GRAY.getARGB());
        put(FunctionValue.ROOF_COLOR, TColor.BROWN.getARGB());
        put(FunctionValue.TERRAIN_MIX, infitratesWater ? 0.2 : 0.8);

        put(FunctionValue.MIN_FLOORS, 1d);
        put(FunctionValue.DEFAULT_FLOORS, 1d);
        put(FunctionValue.MAX_FLOORS, zoningPermitRequired ? 5d : 1d);
        put(FunctionValue.SOLID,
                term != ClientTerms.FUNCTION_CATEGORY_OTHER && term != ClientTerms.FUNCTION_CATEGORY_AGRICULTURE
                        && term != ClientTerms.FUNCTION_CATEGORY_UNDERGROUND && zoningPermitRequired
                        || term == ClientTerms.FUNCTION_CATEGORY_BRIDGE ? 1.0 : 0.0);
        put(FunctionValue.BASEMENT_HEIGHT_M,
                term != ClientTerms.FUNCTION_CATEGORY_OTHER && zoningPermitRequired && !infitratesWater ? 2d : 0d);

        if (term == ClientTerms.FUNCTION_CATEGORY_OFFICES || term == ClientTerms.FUNCTION_CATEGORY_SHOPPING
                || term == ClientTerms.FUNCTION_CATEGORY_EDUCATION) {
            put(FunctionValue.BASEMENT_HEIGHT_M, 4.0);
        }

        put(FunctionValue.ZONING_PERMIT_REQUIRED, zoningPermitRequired ? 1d : 0d);
        put(FunctionValue.SLANTING_ROOF_HEIGHT, 0d);
        put(FunctionValue.HEAT_EFFECT, heat);
        put(FunctionValue.DISTANCE_ZONE_M, 0d);
        put(FunctionValue.LIVABILITY_EFFECT, qol);
        put(FunctionValue.VEGETATION_FRACTION, vegetationFraction);
        put(FunctionValue.FOLIAGE_CROWN_FACTOR, term == ClientTerms.FUNCTION_CATEGORY_NATURE ? 0.5 : 0.0);
        put(FunctionValue.BOWEN_RATIO, infitratesWater ? 0.4 : 3.0);
        put(FunctionValue.ALIGN_ELEVATION,
                term == ClientTerms.FUNCTION_CATEGORY_BRIDGE
                        || term != ClientTerms.FUNCTION_CATEGORY_OTHER && zoningPermitRequired && !infitratesWater ? Function.ALIGN_FLAT
                                : Function.ALIGN_SURFACE);

        put(CategoryValue.CATEGORY_WEIGHT, 1d);
        put(CategoryValue.UNIT_SIZE_M2, unitSizeM2);
        put(CategoryValue.CONSTRUCTION_COST_M2, buildCostM2);
        put(CategoryValue.DEMOLISH_COST_M2, demolishCostM2);
        put(CategoryValue.SELL_PRICE_M2, sellPriceM2);

        put(CategoryValue.PARKING_LOTS_PER_M2, 0d);
        put(CategoryValue.PARKING_LOTS_DEMAND_PER_M2, Math.abs(parkingLotsDemandPerM2));
        put(FunctionValue.HEIGHT_OFFSET_M, term == ClientTerms.FUNCTION_CATEGORY_BRIDGE ? 3.0 : 0.0);

        put(FunctionValue.TRAFFIC_NOISE_SIGMA, 0.0);
        put(FunctionValue.TRAFFIC_NOISE_TAU, 0.0);
        put(FunctionValue.TRAFFIC_SPEED, road ? 50.0 : 0.0);
        put(FunctionValue.TRAFFIC_LANES, 0.0);
        put(FunctionValue.PIPES_PERMITTED, road ? 1.0 : 0.0);

        for (TrafficType type : TrafficType.values()) {
            put(type.getNumValue(), 0.0); // default zero
            if (type.getJamValue() != null) { // default only intersections get jammed
                put(type.getJamValue(), term == ClientTerms.FUNCTION_CATEGORY_INTERSECTION ? 0.25 : 0.0);
            }
        }

        put(FunctionValue.MONUMENTAL, 0.0);
        put(FunctionValue.SOLAR_PANELS, 0.0);

        /**
         * Buyout costs are 80% of original price. -> Price you need to pay to kick out the inhabitants
         */
        put(CategoryValue.BUYOUT_COST_M2, zoningPermitRequired ? sellPriceM2 * 0.8d : 0d);

        put(FunctionValue.DRAINAGE, 0d);
        put(FunctionValue.WATER_STORAGE_M2, 0.0);
        put(FunctionValue.WATER_TRANSPIRATION_FACTOR, infitratesWater ? new double[] { 1.0, 1.0, 1.0, 1.0 } : new double[] { 0.0 });
        put(FunctionValue.WATER_MANNING, zoningPermitRequired ? 0.013 : road || paved ? 0.015 : 0.03);
        put(FunctionValue.WATER_MICRORELIEF_M, 0.0);
        put(FunctionValue.ROOT_DEPTH_M, infitratesWater ? 0.05 : 0.0);
        put(FunctionValue.GROUND_INFILTRATION_MD, infitratesWater ? 1.0 : 0.0);
        put(FunctionValue.FLOOR_HEIGHT_M, zoningPermitRequired ? 3.5 : 0.0);
        put(FunctionValue.SEWERED, term != ClientTerms.FUNCTION_CATEGORY_OTHER && term != ClientTerms.FUNCTION_CATEGORY_AGRICULTURE
                && term != ClientTerms.FUNCTION_CATEGORY_UNDERGROUND && zoningPermitRequired || road ? 1.0 : 0.0);
        put(FunctionValue.SAFETY_DISTANCE_M, safetyDistance);
        put(FunctionValue.DISTURBANCE_DISTANCE_M, disturbanceDistance);

        put(CategoryValue.HEAT_FLOW_M2_START_YEAR, heatFlowStartYear);
        put(CategoryValue.HEAT_FLOW_M2_START_VALUE, heatFlowGJm2StartValue);
        put(CategoryValue.HEAT_FLOW_M2_CHANGE_PER_YEAR, heatFlowGJm2ChangePerYear);
        put(CategoryValue.HEAT_POWER_TO_FLOW_MULTIPLIER, heatFlowPowerMultiplier);

        for (CategoryValue value : CategoryValue.values()) {
            String attributeKey = this.name() + StringUtils.UNDER_SCORE + value.name();
            attributeKeys.put(value, attributeKey);
        }

        /**
         * Calc default critical infra score
         */
        if (term == ClientTerms.FUNCTION_CATEGORY_AGRICULTURE) {
            put(CRITICAL_INFRASTRUCTURE, 0);
        } else if (term == ClientTerms.FUNCTION_CATEGORY_EDUCATION || term == ClientTerms.FUNCTION_CATEGORY_HEALTHCARE
                || term == ClientTerms.FUNCTION_CATEGORY_SENIOR) {
            put(CRITICAL_INFRASTRUCTURE, 2);
        } else {
            put(CRITICAL_INFRASTRUCTURE, zoningPermitRequired ? 1 : 0);
        }
    }

    public final double[] getAttributeArray(String key) {
        return attributes.get(key);
    }

    public final String getAttributeKey(CategoryValue catValue) {
        return attributeKeys.get(catValue);
    }

    public final CategoryValue getCategoryValue(String attributeName) {

        if (!StringUtils.containsData(attributeName)) {
            return null;
        }
        if (!(attributeName.startsWith(name()) && attributeName.startsWith(StringUtils.UNDER_SCORE, name().length()))) {
            return null;
        }
        String catValue = attributeName.substring(name().length() + StringUtils.UNDER_SCORE.length());
        return CategoryValue.VALUE_MAP.get(catValue);
    }

    public final ClientTerms getClientTerm() {
        return term;
    }

    public final TColor getColor() {
        return TColor.array(getAttributeArray(BaseAttribute.COLOR.name()));
    }

    public float[] getDecals(FaceType faceType) {

        if (!isZoningPermitRequired()) {
            return EMPTY_ARRAY;
        }
        return defaultDecals.get(faceType);
    }

    public final String getDefaultRoofTexture() {

        if (this == NATURE || this == PARK || this == GARDEN) {
            return DEFAULT_NATURE_ROOF_TEXTURE;
        }
        return DEFAULT_BUILDING_ROOF_TEXTURE;
    }

    public final String getIconName() {
        return iconName;
    }

    public final Layer getLayer() {

        return switch (this) {
            case BRIDGE -> Layer.BRIDGE;
            case UNDERGROUND -> Layer.UNDERGROUND;
            default -> Layer.SURFACE;
        };
    }

    public final boolean isResidential() {
        return residence;
    }

    public final boolean isRoad() {
        return road;
    }

    public final boolean isSingle() {
        return single;
    }

    public final boolean isZoningPermitRequired() {
        return attributes.get(FunctionValue.ZONING_PERMIT_REQUIRED.name())[0] > 0;
    }

    private final void put(CategoryValue catValue, double value) {
        String attributeKey = this.name() + StringUtils.UNDER_SCORE + catValue.name();
        attributes.put(attributeKey, new double[] { value });
    }

    private final void put(FunctionValue key, double value) {
        put(key, new double[] { value });
    }

    private final void put(FunctionValue key, double[] value) {
        attributes.put(key.name(), value);
    }
}
