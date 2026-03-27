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
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.serializable.Category;

/**
 * Zone extended by category check
 *
 * @author Frank Baars
 */
public class CategoryZone extends Zone {

    private static final long serialVersionUID = 589934753503086968L;

    @XMLValue
    @ListOfClass(Category.class)
    private ArrayList<Category> allowedCategories = new ArrayList<>();

    public boolean addAllowedCategory(Category category) {
        if (allowedCategories.contains(category)) {
            return false;
        }
        allowedCategories.add(category);
        return true;
    }

    public boolean areCategoriesConformZoningPlan(Building building) {
        if (!building.isZoningPermitRequired()) {
            return true;
        }
        return allowedCategories.containsAll(building.getCategories());
    }

    public List<Category> getAllowedCategories() {
        return this.allowedCategories;
    }

    public boolean isCategoryAllowed(Category category) {
        return allowedCategories.contains(category);
    }

    public boolean removeCategory(Category category) {
        return allowedCategories.remove(category);
    }

    public void setAllowedCategories(List<Category> categories) {
        allowedCategories = new ArrayList<>();
        for (Category category : categories) {
            addAllowedCategory(category);
        }
    }
}
