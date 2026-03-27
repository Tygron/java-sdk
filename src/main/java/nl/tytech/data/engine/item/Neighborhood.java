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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Area.AreaAttribute;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.util.ItemIntersector;
import nl.tytech.util.ItemIntersector.IntersectorItem;

/**
 * Neighborhood
 *
 * @author Maxim Knepfle
 *
 */
public class Neighborhood extends PolygonAttributeItem implements ActiveItem, IntersectorItem {

    public enum NeighborhoodAttribute implements ReservedAttribute {

        URBANIZATION(Double.class, 0),

        INHABITANTS(Double.class, 0),

        ACTIVE(Boolean.class, 1),

        ;

        private final Class<?> type;
        private final double defaultValue;

        private NeighborhoodAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public double[] defaultArray() {
            return new double[] { defaultValue() };
        }

        @Override
        public double defaultValue() {
            return defaultValue;
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    private static final long serialVersionUID = -8951270041649371970L;

    @JsonIgnore
    public transient ItemIntersector intersector = null;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer municipalityID = Item.NONE;

    public int getCityDegree() {
        return (int) Math.round(getAttribute(NeighborhoodAttribute.URBANIZATION));
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return NeighborhoodAttribute.values();
    }

    @Override
    public GeometryCollection getIntersectorGeometies(MapType mapType) {
        return this.getMultiPolygon();
    }

    public Stakeholder getMunicipality() {
        return getItem(MapLink.STAKEHOLDERS, municipalityID);
    }

    public Integer getMunicipalityID() {
        return municipalityID;
    }

    @Override
    public boolean isActive() {
        return this.getAttribute(AreaAttribute.ACTIVE) > 0.0;
    }

    public final boolean isCovers(MapType mapType, int rasterID, int rasters, double cellM, Geometry superCell, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isCovers(mapType, cellM, superCell, cacheID, rasterID);
    }

    public final boolean isIntersect(MapType mapType, int rasterID, int rasters, double cellM, Geometry cell, Point center, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isIntersect(mapType, cellM, cell, center, cacheID, rasterID);
    }

    public final void setActive(boolean active) {
        setAttributeArray(NeighborhoodAttribute.ACTIVE, active ? ONE : ZERO);
    }

    public void setMunicipalityID(Integer id) {
        this.municipalityID = id;
    }
}
