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
package nl.tytech.locale;

import java.util.Currency;
import java.util.Locale;
import nl.tytech.util.SkipObfuscation;

// Egyptian POUND
// Israeli SHEKEL
// South African RAND
// Turkish NEW TURKISH LIRA
// UAE DIRHAM
// Australian DOLLAR
// Chinese YUANRENMINBI
// Hong Kong DOLLAR
// Indian RUPEE
// Indonesian RUPIAH
// Japanese YEN
// Malaysian RINGGIT
// New Zealand DOLLAR
// Pakistani RUPEE
// Singapore DOLLAR
// South Korean WON
// Taiwanese DOLLAR
// Thai BAHT
// Argentinean PESO
// Brazilian REAL
// Canadian DOLLAR
// Chilean PESO
// Dominican PESO
// Mexican PESO
// British POUND
// Czech KORUNA
// Danish KRONE
// European EURO
// Hungarian FORINT
// Norwegian KRONE
// Polish ZLOTY
// Russian RUBLE
// Swedish KRONA
// Swiss FRANC
// BITCOIN

/**
 * @author Jurrian Hartveldt
 */
public enum TCurrency implements SkipObfuscation {

    DOLLAR_US(Locale.US),

    EURO(Locale.GERMANY),

    POUND_BRITISH(Locale.UK);

    public static final TCurrency[] VALUES = values();

    private String currencyCharacter;

    private String currencyName;

    private TCurrency(Locale notationLocale) {
        Currency currency = Currency.getInstance(notationLocale);
        this.currencyName = currency.getDisplayName(notationLocale);
        this.currencyCharacter = currency.getSymbol(notationLocale);
    }

    public String getCurrencyCharacter() {
        return currencyCharacter;
    }

    public String getDisplayName() {
        return currencyName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
