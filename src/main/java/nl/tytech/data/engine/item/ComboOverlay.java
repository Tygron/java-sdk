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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.ComboUtils;
import nl.tytech.core.util.ComboUtils.ComboException;
import nl.tytech.data.engine.item.ComboOverlay.Input;
import nl.tytech.data.engine.item.DefaultOverlay.DefaultResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.serializable.PrequelLink;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Simple combinatorial math grid overlay
 *
 * @author Maxim Knepfle
 */
public class ComboOverlay extends RasterizationOverlay<DefaultResult, Input> {

    public enum AttributeKey implements Key {

        A, B, C, D, E,

        F, G, H, I, J;

        public static final AttributeKey[] VALUES = AttributeKey.values();

        private final String txt;

        private AttributeKey() {
            this.txt = "@" + name();
        }

        @Override
        public UnitType getUnitType() {
            return null;
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public boolean isOutput() {
            return false;
        }

        @Override
        public String toString() {
            return txt;
        }
    }

    public enum ComboModelAttribute implements ReservedAttribute {

        ATTRIBUTE_DEFAULT_VALUE(Double.class, 0.0),

        ATTRIBUTE_SURFACE_ONLY(Double.class, 0.0);

        private final Class<?> type;

        private final double[] defaultArray;

        private ComboModelAttribute(Class<?> type, double defaultValue) {
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

    public enum Input implements PrequelType {

        A, B, C, D, E,

        F, G, H, I, J;

        public static final Input[] VALUES = Input.values();
    }

    public enum Operator {

        // single arguments
        SIN("SINE", 1, "Sine function"),

        COS("COSINE", 1, "Cosine function"),

        TAN("TANGENT", 1, "Tangent function"),

        ASIN(1, "Arc sine function"),

        ACOS(1, "Arc cosine function"),

        ATAN(1, "Arc tangent function"),

        SQRT("ROOT", 1, "The square root function"),

        ROUND(1, "Round function"),

        FLOOR(1, "Floor function"),

        CEIL(1, "Ceil function"),

        ABS(1, "Absolute function"),

        EXP(1, "Exponential function"),

        LN(1, "The natural logarithm (base e) function"),

        LOG("LOG10", 1, "The natural logarithm (base 10) function"),

        RANDOM("RAND", 1, "The random function, which returns a number greater than or equal to 0 and less than the provided argument"),

        ERF(1, "The gauss error function, for probability calculations"),

        // two arguments (boolean result)
        GTE(2, "Returns 1 if the first argument is larger than or equal to the second argument, else returns 0"),

        GT(2, "Returns 1 if the first argument is larger than the second argument, else returns 0"),

        LTE(2, "Returns 1 if the first argument is smaller than or equal to the second argument, else returns 0"),

        LT(2, "Returns 1 if the first argument is smaller than the second argument, else returns 0"),

        NEQ(2, "Returns 1 if the first argument is not equal to the second argument, else returns 0"),

        EQ("EQUALS", 2, "Returns 1 if the first argument is equal to second argument, else returns 0"),

        // three arguments
        IF(3, "If first argument is true (numerical value exactly 1), it returns the second argument, else it returns the third argument"),

        // multi arguments (boolean result)
        AND("Returns 1 if all arguments are not equal to 0"),

        OR("Returns 1 if any argument is not equal to 0"),

        // multi arguments
        SWITCH("Switch first argument is expression, second is default value and the rest are cases (case value and its result)."),

        ADD("Add one or more arguments"),

        MUL("MULT", "Multiply one or more arguments"),

        DIV("Divide the first argument with one or more other arguments"),

        SUB("Subtract one or more arguments from the first argument"),

        MAX("Returns the largest of the provided arguments. That is, the result is the argument closest to positive infinity."),

        MIN("Returns the smallest of the provided arguments. That is, the result is the argument closest to negative infinity."),

        AVG("AVERAGE", "Returns the arithmetic mean of the provided arguments"),

        POW("Raises the first argument to the power of the consecutive arguments. For example 2 to the power 3 to the power 4."),

        // percentiles
        PERCENTILE("Calculates the linear interpolated k-th percentile (value between 0 and 1) of the sorted array of provided arguments. "
                + "The last provided argument should be the percentile."),

        AVG_GTE_PERCENTILE("Calculates the average of the provided arguments which are above the calculated linear interpolated "
                + "k-th percentile (value between 0 and 1). The last provided argument should be the percentile."),

        AVG_LTE_PERCENTILE("Calculates the average of the provided arguments which are below the calculated linear interpolated "
                + "k-th percentile (value between 0 and 1). The last provided argument should be the percentile."),

        // Image operations
        COLOR("Takes R, G, B (and optional A values) between 0-255 as arguments and converts to color."),

        RED(1, "Takes a color from legend or pixel as argument and retrieves the red channel 0-255 value."),

        GREEN(1, "Takes a color from legend or pixel as argument and retrieves the green channel 0-255 value."),

        BLUE(1, "Takes a color from legend or pixel as argument and retrieves the blue channel 0-255 value."),

        ALPHA(1, "Takes a color from legend or pixel as argument and retrieves the alpha channel 0-255 value.");

        private final int fixedArguments;

        private final String explanation;

        private final String alternative;

        private Operator(int fixedArguments, String explanation) {
            this(null, fixedArguments, explanation);
        }

        private Operator(String explanation) {
            this(-1, explanation);
        }

        private Operator(String alternative, int fixedArguments, String explanation) {
            this.alternative = alternative;
            this.fixedArguments = fixedArguments;
            this.explanation = explanation;
        }

        private Operator(String alternative, String explaination) {
            this(alternative, -1, explaination);
        }

        public String getAlternative() {
            return alternative != null ? alternative : name();
        }

        public double getDefaultValue() {
            return this == RANDOM ? 1.0 : 0.0;
        }

        public String getExplanation() {
            return explanation;
        }

        public int getFixedArguments() {
            return fixedArguments;
        }

        public final boolean isColorChannel() {
            return this == RED || this == GREEN || this == BLUE || this == ALPHA;
        }

        public final boolean isFixedArguments() {
            return fixedArguments >= 0;
        }

        public final boolean isImageOp() {
            return this == COLOR || isColorChannel();
        }

        public final boolean isPercentileOp() {
            return this == PERCENTILE || this == AVG_GTE_PERCENTILE || this == AVG_LTE_PERCENTILE;
        }
    }

    private static final long serialVersionUID = 5024326114506247922L;

    @XMLValue
    private String formula = "";

    @Override
    protected final boolean calcPrequelTimeframes() {
        return isActive();
    }

    @Override
    public final boolean calcSelfPrequel() {
        return false; // due to possible Prequel getTimeframes() loop
    }

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {

        if (calcPrequelTimeframes()) { // only when active == calcPrequelTimeframes()
            try {
                // validate formula
                ItemMap<Global> globals = this.getMap(MapLink.GLOBALS);
                int timeframes = ComboUtils.testParse(this, globals.values(), cache, formula).getTimeframes();
                return MathUtils.clamp(timeframes, 1, getMaxTimeFrames());
            } catch (ComboException e) {
                // ignore invalid formula
            } catch (Exception e) {
                TLogger.exception(e);
            }
        }
        // fallback when inactive or invalid formula
        return super.calcTimeframes(cache);
    }

    public final String[] getAttributeKeys() {

        Set<String> attributes = new HashSet<>();
        for (AttributeKey key : AttributeKey.VALUES) {
            String attribute = getKey(key);
            if (StringUtils.containsData(attribute) && formula != null && formula.contains(key.toString())) {
                attributes.add(attribute);
            }
        }
        return attributes.toArray(String[]::new);
    }

    @Override
    protected DefaultOverlay.DefaultResult getDefaultResult() {
        return DefaultOverlay.DefaultResult.DEFAULT;
    }

    @Override
    public Style[] getDefaultStyles() {

        // most likely a COLOR type result
        if (formula != null && formula.contains(Operator.COLOR.name())) {
            return new Style[] { Style.COLOR };
        }

        for (Input input : Input.VALUES) {
            GridOverlay<?, ?> prequel = getPrequel(input);
            if (prequel != null && !prequel.hasPrequel(getID())) {
                return prequel.getDefaultStyles();
            }
        }
        return super.getDefaultStyles();
    }

    public String getFormula() {
        return formula;
    }

    @Override
    public Input[] getPrequelTypes() {
        return Input.VALUES;
    }

    @Override
    protected Class<DefaultOverlay.DefaultResult> getResultClass() {
        return DefaultOverlay.DefaultResult.class;
    }

    @Override
    public String getWarnings() {

        for (Input input : Input.VALUES) {
            PrequelLink link = getPrequelLink(input);
            if (link != null && link.isPreviousIteration() && !hasSequal(link.getOverlayID())) {
                return "Previous Iteration: " + input.name() + " not applicable.";
            }
        }
        return super.getWarnings();
    }

    @Override
    public boolean isPreviousIterationAllowed() {
        return true; // allow going back one iteration
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (formula != null && formula.contains("ATTRIBUTE")) {
            formula = formula.replaceAll("ATTRIBUTE", AttributeKey.A.toString());
        }
        return result;
    }
}
