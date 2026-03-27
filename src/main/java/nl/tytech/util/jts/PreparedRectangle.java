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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.locationtech.jts.operation.predicate.RectangleContains;
import org.locationtech.jts.operation.predicate.RectangleIntersects;

/**
 * Short circuit test for fast rectangle testing, needed for e.g. rasterization of grid cells.
 *
 * @author Maxim Knepfle
 *
 */
public final class PreparedRectangle extends PreparedPolygon {

    private final Polygon rectangle;

    public PreparedRectangle(Polygon polygon) {

        super(polygon);
        if (!polygon.isRectangle()) {
            throw new IllegalArgumentException("Input polygon (" + polygon + ") is not a rectangle!");
        }
        this.rectangle = polygon;
    }

    @Override
    public final boolean contains(Geometry g) {
        // short-circuit test with covers optimization for rectangles
        return envelopeCovers(g) && new RectangleContains(rectangle).contains(g);
    }

    @Override
    public final boolean covers(Geometry g) {
        // short-circuit test, rectangle always covers
        return envelopeCovers(g);
    }

    @Override
    public final boolean intersects(Geometry g) {
        // short-circuit test with intersect optimization for rectangles
        return envelopesIntersect(g) && new RectangleIntersects(rectangle).intersects(g);
    }
}
