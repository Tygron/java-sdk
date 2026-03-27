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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.EnumOrderedItem;

/**
 * Time Recording to measure Project Performance
 * @author Maxim Knepfle
 */
public class Recording extends EnumOrderedItem<Recording.Type> {

    public enum Type {

        TOTAL("Total"),

        LOAD_QUERIES("Init Queries"),

        BEFORE_TRIGGERS(1, "API Triggers (before)"),

        BEFORE_UPDATE_QUERIES(2, "Update Queries (before)"),

        GRIDS(3, "Grids"),

        RASTER("Rasterizing"),

        GRID_MODELS("Simulation Models"),

        AFTER_UPDATE_QUERIES(4, "Update Queries (after)"),

        ALL_QUERIES("TQL Queries"),

        QUERY_GEOMETRIES("Shared Geometries"),

        GEO_QUERIES("Unique Geometry Queries"),

        ITEM_QUERIES("Unique Item Queries"),

        CALCULATORS(5, "Calculators"),

        AFTER_TRIGGERS(6, "API Triggers (after)"),

        @Deprecated(since = "August 2025: Replaced by CALCULATORS")
        EXCELS("Excels"),

        @Deprecated(since = "August 2025: Replaced by BEFORE_UPDATE_QUERIES and AFTER_UPDATE_QUERIES")
        GRID_UPDATE_QUERIES("Grid Updating Queries"),

        @Deprecated(since = "August 2025: Replaced by GRIDS")
        GPUS("Calculating Grids"),

        ;

        private String desc;

        private Type(Integer step, String desc) {
            this.desc = (step != null ? "Step " + step.toString() + ": " : "") + desc;
        }

        private Type(String desc) {
            this(null, desc);
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
        return getType().desc;
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
