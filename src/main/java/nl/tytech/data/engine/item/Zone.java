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

import java.util.Map;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.ExcelItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.Outline;
import nl.tytech.data.engine.serializable.Relation;

/**
 * Zone: Maintains the functions allocated to the zone.
 *
 * @author Maxim Knepfle
 */
public class Zone extends PolygonAttributeItem implements ExcelItem {

    public enum ZoneAttribute implements ReservedAttribute {

        MAX_BUILDING_HEIGHT(Double.class),

        MAX_ROOF_GUTTER_HEIGHT(Double.class),

        MAX_FLOORS(Double.class),

        MIN_FLOORS(Double.class),

        AMOUNT_FLOORS(Double.class),

        MAX_BUILDING_PCT(Double.class),

        MONUMENTAL(Double.class),

        ;

        private final Class<?> type;

        private ZoneAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return AttributeItem.ZERO;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    private static final long serialVersionUID = 6586583895720453036L;

    public static final String PERMITTER_ATTRIBUTE = "PERMITTER";
    public static final String PARENT_ATTRIBUTE = "PARENT_ZONE";

    private static final String ZONE_TQL_NAME = MapLink.ZONES.getTQLName();

    @XMLValue
    @ItemIDField(MapLink.EXCEL_SHEETS)
    private Integer excelID = 0; // default zone excel

    @XMLValue
    private Outline outline = Outline.DASHED;

    @XMLValue
    @ItemIDField(MapLink.ZONES)
    private Integer parentZoneID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer permitterID = Item.NONE;

    // runtime setting do not save to XML
    private transient boolean excelUpdated = true;

    public Zone() {
    }

    @Override
    public Integer getContentID() {
        return null;
    }

    @Override
    public MapLink getContentMapLink() {
        return null;
    }

    @Override
    public String getContentParam() {
        return null;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return ZoneAttribute.values();
    }

    @Override
    public Integer getExcelID() {
        return this.excelID;
    }

    @Override
    public ExcelSheet getExcelSheet() {
        return getItem(MapLink.EXCEL_SHEETS, getExcelID());
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        if (this.getPermitter() != null) {
            map.put(PERMITTER_ATTRIBUTE, this.getPermitter().getName());
        }
        map.put(PARENT_ATTRIBUTE, parentZoneID);
        return map;
    }

    @Override
    public String getMyParam() {
        return ZONE_TQL_NAME;
    }

    public Outline getOutline() {
        return outline;
    }

    public Zone getParentZone() {
        return getItem(MapLink.ZONES, parentZoneID);
    }

    public Integer getParentZoneID() {
        return parentZoneID;
    }

    public Stakeholder getPermitter() {
        return getItem(MapLink.STAKEHOLDERS, permitterID);
    }

    public Integer getPermitterID() {
        return permitterID;
    }

    @Override
    public Integer getRelationID(Relation relation) {
        if (relation == Relation.PERMITTER) {
            return getPermitterID();
        }
        return Item.NONE;
    }

    @Override
    public boolean isLogicUpdated() {
        return excelUpdated;
    }

    /**
     * Check if the category contains functions that require a permit to be build.
     * @param category
     * @return
     */
    public boolean isZoningPermitRequired(Category category) {

        for (Function function : this.<Function> getMap(MapLink.FUNCTIONS).values()) {
            if (function.getCategories().contains(category) && function.isZoningPermitRequired()) {
                return true;
            }
        }
        return false;
    }

    public void setExcelID(Integer excelID) {
        this.excelID = excelID;
        this.setLogicUpdated(true);
    }

    @Override
    public void setLogicUpdated(boolean excelUpdated) {
        this.excelUpdated = excelUpdated;
    }

    public void setOutline(Outline outline) {
        this.outline = outline;
    }

    public void setParentZoneID(Integer parentZoneID) {
        this.parentZoneID = parentZoneID;
    }

    public void setPermitterID(Integer stakeholderID) {
        this.permitterID = stakeholderID;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
