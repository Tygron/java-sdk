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
package nl.tytech.util.jts;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Not modifiable (?) Empty MP
 *
 * @author Maxim Knepfle
 */
public class EmptyMultiPolygon extends MultiPolygon {

    private static final long serialVersionUID = 3451947641123390894L;

    private static final Coordinate[] COORDINATES = new Coordinate[0];

    private static final double ZERO = 0d;

    private final MultiLineString BOUNDARY;

    public EmptyMultiPolygon(GeometryFactory factory) {
        super(new Polygon[0], factory);
        BOUNDARY = new MultiLineString(null, factory);
    }

    @Override
    public final double getArea() {
        return ZERO;
    }

    @Override
    public final Geometry getBoundary() {
        return BOUNDARY;
    }

    @Override
    public final Coordinate getCoordinate() {
        return null;
    }

    @Override
    public final Coordinate[] getCoordinates() {
        return COORDINATES;
    }

    @Override
    public final double getLength() {
        return ZERO;
    }

    @Override
    public final boolean isEmpty() {
        return true;
    }
}
