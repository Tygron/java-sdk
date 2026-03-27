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
package nl.tytech.core.util;

import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;

/**
 * Helper utils for building details.
 *
 * @author Frank Baars
 */
public class DetailUtils {

    public static final double getBuildingDetailForM2(ValueItem valueItem, double buildingSizeM2, double floors, Detail detail,
            boolean isVacant) {

        double floorSizeM2 = buildingSizeM2 * floors;

        switch (detail) {
            case CONSTRUCTION_COST:
                return floorSizeM2 * valueItem.getValue(CategoryValue.CONSTRUCTION_COST_M2);

            case DEMOLISH_COST:
                double costs = valueItem.getValue(CategoryValue.DEMOLISH_COST_M2);
                if (!isVacant) {
                    // also include buyout costs when not vacant.
                    costs += valueItem.getValue(CategoryValue.BUYOUT_COST_M2);
                }
                return floorSizeM2 * costs;

            case BUYOUT_COST:
                return floorSizeM2 * valueItem.getValue(CategoryValue.BUYOUT_COST_M2);

            case SELL_PRICE:
                return floorSizeM2 * valueItem.getValue(CategoryValue.SELL_PRICE_M2);

            case SELLABLE_FLOORSPACE_M2:
                return floorSizeM2;

            case NUMBER_OF_HOUSES:
                double houses = 0;
                for (Category cat : valueItem.getCategories()) {
                    if (cat.isResidential()) {
                        double unitSizeM2 = valueItem.getValue(cat, CategoryValue.UNIT_SIZE_M2);
                        houses += floorSizeM2 * valueItem.getCategoryFraction(cat) / unitSizeM2;
                    }
                }
                return houses;
            default:
                return 0;
        }
    }

    public static double getBuyoutUpgradedBuildingDetailForM2(ValueItem valueItem, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case CONSTRUCTION_COST:
            case SELL_PRICE:
                return 0;
            default:
                return getBuildingDetailForM2(valueItem, surfaceArea, floors, detail, isVacant);

        }
    }

    public static final double getCategoryUnits(ValueItem item, Category cat, double surfaceArea, double floors) {

        /**
         * NOTE: only houses are counted per unit size, other all go for 1 as unit size
         */
        double unitSizeM2 = cat.isResidential() ? item.getValue(cat, CategoryValue.UNIT_SIZE_M2) : 1;
        double floorSizeM2 = surfaceArea * floors;
        double catFloorSizeM2 = floorSizeM2 * item.getCategoryFraction(cat);
        return unitSizeM2 != 0.0 ? catFloorSizeM2 / unitSizeM2 : 0.0;
    }

    public static final double getConstructedBuildingDetailForM2(ValueItem valueItem, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
                return 0;
            default:
                return getBuildingDetailForM2(valueItem, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getDefaultUpgradedFromBuildingDetailForM2(ValueItem valueItem, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
            case CONSTRUCTION_COST:
            case SELL_PRICE:
                return 0;
            default:
                return getBuildingDetailForM2(valueItem, surfaceArea, floors, detail, isVacant);
        }
    }

    public static double getDefaultUpgradedToBuildingDetailForM2(ValueItem valueItem, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
            case CONSTRUCTION_COST:
            case SELL_PRICE:
            case SELLABLE_FLOORSPACE_M2:
                return 0;
            default:
                return getBuildingDetailForM2(valueItem, surfaceArea, floors, detail, isVacant);

        }
    }

    public static final double getDemolishedBuildingDetailForM2(ValueItem valueItem, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case CONSTRUCTION_COST:
            case SELL_PRICE:
                return 0;
            default:
                return getBuildingDetailForM2(valueItem, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getSoldUpgradedBuildingDetailForM2(ValueItem valueItem, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
            case CONSTRUCTION_COST:
                return 0;
            default:
                return getBuildingDetailForM2(valueItem, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getUpgradeDetailForM2(UpgradeType upgrade, double surfaceArea, double floors, Detail detail) {
        switch (detail) {
            case CONSTRUCTION_COST:
                return upgrade.getCosts(surfaceArea, floors);
            default:
                return 0;
        }
    }
}
