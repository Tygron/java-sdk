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
import nl.tytech.core.item.annotations.XMLValue;

/**
 * Top10NL Water linkage
 *
 * @author Jurrian Hartveldt & Frank Baars
 */
public class TNLWaterLink extends TerrainGeoLink {

    public enum TNLHoofdafwatering {

        JA("ja"), //
        NEE("nee"); //

        public static final String TNL_TAG = "hoofdafwatering";

        public static final TNLHoofdafwatering[] VALUES = values();

        public static TNLHoofdafwatering getDefault() {
            return NEE;
        }

        public static TNLHoofdafwatering getEnumForValue(String name) {
            for (TNLHoofdafwatering type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLHoofdafwatering(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterBreedte {

        XS("0,5 - 3 meter", 2d), //
        SMALL("3 - 6 meter", 5d), //
        MEDIUM_SMALL("6 - 12 meter", 10d), //
        MEDIUM("12 - 50 meter", 25d), //
        LARGE("50 - 125 meter", 75d), //
        XL("> 125 meter", 125d), //
        ONBEKEND("onbekend", 3d);//

        public static final String TNL_TAG = "breedteklasse";

        public static final TNLWaterBreedte[] VALUES = values();

        public static TNLWaterBreedte getDefault() {
            return ONBEKEND;
        }

        public static TNLWaterBreedte getEnumForValue(String name) {
            for (TNLWaterBreedte type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;
        private double width;

        private TNLWaterBreedte(String name, double width) {
            this.name = name;
            this.width = width;
        }

        public double getWidth() {
            return width;
        }
    }

    public enum TNLWaterFunctie {

        DRINK_WATER_BEKKEN("drinkwaterbekken"), //
        HAVEN("haven"), //
        NATUURBAD("natuurbad"), //
        VLOEIVELD("vloeiveld"), //
        VISKWEKERIJ("viskwekerij"), //
        VISTRAP("vistrap"), //
        WATERVAL("waterval"), //
        WATERZUIVERING("waterzuivering"), //
        ZWEMBAD("zwembad"), //
        OVERIG("overig"), //
        ONBEKEND("onbekend");//

        public static final String TNL_TAG = "functie";

        public static final TNLWaterFunctie[] VALUES = values();

        public static TNLWaterFunctie getDefault() {
            return ONBEKEND;
        }

        public static TNLWaterFunctie getEnumForValue(String name) {
            for (TNLWaterFunctie type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterFunctie(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterInfrastructuur {

        VERBINDING("verbinding"), //
        KRUISING("kruising"), //
        OVERIG("overig watergebied");//

        public static final String TNL_TAG = "typeinfrastructuurwaterdeel";

        public static TNLWaterInfrastructuur[] VALUES = values();

        public static TNLWaterInfrastructuur getDefault() {
            return OVERIG;
        }

        public static TNLWaterInfrastructuur getEnumForValue(String name) {
            for (TNLWaterInfrastructuur type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterInfrastructuur(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterType {

        ZEE("zee"), //
        WATERLOOP("waterloop"), //
        MEER("meer, plas, ven, vijver"), //
        SLOOT("greppel, droge sloot"), //
        DROOGVALLEND("droogvallend"), //
        BRON("bron, wel"), //
        ONBEKEND("onbekend");//

        public static final String TNL_TAG = "typewater";

        public static final TNLWaterType[] VALUES = values();

        public static TNLWaterType getDefault() {
            return ONBEKEND;
        }

        public static TNLWaterType getEnumForValue(String name) {
            for (TNLWaterType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterType(String name) {
            this.name = name;
        }
    }

    public enum TNLWaterVoorkomen {

        MET_RIET("met riet"), //
        OVERIG("overig");//

        public static final String TNL_TAG = "voorkomen";

        public static final TNLWaterVoorkomen[] VALUES = values();

        public static TNLWaterVoorkomen getDefault() {
            return OVERIG;
        }

        public static TNLWaterVoorkomen getEnumForValue(String name) {
            for (TNLWaterVoorkomen type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLWaterVoorkomen(String name) {
            this.name = name;
        }
    }

    private static final long serialVersionUID = -5338020748053612302L;

    @XMLValue
    private ArrayList<TNLWaterInfrastructuur> infrastructures = new ArrayList<>();

    @XMLValue
    private ArrayList<TNLWaterType> waterTypes = new ArrayList<>();

    @XMLValue
    private ArrayList<TNLWaterFunctie> waterFunctions = new ArrayList<>();

    @XMLValue
    private ArrayList<TNLHoofdafwatering> mainDrainages = new ArrayList<>();

    @XMLValue
    private ArrayList<TNLWaterVoorkomen> appearances = new ArrayList<>();

    public TNLWaterLink() {

    }

    public List<TNLWaterVoorkomen> getAppearances() {
        return appearances;
    }

    public List<TNLWaterInfrastructuur> getInfrastructures() {
        return infrastructures;
    }

    public List<TNLHoofdafwatering> getMainDrainages() {
        return mainDrainages;
    }

    public List<TNLWaterFunctie> getWaterFunctions() {
        return waterFunctions;
    }

    public List<TNLWaterType> getWaterTypes() {
        return waterTypes;
    }

}
