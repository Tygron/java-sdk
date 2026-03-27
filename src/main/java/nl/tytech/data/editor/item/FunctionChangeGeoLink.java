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
package nl.tytech.data.editor.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.serializable.Category;

/**
 * Function changing geo linkage
 *
 * @author Frank Baars
 */
public class FunctionChangeGeoLink extends FunctionGeoLink {

    private static final long serialVersionUID = -4701011190670031139L;

    @XMLValue
    private HashMap<String, List<String>> featureAttributes = new HashMap<>();

    @XMLValue
    private boolean add = false;

    @XMLValue
    private boolean update = true;

    @XMLValue
    private Category category = null;

    public boolean allowFunctionChange() {
        return update;
    }

    public Map<String, List<String>> getAttributes() {
        return featureAttributes;
    }

    @Override
    public Collection<Category> getCategories() {
        Function function = getFunction();
        if (function == null) {
            Set<Category> categories = new HashSet<>();
            if (category != null) {
                categories.add(category);
            }
            return categories;
        }
        return super.getCategories();
    }

    public Category getCategory() {
        return category;
    }

    public Layer getLayer() {

        Layer groundLayerType = null;
        Function function = getFunction();
        if (function != null) {
            groundLayerType = function.getLayer();
        } else if (getCategory() != null) {
            groundLayerType = getCategory().getLayer();
        }
        return groundLayerType;
    }

    public boolean isAdd() {
        return add;
    }

}
