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
package nl.tytech.core.net.serializable;

/**
 * MapGroup: grouping for MapLinks
 *
 * @author Frank Baars
 */
public enum MapGroup {

    CURRENT_SITUATION, //
    FUTURE_DESIGN, //
    MULTI_SCENARIO, //
    TOOLS, //
    COMMUNITY //
    ;

    public static enum Sub {

        CONSTRUCTION, //
        URBAN_SUBDIVISION, //
        CALCULATION, //

        GEOGRAPHY, //
        NETWORK, //

        FINANCIAL, //

        CONFIGURATION, //
        ACTION,

        VISUALS, //
        INTERACTION, //
        ASSETS, //

        GEO, //
        API,//

        ;

    }

    public static final MapGroup[] VALUES = values();

    public static final Sub[] SUB_VALUES = Sub.values();

    public String getTitle() {
        return this.name().replaceAll("_", this == MULTI_SCENARIO ? "-" : " ");
    }
}
