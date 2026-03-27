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
package nl.tytech.data.engine.serializable;

import java.util.Calendar;

/**
 * Period in time for Building Styles.
 *
 * @author Maxim Knepfle
 *
 */
public enum ConstructionPeriod {

    ANCIENT(Integer.MIN_VALUE),

    CLASSIC(1500),

    PRE_WAR(1880),

    POST_WAR(1945),

    CONTEMPORARY(1970),

    FUTURISTIC(Calendar.getInstance().get(Calendar.YEAR));

    public static final int UNKNOWN_YEAR = 9999;

    public static final ConstructionPeriod[] VALUES = values();

    public static final ConstructionPeriod get(int constructionYear) {

        ConstructionPeriod selectedPeriod = ANCIENT;
        for (ConstructionPeriod period : ConstructionPeriod.VALUES) {
            if (period.getStartOfPeriod() > constructionYear) {
                return selectedPeriod;
            }

            selectedPeriod = period;
        }

        if (UNKNOWN_YEAR == constructionYear) {
            return CONTEMPORARY;
        }

        return FUTURISTIC;
    }

    private int startOfPeriod;

    private ConstructionPeriod(int startOfPeriod) {
        this.startOfPeriod = startOfPeriod;
    }

    public int getStartOfPeriod() {
        return startOfPeriod;
    }
}
