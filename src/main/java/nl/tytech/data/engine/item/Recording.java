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

import nl.tytech.core.item.annotations.Description;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.util.ObjectUtils;

/**
 * Time Recording to measure Project Performance
 * @author Maxim Knepfle
 */
public class Recording extends EnumOrderedItem<Recording.Type> {

    public enum Type {

        @Description("Total")
        TOTAL,

        @Description("Init Queries")
        LOAD_QUERIES,

        @Description("Step 1: API Triggers (before)")
        BEFORE_TRIGGERS,

        @Description("Step 2: Update Queries (before)")
        BEFORE_UPDATE_QUERIES,

        @Description("Step 3: Grids")
        GRIDS,

        @Description("Rasterizing")
        RASTER,

        @Description("Simulation Models")
        GRID_MODELS,

        @Description("Step 4: Update Queries (after)")
        AFTER_UPDATE_QUERIES,

        @Description("TQL Queries")
        ALL_QUERIES,

        @Description("Shared Geometries")
        QUERY_GEOMETRIES,

        @Description("Unique Geometry Queries")
        GEO_QUERIES,

        @Description("Unique Item Queries")
        ITEM_QUERIES,

        @Description("Step 5: Calculators")
        CALCULATORS,

        @Description("Step 6: API Triggers (after)")
        AFTER_TRIGGERS,

        @Description("Deprecated: Replaced by CALCULATORS")
        @Deprecated(since = "August 2025: Replaced by CALCULATORS")
        EXCELS,

        @Description("Deprecated: Replaced by BEFORE_UPDATE_QUERIES and AFTER_UPDATE_QUERIES")
        @Deprecated(since = "August 2025: Replaced by BEFORE_UPDATE_QUERIES and AFTER_UPDATE_QUERIES")
        GRID_UPDATE_QUERIES,

        @Description("Deprecated: Replaced by GRIDS")
        @Deprecated(since = "August 2025: Replaced by GRIDS")
        GPUS;

        public final String getDescription() {
            return ObjectUtils.getDescription(this);
        }
    }

    private static final long serialVersionUID = -8755571740380078803L;

    @XMLValue
    private long timeMS = 0;

    @XMLValue
    private Integer count = null;

    public Recording() {
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }

    public final String getName() {
        return getType().getDescription();
    }

    public long getTimeMS() {
        return timeMS;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setTimeMS(long timeMS) {
        this.timeMS = timeMS;
    }

    @Override
    public String toString() {
        return getName() + ": " + getTimeMS();
    }
}
