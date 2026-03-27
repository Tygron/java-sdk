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

import cjava.NoiseVar;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.item.NoiseOverlay.NoiseResult;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.StringUtils;

/**
 * Traffic noise overlay
 *
 * @author Maxim Knepfle
 */
public class NoiseOverlay extends TrafficOverlay<NoiseResult> {

    public enum NoiseResult implements ResultType {

        NOISE_DB(NoiseVar.TYPE_NOISE_DB, ClientTerms.NOISE_DB),

        EMISSION(NoiseVar.TYPE_EMISSION, ClientTerms.NOISE_EMISSION),

        ;

        private final byte index;
        private final ClientTerms term;

        private NoiseResult(byte index, ClientTerms term) {
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
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum TrafficNoiseAttribute implements ReservedAttribute {

        /**
         * Background value, between 35-100db
         */
        BACKGROUND_DB(Double.class, 45),

        V0_CAR(Double.class, 80), // referentie snelheid auto
        V0_VAN(Double.class, 80), // referentie snelheid busje
        V0_TRUCK(Double.class, 70), // referentie snelheid truck
        V0_BUS(Double.class, 70), // referentie snelheid truck

        EMISSION_START_CAR(Double.class, 70.0), // Start waarde auto
        EMISSION_START_VAN(Double.class, 73.2), // Start waarde busje
        EMISSION_START_TRUCK(Double.class, 76.0), // Start waarde truck
        EMISSION_START_BUS(Double.class, 76.0), // Start waarde bus

        EMISSION_MULT_CAR(Double.class, 29.8), // Multiplier waarde auto
        EMISSION_MULT_VAN(Double.class, 19.0), // Multiplier waarde busje
        EMISSION_MULT_TRUCK(Double.class, 17.9), // Multiplier waarde truck
        EMISSION_MULT_BUS(Double.class, 17.9), // Multiplier waarde bus
        ;

        private final Class<?> type;
        private final double[] defaultArray;

        private TrafficNoiseAttribute(Class<?> type, double defaultValue) {
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

    private static final long serialVersionUID = 4911257565806176231L;

    @Override
    public int getDecimals() {
        return 2;
    }

    @Override
    protected NoiseResult getDefaultResult() {
        return NoiseResult.NOISE_DB;
    }

    @Override
    protected Class<NoiseResult> getResultClass() {
        return NoiseResult.class;
    }
}
