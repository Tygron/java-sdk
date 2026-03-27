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
package nl.tytech.data.engine.other;

import static nl.tytech.util.MathUtils.clamp;
import static nl.tytech.util.MathUtils.toBytePct;
import static nl.tytech.util.MathUtils.toShort;
import nl.tytech.data.engine.item.Setting.Size;

/**
 * Central interface to handle grid data from Matrices, Arrays or Grid Data objects
 *
 * @author Maxim Knepfle
 */
public abstract class Grid {

    public abstract float get(int x, int y);

    public abstract int getHeight();

    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }

    public abstract int getWidth();

    public abstract void set(int x, int y, float value);

    public abstract float[][] toMatrix();

    /**
     * Fill target matrix with clamped values
     */
    public final double[][] toMatrix(double[][] target, float min, float max) {

        int h = getHeight();
        int w = getWidth();
        for (int y = 0; y < h; y++) {
            double[] line = target[y];
            for (int x = 0; x < w; x++) {
                line[x] = clamp(get(x, y), min, max);
            }
        }
        return target;
    }

    /**
     * Fill a copy of this matrix with clamped values
     */
    public float[][] toMatrix(float min, float max) {
        return toMatrix(1f, min, max);
    }

    /**
     * Fill a copy of this matrix with clamped values
     */
    public float[][] toMatrix(float multiplier, float min, float max) {
        return toMatrix(new float[getHeight()][getWidth()], multiplier, min, max);
    }

    /**
     * Fill target matrix with clamped values
     */
    public final float[][] toMatrix(float[][] target, float min, float max) {
        return toMatrix(target, 1f, min, max);
    }

    /**
     * Fill target matrix with multiplier in clamped values
     */
    public final float[][] toMatrix(float[][] target, float multiplier, float min, float max) {

        int h = getHeight();
        int w = getWidth();
        for (int y = 0; y < h; y++) {
            float[] line = target[y];
            for (int x = 0; x < w; x++) {
                line[x] = clamp(get(x, y) * multiplier, min, max);
            }
        }
        return target;
    }

    /**
     * Fill target matrix with multiplier in clamped matrix values
     */
    public final float[][] toMatrix(float[][] target, float min, float[][] max) {

        int h = getHeight();
        int w = getWidth();
        for (int y = 0; y < h; y++) {
            float[] line = target[y];
            float[] maxLine = max[y];
            for (int x = 0; x < w; x++) {
                line[x] = clamp(get(x, y), min, maxLine[x]);
            }
        }
        return target;
    }

    /**
     * Fill target matrix with clamped values
     */
    public final short[][] toMatrix(short[][] target, float min, float max) {
        return toMatrix(target, 1f, min, max);
    }

    /**
     * Fill target matrix with multiplier in clamped values
     */
    public final short[][] toMatrix(short[][] target, float multiplier, float min, float max) {

        int h = getHeight();
        int w = getWidth();
        for (int y = 0; y < h; y++) {
            short[] line = target[y];
            for (int x = 0; x < w; x++) {
                line[x] = toShort(clamp(get(x, y) * multiplier, min, max));
            }
        }
        return target;
    }

    /**
     * Fill target matrix with byte percentages
     */
    public final byte[][] toMatrixPct(byte[][] target, float multiplier) {

        int h = getHeight();
        int w = getWidth();
        for (int y = 0; y < h; y++) {
            byte[] line = target[y];
            for (int x = 0; x < w; x++) {
                line[x] = toBytePct(clamp(get(x, y) * multiplier, 0f, 1f));
            }
        }
        return target;
    }
}
