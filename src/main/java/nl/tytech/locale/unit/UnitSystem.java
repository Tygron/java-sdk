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
package nl.tytech.locale.unit;

import java.text.NumberFormat;
import java.text.ParseException;
import nl.tytech.locale.TCurrency;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public abstract class UnitSystem {

    public static final String SEPERATOR = " / ";

    private static final double EPSILON = 1e-10d;

    /**
     * Default min decimals
     */
    private static final int MIN_DECIMALS = 0;

    /**
     * Default max decimals
     */
    private static final int MAX_DECIMALS = 3;

    protected abstract UnitSystem create();

    public final String formatLocalValue(double localValue) {
        return formatLocalValue(localValue, MAX_DECIMALS);
    }

    public final String formatLocalValue(double localValue, int maxDecimals) {
        return formatLocalValue(localValue, MIN_DECIMALS, maxDecimals);
    }

    public final String formatLocalValue(double localValue, int minDecimals, int maxDecimals) {
        return getLocalNumberFormatter(minDecimals, maxDecimals).format(localValue);
    }

    public final String formatLocalValue(Number number) {
        return getLocalNumberFormatter().format(number);
    }

    public final String formatLocalValues(double[] localValues) {
        return formatLocalValues(localValues, MAX_DECIMALS);
    }

    public final String formatLocalValues(double[] localValues, int maxDecimals) {
        return formatLocalValues(localValues, MIN_DECIMALS, maxDecimals);
    }

    public final String formatLocalValues(double[] localValues, int minDecimals, int maxDecimals) {

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < localValues.length; i++) {
            builder.append(formatLocalValue(localValues[i], minDecimals, maxDecimals));
            if (i < localValues.length - 1) {
                builder.append(StringUtils.WHITESPACE);
            }
        }
        return builder.toString();
    }

    protected abstract NumberFormat getLocalNumberFormatter();

    private final NumberFormat getLocalNumberFormatter(int minDecimals, int maxDecimals) {

        NumberFormat nf = getLocalNumberFormatter();
        nf.setMinimumFractionDigits(minDecimals);
        nf.setMaximumFractionDigits(maxDecimals);
        return nf;
    }

    protected abstract LocalUnit getLocalUnit(UnitType unit);

    private double getLocalValueWithAdjustedNotation(double siValue, LocalUnit localUnit) {
        return localUnit.toLocalValue(siValue) / localUnit.getRelativeSingleUnitValue();
    }

    private final LocalUnit getSignificantLocalUnit(double siValue, UnitType units) {

        LocalUnit localUnit = getLocalUnit(units);
        siValue = Math.abs(siValue);
        for (LocalUnit significantOrder : localUnit.getValues()) {
            if (Math.abs(siValue) < significantOrder.getMaxValue()) {
                return significantOrder;
            }
        }
        return localUnit.getDefault();
    }

    private final String getSignPrefix(double value) {
        return value > 0 ? "+" : StringUtils.EMPTY;
    }

    public final String getUnitAbbreviation(boolean multi, UnitType... units) {

        if (units == null) {
            throw new IllegalArgumentException("Missing units array!");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < units.length; i++) {
            if (i == 0) {
                result.append(getLocalUnit(units[i]).getPostFix(multi));
            } else {
                result.append(SEPERATOR + getLocalUnit(units[i]).getPostFix(false));
            }
        }
        return result.toString();
    }

    public final String getUnitAbbreviation(TCurrency currency, boolean multi, UnitType... units) {
        return currency.getCurrencyCharacter() + getUnitAbbreviation(multi, units);
    }

    public final String getUnitAbbreviation(TCurrency currency, UnitType... units) {
        return getUnitAbbreviation(currency, false, units);
    }

    public final String getUnitAbbreviation(UnitType... units) {
        return getUnitAbbreviation(false, units);
    }

    protected abstract UnitSystemType getUnitSystem();

    private boolean isMulti(double localValue) {
        return localValue == 0.0 || localValue > 1.0 || localValue < -1.0;
    }

    public final double parseDouble(String text) throws ParseException {
        return parseDouble(text, MAX_DECIMALS);
    }

    public final double parseDouble(String text, int maxDecimals) throws ParseException {

        double value = getLocalNumberFormatter(MIN_DECIMALS, maxDecimals).parse(text).doubleValue();
        if (!Double.isFinite(value)) {
            throw new ParseException("Only finite numbers allowed", 0);
        }
        return value;
    }

    public final double[] parseDoubles(String text) throws ParseException {
        return parseDoubles(text, MAX_DECIMALS);
    }

    public final double[] parseDoubles(String text, int maxDecimals) throws ParseException {

        String[] split = StringUtils.split(text);
        double[] array = new double[split.length];

        for (int i = 0; i < split.length; i++) {
            array[i] = parseDouble(split[i], maxDecimals);
        }
        return array;
    }

    public final float parseFloat(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).floatValue();
    }

    public final float parseFloat(String text, int decimals) throws ParseException {
        return getLocalNumberFormatter(0, decimals).parse(text).floatValue();
    }

    public final int parseInt(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).intValue();
    }

    public final int parseInteger(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).intValue();
    }

    public final long parseLong(String text) throws ParseException {
        return getLocalNumberFormatter().parse(text).longValue();
    }

    private final boolean significantlyDifferent(double value1, double value2, double significance) {
        return Math.abs(value1 - value2) + EPSILON > significance;
    }

    public final boolean significantlyDifferent(double value1, double value2, int minDecimals) {
        return significantlyDifferent(value1, value2, Math.pow(10d, -1d * minDecimals));
    }

    public final boolean significantlyDifferent(double value1, double value2, UnitType... units) {
        double significance = units.length > 0 ? Double.MAX_VALUE : UnitType.NONE.getSignificance();
        for (UnitType unit : units) {
            significance = Math.min(unit.getSignificance(), significance);
        }
        return significantlyDifferent(value1, value2, significance);
    }

    public final double toLocalValue(double siValue, boolean round, boolean useSignificantLocalUnit, UnitType unitDimensionType) {
        double value = toLocalValue(siValue, useSignificantLocalUnit, unitDimensionType);
        return round ? MathUtils.round(value, unitDimensionType.getRoundingDecimalPosition()) : value;
    }

    private final double toLocalValue(double siValue, boolean useSignificantLocalUnit, UnitType unitDimensionType) {

        LocalUnit unitDimension = useSignificantLocalUnit ? getSignificantLocalUnit(siValue, unitDimensionType)
                : getLocalUnit(unitDimensionType);
        return getLocalValueWithAdjustedNotation(siValue, unitDimension);
    }

    public final double toLocalValue(double siValue, boolean round, UnitType... units) {
        double value = toLocalValue(siValue, units);
        int decimals = 1;
        if (units.length > 0) {
            decimals = units[0].getRoundingDecimalPosition();
        }
        return round ? MathUtils.round(value, decimals) : value;
    }

    public final double toLocalValue(double siValue, int decimals, boolean useSignificantLocalUnit, UnitType... unitDimensionType) {
        // TODO: This is incorrect, useSignificalLocalUnit is used for rounding...
        double value = toLocalValue(siValue, useSignificantLocalUnit, unitDimensionType);
        return MathUtils.round(value, decimals);
    }

    public final double toLocalValue(double siValue, int decimals, UnitType... units) {
        return MathUtils.round(toLocalValue(siValue, units), decimals);
    }

    public final double toLocalValue(double siValue, UnitType... units) {

        if (units == null) {
            throw new IllegalArgumentException("Missing units array!");
        }

        double value = siValue;

        for (int i = 0; i < units.length; i++) {
            LocalUnit localUnit = getLocalUnit(units[i]);

            if (i == 0) {
                value = getLocalValueWithAdjustedNotation(siValue, localUnit);
            } else {
                value /= getLocalValueWithAdjustedNotation(1d, localUnit);
            }
        }
        return value;
    }

    public final String toLocalValuesWithUnit(double[] siValues, UnitType... units) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < siValues.length; i++) {
            double localValue = toLocalValue(siValues[i], units);
            builder.append(formatLocalValue(localValue) + StringUtils.WHITESPACE + getUnitAbbreviation(isMulti(localValue), units));
            if (i < siValues.length - 1) {
                builder.append(StringUtils.WHITESPACE);
            }
        }
        return builder.toString();
    }

    public final String toLocalValueWithFormatting(double siValue, boolean round, boolean useSignificantLocalUnit, UnitType unit) {
        return formatLocalValue(toLocalValue(siValue, round, useSignificantLocalUnit, unit));
    }

    public final String toLocalValueWithFormatting(double siValue, int maxDecimals, boolean useSignificantLocalUnit, UnitType... unit) {
        return formatLocalValue(toLocalValue(siValue, maxDecimals, useSignificantLocalUnit, unit), maxDecimals);
    }

    public final String toLocalValueWithFormatting(double siValue, int maxDecimals, UnitType... units) {
        return formatLocalValue(toLocalValue(siValue, maxDecimals, units), maxDecimals);
    }

    public final String toLocalValueWithFormatting(double siValue, UnitType... units) {
        return formatLocalValue(toLocalValue(siValue, true, units));
    }

    public final String toLocalValueWithFormatting(double[] siValues, int maxDecimals, UnitType... units) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < siValues.length; i++) {
            builder.append(formatLocalValue(toLocalValue(siValues[i], maxDecimals, units), maxDecimals));
            if (i < siValues.length - 1) {
                builder.append(StringUtils.WHITESPACE);
            }
        }
        return builder.toString();
    }

    public final String toLocalValueWithFormatting(double[] siValues, UnitType... units) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < siValues.length; i++) {
            builder.append(toLocalValue(siValues[i], units));
            if (i < siValues.length - 1) {
                builder.append(StringUtils.WHITESPACE);
            }
        }
        return builder.toString();
    }

    public final String toLocalValueWithUnit(double siValue, boolean round, boolean useSignificantLocalUnit, UnitType unit) {
        double localValue = toLocalValue(siValue, round, useSignificantLocalUnit, unit);
        String postFix = useSignificantLocalUnit ? getSignificantLocalUnit(siValue, unit).getPostFix(isMulti(localValue))
                : getUnitAbbreviation(isMulti(localValue), unit);
        return formatLocalValue(localValue) + StringUtils.WHITESPACE + postFix;
    }

    public final String toLocalValueWithUnit(double siValue, int maxDecimals, boolean useSignificantLocalUnit, UnitType... units) {
        double localValue = toLocalValue(siValue, maxDecimals, useSignificantLocalUnit, units);
        return formatLocalValue(localValue, maxDecimals) + StringUtils.WHITESPACE + getUnitAbbreviation(isMulti(localValue), units);

    }

    public final String toLocalValueWithUnit(double siValue, UnitType... units) {
        double localValue = toLocalValue(siValue, true, units);
        boolean multi = isMulti(localValue);
        return formatLocalValue(localValue) + StringUtils.WHITESPACE + getUnitAbbreviation(multi, units);
    }

    public final String toSignedLocalValue(double siValue) {

        String result = toLocalValueWithUnit(siValue, UnitType.NONE);
        return siValue > 0.0 ? "+" + result : result;
    }

    public final String toSignedLocalValue(double siValue, int maxDecimals, boolean useSignificantLocalUnit, UnitType... units) {
        return getSignPrefix(siValue) + toLocalValueWithUnit(siValue, maxDecimals, useSignificantLocalUnit, units);
    }

    public final String toSignedLocalValue(double siValue, UnitType... units) {
        return getSignPrefix(siValue) + toLocalValueWithUnit(siValue, units);
    }

    public final String toSignedLocalValueWithFormatting(double siValue, UnitType... units) {
        return formatLocalValue(toLocalValue(siValue, true, units));
    }

    public final double toSIValue(double localValue, int decimals, UnitType... units) {
        return MathUtils.round(toSIValue(localValue, units), decimals);
    }

    public final double toSIValue(double localValue, UnitType... units) {

        if (units == null) {
            throw new IllegalArgumentException("Missing units array!");
        }

        double newValue = localValue;
        for (int i = 0; i < units.length; i++) {
            LocalUnit unitDimension = getLocalUnit(units[i]);
            if (i == 0) {
                newValue = toSIValueWithAdjustedNotation(localValue, unitDimension);
            } else {
                newValue /= toSIValueWithAdjustedNotation(1d, unitDimension);
            }
        }
        return newValue;
    }

    public final double[] toSIValues(double[] localValues, UnitType... units) {

        double[] si = new double[localValues.length];
        for (int i = 0; i < localValues.length; i++) {
            si[i] = toSIValue(localValues[i], units);
        }
        return si;
    }

    private final double toSIValueWithAdjustedNotation(double localValue, LocalUnit unit) {
        return unit.toSIValue(localValue) * unit.getRelativeSingleUnitValue();
    }
}
