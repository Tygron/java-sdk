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

import java.util.Map;
import nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.TrafficHourInterface;
import nl.tytech.util.MathUtils;

/**
 * Traffic Density overlay
 *
 * @author Maxim Knepfle
 */
public class TrafficDensityOverlay extends Overlay implements TrafficHourInterface {

    private static final long serialVersionUID = 4911257565806176441L;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        if (isActive()) {
            double[] hours = this.getOrDefaultArray(TrafficAttribute.HOURS);
            return MathUtils.clamp(hours.length, 1, 24);
        }
        return super.calcTimeframes(cache);
    }

    @Override
    public int getHour(int tf) {

        double[] hours = this.getOrDefaultArray(TrafficAttribute.HOURS);
        int hour = tf < hours.length ? (int) hours[tf] : 0;
        return MathUtils.clamp(hour, 0, 23);
    }

    @Override
    public final double getHourNum(Building b, TrafficType type, int hour) {

        if (type.getActiveAttribute() != null && getOrDefault(type.getActiveAttribute()) <= 0.0) {
            return 0.0;
        }
        return b.getTrafficHour(type, hour);
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {
        return getHour(timeframe) + "u";
    }

    @Override
    public boolean hasValueLegend() {
        return true;
    }
}
