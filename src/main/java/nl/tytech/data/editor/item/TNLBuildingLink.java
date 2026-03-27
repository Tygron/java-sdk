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
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.serializable.ConstructionPeriod;

/**
 * Top10NL Building linkage
 *
 * @author Jurrian Hartveldt
 *
 */
public class TNLBuildingLink extends FunctionGeoLink {

    public enum TNLBuildingHeightType {

        LAAGBOUW("laagbouw"), //
        HOOGBOUW("hoogbouw"), //
        ONBEKEND("onbekend"), //
        ;

        public static final String TNL_TAG = "HOOGTEKLASSE";

        public static TNLBuildingHeightType[] VALUES = values();

        public static TNLBuildingHeightType getDefault() {
            return ONBEKEND;
        }

        public static TNLBuildingHeightType getEnumForValue(String name) {
            for (TNLBuildingHeightType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLBuildingHeightType(String name) {
            this.name = name;
        }
    }

    public enum TNLBuildingType {

        BRANDTOREN("brandtoren"), //
        BEZOEKERSCENTRUM("bezoekerscentrum"), //
        BUNKER("bunker"), //
        CREMATORIUM("crematorium"), //
        DEELRAADSECRETARIE("deelraadsecretarie"), //
        DOK("dok"), //
        ELEKTRICITEITSCENTRALE("elektriciteitscentrale"), //
        FABRIEK("fabriek"), //
        FORT("fort"), //
        GASCOMPRESSIESTATION("gascompressiestation"), //
        GEMAAL("gemaal"), //
        GEMEENTEHUIS("gemeentehuis"), //
        GEVANGENIS("gevangenis"), //
        GRENSKANTOOR("grenskantoor"), //
        HOTEL("hotel"), //
        HUIZENBLOK("huizenblok"), //
        HULPSECRETARIE("hulpsecretarie"), //
        KAPEL("kapel"), //
        KAS_WARENHUIS("kas, warenhuis"), //
        KASTEEL("kasteel"), //
        KERK("kerk"), //
        KERNCENTRALE("kerncentrale, kernreactor"), //
        KLOKKENTOREN("klokkentoren"), //
        KLOOSTER("klooster, abdij"), //
        KLINIEK("kliniek, inrichting, sanatorium"), //
        KUNSTIJSBAAN("kunstijsbaan"), //
        KOELTOREN("koeltoren"), //
        KOEPEL("koepel"), //
        LICHTTOREN("lichttoren"), //
        LUCHTWACHTTOREN("luchtwachttoren"), //
        MANEGE("manege"), //
        METROSTATION("metrostation"), //
        MILITAIR_GEBOUW("militair gebouw"), //
        MOTEL("motel"), //
        MUSEUM("museum"), //
        PARKEERGELEGENHEID("parkeerdak, parkeerdek, parkeergarage"), //
        PEILMEETSTATION("peilmeetstation"), //
        POLITIE_BUREAU("politiebureau"), //
        POMPSTATION("pompstation"), //
        POSTKANTOOR("postkantoor"), //
        PSYCHIATRISCH_ZIEKENHUIS("psychiatrisch ziekenhuis, psychiatrisch centrum"), //
        RADARPOST("radarpost"), //
        RADARTOREN("radartoren"), //
        RADIOTOREN("radiotoren, televisietoren"), //
        RECREATIECENTRUM("recreatiecentrum"), //
        REDDINGBOOTHUISJE("reddingboothuisje"), //
        REDDINGHUISJE("reddinghuisje, schuilhut"), //
        RELIGIEUS_GEBOUW("religieus gebouw"), //
        REMISE("remise"), //
        RUINE("ru?ne"), //
        SCHAAPSKOOI("schaapskooi"), //
        SCHOOL("school"), //
        SCHOORSTEEN("schoorsteen"), //
        SPORTHAL("sporthal"), //
        STADION("stadion"), //
        STADSKANTOOR("stadskantoor"), //
        TANK("tank"), //
        TANKSTATION("tankstation"), //
        TELECOMMUNICATIETOREN("telecommunicatietoren"), //
        TOREN("toren"), //
        TRANSFORMATORSTATION("transformatorstation"), //
        TREINSTATION("treinstation"), //
        UITZICHTTOREN("uitzichttoren"), //
        UNIVERSITEIT("universiteit"), //
        VEILING("veiling"), //
        VERKEERSTOREN("verkeerstoren"), //
        WATERRADMOLEN("waterradmolen"), //
        WATERTOREN("watertoren"), //
        WEGENWACHTSTATION("wegenwachtstation"), //
        WEGRESTAURANT("wegrestaurant"), //
        WERF("werf"), //
        WINDMOLEN("windmolen"), //
        WATERMOLEN("windmolen: watermolen"), //
        KORENMOLEN("windmolen: korenmolen"), //
        WINDTURBINE("windturbine"), //
        ZENDTOREN("zendtoren"), //
        ZIEKENHUIS("ziekenhuis"), //
        ZWEMBAD("zwembad"), //
        OVERIG("overig"), //
        ;

        public static final String TNL_TAG = "TYPEGEBOUW_CSV";

        public static TNLBuildingType[] VALUES = values();

        public static TNLBuildingType getDefault() {
            return OVERIG;
        }

        public static TNLBuildingType getEnumForValue(String name) {
            for (TNLBuildingType type : VALUES) {
                if (name != null && name.contains(type.name)) {
                    return type;
                }
            }
            return getDefault();
        }

        private String name;

        private TNLBuildingType(String name) {
            this.name = name;
        }

    }

    private static final long serialVersionUID = 2875735299514893715L;

    @XMLValue
    private ArrayList<TNLBuildingType> buildingTypes = new ArrayList<>();

    @XMLValue
    private ArrayList<TNLBuildingHeightType> heightTypes = new ArrayList<>();

    private Double slantingRoofHeight = null;

    public TNLBuildingLink() {

    }

    public TNLBuildingLink(Function function) {
        this.setFunctionID(function.getID());
    }

    public List<TNLBuildingType> getBuildingTypes() {
        return buildingTypes;
    }

    @Override
    public List<ConstructionPeriod> getConstructionPeriods() {
        return getFunction().getConstructionPeriods();
    }

    public List<TNLBuildingHeightType> getHeightTypes() {
        return heightTypes;
    }

    public Double getSlantingRoofHeight() {
        return slantingRoofHeight;
    }

    public void setSlantingRoofHeight(Double slantingRoofHeight) {
        this.slantingRoofHeight = slantingRoofHeight;
    }

}
