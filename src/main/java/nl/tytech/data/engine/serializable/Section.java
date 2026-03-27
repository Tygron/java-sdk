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
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.Function;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import straightskeleton.Skeleton;

/**
 * Section: is part of a building
 *
 * @author Maxim Knepfle
 */
public class Section implements Serializable {

    private record NeighborPredicate(Building me) implements Predicate<Building> {

        @Override
        public boolean test(Building other) {
            return !me.getID().equals(other.getID())

                    && me.isResidential() && other.isResidential()

                    && me.getConstructionYear() != null && me.getConstructionYear().equals(other.getConstructionYear())

                    && Math.abs(me.getHeightM() - other.getHeightM()) < 0.5

                    && Math.abs(
                            me.getSlantingRoofHeightM(me.getFirstSection()) - other.getSlantingRoofHeightM(other.getFirstSection())) < 0.5;
        }
    }

    private static final long serialVersionUID = -3060395347540498878L;

    /**
     * Simply polygons with this factor, when sketonizing, to make it easier/faster
     */
    public static final double SKELETON_SIMPLIFY_FACTOR = 100d;

    private static final int MAX_SKELETON_POINTS = 1000;

    private static final double MIN_ROOF_SKELETON_AREA = 1.0;

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    @XMLValue
    private MultiLineString skeletonLines = null;

    @XMLValue
    private MultiLineString outerLines = null;

    @XMLValue
    private GeometryCollection roofPolygons = null;

    @XMLValue
    private float[] roofShape = null;

    @XMLValue
    private int floors = 1;

    @XMLValue
    private Double slantingRoofHeightM = null;

    @XMLValue
    private Integer id = Item.NONE;

    // only used in GIS reading, temp var
    private transient Double roofHeightM = null;

    private transient Point skeletonStart = null, skeletonEnd = null;

    public Section() {
    }

    public Section(int floors, Double slantingRoofHeightM, MultiPolygon polygons, MultiLineString lines, Double roofHeightM) {
        this(floors, polygons);
        this.slantingRoofHeightM = slantingRoofHeightM;
        this.skeletonLines = lines;
        this.roofHeightM = roofHeightM;
    }

    public Section(int floors, MultiPolygon polygons) {
        this.floors = floors;
        this.polygons = polygons;
    }

    public int getFloors() {
        return floors;
    }

    /**
     * only used in GIS reading, temp var
     */
    public Double getGISFloorHeightM() {

        if (roofHeightM == null || roofHeightM.doubleValue() <= 0) {
            return null;
        }
        double floorHeightM = MathUtils.round(roofHeightM / floors, Building.HEIGHT_ACCURACY);
        return MathUtils.clamp(floorHeightM, Function.MIN_FLOOR_HEIGHT_M, Double.MAX_VALUE);
    }

    public Integer getID() {
        return id;
    }

    public final double getLotSizeM2() {
        return polygons.getArea();
    }

    public String getName() {
        return "Section " + getID();
    }

    public MultiLineString getOuterLines() {
        return outerLines != null ? outerLines : JTSUtils.createMultiLineString(polygons);
    }

    public MultiPolygon getPolygons() {
        return polygons;
    }

    public Collection<Polygon> getRoofPolygons(Geometry g) {

        PreparedGeometry prep = g != null ? JTSUtils.prepare(g) : null;
        Collection<Polygon> result = new ArrayList<>();

        if (roofPolygons != null) {
            for (Polygon roofPolygon : JTSUtils.getPolygons(roofPolygons)) {
                if (prep == null || JTSUtils.intersectsBorderIncluded(prep, roofPolygon)) {
                    result.add(roofPolygon);
                }
            }
        }
        return result;
    }

    public float[] getRoofShape() {
        return roofShape;
    }

    public Double getSkeletonAngle() {

        if (skeletonLines != null && !skeletonLines.isEmpty()) {
            return JTSUtils.getAngle(skeletonLines);
        }
        return null;
    }

    public Point getSkeletonEnd() {

        if (skeletonEnd != null) {
            return skeletonEnd;
        }
        if (skeletonLines == null || skeletonLines.getCoordinates().length == 0) {
            return null;
        }
        skeletonEnd = JTSUtils.createPoint(skeletonLines.getCoordinates()[skeletonLines.getCoordinates().length - 1]);
        return skeletonEnd;
    }

    public MultiLineString getSkeletonLines(Geometry g) {

        PreparedGeometry prep = g != null ? JTSUtils.prepare(g) : null;
        Collection<Geometry> result = new ArrayList<>();

        if (hasSkeletonLines()) {
            for (int n = 0; n < skeletonLines.getNumGeometries(); n++) {
                LineString line = (LineString) skeletonLines.getGeometryN(n);
                if (prep == null || JTSUtils.intersectsBorderIncluded(prep, line)) {
                    Geometry pLine = g != null ? JTSUtils.intersectionOperation(g, line) : line;
                    for (int i = 0; i < pLine.getNumGeometries(); i++) {
                        result.add(pLine.getGeometryN(i));
                    }
                }
            }
        }
        return JTSUtils.createMultiLineString(result);
    }

    public Point getSkeletonStart() {

        if (skeletonStart != null) {
            return skeletonStart;
        }
        if (skeletonLines == null || skeletonLines.getCoordinates().length == 0) {
            return null;
        }
        skeletonStart = JTSUtils.createPoint(skeletonLines.getCoordinates()[0]);
        return skeletonStart;
    }

    public Double getSlantingRoofHeightM() {
        return slantingRoofHeightM;
    }

    public boolean hasSkeletonLines() {
        return skeletonLines != null;
    }

    public void reset() {
        JTSUtils.clearUserData(polygons);
    }

    public void resetSlantingRoofHeight(Building building, double defaultSlantingRoofHeight) {
        this.slantingRoofHeightM = null;
        if (defaultSlantingRoofHeight != 0 && skeletonLines == null) {
            updateRoofSkeleton(building);
        }
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    /**
     * Warning: Internal use only, call building.setMultiPolygons()
     */
    public void setPolygons(MultiPolygon mp) {
        this.polygons = mp;
    }

    public void setRoofGeometry(float[] roofShape) {
        this.roofShape = roofShape;
    }

    public void setSkeletonLines(List<Geometry> list) {

        List<Geometry> lines = new ArrayList<>();
        for (Geometry g : list) {
            lines.addAll(JTSUtils.getLineStrings(JTSUtils.intersectionOperation(polygons, g)));
        }
        skeletonLines = JTSUtils.createMultiLineString(lines);
    }

    public void setSlantingRoofHeightM(Building building, double slantingRoofHeightM) {
        this.slantingRoofHeightM = slantingRoofHeightM;
        if (slantingRoofHeightM != 0 && skeletonLines == null) {
            updateRoofSkeleton(building);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public void updateRoofSkeleton(Building building) {

        this.skeletonLines = null;
        this.outerLines = null;
        this.roofPolygons = null;
        this.skeletonStart = null;
        this.skeletonEnd = null;

        if (polygons == null || polygons.getArea() < MIN_ROOF_SKELETON_AREA || this.polygons.getNumPoints() > MAX_SKELETON_POINTS) {
            return;
        }

        MultiPolygon roofMP = polygons;
        if (building.getSections().size() == 1) {

            List<GeometryCollection> neighbors = building.getLord()
                    .getItems(MapLink.BUILDINGS, JTSUtils.bufferSimple(roofMP, 0.1), new NeighborPredicate(building))
                    .map(b -> b.getPolygons(null)).collect(Collectors.toList());

            if (neighbors.size() > 0) {
                MultiPolygon neighborRoofMP = JTSUtils.createMP(neighbors);
                List<Geometry> outerLines = new ArrayList<>();
                Geometry extendedNeighborRoofMP = JTSUtils.bufferSimple(neighborRoofMP, 0.1);

                for (Polygon polygon : JTSUtils.getPolygons(polygons)) {
                    LineString border = polygon.getExteriorRing();
                    Geometry outerGeometry = JTSUtils.differenceOperation(border, extendedNeighborRoofMP);
                    for (int i = 0; i < outerGeometry.getNumGeometries(); i++) {
                        outerLines.add(outerGeometry.getGeometryN(i));
                    }
                }
                this.outerLines = JTSUtils.createMultiLineString(outerLines);
                roofMP = JTSUtils.createMP(roofMP, neighborRoofMP); // add neighbors to my roof
            }
        }

        List<Polygon> allPoly = new ArrayList<>();
        List<Geometry> allLines = new ArrayList<>();

        double roundoff = 0.1;

        for (MultiPolygon mp : new MultiPolygon[] { roofMP, polygons }) { // fallback to own polygon when roofMP fails
            if (allLines.isEmpty()) {
                allPoly.clear();
                for (Polygon polygon : JTSUtils.getPolygons(JTSUtils.erodeAndDilate(mp, roundoff))) {
                    if (polygon.getNumInteriorRing() == 0) {
                        Skeleton skeleton = JTSUtils.getSkeleton(polygon.getExteriorRing());
                        List<Polygon> skelPolygons = JTSUtils.getSkeletonPolygons(polygons, skeleton);
                        for (Polygon rp : skelPolygons) {
                            allPoly.add(rp);
                        }
                        Collection<LineString> ls = JTSUtils.getSkeletonTopLines(skeleton, roundoff, false);
                        for (LineString rl : ls) {
                            Geometry g = JTSUtils.intersectionOperation(polygons, rl);
                            if (!g.isEmpty()) {
                                allLines.add(g);
                            }
                        }
                        if (ls.isEmpty() && !skelPolygons.isEmpty()) {
                            // check shared centerpoint
                            Point shared = null;
                            Polygon p1 = skelPolygons.get(0);
                            LinearRing r = p1.getExteriorRing();
                            for (int n = 0; n < r.getNumPoints(); n++) {
                                shared = r.getPointN(n);
                                skelloop: for (int i = 1; i < skelPolygons.size() && shared != null; i++) {
                                    LinearRing r2 = skelPolygons.get(i).getExteriorRing();
                                    for (int k = 0; k < r2.getNumPoints(); k++) {
                                        if (r2.getPointN(k).equals(shared)) {
                                            continue skelloop;
                                        }
                                    }
                                    shared = null;
                                }
                                if (shared != null) {
                                    allLines.add(JTSUtils.createLine(new Coordinate(shared.getCoordinate()),
                                            new Coordinate(shared.getX() + 0.001, shared.getY())));
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }

        this.skeletonLines = JTSUtils.createMultiLineString(allLines);
        this.roofPolygons = JTSUtils.createCollection(allPoly);
    }

    public void updateTopLineSkeleton(boolean simplify) {

        this.skeletonLines = null;
        this.roofPolygons = null;
        this.skeletonStart = null;
        this.skeletonEnd = null;
        if (!JTSUtils.hasArea(polygons) || this.polygons.getNumPoints() > MAX_SKELETON_POINTS) {
            return;
        }

        // margin of 0.1-2.0 meters is enough for roads
        double accuracyM = simplify ? MathUtils.clamp(polygons.getNumPoints() / SKELETON_SIMPLIFY_FACTOR, 0.1, 2.0) : 0.1;
        this.skeletonLines = JTSUtils.getSkeletonTopLines(polygons, accuracyM);
    }
}
