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

import java.util.List;
import nl.tytech.data.engine.serializable.LegendEntry;

/**
 * Utils that convert grid values to legend colors
 *
 * @author Maxim Knepfle
 *
 */
public class LegendUtils {

    public static final int getARGB(float value) {
        return ColorUtils.toInt(value);
    }

    public static final int getARGB(List<LegendEntry> legend, float value) {

        if (value <= legend.getFirst().getValue()) {
            return legend.getFirst().getColor().getARGB();
        }
        int startindex = 1;
        if (legend.size() > 1000) {
            for (int i = startindex; i < legend.size(); i += 500) {
                if (value > legend.get(i).getValue()) {
                    startindex = Math.max(1, i - 500);
                }
            }
        }
        for (int i = startindex; i < legend.size(); i++) {
            if (value < legend.get(i).getValue()) {
                return getARGBInterpolated(legend.get(i - 1), legend.get(i), value);
            }
        }
        return legend.getLast().getColor().getARGB();
    }

    private static final int getARGBInterpolated(LegendEntry diaLow, LegendEntry diaHigh, float value) {

        float totalDistance = diaHigh.getValue() - diaLow.getValue();
        float distance = totalDistance == 0 ? 1 : (diaHigh.getValue() - value) / totalDistance;
        TColor colLow = diaLow.getColor();
        TColor colHigh = diaHigh.getColor();
        int red = colHigh.getRed() + (int) (distance * (colLow.getRed() - colHigh.getRed()));
        int green = colHigh.getGreen() + (int) (distance * (colLow.getGreen() - colHigh.getGreen()));
        int blue = colHigh.getBlue() + (int) (distance * (colLow.getBlue() - colHigh.getBlue()));
        int alpha = colHigh.getAlpha() + (int) (distance * (colLow.getAlpha() - colHigh.getAlpha()));
        return TColor.toARGB(red, green, blue, alpha);
    }
}
