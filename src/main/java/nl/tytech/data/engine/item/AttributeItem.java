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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.other.AttributeQueryInterface;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Base abstract Item that has attributes
 *
 * @author Maxim Knepfle
 */
public abstract class AttributeItem extends UniqueNamedItem implements AttributeQueryInterface {

    public enum BaseAttribute implements ReservedAttribute {

        COLOR(TColor.class, TColor.WHITE.getARGB());

        private final Class<?> type;
        private final double[] defaultArray;

        private BaseAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
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

    public interface ReservedAttribute {

        public double[] defaultArray();

        public double defaultValue();

        public Class<?> getType();

        // public String getUnit();

        public String name();
    }

    private static final long serialVersionUID = 1362611699406604467L;

    private static final Set<String> EMPTY_SET = new HashSet<>();

    public static final double NO_DATA = GridOverlay.NO_DATA;
    public static final double[] NO_DATA_ARRAY = new double[] { NO_DATA };

    public static final double DEFAULT_VALUE = 0.0;
    public static final double[] EMPTY = new double[] {};
    public static final double[] ZERO = new double[] { 0.0 };
    public static final double[] ONE = new double[] { 1.0 };

    @XMLValue
    private TreeMap<String, double[]> attributes = null;

    @XMLValue
    private TreeMap<String, double[]> maquetteOverride = null;

    public AttributeItem() {
    }

    public final double getAttribute(MapType mapType, ReservedAttribute type) {
        return getAttribute(mapType, type.name());
    }

    @Override
    public final double getAttribute(MapType mapType, String key) {
        return getAttribute(mapType, key, 0);
    }

    @Override
    public double getAttribute(MapType mapType, String key, int index) {

        double[] array = getAttributeArray(mapType, key);
        return index >= 0 && array.length > index ? array[index] : DEFAULT_VALUE;
    }

    public final double getAttribute(ReservedAttribute key) {
        return this.getAttribute(key.name());
    }

    public final double getAttribute(ReservedAttribute key, int index) {
        return this.getAttribute(getDefaultMap(), key.name(), index);
    }

    @Override
    public final double getAttribute(String key) {
        return this.getAttribute(getDefaultMap(), key);
    }

    public final double[] getAttributeArray(MapType mapType, ReservedAttribute type) {
        return this.getAttributeArray(mapType, type.name());
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {

        if (mapType == MapType.MAQUETTE && maquetteOverride != null) {
            double[] array = key != null ? maquetteOverride.get(key) : null;
            if (array != null) {
                return array;
            }
        }
        if (attributes != null) {
            double[] array = key != null ? attributes.get(key) : null;
            if (array != null) {
                return array;
            }
        }
        return EMPTY;
    }

    public final double[] getAttributeArray(ReservedAttribute type) {
        return this.getAttributeArray(type.name());
    }

    @Override
    public final double[] getAttributeArray(String key) {
        return this.getAttributeArray(getDefaultMap(), key);
    }

    @Override
    public final Collection<String> getAttributes() {
        return getAttributes(getDefaultMap());
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {

        if (mapType == MapType.CURRENT) {
            return attributes == null ? EMPTY_SET : this.attributes.keySet();
        }

        // get both maquette and current
        Set<String> all = new HashSet<>();
        if (attributes != null) {
            all.addAll(attributes.keySet());
        }
        if (maquetteOverride != null) {
            all.addAll(maquetteOverride.keySet());
        }
        return all;
    }

    public final double getClampedAttribute(MapType mapType, String key, double min, double max) {
        return MathUtils.clamp(getAttribute(mapType, key, 0), min, max);
    }

    public final float getClampedAttribute(MapType mapType, String key, float min, float max) {
        return MathUtils.clamp((float) getAttribute(mapType, key, 0), min, max);
    }

    public double[] getClampedAttributeArray(MapType mapType, String key, double minMax) {
        return MathUtils.clamp(getAttributeArray(mapType, key), -minMax, minMax);
    }

    public double[] getClampedUnevenAttributeArray(MapType mapType, String key, double minMax) {
        double values[] = getAttributeArray(mapType, key);
        return values.length == 1 ? MathUtils.clamp(values, -minMax, minMax) : MathUtils.clampUneven(values, -minMax, minMax);
    }

    public TColor getColor() {
        return getColor(BaseAttribute.COLOR);
    }

    public final TColor getColor(ReservedAttribute attribute) {
        return getColor(attribute.name());
    }

    public final TColor getColor(String attribute) {
        return TColor.array(getAttributeArray(attribute));
    }

    public double[] getDefaultArray(String attribute) {
        // default item specific attributes
        ReservedAttribute ra = getReservedAttribute(attribute);
        return ra != null ? ra.defaultArray() : null;
    }

    protected abstract ReservedAttribute[] getDefaultAttributes();

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {

        Map<String, Object> map = super.getExportAttributes(inherited);
        for (String attributeName : this.getAttributes()) {
            double[] array = this.getAttributeArray(attributeName);
            map.putIfAbsent(attributeName, array.length == 1 ? array[0] : array);
        }
        return map;
    }

    /**
     * Return maquette override attributes, NOTE: may return NULL when emtpy
     */
    public Collection<String> getMaquetteAttributes() {
        return maquetteOverride == null ? null : maquetteOverride.keySet();
    }

    public double getOrDefault(ReservedAttribute attribute) {
        return getOrDefaultArray(attribute)[0];
    }

    public double[] getOrDefaultArray(MapType mapType, ReservedAttribute attribute) {
        return hasAttribute(mapType, attribute) ? getAttributeArray(mapType, attribute) : attribute.defaultArray();
    }

    public double[] getOrDefaultArray(ReservedAttribute attribute) {
        return getOrDefaultArray(getDefaultMap(), attribute);
    }

    /**
     * Return attribute of related item, if relation does not exist return NULL
     */
    @Override
    public final AttributeQueryInterface getRelationAttribute(Relation relation) {

        Integer linkID = this.getRelationID(relation);
        Item item = this.getItem(relation.getMapLink(), linkID);
        return item instanceof AttributeQueryInterface aqi ? aqi : null;
    }

    /**
     * Override with item specifics
     * @param relation
     * @return
     */
    public Integer getRelationID(Relation relation) {
        return Item.NONE;
    }

    /**
     * Return name of related item, if relation does not exist return ""
     */

    public final String getRelationName(Relation relation) {

        Integer linkID = this.getRelationID(relation);
        Item item = this.getItem(relation.getMapLink(), linkID);
        if (item instanceof UniqueNamedItem ui) {
            return ui.getName();
        }
        return StringUtils.EMPTY;
    }

    public ReservedAttribute getReservedAttribute(String attribute) {
        for (ReservedAttribute ra : getDefaultAttributes()) {
            if (ra.name().equals(attribute)) {
                return ra;
            }
        }
        // default item generic attributes
        for (ReservedAttribute ra : BaseAttribute.values()) {
            if (ra.name().equals(attribute)) {
                return ra;
            }
        }
        return null;
    }

    public final boolean hasAttribute(MapType mapType, ReservedAttribute type) {
        return hasAttribute(mapType, type.name());
    }

    @Override
    public boolean hasAttribute(MapType mapType, String key) {

        if (mapType == MapType.MAQUETTE && maquetteOverride != null && key != null && maquetteOverride.containsKey(key)) {
            return true;
        }
        return attributes != null && key != null && attributes.containsKey(key);
    }

    public final boolean hasAttribute(ReservedAttribute type) {
        return hasAttribute(type.name());
    }

    @Override
    public final boolean hasAttribute(String attribute) {
        return hasAttribute(getDefaultMap(), attribute);
    }

    public boolean isAttributeInherited(MapType mapType, String attribute) {
        return false;
    }

    public boolean isAttributeInherited(String attribute) {
        return isAttributeInherited(getDefaultMap(), attribute);
    }

    public boolean isAttributeOverride(String attribute, double[] values) {

        double[] defaultArray = getDefaultArray(attribute);
        if (defaultArray != null) {
            return !Arrays.equals(defaultArray, values);
        }

        return hasAttribute(attribute); // custom user attributes
    }

    public boolean isAttributeRemovable(String attribute) {
        if (isReservedAttribute(attribute)) {
            return false;
        }
        return hasAttribute(attribute) && !isAttributeInherited(attribute);
    }

    public boolean isAttributeResettable(MapType mapType, String attribute) {
        double[] defaultArray = getDefaultArray(attribute);
        if (defaultArray == null) {
            return false;
        }

        return !Arrays.equals(defaultArray, getAttributeArray(mapType, attribute));

    }

    public final boolean isAttributeResettable(String attribute) {
        return this.isAttributeResettable(getDefaultMap(), attribute);
    }

    public boolean isReservedAttribute(String attribute) {
        return getReservedAttribute(attribute) != null;
    }

    public final void removeAllAttributes() {
        this.attributes = null;
        this.maquetteOverride = null;
    }

    public final boolean removeAttribute(ReservedAttribute attribute) {
        return removeAttribute(attribute.name());
    }

    public final boolean removeAttribute(String key) {
        return removeAttribute(key, false);
    }

    public final boolean removeAttribute(String key, boolean allowMaqRemoval) {

        if (maquetteOverride != null) {
            if (!allowMaqRemoval) {
                TLogger.severe("Removing Attribute: " + key + " from Maquette in Item: " + this.toString() + ", should not be possible!");
            }
            return key != null && this.maquetteOverride.remove(key) != null;
        }
        return attributes != null && key != null && this.attributes.remove(key) != null;
    }

    public final void setAttribute(ReservedAttribute type, double value) {
        setAttribute(type.name(), value);
    }

    public final void setAttribute(String key, double value) {
        setAttributeArray(key, new double[] { value });
    }

    public final void setAttributeArray(MapType mapType, String key, double[] values) {

        if (key == null) {
            return;
        }

        if (mapType == MapType.MAQUETTE) {
            if (this.maquetteOverride == null) {
                this.maquetteOverride = new TreeMap<>();
            }
            this.maquetteOverride.put(key, values);

        } else {
            if (this.attributes == null) {
                this.attributes = new TreeMap<>();
            }
            this.attributes.put(key, values);
        }
    }

    public final void setAttributeArray(ReservedAttribute type, double[] values) {
        setAttributeArray(type.name(), values);
    }

    public final void setAttributeArray(String key, double[] values) {
        setAttributeArray(getDefaultMap(), key, values);
    }

    public final void setColor(TColor color) {
        this.setAttribute(BaseAttribute.COLOR, color.getARGB());
    }

    public boolean updateGeometry(String attribute) {
        return false;
    }
}
