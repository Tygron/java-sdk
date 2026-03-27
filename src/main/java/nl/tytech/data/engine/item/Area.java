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

import java.util.TreeMap;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.ItemIntersector;
import nl.tytech.util.ItemIntersector.IntersectorItem;

/**
 * @author Maxim Knepfle
 */
public class Area extends PolygonAttributeItem implements ActiveItem, IntersectorItem {

    public enum AreaAttribute implements ReservedAttribute {

        ACTIVE(Boolean.class, 1),

        NATURE_RESERVE(Boolean.class, 0),

        INTEREST_AREA(Boolean.class, 1);

        private final Class<?> type;
        private final double[] defaultArray;

        private AreaAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }

    }

    private static final long serialVersionUID = 1362611699406604463L;

    @JsonIgnore
    public transient ItemIntersector intersector = null;

    @XMLValue
    private TreeMap<Relation, Integer> relations = null;

    public Area() {
    }

    public Area(String name) {
        setName(name);
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return AreaAttribute.values();
    }

    @Override
    public GeometryCollection getIntersectorGeometies(MapType mapType) {
        return this.getMultiPolygon();
    }

    @Override
    public Integer getRelationID(Relation relation) {

        Integer linkID = relations != null && relation != null ? relations.get(relation) : null;
        return linkID == null ? Item.NONE : linkID;
    }

    @Override
    public final boolean isActive() {
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

    public Integer removeRelation(Relation relation) {
        return relations != null && relation != null ? relations.remove(relation) : null;
    }

    public final void setActive(boolean active) {
        setAttributeArray(AreaAttribute.ACTIVE, active ? ONE : ZERO);
    }

    public void setRelation(Relation relation, Integer linkID) {

        if (relation == null) {
            return;
        }
        if (relations == null) {
            relations = new TreeMap<>();
        }
        relations.put(relation, linkID);
    }
}
