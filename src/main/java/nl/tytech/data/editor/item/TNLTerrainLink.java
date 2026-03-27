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
 * Top10NL Terrain Linkage
 *
 * @author Frank Baars
 */
public abstract class TNLTerrainLink extends GeoLink {

    public enum TNLTerrainLandUse {

        AANLEGSTEIGER("aanlegsteiger"), //
        AKKERLAND("akkerland"), //
        BEBOUWD_GEBIED("bebouwd gebied"), //
        BOOMGAARD("boomgaard"), //
        BOOMKWEKERIJ("boomkrekerij"), //
        BOS_GEMENGD_BOS("bos: gemengd bos"), //
        BOS_GRIEND("bos: griend"), //
        BOS_LOOFBOS("bos: loofbos"), //
        BOS_NAALDBOS("bos: naaldbos"), //
        DODENAKKER("dodenakker"), //
        DODENAKKER_MET_BOS("donenakker met bos"), //
        FRUITKWEKERIJ("fruitkwekerij"), //
        GRASLAND("grasland"), //
        HEIDE("heide"), //
        LAADPERRON("laadperron"), //
        BASSALTBLOKKEN("basaltblokken, steenglooiing"), //
        POPULIEREN("populieren"), //
        SPOORBAANLICHAAM("spoorbaanlichaam"), //
        ZAND("zand"), //
        OVERIG("overig"), //
        ONBEKEND("onbekend"), //
        ;

        public static final String TNL_TAG = "typelandgebruik";

        public static final TNLTerrainLandUse[] VALUES = values();

        public static TNLTerrainLandUse getDefault() {
            return ONBEKEND;
        }

        public static TNLTerrainLandUse getEnumForValue(String name) {
            for (TNLTerrainLandUse type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private final String name;

        private TNLTerrainLandUse(String name) {
            this.name = name;
        }
    }

    private static final long serialVersionUID = -8289827208920464674L;

    @XMLValue
    private ArrayList<TNLTerrainLandUse> landUses = new ArrayList<>();

    public List<TNLTerrainLandUse> getLandUses() {
        return landUses;
    }

    public abstract boolean mustContainHouses();

}
