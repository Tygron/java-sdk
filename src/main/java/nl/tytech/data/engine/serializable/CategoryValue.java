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
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.Function.FunctionValueGroup;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * Category value enum
 *
 * @author Maxim Knepfle
 *
 */
public enum CategoryValue implements Value {

    /**
     * Size of a house in m2, all other use 1m
     */
    CATEGORY_WEIGHT(FunctionValueGroup.CONSTRUCTION, ClientTerms.CATEGORY_WEIGHT, Double.MIN_VALUE, Double.MAX_VALUE, false, UnitType.NONE),

    /**
     * Size of a house in m2, all other use 1m
     */
    UNIT_SIZE_M2(FunctionValueGroup.CONSTRUCTION, "Unit Size", ClientTerms.UNIT_FLOORSPACE, 0, Double.MAX_VALUE, false, UnitType.SURFACE),

    /**
     * Demand for heat flow per m2 floorspace start value.
     */
    HEAT_FLOW_M2_START_VALUE(FunctionValueGroup.NETWORK, "Heat Flow value at start year", ClientTerms.HEAT_FLOW_M2_START_VALUE, -100, 100,
            false, UnitType.ENERGY),

    /**
     * Demand for heat flow start year.
     */
    HEAT_FLOW_M2_START_YEAR(FunctionValueGroup.NETWORK, "Heat Flow formula start year", ClientTerms.HEAT_FLOW_M2_START_YEAR, 0, 3000, false,
            UnitType.NONE),

    /**
     * Demand for heat flow per m2 floorspace change per year after start year.
     */
    HEAT_FLOW_M2_CHANGE_PER_YEAR(FunctionValueGroup.NETWORK, "Heat Flow change per year", ClientTerms.HEAT_FLOW_M2_CHANGE_PER_YEAR,
            -Double.MAX_VALUE, Double.MAX_VALUE, false, UnitType.ENERGY),

    /**
     * Heat flow to GJ multiplier
     */
    HEAT_POWER_TO_FLOW_MULTIPLIER(FunctionValueGroup.NETWORK, "Heat Power to Flow multiplier", ClientTerms.HEAT_POWER_TO_FLOW_MULTIPLIER,
            -Double.MAX_VALUE, Double.MAX_VALUE, false, UnitType.NONE),

    /**
     * Park lots per m2 floorspace
     */
    PARKING_LOTS_PER_M2(FunctionValueGroup.CAR_TRAFFIC, "Parking Lots", ClientTerms.DETAIL_PARKING_SPACES, 0, 1, false, UnitType.NONE,
            UnitType.SURFACE),

    /**
     * Park lots demand per m2 floorspace
     */
    PARKING_LOTS_DEMAND_PER_M2(FunctionValueGroup.CAR_TRAFFIC, "Parking Lots Demand", ClientTerms.DETAIL_PARKING_SPACES_DEMAND, 0, 2, false,
            UnitType.NONE, UnitType.SURFACE),

    /**
     * Building cost in euro per M2 floorspace.
     */
    CONSTRUCTION_COST_M2(FunctionValueGroup.FINANCIAL, "Construction Cost", ClientTerms.CONSTRUCTION_COST, 0, Double.MAX_VALUE, true,
            UnitType.NONE, UnitType.SURFACE),

    /**
     * Demolish cost in euro per M2 floorspace.
     */
    DEMOLISH_COST_M2(FunctionValueGroup.FINANCIAL, "Demolition Cost", ClientTerms.DETAIL_DEMOLISH_COST, 0, Double.MAX_VALUE, true,
            UnitType.NONE, UnitType.SURFACE),

    /**
     * Owner buyout cost in euro per M2 floorspace.
     */
    BUYOUT_COST_M2(FunctionValueGroup.FINANCIAL, "Buyout Cost", ClientTerms.DETAIL_BUYOUT_COST, 0, Double.MAX_VALUE, true, UnitType.NONE,
            UnitType.SURFACE),

    /**
     * Sell price in euro per M2 floorspace.
     */
    SELL_PRICE_M2(FunctionValueGroup.FINANCIAL, "Sell Price", ClientTerms.DETAIL_SELL_PRICE, 0, Double.MAX_VALUE, true, UnitType.NONE,
            UnitType.SURFACE);

    public static final Map<String, CategoryValue> VALUE_MAP = new HashMap<>();
    public static final CategoryValue[] VALUES;
    static {
        List<CategoryValue> types = new ArrayList<>();
        for (CategoryValue type : CategoryValue.values()) {
            Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
            if (depAnno == null) {
                types.add(type);
                VALUE_MAP.put(type.name(), type);
            }
        }
        types.sort(ObjectUtils.ALPHANUMERICAL_ORDER);
        VALUES = types.toArray(new CategoryValue[types.size()]);
    }

    private final double minValue;
    private final double maxValue;
    private final ClientTerms description;
    private final UnitType[] unitTypes;
    private final boolean monetary;
    private final String editorName;
    private final FunctionValueGroup group;

    private CategoryValue(FunctionValueGroup group, ClientTerms description, double minValue, double maxValue, boolean monetary,
            UnitType... unitTypes) {
        this(group, null, description, minValue, maxValue, monetary, unitTypes);
    }

    private CategoryValue(FunctionValueGroup group, String editorName, ClientTerms description, double minValue, double maxValue,
            boolean monetary, UnitType... unitTypes) {
        this.description = description;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.unitTypes = unitTypes.length == 0 ? new UnitType[] { UnitType.NONE } : unitTypes;
        this.monetary = monetary;
        this.editorName = StringUtils.containsData(editorName) ? editorName : StringUtils.capitalizeWithSpacedUnderScores(this);
        this.group = group;
    }

    @Override
    public ClientTerms getClientTerm() {
        return description;
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
    public double[] getSIUnitValue(double[] values, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toSIValues(values, getUnitTypes());
    }

    @Override
    public String getUnit(TCurrency currency, UnitSystemType unitSystem) {

        if (isMonetary()) {
            return unitSystem.getImpl().getUnitAbbreviation(currency, getUnitTypes());
        } else {
            return unitSystem.getImpl().getUnitAbbreviation(getUnitTypes());
        }
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
    public String getUnitValueFormatted(double[] value, UnitSystemType unitSystem) {
        return unitSystem.getImpl().toLocalValueWithFormatting(value, getUnitTypes());
    }

    public boolean isMonetary() {
        return monetary;
    }
}
