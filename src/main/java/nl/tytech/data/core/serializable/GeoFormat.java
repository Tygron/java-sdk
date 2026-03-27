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
package nl.tytech.data.core.serializable;

/**
 *
 * Supported GEO formats
 *
 * @author Maxim Knepfle
 *
 */
public enum GeoFormat {

    /**
     * Direct query to GeoJSON
     */
    GEOJSON(true),

    /**
     * Open Geospatial Consortium GeoPackage
     */
    GEOPACKAGE(true),

    /**
     * Open Geospatial Consortium WFS format for JSON features
     */
    WFS_JSON(true),

    /**
     * Open Geospatial Consortium WFS format for GML features
     */
    WFS_GML(true),

    /**
     * Open Geospatial Consortium WMS format for PNG/JPG Images
     */
    WMS_IMAGE(false),

    /**
     * Open Geospatial Consortium WMS format for TIFF Coverages
     */
    WCS_TIFF(false),

    /**
     * Open Street Maps format for XML features
     */
    OSM(true),

    /**
     * BGT Extract ZIP file format
     */
    BGT_GML(true),

    /**
     * ESRI format for JSON features
     */
    ESRI_JSON(true),

    /**
     * ESRI format for PNG/JPG Images
     */
    ESRI_IMAGE(false),

    /**
     * Cyclomedia own Image format
     */
    CYCLO_IMAGE(false),

    /**
     * OGC I3S format
     */
    I3S(true),

    /**
     * Direct query to GeoTIFF
     */
    GEOTIFF(false),

    /**
     * Autocad Exchange format
     */
    DXF(true),

    ;

    /**
     * User Selectable Coverage formats
     */
    public static GeoFormat[] getSelectableCoverageFormats() {
        return new GeoFormat[] { WCS_TIFF };
    }

    /**
     * User Selectable Feature formats, supported all the way
     */
    public static GeoFormat[] getSelectableFeatureFormats() {
        return new GeoFormat[] { WFS_JSON, WFS_GML, ESRI_JSON, I3S };
    }

    /**
     * User Selectable values, supported all the way
     */
    public static GeoFormat[] getSelectableImageFormats() {
        return new GeoFormat[] { WMS_IMAGE, ESRI_IMAGE };
    }

    private final boolean feature;

    private GeoFormat(boolean feature) {
        this.feature = feature;
    }

    public boolean hasLayers() {
        switch (this) {
            case BGT_GML:
            case WFS_JSON:
            case WFS_GML:
            case WCS_TIFF:
            case WMS_IMAGE:
            case ESRI_IMAGE:
            case ESRI_JSON:
            case I3S:
                return true;
            default:
                return false;
        }
    }

    public boolean isFeature() {
        return feature;
    }
}
