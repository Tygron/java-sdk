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

import cjava.WatershedVar;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.WatershedOverlay.WatershedPrequel;
import nl.tytech.data.engine.item.WatershedOverlay.WatershedResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;

/**
 * WatershedOverlay: watershedding algorithm overlay
 *
 * @author Frank Baars
 */
public class WatershedOverlay extends ResultParentOverlay<WatershedResult, WatershedPrequel> {

    public enum DischargeMethod {

        HEIGHT_MINIMA("Height minima", 0),

        WATER_TERRAINS("Water Terrains", 1),

        AREAS("Specified Areas", 2);

        private final String humanName;

        private final double index;

        private DischargeMethod(String name, int index) {
            this.humanName = name;
            this.index = index;
        }

        public double getValue() {
            return index;
        }

        @Override
        public String toString() {
            return humanName;
        }
    }

    public enum WatershedKey implements Key {

        DISCHARGE_AREA(1.0);

        private double[] defaultArray;

        private WatershedKey(double value) {
            this(new double[] { value });
        }

        private WatershedKey(double[] value) {
            defaultArray = value;
        }

        public double[] getDefaultArray() {
            return defaultArray;
        }

        public double getDefaultValue() {
            return defaultArray[0];
        }

        @Override
        public UnitType getUnitType() {
            return UnitType.NONE;
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public boolean isOutput() {
            return false;
        }

    }

    public enum WatershedModelAttribute implements ReservedAttribute {

        FILL_DISTANCE(Double.class, 10_000.0),

        DISCHARGE_REMAINING_WATER(Boolean.class, 0.0),

        CULVERT_FLOW_DIRECTION(Boolean.class, 0.0),

        MIN_AREA(Double.class, 100.0),

        LIMIT_ROAD(Boolean.class, 1.0),

        ;

        private final double[] defaultValues;
        private final Class<?> type;

        private WatershedModelAttribute(Class<?> type, double... defaultValue) {
            this.type = type;
            this.defaultValues = defaultValue;
        }

        @Override
        public double[] defaultArray() {
            return defaultValues;
        }

        @Override
        public double defaultValue() {
            return defaultValues[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }

    }

    public enum WatershedPrequel implements PrequelType {

        DIRECTION;

        public static final WatershedPrequel[] VALUES = WatershedPrequel.values();

    }

    public enum WatershedResult implements ResultType {

        WATERSHEDS(WatershedVar.TYPE_WATERSHEDS),

        DISCHARGE_AREAS(WatershedVar.TYPE_DISCHARGE_AREAS),

        DIRECTION(WatershedVar.TYPE_DIRECTION),

        BASE_TYPES(WatershedVar.TYPE_BASE_TYPES),

        ;

        private final byte index;

        private WatershedResult(byte index) {
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

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    private static final long serialVersionUID = -4100431060014120031L;

    @XMLValue
    private DischargeMethod method = DischargeMethod.HEIGHT_MINIMA;

    public WatershedOverlay() {

    }

    @Override
    protected WatershedResult getDefaultResult() {
        return WatershedResult.WATERSHEDS;
    }

    public DischargeMethod getDischargeMethod() {
        return method;
    }

    @Override
    public WatershedPrequel[] getPrequelTypes() {
        return WatershedPrequel.VALUES;
    }

    @Override
    protected Class<WatershedResult> getResultClass() {
        return WatershedResult.class;
    }

    public WaterOverlay getWaterPrequel() {

        Overlay overlay = getPrequel(WatershedPrequel.DIRECTION);
        if (overlay instanceof ResultChildOverlay<?, ?>) {
            overlay = overlay.getParent();
        }
        return overlay instanceof WaterOverlay wa ? wa : null;
    }

    public void setDischargeMethod(DischargeMethod method) {
        this.method = method;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        // add defaults
        for (WatershedModelAttribute type : WatershedModelAttribute.values()) {
            if (!this.hasAttribute(type)) {
                this.setAttribute(type, type.defaultValue());
            }
        }

        return result;
    }
}
