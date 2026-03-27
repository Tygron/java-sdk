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
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.naming.GeoNC;

/**
 * Image file in TIFF format that stores GEO grid data.
 *
 * @author Maxim Knepfle & Frank Baars
 */
public class GeoTiff extends DataItem {

    public interface Format {

        public String getExtension();

        public String name();

    }

    public enum GridFormat implements Format {

        GeoTIFF("tiff", GeoNC.GEOTIFF),

        AsciiDXDY("asc", GeoNC.ASCII + ": CRS based DX DY (accurate)"),

        AsciiSquareM("asc", GeoNC.ASCII + ": Square CELLSIZE in Meters (accurate)"),

        AsciiSquareCRS("asc", GeoNC.ASCII + ": Square CRS CELLSIZE (approximation)");

        private final String extension;

        private final String humanReadable;

        private GridFormat(String extension, String humanReadable) {
            this.extension = extension;
            this.humanReadable = humanReadable;
        }

        @Override
        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return humanReadable;
        }
    }

    public enum LegendFormat implements Format {

        Points("geojson", GeoNC.GEOJSON + ": Legend Points (center)"), //

        Polygons("geojson", GeoNC.GEOJSON + ": Legend Polygons"), //

        MultiPolygons("geojson", GeoNC.GEOJSON + ": Legend MultiPolygons"),

        GpkgPoints("gpkg", GeoNC.GEOPACKAGE + ": Legend Points (center)"), //

        GpkgPolygons("gpkg", GeoNC.GEOPACKAGE + ": Legend Polygons"), //

        GpkgMultiPolygons("gpkg", GeoNC.GEOPACKAGE + ": Legend MultiPolygons");

        private final String extension;

        private final String humanReadable;

        private LegendFormat(String extension, String humanReadable) {
            this.extension = extension;
            this.humanReadable = humanReadable;
        }

        @Override
        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return humanReadable;
        }
    }

    private static final long serialVersionUID = 5371401657101249581L;

    public static final long MAX_EXPORT_AREA = Integer.MAX_VALUE;

    public static final Format getFormat(String format) {

        for (GridFormat f : GridFormat.values()) {
            if (f.name().equalsIgnoreCase(format)) {
                return f;
            }
        }
        for (LegendFormat f : LegendFormat.values()) {
            if (f.name().equalsIgnoreCase(format)) {
                return f;
            }
        }
        if (format.toLowerCase().startsWith("ascii")) {
            return GridFormat.AsciiDXDY; // default for ascii formats
        }
        if (format.toLowerCase().startsWith("tiff")) {
            return GridFormat.GeoTIFF; // tiff == geotiff
        }
        if (format.toLowerCase().startsWith("geojson")) {
            return LegendFormat.MultiPolygons; // default multi polygon
        }
        return null;
    }

    @Override
    public String getExtension() {
        return GridFormat.GeoTIFF.getExtension();
    }

    @Override
    public List<Item> getLinks() {

        List<Item> links = new ArrayList<>();
        for (Item item : getMap(MapLink.OVERLAYS)) {
            if (item instanceof GeoTiffOverlay gto && gto.hasGeoTiffID(this.getID())) {
                links.add(item);
            }
        }
        for (Item item : getMap(MapLink.MEASURES)) {
            if (item instanceof MapMeasure m && m.hasGeoTiffID(this.getID())) {
                links.add(item);
            }
        }
        return links;
    }

}
