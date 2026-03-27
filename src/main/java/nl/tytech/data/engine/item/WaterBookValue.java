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

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 * Book value for Water Balance (both positive or negative)
 *
 * @author Frank Baars & Maxim Knepfle
 *
 */
public class WaterBookValue extends BookValue {

    public enum WaterValue implements Type {

        WATER_SURFACE("Water Surface"),

        LAND_SURFACE("Land Surface"),

        BUILDING_STORAGE("Building Storage"),

        SEWER_STORAGE("Sewer Storage"),

        SEWER_OUT_EXTERNAL("Sewer Out External"),

        UNDERGROUND_STORAGE("Saturated Ground Storage"),

        UNSAT_STORAGE("Unsaturated Ground Storage"),

        EVAPORATED("Evaporated"),

        OUTLET("Outlet"),

        BREACH_OUT("Breach Out"),

        // in:
        RAIN("Rain"),

        INLET("Inlet"),

        INUNDATED("Inundated"),

        BREACH("Breach"),

        BOTTOM_IN("Bottom in"),

        BOTTOM_OUT("Bottom out")

        ;

        private final String term;
        private final Detail buildingDetail;

        private WaterValue(String term) {
            this(term, null);
        }

        private WaterValue(String term, Detail buildingDetail) {
            this.buildingDetail = buildingDetail;
            this.term = term;
        }

        public Detail getDetail() {
            return buildingDetail;
        }

        public String getTerm() {
            return term;
        }

        @Override
        public ClientTerms getTranslationTerm() {
            return ClientTerms.COST_DEFAULT;
        }
    }

    private static final long serialVersionUID = 946868301600695458L;

    @XMLValue
    private WaterValue costType = WaterValue.LAND_SURFACE;

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private Integer overlayID = Item.NONE;

    @XMLValue
    private boolean in = true;

    public WaterBookValue() {
    }

    public WaterBookValue(final Overlay overlay, final MapLink mapLink, Integer linkID, WaterValue cost, final String name,
            final double value, boolean in) {
        super(mapLink, linkID, name, value);
        this.overlayID = overlay.getID();
        this.costType = cost;
        this.in = in;
    }

    public Overlay getOverlay() {
        return getItem(MapLink.OVERLAYS, overlayID);
    }

    public Integer getOverlayID() {
        return overlayID;
    }

    @Override
    public WaterValue getType() {
        return costType;
    }

    public boolean isIn() {
        return in;
    }
}
