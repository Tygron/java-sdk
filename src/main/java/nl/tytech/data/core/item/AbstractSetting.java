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
package nl.tytech.data.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.Crs;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Setting: This Item can be used to save settings. These setting can differ in type and thus are all stored as Strings.
 *
 * @author Maxim Knepfle
 */
public abstract class AbstractSetting<E extends Enum<E>> extends EnumOrderedItem<E> {

    public static final long serialVersionUID = 5772008764384172107L;

    @XMLValue
    private String value = StringUtils.EMPTY;

    public AbstractSetting() {

    }

    public final boolean getBooleanValue() {

        try {
            return Boolean.parseBoolean(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return false;
        }
    }

    public final TColor getColorValue() {

        try {
            if (StringUtils.containsData(getValue())) {
                int[] data = this.getIntArrayValue();
                if (data.length == 1) {
                    return new TColor(data[0]);
                } else if (data.length == 3) {
                    return new TColor(data[0], data[1], data[2]);
                } else if (data.length >= 4) {
                    return new TColor(data[0], data[1], data[2], data[3]);
                }
            }
            return null;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final Crs getCrs() {
        return new Crs(this.getValue(), true);
    }

    public final double[] getDoubleArrayValue() {

        try {
            return StringUtils.splitDouble(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final double getDoubleValue() {

        try {
            return Double.parseDouble(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return -1;
        }
    }

    public final <F extends Enum<F>> Collection<F> getEnumCollection(Class<F> type) {

        try {
            final String[] split = StringUtils.split(getValue());
            final Set<F> set = new LinkedHashSet<>();
            for (int i = 0; i < split.length; i++) {
                try {
                    F item = Enum.valueOf(type, split[i]);
                    if (item != null) {
                        set.add(item);
                    }
                } catch (IllegalArgumentException e) {
                    TLogger.warning("Setting contains an enum op type [" + type.getSimpleName() + "] with value [" + split[i]
                            + "], but this value is not valid.");
                }
            }
            return set;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public <F extends Enum<F>> F getEnumValue(Class<F> type) {
        try {
            return Enum.valueOf(type, getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final int[] getIntArrayValue() {

        try {
            return StringUtils.splitInt(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final List<Integer> getIntegerListValue() {

        try {
            final String[] split = StringUtils.split(getValue());
            final List<Integer> list = new ArrayList<>(split.length);
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() > 0) {
                    list.add(Integer.valueOf(split[i]));
                }
            }
            return list;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final Integer getIntegerValue() {
        return this.getIntValue();
    }

    public final int getIntValue() {

        try {
            return Double.valueOf(getValue()).intValue();
        } catch (Exception exp) {
            TLogger.exception(exp);
            return NONE;
        }
    }

    public long[] getLongArrayValue() {
        try {
            final String[] split = StringUtils.split(getValue());
            final long[] array = new long[split.length];
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() > 0) {
                    array[i] = Long.parseLong(split[i]);
                }
            }
            return array;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final long getLongValue() {

        try {
            return Long.parseLong(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return NONE;
        }
    }

    public final MultiPolygon getMultiPolygon() {

        WKTReader wtkReader = new WKTReader();
        if (!StringUtils.containsData(getValue())) {
            return JTSUtils.EMPTY;
        }
        try {
            return (MultiPolygon) wtkReader.read(getValue());
        } catch (Exception e) {
            TLogger.exception(e);
            return JTSUtils.EMPTY;
        }
    }

    public final String[] getStringArrayValue() {

        try {
            return StringUtils.split(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final List<String> getStringListValue() {

        try {
            final String[] split = StringUtils.split(getValue());
            final List<String> list = new ArrayList<>(split.length);
            for (int i = 0; i < split.length; i++) {
                list.add(split[i]);
            }
            return list;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final TimeZone getTimeZone() {

        try {
            return TimeZone.getTimeZone(getValue());
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
    }

    public final String getValue() {
        return value != null ? value.trim() : null;
    }

    public final void setValue(final Boolean value) {
        this.value = String.valueOf(value);
    }

    public <T extends Enum<?>> void setValue(Collection<T> collection) {

        StringBuilder data = new StringBuilder();
        for (T e : collection) {
            data.append(e.name());
            data.append(StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final double[] value) {

        StringBuilder data = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            data.append(value[i]);
            data.append(StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final Enum<?> value) {
        this.value = value.name();
    }

    public final void setValue(final int[] value) {

        StringBuilder data = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            data.append(value[i]);
            data.append(StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final long[] value) {

        StringBuilder data = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            data.append(value[i]);
            data.append(StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final Number value) {
        this.value = String.valueOf(value);
    }

    public final void setValue(final Number[] value) {

        StringBuilder data = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            data.append(value[i]);
            data.append(StringUtils.WHITESPACE);
        }
        this.value = data.toString();
    }

    public final void setValue(final String value) {
        this.value = value;
    }
}
