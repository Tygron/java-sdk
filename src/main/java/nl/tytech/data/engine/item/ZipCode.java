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
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.data.core.item.SourcedItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.color.ColorUtils;
import nl.tytech.util.color.TColor;

/**
 * @author Maxim Knepfle
 */
public class ZipCode extends SourcedItem implements GeometryItem<MultiPolygon> {

    private static final long serialVersionUID = 1362611699406604464L;

    @XMLValue
    private TColor color = ColorUtils.createRandomRGB();

    @XMLValue
    private MultiPolygon polygons = JTSUtils.EMPTY;

    public ZipCode() {
    }

    @Override
    public Point getCenterPoint() {
        return null;
    }

    public TColor getColor() {
        return this.color;
    }

    @Override
    public Geometry getExportGeometry() {
        return getMultiPolygon();
    }

    public MultiPolygon getMultiPolygon() {
        return this.polygons;
    }

    @Override
    public MultiPolygon[] getQTGeometries() {
        return new MultiPolygon[] { polygons };
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(polygons);
    }

    public void setMultiPolygon(MultiPolygon mp) {
        this.polygons = mp;
    }
}
