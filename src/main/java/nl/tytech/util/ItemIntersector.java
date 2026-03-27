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
package nl.tytech.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import nl.tytech.core.util.IntBooleanMap;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Building.Layer;

/**
 * Used by Grid Calculator to cache or prepare previous intersects and covers
 *
 * @author Maxim Knepfle
 */
public final class ItemIntersector {

    public interface IntersectorItem {

        public GeometryCollection getIntersectorGeometies(MapType mapType);

        public int getVersion();
    }

    /**
     * Margin for diamond shaped objects that must cover a grid cell square <[]>
     */
    private static final double DIAMOND_MARGIN = 1.01;

    /**
     * Simple helper method with less overhead then JTSUtils.createSquare()
     */
    private static final Polygon createSquare(Point center, double cellSizeM) {

        double x = center.getX() - 0.5 * cellSizeM;
        double y = center.getY() - 0.5 * cellSizeM;
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(x, y);
        coordinates[1] = new Coordinate(x, y + cellSizeM);
        coordinates[2] = new Coordinate(x + cellSizeM, y + cellSizeM);
        coordinates[3] = new Coordinate(x + cellSizeM, y);
        coordinates[4] = new Coordinate(x, y);
        LinearRing outer = JTSUtils.sourceFactory.createLinearRing(coordinates);
        return JTSUtils.sourceFactory.createPolygon(outer, null);
    }

    private static final boolean hasSmallPolygon(GeometryCollection gc, double cellSizeM) {

        for (int n = 0; n < gc.getNumGeometries(); n++) {
            Geometry child = gc.getGeometryN(n);
            if (child instanceof GeometryCollection childGC) {
                if (hasSmallPolygon(childGC, cellSizeM)) {
                    return true;
                }
            } else if (!child.isEmpty()) {
                // catch smaller then cell objects
                Envelope env = child.getEnvelopeInternal();
                if (env.getWidth() <= cellSizeM || env.getHeight() <= cellSizeM) {
                    return true;
                }
                // catch e.g. diamond shaped objects -> larger envelope, polygon between center points <[]>
                Polygon square = createSquare(child.getCentroid(), cellSizeM);
                if (!JTSUtils.covers(child, square)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final ItemIntersector init(final ItemIntersector old, final IntersectorItem item, int cacheID, int rasters) {

        if (old == null || old.different(cacheID, rasters)) {
            return new ItemIntersector(item, cacheID, rasters);
        }
        return old;
    }

    private final IntBooleanMap[][] cacheMaps;

    private final PreparedGeometry[] prepGeometry = new PreparedGeometry[MapType.VALUES.length];

    private final int[] version = new int[MapType.VALUES.length];

    private final boolean[] small = new boolean[MapType.VALUES.length];

    private final boolean[] surface = new boolean[MapType.VALUES.length];

    private final IntersectorItem item;

    private ItemIntersector(IntersectorItem item, int cacheID, int rasters) {

        // start caching when ID is positive, thus cache required
        this.cacheMaps = cacheID >= 0 ? new IntBooleanMap[MapType.VALUES.length][rasters] : null;
        this.item = item;
    }

    private final boolean different(int cacheID, int rasters) {
        return cacheID >= 0 ? cacheMaps == null || cacheMaps[0].length != rasters : false;
    }

    private final void init(final MapType mapType, final double thresholdM) {

        if (cacheMaps != null) { // cache is active
            for (int r = 0; r < cacheMaps[0].length; r++) {
                cacheMaps[mapType.ordinal()][r] = new IntBooleanMap(8);
            }
        }
        GeometryCollection gc = item.getIntersectorGeometies(mapType);
        prepGeometry[mapType.ordinal()] = JTSUtils.prepare(gc);

        // check if any of the object geometries might fall between cell center points
        small[mapType.ordinal()] = thresholdM >= Double.MAX_VALUE || hasSmallPolygon(gc, thresholdM * DIAMOND_MARGIN);

        // check is this is a solid surface building (relative expensive)
        if (item instanceof Building b) {
            surface[mapType.ordinal()] = b.getLayer() != Layer.UNDERGROUND && b.isSolid();
        }
    }

    public final boolean isCovers(final MapType mapType, final double thresholdM, final Geometry cell, int cacheID, int rasterID) {

        sync(mapType, thresholdM);

        // skip cache
        if (cacheMaps == null) {
            return JTSUtils.covers(prepGeometry[mapType.ordinal()], cell);
        }

        // get from cache
        IntBooleanMap map = cacheMaps[mapType.ordinal()][rasterID];
        Boolean oldValue;
        synchronized (map) { // try old in sync
            oldValue = map.get(cacheID);
        }
        if (oldValue != null) {
            return oldValue.booleanValue();
        }

        // calc new
        boolean newValue = JTSUtils.covers(prepGeometry[mapType.ordinal()], cell);
        synchronized (map) { // in sync
            map.put(cacheID, newValue);
        }
        return newValue;
    }

    public final boolean isIntersect(final MapType mapType, final double thresholdM, final Geometry cell, final Point center, int cacheID,
            int rasterID) {

        sync(mapType, thresholdM);

        // for small geometries intersect on cell otherwise use cell's center point
        Geometry g = small[mapType.ordinal()] ? cell : center;

        // skip cache
        if (cacheMaps == null) {
            return JTSUtils.intersectsBorderIncluded(prepGeometry[mapType.ordinal()], g);
        }

        // get from cache
        IntBooleanMap map = cacheMaps[mapType.ordinal()][rasterID];
        Boolean oldValue;
        synchronized (map) { // try old in sync
            oldValue = map.get(cacheID);
        }
        if (oldValue != null) {
            return oldValue.booleanValue();
        }

        // calc new
        boolean newValue = JTSUtils.intersectsBorderIncluded(prepGeometry[mapType.ordinal()], g);
        synchronized (map) { // in sync
            map.put(cacheID, newValue);
        }
        return newValue;
    }

    /**
     * True when object envelope is smaller then thresholdM
     */
    public final boolean isSmall(final MapType mapType, final double thresholdM) {

        // other thread maybe in init(), thus sync first
        sync(mapType, thresholdM);
        return small[mapType.ordinal()];
    }

    /**
     * True when surface building (not underground) and is solid (attribute)
     */
    public final boolean isSurface(final MapType mapType, final double thresholdM) {

        // other thread maybe in init(), thus sync first
        sync(mapType, thresholdM);
        return surface[mapType.ordinal()];
    }

    /**
     * Sync: Item was changed, clear out cache and run init() again!
     */
    private final void sync(final MapType mapType, final double thresholdM) {

        if (item.getVersion() > version[mapType.ordinal()]) {
            synchronized (item) {
                // check again inside lock
                if (item.getVersion() > version[mapType.ordinal()]) {
                    init(mapType, thresholdM);
                    version[mapType.ordinal()] = item.getVersion();
                }
            }
        }
    }
}
