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
package nl.tytech.data.editor.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;

/**
 * Top10NL railroads
 * @author Maxim Knepfle
 *
 */
public class TNLRailLink extends FunctionGeoLink {

    public enum TNLRailAmount {

        ENKEL("enkel", 1), //
        DUBBEL("dubbel", 2), //
        MEERVOUDIG("meervoudig", 3);//

        public static final String TNL_TAG = "aantalsporen";

        public static final TNLRailAmount[] VALUES = values();

        public static TNLRailAmount getDefault() {
            return ENKEL;
        }

        public static TNLRailAmount getEnumForValue(String name) {
            for (TNLRailAmount type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;
        private int amount;

        private TNLRailAmount(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }

        public int getRailAmount() {
            return amount;
        }
    }

    public enum TNLRailType {

        TREIN("trein"), //
        METRO("metro"), //
        TRAM("tram"), //
        GEMENGD("gemengd"), //
        SNELTRAM("sneltram");//

        public static final String TNL_RAIL_TAG = "typespoorbaan";

        public static final TNLRailType[] VALUES = values();

        public static TNLRailType getDefaultRailType() {
            return TREIN;
        }

        public static TNLRailType getEnumForValue(String name) {
            for (TNLRailType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefaultRailType();
        }

        private String name;

        private TNLRailType(String name) {
            this.name = name;
        }

    }

    private static final long serialVersionUID = 4354829238829605069L;
    public static final String IN_TUNNEL_TAG = "fysiekvoorkomen";
    public static final String IN_TUNNEL_PARAM = "in tunnel";
    public static final String TNL_NAME_TAG = "baanvaknaam";

    @XMLValue
    @ListOfClass(TNLRailType.class)
    private ArrayList<TNLRailType> railTypes = new ArrayList<>();

    public TNLRailLink() {

    }

    public List<TNLRailType> getRailTypes() {
        return railTypes;
    }

}
