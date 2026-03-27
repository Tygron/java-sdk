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

import cjava.NO2Var;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.NO2Overlay.NO2Result;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * NO2 based on a grid
 *
 * @author Maxim Knepfle
 */
public class NO2Overlay extends TrafficOverlay<NO2Result> {

    public enum GasType {
        NOX, NO2
    }

    public enum NO2Result implements ResultType {

        CONCENTRATION(NO2Var.TYPE_CONCENTRATION, ClientTerms.NO2_CONCENTRATION),

        EMISSION_NO2(NO2Var.TYPE_EMISSION_NO2, ClientTerms.NO2_EMISSION),

        EMISSION_NOX(NO2Var.TYPE_EMISSION_NOX, ClientTerms.NOX_EMISSION),

        TREEFACTOR(NO2Var.TYPE_TREEFACTOR, ClientTerms.NO2_TREEFACTOR),

        TREES(NO2Var.TYPE_TREES, ClientTerms.NO2_TREES),

        ;

        private final byte index;

        private final ClientTerms term;

        private NO2Result(byte index, ClientTerms term) {
            this.index = index;
            this.term = term;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        public ClientTerms getTerm() {
            return term;
        }

        @Override
        public boolean isStatic() {
            return this == TREES || this == TREEFACTOR;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum TrafficNO2Attribute implements ReservedAttribute {

        BASE_O3(Double.class, 42),

        BASE_NO2(Double.class, 0),

        // cars: NOx No2 g/km 2015 niet snelwegen stad normaal licht motorvoertuig bron: infomill

        EMISSION_CAR_NOX(Double.class, 0.36),

        EMISSION_CAR_NO2(Double.class, 0.09),

        EMISSION_JAM_CAR_NOX(Double.class, 0.57),

        EMISSION_JAM_CAR_NO2(Double.class, 0.14),

        // vans: NOx No2 g/km 2015 niet snelwegen stad normaal middelzwaar motorvoertuig bron: infomill

        EMISSION_VAN_NOX(Double.class, 6.9),

        EMISSION_VAN_NO2(Double.class, 0.41),

        EMISSION_JAM_VAN_NOX(Double.class, 11.32),

        EMISSION_JAM_VAN_NO2(Double.class, 0.68),

        // trucks: NOx no2 g/km 2015 niet snelwegen stad normaal zwaar motorvoertuig bron: infomill

        EMISSION_TRUCK_NOX(Double.class, 8.99),

        EMISSION_TRUCK_NO2(Double.class, 0.46),

        EMISSION_JAM_TRUCK_NOX(Double.class, 14.74),

        EMISSION_JAM_TRUCK_NO2(Double.class, 0.76),

        // buses: NOx no2 g/km 2015 niet snelwegen stad stagnered bussen bron: infomill

        EMISSION_BUS_NOX(Double.class, 5.88),

        EMISSION_BUS_NO2(Double.class, 0.61),

        EMISSION_JAM_BUS_NOX(Double.class, 9.4),

        EMISSION_JAM_BUS_NO2(Double.class, 0.98),

        // Road Type 1: algemeen stedelijk variables weg type 1

        ROAD1_30M_A(Double.class, 0.000325),

        ROAD1_30M_B(Double.class, -0.0205),

        ROAD1_30M_C(Double.class, 0.39),

        ROAD1_60M_ALPHA(Double.class, 0.856),

        ROAD1_60M_POWER(Double.class, -0.747),

        MIN_TREE_HEIGHT_M(Double.class, 2),

        ;

        private final Class<?> type;
        private final double[] defaultArray;

        private TrafficNO2Attribute(Class<?> type, double defaultValue) {
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

    private static final long serialVersionUID = -2572949484529750426L;

    @Override
    public int getDecimals() {

        switch (this.getResultType()) {
            case CONCENTRATION:
            case TREES:
                return -1; // Input, no roundoff
            default:
                return 2; // Output
        }
    }

    @Override
    protected NO2Result getDefaultResult() {
        return NO2Result.CONCENTRATION;
    }

    public TrafficNO2Attribute getEmissionAttribute(TrafficType type, GasType gasType, boolean jam) {

        switch (type) {
            case CAR:
                switch (gasType) {
                    case NO2:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_CAR_NO2 : TrafficNO2Attribute.EMISSION_CAR_NO2;
                    case NOX:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_CAR_NOX : TrafficNO2Attribute.EMISSION_CAR_NOX;
                    default:
                        break;
                }
            case VAN:
                switch (gasType) {
                    case NO2:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_VAN_NO2 : TrafficNO2Attribute.EMISSION_VAN_NO2;
                    case NOX:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_VAN_NOX : TrafficNO2Attribute.EMISSION_VAN_NOX;
                    default:
                        break;
                }
            case TRUCK:
                switch (gasType) {
                    case NO2:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_TRUCK_NO2 : TrafficNO2Attribute.EMISSION_TRUCK_NO2;
                    case NOX:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_TRUCK_NOX : TrafficNO2Attribute.EMISSION_TRUCK_NOX;
                    default:
                        break;
                }
            case BUS:
                switch (gasType) {
                    case NO2:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_BUS_NO2 : TrafficNO2Attribute.EMISSION_BUS_NO2;
                    case NOX:
                        return jam ? TrafficNO2Attribute.EMISSION_JAM_BUS_NOX : TrafficNO2Attribute.EMISSION_BUS_NOX;
                    default:
                        break;
                }
            default:
                break;
        }
        TLogger.severe("No Emission found for: " + type + " " + gasType + " " + jam);
        return null;
    }

    public double getEmissionValue(TrafficType type, GasType gasType, boolean jam) {
        TrafficNO2Attribute attribute = getEmissionAttribute(type, gasType, jam);
        return attribute != null ? getOrDefault(attribute) : 0.0;
    }

    public double[] getEmissionValues(TrafficType type, boolean jam) {
        return new double[] { getEmissionValue(type, GasType.NO2, jam), getEmissionValue(type, GasType.NOX, jam) };
    }

    @Override
    protected Class<NO2Result> getResultClass() {
        return NO2Result.class;
    }
}
