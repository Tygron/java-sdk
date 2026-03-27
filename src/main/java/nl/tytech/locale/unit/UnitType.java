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

/**
 *
 * @author Frank Baars
 *
 */
public enum UnitType {

    LENGTH(1), //
    HEIGHT(3), //
    SURFACE(0), //
    VOLUME(1), //
    FLOW_RATE(1), //
    BOOLEAN(0), //
    TEMPERATURE(1), //
    TEMPERATURE_RELATIVE(2), //
    PERCENTAGE(1), //
    SPACES(1), //
    UNIT(1), //
    NONE(1), //
    HEAT_FLOW(1), //
    ENERGY(1), //
    AIR_POLLUTION(1), //
    WATER_MM(2), //
    GROUNDWATER_DEPTH(2), //
    DIAMETER(3), //
    NET_POWER(3), //
    POWER(2), //
    MAP_SIZE(0), //
    NOISE(1), //
    ANGLE(2), //
    VEHICLE_SPEED(0), //
    VEHICLE_EMISSION(2),//
    ;

    private int roundingDecimalPosition;
    private double significance;

    private UnitType(int roundingDecimalPosition) {
        this.roundingDecimalPosition = roundingDecimalPosition;
        this.significance = Math.pow(10, -1 * roundingDecimalPosition);
    }

    public int getRoundingDecimalPosition() {
        return roundingDecimalPosition;
    }

    public double getSignificance() {
        return significance;
    }

    public UnitTime getUnitTime() {

        return switch (this) {
            case FLOW_RATE -> UnitTime.SECOND;
            case VEHICLE_SPEED -> UnitTime.HOUR;
            default -> UnitTime.NONE;
        };
    }
}
