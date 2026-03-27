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
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.engine.item.DefaultOverlay.DefaultResult;
import nl.tytech.data.engine.item.IterationOverlay.IterationPrequel;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.util.DateUtils;
import nl.tytech.util.MathUtils;

/**
 * Collects the result of each Iteration as a timeframe
 *
 * @author Maxim Knepfle
 */
public class IterationOverlay extends GridOverlay<DefaultResult, IterationPrequel> {

    public enum IterationAttribute implements ReservedAttribute {

        MODULUS(Integer.class, 1.0),

        TIMEFRAME_TIMES(Double.class, 0);

        public static final IterationAttribute[] VALUES = IterationAttribute.values();

        private final Class<?> type;
        private final double[] defaultArray;

        private IterationAttribute(Class<?> type, double defaultValue) {
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

    public enum IterationPrequel implements PrequelType {

        ITERATION;

        public static final IterationPrequel[] VALUES = IterationPrequel.values();
    }

    private static final long serialVersionUID = 5024326114506247822L;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        if (isActive()) { // only when active
            Setting setting = getItem(MapLink.SETTINGS, Setting.Type.ITERATIONS);
            return Math.max(1, setting.getIntValue() / getModulus());
        }
        // fallback when inactive or invalid formula
        return super.calcTimeframes(cache);
    }

    @Override
    protected DefaultResult getDefaultResult() {
        return DefaultResult.DEFAULT;
    }

    public int getModulus() {
        return MathUtils.clamp((int) getOrDefault(IterationAttribute.MODULUS), 1, 1000);
    }

    @Override
    public IterationPrequel[] getPrequelTypes() {
        return IterationPrequel.VALUES;
    }

    @Override
    protected Class<DefaultResult> getResultClass() {
        return DefaultResult.class;
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {

        double[] timeframeTimes = getOrDefaultArray(IterationAttribute.TIMEFRAME_TIMES);
        if (timeframeTimes != null && timeframeTimes.length > timeframe) {
            long timeMS = getTimeframeTimeSec(timeframe) * Moment.SECOND;
            return DateUtils.format(DateUtils.UTC, format, timeMS);
        }
        return super.getTimeframeText(timeframe, format);
    }

    @Override
    public long getTimeframeTimeSec(int timeframe) {

        int timeframes = getTimeframes();
        double[] timeframeTimes = getOrDefaultArray(IterationAttribute.TIMEFRAME_TIMES);
        if (timeframe < 0 || timeframeTimes.length == 0) {
            return 0;
        }
        int index = Math.min(timeframes, timeframeTimes.length) - 1;
        return DateUtils.toTimeMillis(timeframeTimes[Math.min(timeframe, index)]);
    }
}
