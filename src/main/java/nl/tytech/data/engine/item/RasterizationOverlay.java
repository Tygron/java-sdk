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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;

/**
 * Rasterize specific maplinks
 *
 * @author Frank Baars & Maxim Knepfle
 */
public abstract class RasterizationOverlay<R extends ResultType, P extends PrequelType> extends GridOverlay<R, P> {

    public enum Rasterization {

        FIRST("Attribute: First layer"),

        MIN("Attribute: Min value"),

        MAX("Attribute: Max value"),

        SINGLE_LAYER("Attribute: Specific layer"),

        GRID("Grid: Input Overlay");

        private final String title;

        private Rasterization(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private static final long serialVersionUID = -2864817941868350408L;

    @XMLValue
    private Rasterization rasterization = Rasterization.SINGLE_LAYER;

    @XMLValue
    private MapLink layerMapLink = MapLink.BUILDINGS; // default to single layer buildings

    public MapLink getLayerMapLink() {
        return layerMapLink;
    }

    public Rasterization getRasterization() {
        return rasterization;
    }

    public void setLayerMapLink(MapLink layerMapLink) {
        this.layerMapLink = layerMapLink;
    }

    public void setRasterizationType(Rasterization rasterization) {
        this.rasterization = rasterization;
    }
}
