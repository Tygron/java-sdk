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

import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.other.ExcelItem;
import nl.tytech.data.engine.other.TextItem;

/**
 * @author Frank Baars
 */
public class ExcelPanel extends LogicPanel implements ExcelItem, TextItem {

    private static final long serialVersionUID = 2805451253961720700L;

    @XMLValue
    @ItemIDField(MapLink.EXCEL_SHEETS)
    private Integer excelID = 1; // default excelpanel sheet

    @XMLValue
    private MultiPolygon polygons = null;

    public ExcelPanel() {
        this(PanelType.EXCEL_PANEL);
    }

    protected ExcelPanel(PanelType type) {
        super(type);
    }

    @Override
    public Integer getExcelID() {
        return excelID;
    }

    @Override
    public ExcelSheet getExcelSheet() {
        return getItem(MapLink.EXCEL_SHEETS, getExcelID());
    }

    public MultiPolygon getPolygons() {
        return polygons;
    }

    @Override
    public boolean hasStyle() {
        return false;
    }

    public void setExcelID(Integer excelID) {
        this.excelID = excelID;
        this.setLogicUpdated(true);
    }

    public void setPolygons(MultiPolygon polygons) {
        this.polygons = polygons;
    }

    public void setText(String text) {
        this.text = text;
    }
}
