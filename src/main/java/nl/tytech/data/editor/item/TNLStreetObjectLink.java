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
 * Top10NL Street Object linkages
 *
 * @author Frank Baars
 */
public class TNLStreetObjectLink extends FunctionGeoLink {

    public enum TNLStreetObjectType {

        PAAL("paal"), //
        PAALWERK("paalwerk"), //
        METROSTATION("metrostation"), //
        MUUR("muur"), //
        OLIEPOMPINSTALLATIE("oliepompinstallatie"), //
        RADIOBAKEN("radiobaken"), //
        RD_PUNT("RD punt"), //
        RADIOTELESCOOP("radiotelescoop"), //
        PEILSCHAAL("peilschaal"), //
        PIJLER("pijler"), //
        PLAATSNAAMBORD("plaatsnaambord"), //
        HUNEBED("hunebed"), //
        KAAP("kaap"), //
        KABELBAAN("kabelbaan"), //
        KABELBAANMAST("kabelbaanmast"), //
        HEG_HAAG("heg, haag"), //
        HEKWERK("hekwerk"), //
        HELIKOPTERLANDINGSPLATFORM("helikopterlandingsplatform"), //
        HOOGSPANNINGSLEIDING("hoogspanningsleiding"), //
        HOOGSPANNINGSMAST("hoogspanningsmast"), //
        KRAAN("kraan"), //
        KRUIS("kruis"), //
        KOGELVANGER_SCHIETBAAN("kogelvanger schietbaan"), //
        LUCHTVAARTLICHT("luchtvaartlicht"), //
        LEIDING("leiding"), //
        MARKANT_OBJECT("markant object"), //
        KILOMETERPAAL("kilometerpaal"), //
        KILOMETERPAAL_SPOORWEG("kilometerpaal spoorweg"), //
        KILOMETERRAAIPAAL("kilometerraaipaal"), //
        KILOMETERPAAL_WATER("kilometerpaal water"), //
        KILOMETERRAAIBORD("kilometerraaibord"), //
        KOEDAM("koedam"), //
        DUKDALF("dukdalf"), //
        BUSSTATION("busstation"), //
        CALAMITEITENDOORGANG("calamiteitendoorgang"), //
        GOLFMEETPAAL("golfmeetpaal"), //
        GRENSPUNT("grenspunt"), //
        GPS_KERNNETPUNT("GPS kernnetpunt"), //
        GASWINNING("gaswinning"), //
        GELUIDSWERING("geluidswering"), //
        GEDENKTEKEN("gedenkteken, monument"), //
        BOMENRIJ("bomenrij"), //
        BOOM("boom"), //
        BOTENHELLING("botenhelling"), //
        AANLEGSTEIGER("aanlegsteiger"), //
        BAAK("baak"), //
        WEGWIJZER("wegwijzer"), //
        WEGAFSLUITING("wegafsluiting"), //
        VERKEERSGELEIDER("verkeersgeleider"), //
        VLAMPIJP("vlampijp"), //
        VLIEDBERG("vliedberg"), //
        ZICHTBAAR_WRAK("zichtbaar wrak"), //
        WINDMOLENTJE("windmolentje"), //
        ZENDMAST("zendmast"), //
        SNELTRAMHALTE("sneltramhalte"), //
        SLUISDEUR("sluisdeur"), //
        SCHEEPVAARTLICHT("scheepvaartlicht"), //
        SCHIETBAAN("schietbaan"), //
        SEINMAST("seinmast"), //
        TOL("tol"), //
        TREINSTATION("treinstation"), //
        STORMVLOEDKERING("stormvloedkering"), //
        STREKDAM_KRIB_GOLFBREKER("strekdam, krib, golfbreker"), //
        STUW("stuw"), //
        STRANDPAAL("strandpaal"), //
        HAVENHOOFD("havenhoofd"), //
        KLOKKENSTOEL("klokkenstoel"), //
        UITZICHTPUNT("uitzichtpunt"), //
        OVERIG("overig"), //
        ;

        public static final String TNL_TAG = "typeinrichtingselement";

        public static final TNLStreetObjectType[] VALUES = values();

        public static final TNLStreetObjectType getDefault() {
            return OVERIG;
        }

        public static final TNLStreetObjectType getEnumForValue(String name) {
            for (TNLStreetObjectType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private final String name;

        private TNLStreetObjectType(String name) {
            this.name = name;
        }
    }

    private static final long serialVersionUID = -7067202394124518025L;

    @XMLValue
    private ArrayList<TNLStreetObjectType> objectTypes = new ArrayList<>();

    public TNLStreetObjectLink() {

    }

    public List<TNLStreetObjectType> getWaterBuildingTypes() {
        return objectTypes;
    }

}
