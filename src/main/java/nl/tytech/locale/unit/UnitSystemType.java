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

import nl.tytech.locale.unit.imperial.ImperialUnitSystem;
import nl.tytech.locale.unit.si.SIUnitSystem;
import nl.tytech.locale.unit.uscustomary.USCustomaryUnitSystem;
import nl.tytech.util.SkipObfuscation;

/**
 * @author Frank Baars
 */
public enum UnitSystemType implements SkipObfuscation {

    /**
     * British Imperial Measurement system
     */
    BRITISH_IMPERIAL("British Imperial", new ImperialUnitSystem()),

    /**
     * SI: International System of Units
     */
    SI("International", new SIUnitSystem()),

    /**
     * US Customary
     */
    US_CUSTOMARY("US Customary", new USCustomaryUnitSystem());

    public static final UnitSystemType[] VALUES = values();
    private String name;

    private UnitSystem impl;

    private UnitSystemType(String name, UnitSystem impl) {
        this.name = name;
        this.impl = impl;
    }

    public UnitSystem getImpl() {
        return impl;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
