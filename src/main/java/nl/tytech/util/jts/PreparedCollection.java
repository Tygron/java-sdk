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
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import nl.tytech.util.JTSUtils;

/**
 *
 * Prepared Geometry for Collections...
 *
 * @author Maxim Knepfle
 *
 */
public class PreparedCollection implements PreparedGeometry {

    private final PreparedGeometry[] preps;

    private final GeometryCollection gc;

    public PreparedCollection(GeometryCollection gc) {

        this.gc = gc;
        this.preps = new PreparedGeometry[gc.getNumGeometries()];
        for (int i = 0; i < gc.getNumGeometries(); i++) {
            this.preps[i] = JTSUtils.prepare(gc.getGeometryN(i));
        }
    }

    @Override
    public boolean contains(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (prep.contains(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsProperly(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (prep.containsProperly(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean coveredBy(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (!prep.coveredBy(other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean covers(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (prep.covers(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean crosses(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (prep.crosses(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean disjoint(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (!prep.disjoint(other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Geometry getGeometry() {
        return gc;
    }

    @Override
    public boolean intersects(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (prep.intersects(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean overlaps(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (!prep.overlaps(other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean touches(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (prep.touches(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean within(Geometry other) {
        for (PreparedGeometry prep : preps) {
            if (!prep.within(other)) {
                return false;
            }
        }
        return true;
    }
}
