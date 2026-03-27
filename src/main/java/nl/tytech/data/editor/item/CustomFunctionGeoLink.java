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
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.ConstructionPeriod;

/**
 *
 * @author Frank Baars
 *
 */
public abstract class CustomFunctionGeoLink extends CustomGeoLink {

    private static final long serialVersionUID = 8486065448433205982L;

    public static Stakeholder.Type getDefaultStakeholderType(Iterable<Category> categories) {
        for (Category cat : categories) {
            switch (cat) {
                case AGRICULTURE:
                    return Stakeholder.Type.FARMER;
                case SHOPPING:
                case LEISURE:
                case INDUSTRY:
                case OFFICES:
                    return Stakeholder.Type.COMPANY;
                case STUDENT:
                case SOCIAL:
                    return Stakeholder.Type.HOUSING_CORPORATION;
                case NORMAL:
                case LUXE:
                case SENIOR:
                    return Stakeholder.Type.CIVILIAN;
                case EDUCATION:
                    return Stakeholder.Type.EDUCATION;
                case HEALTHCARE:
                    return Stakeholder.Type.HEALTHCARE;
                default:
            }
        }
        /**
         * All other is by default owned by the municipality
         */
        return Stakeholder.Type.MUNICIPALITY;
    }

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer functionID = Item.NONE;

    public CustomFunctionGeoLink() {

    }

    public CustomFunctionGeoLink(String name) {
        setName(name);
    }

    public Collection<Category> getCategories() {
        return this.getFunction().getCategories();
    }

    public List<ConstructionPeriod> getConstructionPeriods() {
        return getFunction().getConstructionPeriods();
    }

    @Override
    public Type getDefaultStakeholderType() {
        return getDefaultStakeholderType(getCategories());
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, functionID);
    }

    public Integer getFunctionID() {
        return functionID;
    }

    public List<Region> getRegions() {
        return getFunction().getRegions();
    }

    @Override
    public boolean isWater() {
        return false;
    }

    public void setFunctionID(Integer functionID) {
        this.functionID = functionID;
    }

}
