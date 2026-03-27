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
package nl.tytech.data.core.item;

import java.util.List;
import java.util.stream.Stream;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Keep track of the Geo Source
 *
 * @author Maxim Knepfle
 */
public abstract class Source extends UniqueNamedItem {

    public interface BaseSourceInterface {

        public List<Source> getSources();
    }

    public interface SourceInterface extends BaseSourceInterface {

        public boolean addSource(Integer sourceID);
    }

    public static final MapLink[] SOURCED_MAPS = new MapLink[] { MapLink.HEIGHTS, MapLink.TERRAINS, MapLink.NEIGHBORHOODS, MapLink.ZONES,
            MapLink.BUILDINGS, MapLink.AREAS, MapLink.PLOTS, MapLink.MEASUREMENTS, MapLink.GEO_PLUGINS, MapLink.OVERLAYS,
            MapLink.PARAMETRIC_DESIGNS };

    public static final Integer BAG = 0;

    public static final Integer BRP = 1;

    public static final Integer RISICOKAART = 2;

    public static final Integer BGT = 3;

    public static final Integer TOP10NL = 4;

    public static final Integer NYOPENDATA = 5;

    public static final Integer OSM = 6;

    public static final Integer AHN2 = 7;

    public static final Integer AHN3 = 8;

    public static final Integer ESRI_ELEVATION = 9;

    public static final Integer FGR = 10;

    public static final Integer WIJKEN_BUURTEN = 11;

    public static final Integer GENERATED = 12;

    public static final Integer RUIMTELIJKE_PLANNEN = 13;

    public static final Integer NSL = 14;

    public static final Integer BRO = 15;

    public static final Integer IMWA = 16;

    public static final Integer KADASTER = 18;

    public static final Integer AHN4 = 19;

    public static final Integer INWEVA = 20;

    public static final Integer INFOMILL = 21;

    public static final Integer BAG3D = 22;

    public static final Integer EP = 23;

    public static final Integer NWB = 25;

    public static final Integer AHN5 = 26;

    public static final Integer AERIAL_IMAGERY = 27;

    public static final Integer WFST = 28;

    public static final Integer AHN6 = 29;

    private static final long serialVersionUID = -4253782210241563579L;

    @XMLValue
    private String uploaderName = StringUtils.EMPTY;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private Long date = System.currentTimeMillis();

    @XMLValue
    private String infoUrl = StringUtils.EMPTY;

    public Source() {

    }

    public Source(String name) {
        setName(name);
    }

    public abstract TColor getColor();

    public Long getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public int getLinkAmount() {
        return (int) Stream.of(SOURCED_MAPS).flatMap(m -> getMap(m).stream())
                .filter(i -> i instanceof BaseSourceInterface si && si.getSources().contains(this)).count();
    }

    public abstract String getTypeName();

    public String getUploaderName() {
        return uploaderName;
    }

    public boolean isGenerated() {
        return this.getID().intValue() == GENERATED;
    }

    public boolean isSpecific() {
        return this.getID().intValue() >= Item.SPECIFIC_START_ID;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

}
