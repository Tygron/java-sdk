package com.bedatadriven.jackson.datatype.jts;

import static com.bedatadriven.jackson.datatype.jts.GeoJson.COORDINATES;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.GEOMETRIES;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.GEOMETRY_COLLECTION;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.LINE_STRING;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.MULTI_LINE_STRING;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.MULTI_POINT;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.MULTI_POLYGON;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.POINT;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.POLYGON;
import static com.bedatadriven.jackson.datatype.jts.GeoJson.TYPE;
import java.io.IOException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tytech.util.JTSUtils;

public class GeometryDeserializer<T extends Geometry> extends JsonDeserializer<T> {

    /**
     * Use simple empty own mapper to prevent tying issues from default mapper.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final GeometryFactory gf = JTSUtils.sourceFactory;

    private static final Coordinate[] EMPTY_COORDINATES = new Coordinate[0];

    private static final LineString EMPTY_LINE = gf.createLineString(EMPTY_COORDINATES);

    private static final LineString[] EMPTY_LINES = new LineString[0];

    private static final MultiLineString EMPTY_MULTILINE = gf.createMultiLineString(EMPTY_LINES);

    private static final LinearRing EMPTY_RING = gf.createLinearRing(EMPTY_COORDINATES);

    private static final LinearRing[] EMPTY_RINGS = new LinearRing[0];

    private static final Polygon EMPTY_POLYGON = gf.createPolygon(EMPTY_COORDINATES);

    private static final Polygon[] EMPTY_POLYGONS = new Polygon[0];

    private static final Geometry[] EMPTY_GEOMETRIES = new Geometry[0];

    private static final GeometryCollection EMPTY_COLLECTION = gf.createGeometryCollection(EMPTY_GEOMETRIES);

    private static final MultiPolygon EMPTY_MULTIPOLYGON = gf.createMultiPolygon(EMPTY_POLYGONS);

    private static final CoordinateArraySequenceFactory SEQUENCE_FACTORY = CoordinateArraySequenceFactory.instance();

    private static final double MIN_COORDINATE_ORDINAL_DIST = 1E-6;

    private final Class<T> type;

    public GeometryDeserializer(Class<T> type) {
        this.type = type;
    }

    private final CoordinateSequence addCoordinate(CoordinateSequence seq, Coordinate coordinate) {

        CoordinateSequence seq2 = SEQUENCE_FACTORY.create(seq.size() + 1, seq.getDimension());
        for (int i = 0; i < seq.size(); i++) {
            for (int j = 0; j < seq.getDimension(); j++) {
                seq2.setOrdinate(i, j, seq.getCoordinate(i).getOrdinate(j));
            }
        }
        for (int j = 0; j < seq.getDimension(); j++) {
            seq2.setOrdinate(seq.size(), j, coordinate.getOrdinate(j));
        }
        return seq2;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return (T) parseGeometry(MAPPER.readTree(jp));
    }

    private final JsonNode getCoordinates(JsonNode root, String type) throws JsonMappingException {

        JsonNode coordNode = root.get(COORDINATES);
        if (coordNode == null) {
            throw new JsonMappingException("Missing [" + COORDINATES + "] node in " + type);
        }
        return coordNode;
    }

    @Override
    public Class<T> handledType() {
        return type;
    }

    private final Coordinate parseCoordinate(JsonNode array) {

        if (array.size() == 2) {
            return new Coordinate(array.get(0).asDouble(), array.get(1).asDouble());
        } else {
            return new Coordinate(array.get(0).asDouble(), array.get(1).asDouble(), array.get(2).asDouble());
        }
    }

    private final CoordinateSequence parseCoordinateSequence(JsonNode array) {

        if (array.size() == 0) {
            return null;
        }

        // determine MAX dimension
        int dim = 0;
        for (int i = 0; i < array.size(); i++) {
            dim = Math.max(dim, array.get(i).size());
        }

        // load the coordinates
        CoordinateSequence seq = SEQUENCE_FACTORY.create(array.size(), dim);
        for (int i = 0; i < array.size(); i++) {
            JsonNode cNode = array.get(i);
            for (int j = 0; j < dim; j++) {
                seq.setOrdinate(i, j, j < cNode.size() ? cNode.get(j).asDouble() : Coordinate.NULL_ORDINATE);
            }
        }
        return seq;
    }

    private final Geometry[] parseGeometries(JsonNode arrayOfGeoms) throws JsonMappingException {

        if (arrayOfGeoms.size() == 0) {
            return EMPTY_GEOMETRIES;
        }
        Geometry[] items = new Geometry[arrayOfGeoms.size()];
        for (int i = 0; i != arrayOfGeoms.size(); ++i) {
            items[i] = parseGeometry(arrayOfGeoms.get(i));
        }
        return items;
    }

    @SuppressWarnings("deprecation")
    private final Geometry parseGeometry(JsonNode root) throws JsonMappingException {

        JsonNode type = root.get(TYPE);
        if (type == null) {
            if (root.get(COORDINATES) == null) {
                return EMPTY_POLYGON;
            } else {
                throw new JsonMappingException("Missing [" + TYPE + "] node in Geometry");
            }
        }

        return switch (type.asText()) {
            case POINT -> parsePoint(root);
            case MULTI_POINT -> parseMultiPoint(root);
            case LINE_STRING -> parseLineString(root);
            case MULTI_LINE_STRING -> parseMultiLineStrings(root);
            case POLYGON -> parsePolygon(root);
            case MULTI_POLYGON -> parseMultiPolygon(root);
            case GEOMETRY_COLLECTION -> parseGeometryCollection(root);
            default -> throw new JsonMappingException("Invalid Geometry type: " + type.asText());
        };
    }

    private final GeometryCollection parseGeometryCollection(JsonNode root) throws JsonMappingException {

        JsonNode geometriesNode = root.get(GEOMETRIES);
        if (geometriesNode == null) {
            throw new JsonMappingException("Missing [" + GEOMETRIES + "] node in GeometryCollection");
        }
        Geometry[] array = parseGeometries(geometriesNode);
        return array.length == 0 ? EMPTY_COLLECTION : gf.createGeometryCollection(array);
    }

    private final LinearRing[] parseInteriorRings(JsonNode arrayOfRings) {

        if (arrayOfRings.size() == 1) {
            return EMPTY_RINGS;
        }
        LinearRing[] rings = new LinearRing[arrayOfRings.size() - 1];
        for (int i = 1; i < arrayOfRings.size(); ++i) {
            rings[i - 1] = parseLinearRing(arrayOfRings.get(i));
        }
        return rings;
    }

    private final LinearRing parseLinearRing(JsonNode coordinates) {

        CoordinateSequence seq = parseCoordinateSequence(coordinates);
        if (seq == null) {
            return EMPTY_RING;
        }

        // fix round off issues when last coordinate does not match first
        if (seq.size() > 1 && !seq.getCoordinate(0).equals(seq.getCoordinate(seq.size() - 1))) {
            Coordinate first = seq.getCoordinate(0);
            Coordinate last = seq.getCoordinate(seq.size() - 1);
            if (Math.abs(first.x - last.x) < MIN_COORDINATE_ORDINAL_DIST && Math.abs(first.y - last.y) < MIN_COORDINATE_ORDINAL_DIST) {
                int i = seq.size() - 1;
                for (int j = 0; j < seq.getDimension(); j++) {
                    seq.setOrdinate(i, j, first.getOrdinate(j));
                }
            } else {
                seq = addCoordinate(seq, first);
            }
        }

        return gf.createLinearRing(seq);
    }

    private final LineString parseLineString(JsonNode root) throws JsonMappingException {

        CoordinateSequence seq = parseCoordinateSequence(getCoordinates(root, GeoJson.LINE_STRING));
        return seq == null ? EMPTY_LINE : gf.createLineString(seq);
    }

    private final LineString[] parseLineStrings(JsonNode array) {

        if (array.size() == 0) {
            return EMPTY_LINES;
        }
        LineString[] strings = new LineString[array.size()];
        for (int i = 0; i != array.size(); ++i) {
            CoordinateSequence sec = parseCoordinateSequence(array.get(i));
            strings[i] = sec == null ? EMPTY_LINE : gf.createLineString(sec);
        }
        return strings;
    }

    private final MultiLineString parseMultiLineStrings(JsonNode root) throws JsonMappingException {

        LineString[] array = parseLineStrings(getCoordinates(root, GeoJson.MULTI_LINE_STRING));
        return array.length == 0 ? EMPTY_MULTILINE : gf.createMultiLineString(array);
    }

    private final MultiPoint parseMultiPoint(JsonNode root) throws JsonMappingException {
        return gf.createMultiPoint(parseCoordinateSequence(getCoordinates(root, GeoJson.MULTI_POINT)));
    }

    private final MultiPolygon parseMultiPolygon(JsonNode root) throws JsonMappingException {

        Polygon[] array = parsePolygons(getCoordinates(root, GeoJson.MULTI_POLYGON));
        return array.length == 0 ? EMPTY_MULTIPOLYGON : gf.createMultiPolygon(array);
    }

    private final Point parsePoint(JsonNode root) throws JsonMappingException {
        return gf.createPoint(parseCoordinate(getCoordinates(root, GeoJson.POINT)));
    }

    private final Polygon parsePolygon(JsonNode root) throws JsonMappingException {
        return parsePolygonCoordinates(getCoordinates(root, GeoJson.POLYGON));
    }

    private final Polygon parsePolygonCoordinates(JsonNode arrayOfRings) {

        if (arrayOfRings.size() == 0) {
            return EMPTY_POLYGON;
        }
        LinearRing shell = parseLinearRing(arrayOfRings.get(0));
        return shell.isEmpty() ? EMPTY_POLYGON : gf.createPolygon(shell, parseInteriorRings(arrayOfRings));
    }

    private final Polygon[] parsePolygons(JsonNode arrayOfPolygons) {

        if (arrayOfPolygons.size() == 0) {
            return EMPTY_POLYGONS;
        }
        Polygon[] polygons = new Polygon[arrayOfPolygons.size()];
        for (int i = 0; i != arrayOfPolygons.size(); ++i) {
            polygons[i] = parsePolygonCoordinates(arrayOfPolygons.get(i));
        }
        return polygons;
    }
}
