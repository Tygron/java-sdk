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
import java.util.Collection;
import java.util.TreeMap;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.AttributeItem;
import nl.tytech.data.engine.item.AttributeItem.ReservedAttribute;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.ParametricDesign;
import nl.tytech.data.engine.other.AttributeQueryInterface;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
public class PlotDesign implements Serializable, AttributeQueryInterface {

    public enum PlotDesignAttribute implements ReservedAttribute {

        AREA_M2(Double.class, 100),

        WIDTH_M(Double.class, 0),

        SIDE_DISTANCE_M(Double.class, 2),

        ROAD_DISTANCE_M(Double.class, 3),

        UNITS(Integer.class, 1),

        FLOORS(Double.class, 1),

        FLOOR_HEIGHT_M(Double.class, 10),

        FIT_FRACTION(Double.class, 0.9),

        FRACTION(Double.class, 1)

        ;

        private final Class<?> type;

        private final double[] defaultArray;

        private final TColor color;

        private PlotDesignAttribute(Class<?> type, double defaultValue) {
            this(type, defaultValue, TColor.WHITE);
        }

        private PlotDesignAttribute(Class<?> type, double defaultValue, TColor color) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
            this.color = color;
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        public TColor getColor() {
            return color;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        public boolean isFraction() {
            return this == FRACTION;
        }

    }

    private static final long serialVersionUID = 3143311956132326930L;

    public static final double MIN__AREA_M2 = 0.1;

    public static final double DEFAULT_FLOOR_AREA_M2 = 50;

    @XMLValue
    private MultiPolygon multiPolygon = JTSUtils.EMPTY;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer functionID = Item.NONE;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    private TreeMap<String, double[]> attributes = new TreeMap<>();

    public PlotDesign() {

    }

    public PlotDesign(Integer id, Integer functionID) {
        this.id = id;
        this.functionID = functionID;
    }

    @Override
    public double getAttribute(MapType mapType, String key) {
        return getAttribute(mapType, key, 0);
    }

    @Override
    public double getAttribute(MapType mapType, String key, int index) {
        double[] array = getAttributeArray(key);
        return index >= 0 && array.length > index ? array[index] : AttributeItem.DEFAULT_VALUE;
    }

    public double getAttribute(ReservedAttribute key) {
        return getAttribute(key.name());
    }

    @Override
    public double getAttribute(String key) {
        return getAttribute(MapType.CURRENT, key, 0);
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {
        double[] array = key != null ? attributes.get(key) : null;
        return array != null ? array : AttributeItem.EMPTY;
    }

    public double[] getAttributeArray(ReservedAttribute key) {
        return getAttributeArray(key.name());
    }

    @Override
    public double[] getAttributeArray(String key) {
        return getAttributeArray(MapType.CURRENT, key);
    }

    @Override
    public Collection<String> getAttributes() {
        return this.attributes.keySet();
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {
        return getAttributes();
    }

    public Integer getFunctionID() {
        return functionID;
    }

    public Integer getID() {
        return id;
    }

    public String getName() {
        return PlotDesign.class.getSimpleName() + " " + id;
    }

    public double getOrDefault(ReservedAttribute attribute) {
        return getOrDefaultArray(attribute)[0];
    }

    public double[] getOrDefaultArray(MapType mapType, ReservedAttribute attribute) {
        return hasAttribute(mapType, attribute.name()) ? getAttributeArray(mapType, attribute.name()) : attribute.defaultArray();
    }

    public double[] getOrDefaultArray(ReservedAttribute attribute) {
        return getOrDefaultArray(MapType.CURRENT, attribute);
    }

    public double getPlotDepth() {

        double width = getOrDefault(PlotDesignAttribute.WIDTH_M);
        if (width > 0) {
            return getOrDefault(PlotDesignAttribute.AREA_M2) / width;
        } else {
            return Math.sqrt(Math.max(0, getOrDefault(PlotDesignAttribute.AREA_M2)));
        }
    }

    public double getPlotWidth() {

        double width = getOrDefault(PlotDesignAttribute.WIDTH_M);
        if (width > 0) {
            return width;
        } else {
            return Math.sqrt(Math.max(0, getOrDefault(PlotDesignAttribute.AREA_M2)));
        }
    }

    @Override
    public AttributeQueryInterface getRelationAttribute(Relation relation) {
        return null;
    }

    public double getUnitM2() {
        return getOrDefault(PlotDesignAttribute.UNITS)
                / MathUtils.clamp(getOrDefault(PlotDesignAttribute.AREA_M2), MIN__AREA_M2, Double.MAX_VALUE);
    }

    public int getValidFloors(ParametricDesign parent) {

        Function f = parent.getPlotFuction(this);
        if (f == null) {
            return (int) FunctionValue.DEFAULT_FLOORS.defaultValue();
        }

        int floors = (int) getOrDefault(PlotDesignAttribute.FLOORS);
        int min = f != null ? f.getMinFloorsFunction() : (int) FunctionValue.MIN_FLOORS.defaultValue();
        int max = f != null ? f.getMaxFloorsFunction() : (int) FunctionValue.MAX_FLOORS.defaultValue();
        if (floors <= 0) { // do it random
            int range = max - min;
            floors = (int) (Math.random() * range) + min;
        }
        return MathUtils.clamp(floors, min, max);
    }

    @Override
    public boolean hasAttribute(MapType mapType, String attribute) {
        return hasAttribute(attribute);
    }

    @Override
    public boolean hasAttribute(String key) {
        return key != null && !attributes.isEmpty() && attributes.containsKey(key);
    }

    public boolean removeAttribute(String aatribute) {
        return attributes.remove(aatribute) != null;
    }

    public final void setAttribute(ReservedAttribute type, double value) {
        setAttribute(type.name(), value);
    }

    public final void setAttribute(String key, double value) {
        setAttributeArray(key, new double[] { value });
    }

    public final void setAttributeArray(MapType mapType, String key, double[] values) {

        if (key != null) {
            this.attributes.put(key, values);
        }
    }

    public final void setAttributeArray(ReservedAttribute type, double[] values) {
        setAttributeArray(type.name(), values);
    }

    public final void setAttributeArray(String key, double[] values) {
        setAttributeArray(MapType.CURRENT, key, values);
    }

    public boolean setFunctionID(Integer functionID) {

        if (!this.functionID.equals(functionID)) {
            this.functionID = functionID;
            return true;
        }
        return false;
    }
}
