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
package nl.tytech.core.util;

import static nl.tytech.data.engine.item.ComboOverlay.Operator.AVG_GTE_PERCENTILE;
import static nl.tytech.data.engine.item.ComboOverlay.Operator.AVG_LTE_PERCENTILE;
import static nl.tytech.data.engine.item.ComboOverlay.Operator.PERCENTILE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.ComboOverlay;
import nl.tytech.data.engine.item.ComboOverlay.AttributeKey;
import nl.tytech.data.engine.item.ComboOverlay.ComboModelAttribute;
import nl.tytech.data.engine.item.ComboOverlay.Input;
import nl.tytech.data.engine.item.ComboOverlay.Operator;
import nl.tytech.data.engine.item.Global;
import nl.tytech.data.engine.item.GridOverlay;
import nl.tytech.data.engine.item.GridOverlay.Style;
import nl.tytech.data.engine.other.Grid;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.ColorUtils;
import nl.tytech.util.color.LegendUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 *
 * Combo Overlay Formula Parsers Utils
 *
 * @author Maxim Knepfle
 */
public class ComboUtils {

    public static class Array {

        private final ComboGrid grid;
        private final Input input;
        private int start = 0;
        private int end = 0;

        public Array(ComboGrid grid) {
            this.grid = grid;
            this.input = grid.input;
        }

        @Override
        public String toString() {
            return end > start ? input.name() + start + ":" + end : grid.toString();
        }
    }

    public static class AttributeGrid implements ComboFormula {

        private final GridInterface gridInterface;

        private final AttributeKey key;

        private Grid grid;

        private AttributeGrid(GridInterface gridInterface, AttributeKey key) {
            this.gridInterface = gridInterface;
            this.key = key;
        }

        @Override
        public List<ComboGrid> getInputGrids() {
            return new ArrayList<>();
        }

        @Override
        public double getResult(int x, int y, int timeframe) {

            if (grid == null) { // init a valid sized grid
                grid = gridInterface.getAttributeGrid(key);
            }
            return grid.get(x, y);
        }

        @Override
        public int getTimeframes() {
            return 1; // no info on this
        }

        @Override
        public String toString() {
            return key.toString();
        }
    }

    public static class ComboException extends Exception {

        private static final long serialVersionUID = 5337434435182772560L;

        public ComboException(String message) {
            super(message);
        }
    }

    public static interface ComboFormula {

        public List<ComboGrid> getInputGrids();

        public double getResult(int x, int y, int timeframe) throws ComboException;

        public int getTimeframes();
    }

    public static class ComboFunction implements ComboFormula {

        private final Operator operator;

        private final List<ComboFormula> arguments;

        private ComboFunction(Operator operator, List<ComboFormula> arguments) {
            this.operator = operator;
            this.arguments = arguments;
        }

        public ComboFormula getArgument(int index) {
            return arguments.get(index);
        }

        public List<ComboFormula> getArguments() {
            return arguments;
        }

        private final double getColor(int x, int y, int tf) throws ComboException {

            // converts RGBA double values to single float ARGB value
            final int r = arguments.size() > 0 ? MathUtils.clamp((int) arguments.get(0).getResult(x, y, tf), 0, 255) : 0;
            final int g = arguments.size() > 1 ? MathUtils.clamp((int) arguments.get(1).getResult(x, y, tf), 0, 255) : 0;
            final int b = arguments.size() > 2 ? MathUtils.clamp((int) arguments.get(2).getResult(x, y, tf), 0, 255) : 0;
            final int a = arguments.size() > 3 ? MathUtils.clamp((int) arguments.get(3).getResult(x, y, tf), 0, 255) : 255;
            return ColorUtils.toFloatRGB(TColor.toARGB(r, g, b, a));
        }

        private final double getColorChannel(int x, int y, int tf) throws ComboException {

            final int argb; // get ARGB integer from legend or pixel value (e.g. WMS or Sat)
            if (arguments.isEmpty()) {
                argb = GridOverlay.NO_DATA_COLOR.getARGB();
            } else {
                final float value = (float) arguments.get(0).getResult(x, y, tf);
                if (arguments.get(0) instanceof ComboGrid cg && !cg.hasLegend()) {
                    argb = LegendUtils.getARGB(value); // WMS or Sat: pixel has no NO_DATA
                } else if (value <= GridOverlay.NO_DATA) {
                    argb = GridOverlay.NO_DATA_COLOR.getARGB(); // NO_DATA
                } else if (arguments.get(0) instanceof ComboGrid cg && cg.hasLegend() && !cg.getLegend().isEmpty()) {
                    argb = LegendUtils.getARGB(cg.getLegend(), value); // Legend
                } else {
                    argb = LegendUtils.getARGB(value); // other values, e.g. variables
                }
            }
            return switch (operator) { // convert to channel
                case RED -> ColorUtils.getRed(argb);
                case GREEN -> ColorUtils.getGreen(argb);
                case BLUE -> ColorUtils.getBlue(argb);
                case ALPHA -> ColorUtils.getAlpha(argb);
                default -> 0.0;
            };
        }

        @Override
        public List<ComboGrid> getInputGrids() {

            List<ComboGrid> grids = new ArrayList<>();
            for (ComboFormula argument : getArguments()) {
                grids.addAll(argument.getInputGrids());
            }
            return grids;
        }

        public Operator getOperator() {
            return operator;
        }

        private double getPercentileOp(int x, int y, int tf) throws ComboException {

            // check for valid values
            if (arguments.size() <= 1) {
                throw new ComboException("Missing percentile arguments in: " + toString() + " at location: " + x + " x " + y);
            }
            double percentile = arguments.get(arguments.size() - 1).getResult(x, y, tf);
            if (percentile < 0 || percentile > 1) {
                throw new ComboException("Percentile must be in range [0-1] in: " + toString() + " at location: " + x + " x " + y);
            }
            double[] array = new double[arguments.size() - 1];
            for (int i = 0; i < array.length; i++) {
                array[i] = arguments.get(i).getResult(x, y, tf);
            }

            // just simple percentile
            double p = MathUtils.percentile(array, percentile);
            if (operator == PERCENTILE) {
                return p;
            }

            // calculate average
            double sum = 0.0;
            int count = 0;
            for (int i = 0; i < array.length; i++) {
                double v = array[i];
                if (operator == AVG_GTE_PERCENTILE && v >= p || operator == AVG_LTE_PERCENTILE && v <= p) {
                    sum += v;
                    count++;
                }
            }
            return count == 0 ? 0.0 : sum / count;
        }

        @Override
        public double getResult(int x, int y, int tf) throws ComboException {

            // evaluate percentile function
            if (operator.isPercentileOp()) {
                return getPercentileOp(x, y, tf);
            }

            // evaluate color
            if (operator == Operator.COLOR) {
                return getColor(x, y, tf);
            }

            // evaluate color channel
            if (operator.isColorChannel()) {
                return getColorChannel(x, y, tf);
            }

            // start with first value
            double result = arguments.isEmpty() ? 0.0 : arguments.get(0).getResult(x, y, tf);

            // evaluate IF with 3 arguments
            if (operator == Operator.IF) {
                return arguments.get(result == TRUE ? 1 : 2).getResult(x, y, tf);
            }

            // evaluate switch (expression, default, cases)
            if (operator == Operator.SWITCH) {
                return getSwitchOp(result, x, y, tf);
            }

            // single argument calls
            if (operator.getFixedArguments() == 1) {
                switch (operator) {
                    case SIN:
                        return Math.sin(result);
                    case COS:
                        return Math.cos(result);
                    case TAN:
                        return Math.tan(result);
                    case ACOS:
                        return Math.acos(result);
                    case ASIN:
                        return Math.asin(result);
                    case ATAN:
                        return Math.atan(result);
                    case ROUND:
                        return Math.round(result);
                    case FLOOR:
                        return Math.floor(result);
                    case CEIL:
                        return Math.ceil(result);
                    case ABS:
                        return Math.abs(result);
                    case EXP:
                        return Math.exp(result);
                    case LN:
                        if (result == 0.0) {
                            throw new ComboException("Log of zero is not defined in: " + toString() + " at location: " + x + " x " + y);
                        }
                        return Math.log(result); // natural log == ln
                    case LOG:
                        if (result == 0.0) {
                            throw new ComboException("Log of zero is not defined in: " + toString() + " at location: " + x + " x " + y);
                        }
                        return Math.log10(result);
                    case RANDOM:
                        return Math.random() * result;
                    case ERF:
                        return MathUtils.erf(result);
                    case SQRT:
                        if (result < 0.0) {
                            throw new ComboException("Cannot take negative root in: " + toString() + " at location: " + x + " x " + y);
                        }
                        return Math.sqrt(result);
                    default:
                        TLogger.severe("Missing implementation for: " + operator);
                        return 0.0;
                }
            }

            // now do the rest
            for (int i = 1; i < arguments.size(); i++) {
                double value = arguments.get(i).getResult(x, y, tf);
                switch (operator) {
                    case ADD:
                    case AVG:
                        result += value;
                        break;
                    case SUB:
                        result -= value;
                        break;
                    case MUL:
                        result *= value;
                        break;
                    case DIV:
                        if (value == 0.0) {
                            throw new ComboException("Division by Zero in: " + this.toString() + " at location: " + x + " x " + y);
                        }
                        result /= value;
                        break;
                    case MAX:
                        result = Math.max(result, value);
                        break;
                    case MIN:
                        result = Math.min(result, value);
                        break;
                    case POW:
                        result = Math.pow(result, value);
                        break;
                    case GT:
                        result = result > value ? TRUE : FALSE;
                        break;
                    case GTE:
                        result = result >= value ? TRUE : FALSE;
                        break;
                    case LT:
                        result = result < value ? TRUE : FALSE;
                        break;
                    case LTE:
                        result = result <= value ? TRUE : FALSE;
                        break;
                    case EQ:
                        result = result == value ? TRUE : FALSE;
                        break;
                    case NEQ:
                        result = result != value ? TRUE : FALSE;
                        break;
                    case AND:
                        result = result != 0.0 && value != 0.0 ? TRUE : FALSE;
                        break;
                    case OR:
                        result = result != 0.0 || value != 0.0 ? TRUE : FALSE;
                        break;
                    default:
                        TLogger.severe("Missing implementation for: " + operator);
                        return 0.0;
                }
            }

            // avg needs division
            if (operator == Operator.AVG && !arguments.isEmpty()) {
                result /= arguments.size();
            }
            return result;
        }

        private final double getSwitchOp(double expression, int x, int y, int tf) throws ComboException {

            // check for valid values
            if (arguments.size() < 2) {
                throw new ComboException("Switch requires at least two arguments: expression and default value in: " + toString());
            }
            // check for even arguments
            if (arguments.size() % 2 != 0) {
                throw new ComboException("Switch requires two arguments per case: case value and its result in: " + toString());
            }

            // select case value based on expression argument
            for (int i = 2; i < arguments.size(); i += 2) {
                if (arguments.get(i).getResult(x, y, tf) == expression) {
                    return arguments.get(i + 1).getResult(x, y, tf);
                }
            }

            // otherwise fallback to default
            return arguments.get(1).getResult(x, y, tf);
        }

        @Override
        public int getTimeframes() {

            // get maximal time frame
            int timeframes = 1;
            for (ComboFormula argument : arguments) {
                timeframes = Math.max(timeframes, argument.getTimeframes());
            }
            return timeframes;
        }

        @Override
        public String toString() {

            StringBuilder builder = new StringBuilder();
            builder.append(operator.name());
            builder.append("(");
            Array array = null;

            for (int i = 0; i < arguments.size(); i++) {
                boolean more = i < arguments.size() - 1;
                boolean single = true;
                if (arguments.get(i) instanceof ComboGrid grid && grid.fixedTimeframe != null) {
                    // extend existing array
                    if (array != null && array.input == grid.input && array.end == grid.fixedTimeframe - 1) {
                        array.end = grid.fixedTimeframe;
                        single = false;
                        if (more) {
                            continue;
                        }
                    } else if (more) {
                        // safe old
                        if (array != null) {
                            builder.append(array);
                            builder.append(more ? ", " : "");
                            array = null;
                        }
                        // start new
                        array = new Array(grid);
                        array.start = grid.fixedTimeframe;
                        array.end = grid.fixedTimeframe;
                        single = false;
                        continue;
                    }
                }
                if (array != null) {
                    builder.append(array);
                    builder.append(single || more ? ", " : "");
                    array = null;
                }
                if (single) {
                    builder.append(arguments.get(i).toString());
                    builder.append(more ? ", " : "");
                }
            }
            builder.append(")");
            return builder.toString();
        }
    }

    public static class ComboGrid implements ComboFormula {

        private final Input input;

        private final GridInterface gridInterface;

        private final Integer fixedTimeframe;

        private final MapType mapType;

        private Grid grid;

        private int gridTF = -1;

        private final int timeframes;

        private ComboGrid(Input input, MapType mapType, GridInterface gridInterface, int timeframes, Integer fixedTimeframe) {

            this.input = input;
            this.mapType = mapType;
            this.gridInterface = gridInterface;
            this.fixedTimeframe = fixedTimeframe;
            this.timeframes = fixedTimeframe == null ? timeframes : 1;
        }

        public Input getInput() {
            return input;
        }

        @Override
        public List<ComboGrid> getInputGrids() {
            return Arrays.asList(this);
        }

        public final List<LegendEntry> getLegend() {
            return gridInterface.get(input).getLegend();
        }

        @Override
        public double getResult(int x, int y, int timeframe) {

            int tf = fixedTimeframe == null ? MathUtils.clamp(timeframe, 0, timeframes - 1) : fixedTimeframe;
            if (grid == null || gridTF != tf) { // init a valid sized grid
                grid = gridInterface.getGrid(input, mapType, tf);
                gridTF = tf;
            }
            return grid.get(x, y);
        }

        public final List<Integer> getTimeframeIndexes() {

            if (fixedTimeframe != null) {
                return Arrays.asList(fixedTimeframe);
            }
            List<Integer> indexes = new ArrayList<>();
            for (int t = 0; t < timeframes; t++) {
                indexes.add(t);
            }
            return indexes;
        }

        @Override
        public int getTimeframes() {
            return timeframes;
        }

        public final boolean hasLegend() {
            return Style.hasLegend(gridInterface.get(input).getDefaultStyles());
        }

        @Override
        public String toString() {

            String result = input.name();
            if (fixedTimeframe == null) {
                result += TIMEFRAME;
            } else if (fixedTimeframe != gridInterface.get(input).getLastTimeframe()) {
                result += Integer.toString(fixedTimeframe);
            }
            return result;
        }
    }

    public static class ComboVariable implements ComboFormula {

        private final double value;

        private ComboVariable(double value) {
            this.value = value;
        }

        @Override
        public List<ComboGrid> getInputGrids() {
            return new ArrayList<>();
        }

        @Override
        public double getResult(int x, int y, int timeframe) {
            return getValue();
        }

        @Override
        public int getTimeframes() {
            return 1; // no info on this
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {

            // show as integer
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                return Integer.toString((int) value);
            }
            // show as double
            return Double.toString(value);
        }
    }

    public static class DXVariable extends ComboVariable {

        private DXVariable(double dxValue) {
            super(dxValue);
        }

        @Override
        public String toString() {
            return DX;
        }
    }

    public interface GlobalInterface {

        public Global getByName(String name);
    }

    public static class GlobalVariable implements ComboFormula {

        private final Global global;

        private GlobalVariable(Global global) {
            this.global = global;
        }

        @Override
        public List<ComboGrid> getInputGrids() {
            return new ArrayList<>();
        }

        @Override
        public double getResult(int x, int y, int timeframe) throws ComboException {
            return global.getActualValue(timeframe);
        }

        @Override
        public int getTimeframes() {
            return global.getActualValue().length;
        }

        @Override
        public String toString() {
            return GLOBAL + global.getName();
        }
    }

    public interface GridInterface {

        public GridOverlay<?, ?> get(Input input);

        public Grid getAttributeGrid(AttributeKey key);

        public Grid getGrid(Input input, MapType mapType, int timeframe);

        public int getIteration();

        public ComboOverlay getMyCombo();

    }

    public static class IterationVariable extends ComboVariable {

        private IterationVariable(int iterationValue) {
            super(iterationValue);
        }

        @Override
        public String toString() {
            return ITERATION;
        }
    }

    public static class NoDataVariable extends ComboVariable {

        private NoDataVariable() {
            super(GridOverlay.NO_DATA);
        }

        @Override
        public String toString() {
            return NO_DATA;
        }
    }

    private record TestGlobalInterface(Collection<Global> globals) implements GlobalInterface {

        @Override
        public Global getByName(String name) {

            for (Global global : globals) {
                if (global.getName().equals(name)) {
                    return global;
                }
            }
            return null;
        }
    }

    private record TestGridInterface(ComboOverlay combo) implements GridInterface {

        @Override
        public GridOverlay<?, ?> get(Input input) {
            return combo.getPrequel(input);
        }

        @Override
        public Grid getAttributeGrid(AttributeKey key) {
            throw new UnsupportedOperationException(); // test has no attribute grid data
        }

        @Override
        public Grid getGrid(Input input, MapType mapType, int timeframe) {
            throw new UnsupportedOperationException(); // test has no grid data
        }

        @Override
        public int getIteration() {
            return 0;
        }

        @Override
        public ComboOverlay getMyCombo() {
            return combo;
        }
    }

    public static final double TRUE = 1.0;

    public static final double FALSE = 0.0;

    private static final Pattern REGEX_FRAMES_INPUT = Pattern.compile("([a-zA-Z]+)(\\d+):(\\d+)");

    private static final Pattern REGEX_NUMBER = Pattern.compile("[0-9]+");

    public static final String TIMEFRAME = "T";

    public static final String GLOBAL = "GLOBAL_";

    public static final String NO_DATA = "NO_DATA";

    public static final String DX = "DX";

    public static final String ITERATION = "ITERATION";

    private static final Map<String, Operator> operatorMap = new ConcurrentHashMap<>();
    static {
        for (Operator t : Operator.values()) {
            operatorMap.put(t.name(), t);
            operatorMap.put(t.getAlternative(), t);
        }
    }

    private static final List<String> getArguments(String formula) throws ComboException {

        int counter = 0;
        List<String> list = new ArrayList<>();
        try {
            String[] parts = formula.substring(formula.indexOf("(") + 1, formula.length() - 1).split(",");
            String sub = null;
            for (String part : parts) {
                counter += part.chars().filter(ch -> ch == '(').count();
                counter -= part.chars().filter(ch -> ch == ')').count();
                sub = sub == null ? part : sub + "," + part;

                if (counter == 0) {
                    // split multi timeframes into seperate arguments
                    Matcher m = REGEX_FRAMES_INPUT.matcher(sub);
                    if (getOperator(sub) == null && m.find()) {
                        String op = m.group(1);
                        int start = Integer.parseInt(m.group(2));
                        int end = Integer.parseInt(m.group(3));
                        for (int i = start; i <= end; i++) {
                            list.add(op + i);
                        }
                    } else {
                        list.add(sub);
                    }
                    sub = null;
                }
            }
        } catch (Exception e) {
            throw new ComboException("Invalid arguments in: " + formula);
        }
        if (counter != 0) {
            throw new ComboException("Missing brackets in: " + formula);
        }
        return list;
    }

    public static final String getFeedback(ComboOverlay overlay, Collection<Global> globals, String part) {
        return getFeedback(new TestGridInterface(overlay), globals, part);
    }

    public static final String getFeedback(GridInterface gridInterface, Collection<Global> globals, String part) {

        // nothing
        if (!StringUtils.containsData(part)) {
            return null;
        }

        // force upper
        part = part.toUpperCase();

        // no data
        if (ComboUtils.NO_DATA.equals(part)) {
            return "No Data value";
        }

        // DX value
        if (ComboUtils.DX.equals(part)) {
            String dx = UnitSystemType.SI.getImpl().toLocalValueWithUnit(gridInterface.getMyCombo().getCellSizeM(), UnitType.LENGTH);
            return "Combo grid cell size (" + dx + ")";
        }

        // Iteration value
        if (ComboUtils.ITERATION.equals(part)) {
            return "Simulation iteration index";
        }

        // Attribute value
        for (AttributeKey key : AttributeKey.VALUES) {
            if (key.toString().equals(part)) {
                ComboOverlay c = gridInterface.getMyCombo();
                if (StringUtils.containsData(c.getKey(key))) {
                    String mapLinkName = c.getLayerMapLink() == null ? "" : " " + c.getLayerMapLink().name();
                    return c.getRasterization() + mapLinkName + ": " + c.getKey(key);
                } else {
                    String d = UnitSystemType.SI.getImpl().formatLocalValue(c.getAttribute(ComboModelAttribute.ATTRIBUTE_DEFAULT_VALUE));
                    return "Attribute " + key.name() + " is not set, revert to default value: " + d;
                }
            }
        }

        // timeframe
        if (ComboUtils.TIMEFRAME.equals(part)) {
            return "Run formula per timeframe, creates a multi-timeframe overlay";
        }

        // globals
        if (part.startsWith(ComboUtils.GLOBAL)) {
            String name = part.substring(ComboUtils.GLOBAL.length());
            for (Global global : globals) {
                if (global.getName().equals(name)) {
                    return "Global: " + name + "\n\nActual value: "
                            + UnitSystemType.SI.getImpl().formatLocalValues(global.getActualValue(), Global.DECIMALS);
                }
            }
            return "INVALID Global: " + name;
        }

        // match to operator
        Operator op = operatorMap.get(part);
        if (op != null) {
            String txt = op.getAlternative() + " Operator\n\n" + op.getExplanation();
            if (!op.name().equals(op.getAlternative())) {
                txt += "\nAlternative names: " + op.getAlternative() + ", " + op.name();
            }
            return txt;
        }

        ComboOverlay me = gridInterface.getMyCombo();
        Map<Integer, Integer> cache = new TreeMap<>();

        // match to input
        for (Input input : Input.VALUES) {
            if (part.startsWith(input.name())) {
                if (gridInterface == null || gridInterface.get(input) == null) {
                    return input.name() + ": UNDEFINED";
                }
                GridOverlay<?, ?> overlay = gridInterface.get(input);
                int timeframes = me != null ? me.getPrequelTimeframes(input, cache) : overlay.getTimeframes();
                String name = input.name() + ": " + overlay + "\n\n";
                if (part.endsWith(ComboUtils.TIMEFRAME)) {
                    return name + "For each Timeframe (" + timeframes + ")";
                }
                String number = part.substring(input.name().length());
                if (!REGEX_NUMBER.matcher(number).matches()) {
                    if (timeframes > 1) {
                        return name + "Timeframe: " + (timeframes - 1);
                    } else {
                        return name.trim();
                    }
                }
                int frame = Integer.parseInt(number);
                if (frame >= 0 && frame < timeframes) {
                    return name + "Timeframe: " + frame;
                } else {
                    return name + "INVALID Timeframe: " + frame;
                }
            }
        }

        // nothing
        return null;
    }

    private static final Operator getOperator(String formula) throws ComboException {

        // check for starting bracket
        int bracketStart = formula.indexOf("(");
        if (bracketStart <= 0) {
            return null;// no operator
        }

        // parse operator text before bracket
        String opPart = formula.substring(0, bracketStart);
        Operator t = operatorMap.get(opPart);
        if (t == null) {
            throw new ComboException("Invalid Operator: " + opPart);
        }
        return t;
    }

    private static final ComboFormula getVariable(Operator parent, MapType mapType, GridInterface gridInterface,
            GlobalInterface globalInterface, Map<Integer, Integer> cache, String formula) throws ComboException {

        // null or empty is zero
        if (!StringUtils.containsData(formula)) {
            return new ComboVariable(parent != null ? parent.getDefaultValue() : 0.0);
        }

        // first as no data variable
        if (formula.equalsIgnoreCase(NO_DATA)) {
            return new NoDataVariable();
        }
        // first as DX variable
        if (formula.equalsIgnoreCase(DX)) {
            return new DXVariable(gridInterface.getMyCombo().getCellSizeM());
        }
        // first as Iteration variable
        if (formula.equalsIgnoreCase(ITERATION)) {
            return new IterationVariable(gridInterface.getIteration());
        }
        // first as Attribute variable
        for (AttributeKey key : AttributeKey.VALUES) {
            if (formula.equalsIgnoreCase(key.toString())) {
                ComboOverlay me = gridInterface != null ? gridInterface.getMyCombo() : null;
                if (me != null && !StringUtils.containsData(me.getKey(key))) {
                    throw new ComboException("No Attribute linked to: " + key.toString());
                }
                return new AttributeGrid(gridInterface, key);
            }
        }

        // second as global value
        if (formula.startsWith(GLOBAL)) {
            String globalName = formula.substring(GLOBAL.length());
            Global global = globalInterface != null ? globalInterface.getByName(globalName) : null;
            if (global == null) {
                throw new ComboException("Invalid Global: " + globalName);
            }
            return new GlobalVariable(global);
        }

        // third as input grid
        for (Input input : Input.VALUES) {
            if (formula.startsWith(input.name())) {

                // check if available
                GridOverlay<?, ?> overlay = gridInterface != null ? gridInterface.get(input) : null;
                if (overlay == null) {
                    throw new ComboException("No Grid Overlay linked to: " + input.name());
                }
                // check for input loops
                ComboOverlay me = gridInterface != null ? gridInterface.getMyCombo() : null;
                if (me != null && !overlay.isValidPrequelTo(me, me.getPrequelLink(input).isPreviousIteration())) {
                    throw new ComboException("Input loop, this Combo is already used in Input: " + input.name());
                }

                // calculate cached time frames
                int timeframes = me != null ? me.getPrequelTimeframes(input, cache) : overlay.getTimeframes();
                Integer fixedTimeframe = timeframes - 1;

                if (!formula.equals(input.name())) {
                    try {
                        String f = formula.substring(input.name().length());
                        fixedTimeframe = TIMEFRAME.equals(f) ? null : Integer.parseInt(f);
                    } catch (Exception e) {
                        throw new ComboException("Invalid Timeframe: " + formula);
                    }
                    if (fixedTimeframe != null && (fixedTimeframe < 0 || fixedTimeframe >= timeframes)) {
                        throw new ComboException("Invalid Timeframe: " + fixedTimeframe + " range [0-" + (timeframes - 1) + "].");
                    }
                }
                // time frames count from 1 for user
                return new ComboGrid(input, mapType, gridInterface, timeframes, fixedTimeframe);
            }
        }

        // fourth as variable
        try {
            return new ComboVariable(Double.parseDouble(formula));
        } catch (Exception e) {
            throw new ComboException("Invalid argument: " + formula);
        }
    }

    private static final ComboFormula innerParse(Operator parent, MapType mapType, GridInterface gridInterface,
            GlobalInterface globalInterface, Map<Integer, Integer> cache, String formula) throws ComboException {

        // get operator
        Operator operator = getOperator(formula);
        if (operator == null) {
            return getVariable(parent, mapType, gridInterface, globalInterface, cache, formula);
        }

        // get arguments
        List<String> arguments = getArguments(formula);
        List<ComboFormula> subFunctions = new ArrayList<>();
        for (String argument : arguments) {
            subFunctions.add(innerParse(operator, mapType, gridInterface, globalInterface, cache, argument));
        }

        // check valid single arguments
        if (operator.isFixedArguments() && operator.getFixedArguments() != arguments.size()) {
            throw new ComboException("Operator: " + operator.name() + " requires: " + operator.getFixedArguments() + " argument(s).");
        }

        // done
        return new ComboFunction(operator, subFunctions);
    }

    private static final ComboFormula parse(MapType mapType, GridInterface gridInterface, GlobalInterface globalInterface,
            Map<Integer, Integer> cache, String formula) throws ComboException {

        // null is zero
        if (!StringUtils.containsData(formula)) {
            return new ComboVariable(0.0);
        }
        // fix caps
        formula = formula.toUpperCase();

        // fix spaces
        formula = StringUtils.internalTrim(formula, true);

        // default map type
        mapType = mapType != null ? mapType : MapType.CURRENT;

        // do actual parsing
        return innerParse(null, mapType, gridInterface, globalInterface, cache, formula);
    }

    public static final ComboFormula parse(MapType mapType, GridInterface gridInterface, GlobalInterface globalInterface, String formula)
            throws ComboException {
        return parse(mapType, gridInterface, globalInterface, new TreeMap<>(), formula);
    }

    public static final ComboFormula parse(MapType mapType, GridInterface gridInterface, String formula) throws ComboException {
        return parse(mapType, gridInterface, null, formula);
    }

    public static final ComboFormula testParse(ComboOverlay overlay, Collection<Global> globals, Map<Integer, Integer> cache,
            String formula) throws ComboException {
        return parse(MapType.CURRENT, new TestGridInterface(overlay), new TestGlobalInterface(globals), cache, formula);
    }

    public static final ComboFormula testParse(ComboOverlay overlay, Collection<Global> globals, String formula) throws ComboException {
        return testParse(overlay, globals, new TreeMap<>(), formula);
    }
}
