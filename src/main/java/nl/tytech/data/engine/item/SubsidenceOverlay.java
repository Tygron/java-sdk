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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.engine.item.SubsidenceOverlay.SubsidencePrequel;
import nl.tytech.data.engine.item.SubsidenceOverlay.SubsidenceResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * Subsidence based on a grid
 *
 * @author Maxim Knepfle
 */
@Deprecated(since = "To be non functional in LTS 2028, for LTS 2026 mark as deprecated")
public class SubsidenceOverlay extends ResultParentOverlay<SubsidenceResult, SubsidencePrequel> {

    @Deprecated
    public static class ClimateModel {

        private static final double Q10 = 3;

        private int startYear = (int) SubsidenceAttribute.CLIMATE_START_YEAR.defaultValue();
        private int endYear = (int) SubsidenceAttribute.CLIMATE_FINAL_YEAR.defaultValue();
        private double startTemp = SubsidenceAttribute.CLIMATE_START_TEMP.defaultValue();
        private double endTemp = SubsidenceAttribute.CLIMATE_FINAL_TEMP.defaultValue();
        private double oxidation = SubsidenceAttribute.CLIMATE_OXIDATION.defaultValue();
        private double soilTempFactor = SubsidenceAttribute.CLIMATE_SOIL_TEMP_FACTOR.defaultValue();

        private double _getDeltaMicroActivity(double yearFactor) {
            return Math.pow(Q10, _getDeltaTemp(yearFactor) / 10d) - 1;
        }

        private double _getDeltaSubsidence(double yearFactor) {
            return _getDeltaMicroActivity(yearFactor) * oxidation;
        }

        private double _getDeltaTemp(double yearFactor) {
            return (endTemp - startTemp) * yearFactor * soilTempFactor;
        }

        private double _getYearFactor(int calcYear) {
            double deltaYears = endYear - startYear;
            return deltaYears == 0 ? 0 : (calcYear - startYear) / deltaYears;
        }

        @Deprecated
        public double getA(int calcYear, double startA) {
            double yearFactor = _getYearFactor(calcYear);
            return startA * (_getDeltaSubsidence(yearFactor) + 1);
        }

        @Deprecated
        public double getDeltaTemp(int calcYear) {
            return _getDeltaTemp(_getYearFactor(calcYear));
        }

        @Deprecated
        public void setEndTemp(double endTemp) {
            this.endTemp = endTemp;
        }

        @Deprecated
        public void setEndYear(int endYear) {
            this.endYear = endYear;
            this.startYear = Math.min(startYear, endYear - 1);
        }

        @Deprecated
        public void setOxidation(double oxidation) {
            this.oxidation = oxidation;
        }

        @Deprecated
        public void setSoilTempFactor(double soilTempFactor) {
            this.soilTempFactor = soilTempFactor;
        }

        @Deprecated
        public void setStartTemp(double startTemp) {
            this.startTemp = startTemp;
        }

        @Deprecated
        public void setStartYear(int startYear) {
            this.startYear = startYear;
            this.endYear = Math.max(endYear, startYear + 1);
        }

        @Deprecated
        public void setup(SubsidenceOverlay overlay) {

            if (overlay.hasAttribute(SubsidenceAttribute.CLIMATE_START_TEMP)) {
                setStartTemp(overlay.getAttribute(SubsidenceAttribute.CLIMATE_START_TEMP));
            }
            if (overlay.hasAttribute(SubsidenceAttribute.CLIMATE_FINAL_TEMP)) {
                setEndTemp(overlay.getAttribute(SubsidenceAttribute.CLIMATE_FINAL_TEMP));
            }
            if (overlay.hasAttribute(SubsidenceAttribute.CLIMATE_START_YEAR)) {
                setStartYear((int) overlay.getAttribute(SubsidenceAttribute.CLIMATE_START_YEAR));
            }
            if (overlay.hasAttribute(SubsidenceAttribute.CLIMATE_FINAL_YEAR)) {
                setEndYear((int) overlay.getAttribute(SubsidenceAttribute.CLIMATE_FINAL_YEAR));
            }
            if (overlay.hasAttribute(SubsidenceAttribute.CLIMATE_OXIDATION)) {
                setOxidation(overlay.getAttribute(SubsidenceAttribute.CLIMATE_OXIDATION));
            }
            if (overlay.hasAttribute(SubsidenceAttribute.CLIMATE_SOIL_TEMP_FACTOR)) {
                setSoilTempFactor(overlay.getAttribute(SubsidenceAttribute.CLIMATE_SOIL_TEMP_FACTOR));
            }
        }
    }

    @Deprecated
    public enum SubsidenceAttribute implements ReservedAttribute {

        YEARS(Double.class, 30),

        YEARS_PER_TIMEFRAME(Double.class, 3),

        START_YEAR(Integer.class, Moment.getYear(System.currentTimeMillis())),

        A(Double.class, 0.023537),

        B(Double.class, 0.01263),

        C(Double.class, 0.00668),

        ACTIVE_DRAINAGE_FACTOR(Double.class, 1.0),

        PASSIVE_DRAINAGE_FACTOR(Double.class, 1.0),

        CLIMATE_START_TEMP(Double.class, 10.1),

        CLIMATE_FINAL_TEMP(Double.class, 10.7),

        CLIMATE_START_YEAR(Integer.class, 1990),

        CLIMATE_FINAL_YEAR(Integer.class, 2060),

        CLIMATE_OXIDATION(Double.class, 0.67),

        CLIMATE_SOIL_TEMP_FACTOR(Double.class, 0.5),

        /**
         * When a drainage is active the low-level is 0.0M below (based on value from Henk/HDSR, updated 27/9/2016)
         */
        LOW_PASSIVE_DRAINAGE(Double.class, 0.0),

        /**
         * When a drainage is active the hi-level is 0.10M above (based on value from Henk/HDSR, updated 27/9/2016)
         */
        HI_PASSIVE_DRAINAGE(Double.class, -0.10),

        DEFAULT_PEAT_FRACTION(Double.class, 0.4),

        DEFAULT_TOP_LAYER_THICKNESS(Double.class, 5.0),

        DEFAULT_CLAY_THICKNESS(Double.class, 0.2),

        INUNDATE_LAND_BELOW_WATER_LEVEL(Boolean.class, 1.0),

        ;

        @Deprecated
        public static final SubsidenceAttribute[] VALUES = SubsidenceAttribute.values();

        private final Class<?> type;
        private final double[] defaultArray;

        private SubsidenceAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Deprecated
        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Deprecated
        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Deprecated
        @Override
        public Class<?> getType() {
            return type;
        }

    }

    @Deprecated
    public enum SubsidenceKey implements Key {

        WATER_LEVEL(false), //
        WATER_LEVEL_OUTPUT(true, true, UnitType.HEIGHT), CLAY_THICKNESS(true), //
        INDEXATION(true), //
        PEAT_FRACTION(true), //
        TOPLAYER_THICKNESS(true), //
        SUBSIDENCE(true), //
        DRAINAGE(false), //
        HI_GROUND_WATER_LEVEL(true), //
        LOW_GROUND_WATER_LEVEL(true);

        private final UnitType unitType;
        private final boolean optional;
        private final boolean output;

        private SubsidenceKey(boolean optional) {
            this(optional, false);
        }

        private SubsidenceKey(boolean optional, boolean output) {
            this(optional, output, UnitType.NONE);
        }

        private SubsidenceKey(boolean optional, boolean output, UnitType unitType) {
            this.optional = optional;
            this.output = output;
            this.unitType = unitType;
        }

        @Deprecated
        @Override
        public UnitType getUnitType() {
            return unitType;
        }

        @Deprecated
        @Override
        public boolean isOptional() {
            return optional;
        }

        @Deprecated
        @Override
        public boolean isOutput() {
            return output;
        }
    }

    @Deprecated
    public enum SubsidencePrequel implements PrequelType {

        LOW_GROUND_WATER(), //
        HIGH_GROUND_WATER(), //
        CLAY_THICKNESS(),//
        ;

        @Deprecated
        public static final SubsidencePrequel[] VALUES = SubsidencePrequel.values();
    }

    @Deprecated
    public enum SubsidenceResult implements ResultType {

        SUBSIDENCE(0),

        OXIDATION(1),

        SETTLEMENT(2),

        LOW_GROUND_WATER(3),

        HI_GROUND_WATER(4),

        ;

        @Deprecated
        public static final SubsidenceResult[] VALUES;

        static {
            List<SubsidenceResult> result = new ArrayList<>();
            for (SubsidenceResult r : values()) {
                Deprecated depAnno = ObjectUtils.getEnumAnnotation(r, Deprecated.class);
                if (depAnno == null) {
                    result.add(r);
                }
            }
            VALUES = result.toArray(new SubsidenceResult[result.size()]);
        }

        private final byte index;

        private SubsidenceResult(int index) {
            this.index = (byte) index;
        }

        @Deprecated
        @Override
        public byte getIndex() {
            return index;
        }

        @Deprecated
        @Override
        public boolean isStatic() {
            return false;
        }

        @Deprecated
        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    private static final long serialVersionUID = -2572949484529750226L;

    @Deprecated
    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        int years = getYears();
        int yearsPerTimeframe = getYearsPerTimeframe();
        return Math.max(1, years / yearsPerTimeframe + (years % yearsPerTimeframe == 0 ? 0 : 1));
    }

    @Deprecated
    public double getA() {
        return MathUtils.clamp(getAttribute(SubsidenceAttribute.A), 0, 10);
    }

    @Deprecated
    public double getActiveDrainageFactor() {
        return MathUtils.clamp(getAttribute(SubsidenceAttribute.ACTIVE_DRAINAGE_FACTOR), 0, 1);
    }

    @Deprecated
    public double getB() {
        return MathUtils.clamp(getAttribute(SubsidenceAttribute.B), 0, 10);
    }

    @Deprecated
    public double getC() {
        return MathUtils.clamp(getAttribute(SubsidenceAttribute.C), 0, 10);
    }

    @Deprecated
    @Override
    protected SubsidenceResult getDefaultResult() {
        return SubsidenceResult.SUBSIDENCE;
    }

    @Deprecated
    @Override
    public String getKeyOrDefault(Key key) {

        String result = super.getKey(key);
        if (result != null) {
            return result;

        } else if (key == SubsidenceKey.HI_GROUND_WATER_LEVEL) {
            // use special key for these
            return "GHG";

        } else if (key == SubsidenceKey.LOW_GROUND_WATER_LEVEL) {
            // use special key for these
            return "GLG";

        } else if (key == SubsidenceKey.WATER_LEVEL_OUTPUT) {
            // use special key for these
            return getType() == OverlayType.SUBSIDENCE ? SubsidenceKey.WATER_LEVEL_OUTPUT.name() : StringUtils.EMPTY;

        } else {
            return key.name();
        }
    }

    @Deprecated
    public double getPassiveDrainageFactor() {
        return MathUtils.clamp(getAttribute(SubsidenceAttribute.PASSIVE_DRAINAGE_FACTOR), 0, 1);
    }

    @Deprecated
    @Override
    public SubsidencePrequel[] getPrequelTypes() {
        return SubsidencePrequel.VALUES;
    }

    @Deprecated
    @Override
    protected Class<SubsidenceResult> getResultClass() {
        return SubsidenceResult.class;
    }

    @Deprecated
    public int getStartYear() {
        return (int) Math.round(getOrDefaultArray(SubsidenceAttribute.START_YEAR)[0]);
    }

    @Deprecated
    @Override
    public String getTimeframeText(int timeframe, String format) {
        return Integer.toString(getTimeframeYear(timeframe));
    }

    @Deprecated
    public int getTimeframeYear(int timeframe) {
        return getStartYear() + getYears(timeframe);
    }

    @Deprecated
    public int getYears() {
        return (int) Math.round(MathUtils.clamp(getAttribute(SubsidenceAttribute.YEARS), 1, 1000));
    }

    @Deprecated
    public int getYears(int timeframe) {
        return Math.min(getYears(), (timeframe + 1) * getYearsPerTimeframe());
    }

    @Deprecated
    public int getYearsPerTimeframe() {
        return (int) Math.round(MathUtils.clamp(getOrDefaultArray(SubsidenceAttribute.YEARS_PER_TIMEFRAME)[0], 1, getYears()));
    }

    @Deprecated
    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        if (!this.hasAttribute(SubsidenceAttribute.LOW_PASSIVE_DRAINAGE)) {
            this.setAttributeArray(SubsidenceAttribute.LOW_PASSIVE_DRAINAGE, SubsidenceAttribute.LOW_PASSIVE_DRAINAGE.defaultArray());
        }
        if (!this.hasAttribute(SubsidenceAttribute.HI_PASSIVE_DRAINAGE)) {
            this.setAttributeArray(SubsidenceAttribute.HI_PASSIVE_DRAINAGE, SubsidenceAttribute.HI_PASSIVE_DRAINAGE.defaultArray());
        }

        // add defaults
        for (SubsidenceAttribute type : SubsidenceAttribute.values()) {
            if (!this.hasAttribute(type)) {
                this.setAttributeArray(type, type.defaultArray());
            }
        }
        return result;
    }
}
