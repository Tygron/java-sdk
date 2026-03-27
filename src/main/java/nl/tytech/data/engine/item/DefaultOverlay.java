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

import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.StringUtils;

/**
 * Default Grid Overlay
 *
 * @author Maxim Knepfle
 *
 */
public class DefaultOverlay extends GridOverlay<DefaultOverlay.DefaultResult, GridOverlay.NoPrequel> {

    public enum DefaultAttribute implements ReservedAttribute {

        /**
         * Default Attribute
         */
        ATTRIBUTE(Double.class, 0.0),

        ;

        private final double[] defaultValues;
        private final Class<?> type;

        private DefaultAttribute(Class<?> type, double... defaultValue) {
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

    public enum DefaultResult implements ResultType {

        DEFAULT;

        private DefaultResult() {
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

    private static final long serialVersionUID = -1445005439893527195L;

    @Override
    protected DefaultOverlay.DefaultResult getDefaultResult() {
        return DefaultOverlay.DefaultResult.DEFAULT;
    }

    @Override
    public NoPrequel[] getPrequelTypes() {
        return NoPrequel.VALUES;
    }

    @Override
    protected Class<DefaultOverlay.DefaultResult> getResultClass() {
        return DefaultOverlay.DefaultResult.class;
    }
}
