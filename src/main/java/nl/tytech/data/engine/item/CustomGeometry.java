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

import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.index.quadtree.Quadtree;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Custom 3D Geometry for one or multiple buildings
 *
 * Data Layout is as follows: each Triangle has 18 data values:
 *
 * 1 Triangle Coordinate (x, y (height), z and texture coordinate: tx1, tx2)
 *
 * 2 Triangle Coordinate (x, y (height), z and texture coordinate: tx1, tx2)
 *
 * 3 Triangle Coordinate (x, y (height), z and texture coordinate: tx1, tx2)
 *
 * 4 Normal vector (x, y, z)
 *
 * @author Maxim Knepfle
 *
 */
public class CustomGeometry extends Item {

    public static final TColor DEFAULT_COLOR = TColor.LIGHT_GRAY;

    public static final String TEXTURES_LOC = "Textures/Custom/";

    public static final String DEFAULT_TEXTURE = "default.jpg";

    public static final int TRIANGLE_DATA_LENGTH = 18;

    private static final long serialVersionUID = 6607007330802054677L;

    // small values against round offs
    private static final double MIN_TRIANGLE_AREA = 0.0001;

    @XMLValue
    private float[] data = null;

    @XMLValue
    private String texture = null;

    @XMLValue
    private TColor color = DEFAULT_COLOR;

    @JsonIgnore
    private transient Quadtree cachedTree = null;

    @XMLValue
    private boolean absoluteHeight = false;

    @XMLValue
    private boolean backFaceCulling = false; // default false to be backward compatible with pre 2024 model with bad normals

    public boolean clearCache() {

        if (cachedTree != null) {
            cachedTree = null;
            return true;
        }
        return false;
    }

    /**
     * Approximation of data byte size, when cached count it twice.
     */
    public long getByteSize() {

        if (data == null) {
            return 0l;
        }
        long byteSize = (long) data.length * (long) Float.BYTES;
        return cachedTree != null ? 2l * byteSize : byteSize;
    }

    public TColor getColor() {
        return color;
    }

    public float[] getData() {
        return data;
    }

    public final Envelope getEnvelope() {

        Envelope envelope = new Envelope();
        for (int i = 0; i < data.length; i += TRIANGLE_DATA_LENGTH) {
            for (int j = 0; j < 3; j++) { // note: Y is height thus use X, Z
                envelope.expandToInclude(new Coordinate(data[i + j * 5 + 0], data[i + j * 5 + 2]));
            }
        }
        return envelope;
    }

    public double getMaxHeightM(PreparedGeometry cell, PreparedGeometry center) {

        double maxHeightM = 0.0;
        for (Polygon triangle : getTriangles(cell.getGeometry().getEnvelopeInternal())) {
            double z = JTSUtils.getTriangleZ(triangle, center.getGeometry().getCoordinate());
            if (Double.isFinite(z) && z > maxHeightM) {
                maxHeightM = z;
            }
        }
        return maxHeightM;
    }

    public String getTextureLocation() {
        return TEXTURES_LOC + (hasTexture() ? texture : DEFAULT_TEXTURE);
    }

    @SuppressWarnings("unchecked")
    private synchronized List<Polygon> getTriangles(Envelope searchEnv) {

        Quadtree qt = cachedTree;
        if (qt == null) {
            qt = new Quadtree();

            for (int i = 0; i < data.length; i += TRIANGLE_DATA_LENGTH) {
                Coordinate[] coords = new Coordinate[4];
                coords[0] = new Coordinate(data[i + 0], data[i + 2], data[i + 1]);
                coords[1] = new Coordinate(data[i + 5], data[i + 7], data[i + 6]);
                coords[2] = new Coordinate(data[i + 10], data[i + 12], data[i + 11]);
                coords[3] = coords[0]; // first is also last

                // create polygon direct to speed up
                Polygon triangle = JTSUtils.sourceFactory.createPolygon(coords);
                if (triangle != null && triangle.getArea() > MIN_TRIANGLE_AREA) {
                    qt.insert(triangle.getEnvelopeInternal(), triangle);
                }
            }
            // store in cache
            cachedTree = qt;
        }
        return qt.query(searchEnv);
    }

    public boolean hasTexture() {
        return StringUtils.containsData(texture);
    }

    public boolean isAbsoluteHeight() {
        return absoluteHeight;
    }

    public boolean isBackFaceCulling() {
        return backFaceCulling;
    }

    public void setAbsoluteHeight(boolean absolute) {
        this.absoluteHeight = absolute;
    }

    public void setBackFaceCulling(boolean backFaceCulling) {
        this.backFaceCulling = backFaceCulling;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setData(float[] data) {
        this.data = data;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    @Override
    public String toString() {
        return CustomGeometry.class.getSimpleName() + " " + this.getID();
    }
}
