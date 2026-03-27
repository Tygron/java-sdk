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
import nl.tytech.data.engine.item.DistanceOverlay.DistanceResult;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * Overlay that calculates distances
 *
 * @author Frank Baars
 */
public class DistanceOverlay extends DefaultRasterizationOverlay<DistanceResult> {

    public enum DistanceResult implements ResultType {

        ZONE, SIGHT;

        private DistanceResult() {
        }

        @Override
        public byte getIndex() {
            return (byte) ordinal();
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum SightDistanceAttribute implements ReservedAttribute {

        SOURCE_HEIGHT_M(Double.class, 0.75),

        OBSERVER_HEIGHT_M(Double.class, 0.0),

        ATTRIBUTE_TIMEFRAMES(Double.class, 1);

        private final Class<?> type;
        private final double[] defaultArray;

        private SightDistanceAttribute(Class<?> type, double defaultValue) {
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

    public enum ZoneDistanceAttribute implements ReservedAttribute {

        ATTRIBUTE_TIMEFRAMES(Double.class, 1),

        HIT_COUNT(Boolean.class, 0);

        private final Class<?> type;
        private final double[] defaultArray;

        private ZoneDistanceAttribute(Class<?> type, double defaultValue) {
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

    private static final long serialVersionUID = 1228881784662366612L;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        // when active override with prequel frames
        if (isActive()) {
            if (calcPrequelTimeframes()) {
                return getPrequelTimeframes(RasterizationPrequel.INPUT, cache);
            } else {
                return MathUtils.clamp((int) getAttribute(ZoneDistanceAttribute.ATTRIBUTE_TIMEFRAMES), 1, getMaxTimeFrames());
            }
        }
        return super.calcTimeframes(cache);
    }

    @Override
    public int getDecimals() {
        return 3; // Millimeters
    }

    @Override
    protected String getDefaultImagePrefix() {
        return "distance";
    }

    @Override
    protected DistanceResult getDefaultResult() {
        return getType() == OverlayType.SIGHT_DISTANCE ? DistanceResult.SIGHT : DistanceResult.ZONE;
    }

    @Override
    protected Class<DistanceResult> getResultClass() {
        return DistanceResult.class;
    }

    @Override
    public DistanceResult getResultType() {
        // not changeable for sight or zone
        return getType() == OverlayType.SIGHT_DISTANCE ? DistanceResult.SIGHT : DistanceResult.ZONE;
    }
}
