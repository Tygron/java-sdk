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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.vecmath.Point3d;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.algorithm.InteriorPoint;
import org.locationtech.jts.algorithm.locate.IndexedPointInAreaLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Puntal;
import org.locationtech.jts.geom.SearchPoint;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.geom.util.PolygonExtracter;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.overlay.snap.SnapIfNeededOverlayOp;
import org.locationtech.jts.precision.GeometryPrecisionReducer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.polygon.PolygonTriangulator;
import org.locationtech.jts.triangulate.tri.Tri;
import org.locationtech.jts.util.AssertionFailedException;
import nl.tytech.util.jts.EmptyMultiPolygon;
import nl.tytech.util.jts.PreparedCollection;
import nl.tytech.util.logger.TLogger;
import straightskeleton.Corner;
import straightskeleton.Edge;
import straightskeleton.Machine;
import straightskeleton.Output.Face;
import straightskeleton.Output.SharedEdge;
import straightskeleton.Skeleton;
import utils.Loop;
import utils.LoopL;

/**
 * Helper methods for JTS
 * @author Maxim Knepfle
 *
 */
public class JTSUtils {

    private static final BufferParameters flatParams = new BufferParameters();

    static {
        flatParams.setEndCapStyle(BufferParameters.CAP_FLAT);
        flatParams.setJoinStyle(BufferParameters.JOIN_MITRE);
        flatParams.setQuadrantSegments(1);
    }
    private static final BufferParameters roundParams = new BufferParameters();

    static {
        roundParams.setEndCapStyle(BufferParameters.CAP_ROUND);
        roundParams.setJoinStyle(BufferParameters.JOIN_MITRE);
        roundParams.setQuadrantSegments(8);
    }
    public static final GeometryFactory sourceFactory = new GeometryFactory(new PrecisionModel(1000d));

    public static final GeometryFactory overlayOperationFactory = new GeometryFactory(new PrecisionModel(1000d * 1000d));

    public static final double TOLERANCE = 0.1d;

    public static final double PRECISION = 1e-3;

    /**
     * Buffering near the precision fails add safety at 100x precision, see: https://github.com/locationtech/jts/issues/1143
     */
    private static final double BUFFER_PRECISION = 100.0 * PRECISION;

    /**
     * Allowed margin is 2X the smallest precision value
     */
    public static final double INTERSECTION_BORDER_MARGIN = 2d / sourceFactory.getPrecisionModel().getScale();

    /**
     * Erode and dilate at 10X the smallest precision value
     */
    public static final double ERODE_DILATE_MARGIN = 10d / sourceFactory.getPrecisionModel().getScale();

    public static final MultiPolygon EMPTY = new EmptyMultiPolygon(sourceFactory);

    /**
     * Approximate byte size of a Coordinate object (16 bytes for header + coordinates), confirmed with Yourkit (40 bytes)
     */
    private static final long COORDINATE_BYTES = 3 * Double.BYTES + 16;

    public static final boolean anyCovers(List<? extends Geometry> geometries, Point point) {

        // point can be covered by any geometry in list
        for (int i = 0; i < geometries.size(); i++) {
            Geometry g = geometries.get(i);
            if (g != null && covers(g, point)) {
                return true;
            }
        }
        return false;
    }

    public static final Geometry bufferSimple(Geometry geometry, double distance) {
        return bufferSimple(geometry, distance, geometry instanceof Puntal ? roundParams : flatParams);
    }

    public static final Geometry bufferSimple(Geometry geometry, double distance, BufferParameters params) {

        // buffer zero for everything smaller the precision
        if (distance == 0.0 || Math.abs(distance) < PRECISION) {
            return BufferOp.bufferOp(geometry, 0.0, params);
        }

        // small buffers need higher precision
        if (Math.abs(distance) <= BUFFER_PRECISION) {
            return toLocalPrecision(BufferOp.bufferOp(overlayOperationFactory.createGeometry(geometry), distance, params));
        }

        // normal operation
        return BufferOp.bufferOp(geometry, distance, params);
    }

    private static final Geometry bufferZero(Geometry geometry) {
        return bufferSimple(geometry, 0.0);
    }

    public static final void clearUserData(Geometry geometry) {

        if (geometry == null) {
            return;
        }

        // clear myself
        geometry.setUserData(null);

        // clear children
        for (int n = 0; n < geometry.getNumGeometries(); n++) {
            Geometry child = geometry.getGeometryN(n);
            if (child != geometry) {
                clearUserData(geometry.getGeometryN(n));
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static final <T extends Geometry> T clone(Geometry original) {

        if (original == null) {
            return null; // Cloned as NULL
        }

        T clone = (T) original.clone(); // clone it
        clearUserData(clone); // clear user data from clone
        return clone;
    }

    public static final Coordinate[] closestPoints(Geometry geometry1, Geometry geometry2) {
        try {
            if (!isEmpty(geometry1) && !isEmpty(geometry2)) {
                return DistanceOp.nearestPoints(geometry1, geometry2);
            }
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    public static final Geometry convexHull(Collection<Coordinate> coordinates) {
        return convexHull(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

    public static final Geometry convexHull(Coordinate[] coordinates) {
        ConvexHull hull = new ConvexHull(coordinates, sourceFactory);
        return hull.getConvexHull();
    }

    public static final boolean covers(Geometry geometry1, Geometry geometry2) {

        // Note: if first geometry is collection this is handled by PreparedCollection
        PreparedGeometry preparedGeometry1 = prepare(geometry1);
        return covers(preparedGeometry1, geometry2);
    }

    public static final boolean covers(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        // second geometry is collection
        if (isGeometryCollection(geometry2)) {
            GeometryCollection gc2 = (GeometryCollection) geometry2;
            for (int n = 0; n < gc2.getNumGeometries(); n++) {
                if (!covers(preparedGeometry1, gc2.getGeometryN(n))) {
                    return false;
                }
            }
            return true;
        }

        try {
            // cheap method to test covers first
            return preparedGeometry1.covers(geometry2);

        } catch (TopologyException | AssertionFailedException e) {
            // TLogger.warning("Covers fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
        }

        /**
         * Fall back scenario adding border margin
         */

        // point cannot to negative margin test, thus must be contained in geom 1
        if (geometry2 instanceof Point) {
            // both are points, skip overlap test (unlikely)
            if (preparedGeometry1.getGeometry() instanceof Point) {
                return preparedGeometry1.contains(geometry2);
            }
            // add margin to geom 1
            Geometry geometry1 = bufferSimple(preparedGeometry1.getGeometry(), -INTERSECTION_BORDER_MARGIN);
            preparedGeometry1 = prepare(geometry1);
            return preparedGeometry1.covers(geometry2);
        }

        // try expensive more robust method
        return preparedGeometry1.covers(bufferSimple(geometry2, -INTERSECTION_BORDER_MARGIN));
    }

    public static final GeometryCollection createCollection(Collection<? extends Geometry> geoms) {
        return createCollection(geoms.toArray(new Geometry[geoms.size()]));
    }

    public static final GeometryCollection createCollection(Geometry... geoms) {
        return new GeometryCollection(geoms, sourceFactory);
    }

    public static Geometry createEllipse(Coordinate mid, double width, double height, double rotation, double segmentSize) {
        return createEllipse(mid, width, height, rotation, segmentSize, 0, Math.PI * 2);
    }

    public static Geometry createEllipse(Coordinate mid, double width, double height, double rotation, double segmentSize,
            double startAngle, double endAngle) {

        List<Coordinate> pts = createEllipseCoords(mid, width, height, segmentSize, startAngle, endAngle);
        if (pts == null) {
            return JTSUtils.EMPTY;
        }

        Geometry geom;
        if (pts.getFirst().equals(pts.getLast())) {
            geom = JTSUtils.createPolygon(pts);
        } else {
            geom = JTSUtils.createLine(pts.toArray(new Coordinate[pts.size()]));
        }

        if (rotation != 0) {
            AffineTransformation trans = AffineTransformation.rotationInstance(rotation, mid.x, mid.y);
            geom.apply(trans);
        }
        return geom;
    }

    public static List<Coordinate> createEllipseCoords(Coordinate mid, double width, double height, double segmentSize, double startAngle,
            double endAngle) {
        // SOURCE: http://mathworld.wolfram.com/Ellipse.html
        double perimeter = (endAngle - startAngle) / 2 * Math.sqrt(2d * (width / 2d * (width / 2d) + height / 2d * (height / 2d)));
        int numberOfPoints = Math.max(3, (int) Math.ceil(perimeter / segmentSize));

        if (perimeter < 0) {
            return null;
        }

        List<Coordinate> pts = new ArrayList<>();
        boolean closed = Math.abs(Math.abs(endAngle - startAngle) - 2 * Math.PI) < 0.001;
        double step = (endAngle - startAngle) / numberOfPoints;
        double angle = startAngle;
        for (int i = 0; i < numberOfPoints && angle <= endAngle; i++, angle += step) {

            pts.add(new Coordinate(//
                    0.5d * width * Math.cos(angle) + mid.x, //
                    0.5d * height * Math.sin(angle) + mid.y//
            ));
        }
        if (closed) {
            pts.add(new Coordinate(pts.getFirst()));
        }
        return pts;
    }

    public static final List<LineString> createGrid(double width, double height, double step) {

        List<LineString> lines = new ArrayList<>();
        for (double x = 0; x < width; x += step) {
            Coordinate[] coordinates = new Coordinate[2];
            coordinates[0] = new Coordinate(x, 0, 0);
            coordinates[1] = new Coordinate(x, height, 0);
            lines.add(sourceFactory.createLineString(coordinates));
        }

        for (double y = 0; y < height; y += step) {
            Coordinate[] coordinates = new Coordinate[2];
            coordinates[0] = new Coordinate(0, y, 0);
            coordinates[1] = new Coordinate(width, y, 0);
            lines.add(sourceFactory.createLineString(coordinates));
        }
        return lines;
    }

    public static final LineString createLine(Coordinate... coordinates) {

        for (Coordinate c : coordinates) {
            c.x = sourceFactory.getPrecisionModel().makePrecise(c.x);
            c.y = sourceFactory.getPrecisionModel().makePrecise(c.y);
            c.z = sourceFactory.getPrecisionModel().makePrecise(c.z);
        }
        return sourceFactory.createLineString(coordinates);
    }

    public static final LineString createLine(Geometry... geometries) {

        Coordinate[] coordinates = new Coordinate[geometries.length];
        for (int i = 0; i < geometries.length; i++) {
            coordinates[i] = geometries[i].getCentroid().getCoordinate();
        }
        return createLine(coordinates);
    }

    public static final LinearRing createLinearRing(Coordinate... coordinates) {

        for (Coordinate c : coordinates) {
            c.x = sourceFactory.getPrecisionModel().makePrecise(c.x);
            c.y = sourceFactory.getPrecisionModel().makePrecise(c.y);
            c.z = sourceFactory.getPrecisionModel().makePrecise(c.z);
        }
        return sourceFactory.createLinearRing(coordinates);
    }

    public static final Geometry createLocalPrecision(Geometry input) {

        // keep collection intact and reduce each child
        if (!isGeometryCollection(input)) {
            return toLocalPrecision(input);
        }
        GeometryCollection gc = (GeometryCollection) input;
        Geometry[] geoms = new Geometry[gc.getNumGeometries()];
        for (int n = 0; n < gc.getNumGeometries(); n++) {
            geoms[n] = createLocalPrecision(gc.getGeometryN(n));
        }
        return createCollection(geoms);
    }

    public static final MultiPolygon createMP(Geometry... geometries) {

        if (geometries == null || geometries.length == 0) {
            return EMPTY;
        }
        if (geometries.length == 1) {
            return createMP(geometries[0]);
        }
        return createMP(Arrays.asList(geometries));
    }

    public static final MultiPolygon createMP(Geometry geometry) {

        // Filter out points, lines, empty polygons, etc
        if (!hasArea(geometry)) {
            return EMPTY;
        }

        // handle as collection object
        if (isGeometryCollection(geometry)) {
            return createMP(((GeometryCollection) geometry).getGeometries());
        }

        // check validity
        Geometry result = validate(geometry);

        // clone to make sure its unique
        if (result == geometry) {
            result = clone(result);
        }

        if (result instanceof MultiPolygon mp) {
            return mp;

        } else if (result instanceof Polygon polygon) {
            return new MultiPolygon(new Polygon[] { polygon }, sourceFactory);

        } else {
            TLogger.severe("Unknown geometry created: " + result.toString());
            return null;
        }
    }

    /**
     * Creates a MultiPolygon of the polygons and discards all others (points, lines, etc)
     * @param geometry
     * @return
     */
    public static final MultiPolygon createMP(SequencedCollection<? extends Geometry> geometries) {

        // no polys is empty
        if (geometries == null || geometries.isEmpty()) {
            return EMPTY;
        }
        // when only single polygonal then validate and clone
        if (geometries.size() == 1) {
            Geometry g = geometries.getFirst();
            if (!isGeometryCollection(g)) {
                return createMP(g);
            }
        }

        List<Geometry> validPolyons = new ArrayList<>();
        for (Geometry geometry : geometries) {
            // validate and or fix it first
            geometry = validate(geometry);
            // extract polygons to list validPolyons
            PolygonExtracter.getPolygons(geometry, validPolyons);
        }

        // tricky bit, geom precision must be applied here to prevent Topology Exceptions
        Polygon[] pa = new Polygon[validPolyons.size()];
        GeometryCollection gc = new GeometryCollection(validPolyons.toArray(pa), overlayOperationFactory);
        Geometry result = toLocalPrecision(gc); // reduce and do buffer here

        return createMP(result);
    }

    public static final MultiLineString createMultiLineString(Collection<? extends Geometry> geometries) {

        LineString[] lines = geometries.stream().filter(g -> g instanceof LineString).toArray(LineString[]::new);
        return new MultiLineString(lines, sourceFactory);
    }

    public static final MultiLineString createMultiLineString(MultiPolygon mp) {

        LineString[] lines = getPolygons(mp).stream().map(p -> p.getExteriorRing()).toArray(LineString[]::new);
        return new MultiLineString(lines, sourceFactory);
    }

    public static final MultiPoint createMultiPoint(List<Point> points) {
        return new MultiPoint(points.stream().filter(g -> g != null).toArray(Point[]::new), sourceFactory);
    }

    public static final Geometry createOrientedRectangle(Point point, double width, double height, double angle) {

        LineSegment ls = new LineSegment(point.getX(), point.getY(), point.getX() + Math.cos(angle), point.getY() + Math.sin(angle));
        LinearRing ring = createLinearRing(ls.pointAlongOffset(width / 2, height / 2), ls.pointAlongOffset(width / 2, -height / 2),
                ls.pointAlongOffset(-width / 2, -height / 2), ls.pointAlongOffset(-width / 2, height / 2),
                ls.pointAlongOffset(width / 2, height / 2));
        return createPolygon(ring, new LinearRing[0]);
    }

    public static final Point createPoint(Coordinate coordinate) {
        return createPoint(coordinate, true);
    }

    /**
     * Use local precision when storing this point, false for temp calculation point values
     */
    public static final Point createPoint(Coordinate coordinate, boolean localPrecision) {

        Point p = sourceFactory.createPoint(coordinate);
        if (localPrecision) {
            return (Point) toLocalPrecision(p);
        } else {
            return p;
        }
    }

    public static final Point createPoint(double x, double y) {
        return createPoint(new Coordinate(x, y));
    }

    public static final Point createPoint(double x, double y, double z) {
        return createPoint(new Coordinate(x, y, z));
    }

    public static final List<Point> createPoints(Geometry geom, double buffer, boolean random, double angle) {
        return createPoints(geom, buffer, buffer, random, angle);
    }

    public static final List<Point> createPoints(Geometry geom, double dx, double dy, boolean random, double angle) {

        dx = Math.max(1, dx);// buffer should be at least 1m
        dy = Math.max(1, dy);// buffer should be at least 1m
        List<Point> points = new ArrayList<>();

        if (!hasArea(geom)) {
            return points;
        }

        IndexedPointInAreaLocator locator = new IndexedPointInAreaLocator(geom);
        Envelope env = getEnvelope(geom);
        double minx = env.getMinX();
        double miny = env.getMinY();

        int width = (int) (env.getWidth() / dx);
        int height = (int) (env.getHeight() / dy);

        LineSegment dir = new LineSegment(new Coordinate(minx, miny), new Coordinate(minx + Math.cos(angle), miny + Math.sin(angle)));

        if (angle != 0.0) {
            LineSegment yls = new LineSegment(dir.p0, dir.pointAlongOffset(0, 1));

            double dxmin = Double.MAX_VALUE;
            double dxmax = -Double.MAX_VALUE;
            double dymin = Double.MAX_VALUE;
            double dymax = -Double.MAX_VALUE;
            for (Coordinate coordinate : geom.getCoordinates()) {
                double xprojection = dir.projectionFactor(coordinate);
                dxmin = Math.min(dxmin, xprojection);
                dxmax = Math.max(dxmax, xprojection);
                double yprojection = yls.projectionFactor(coordinate);
                dymin = Math.min(dymin, yprojection);
                dymax = Math.max(dymax, yprojection);
            }

            width = (int) ((dxmax - dxmin) / dx);
            height = (int) ((dymax - dymin) / dy);

            Coordinate start = dir.pointAlongOffset(dxmin, dymin);
            dir = new LineSegment(start, new Coordinate(start.x + Math.cos(angle), start.y + Math.sin(angle)));
        }

        PrecisionModel model = sourceFactory.getPrecisionModel();

        // random location within a grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Coordinate coordinate = dir.pointAlongOffset(//
                        (x + (random ? Math.random() : 0.5)) * dx, //
                        (y + (random ? Math.random() : 0.5)) * dy);
                coordinate.x = model.makePrecise(coordinate.x);
                coordinate.y = model.makePrecise(coordinate.y);

                if (locator.locate(coordinate) != Location.EXTERIOR) {
                    points.add(createPoint(coordinate, false));
                }
            }
        }
        return points;
    }

    public static final Geometry createPolygon(LinearRing outer) {
        return createPolygon(outer, null);
    }

    public static final Geometry createPolygon(LinearRing outer, LinearRing[] inner) {
        return bufferZero(sourceFactory.createPolygon(outer, inner));
    }

    public static final Geometry createPolygon(List<Coordinate> coordinates) {

        if (coordinates.size() < 3) {
            return null;
        }

        Coordinate[] array;
        Coordinate first = coordinates.getFirst();
        Coordinate last = coordinates.getLast();

        if (first.equals(last)) {
            if (coordinates.size() < 4) {
                return null;
            }
            array = new Coordinate[coordinates.size()];
            if (first == last) {
                last = new Coordinate(last);
            }

        } else {
            last = new Coordinate(first);
            array = new Coordinate[coordinates.size() + 1];
        }

        for (int i = 0; i < coordinates.size(); ++i) {
            array[i] = coordinates.get(i);
        }
        array[array.length - 1] = last;

        LinearRing linear = createLinearRing(array);
        return createPolygon(linear, null);
    }

    public static final MultiPolygon createRectangle(double x, double y, double width, double height) {

        if (width == 0 || height == 0) {
            return EMPTY;
        }
        return createMP(createRectanglePolygon(x, y, width, height));
    }

    public static final MultiPolygon createRectangle(Envelope envelope) {
        return createRectangle(envelope.getMinX(), envelope.getMinY(), envelope.getWidth(), envelope.getHeight());
    }

    public static final Polygon createRectanglePolygon(double x, double y, double width, double height) {
        return createRectanglePolygon(x, y, width, height, null);
    }

    public static final Polygon createRectanglePolygon(double x, double y, double width, double height, GeometryFactory geomFactory) {

        // note: in case of negative values move start point to cycle clockwise
        if (width < 0) {
            x += width;
            width = Math.abs(width);
        }
        if (height < 0) {
            y += height;
            height = Math.abs(height);
        }

        /**
         * Make sure at start these are precise
         */
        if (geomFactory == null) {
            geomFactory = sourceFactory;
        }

        PrecisionModel model = geomFactory.getPrecisionModel();
        x = model.makePrecise(x);
        y = model.makePrecise(y);
        width = model.makePrecise(width);
        height = model.makePrecise(height);

        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(x, y);
        coordinates[1] = new Coordinate(x, y + height);
        coordinates[2] = new Coordinate(x + width, y + height);
        coordinates[3] = new Coordinate(x + width, y);
        coordinates[4] = new Coordinate(x, y);

        /**
         * Create them direct without further checks, for SIMPLE rectangles this should be okay
         */
        LinearRing outer = geomFactory.createLinearRing(coordinates);
        return geomFactory.createPolygon(outer, null);
    }

    public static final Polygon createRectanglePolygon(Envelope envelope) {
        return createRectanglePolygon(envelope.getMinX(), envelope.getMinY(), envelope.getWidth(), envelope.getHeight());
    }

    /**
     * SerachPoint is an regular Point that keeps the envelope object intact, useful for faster iterations across grids
     */
    public static final SearchPoint createSearchPoint() {
        return new SearchPoint(sourceFactory.getCoordinateSequenceFactory().create(new Coordinate[] { new Coordinate() }), sourceFactory);
    }

    public static final Geometry createSquare(Point p, double size) {

        if (p == null) {
            return null;
        }
        return createRectanglePolygon(p.getX() - size / 2.0, p.getY() - size / 2.0, size, size, null);
    }

    public static final MultiPolygon difference(Geometry base, Geometry remove) {
        return difference(base, remove, false);
    }

    public static final MultiPolygon difference(Geometry base, Geometry remove, boolean erodeAndDilate) {

        if (!hasArea(remove)) {
            return createMP(base);
        }
        if (covers(remove, base)) {
            return EMPTY;
        }

        if (isGeometryCollection(base)) {
            // first geometry is collection
            GeometryCollection gc1 = (GeometryCollection) base;
            List<Geometry> remainders = new ArrayList<>();
            for (int n = 0; n < gc1.getNumGeometries(); n++) {
                remainders.add(difference(gc1.getGeometryN(n), remove));
            }
            return createMP(remainders);

        } else if (isGeometryCollection(remove)) {
            // second geometry is collection
            GeometryCollection gc2 = (GeometryCollection) remove;
            for (int n = 0; n < gc2.getNumGeometries(); n++) {
                base = difference(base, gc2.getGeometryN(n));
            }
            return createMP(base);
        }

        Geometry result = differenceOperation(base, remove);

        // maybe also do an erode dilate
        if (erodeAndDilate) {
            result = erodeAndDilate(result, ERODE_DILATE_MARGIN);
        }
        return createMP(result);
    }

    public static final Geometry differenceOperation(Geometry base, Geometry remove) {
        return executeOverlayOperation(base, remove, OverlayOp.DIFFERENCE);
    }

    public static final double distance(Geometry geometry1, Geometry geometry2) {

        double closest = Double.MAX_VALUE;

        if (isGeometryCollection(geometry1)) {
            // first geometry is collection
            for (int n = 0; n < geometry1.getNumGeometries(); n++) {
                double distance = distance(geometry1.getGeometryN(n), geometry2);
                if (distance < closest) {
                    closest = distance;
                }
            }
        } else if (isGeometryCollection(geometry2)) {
            // second geometry is collection
            for (int n = 0; n < geometry2.getNumGeometries(); n++) {
                double distance = distance(geometry1, geometry2.getGeometryN(n));
                if (distance < closest) {
                    closest = distance;
                }
            }
        } else if (!isEmpty(geometry1) && !isEmpty(geometry2)) {
            // BOTH geometries MUST contain something or be a point/line
            double distance = geometry1.distance(geometry2);
            if (distance < closest) {
                closest = distance;
            }
        }
        return closest;
    }

    public static final double distance3D(Coordinate a, Coordinate b) {

        // Do 3D when both points have valid coordinates, otherwise 2D
        if (!Double.isNaN(a.getZ()) && !Double.isNaN(b.getZ())) {
            return a.distance3D(b);
        } else {
            return a.distance(b);
        }
    }

    public static final double distance3D(LineSegment segment, Point p) {

        // check for 3D Coordinates, if not fallback to 2D
        if (Double.isNaN(segment.p0.z) || Double.isNaN(segment.p1.z) || Double.isNaN(p.getZ())) {
            return segment.distance(p.getCoordinate());
        }

        // calculate z value for closest point using projection factor
        Coordinate closest = segment.closestPoint(p.getCoordinate());
        closest.z = segment.p0.z + segment.segmentFraction(closest) * (segment.p1.z - segment.p0.z);
        return closest.distance3D(p.getCoordinate());
    }

    public static final double distance3D(Point a, Point b) {
        return distance3D(a.getCoordinate(), b.getCoordinate());
    }

    public static final boolean equals(MultiPolygon mp, MultiPolygon other) {

        if (mp.getNumGeometries() != other.getNumGeometries()) {
            return false;
        }

        for (int i = 0; i < mp.getNumGeometries(); ++i) {
            Geometry geom = mp.getGeometryN(i);
            Geometry otherGeom = other.getGeometryN(i);
            if (geom instanceof Polygon gPolygon && otherGeom instanceof Polygon oPolygon) {
                if (!equals(gPolygon, oPolygon)) {
                    return false;
                }
            } else if (!geom.equals(otherGeom)) {
                return false;
            }
        }
        return true;
    }

    public static final boolean equals(Polygon polygon, Polygon other) {

        if (!polygon.getExteriorRing().equals(other.getExteriorRing())) {
            return false;
        }

        if (polygon.getNumInteriorRing() != other.getNumInteriorRing()) {
            return false;
        }

        for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
            if (!polygon.getInteriorRingN(i).equals(other.getInteriorRingN(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes minor "frutsels"
     */
    public static final Geometry erodeAndDilate(Geometry geom, double valueM) {
        return bufferSimple(bufferSimple(geom, -valueM), valueM);
    }

    private static final Geometry executeOverlayOperation(Geometry mp1, Geometry mp2, int type) {

        try {
            /**
             * First try executing it the normal way on existing precision
             */
            return SnapIfNeededOverlayOp.overlayOp(mp1, mp2, type);

        } catch (TopologyException | AssertionFailedException e) {

            try {
                /**
                 * Second attempt forced increase precision model
                 */
                Geometry exactMp1 = bufferZero(overlayOperationFactory.createGeometry(mp1));
                Geometry exactMp2 = bufferZero(overlayOperationFactory.createGeometry(mp2));
                Geometry result = SnapIfNeededOverlayOp.overlayOp(exactMp1, exactMp2, type);
                return createLocalPrecision(result);

            } catch (TopologyException | AssertionFailedException e2) {
                /**
                 * Third attempt forced increase precision model and make the objects a bit smaller.
                 */
                Geometry exactMp1 = bufferSimple(overlayOperationFactory.createGeometry(mp1), -INTERSECTION_BORDER_MARGIN);
                Geometry exactMp2 = bufferSimple(overlayOperationFactory.createGeometry(mp2), -INTERSECTION_BORDER_MARGIN);
                Geometry result = SnapIfNeededOverlayOp.overlayOp(exactMp1, exactMp2, type);
                return createLocalPrecision(result);
            }
        }
    }

    /**
     * Get angle form largest part of this Geometry in rad.
     */
    public static final double getAngle(Geometry geometry) {

        double angle = 0.0;
        double maxDistance = 0.0;

        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry child = geometry.getGeometryN(i);
            LineString line = null;
            if (child instanceof LineString l) {
                line = l;
            } else if (child instanceof Polygon p) {
                line = p.getExteriorRing();
            }
            if (line != null) {
                // get longest part of line string and calculate angle
                for (int j = 0; j < line.getNumPoints() - 1; j++) {
                    Coordinate start = line.getCoordinateN(j);
                    Coordinate end = line.getCoordinateN(j + 1);
                    double distance = start.distance(end);
                    if (distance > maxDistance) {
                        angle = Angle.angle(start, end);
                        maxDistance = distance;
                    }
                }
            }
        }
        return angle;
    }

    public static final double getArea(Collection<? extends Geometry> geometries) {

        double total = 0.0;
        if (geometries != null) {
            for (Geometry mp : geometries) {
                total += mp.getArea();
            }
        }
        return total;
    }

    public static final Geometry getBoundaries(Geometry geometry) {

        if (!isGeometryCollection(geometry)) {
            return geometry.getBoundary();
        }
        GeometryCollection gc = (GeometryCollection) geometry;
        List<Geometry> geoms = new ArrayList<>();
        for (int n = 0; n < gc.getNumGeometries(); n++) {
            Geometry boundary = getBoundaries(gc.getGeometryN(n));
            if (boundary != null && !boundary.isEmpty()) {
                geoms.add(boundary);
            }
        }
        return createCollection(geoms);
    }

    /**
     * Minimal approximation of the byte count based on coordinates
     */
    public static final long getByteCount(Geometry g) {
        return g != null ? g.getNumPoints() * COORDINATE_BYTES : 0;
    }

    /**
     * Returns the Interior Point, often the centroid, but always inside the geometry.
     */
    public static final Point getCenterPoint(Geometry geom) {

        if (isEmpty(geom)) {
            return null;
        }
        Coordinate c = InteriorPoint.getInteriorPoint(geom);
        return c != null ? createPoint(c) : null;
    }

    /**
     * Returns the Interior Point, often the centroid, but always inside the geometry list.
     */
    public static final Point getCenterPoint(List<? extends Geometry> geoms) {

        if (geoms == null || geoms.size() == 0) {
            return null;
        }
        Geometry[] array = geoms.toArray(new Geometry[geoms.size()]);
        return getCenterPoint(new GeometryCollection(array, overlayOperationFactory));
    }

    public static final Envelope getEnvelope(Geometry geometry) {

        Envelope envelope = new Envelope();
        envelope.expandToInclude(geometry.getEnvelopeInternal());
        return envelope;
    }

    public static final Polygon getLargestPolygon(final Geometry g) {

        if (g instanceof Polygon p) {
            return p;
        }
        double topSize = 0.0;
        Polygon largest = null;

        List<Polygon> polygons = getPolygons(g);
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            double mySize = polygon.getArea();
            if (mySize > topSize) {
                topSize = mySize;
                largest = polygon;
            }
        }
        return largest;
    }

    public static final List<LineSegment> getLineSegments(LineString line) {

        List<LineSegment> result = new ArrayList<>();
        for (int i = 1; i < line.getNumPoints(); i++) {
            result.add(new LineSegment(line.getCoordinateN(i - 1), line.getCoordinateN(i)));
        }
        return result;
    }

    public static final List<? extends LineString> getLineStrings(Geometry g) {

        List<LineString> lines = new ArrayList<>();
        if (g instanceof LineString line) {
            lines.add(line);

        } else if (g instanceof MultiLineString || isGeometryCollection(g)) {
            for (int n = 0; n < g.getNumGeometries(); n++) {
                Geometry child = g.getGeometryN(n);
                if (child instanceof LineString line) {
                    lines.add(line);
                } else {
                    lines.addAll(getLineStrings(child));
                }
            }
        }
        return lines;
    }

    public static final List<LineString> getPolygonLines(Geometry geometry) {

        List<LineString> lines = new ArrayList<>();
        List<Polygon> polygons = getPolygons(geometry);
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            lines.add(polygon.getExteriorRing());
            for (int n = 0; n < polygon.getNumInteriorRing(); n++) {
                lines.add(polygon.getInteriorRingN(n));
            }
        }
        return lines;
    }

    public static final List<Polygon> getPolygons(Collection<? extends Geometry> geometries) {

        List<Polygon> polygons = new ArrayList<>();
        if (geometries == null) {
            return polygons;
        }
        List<Polygon> temp = new ArrayList<>();
        for (Geometry geometry : geometries) {
            polygons.addAll(getPolygons(geometry, temp));
        }
        return polygons;
    }

    public static final List<Polygon> getPolygons(Geometry geometry) {
        return getPolygons(geometry, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public static final List<Polygon> getPolygons(Geometry geometry, List<Polygon> store) {

        store.clear(); // remove possible old data

        if (geometry instanceof Polygon polygon) {
            if (geometry.getArea() > 0.0) {
                store.add(polygon);
            }
        } else {
            List<Polygon> polygons = PolygonExtracter.getPolygons(geometry);
            for (int i = 0; i < polygons.size(); i++) {
                Polygon polygon = polygons.get(i);
                if (polygon.getArea() > 0.0) {
                    store.add(polygon);
                }
            }
        }
        return store;
    }

    public static final GeometryCollection getSizedPolygonCollection(Geometry geometry, double maxArea) {

        List<Geometry> list = new ArrayList<>();
        List<Polygon> temp = new ArrayList<>();

        for (int n = 0; n < geometry.getNumGeometries(); n++) {
            Geometry childGeometry = geometry.getGeometryN(n);

            List<Polygon> polygons = getPolygons(childGeometry, temp);
            for (int m = 0; m < polygons.size(); m++) {
                Polygon polygon = polygons.get(m);
                Envelope envelope = polygon.getEnvelopeInternal();

                if (envelope.getArea() > maxArea) {
                    // note: dividing into quarts and then again is faster than direct division into many
                    for (Envelope qenv : subdivideEnvelope(envelope)) {
                        MultiPolygon envMP = intersection(childGeometry, createRectangle(qenv));
                        if (hasArea(envMP)) { // maybe empty intersection
                            GeometryCollection subGC = getSizedPolygonCollection(envMP, maxArea);
                            for (int i = 0; i < subGC.getNumGeometries(); i++) {
                                list.add(subGC.getGeometryN(i));
                            }
                        }
                    }
                } else {
                    list.add(polygon);
                }
            }
        }
        return createCollection(list);
    }

    public static final Skeleton getSkeleton(LinearRing line) {

        // setup skeleton
        LoopL<Edge> out = new LoopL<>();
        Loop<Edge> loop1 = new Loop<>();
        Machine directionMachine = new Machine();
        Corner[] corners = new Corner[line.getNumPoints()];

        // convert points to corners in reverse order
        int c = 0;
        for (int i = line.getNumPoints() - 1; i > 0; i--) {
            Point p1 = line.getPointN(i);
            corners[c] = new Corner(p1.getX(), p1.getY());
            c++;
        }
        // finish with first
        corners[c] = corners[0];

        // create edges from corners
        for (int i = 0; i < corners.length - 1; i++) {
            Corner c1 = corners[i];
            Corner c2 = corners[i + 1];
            Edge edge = new Edge(c1, c2);
            edge.machine = directionMachine;
            loop1.append(edge);
        }

        // do skeleton stuff
        out.add(loop1);
        Skeleton skeleton = new Skeleton(out, true);
        skeleton.skeleton();
        return skeleton;
    }

    public static final List<Polygon> getSkeletonPolygons(Geometry originalPolygon, Skeleton skeleton) {

        List<Polygon> result = new ArrayList<>();
        Geometry remainder = originalPolygon;

        for (Face face : skeleton.output.faces.values()) {
            LoopL<Point3d> loopl = face.getLoopL();

            for (Loop<Point3d> pl : loopl) {
                List<Coordinate> coordinates = new ArrayList<>(pl.count());
                for (Point3d p : pl) {
                    coordinates.add(new Coordinate(p.x, p.y));
                }
                Geometry geom = createPolygon(coordinates);

                // Note: Sometimes the algorithm above freaks out, reduce roof polygons orginal Poly
                result.addAll(getPolygons(intersectionOperation(geom, originalPolygon)));
                remainder = differenceOperation(remainder, geom);
            }
        }

        // Note: add large remaining polygons the skeleton algorithm migth have missed
        for (Polygon p : getPolygons(remainder)) {
            if (p.getArea() > 5.0) {
                result.add(p);
            }
        }
        return result;
    }

    public static final MultiLineString getSkeletonTopLines(Geometry geometry, double accuracy) {

        Geometry g = simplify(geometry, accuracy);
        List<LineString> result = new ArrayList<>();

        List<Polygon> polygons = getPolygons(g);
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            if (polygon.getNumInteriorRing() == 0) {
                result.addAll(getSkeletonTopLines(getSkeleton(polygon.getExteriorRing()), accuracy, true));
            }
        }
        return new MultiLineString(result.toArray(new LineString[result.size()]), sourceFactory);
    }

    @SuppressWarnings("unchecked")
    public static final Collection<LineString> getSkeletonTopLines(Skeleton skeleton, double accuracy, boolean merged) {

        List<Point3d> bottomPoints = new ArrayList<>();
        LineMerger merger = merged ? new LineMerger() : null;
        List<LineString> seperateLines = new ArrayList<>();

        for (Face face : skeleton.output.faces.values()) {

            // gather bottom points (on outer contour of polygon)
            for (SharedEdge edge : face.definingSE) {
                bottomPoints.add(edge.start);
                bottomPoints.add(edge.end);
            }

            for (Loop<SharedEdge> edgeLoop : face.edges) {
                for (SharedEdge edge : edgeLoop) {

                    // add only edges that to not intersect with outer polygon contour
                    if (!bottomPoints.contains(edge.start) && !bottomPoints.contains(edge.end)) {

                        Coordinate start = new Coordinate(edge.start.x, edge.start.y);
                        Coordinate end = new Coordinate(edge.end.x, edge.end.y);

                        LineString topLine;
                        if (start.x > end.x) {
                            topLine = createLine(start, end);
                        } else {
                            topLine = createLine(end, start);
                        }

                        // check if line is already there
                        boolean contains = false;
                        for (LineString line : seperateLines) {
                            if (topLine.equalsExact(line, accuracy)) {
                                contains = true;
                                break;
                            }
                        }

                        // add only new lines
                        if (!contains) {
                            if (merged) {
                                merger.add(topLine);
                            }
                            seperateLines.add(topLine);
                        }
                    }
                }
            }
            // reset per face, faster
            bottomPoints.clear();
        }
        return merged ? merger.getMergedLineStrings() : seperateLines;
    }

    public static final List<Tri> getTriangles(Collection<? extends Geometry> collection, double minTriangleArea) {

        List<Tri> triangles = new ArrayList<>();
        List<Polygon> temp = new ArrayList<>();

        for (Geometry geom : collection) {
            List<Polygon> polygons = getPolygons(geom, temp);
            for (int i = 0; i < polygons.size(); i++) {
                getTriangles(triangles, polygons.get(i), minTriangleArea);
            }
        }
        return triangles;
    }

    private static final void getTriangles(List<Tri> triangles, Polygon polygon, double minTriangleArea) {

        // invalid, early out
        if (isEmpty(polygon) || polygon.getArea() < minTriangleArea) {
            return;
        }

        // fastest: already a triangle?
        if (polygon.getNumInteriorRing() == 0 && polygon.getExteriorRing().getNumPoints() == 4) {
            triangles.add(Tri.create(polygon.getExteriorRing().getCoordinates()));
            return;
        }

        // First try fast Ear-clipping, try buffer, then do same with expensive Delaunay
        try {
            getTrianglesEarClipping(triangles, polygon, minTriangleArea);
        } catch (Exception e) {
            try {
                getTrianglesEarClipping(triangles, bufferSimple(polygon, 0), minTriangleArea);
            } catch (Exception e2) {
                try {
                    getTrianglesDelaunay(triangles, polygon, minTriangleArea);
                } catch (Exception e3) {
                    try {
                        getTrianglesDelaunay(triangles, bufferSimple(polygon, 0), minTriangleArea);
                    } catch (Exception e4) {
                        TLogger.exception(e4, "Triangulation failed on: " + polygon.toString());
                    }
                }
            }
        }
    }

    public static final List<Tri> getTriangles(Polygon polygon, double minTriangleArea) {

        List<Tri> triangles = new ArrayList<>();
        getTriangles(triangles, polygon, minTriangleArea);
        return triangles;
    }

    /**
     * JTS18: expensive but better quality ConformingDelaunay algorithm
     */
    private static final void getTrianglesDelaunay(List<Tri> triangles, Geometry g, double minTriangleArea) {

        PreparedGeometry optimizedPolygon = prepare(bufferSimple(g, -TOLERANCE));
        ConformingDelaunayTriangulationBuilder triangulator = new ConformingDelaunayTriangulationBuilder();
        triangulator.setTolerance(TOLERANCE);
        triangulator.setConstraints(g);
        triangulator.setSites(g);

        /**
         * Filter out all triangles outside the original polygon.
         */
        Geometry gc = triangulator.getTriangles(sourceFactory);
        for (int i = 0; i < gc.getNumGeometries(); i++) {
            Geometry triangle = gc.getGeometryN(i);
            if (intersectsBorderIncluded(optimizedPolygon, triangle)) {
                Coordinate[] coords = triangle.getCoordinates();
                if (triangle.getArea() >= minTriangleArea && coords.length == 4) {
                    triangles.add(new Tri(coords[0], coords[1], coords[2]));
                }
            }
        }
    }

    /**
     * JTS 19: fast ear-clipping, see https://github.com/locationtech/jts/pull/775
     */
    private static final void getTrianglesEarClipping(List<Tri> triangles, Geometry g, double minTriangleArea) {

        PolygonTriangulator triangulator = new PolygonTriangulator(g);
        List<Tri> tris = triangulator.getTriangles();
        for (int i = 0; i < tris.size(); i++) {
            Tri tri = tris.get(i);
            if (tri.getArea() >= minTriangleArea) {
                triangles.add(tri);
            }
        }
    }

    /**
     * Get interpolated Z value in triangle
     *
     * Based on: https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Conversion_between_barycentric_and_Cartesian_coordinates
     *
     */
    public static final double getTriangleZ(Geometry triangle, Coordinate c) {

        Coordinate[] coords = triangle.getCoordinates();
        if (coords.length != 4) {
            return Double.NaN; // only for triangles
        }
        double det = (coords[1].y - coords[2].y) * (coords[0].x - coords[2].x) + (coords[2].x - coords[1].x) * (coords[0].y - coords[2].y);
        if (det == 0) {
            return Double.NaN;
        }
        double l1 = ((coords[1].y - coords[2].y) * (c.x - coords[2].x) + (coords[2].x - coords[1].x) * (c.y - coords[2].y)) / det;
        double l2 = ((coords[2].y - coords[0].y) * (c.x - coords[2].x) + (coords[0].x - coords[2].x) * (c.y - coords[2].y)) / det;
        double l3 = 1 - l1 - l2;
        if (l1 < 0 || l1 > 1 || l2 < 0 || l2 > 1 || l3 < 0 || l3 > 1) {
            return Double.NaN;
        }

        return l1 * coords[0].z + l2 * coords[1].z + l3 * coords[2].z;
    }

    /**
     * When true geometry is not NULL and contains at least some area
     */
    public static final boolean hasArea(Geometry geometry) {

        // check for null or no points
        if (geometry == null) {
            return false;
        }

        // Otherwise check area (note: cannot do this on envelope)
        if (geometry.getNumGeometries() == 1) {
            return !geometry.isEmpty() && geometry.getArea() > 0;
        }

        // check collection per geometry for early outs
        for (int n = 0; n < geometry.getNumGeometries(); n++) {
            if (hasArea(geometry.getGeometryN(n))) {
                return true;
            }
        }
        return false;
    }

    public static final boolean hasZ(Point point) {
        return point != null && point.getCoordinate() != null && !Double.isNaN(point.getCoordinate().z);
    }

    public static final MultiPolygon intersection(Geometry geometry1, Geometry geometry2) {

        if (!hasArea(geometry1) || !hasArea(geometry2)) {
            return EMPTY;
        }
        if (covers(geometry1, geometry2)) {
            return createMP(geometry2);
        }
        if (covers(geometry2, geometry1)) {
            return createMP(geometry1);
        }

        if (isGeometryCollection(geometry1)) {
            // first geometry is collection
            GeometryCollection gc1 = (GeometryCollection) geometry1;
            List<Geometry> intersections = new ArrayList<>();
            for (int n = 0; n < gc1.getNumGeometries(); n++) {
                intersections.add(intersection(gc1.getGeometryN(n), geometry2));
            }
            // combine into single MultiPolygon
            return createMP(intersections);

        } else if (isGeometryCollection(geometry2)) {
            // second geometry is collection
            GeometryCollection gc2 = (GeometryCollection) geometry2;
            List<Geometry> intersections = new ArrayList<>();
            for (int n = 0; n < gc2.getNumGeometries(); n++) {
                intersections.add(intersection(geometry1, gc2.getGeometryN(n)));
            }
            // combine into single MultiPolygon
            return createMP(intersections);
        }

        return createMP(intersectionOperation(geometry1, geometry2));
    }

    public static final double intersectionFraction(Geometry geometry1, Geometry geometry2) {
        double area = geometry1 != null ? geometry1.getArea() : 0;
        return area == 0 ? 0 : intersection(geometry1, geometry2).getArea() / area;
    }

    public static final Geometry intersectionOperation(Geometry g1, Geometry g2) {
        return executeOverlayOperation(g1, g2, OverlayOp.INTERSECTION);
    }

    public static final boolean intersects(PreparedGeometry geometry1, Geometry geometry2, boolean borderIncluded) {

        if (borderIncluded) {
            return intersectsBorderIncluded(geometry1, geometry2);
        } else {
            return intersectsBorderExcluded(geometry1, geometry2);
        }
    }

    /**
     * Accurate intersection check, Border is NOT included (by a small margin)
     */
    public static final boolean intersectsBorderExcluded(Envelope env1, Envelope env2) {
        return env1.intersection(env2).getArea() > INTERSECTION_BORDER_MARGIN;
    }

    /**
     * Accurate intersection check, Border is NOT included (by a small margin)
     */
    public static final boolean intersectsBorderExcluded(Geometry geometry1, Geometry geometry2) {
        return intersectsBorderExcluded(prepare(geometry1), geometry2);
    }

    /**
     * Accurate intersection check, Border is NOT included (by a small margin)
     */
    public static final boolean intersectsBorderExcluded(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        try {
            // cheap method to test intersection first
            if (!preparedGeometry1.intersects(geometry2)) {
                return false;
            }
            // cheap method to test if it only touches edges
            if (touchesAll(preparedGeometry1, geometry2)) {
                return false;
            }

        } catch (TopologyException | AssertionFailedException e) {
            // TLogger.warning("Intersect fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
        }

        // point cannot to negative margin test, thus must be contained in geom 1
        if (geometry2 instanceof Point) {

            // both are points, skip overlap test
            if (preparedGeometry1.getGeometry() instanceof Point) {
                return preparedGeometry1.contains(geometry2);
            }
            // add margin to geom 1
            Geometry geometry1 = bufferSimple(preparedGeometry1.getGeometry(), -INTERSECTION_BORDER_MARGIN);
            return prepare(geometry1).intersects(geometry2);
        }

        // try expensive more robust method
        return preparedGeometry1.intersects(bufferSimple(geometry2, -INTERSECTION_BORDER_MARGIN));
    }

    /**
     * Faster intersection check, Including Border, when it fails fall back to Border Excluded
     */
    public static final boolean intersectsBorderIncluded(Geometry geometry1, Geometry geometry2) {
        return intersectsBorderIncluded(prepare(geometry1), geometry2);
    }

    /**
     * Faster intersection check, Including Border, when it fails fall back to Border Excluded
     */
    public static final boolean intersectsBorderIncluded(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        try {
            // cheap method to test intersection
            return preparedGeometry1.intersects(geometry2);
        } catch (TopologyException | AssertionFailedException e) {
            TLogger.warning("Rough intersect fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
            // fall back to exact check:
            return intersectsBorderExcluded(preparedGeometry1, geometry2);
        }
    }

    public static final boolean isEmpty(Geometry geometry) {
        return geometry == null || geometry.isEmpty();
    }

    public static final boolean isExactWidthAndHeight(final MultiPolygon mp, final double width, final double height, double errorMargin) {

        if (Math.abs(mp.getArea() - width * height) > errorMargin) {
            return false;
        }
        Envelope envelope = getEnvelope(mp);
        return Math.abs(envelope.getHeight() - height) < errorMargin && Math.abs(envelope.getWidth() - width) < errorMargin;
    }

    public static final boolean isGeometryCollection(Geometry geometry) {
        return geometry != null && geometry.isGeometryCollection();
    }

    /**
     * Prepare geometry that also supports collections
     */
    public static final PreparedGeometry prepare(Geometry geometry) {
        return prepare(geometry, true);
    }

    /**
     * Prepare geometry that also supports collections
     */
    public static final PreparedGeometry prepare(Geometry geometry, boolean cache) {

        PreparedGeometry prepared = geometry.getSoftPrepared();
        if (prepared == null) {
            if (isGeometryCollection(geometry)) { // check on class to get only the collection objects
                prepared = new PreparedCollection((GeometryCollection) geometry);
            } else {
                prepared = PreparedGeometryFactory.prepare(geometry);
            }
            if (cache) { // store soft reference for later
                geometry.setSoftPrepared(prepared);
            }
        }
        return prepared;
    }

    public static final MultiPolygon removeHoles(Geometry g, double minHoleArea) {

        List<Polygon> validPolygons = new ArrayList<>();
        for (Polygon p : getPolygons(g)) {
            // check holes for minimal size
            if (p.getNumInteriorRing() > 0) {
                List<LinearRing> rings = new ArrayList<>();
                for (int n = 0; n < p.getNumInteriorRing(); n++) {
                    LinearRing hole = p.getInteriorRingN(n);
                    if (hole.getArea() >= minHoleArea) {
                        rings.add(hole);
                    }
                }
                LinearRing exterior = p.getExteriorRing();
                LinearRing[] holes = rings.toArray(new LinearRing[rings.size()]);
                validPolygons.add(sourceFactory.createPolygon(exterior, holes));

            } else { // simple exterior only
                validPolygons.add(p);
            }
        }
        return createMP(validPolygons);
    }

    public static final MultiPolygon removePolygons(Geometry g, double minPolygonArea) {
        return createMP(getPolygons(g).stream().filter(p -> p.getArea() >= minPolygonArea).collect(Collectors.toList()));
    }

    public static final Object removeUserData(Geometry geometry) {

        final Object data = geometry != null ? geometry.getUserData() : null;
        clearUserData(geometry); // remove also for children to be sure
        return data;
    }

    public static final boolean setZ(Point point, double zValue) {

        if (point == null || point.getCoordinate() == null) {
            return false;
        }
        // set Z coordinate direct at correct precision
        point.getCoordinate().z = sourceFactory.getPrecisionModel().makePrecise(zValue);
        return true;
    }

    public static final List<Polygon> simplifiedPolygons(Geometry input, double tolerance) {

        List<Polygon> result = new ArrayList<>();
        for (Polygon polygon : getPolygons(input)) {
            Geometry g = DouglasPeuckerSimplifier.simplify(polygon, tolerance);
            if (g instanceof Polygon p) {
                result.add(p);
            } else if (g instanceof MultiPolygon mp) {
                result.addAll(getPolygons(mp));
            } else {
                TLogger.severe("Unknown geometry result in simplify: " + g.toString());
            }
        }
        return result;
    }

    public static final MultiPolygon simplify(Geometry g, double tolerance) {
        return createMP(simplifiedPolygons(g, tolerance));
    }

    /**
     * Sort to largest geometries first
     */
    public static final void sort(List<? extends Geometry> geometries) {
        geometries.sort((g1, g2) -> Double.compare(g2 != null ? g2.getArea() : 0.0, g1 != null ? g1.getArea() : 0.0));
    }

    public static final Envelope[] subdivideEnvelope(Envelope env) {
        return subdivideEnvelope(env, 2);
    }

    public static final Envelope[] subdivideEnvelope(Envelope env, int factor) {

        factor = Math.max(factor, 2);

        // Divide into 4 quarters
        Envelope[] array = new Envelope[factor * factor];
        double width = env.getWidth() / factor;
        double height = env.getHeight() / factor;

        for (int x = 0; x < factor; x++) {
            for (int y = 0; y < factor; y++) {
                Envelope newEnv = new Envelope(//
                        env.getMinX() + x * width, //
                        env.getMinX() + (x + 1) * width, //
                        env.getMinY() + y * height, //
                        env.getMinY() + (y + 1) * height //
                );
                array[x + y * factor] = newEnv;
            }
        }
        return array;
    }

    private static final Geometry toLocalPrecision(Geometry input) {

        GeometryPrecisionReducer reducer = new GeometryPrecisionReducer(sourceFactory.getPrecisionModel());
        reducer.setChangePrecisionModel(true);

        Geometry result;
        try {
            result = reducer.reduce(input);
            // validate that is still exists
            if (!isEmpty(input) && isEmpty(result)) {
                result = reducer.reduce(validate(input));
            }
        } catch (Exception e) {
            // when it fails, validate and try once more...
            result = reducer.reduce(validate(input));
        }

        // make sure the 2D topology valid again (reduce can break polygons)
        if (input instanceof Polygonal || isGeometryCollection(input)) {
            result = bufferZero(result);
        }

        // maybe convert back to the original Geometry type?
        if (input instanceof MultiPolygon && result instanceof Polygon polygon) {
            return new MultiPolygon(new Polygon[] { polygon }, sourceFactory);

        } else if (input instanceof MultiLineString && result instanceof LineString line) {
            return new MultiLineString(new LineString[] { line }, sourceFactory);

        } else if (input instanceof MultiPoint && result instanceof Point point) {
            return new MultiPoint(new Point[] { point }, sourceFactory);

        } else {
            return result;
        }
    }

    private static final boolean touchesAll(PreparedGeometry preparedGeometry1, Geometry geom2) {

        if (!isGeometryCollection(geom2)) {
            return preparedGeometry1.touches(geom2);
        }

        GeometryCollection gc2 = (GeometryCollection) geom2;
        boolean result = gc2.getNumGeometries() > 0;
        for (int i = 0; i < gc2.getNumGeometries() && result; i++) {
            result &= touchesAll(preparedGeometry1, gc2.getGeometryN(i));
        }
        return result;
    }

    public static final MultiPolygon union(GeometryCollection... gcs) {
        return createMP(Stream.of(gcs).filter(gc -> !isEmpty(gc)).toList());
    }

    public static final Geometry validate(Geometry geometry) {

        if (geometry == null) {
            return null;
        }
        Exception exp = null;
        int attempts = 10;

        // first try some buffer action preserving the topology
        for (int i = 0; i < attempts; i++) {
            try {
                if (geometry.isValid()) {
                    return geometry;
                } else {
                    geometry = bufferZero(geometry);
                }
            } catch (Exception e) {
                // ignore
                exp = e;
            }
        }

        // still invalid, now force a fix that may change the topology
        for (int i = 0; i < attempts; i++) {
            try {
                if (geometry.isValid()) {
                    return geometry;
                } else {
                    geometry = DouglasPeuckerSimplifier.simplify(geometry, i * TOLERANCE);
                }
            } catch (Exception e) {
                // ignore
                exp = e;
            }
        }

        TLogger.severe("Invalid Geometry: " + geometry.toString() + " optional exception: " + exp);
        return geometry;
    }

    public static final boolean within(Geometry geometry1, Geometry geometry2) {

        // Note: if first geometry is collection this is handled by PreparedCollection
        PreparedGeometry preparedGeometry1 = prepare(geometry1);
        return within(preparedGeometry1, geometry2);
    }

    public static final boolean within(PreparedGeometry preparedGeometry1, Geometry geometry2) {

        // second geometry is collection
        if (isGeometryCollection(geometry2)) {
            GeometryCollection gc2 = (GeometryCollection) geometry2;
            for (int n = 0; n < gc2.getNumGeometries(); n++) {
                if (!within(preparedGeometry1, gc2.getGeometryN(n))) {
                    return false;
                }
            }
            return true;
        }

        try {
            // cheap method to test within first
            return preparedGeometry1.within(geometry2);

        } catch (TopologyException | AssertionFailedException e) {
            // TLogger.warning("Within fail on: " + preparedGeometry1.getGeometry().toString() + " and " + geometry2.toString());
        }

        /**
         * Fall back scenario adding border margin
         */

        // point cannot to negative margin test, thus must be within in geom 1
        if (geometry2 instanceof Point) {
            // both are points, skip overlap test (unlikely)
            if (preparedGeometry1.getGeometry() instanceof Point) {
                return preparedGeometry1.within(geometry2);
            }
            // add margin to geom 1
            Geometry geometry1 = bufferSimple(preparedGeometry1.getGeometry(), -INTERSECTION_BORDER_MARGIN);
            preparedGeometry1 = prepare(geometry1);
            return preparedGeometry1.within(geometry2);
        }

        // try expensive more robust method
        return preparedGeometry1.covers(bufferSimple(geometry2, -INTERSECTION_BORDER_MARGIN));
    }
}
