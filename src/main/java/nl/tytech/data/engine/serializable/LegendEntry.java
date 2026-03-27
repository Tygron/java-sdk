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
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Entry on an Overlay's legend
 *
 * @author Maxim Knepfle
 */
public class LegendEntry implements Serializable, Comparable<LegendEntry> {

    public static final int MAX_ENTRIES = 21; // LegendPanelController capacity is 7 * 3

    private static final long serialVersionUID = -3270236333331543401L;

    @XMLValue
    private TColor color = TColor.BLACK;

    @XMLValue
    private String entryName = StringUtils.EMPTY;

    @XMLValue
    private float value = Float.NEGATIVE_INFINITY;

    @XMLValue
    private Integer id = Item.NONE;

    public LegendEntry() {

    }

    public LegendEntry(String entryName, TColor color) {
        this.entryName = entryName;
        this.color = color;
    }

    public LegendEntry(String entryName, TColor color, double value) {
        this(entryName, color);
        this.value = (float) value;
    }

    @Override
    public int compareTo(LegendEntry o) {
        int sign = Double.compare(value, o.value);
        if (sign == 0) {
            sign = id.compareTo(o.id);
        }
        if (sign == 0) {
            sign = entryName.compareTo(o.entryName);
        }
        return sign;
    }

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }
        if (object != null && object instanceof LegendEntry entry) {
            return entry.getID().equals(this.getID()) && entry.getValue() == value && entry.getColor().equals(color)
                    && entry.getEntryName().equals(entryName);
        }
        return false;
    }

    public TColor getColor() {
        return this.color;
    }

    public String getEntryName() {
        return this.entryName;
    }

    public Integer getID() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public boolean hasValue() {
        return Float.NEGATIVE_INFINITY != value;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setEntryName(String newName) {
        this.entryName = newName;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
