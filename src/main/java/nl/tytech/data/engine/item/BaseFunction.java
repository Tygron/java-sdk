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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.data.engine.serializable.FaceType;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.util.StringUtils;

/**
 * BaseFunction: Generic base function can be duplicated by a DuplicateFunction
 *
 * @author Maxim Knepfle
 */
public class BaseFunction extends Function {

    private static final long serialVersionUID = 295091661000494566L;

    @AssetDirectory(ACTION_IMAGE_LOCATION)
    @XMLValue
    private String imageName = DEFAULT_IMAGE;

    @XMLValue
    @ListOfClass(Region.class)
    private ArrayList<Region> regions = new ArrayList<>();

    @Html
    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private int dimension = Item.NONE;

    @XMLValue
    private String name = "0_new function";

    @XMLValue
    private HashMap<FaceType, String> textures = new HashMap<>();

    @XMLValue
    private HashMap<FaceType, float[]> decals = null;

    @XMLValue
    @ItemIDField(MapLink.MODEL_SETS)
    private Integer modelSetID = Item.NONE;

    @XMLValue
    @ListOfClass(Stakeholder.Type.class)
    private ArrayList<Stakeholder.Type> defaults = new ArrayList<>();

    @XMLValue
    private PlacementType placementType = PlacementType.LAND;

    @XMLValue
    @ListOfClass(ConstructionPeriod.class)
    private ArrayList<ConstructionPeriod> constructionPeriods = new ArrayList<>();

    /**
     * NOTE: FUNCTION DOES NOT HAVE MAQUETTE VALUES!!!
     */
    @XMLValue
    private HashMap<String, double[]> attributes = new HashMap<>();

    @XMLValue
    private ArrayList<Category> categories = new ArrayList<>();

    public BaseFunction() {

    }

    public BaseFunction(String name) {
        this.name = name;
    }

    @Override
    protected double[] getAttributeArray(MapType mapType, String key, boolean allowOverride, Integer catFunctionID) {

        // Try override first
        if (allowOverride && this.getLord() != null) {
            FunctionOverride override = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (override != null && override.hasAttribute(mapType, key)) {
                return override.getAttributeArray(mapType, key);
            }
        }

        // try my Function next, NOTE: FUNCTION DOES NOT HAVE MAQUETTE VALUES!!!
        double[] value = attributes.get(key);
        if (value != null) {
            return value;
        }

        // Fallback to category
        List<Category> categories = getCategories(catFunctionID);
        for (int i = 0; i < categories.size(); i++) {
            double[] catValue = categories.get(i).getAttributeArray(key);
            if (catValue != null) {
                return catValue;
            }
        }

        return AttributeItem.EMPTY;
    }

    @Override
    public Collection<String> getAttributes() {
        return attributes.keySet();
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {
        return getAttributes();// functions have no MapType
    }

    @Override
    public List<ConstructionPeriod> getConstructionPeriods() {
        return constructionPeriods;
    }

    @Override
    public float[] getDecals(FaceType faceType) {

        if (decals != null && decals.containsKey(faceType)) {
            return decals.get(faceType);
        }
        /**
         * Fallback to first category (always has a value).
         */
        for (Category cat : this.getCategories()) {
            return cat.getDecals(faceType);
        }
        return null;
    }

    /**
     * Default size for buildings like 1x1 or 3x3.
     */
    @Override
    public final int getDefaultDimension() {
        return dimension;
    }

    @Override
    public String getDescription() {

        String result = StringUtils.EMPTY;
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getDescription())) {
            result = functionOverride.getDescription();
        } else {
            result = description;
        }

        if (result == null || result.equals(StringUtils.EMPTY)) {
            return "<p>" + getName() + "</p>";
        } else {
            return result;
        }
    }

    @Override
    public String getImageLocation() {

        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageLocation();
        }
        return ACTION_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {

        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageName();
        } else if (StringUtils.containsData(imageName)) {
            return imageName;
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.FUNCTIONS;
    }

    @Override
    public ModelSet getModelSet() {
        return this.getItem(MapLink.MODEL_SETS, this.getModelSetID());
    }

    public Integer getModelSetID() {
        return this.modelSetID;
    }

    /**
     * The name of the function
     *
     * @return
     */
    @Override
    public final String getName() {

        String result = name;
        if (this.getLord() != null) {
            FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (functionOverride != null && StringUtils.containsData(functionOverride.getName())) {
                result = functionOverride.getName();
            }
        }

        for (Category cat : this.getCategories()) {
            if (cat.isRoad() || cat == Category.NATURE || cat == Category.PARK || cat == Category.OTHER) {
                return result;
            }
        }

        String floors = StringUtils.EMPTY;

        if (result.contains(StringUtils.LANG_SPLIT)) {

            if (this.getMinFloorsFunction() == this.getMaxFloorsFunction()) {
                floors = " (" + this.getMinFloorsFunction() + "h)";
            } else {
                floors = " (" + this.getMinFloorsFunction() + "-" + this.getMaxFloorsFunction() + "h)";
            }

            result = result.replaceFirst(StringUtils.LANG_SPLIT, floors + StringUtils.LANG_SPLIT);
        } else {
            result += floors;
        }
        return result;
    }

    public String getOrginalName() {
        return this.name;
    }

    @Override
    public final List<Category> getOriginalCategories() {
        return this.categories;
    }

    @Override
    public PlacementType getPlacementType() {
        return placementType;
    }

    @Override
    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public double getRoofInset() {
        return this.getModelSet().getRoofInset();
    }

    @Override
    public String getTexture(FaceType type) {

        String texture = this.textures.get(type);
        if (type == FaceType.ROOF && texture == null) {
            for (Category cat : this.getCategories()) {
                return cat.getDefaultRoofTexture();
            }
        } else if (type == FaceType.BASEMENT && texture == null) {
            return Category.DEFAULT_BUILDING_BASEMENT_TEXTURE;
        }
        return texture;
    }

    @Override
    public boolean hasAttribute(MapType mapType, String key) {

        // Try override first
        if (this.getLord() != null) {
            FunctionOverride override = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
            if (override != null && override.hasAttribute(mapType, key)) {
                return true;
            }
        }

        // try my Function next, NOTE: FUNCTION DOES NOT HAVE MAQUETTE VALUES!!!
        if (attributes.containsKey(key)) {
            return true;
        }

        // Fallback to category
        List<Category> categories = getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getAttributeArray(key) != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isDefaultFunction(Stakeholder.Type stakeholderType) {
        return defaults.contains(stakeholderType);
    }

    /**
     * When true this function is either region or part of given region.
     * @param region
     * @return
     */
    @Override
    public boolean isInRegion(Region region) {
        return regions.size() == 0 || region == null || regions.contains(region);
    }

    @Override
    public boolean isWalledModel() {

        if (textures == null || textures.isEmpty()) {
            return false;
        }
        // only roof texture is not valid wall
        if (textures.size() == 1 && textures.containsKey(FaceType.ROOF)) {
            return false;
        }
        // must have wall textures
        return true;
    }

    public void removeDefaultValue(FunctionValue functionValue) {

        if (this.getLord() != null) {
            throw new IllegalArgumentException("Only allowed to remove default value when not in normal simulation!");
        }
        attributes.remove(functionValue.name());
    }

    public void setDefaultValue(FunctionValue functionValue, double[] value) {

        if (this.getLord() != null) {
            throw new IllegalArgumentException("Only allowed to set default value when not in normal simulation!");
        }
        attributes.put(functionValue.name(), value);
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        // store default function values in attribute map so they can be accessed by API Users
        for (FunctionValue value : FunctionValue.ACTIVE_VALUES) {
            attributes.putIfAbsent(value.name(), getValueArray(value));
        }

        // store default cat values in attribute map so they can be accessed by API Users
        for (Category cat : getCategories()) {
            for (CategoryValue catValue : CategoryValue.VALUES) {
                attributes.putIfAbsent(cat.getAttributeKey(catValue), getValueArray(catValue));
            }
        }

        return result;
    }

}
