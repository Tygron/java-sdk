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
package nl.tytech.util;

import java.lang.reflect.Array;
import nl.tytech.data.core.other.IndexedEnum;

/**
 *
 * @author Frank Baars
 *
 */
public class EnumUtils {

    private static final double MATCH_THRESHOLD = 0.1;

    public static final boolean contains(double[] array, double value, double threshold) {
        for (int i = 0; i < array.length; i++) {
            if (Math.abs(array[i] - value) < threshold) {
                return true;
            }
        }
        return false;
    }

    public static final <E extends Enum<E> & IndexedEnum> boolean contains(double[] array, E enhum) {
        return contains(array, enhum.getValue(), MATCH_THRESHOLD);
    }

    public static final <E extends Enum<E>> boolean contains(E[] enhumValues, E search) {

        if (search == null || enhumValues == null) {
            return false;
        }
        for (int i = 0; i < enhumValues.length; i++) {
            if (search.equals(enhumValues[i])) {
                return true;
            }
        }
        return false;
    }

    public static final <E extends Enum<E>> boolean containsAny(E[] enhumValues, E[] search) {

        if (search == null || enhumValues == null) {
            return false;
        }
        for (int i = 0; i < enhumValues.length; i++) {
            for (int j = 0; j < search.length; j++) {
                if (search[j] != null && search[j].equals(enhumValues[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final <T> T get(Class<T> classz, String name) {

        if (classz == null || !classz.isEnum()) {
            throw new IllegalArgumentException(classz + " is not an Enum.");
        }
        for (T enhum : classz.getEnumConstants()) {
            if (((Enum<?>) enhum).name().equals(name)) {
                return enhum;
            }
        }
        throw new IllegalArgumentException("No enum constant " + classz.getCanonicalName() + "." + name);
    }

    public static final <E extends Enum<E> & IndexedEnum> E get(E[] enhumValues, double value, E defaultResult) {

        for (E enhum : enhumValues) {
            if (Math.abs(enhum.getValue() - value) < MATCH_THRESHOLD) {
                return enhum;
            }
        }
        return defaultResult;
    }

    @SuppressWarnings("unchecked")
    public static final <E extends Enum<E> & IndexedEnum> E[] get(E[] enhumValues, double[] values, E defaultResult) {

        E[] result = (E[]) Array.newInstance(defaultResult.getClass(), values.length);
        for (int i = 0; i < values.length; i++) {
            result[i] = defaultResult;
            for (E enhum : enhumValues) {
                if (Math.abs(enhum.getValue() - values[i]) < MATCH_THRESHOLD) {
                    result[i] = enhum;
                }
            }
        }
        return result;
    }

    public static final <E extends Enum<E> & IndexedEnum> double[] get(E[] enhumValues, E defaultEnhum) {

        double[] array = new double[enhumValues.length];
        for (int i = 0; i < enhumValues.length; i++) {
            array[i] = enhumValues[i] == null ? defaultEnhum.getValue() : enhumValues[i].getValue();
        }
        return array;
    }
}
