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

import java.util.ArrayList;
import java.util.List;
import nl.tytech.util.MathUtils;
import nl.tytech.util.logger.TLogger;

/**
 * @author Jeroen Warmerdam, Frank Baars, Maxim Knepfle
 *
 */
public class ColorUtils {

    // Interface colors
    public static final TColor COLOR_INTERFACE_RED = new TColor(225, 135, 135);

    public static final TColor COLOR_INTERFACE_SHADOW_GREEN = new TColor(125, 235, 125);

    public static final TColor COLOR_INTERFACE_GREEN = new TColor(99, 255, 99);

    public static final TColor COLOR_INTERFACE_BLUE = new TColor(192, 192, 255);

    public static final TColor COLOR_INTERFACE_YELLOW = new TColor(230, 230, 120);

    public static final TColor COLOR_INTERFACE_ORANGE = new TColor(255, 170, 70);

    public static final TColor COLOR_INTERFACE_ACTIVE = new TColor(94, 255, 255); // +10 for 2018 update

    public static final TColor COLOR_INTERFACE = new TColor(79, 185, 207); // +10 for 2018 update

    public static final TColor COLOR_INTERFACE_WHITE = new TColor(255, 255, 255); // +10 for 2018 update

    public static final TColor COLOR_INTERFACE_BLACK = new TColor(0, 0, 0); // +10 for 2018 update

    public static final TColor BACKGROUND_BLACK = new TColor(29, 32, 33, 217);

    public static final TColor BACKGROUND_WHITE = new TColor(240, 240, 245);

    public static final TColor BACKGROUND_VIEWPORT = new TColor(0.75f, 0.75f, 0.75f, 1.0f);

    // Selection colors
    public static final TColor SELECTION_ACCEPT_COLOR = COLOR_INTERFACE_GREEN;
    public static final TColor SELECTION_NO_OWNERSHIP = COLOR_INTERFACE_ORANGE;
    public static final TColor SELECTION_DECORATION = COLOR_INTERFACE_YELLOW;
    public static final TColor SELECTION_DISCARD = COLOR_INTERFACE_RED;
    public static final TColor SELECTION_CURSOR_COLOR = new TColor(85, 255, 255);
    public static final TColor DEFAULT_MAQUETTE_COLOR = new TColor(146, 181, 190);
    public static final TColor DEFAULT_ORGINAL_COLOR = new TColor(146, 181, 190, 80);

    // Random Color Generation Parameters
    private static final int MINIMAL_RESERVED_COLOR_DISTANCE = 10;
    private static final int MAX_RANDOM_TRIES = 500;
    public static final int MAX_DIFFERENT_COLORS = 30;

    private static final List<TColor> RESERVED_COLORS = new ArrayList<>();

    static {
        RESERVED_COLORS.add(SELECTION_ACCEPT_COLOR);
        RESERVED_COLORS.add(SELECTION_NO_OWNERSHIP);
        RESERVED_COLORS.add(SELECTION_DECORATION);
        RESERVED_COLORS.add(SELECTION_DISCARD);
        RESERVED_COLORS.add(SELECTION_CURSOR_COLOR);
        RESERVED_COLORS.add(DEFAULT_MAQUETTE_COLOR);
        RESERVED_COLORS.add(BACKGROUND_BLACK);
        RESERVED_COLORS.add(BACKGROUND_WHITE);
    }

    private static final List<TColor> DEFAULT_GRAPH_COLORS = new ArrayList<>();

    static {
        DEFAULT_GRAPH_COLORS.add(new TColor(255, 10, 10));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 255, 100));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 180, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(237, 132, 0));
        DEFAULT_GRAPH_COLORS.add(new TColor(255, 255, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(150, 200, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 255, 100));
        DEFAULT_GRAPH_COLORS.add(new TColor(0, 180, 255));
        DEFAULT_GRAPH_COLORS.add(new TColor(237, 132, 0));
        DEFAULT_GRAPH_COLORS.add(new TColor(255, 255, 255));
    }

    private static final double colorDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
        double rmean = (r1 + r2) / 2;
        int r = r1 - r2;
        int g = g1 - g2;
        int b = b1 - b2;
        double weightR = 2 + rmean / 256;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256;
        return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
    }

    /**
     * Creates a random RGB value. To avoid black and white, 0 and 255 have been prohibited.
     *
     * @return random RGB value
     */
    public static final TColor createRandomRGB() {
        return ColorUtils.createRandomRGB(255, null);
    }

    /**
     * Creates a random RGB value. To avoid black and white, 0 and 255 have been prohibited.
     * @param alpha The alpha the generated colour should have
     * @return random RGB value
     */
    public static final TColor createRandomRGB(int alpha, Iterable<TColor> forbiddenColors) {

        double distance = 0;
        int r, g, b = 0;
        int tries = 0;
        while (tries < MAX_RANDOM_TRIES) {
            distance = Double.MAX_VALUE;
            r = MathUtils.randomInt(254) + 1;
            g = MathUtils.randomInt(254) + 1;
            b = MathUtils.randomInt(254) + 1;

            for (TColor rc : RESERVED_COLORS) {
                distance = Math.min(distance, colorDistance(r, g, b, rc.getRed(), rc.getGreen(), rc.getBlue()));
            }

            if (forbiddenColors != null) {
                for (TColor rc : forbiddenColors) {
                    distance = Math.min(distance, colorDistance(r, g, b, rc.getRed(), rc.getGreen(), rc.getBlue()));
                }
            }

            if (distance > MINIMAL_RESERVED_COLOR_DISTANCE) {
                return new TColor(r, g, b);
            }
        }
        TLogger.warning("Create non-unique color, because we've already tried " + MAX_RANDOM_TRIES + " random generations.");
        return new TColor(MathUtils.randomInt(254) + 1, MathUtils.randomInt(254) + 1, MathUtils.randomInt(254) + 1, alpha);
    }

    public static final TColor createRandomRGB(Iterable<TColor> forbiddenColors) {
        return ColorUtils.createRandomRGB(255, forbiddenColors);
    }

    public static final int getAlpha(int argb) {
        return argb >> 24 & 0xFF;
    }

    public static final int getBlue(int argb) {
        return argb >> 0 & 0xFF;
    }

    public static final TColor getDefaultGraphColor(int index) {
        if (index < 0 || index >= DEFAULT_GRAPH_COLORS.size()) {
            return null;
        }
        return DEFAULT_GRAPH_COLORS.get(index);
    }

    public static final int getGreen(int argb) {
        return argb >> 8 & 0xFF;
    }

    public static final int getRed(int argb) {
        return argb >> 16 & 0xFF;
    }

    public static final TColor simplify(TColor color, int factor) {

        if (color == null) {
            return null;
        }
        int red = MathUtils.clamp(Math.round(color.getRed() / (float) factor) * factor, 0, 255);
        int green = MathUtils.clamp(Math.round(color.getGreen() / (float) factor) * factor, 0, 255);
        int blue = MathUtils.clamp(Math.round(color.getBlue() / (float) factor) * factor, 0, 255);
        int alpha = MathUtils.clamp(Math.round(color.getAlpha() / (float) factor) * factor, 0, 255);
        return new TColor(red, green, blue, alpha);
    }

    public static final float toFloat(int bits) {
        return Float.intBitsToFloat(bits);
    }

    public static final float toFloatRGB(int rgb) {
        return toFloat(rgb & 0xFFFFFF + (254 << 24)); // 254 due to float restrictions?
    }

    public static final int toInt(float value) {
        return Float.floatToRawIntBits(value);
    }
}
