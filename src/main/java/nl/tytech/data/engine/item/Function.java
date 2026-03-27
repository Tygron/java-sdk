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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.AttributeItem.BaseAttribute;
import nl.tytech.data.engine.item.ModelData.Placement;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.AttributeQueryInterface;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.other.LayerItem;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.data.engine.serializable.FaceType;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Function: This class keeps track of the building types.
 *
 * @author Maxim Knepfle
 */
public abstract class Function extends Item implements Action, ValueItem, AttributeQueryInterface, ImageItem, NamedItem, LayerItem {

    public enum FunctionValueGroup {

        CONSTRUCTION,

        FINANCIAL,

        VISUALISATION,

        NETWORK,

        ENVIRONMENT,

        CAR_TRAFFIC,

        OTHER_TRAFFIC,

        WATER,

        // Special groups
        ATTRIBUTES,

        ASSETS,

        CUSTOM

        ;

        public static final List<FunctionValueGroup> defaultValues() {
            return Arrays.stream(values()).filter(g -> g != ATTRIBUTES && g != ASSETS && g != CUSTOM).toList();
        }

        public boolean isPart(String attributeName) {

            for (FunctionValue value : FunctionValue.values()) {
                if (value.getGroup() == this && value.name().equals(attributeName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum PlacementType {
        WATER, LAND, HYBRID
    }

    public enum Region {

        NORTH_AMERICA("America"),

        NORTHWESTERN_EUROPE("Europe"),

        ASIA("Asia"),

        AFRICA("Africa"),

        /**
         * Rest of the world.
         */
        OTHER(null);

        public static final Region[] VALUES = values();

        private final String continent;

        private Region(String continent) {
            this.continent = continent;
        }

        public TColor getRoadLineColor() {
            return this == NORTH_AMERICA ? TColor.YELLOW : TColor.WHITE;
        }

        public final boolean isTimeZone(String timeZone) {
            return timeZone != null && continent != null && timeZone.toLowerCase().startsWith(continent.toLowerCase());
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public interface Value {

        public ClientWord.ClientTerms getClientTerm();

        public String getEditorName();

        public FunctionValueGroup getGroup();

        public double getMaxValue();

        public double getMinValue();

        public double[] getSIUnitValue(double[] value, UnitSystemType unitSystem);

        public String getUnit(TCurrency currency, UnitSystemType unitSystem);

        public UnitType[] getUnitTypes();

        public double getUnitValue(double value, UnitSystemType unitSystem);

        public String getUnitValueFormatted(double[] value, int decimals, UnitSystemType unitSystem);

        public String getUnitValueFormatted(double[] value, UnitSystemType unitSystem);

        public String name();

    }

    public static final double MIN_FLOOR_HEIGHT_M = 2;
    public static final int MIN_ALLOWED_FLOORS = 1;
    public static final int MAX_ALLOWED_FLOORS = 250;

    public static final double ALIGN_FLAT = 0;
    public static final double ALIGN_FLOATING = 1;
    public static final double ALIGN_SURFACE = 2;

    private static final long serialVersionUID = 295091661000494567L;

    @Override
    public final double getAttribute(MapType mapType, String key) {
        return getAttribute(mapType, key, 0);
    }

    @Override
    public final double getAttribute(MapType mapType, String key, int index) {
        double[] array = getAttributeArray(mapType, key);
        return index >= 0 && array.length > index ? array[index] : AttributeItem.DEFAULT_VALUE;
    }

    @Override
    public double getAttribute(String key) {
        return getAttribute(getDefaultMap(), key);
    }

    @Override
    public final double[] getAttributeArray(MapType mapType, String key) {
        return getAttributeArray(mapType, key, true);
    }

    public final double[] getAttributeArray(MapType mapType, String key, boolean allowOverride) {
        return getAttributeArray(mapType, key, allowOverride, getID());
    }

    protected abstract double[] getAttributeArray(MapType mapType, String key, boolean allowOverride, Integer functionID);

    @Override
    public double[] getAttributeArray(String key) {
        return getAttributeArray(getDefaultMap(), key);
    }

    @Override
    public abstract Collection<String> getAttributes();

    public double getAvgModelHeightM() {

        double avg = 0;
        int counter = 0;
        for (ModelData model : this.getModels()) {
            avg += model.getModelHeightM();
            counter++;
        }
        return counter == 0 ? 0 : avg / counter;
    }

    public double getAvgModelWidthM() {

        double avg = 0;
        int counter = 0;
        for (ModelData model : this.getModels()) {
            avg += model.getModelWidthM();
            counter++;
        }
        return counter == 0 ? 0 : avg / counter;
    }

    /**
     * @return the range
     */
    @Override
    public List<Category> getCategories() {
        return getCategories(getID());
    }

    /**
     * @return the range
     */
    public List<Category> getCategories(Integer functionID) {

        if (this.getLord() != null) {
            FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, functionID);
            if (functionOverride != null && !functionOverride.getCategories().isEmpty()) {
                return functionOverride.getCategories();
            }

            if (!getID().equals(functionID)) {
                Function function = this.getItem(MapLink.FUNCTIONS, functionID);
                if (function != null) {
                    return function.getOriginalCategories();
                }
            }
        }
        return getOriginalCategories();

    }

    @Override
    public double getCategoryFraction(Category cat) {
        if (getCategories().size() == 1) {
            return 1;
        }

        double sum = 0;
        for (Category someCat : getCategories()) {
            sum += getValue(someCat, CategoryValue.CATEGORY_WEIGHT);
        }
        if (sum == 0) {
            return 0;
        }
        return getValue(cat, CategoryValue.CATEGORY_WEIGHT) / sum;
    }

    public final TColor getColor() {
        return TColor.array(getAttributeArray(BaseAttribute.COLOR.name()));
    }

    public abstract List<ConstructionPeriod> getConstructionPeriods();

    public abstract float[] getDecals(FaceType type);

    /**
     * Fixed sized dimensions of buildings like 10x10 or 30x30. The building is always this size and must have models that exactly match the
     * size.
     *
     * @return
     */
    public abstract int getDefaultDimension();

    public final int getDefaultFloors() {
        return (int) this.getValue(FunctionValue.DEFAULT_FLOORS);
    }

    @Override
    public abstract String getDescription();

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {

        Map<String, Object> map = super.getExportAttributes(inherited);

        // Try override first
        if (this.getLord() != null) {
            FunctionOverride override = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (override != null) {
                for (String att : override.getAttributes()) {
                    map.putIfAbsent(att, override.getAttributeArray(att));
                }
            }
        }
        for (String att : getAttributes()) {
            map.putIfAbsent(att, getAttributeArray(att));
        }

        return map;
    }

    /**
     * Get the valid allowed floor amount for this function or return -1 when not allowed!
     * @param heightM
     * @param flatroofOnly
     * @return
     */
    public int getFloorsForHeight(double heightM, boolean flatroofOnly) {

        if (heightM == 0) {
            return -1;
        }

        double slantingRoofHeight = flatroofOnly ? 0 : this.getValue(FunctionValue.SLANTING_ROOF_HEIGHT) / 2f;// take average
        double defaultFloorHeight = this.getValue(FunctionValue.FLOOR_HEIGHT_M);

        if (defaultFloorHeight + slantingRoofHeight >= heightM) {
            return 1;
        }
        if (2 * defaultFloorHeight + slantingRoofHeight >= heightM) {
            return 2;
        }
        // minus ground
        heightM -= defaultFloorHeight;
        // minus top level
        heightM -= defaultFloorHeight;
        heightM -= slantingRoofHeight;
        return 2 + (int) Math.round(heightM / defaultFloorHeight);
    }

    @Override
    public abstract String getImageLocation();

    @Override
    public abstract String getImageName();

    @Override
    public int getImageVersion() {

        if (this.getLord() != null) {
            FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (functionOverride != null && functionOverride.getCategories().size() > 0) {
                return functionOverride.getImageVersion();
            }
        }
        return 0;
    }

    @Override
    public final Building.Layer getLayer() {
        for (Category cat : this.getCategories()) {
            return cat.getLayer();
        }
        return null;
    }

    public final int getMaxFloorsFunction() {
        return (int) this.getValue(FunctionValue.MAX_FLOORS);
    }

    public final int getMinFloorsFunction() {
        return (int) this.getValue(FunctionValue.MIN_FLOORS);
    }

    public final List<ModelData> getModels() {
        return this.getItems(MapLink.MODEL_DATAS, this.getModelSet().getModelIDs());
    }

    public abstract ModelSet getModelSet();

    /**
     * The name of the function
     *
     * @return
     */
    @Override
    public abstract String getName();

    public abstract List<Category> getOriginalCategories();

    public abstract PlacementType getPlacementType();

    public abstract List<Region> getRegions();

    @Override
    public AttributeQueryInterface getRelationAttribute(Relation relation) {
        return null;
    }

    public abstract double getRoofInset();

    public abstract String getTexture(FaceType type);

    @Override
    public double getValue(Category cat, CategoryValue key) {
        return getValue(getDefaultMap(), cat, key);
    }

    @Override
    public double getValue(MapType mapType, Category cat, CategoryValue key) {
        return this.getAttribute(mapType, cat.getAttributeKey(key));
    }

    @Override
    public double getValue(MapType mapType, Value key) {

        if (key instanceof FunctionValue) {
            return getAttribute(mapType, key.name());

        } else {
            // CategoryValue
            double value = 0;
            for (Category cat : getCategories()) {
                value += getValue(mapType, cat, (CategoryValue) key) * getCategoryFraction(cat);
            }
            return value;
        }
    }

    @Override
    public double getValue(Value key) {
        return getValue(getDefaultMap(), key);
    }

    @Override
    public double[] getValueArray(Category cat, CategoryValue key) {
        return getValueArray(getDefaultMap(), cat, key);
    }

    @Override
    public double[] getValueArray(MapType mapType, Category cat, CategoryValue key) {
        return getAttributeArray(mapType, cat.getAttributeKey(key));
    }

    @Override
    public double[] getValueArray(MapType mapType, Value key) {

        if (key instanceof FunctionValue) {
            return getAttributeArray(mapType, key.name());

        } else {
            // note: CategoryValue is always single value for now
            double[] value = new double[] { 0.0 };
            for (Category cat : getCategories()) {
                value[0] += getValue(mapType, cat, (CategoryValue) key) * getCategoryFraction(cat);
            }
            return value;
        }
    }

    @Override
    public double[] getValueArray(Value key) {
        return getValueArray(getDefaultMap(), key);
    }

    @Override
    public boolean hasAttribute(String key) {
        return hasAttribute(getDefaultMap(), key);
    }

    /**
     * When true the actual value is not the orginal/generic one
     * @param key
     * @return
     */
    public final boolean isAttributeOverride(MapType mapType, String key) {
        return !Arrays.equals(getAttributeArray(mapType, key, true), getAttributeArray(mapType, key, false));
    }

    public boolean isAttributeOverride(String key) {
        return this.isAttributeOverride(getDefaultMap(), key);
    }

    public final boolean isBridge() {
        return this.getCategories().contains(Category.BRIDGE);
    }

    /**
     * @return the buildable
     */
    @Override
    public final boolean isBuildable() {
        return true;
    }

    public abstract boolean isDefaultFunction(Stakeholder.Type stakeholderType);

    @Override
    public final boolean isFixedLocation() {
        // buildings are selectable on the map
        return false;
    }

    /**
     * When true this function is either region or part of given region.
     * @param region
     * @return
     */
    public abstract boolean isInRegion(Region region);

    public boolean isIntersectionFunction() {
        return this.getCategories().contains(Category.INTERSECTION);
    }

    public final boolean isResidential() {
        return getCategories().stream().anyMatch(c -> c.isResidential());
    }

    public final boolean isRoadSystem() {

        List<Category> cats = getCategories();
        for (int i = 0; i < cats.size(); i++) {
            Category c = cats.get(i);
            if (c == Category.ROAD || c == Category.INTERSECTION || c == Category.BRIDGE) {
                return true;
            }
        }
        return false;
    }

    /**
     * When true function model has walls (e.g. trees do NOT have walls, houses DO), note a hedge is walled bu not solid
     */
    public abstract boolean isWalledModel();

    public final boolean isZoningPermitRequired() {
        return this.getValue(FunctionValue.ZONING_PERMIT_REQUIRED) > 0.0;
    }

    @Override
    public void setImageName(String name) {
        throw new IllegalArgumentException("Not allowed for Functions, use OverrideFunction.");
    }

    @Override
    public final String toString() {

        // must be in tools mode!
        if (this.getName().contains(StringUtils.LANG_SPLIT)) {
            return (this.getMinFloorsFunction() != this.getMaxFloorsFunction() ? " " : "") + " " + this.getName();
        }
        return this.getName();
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = StringUtils.EMPTY;

        if (this.getCategories().size() == 0) {
            result += "\nFunction (" + getID() + ") " + this.getName() + " has no Categories!";
            return result;
        }

        for (Category cat : this.getCategories()) {
            if (this.getValue(cat, CategoryValue.UNIT_SIZE_M2) < 1) {
                result += "\nFunction (" + getID() + ") " + this.getName()
                        + " has an invalid unit size, unit sizes should be larger then 1 m2!";
            }
            if (cat.isSingle() && this.getCategories().size() > 1) {
                result += "\nFunction (" + getID() + ") " + this.getName() + " has multiple categories but: " + cat.toString()
                        + " should always be alone!";
            }
        }

        /**
         * Validate amount of floors
         *
         */
        if (this.getMinFloorsFunction() < 1) {
            result += "\nFunction " + this.getName() + " needs at least a value of 1 as the minimum amount of floors.";
        } else if (this.getMinFloorsFunction() > this.getMaxFloorsFunction()) {
            result += "\nFunction (" + getID() + ") " + this.getName() + " has minimal amount of floors (" + this.getMinFloorsFunction()
                    + ") should be lower then its maximum: " + this.getMaxFloorsFunction();
        }

        if (this.getMinFloorsFunction() > this.getDefaultFloors() || this.getMaxFloorsFunction() < this.getDefaultFloors()) {
            result += "\nFunction (" + getID() + ") " + this.getName() + " needs to have a default value between: "
                    + this.getMinFloorsFunction() + " - " + this.getMaxFloorsFunction();
        }

        if (this.getDefaultDimension() > 0) {
            List<ModelData> models = this.getModels();
            for (ModelData model : models) {
                if (model.getPlacement() != Placement.POINT) {
                    result += "\nFunction :" + this.getName() + " has a fixed dimension an can only have Furniture Squares.";
                }
                if (model.getDimension() != this.getDefaultDimension()) {
                    result += "\nFunction :" + this.getName() + " has a fixed dimension " + this.getDefaultDimension()
                            + " the model has dimension " + model.getDimension();
                }
            }
        }
        return result;
    }
}
