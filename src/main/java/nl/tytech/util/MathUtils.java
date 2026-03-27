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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import nl.tytech.data.engine.item.Setting.Size;
import nl.tytech.data.engine.serializable.GridData;
import nl.tytech.data.engine.serializable.Vector3d;
import nl.tytech.util.color.TColor;

/**
 * Extension to default Math utils.
 *
 * @author Maxim Knepfle & Frank Baars & Alexander Hofstede
 */
public class MathUtils {

    private static final class RandomNumberGeneratorHolder {
        static final Random randomNumberGenerator = new Random();
    }

    public static final double TWO_PI = 2.0 * Math.PI;

    public static final double HALF_PI = Math.PI / 2.0;

    public static final long KM = 1000l;

    public static final long KM2 = KM * KM;

    public static final double[] add(double[] a, double[] b) {

        if (a == null || b == null) {
            return null;
        }

        double[] r = new double[Math.max(a.length, b.length)];
        for (int i = 0; i < r.length; i++) {
            r[i] += i < a.length ? a[i] : 0.0;
            r[i] += i < b.length ? b[i] : 0.0;
        }
        return r;
    }

    public static final <N extends Number> double avg(Collection<N> values) {

        if (values == null || values.isEmpty()) {
            return 0;
        }
        double sum = sum(values);
        return sum / values.size();
    }

    public static final double avg(double... values) {

        if (values.length == 0) {
            return 0;
        }
        if (values.length == 1) {
            return values[0];
        }
        double sum = sum(values);
        return sum / values.length;
    }

    public static final float avg(float... values) {

        if (values.length == 0) {
            return 0;
        }
        double sum = sum(values);
        return (float) (sum / values.length);
    }

    public static final float avg(float[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);
        if (dimX == 0 || dimY == 0) {
            return 0f;
        }

        double total = 0.0;
        for (int y = 0; y < dimY; y++) {
            total += sum(matrix[y]);
        }
        return (float) (total / (dimX * dimY));
    }

    public static final Vector3d avg(Vector3d[] array) {

        if (array == null || array.length == 0) {
            return new Vector3d();
        }

        double x = 0;
        double y = 0;
        double z = 0;
        for (Vector3d vec : array) {
            x += vec.x;
            y += vec.y;
            z += vec.z;
        }

        return new Vector3d(x / array.length, y / array.length, z / array.length);
    }

    public static final double clamp(double value, double minMax) {
        return value < -minMax ? -minMax : value > minMax ? minMax : value;
    }

    /**
     * Value is clamped between the min and max values (inclusive)
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final double clamp(double value, double min, double max) {
        return value < min ? min : value > max ? max : value;
    }

    /**
     * Clamp array values and return orginal or new array when needed.
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final double[] clamp(double[] values, double min, double max) {

        // check if array needs clamping
        boolean allValid = true;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min || values[i] > max) {
                allValid = false;
                break;
            }
        }

        // no clamp change required
        if (allValid) {
            return values;
        }

        // clamp to new array
        final double[] clamped = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            clamped[i] = clamp(values[i], min, max);
        }
        return clamped;
    }

    /**
     * Value is clamped between the min and max values (inclusive)
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final float clamp(float value, float minMax) {
        return value < -minMax ? -minMax : value > minMax ? minMax : value;
    }

    /**
     * Value is clamped between the min and max values (inclusive)
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final float clamp(float value, float min, float max) {
        return value < min ? min : value > max ? max : value;
    }

    /**
     * Value is clamped between the min and max values (inclusive)
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final int clamp(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    /**
     * Value is clamped between the min and max values (inclusive)
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final long clamp(long value, long min, long max) {
        return value < min ? min : value > max ? max : value;
    }

    /**
     * Clamps between 0 and 1, use full for fractions.
     */
    public static final float clampFraction(float value) {
        return clamp(value, 0f, 1f);
    }

    /**
     * Value is clamped between the min and max values (inclusive)
     *
     * Note: when max is smaller then min: min has preference, this different from Math.clamp() where and exception is thrown.
     */
    public static final int clampInt(long value, long min, long max) {
        return (int) clamp(value, min, max);
    }

    /**
     * Clamp arrays uneven values and return orginal or new array when needed.
     */
    public static final double[] clampUneven(final double[] values, final double min, final double max) {

        // check if array needs clamping
        boolean allValid = true;
        for (int i = 1; i < values.length; i += 2) {
            if (values[i] < min || values[i] > max) {
                allValid = false;
                break;
            }
        }

        // no clamp change required
        if (allValid) {
            return values;
        }

        // clamp to new array
        final double[] clamped = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            clamped[i] = i % 2 == 0 ? values[i] : clamp(values[i], min, max);
        }
        return clamped;
    }

    public static final boolean contains(float[][] matrix, float value) {

        int height = MathUtils.getDimY(matrix);
        int width = MathUtils.getDimX(matrix);

        for (int y = 0; y < height; y++) {
            float[] line = matrix[y];
            for (int x = 0; x < width; x++) {
                if (line[x] == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final boolean containsData(double[] array) {
        return array != null && array.length > 0;
    }

    public static final boolean equals(double[] a, double[] b, double threshold) {

        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        int length = a.length;
        if (b.length != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (Double.isFinite(a[i]) && Double.isFinite(a[i])) {
                if (Math.abs(a[i] - b[i]) >= threshold) {
                    return false;
                }
            } else if (Double.doubleToLongBits(a[i]) != Double.doubleToLongBits(b[i])) {
                return false;
            }
        }
        return true;
    }

    public static final boolean equals(List<Integer> a, Integer[] b) {

        if (a.size() != b.length) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).intValue() != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * True when all values in the array equal the given value
     */
    public static final boolean equalsAll(double[] array, double value) {

        for (double v : array) {
            if (v != value) {
                return false;
            }
        }
        return true;
    }

    /**
     * True when all values in the array equal the given value
     */
    public static final boolean equalsAll(float[] array, float value) {

        for (float v : array) {
            if (v != value) {
                return false;
            }
        }
        return true;
    }

    /**
     * True when all uneven values in the array equal the given value
     */
    public static final boolean equalsAllUneven(double[] array, double value) {

        for (int i = 1; i < array.length; i += 2) {
            if (array[i] != value) {
                return false;
            }
        }
        return true;
    }

    // fractional error in math formula less than 1.2 * 10 ^ -7.
    // although subject to catastrophic cancellation when z in very close to 0
    // from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
    // From: https://introcs.cs.princeton.edu/java/21function/ErrorFunction.java.html
    public static final double erf(double z) {

        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
        // use Horner's method
        double ans = 1 - t * Math.exp(-z * z - 1.26551223 + t * (1.00002368 + t * (0.37409196 + t * (0.09678418
                + t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 + t * 0.17087277)))))))));
        return z >= 0 ? ans : -ans;
    }

    // fractional error less than x.xx * 10 ^ -4.
    // Algorithm 26.2.17 in Abromowitz and Stegun, Handbook of Mathematical.
    // From: https://introcs.cs.princeton.edu/java/21function/ErrorFunction.java.html
    public static final double erf2(double z) {

        double t = 1.0 / (1.0 + 0.47047 * Math.abs(z));
        double poly = t * (0.3480242 + t * (-0.0958798 + t * 0.7478556));
        double ans = 1.0 - poly * Math.exp(-z * z);
        return z >= 0 ? ans : -ans;
    }

    public static final double erfc(double z) {
        return 1 - erf(z);
    }

    /**
     * Fast but less accurate impl of sqrt
     *
     * Source: http://www.lomont.org/Math/Papers/2003/InvSqrt.pdf
     */
    public static final float fastSqrt(float x) {

        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f375a86 - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= 1.5f - xhalf * x * x;
        return 1.0f / x;
    }

    public static final void fill(float[] array, float value) {
        Arrays.fill(array, value);
    }

    public static final void fill(float[][] matrix, float value) {

        for (int y = 0, len = matrix.length; y < len; y++) {
            Arrays.fill(matrix[y], value);
        }
    }

    /**
     * Get area of matrix, dimX x dimY, Note: dimX can be variable
     */
    public static final long getDimArea(float[][] matrix) {

        long count = 0;
        for (int r = 0; r < matrix.length; r++) {
            count = matrix[r] != null ? count + matrix[r].length : count;
        }
        return count;
    }

    /**
     * X == length of first row in matrix
     */
    public static final int getDimX(Object matrix) {

        if (getDimY(matrix) == 0) {
            return 0;
        }
        Object firstRow = Array.get(matrix, 0);
        return firstRow != null ? Array.getLength(firstRow) : 0;
    }

    /**
     * Y == matrix length/column
     */
    public static final int getDimY(Object matrix) {
        return matrix != null ? Array.getLength(matrix) : 0;
    }

    public static final Integer getInteger(String text) {
        return getInteger(text, null);
    }

    public static final Integer getInteger(String text, Integer defaultValue) {

        try {
            return Integer.valueOf(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static final boolean hasDecimals(double value) {
        return Math.ceil(value) - value > 0d;
    }

    /**
     * Returns the index of the largest value in the array, or the first when all are the same or array is empty
     * @param array
     * @return
     */
    public static final int indexOfMax(float[] array) {

        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }

    public static final <T extends Comparable<T>> int indexOfMax(List<T> list) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).compareTo(max) > 0) {
                    max = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T> int indexOfMax(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (comparator.compare(list.get(i), max) > 0) {
                    max = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T extends Comparable<T>> int indexOfMax(T[] tArray) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (tArray[i].compareTo(max) > 0) {
                    max = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T> int indexOfMax(T[] tArray, Comparator<T> comparator) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T max = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (comparator.compare(tArray[i], max) > 0) {
                    max = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T extends Comparable<T>> int indexOfMin(List<T> list) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).compareTo(min) < 0) {
                    min = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T> int indexOfMin(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = list.get(index);
            for (int i = 0; i < list.size(); ++i) {
                if (comparator.compare(list.get(i), min) < 0) {
                    min = list.get(i);
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T extends Comparable<T>> int indexOfMin(T[] tArray) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (tArray[i].compareTo(min) < 0) {
                    min = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static final <T> int indexOfMin(T[] tArray, Comparator<T> comparator) {
        if (tArray.length <= 0) {
            return -1;
        } else {
            int index = 0;
            T min = tArray[index];
            for (int i = 0; i < tArray.length; ++i) {
                if (comparator.compare(tArray[i], min) < 0) {
                    min = tArray[i];
                    index = i;
                }
            }
            return index;
        }
    }

    public static final boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * checks if a value is in the range min..max, min+max are included
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static final boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static final double interpolate(double x1, double x2, double inbetween) {
        return x1 + (x2 - x1) * inbetween;
    }

    public static final float interpolate(float x1, float x2, float inbetween) {
        return x1 + (x2 - x1) * inbetween;
    }

    public static final int interpolate(int x1, int x2, double inbetween) {
        return (int) (x1 + (x2 - x1) * inbetween);
    }

    public static final TColor interpolateRGB(TColor s, TColor d, double inbetween) {
        return new TColor((int) (s.getRed() * inbetween + d.getRed() * (1 - inbetween)), //
                (int) (s.getGreen() * inbetween + d.getGreen() * (1 - inbetween)), //
                (int) (s.getBlue() * inbetween + d.getBlue() * (1 - inbetween)));//
    }

    // RGB because we can also interpolate in hsv or any other color space
    public static final TColor interpolateRGB(TColor s, TColor d, float inbetween) {
        return interpolateRGB(s, d, (double) inbetween);
    }

    public static final boolean isDimension(GridData matrix, int width, int height, int blockSize) {
        return matrix != null && matrix.getWidth() == width && matrix.getHeight() == height && matrix.getBlockSize() == blockSize;
    }

    public static final boolean isDimension(Object matrix, int x, int y) {
        return matrix != null && getDimX(matrix) == x && getDimY(matrix) == y;
    }

    /**
     * returns true if Double.valueOf does not throw a NumberFormatException
     *
     * @param d
     * @return
     */
    public static final boolean isDouble(String d) {
        try {
            Double.valueOf(d);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * returns true if Integer.valueOf does not throw a NumberFormatException
     *
     * @param d
     * @return
     */
    public static final boolean isInteger(String d) {
        try {
            Integer.valueOf(d);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Uses System.arraycopy() to copy Matrix
     */
    public static final void matrixCopy(byte[][] src, byte[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, dest[i].length);
        }
    }

    /**
     * Uses System.arraycopy() to copy Matrix
     */
    public static final void matrixCopy(float[][] src, float[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, dest[i].length);
        }
    }

    /**
     * Uses System.arraycopy() to copy Matrix
     */
    public static final void matrixCopy(int[][] src, int[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, dest[i].length);
        }
    }

    /**
     * Uses System.arraycopy() to copy Matrix
     */
    public static final void matrixCopy(long[][] src, long[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, dest[i].length);
        }
    }

    public static final double max(double[] array) {

        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static final float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    public static final float max(float a, float b, float c, float d) {
        return Math.max(max(a, b, c), d);
    }

    public static final float max(float a, float b, float c, float d, float e) {
        return Math.max(max(a, b, c, d), e);
    }

    public static final float max(float[] array) {

        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static final float max(float[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        float max = Float.NEGATIVE_INFINITY;
        for (int y = 0; y < dimY; y++) {
            float[] line = matrix[y];
            for (int x = 0; x < dimX; x++) {
                if (line[x] > max) {
                    max = line[x];
                }
            }
        }
        return max;
    }

    /**
     * Returns true when the max distance between values in the array is NOT larger then the given value
     * @param array
     * @param maxDistance
     * @return
     */
    public static final boolean maxDistance(byte[] array, int maxDistance) {

        if (array == null || array.length == 0) {
            return true;
        }

        int min = array[0];
        int max = array[0];
        for (byte value : array) {
            min = Math.min(value, min);
            max = Math.max(value, max);
            if (Math.abs(min - max) > maxDistance) {
                return false;
            }
        }
        return true;
    }

    public static final double min(double[] array) {

        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static final double[] min(double[] a, double[] b) {

        if (a == null || b == null) {
            return null;
        }

        double[] r = new double[Math.max(a.length, b.length)];
        for (int i = 0; i < r.length; i++) {
            if (i < a.length && i < b.length) {
                r[i] = Math.min(a[i], b[i]);
            } else if (i < a.length) {
                r[i] = a[i];
            } else if (i < b.length) {
                r[i] = b[i];
            }
        }
        return r;
    }

    public static final float min(float a, float b, float c, float d) {
        return Math.min(Math.min(Math.min(a, b), c), d);
    }

    public static final float min(float a, float b, float c, float d, float e) {
        return Math.min(min(a, b, c, d), e);
    }

    public static final float min(float[] array) {

        float min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static final float min(float[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        float min = Float.POSITIVE_INFINITY;
        for (int y = 0; y < dimY; y++) {
            float[] line = matrix[y];
            for (int x = 0; x < dimX; x++) {
                if (line[x] < min) {
                    min = line[x];
                }
            }
        }
        return min;
    }

    /**
     * Multiply all uneven values in the given array with the multiplier.
     * @param array
     * @param multiplier
     * @return Cloned version of the array. Not same object!
     */
    public static final double[] multiplyUnevenValues(double[] array, double multiplier, double min, double max) {

        double[] newArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = i % 2 == 0 ? array[i] : MathUtils.clamp(array[i] * multiplier, min, max);
        }
        return newArray;
    }

    /**
     * Normalizes numbers in a collection.
     *
     * @param c the collection you want the sum from
     */
    public static final void normalize(ArrayList<Double> c) {

        double sum = sum(c);
        for (int i = 0; i < c.size(); i++) {
            c.set(i, c.get(i) / sum);
        }
    }

    /**
     * Normalize. This forces sum of fractions to become 1
     */
    public static final void normalize(double[] array) {

        double sum = sum(array);
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] / sum;
        }
    }

    /**
     * Normalize and return as doubles. This forces sum of fractions to become 1
     */
    public static final double[] normalize(float[] array) {

        double sum = sum(array);
        double[] normalized = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            normalized[i] = sum > 0.0 ? array[i] / sum : 1.0 / array.length;
        }
        return normalized;
    }

    /**
     * Calculate linear interpolated k-th percentile [0-1] of the array
     */
    public static final double percentile(double[] array, double k) {

        // sort and get index
        Arrays.sort(array);
        double index = MathUtils.clamp(k, 0, 1) * (array.length - 1);

        // get before and after positions
        int p0 = (int) Math.floor(index);
        int p1 = (int) Math.ceil(index);

        // cannot interpolate
        if (p0 == p1) {
            return array[p0];
        }

        // interpolate between p0 and p1
        double v1 = array[p1] * (index - p0);
        double v2 = array[p0] * (p1 - index);
        return v1 + v2;
    }

    /**
     * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive)
     */
    public static final int randomInt(int bound) {
        return RandomNumberGeneratorHolder.randomNumberGenerator.nextInt(bound);
    }

    /**
     * Reduce timestamp map to specified size
     */
    public static final HashMap<Long, Double> reduceTimeMap(HashMap<Long, Double> map, int size) {

        if (map == null || map.size() < size) {
            return map; // already there
        }

        // create new map half the size
        HashMap<Long, Double> halfMap = new HashMap<>();
        List<Long> list = new ArrayList<>(map.keySet());
        Collections.sort(list); // sort on time

        for (int i = 0; i < list.size(); i += 2) {
            long first = list.get(i);
            long second = list.get(Math.min(i + 1, list.size() - 1));
            long avgTime = (first + second) / 2l;
            double avgValue = (map.get(first) + map.get(second)) / 2d;
            halfMap.put(avgTime, avgValue);
        }

        if (halfMap.size() > size) { // not there try again
            return reduceTimeMap(halfMap, size);
        } else {
            return halfMap;
        }
    }

    public static final void replace(float[] array, float key, float replacement) {

        for (int x = 0, len = array.length; x < len; x++) {
            if (array[x] == key) {
                array[x] = replacement;
            }
        }
    }

    public static final void replace(float[][] matrix, float key, float replacement) {

        for (int y = 0, len = matrix.length; y < len; y++) {
            replace(matrix[y], key, replacement);
        }
    }

    public static final double[] resize(double[] array, int newLength) {

        if (array.length == newLength) {
            return array;
        }

        double step = array.length / (double) newLength;
        double[] newArray = new double[newLength];
        for (int i = 0; i < newLength; i++) {
            int start = (int) (i * step);
            int end = Math.min(array.length, Math.max(start + 1, (int) ((i + 1) * step)));
            int distance = Math.max(1, end - start);
            newArray[i] = sum(array, start, end) / distance;
        }
        return newArray;
    }

    /**
     *
     * @param values
     * @return the same array reversed
     */
    public static double[] reverse(double[] values) {
        for (int i = 0; i < values.length / 2; i++) {
            double temp = values[i];
            values[i] = values[values.length - i - 1];
            values[values.length - i - 1] = temp;
        }
        return values;
    }

    /**
     * Round a double to a certain number of decimal places
     *
     * @param value The double to round
     * @param decimalPlaces The number of decimal places to round off to
     * @return The rounded double
     */
    public static final double round(double value, int decimalPlaces) {

        if (!Double.isFinite(value)) {
            return value;
        }

        // see the Javadoc about why we use a String in the constructor
        // http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static final double[] round(double[] values) {

        // check if array needs clamping
        boolean allValid = true;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != Math.round(values[i])) {
                allValid = false;
                break;
            }
        }

        // no clamp change required
        if (allValid) {
            return values;
        }

        // clamp to new array
        final double[] rounded = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            rounded[i] = Math.round(values[i]);
        }
        return rounded;
    }

    /**
     * Round doubles to a certain number of decimal places
     *
     * @param values The doubles to round
     * @param decimalPlaces The number of decimal places to round off to
     * @return New array with rounded doubles
     */
    public static final double[] round(double[] values, int decimalPlaces) {

        // check if array needs clamping
        boolean allValid = true;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != round(values[i], decimalPlaces)) {
                allValid = false;
                break;
            }
        }

        // no clamp change required
        if (allValid) {
            return values;
        }

        // clamp to new array
        final double[] rounded = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            rounded[i] = round(values[i], decimalPlaces);
        }
        return rounded;
    }

    /**
     * Round a float to a certain number of decimal places, note infinity is converted to max value
     *
     * @param value The float to round
     * @param decimalPlaces The number of decimal places to round off to
     * @return The rounded float
     */
    public static final float round(float value, int decimalPlaces) {
        return round(value, decimalPlaces, true);
    }

    /**
     * Round a float to a certain number of decimal places
     *
     * @param value The float to round
     * @param decimalPlaces The number of decimal places to round off to
     * @param roundInfinite round infinite to max value
     * @return The rounded float
     */
    public static final float round(float value, int decimalPlaces, boolean roundInfinite) {

        if (roundInfinite && Float.isInfinite(value)) {
            value = value > 0f ? Float.MAX_VALUE : -Float.MAX_VALUE;
        }
        if (!Float.isFinite(value)) {
            return value;
        }

        // see the Javadoc about why we use a String in the constructor
        // http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(float)
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    /**
     * Round value to 2 decimals cheaper but slightly (0.01) less accurate then round(value, 2). Use only use when speed is prefered to
     * accuracy. NaN's are converted to zero values.
     */
    public static final float round2Fast(float value) {

        if (value >= Float.MAX_VALUE) {
            return Float.MAX_VALUE;
        } else if (value <= -Float.MAX_VALUE) {
            return -Float.MAX_VALUE;
        }
        return Float.isFinite(value) ? Math.round(value * 100.0f) / 100.0f : 0f;
    }

    public static final void setAllValues(float[][] matrix, float value) {

        if (matrix == null) {
            return;
        }

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        for (int y = 0; y < dimY; y++) {
            float[] line = matrix[y];
            for (int x = 0; x < dimX; x++) {
                line[x] = value;
            }
        }
    }

    /**
     * Sort a map by its values
     *
     * @param <S> Key type
     * @param <T> Value type
     * @param map The map to sort
     * @return The map, sorted by its values
     */
    public static final <S, T extends Comparable<T>> Map<S, T> sortByValue(Map<S, T> map) {

        List<Entry<S, T>> list = new LinkedList<>(map.entrySet());

        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        Map<S, T> result = new LinkedHashMap<>();
        for (Entry<S, T> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static final float[] subtract(float[] a, float[] b) {

        float[] result = new float[a.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    public static final float[][] subtract(float[][] a, float[][] b) {

        int dimX = getDimX(a);
        int dimY = getDimY(a);

        float[][] result = new float[dimY][dimX];
        for (int y = 0; y < dimY; y++) {
            float[] rLine = result[y];
            float[] aLine = a[y];
            float[] bLine = b[y];

            for (int x = 0; x < dimX; x++) {
                rLine[x] = aLine[x] - bLine[x];
            }
        }
        return result;
    }

    public static final double[] subtractD(float[] a, float[] b) {

        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    public static final double[] subtractD(float[] a, float[][] b) {

        int dimX = getDimX(b);
        int dimY = getDimY(b);

        double[] result = new double[a.length];
        for (int y = 0; y < dimY; y++) {
            float[] bLine = b[y];
            for (int x = 0; x < dimX; x++) {
                int index = y * dimX + x;
                result[index] = a[index] - bLine[x];
            }
        }
        return result;
    }

    public static final double[][] subtractD(float[][] a, float[][] b) {

        int dimX = getDimX(a);
        int dimY = getDimY(a);

        double[][] result = new double[dimY][dimX];
        for (int y = 0; y < dimY; y++) {
            double[] rLine = result[y];
            float[] aLine = a[y];
            float[] bLine = b[y];

            for (int x = 0; x < dimX; x++) {
                rLine[x] = aLine[x] - bLine[x];
            }
        }
        return result;
    }

    /**
     * Add all numbers in a collection.
     *
     * @param <N>
     * @param collection the collection you want the sum from
     * @return the sum of all the entries in the collection (double)
     */
    public static final <N extends Number> double sum(Collection<N> collection) {

        double sum = 0;
        for (N number : collection) {
            sum += number.doubleValue();
        }
        return sum;
    }

    public static final double sum(double[] c) {
        return sum(c, 0, c.length);
    }

    public static final double sum(double[] c, int start, int end) {
        double r = 0;
        for (int i = start; i < end; i++) {
            r += c[i];
        }
        return r;
    }

    /**
     * Add all numbers in an array.
     *
     * @param <N>
     * @param c the collection you want the sum from
     * @return the sum of all the entries in the collection (float)
     */
    public static final double sum(float[] c) {
        return sum(c, 0, c.length);
    }

    public static final double sum(float[] c, int start, int end) {
        double r = 0;
        for (int i = start; i < end; i++) {
            r += c[i];
        }
        return r;
    }

    /**
     * Add all numbers in an array.
     *
     * @param <N>
     * @param c the collection you want the sum from
     * @return the sum of all the entries in the collection (int)
     */
    public static final long sum(int[] c) {
        long r = 0;
        for (int x : c) {
            r += x;
        }
        return r;
    }

    /**
     * Return the sum of the map's values
     */
    public static final long sum(Map<Integer, Integer> map) {
        long sum = 0;
        for (Integer i : map.values()) {
            sum += i;
        }
        return sum;
    }

    /**
     * Make sure degrees are between 0-360. e.g. -90 -> 270 degrees
     */
    public static final double to360Degrees(double degrees) {
        return degrees - Math.floor(degrees / 360.0) * 360.0;
    }

    public static final double[] toArray(double[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        double[] result = new double[dimY * dimX];
        for (int y = 0; y < dimY; y++) {
            System.arraycopy(matrix[y], 0, result, y * dimX, dimX);
        }
        return result;
    }

    public static final float[] toArray(float[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        float[] result = new float[dimY * dimX];
        for (int y = 0; y < dimY; y++) {
            System.arraycopy(matrix[y], 0, result, y * dimX, dimX);
        }
        return result;
    }

    public static final int[] toArray(int[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        int[] result = new int[dimY * dimX];
        for (int y = 0; y < dimY; y++) {
            System.arraycopy(matrix[y], 0, result, y * dimX, dimX);
        }
        return result;
    }

    public static final long[] toArray(long[][] matrix) {

        int dimX = getDimX(matrix);
        int dimY = getDimY(matrix);

        long[] result = new long[dimY * dimX];
        for (int y = 0; y < dimY; y++) {
            System.arraycopy(matrix[y], 0, result, y * dimX, dimX);
        }
        return result;
    }

    /**
     * Returns float fraction (clamped to 0-1) as byte value
     */
    public static final byte toBytePct(float fraction) {
        return (byte) (Math.round(clamp(fraction, 0f, 1f) * 200.0f) + Byte.MIN_VALUE);
    }

    /**
     * Ignore higher 16-bit of float.
     *
     * Source: https://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
     */
    public static final float toFloat(short sbits) {

        int hbits = sbits;
        int mant = hbits & 0x03ff; // 10 bits mantissa
        int exp = hbits & 0x7c00; // 5 bits exponent
        if (exp == 0x7c00) {
            exp = 0x3fc00; // -> NaN/Inf

        } else if (exp != 0) {// normalized valueW
            exp += 0x1c000; // exp - 15 + 127
            if (mant == 0 && exp > 0x1c400) {
                return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13); // uncommented: | 0x3ff to be compatible with CUDA
            }

        } else if (mant != 0) { // && exp==0 -> subnormal

            exp = 0x1c400; // make it normal
            do {
                mant <<= 1; // mantissa * 2
                exp -= 0x400; // decrease exp by 1
            } while ((mant & 0x400) == 0); // while not normal
            mant &= 0x3ff; // discard subnormal bit

        } // else +/-0 -> +/-0

        return Float.intBitsToFloat( // combine all parts
                (hbits & 0x8000) << 16 // sign << ( 31 - 15 )
                        | (exp | mant) << 13); // value << ( 23 - 10 )
    }

    /**
     * Returns byte percentage as float fraction (0-1)
     */
    public static final float toFloatFraction(byte byePct) {
        return (byePct - Byte.MIN_VALUE) / 200.0f;
    }

    public static final byte[][] toMatrix(byte[] array, Size size) {

        byte[][] result = new byte[size.y][size.x];
        for (int y = 0; y < size.y; y++) {
            System.arraycopy(array, y * size.x, result[y], 0, size.x);
        }
        return result;
    }

    public static final double[][] toMatrix(double[] array, Size size) {

        double[][] result = new double[size.y][size.x];
        for (int y = 0; y < size.y; y++) {
            System.arraycopy(array, y * size.x, result[y], 0, size.x);
        }
        return result;
    }

    public static final float[][] toMatrix(float[] array, Size size) {

        float[][] result = new float[size.y][size.x];
        for (int y = 0; y < size.y; y++) {
            System.arraycopy(array, y * size.x, result[y], 0, size.x);
        }
        return result;
    }

    public static final int[][] toMatrix(int[] array, Size size) {

        int[][] result = new int[size.y][size.x];
        for (int y = 0; y < size.y; y++) {
            System.arraycopy(array, y * size.x, result[y], 0, size.x);
        }
        return result;
    }

    /**
     * Returns all higher 16 bits as 0 for all results.
     *
     * Source: https://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
     */
    public static final short toShort(float fval) {

        int fbits = Float.floatToIntBits(fval);
        int sign = fbits >>> 16 & 0x8000; // sign only
        int val = (fbits & 0x7fffffff) + 0x1000; // rounded value

        if (val >= 0x47800000) { // might be or become NaN/Inf
            // avoid Inf due to rounding
            if ((fbits & 0x7fffffff) >= 0x47800000) { // is or must become NaN/Inf
                if (val < 0x7f800000) {
                    return (short) (sign | 0x7c00); // make it +/-Inf
                }
                return (short) (sign | 0x7c00 | // remains +/-Inf or NaN
                        (fbits & 0x007fffff) >>> 13); // keep NaN (and Inf) bits
            }
            return (short) (sign | 0x7bff); // unrounded not quite Inf
        }
        if (val >= 0x38800000) {
            return (short) (sign | val - 0x38000000 >>> 13); // exp - 127 + 15
        }
        if (val < 0x33000000) {
            return (short) sign; // becomes +/-0
        }

        val = (fbits & 0x7fffffff) >>> 23; // tmp exp for subnormal calc
        return (short) (sign | (fbits & 0x7fffff | 0x800000) // add subnormal bit
                + (0x800000 >>> val - 102) // round depending on cut off
                >>> 126 - val); // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }

    /**
     * Return array with only unique values
     */
    public static final Integer[] unique(Integer[] array) {

        Set<Integer> set = new LinkedHashSet<>(Arrays.asList(array));

        if (set.size() == array.length) {
            return array; // already unique
        } else {
            return set.toArray(new Integer[set.size()]); // make new unique array
        }
    }
}
