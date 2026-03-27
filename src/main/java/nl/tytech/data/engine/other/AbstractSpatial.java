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
package nl.tytech.data.engine.other;

import java.io.Serializable;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.MapMeasure;

/**
 * Abstract Spatial that is part of Measure
 *
 * @author Frank Baars
 *
 */
public abstract class AbstractSpatial implements Serializable {

    public abstract static class SpatialItem<S extends AbstractSpatial> extends Item {

        private static final long serialVersionUID = 1416745359837116460L;

        @JsonIgnore
        protected final S spatial;

        @JsonIgnore
        protected final MapMeasure mapMeasure;

        public SpatialItem(MapMeasure mapMeasure, S spatial) {

            this.spatial = spatial;
            this.mapMeasure = mapMeasure;

            setId(spatial.getID());
            setLord(mapMeasure.getLord());
            setVersion(mapMeasure.getVersion());
        }

        @Override
        public Geometry getExportGeometry() {
            return spatial.getMultiPolygon();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " " + getID();
        }

    }

    private static final long serialVersionUID = 3231128699372172646L;

    public static final String SPATIAL_TYPE = "SPATIAL_TYPE";
    public static final String SPATIAL_ID = "SPATIAL_ID";
    public static final String HEIGHT = "HEIGHT";
    public static final String RELATIVE = "RELATIVE";
    public static final String AUTOMP = "AUTOMP";

    @XMLValue
    private Integer id = Item.NONE;

    public AbstractSpatial(Integer id) {
        this.id = id;
    }

    public final Integer getID() {
        return id;
    }

    public abstract MultiPolygon getMultiPolygon();

    public final void setID(Integer id) {
        this.id = id;
    }
}
