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
import cjava.AvgVar;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.MathUtils;

/**
 * Take terrain and building averages
 *
 * @author Maxim Knepfle
 */
public class AvgOverlay extends DefaultRasterizationOverlay<AvgOverlay.Operator> {

    public enum AvgModelAttribute implements ReservedAttribute {

        DISTANCE_M(Double.class, 100),

        ATTRIBUTE_TIMEFRAMES(Double.class, 1),

        DEFAULT_VALUE(Double.class, 0.0),

        SURFACE_ONLY(Double.class, 0.0);

        private final Class<?> type;
        private final double[] defaultArray;

        private AvgModelAttribute(Class<?> type, double defaultValue) {
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

    public enum Operator implements ResultType {

        AVG(AvgVar.OP_AVG),

        AVG_INTERPOLATED(AvgVar.OP_AVG_INTERPOLATED),

        MAX(AvgVar.OP_MAX),

        MIN(AvgVar.OP_MIN);

        // More?

        public static Operator fromValue(double attribute) {

            for (Operator b : Operator.values()) {
                if (b.getIndex() == attribute) {
                    return b;
                }
            }
            return Operator.AVG;
        }

        private final byte index;

        private Operator(byte index) {
            this.index = index;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        @Override
        public boolean isStatic() {
            return false;
        }
    }

    public static final int MAX_NO_DATA_INTERPOLATION = 100;

    public static final double MAX_DISTANCE_M = 2000;

    private static final long serialVersionUID = 4911257565806176230L;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        // when active override with prequel frames
        if (isActive()) {
            if (calcPrequelTimeframes()) {
                return getPrequelTimeframes(RasterizationPrequel.INPUT, cache);
            } else {
                return MathUtils.clamp((int) getAttribute(AvgModelAttribute.ATTRIBUTE_TIMEFRAMES), 1, getMaxTimeFrames());
            }
        }
        return super.calcTimeframes(cache);
    }

    @Override
    public int getDecimals() {

        return switch (this.getType()) {
            case LIVABILITY -> 2;
            default -> -1; // no roundoff
        };
    }

    @Override
    protected Operator getDefaultResult() {
        return Operator.AVG;
    }

    @Override
    public Rasterization getRasterization() {
        // Note: Livability always needs FIRST: Buildings + terrain have an LIVABILITY_EFFECT attribute
        return getType() == OverlayType.LIVABILITY ? Rasterization.FIRST : super.getRasterization();
    }

    @Override
    protected Class<Operator> getResultClass() {
        return Operator.class;
    }

    @Override
    public Operator getResultType() {
        return getType() == OverlayType.LIVABILITY ? Operator.AVG : super.getResultType();
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (this.hasAttribute("OPERATOR")) {
            double value = this.getAttribute("OPERATOR");
            this.setResultType(Operator.fromValue(value));
            this.removeAttribute("OPERATOR", true);
        }
        return result;
    }
}
