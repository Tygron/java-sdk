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

import java.util.Collection;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.ConstructionPeriod;
import nl.tytech.data.engine.serializable.FaceType;
import nl.tytech.util.StringUtils;

/**
 * DuplicateFunction: Wrapper class around a base function.
 *
 * @author Maxim Knepfle
 */
public class DuplicateFunction extends Function {

    private static final long serialVersionUID = 295091661010494566L;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer orginalFunctionID = Item.NONE;

    public DuplicateFunction() {
    }

    public DuplicateFunction(Integer orginalFunctionID) {
        this.orginalFunctionID = orginalFunctionID;
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
        return getOriginal().getAttributeArray(mapType, key, allowOverride, catFunctionID);
    }

    @Override
    public Collection<String> getAttributes() {
        return getOriginal().getAttributes();
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {
        return getOriginal().getAttributes(mapType);
    }

    @Override
    public List<ConstructionPeriod> getConstructionPeriods() {
        return getOriginal().getConstructionPeriods();
    }

    @Override
    public float[] getDecals(FaceType type) {
        return getOriginal().getDecals(type);
    }

    @Override
    public final int getDefaultDimension() {
        return getOriginal().getDefaultDimension();
    }

    @Override
    public String getDescription() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getDescription())) {
            return functionOverride.getDescription();
        }
        return getOriginal().getDescription();
    }

    @Override
    public String getImageLocation() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageLocation();
        }
        return getOriginal().getImageLocation();
    }

    @Override
    public String getImageName() {
        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getImageName())) {
            return functionOverride.getImageName();
        }
        return getOriginal().getImageName();
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.FUNCTIONS;
    }

    @Override
    public ModelSet getModelSet() {
        return getOriginal().getModelSet();
    }

    @Override
    public final String getName() {

        /**
         * Try override first
         */
        FunctionOverride functionOverride = this.getItem(MapLink.FUNCTION_OVERRIDES, this.getID());
        if (functionOverride != null && StringUtils.containsData(functionOverride.getName())) {
            return functionOverride.getName();
        }
        return getOriginal().getName();
    }

    private Function getOriginal() {
        return this.getItem(MapLink.FUNCTIONS, orginalFunctionID);
    }

    @Override
    public List<Category> getOriginalCategories() {
        return getOriginal().getOriginalCategories();
    }

    @Override
    public PlacementType getPlacementType() {
        return getOriginal().getPlacementType();
    }

    @Override
    public List<Region> getRegions() {
        return getOriginal().getRegions();
    }

    @Override
    public double getRoofInset() {
        return getOriginal().getRoofInset();
    }

    @Override
    public String getTexture(FaceType type) {
        return getOriginal().getTexture(type);
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
        return getOriginal().hasAttribute(mapType, key);
    }

    @Override
    public boolean isDefaultFunction(Stakeholder.Type stakeholderType) {
        return getOriginal().isDefaultFunction(stakeholderType);
    }

    @Override
    public boolean isInRegion(Region region) {
        return getOriginal().isInRegion(region);
    }

    @Override
    public boolean isWalledModel() {
        return getOriginal().isWalledModel();
    }

    @Override
    public String validated(boolean startNewSession) {

        if (this.getOriginal() == null) {
            return "\nMissing orginal base function with ID: " + this.orginalFunctionID + " for duplicate function with ID: "
                    + this.getID();
        }
        return super.validated(startNewSession);
    }
}
