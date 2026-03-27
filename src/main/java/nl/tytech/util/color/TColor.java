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
package nl.tytech.util.color;

import java.awt.Color;
import java.io.Serializable;
import nl.tytech.util.MathUtils;
import nl.tytech.util.SkipObfuscation;

/**
 * Generic Tygron Color (TColor) optimized for our usage and the default to store data.
 *
 * Client apps can have there own color, e.g. ColorRGBA AWT Color Android Color or FX Color, etc...
 *
 * @author Maxim Knepfle
 */
public class TColor implements Serializable, SkipObfuscation {

    private static final long serialVersionUID = -4420931296679670236L;

    public static final TColor TRANSPARENT = new TColor(0.0, 0.0, 0.0, 0.0);

    public static final TColor BLACK = new TColor(0, 0, 0, 255);

    public static final TColor WHITE = new TColor(255, 255, 255, 255);

    public static final TColor DARK_GRAY = new TColor(0.2, 0.2, 0.2, 1.0);

    public static final TColor GRAY = new TColor(0.5, 0.5, 0.5, 1.0);

    public static final TColor LIGHT_GRAY = new TColor(0.8, 0.8, 0.8, 1.0);

    public static final TColor RED = new TColor(255, 0, 0, 255);

    public static final TColor PURPLE = new TColor(128, 0, 128, 255);

    public static final TColor GREEN = new TColor(0, 128, 0, 255);

    public static final TColor DARK_GREEN = new TColor(0, 100, 0, 255);

    public static final TColor BLUE = new TColor(0, 0, 255, 255);

    public static final TColor NAVY = new TColor(0, 0, 128, 255);

    public static final TColor YELLOW = new TColor(255, 255, 0, 255);

    public static final TColor DARK_YELLOW = new TColor(155, 135, 12, 255);

    public static final TColor MAGENTA = new TColor(255, 0, 255, 255);

    public static final TColor CYAN = new TColor(0, 255, 255, 255);

    public static final TColor ORANGE = new TColor(251, 130, 0, 255);

    public static final TColor BROWN = new TColor(65, 40, 25, 255);

    public static final TColor PINK = new TColor(1.0, 0.68, 0.68, 1.0);

    private static final String RGB_FORMAT = "%s, %s, %s";

    private static final String RGBA_FORMAT = "rgba(%s, %s, %s, %s)";

    private static final String TEXT_FORMAT = "%s %s %s %s";

    public static final TColor array(double[] values) {

        if (values == null) {
            return new TColor();

        } else if (values.length >= 4) {
            return new TColor((int) values[0], (int) values[1], (int) values[2], (int) values[3]);

        } else if (values.length >= 3) {
            return new TColor((int) values[0], (int) values[1], (int) values[2]);

        } else if (values.length >= 1) {
            return new TColor((int) values[0]);

        } else {
            return new TColor();
        }
    }

    /**
     * Java Hex parsing terrible, see:
     * https://stackoverflow.com/questions/11377944/parsing-a-hexadecimal-string-to-an-integer-throws-a-numberformatexception
     */
    public static final int parseInteger(String hex) {

        hex = hex.toLowerCase();

        // remove starting # prefix
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        // remove starting 0x prefix
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        return Integer.parseUnsignedInt(hex.length() == 6 ? "ff" + hex : hex, 16);

    }

    public static final TColor random() {
        return new TColor(Math.random(), Math.random(), Math.random(), 1.0);
    }

    public static final int toARGB(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
    }

    /**
     * The Alpha (24-32 bit), Red (16-24 bit), Green (8-16 bit) Blue (0-8 bit) that make up 1 Integer of 32 bit in Word Order.
     */
    private final int argb;
    /**
     * Cached value of other representations
     */
    private transient String html = null;
    private transient String hex = null;
    private transient Color awt = null;
    private transient String dxf = null;
    private transient double[] array = null;
    private transient String fxString = null;
    private transient String cssRGB = null;
    private transient String cssRGBA = null;

    private transient String string = null;

    public TColor() {
        this(0);
    }

    public TColor(double argb) {
        this((int) argb);
    }

    public TColor(double r, double g, double b) {
        this(r, g, b, 1.0);
    }

    public TColor(double r, double g, double b, double a) {
        this((int) (r * 255d), (int) (g * 255d), (int) (b * 255d), (int) (a * 255d));
    }

    public TColor(int argb) {
        this.argb = argb;
    }

    public TColor(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public TColor(int r, int g, int b, int a) {
        this(toARGB(r, g, b, a));
    }

    public TColor(String hex) {
        this(parseInteger(hex));
    }

    public TColor(TColor color) {
        this(color.argb);
    }

    public TColor(TColor color, int a) {
        this(color.getRed(), color.getGreen(), color.getBlue(), a);
    }

    public int distance(TColor other) {

        int distance = Math.abs(this.getRed() - other.getRed());
        distance += Math.abs(this.getBlue() - other.getBlue());
        distance += Math.abs(this.getGreen() - other.getGreen());
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TColor other && this.argb == other.argb;
    }

    public int getAlpha() {
        return argb >> 24 & 0xff;
    }

    public int getARGB() {
        return argb;
    }

    public int getBlue() {
        return argb >> 0 & 0xff;
    }

    public int getGreen() {
        return argb >> 8 & 0xff;
    }

    public int getRed() {
        return argb >> 16 & 0xff;
    }

    @Override
    public int hashCode() {
        return argb;
    }

    public boolean isOpaque() {
        return getAlpha() == 255;
    }

    public TColor mult(double factor) {
        int r = MathUtils.clamp((int) (getRed() * factor), 0, 255);
        int g = MathUtils.clamp((int) (getGreen() * factor), 0, 255);
        int b = MathUtils.clamp((int) (getBlue() * factor), 0, 255);
        return new TColor(r, g, b, getAlpha());
    }

    public TColor multAlpha(double factor) {
        int a = MathUtils.clamp((int) (getAlpha() * factor), 0, 255);
        return new TColor(getRed(), getGreen(), getBlue(), a);
    }

    /**
     * Attribute Item array format
     */
    public double[] toArray() {
        if (array == null) {
            array = new double[] { argb };
        }
        return array;
    }

    public Color toAWTColor() {
        if (awt == null) {
            awt = new Color(getRed(), getGreen(), getBlue(), getAlpha());
        }
        return awt;
    }

    /**
     * CCS is almost identical to FX, however alpha is 0-1.
     */
    public String toCSS() {
        return toCSS(true);
    }

    /**
     * CCS is almost identical to FX, however optional alpha is 0-1.
     */
    public String toCSS(boolean alpha) {

        if (alpha) {
            if (cssRGBA == null) {
                cssRGBA = RGBA_FORMAT.formatted(getRed(), getGreen(), getBlue(), MathUtils.round(getAlpha() / 255.0, 2));
            }
            return cssRGBA;
        } else {
            if (cssRGB == null) {
                cssRGB = RGB_FORMAT.formatted(getRed(), getGreen(), getBlue());
            }
            return cssRGB;
        }
    }

    /**
     * AutoCAD True Color (RGB)
     */
    public String toDXF() {
        if (dxf == null) {
            dxf = Integer.toString((getRed() & 0xFF) << 16 | (getGreen() & 0xFF) << 8 | (getBlue() & 0xFF) << 0);
        }
        return dxf;
    }

    public String toFXString() {
        if (fxString == null) {
            fxString = RGBA_FORMAT.formatted(getRed(), getGreen(), getBlue(), getAlpha());
        }
        return fxString;
    }

    public String toHex() {
        if (hex == null) {
            hex = Integer.toHexString(argb);
        }
        return hex;
    }

    public String toHTML() {
        if (html == null) {
            String hex = toHex();
            if (hex.length() >= 2) {
                html = "#" + hex.substring(2).toUpperCase();
            } else {
                html = "#000000";// alpha takes over
            }
        }
        return html;
    }

    @Override
    public String toString() {
        if (string == null) {
            string = TEXT_FORMAT.formatted(getRed(), getGreen(), getBlue(), getAlpha());
        }
        return string;
    }
}
