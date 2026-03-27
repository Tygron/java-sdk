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

import java.io.Serializable;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Area overlay with custom colors
 *
 * @author Frank Baars
 */
public class CustomColorArea implements Serializable {

    private static final long serialVersionUID = -6859295119584860624L;

    @XMLValue
    private Integer zoneID = Item.NONE;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private TColor color = TColor.BLUE;

    @XMLValue
    protected MultiPolygon polygons = JTSUtils.EMPTY;

    public CustomColorArea() {

    }

    public CustomColorArea(Integer zoneID, String name, TColor color) {
        this.zoneID = zoneID;
        this.name = name;
        this.color = color;
    }

    public TColor getColor() {
        return color;
    }

    public Integer getID() {
        return zoneID;
    }

    public MultiPolygon getMultiPolygon() {
        return polygons;
    }

    public String getName() {
        return name;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.polygons = multiPolygon;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " (id=" + zoneID + ") " + color;
    }

}
