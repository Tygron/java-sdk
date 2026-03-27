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

import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.TerrainType;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public enum MeasureEditType {

    BUILDING, RAISE, FLATTEN, WATER, UPGRADE, LEVEE, GEOTIFF, GRID;

    public static final MeasureEditType[] VALUES = MeasureEditType.values();
    public static final MeasureEditType[] NO_UPGRADE = new MeasureEditType[] { BUILDING, RAISE, FLATTEN, WATER, LEVEE, GEOTIFF, GRID };

    public final double getDefaultHeightAdjustment() {
        switch (this) {
            case WATER:
                return -2.0;
            case RAISE:
                return 2.0;
            case FLATTEN:
            default:
                return 0.0;
        }
    }

    public final Integer getDefaultTerrainTypeID() {
        switch (this) {
            case WATER:
                return TerrainType.WATER_TERRAIN_ID;
            case RAISE:
            case FLATTEN:
                return TerrainType.GRASSLAND_TERRAIN_ID;
            default:
                return Item.NONE;
        }
    }

    public final String getTitle() {
        return StringUtils.capitalizeWithSpacedUnderScores(this) + "s";
    }
}
