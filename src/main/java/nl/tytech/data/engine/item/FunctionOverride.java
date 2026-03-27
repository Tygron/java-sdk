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
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * FunctionOverride: Score of custom indicator and override default values of the function. function ID = override ID
 *
 * @author Maxim Knepfle
 */
public class FunctionOverride extends AttributeItem implements ImageItem {

    public enum AssetValue {

        NAME, DESCRIPTION, ID, IMAGELOCATION, //
        ROOF_COLOR(FunctionValue.ROOF_COLOR), //
        BASEMENT_COLOR(FunctionValue.BASEMENT_COLOR), //
        GROUND_COLOR(FunctionValue.GROUND_COLOR), //
        EXTRA_COLOR(FunctionValue.EXTRA_COLOR), //
        TOP_COLOR(FunctionValue.TOP_COLOR);

        private FunctionValue value = null;

        private AssetValue() {

        }

        private AssetValue(FunctionValue functionValue) {
            this.value = functionValue;
        }

        public FunctionValue getFunctionValue() {
            return value;
        }

    }

    private static final long serialVersionUID = -640351328367911902L;

    @XMLValue
    private ArrayList<Category> categories = new ArrayList<>();

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private String imageName = StringUtils.EMPTY;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    public boolean addNewCategory(Category cat) {

        readDefaults();

        for (Category oldCat : categories) {
            if (oldCat.isSingle()) {
                return false;
            }
        }

        if (!categories.contains(cat) && !cat.isSingle()) {
            categories.add(cat);
            return true;
        }
        return false;
    }

    public void clearFunctionValues() {
        this.categories.clear();
        this.removeAllAttributes();
    }

    public boolean deleteCategory(Category cat) {
        readDefaults();
        return categories.remove(cat);
    }

    public List<Category> getCategories() {
        return categories;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return new ReservedAttribute[0];
    }

    public String getDescription() {
        return description;
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, this.getFunctionID());
    }

    public Integer getFunctionID() {
        return this.getID();
    }

    @Override
    public String getImageLocation() {
        return Action.ACTION_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageVersion() {
        return imageVersion;
    }

    private void readDefaults() {
        // if zero fill with defaults.
        if (categories.size() == 0) {
            Function function = this.getFunction();
            for (Category defaultCat : function.getCategories()) {
                categories.add(defaultCat);
            }
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setImageName(String image) {
        this.imageName = image;
        this.imageVersion++;
    }

    @Override
    public String toString() {

        Function function = this.getFunction();
        if (function != null) {
            return function.getName();
        }
        return FunctionOverride.class.getSimpleName() + StringUtils.WHITESPACE + getID();
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (hasAttribute("WATER_EVAPORATION_FACTOR") && !hasAttribute(FunctionValue.WATER_TRANSPIRATION_FACTOR)) {
            setAttributeArray(FunctionValue.WATER_TRANSPIRATION_FACTOR, getAttributeArray("WATER_EVAPORATION_FACTOR"));
            this.removeAttribute("WATER_EVAPORATION_FACTOR", true);
            TLogger.warning(getName() + " Converted WATER_EVAPORATION_FACTOR -> " + FunctionValue.WATER_TRANSPIRATION_FACTOR);
        }
        return result;
    }
}
