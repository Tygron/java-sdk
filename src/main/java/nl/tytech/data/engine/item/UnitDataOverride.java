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

import java.util.ArrayList;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * UnitDataOverride: custom values for units.
 *
 * @author Frank Baars
 */
public class UnitDataOverride extends Item implements ActiveItem, NamedItem {

    private static final long serialVersionUID = 1089214173999556938L;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    @ListOfClass(TColor.class)
    private ArrayList<TColor> colors = null;

    @XMLValue
    private Boolean active = null;

    public UnitDataOverride() {

    }

    public void addColor(TColor color) {
        if (!hasColors()) {
            initColors();
        }
        colors.add(color);
    }

    public boolean changeColor(Integer index, TColor color) {
        if (!hasColors()) {
            initColors();
        }
        if (index < 0 || index >= colors.size() || colors.get(index).equals(color)) {
            return false;
        }
        colors.set(index, color);
        return true;
    }

    public ArrayList<TColor> getColors() {
        return colors;
    }

    @Override
    public String getName() {
        return name;
    }

    public TrafficType getTrafficType() {
        UnitData unit = this.getUnitData();
        return unit.getTrafficType();
    }

    public UnitData getUnitData() {
        return this.getItem(MapLink.UNIT_DATAS, this.getUnitDataID());
    }

    public Integer getUnitDataID() {
        return this.getID();
    }

    public boolean hasActive() {
        return active != null;
    }

    public boolean hasColors() {
        return colors != null && colors.size() > 0;
    }

    private void initColors() {
        if (!hasColors()) {
            UnitData unitData = getUnitData();
            if (unitData != null) {
                colors = new ArrayList<>(unitData.getColors());
            } else {
                colors = new ArrayList<>();
            }
        }
    }

    @Override
    public boolean isActive() {
        return active != null ? active.booleanValue() : getUnitData().isActive();
    }

    public boolean removeColor(Integer unitDataID, int index, TColor color) {
        if (!hasColors()) {
            initColors();
        }
        if (index < 0 || index >= colors.size() || !colors.get(index).equals(color)) {
            return false;
        }
        if (colors.get(index).equals(color)) {
            colors.remove(index);
            return true;
        }

        colors.remove(color);
        return true;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (StringUtils.containsData(name)) {
            return name;
        }
        UnitData unitData = this.getUnitData();
        if (unitData != null && StringUtils.containsData(unitData.getName())) {
            return unitData.getName();
        }
        return UnitDataOverride.class.getSimpleName() + StringUtils.WHITESPACE + getID();
    }
}
