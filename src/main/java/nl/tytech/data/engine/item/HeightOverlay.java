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

import nl.tytech.data.engine.item.GridOverlay.NoPrequel;
import nl.tytech.data.engine.other.ResultType;

/**
 * Height based on a grid
 *
 * @author Maxim Knepfle
 */
public class HeightOverlay extends GridOverlay<HeightOverlay.HeightResult, NoPrequel> {

    public enum HeightAttribute implements ReservedAttribute {

        /**
         * Count floors only when zoning permit is required
         */
        ZONING_PERMIT_REQUIRED(Integer.class, 1);

        private final double[] defaultValues;
        private final Class<?> type;

        private HeightAttribute(Class<?> type, double... defaultValue) {
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

    public enum HeightResult implements ResultType {

        /**
         * Digital Surface Model (including buildings)
         */
        DSM("Digital Surface Model"),

        /**
         * Digital Terrain Model (height on ground)
         */
        DTM("Digital Terrain Model"),

        /**
         * Building floors
         */
        FLOORS("Building Floors");

        private final String title;

        private HeightResult(String title) {
            this.title = title;
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
            return title;
        }
    }

    private static final long serialVersionUID = -2572949484529750428L;

    @Override
    protected HeightResult getDefaultResult() {
        return HeightResult.DSM;
    }

    @Override
    public NoPrequel[] getPrequelTypes() {
        return NoPrequel.VALUES;
    }

    @Override
    protected Class<HeightResult> getResultClass() {
        return HeightResult.class;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (this.hasAttribute("INCLUDE_BUILDINGS")) {
            double value = this.getAttribute("INCLUDE_BUILDINGS");
            this.setResultType(value > 0 ? HeightResult.DSM : HeightResult.DTM);
            this.removeAttribute("INCLUDE_BUILDINGS", true);
        }
        return result;
    }
}
