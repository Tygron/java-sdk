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
package nl.tytech.locale.unit.uscustomary;

import java.text.NumberFormat;
import java.util.Locale;
import nl.tytech.locale.unit.LocalUnit;
import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.locale.unit.generic.AirPollution;
import nl.tytech.locale.unit.generic.Angle;
import nl.tytech.locale.unit.generic.Energy;
import nl.tytech.locale.unit.generic.HeatFlow;
import nl.tytech.locale.unit.generic.NoUnitDimension;
import nl.tytech.locale.unit.generic.Noise;
import nl.tytech.locale.unit.generic.Percentage;
import nl.tytech.locale.unit.generic.Power;

/**
 *
 * @author Frank Baars
 *
 */
public class USCustomaryUnitSystem extends UnitSystem {

    @Override
    protected UnitSystem create() {
        return new USCustomaryUnitSystem();
    }

    @Override
    protected NumberFormat getLocalNumberFormatter() {
        return NumberFormat.getInstance(Locale.US);
    }

    @Override
    protected LocalUnit getLocalUnit(UnitType unitDimensionType) {

        switch (unitDimensionType) {
            case LENGTH:
            case HEIGHT:
                return LengthUSCustomary.FEET;
            case VOLUME:
                return VolumeUSCustomary.CUBIC_FEET;
            case FLOW_RATE:
                return FlowRateUSCustomary.CUBIC_FEET_SECOND;
            case SURFACE:
                return SurfaceUSCustomary.SQUARE_SURVEY_FEET;
            case TEMPERATURE:
                return TemperatureUSCustomary.DEGREES_FAHRENHEIT;
            case TEMPERATURE_RELATIVE:
                return RelativeTemperatureUSCustomary.DEGREES_FAHRENHEIT;
            case PERCENTAGE:
                return Percentage.PERCENTAGE;
            case HEAT_FLOW:
                return HeatFlow.HEAT_FLOW;
            case ENERGY:
                return Energy.ENERGY;
            case NET_POWER:
                return Power.KILO_WATT;
            case NOISE:
                return Noise.DECIBEL;
            case AIR_POLLUTION:
                return AirPollution.AIR_POLLUTION;
            case DIAMETER:
            case WATER_MM:
            case GROUNDWATER_DEPTH:
                return LengthUSCustomary.INCHES;
            case MAP_SIZE:
                return LengthUSCustomary.YARDS;
            case VEHICLE_SPEED:
                return VehicleSpeedUSCustomary.MPH;
            case VEHICLE_EMISSION:
                return VehicleEmissionUSCustomary.GRAM_M;
            case ANGLE:
                return Angle.DEGREES;
            case POWER:
                return Power.WATT;
            case UNIT:
                return NoUnitDimension.UNIT;
            case SPACES:
                return NoUnitDimension.SPACES;
            case BOOLEAN:
                return NoUnitDimension.BOOLEAN;
            case NONE:
            default:
                break;
        }
        return NoUnitDimension.VALUES[0].getDefault();
    }

    @Override
    protected UnitSystemType getUnitSystem() {
        return UnitSystemType.US_CUSTOMARY;
    }
}
