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
 * Top10NL Road linkage
 *
 * @author Jurrian Hartveldt & Frank Baars
 */
public class TNLRoadLink extends FunctionGeoLink {

    public enum TNLRoadHardnessType {

        /**
         * Note: Maxim: order is IMPORTANT, first check longer names then smaller when using contains!
         */
        HALFVERHARD("half verhard"), //
        ONVERHARD("onverhard"), //
        VERHARD("verhard"), //
        ONBEKEND("onbekend"); //

        public static final String TNL_TAG = "verhardingstype";

        public static final TNLRoadHardnessType[] VALUES = values();

        public static TNLRoadHardnessType getEnumForValue(String name) {
            for (TNLRoadHardnessType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private TNLRoadHardnessType(String name) {
            this.name = name;
        }
    }

    public enum TNLRoadInfraType {

        CONNECTION("verbinding"), //
        INTERSECTION("kruising");

        public static final String TNL_TAG = "typeinfrastructuur";

        public static final TNLRoadInfraType[] VALUES = values();

        public static TNLRoadInfraType getEnumForValue(String name) {
            for (TNLRoadInfraType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return CONNECTION;
        }

        private String name;

        private TNLRoadInfraType(String name) {
            this.name = name;
        }
    }

    public enum TNLRoadType {

        AUTOSNELWEG("autosnelweg"), //
        HOOFDWEG("hoofdweg"), //
        REGIONALE_WEG("regionale weg"), //
        ROLBAAN("rolbaan"), //
        PLATFORM("platform"), //
        STARTBAAN("startbaan"), //
        LANDINGSBAAN("landingsbaan"), //
        LOKALE_WEG("lokale weg"), //
        STRAAT("straat"), //
        ONBEKEND("onbekend"), //
        OVERIG("overig"), //
        ;//

        public static final String TNL_TAG = "typeweg";

        public static final TNLRoadType[] VALUES = values();

        public static TNLRoadType getEnumForValue(String name) {
            for (TNLRoadType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private TNLRoadType(String name) {
            this.name = name;
        }

    }

    public enum TNLRoadUsageType {

        // GEMENGD_VERKEER("gemengd verkeer"), // not usefull to detect! can be everything!
        SNELVERKEER("snelverkeer"), //
        VLIEGVERKEER("vliegverkeer"), //
        BUSVERKEER("busverkeer"), //
        FIETSERS("fietsers, bromfietsers"), //
        PARKEREN("parkeren"), //
        PARKEREN_CARPOOL("parkeren: carpoolplaats"), //
        PARKEREN_PR("parkeren: P+R parkeerplaats"), //
        RUITERS("ruiters"), //
        VOETGANGERS("voetgangers"), //
        ONBEKEND("onbekend"), //
        OVERIG("overig"), //
        ;//

        public static final String TNL_TAG = "hoofdverkeersgebruik";

        public static final TNLRoadUsageType[] VALUES = values();

        public static TNLRoadUsageType getEnumForValue(String name) {
            for (TNLRoadUsageType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private TNLRoadUsageType(String name) {
            this.name = name;
        }

    }

    public static final String TNL_NAME_TAG = "naam";

    private static final long serialVersionUID = 4354829238829605068L;

    @XMLValue
    @ListOfClass(TNLRoadUsageType.class)
    private ArrayList<TNLRoadUsageType> usageType = new ArrayList<>();

    @XMLValue
    @ListOfClass(TNLRoadType.class)
    private ArrayList<TNLRoadType> roadType = new ArrayList<>();

    @XMLValue
    @ListOfClass(TNLRoadHardnessType.class)
    private ArrayList<TNLRoadHardnessType> hardnessType = new ArrayList<>();

    public TNLRoadLink() {

    }

    public List<TNLRoadHardnessType> getHardnessTypes() {
        return hardnessType;
    }

    public List<TNLRoadType> getRoadTypes() {
        return roadType;
    }

    public List<TNLRoadUsageType> getUsageTypes() {
        return usageType;
    }

    public boolean isBridge() {
        return this.getFunction().isBridge();
    }

    public boolean isIntersection() {
        return this.getFunction().isIntersectionFunction();
    }
}
