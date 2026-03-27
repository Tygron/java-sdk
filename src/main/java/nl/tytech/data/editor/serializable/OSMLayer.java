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
package nl.tytech.data.editor.serializable;

/**
 * Open Streetmap Layers
 *
 * @author Jurrian Hartveldt & Frank Baars
 *
 */
public enum OSMLayer {

    AERIALWAY, //
    AEROWAY, //
    AMENITY, //
    BARRIER, //
    BOUNDARY, //
    BUILDINGS("building"), //
    CONSTRUCTION, //
    CRAFT, //
    EMERGENCY, //
    GEOLOGICAL, //
    ROADS("highway"), //
    HISTORIC, //
    LANDUSE, //
    LEISURE, //
    MAN_MADE, //
    MILITARY, //
    NATURAL, //
    OFFICE, //
    PLACES("place"), //
    POWER, //
    PUBLIC_TRANSPORT, //
    RAILWAYS("railway"), //
    ROUTE, //
    SHOP, //
    SPORT, //
    TOURISM, //
    TUNNEL, //
    WATERWAYS("waterway"), //
    BRIDGES("bridge"),

    ;

    public static final OSMLayer[] VALUES = OSMLayer.values();

    public static String[] toString(OSMLayer[] layers) {
        String[] result = new String[layers.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = layers[i].getOsmName();
        }
        return result;
    }

    private String osmName;

    private OSMLayer() {
        this.osmName = this.name().toLowerCase();
    }

    private OSMLayer(String osmName) {
        this.osmName = osmName;
    }

    public String getOsmName() {
        return osmName;
    }
}
