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

import nl.tytech.core.item.annotations.Description;
import nl.tytech.util.ObjectUtils;

/**
 *
 * Supported GEO formats
 *
 * @author Maxim Knepfle
 *
 */
public enum GeoFormat {

    @Description("Feature format of GeoJSON.org")
    GEOJSON(true),

    @Description("Open Geospatial Consortium GeoPackage")
    GEOPACKAGE(true),

    @Description("Open Geospatial Consortium WFS format for GeoJSON features")
    WFS_JSON(true),

    @Description("Open Geospatial Consortium WFS format for GML features")
    WFS_GML(true),

    @Description("Open Geospatial Consortium WMS format for PNG/JPG Images")
    WMS_IMAGE(false),

    @Description("Open Geospatial Consortium WMS format for TIFF Coverages")
    WCS_TIFF(false),

    @Description("Open Street Maps format for XML features")
    OSM(true),

    @Description("BGT Extract Zipped GML file format")
    BGT_GML(true),

    @Description("ESRI format for JSON features")
    ESRI_JSON(true),

    @Description("ESRI format for PNG/JPG Images")
    ESRI_IMAGE(false),

    @Description("OGC I3S format")
    I3S(true),

    @Description("Direct query to a GeoTIFF")
    GEOTIFF(false),

    @Description("AutoCAD Drawing Exchange Format")
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

    public final String getDescription() {
        return ObjectUtils.getDescription(this);
    }

    public final boolean hasLayers() {
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
