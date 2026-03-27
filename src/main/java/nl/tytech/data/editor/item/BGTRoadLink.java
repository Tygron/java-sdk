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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;

/**
 * BGT road linkage
 *
 * @author Maxim Knepfle
 */
public class BGTRoadLink extends FunctionGeoLink {

    public enum BGTAppearance {

        TEGELS("tegels"), //
        KLINKERS("gebakken klinkers"), //
        BETON_STENEN("betonstraatstenen"), //
        BETON_ELEMENT("beton element"), //
        ASFALT("asfalt"), //
        ZAND("zand"), //
        ONBEKEND("waardeOnbekend"); //

        public static final String BGT_TAG = "plus-fysiekVoorkomenWegdeel";
        public static final BGTAppearance[] VALUES = values();

        public static BGTAppearance getEnumForValue(String name) {
            for (BGTAppearance type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private BGTAppearance(String name) {
            this.name = name;
        }
    }

    public enum BGTFunction {

        VLIEG_BAAN("baan voor vliegverkeer"), //
        FIETSPAD("fietspad"), //
        INRIT("inrit"), //
        OV_BAAN("OV-baan"), //
        OVERWEG("overweg"), //
        PARKEERVLAK("parkeervlak"), //
        AUTOSNEL_WEG("rijbaan autosnelweg"), //
        AUTO_WEG("rijbaan autoweg"), //
        LOKALE_WEG("rijbaan lokale weg"), //
        REGIONALE_WEG("rijbaan regionale weg"), //
        RUITERPAD("ruiterpad"), //
        SPOORBAAN("spoorbaan"), //
        VOETGANGERSGEBIED("voetgangersgebied"), //
        VOETPAD_OP_TRAP("voetpad op trap"), //
        VOETPAD("voetpad"), //
        WOONERF("woonerf"), //
        ONBEKEND("waardeOnbekend"), //
        ;//

        public static final String BGT_TAG = "function";

        public static final BGTFunction[] VALUES = values();

        public static BGTFunction getEnumForValue(String name) {

            for (BGTFunction type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return ONBEKEND;
        }

        private String name;

        private BGTFunction(String name) {
            this.name = name;
        }

    }

    public enum BGTMaterial {

        CLOSED("gesloten verharding"),

        HALF("?"),

        OPEN("open verharding"),

        UNKNOWN("waardeOnbekend"),

        ;

        public static final String BGT_TAG = "surfaceMaterial";
        public static final BGTMaterial[] VALUES = values();

        public static BGTMaterial getEnumForValue(String name) {
            for (BGTMaterial type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        private String name;

        private BGTMaterial(String name) {
            this.name = name;
        }
    }

    private static final long serialVersionUID = 2166420622004734966L;

    @XMLValue
    private BGTFunction function;

    @XMLValue
    private BGTAppearance appearance;

    @XMLValue
    private BGTMaterial material;

    public BGTRoadLink() {
    }

    public BGTAppearance getBGTAppearance() {
        return appearance;
    }

    public BGTFunction getBGTFunction() {
        return function;
    }

    public BGTMaterial getMaterial() {
        return material;
    }

    public boolean isBridge() {
        if (Item.NONE.equals(getFunctionID())) {
            return false;
        }
        return this.getFunction().isBridge();
    }

    public boolean isIntersection() {
        if (Item.NONE.equals(getFunctionID())) {
            return false;
        }
        return this.getFunction().isIntersectionFunction();
    }
}
