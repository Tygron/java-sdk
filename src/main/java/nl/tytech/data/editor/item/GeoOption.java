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

import static nl.tytech.data.editor.item.GeoOption.Group.CUSTOM;
import static nl.tytech.data.editor.item.GeoOption.Group.DEFAULT;
import static nl.tytech.data.editor.item.GeoOption.Group.DEM;
import static nl.tytech.data.editor.item.GeoOption.Group.ROADS;
import static nl.tytech.data.engine.item.HeightSector.DEFAULT_POINT_SIZE_M;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.SettingType;
import nl.tytech.data.core.item.AbstractSetting;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.naming.EngineNC;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * Options in World Wizard
 * @author Maxim Knepfle
 *
 */
public class GeoOption extends AbstractSetting<GeoOption.Type> {

    public enum AHN {

        AHN3(2018, "AHN3_r", "AHN3_i", Source.AHN3),

        AHN4(2022, "AHN4_DSM_50cm", "AHN4_DTM_50cm", Source.AHN4),

        AHN5(2024, "AHN5_DSM_50cm", "AHN5_DTM_50cm", Source.AHN5),

        AHN6(2025, "AHN6_DSM_50cm", "AHN6_DTM_50cm", Source.AHN6);

        private final int publicationYear;
        private final String dsm, dtm;
        private final Integer sourceID;

        private AHN(int year, String dsm, String dtm, Integer sourceID) {
            this.publicationYear = year;
            this.dsm = dsm;
            this.dtm = dtm;
            this.sourceID = sourceID;
        }

        public String getEsriServiceDSM() {
            return "Hoogtebestand/" + dsm + "/ImageServer/exportImage";
        }

        public String getEsriServiceDTM() {
            return "Hoogtebestand/" + dtm + "/ImageServer/exportImage";
        }

        public int getPublicationYear() {
            return publicationYear;
        }

        public Integer getSourceID() {
            return sourceID;
        }

        @Override
        public String toString() {
            return switch (this) {
                case AHN5 -> name() + " (South West only)";
                case AHN6 -> name() + " (Incomplete)";
                default -> name();
            };
        }
    }

    public enum Group {
        DEFAULT, DEM, WATER, ROADS, CUSTOM
    }

    public enum HeightmapOption {

        HYPER(0.1),

        SUPER(0.25),

        HIGH(0.5),

        NORMAL(DEFAULT_POINT_SIZE_M),

        LOW(2.5),

        BASIC(10.0);

        private final String dislayName;
        private final double pixelSizeM;

        private HeightmapOption(double pixelSize) {
            this.dislayName = StringUtils.capitalizeFirstLetter(name().toLowerCase()) + " (" + pixelSize + "m)";
            this.pixelSizeM = pixelSize;
        }

        public double getPixelSizeM() {
            return pixelSizeM;
        }

        @Override
        public String toString() {
            return dislayName;
        }
    }

    public enum SatOption {

        SUPER(0.1), HIGH(0.25), NORMAL(0.5), LOW(2.0);

        private final String dislayName;
        private final double pixelSize;

        private SatOption(double pixelSize) {
            this.dislayName = StringUtils.capitalizeFirstLetter(name().toLowerCase()) + " (" + pixelSize + "m)";
            this.pixelSize = pixelSize;
        }

        public double getPixelSize() {
            return pixelSize;
        }

        @Override
        public String toString() {
            return dislayName;
        }
    }

    public enum Trees {

        DEFAULT("Default Trees"),

        NONE("No Trees"),

        AI("AI Generated (Utrecht) " + EngineNC.BETA);

        private String dislayName;

        private Trees(String dislayName) {
            this.dislayName = dislayName;
        }

        @Override
        public String toString() {
            return dislayName;
        }

    }

    public enum Type implements SettingType {

        NL_BGT(DEFAULT, Boolean.class, "true", "BGT (dataset not complete, more details in roads, water, streetobjects, trees)",
                Source.BGT),

        HEIGHTMAP_RESOLUTION(DEM, HeightmapOption.class, HeightmapOption.NORMAL.name(), "DEM Resolution"),

        SATELLITE_RESOLUTION(DEM, SatOption.class, SatOption.HIGH.name(), "Satellite Background Resolution"),

        @Deprecated(since = "Not available anymore since Dec 2023")
        RISK_ASSESSMENT(DEFAULT, Boolean.class, "false", "Risk Assessment (detect e.g. dangerous objects like LPG stations)",
                Source.RISICOKAART),

        AGRICULTURE(DEFAULT, Boolean.class, "true", "Agriculture (crop fields, grassland, etc)", Source.BRP),

        UNDERGROUND(DEFAULT, Boolean.class, "true", "Underground (soil type)", Source.BRO),

        OWNERSHIP(DEFAULT, Boolean.class, "true", "Ownership (cadastral data)", Source.KADASTER),

        WATER(Group.WATER, Boolean.class, "true", "Water (rivers, canals, etc)"),

        @Deprecated(since = "Not available anymore since June 2024")
        ZONING(DEFAULT, Boolean.class, "false", "Zoningplan (limitations to buildings)", Source.RUIMTELIJKE_PLANNEN),

        NL_AHN(DEM, AHN.class, AHN.AHN4.name(), "Dutch AHN version", Source.AHN4),

        NL_MIN_TREE_HEIGHT(DEM, Double.class, "5", "Tree Height from AHN Surface (min value in meters)"),

        NL_MAX_TREE_HEIGHT(DEM, Double.class, "50", "Tree Height from AHN Surface (max value in meters)"), // largest NL Tree is 50.6m

        DTM_DSM_THRESHOLDS(DEM, double[].class, "0.0 0.0 0.0", "Apply DSM to DTM Thresholds for Land, Roads and Other.", Source.NONE,
                "https://support.tygron.com/wiki/Terrain_height"),

        WORLD_WATER(Group.WATER, WorldWaterOption.class, WorldWaterOption.OSM.name(), "Water (rivers, canals, etc)"),

        OSM_ROADS(DEFAULT, Boolean.class, "true", "Use OpenStreetMap for Roads", Source.OSM),

        OSM_BUILDINGS(DEFAULT, Boolean.class, "true", "Use OpenStreetMap for Buildings", Source.OSM),

        OSM_LANDUSE(DEFAULT, Boolean.class, "true", "Use OpenStreetMap for Landuse", Source.OSM),

        OSM_NEIGHBORHOODS(DEFAULT, Boolean.class, "true", "Use OpenStreetMap for Neighborhoods", Source.OSM),

        IMWA(Group.WATER, Boolean.class, "true", "IMWA Water System constructions such as culverts", Source.IMWA),

        BAG3D_GEOMETRIES(DEFAULT, Boolean.class, "false", "Use 3D BAG by tudelft3d for Building Geometries", Source.BAG3D),

        I3S_GEOMETRIES(DEFAULT, Boolean.class, "false", "Use I3S Scenelayer for Building Geometries " + EngineNC.BETA, Item.NONE,
                "https://www.opengeospatial.org/standards/i3s"),

        NL_TRAFFIC_INWEVA(ROADS, Boolean.class, "true", "INWEVA 24h Data", Source.INWEVA),

        NL_TRAFFIC_NSL(ROADS, Boolean.class, "true", "NSL Monitoring", Source.NSL),

        NL_TRAFFIC_NSL_CONVERSION(ROADS, Double.class, "0.051", "NSL Conversion from day to hour values (default 1.22 / 24)"),

        NL_TRAFFIC_INFOMIL(ROADS, Boolean.class, "true", "InfoMil Ruleset", Source.INFOMILL),

        NL_ENERGY_LABEL(DEFAULT, Boolean.class, "true", "Address Energy Labels", Source.EP),

        INTEREST_AREA_MARGIN(CUSTOM, Double.class, "500", "Default margin around Area of Interest on file drop", Source.NONE),

        TREES(DEFAULT, Trees.class, Trees.DEFAULT.name(), "Tree options"),

        ;

        private final Group group;
        private final String defaultValue;
        private final Class<?> valueType;
        private final String description;
        private final Integer sourceID;
        private final String webLink;

        private Type(Group group, Class<?> valueType, String defaultValue, String description) {
            this(group, valueType, defaultValue, description, Item.NONE);
        }

        private Type(Group group, Class<?> valueType, String defaultValue, String description, Integer sourceID) {
            this(group, valueType, defaultValue, description, sourceID, StringUtils.EMPTY);
        }

        private Type(Group group, Class<?> valueType, String defaultValue, String description, Integer sourceID, String weblink) {
            this.group = group;
            this.valueType = valueType;
            this.defaultValue = defaultValue;
            this.description = description;
            this.sourceID = sourceID;
            this.webLink = weblink;
        }

        private Enum<?> getBasicValue() {

            if (this == HEIGHTMAP_RESOLUTION) {
                return HeightmapOption.BASIC;
            } else if (this == SATELLITE_RESOLUTION) {
                return SatOption.LOW;
            } else {
                return null;
            }
        }

        @Override
        public String getDefaultValue() {
            return getDefaultValue(null);
        }

        @Override
        public String getDefaultValue(Boolean detailed) {
            Enum<?> r = Boolean.FALSE.equals(detailed) ? getBasicValue() : null;
            return r != null ? r.name() : this.defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public Group getGroup() {
            return group;
        }

        public Integer getSourceID() {
            return sourceID;
        }

        @Override
        public Class<?> getValueType() {
            return this.valueType;
        }

        public String getWebLink() {
            return webLink;
        }

        public final boolean isDeprecated() {
            return ObjectUtils.getEnumAnnotation(this, Deprecated.class) != null;
        }
    }

    public enum WorldWaterOption {

        OSM("OpenStreetMap"), TOPO("Topograhic Map"), NONE("None");

        private final String dislayName;

        private WorldWaterOption(String dislayName) {
            this.dislayName = dislayName;

        }

        @Override
        public String toString() {
            return dislayName;
        }
    }

    public static final String DEFAULT_DTM_DSM_THRESHOLDS = "1.0 0.25 0.5";

    private static final long serialVersionUID = 5031621079005021770L;

    public String getDefaultValue() {

        Setting detailed = getItem(MapLink.SETTINGS, Setting.Type.DETAILED);
        return getType().getDefaultValue(detailed.getBooleanValue());
    }

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }

    public List<Enum<?>> getValues() {

        // simple sometimes has only one single value
        Setting detailed = getItem(MapLink.SETTINGS, Setting.Type.DETAILED);
        if (!detailed.getBooleanValue() && getType().getBasicValue() != null) {
            return Arrays.asList(getType().getBasicValue());
        }

        // height map resolution is linked to min cell size
        if (getType() == Type.HEIGHTMAP_RESOLUTION) {
            Setting setting = getItem(MapLink.SETTINGS, Setting.Type.MIN_CELL_M);
            double minCellM = setting.getDoubleValue();
            return Stream.of(HeightmapOption.values()).filter(e -> e.getPixelSizeM() >= minCellM).collect(Collectors.toList());
        }

        // convert non-deprecated enum values to string array
        return Stream.of(getType().getValueType().getEnumConstants()).map(e -> ((Enum<?>) e)).collect(Collectors.toList());
    }
}
