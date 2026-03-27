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

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.SourcedItem;
import nl.tytech.data.core.other.LargeCloneItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.serializable.TerrainSpatial;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;

/**
 * HeightSector
 * @author Maxim Knepfle
 */
public class HeightSector extends SourcedItem implements GeometryItem<MultiPolygon>, LargeCloneItem {

    public enum HeightOperator {

        AVG,

        MIN,

        MAX,

        VOLUME,

        STDEV,

        AREA;
    }

    private static class HeightSquare {

        private final double x;
        private final double y;
        private final double xMin;
        private final double yMin;
        private final double xMax;
        private final double yMax;

        public HeightSquare(double x, double y, int xMin, int yMin, int xMax, int yMax) {
            this.x = x;
            this.y = y;
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
        }

        public double getQ11() {
            return (xMax - x) * (yMax - y);
        }

        public double getQ12() {
            return (xMax - x) * (y - yMin);
        }

        public double getQ21() {
            return (x - xMin) * (yMax - y);
        }

        public double getQ22() {
            return (x - xMin) * (y - yMin);
        }

        public double getX1() {
            return xMax - x;
        }

        public double getX2() {
            return x - xMin;
        }

        public double getY1() {
            return yMax - y;
        }

        public double getY2() {
            return y - yMin;
        }
    }

    /**
     * Surrounding area is lower on detail (DEM, Sat images) by this factor
     */
    public static final double SURROUNDING_DETAIL_FACTOR = 4.0;

    public static final double DEFAULT_POINT_SIZE_M = 1.0;

    private static final long serialVersionUID = -4253782210241563588L;

    public static final double INVALID_DATA = -1_000_000d; // below this is unrealistic

    /**
     * Decimals used to store the actual data that is used in e.g. calc models
     */
    public static final int DATA_DECIMALS = 3;

    private static final double MIN_HEIGHT_CHANGE_M = 1d / Math.pow(10d, DATA_DECIMALS);

    private static final float[][] getSquareMatrix(MapType mapType, Item unusedItem, int dim) {

        if (unusedItem instanceof HeightSector unusedSector) {
            float[][] array;
            if (mapType == MapType.CURRENT) {
                array = unusedSector.currentData;
            } else if (mapType == MapType.MAQUETTE) {
                array = unusedSector.maquetteData;
            } else {
                array = unusedSector.buildingData;
            }
            if (MathUtils.isDimension(array, dim, dim)) {
                return array;
            }
        }
        return new float[dim][dim];
    }

    @XMLValue
    @JsonIgnore
    private transient float[][] currentData = new float[0][0];

    @XMLValue
    @JsonIgnore
    private transient float[][] maquetteData = new float[0][0];

    /**
     * Used during world creation to calculate building height
     */
    @JsonIgnore
    private transient float[][] buildingData = new float[0][0];

    @XMLValue
    private MultiPolygon square;

    private transient Envelope envelope = null;

    /**
     * Var for client side check if map has changed
     */
    private transient Boolean changed = null;

    private final int[] pointWidth = new int[2];

    private double pointAreaM2;

    public HeightSector() {

    }

    public boolean clearMaquetteData() {

        // only reset when it had original data
        if (maquetteData != null && maquetteData.length > 0) {
            maquetteData = new float[0][0];
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Item cloneItem(Item unusedItem) {

        // clone base first: note do not allow clone item method
        HeightSector clone = ObjectUtils.deepCopy(this, false);

        // copy square matrices: note this.currentData.length == dimX == dimY
        clone.currentData = getSquareMatrix(MapType.CURRENT, unusedItem, MathUtils.getDimY(this.currentData));
        MathUtils.matrixCopy(this.currentData, clone.currentData);

        clone.maquetteData = getSquareMatrix(MapType.MAQUETTE, unusedItem, MathUtils.getDimY(this.maquetteData));
        MathUtils.matrixCopy(this.maquetteData, clone.maquetteData);

        clone.buildingData = getSquareMatrix(null, unusedItem, this.buildingData.length);
        MathUtils.matrixCopy(this.buildingData, clone.buildingData);

        return clone;
    }

    public int[] getBoundaries(Geometry geom) {

        MultiPolygon intersection = JTSUtils.intersection(geom, square);
        if (!JTSUtils.hasArea(intersection)) {
            return null;
        }

        Envelope env = intersection.getEnvelopeInternal();
        HeightSquare xyupperLeft = getXYArray(env.getMinX(), env.getMinY());
        HeightSquare xylowerright = getXYArray(env.getMaxX(), env.getMaxY());

        // expand outwards thus floor/ceil
        return new int[] { (int) Math.floor(xyupperLeft.x), (int) Math.floor(xyupperLeft.y), (int) Math.ceil(xylowerright.x),
                (int) Math.ceil(xylowerright.y) };

    }

    @Override
    public Point getCenterPoint() {

        double x = this.getStartX() + this.getWidthM() / 2d;
        double y = this.getStartY() + this.getWidthM() / 2d;

        if (this.getLord().isServerSide()) {
            int dim = getWidthPoints(); // server side includes center Z
            double z = this.getHeightForIndexes(MapType.CURRENT, dim / 2, dim / 2);
            return JTSUtils.createPoint(x, y, z);

        } else {
            return JTSUtils.createPoint(x, y);
        }
    }

    public float[][] getData(MapType mapType) {
        return getData(mapType, false);
    }

    public float[][] getData(MapType mapType, boolean includeBuilding) {

        // only during map creation
        if (includeBuilding) {
            return buildingData;
        }

        // current return current
        if (mapType == null || mapType == MapType.CURRENT) {
            return currentData;
        }

        // this works because its square dimX == dimY
        return maquetteData.length == currentData.length ? maquetteData : currentData;
    }

    public Envelope getEnvelope() {
        return this.getSquare().getEnvelopeInternal();
    }

    @Override
    public Geometry getExportGeometry() {
        return this.getSquare();
    }

    /**
     * Get the approximate interpolated value at world coordinates X,Y
     * @param wx
     * @param wy
     * @return aprox value
     */
    public Double getHeight(MapType mapType, double wx, double wy) {

        HeightSquare xy = getXYArray(wx, wy);
        return getHeightForCoordinate(mapType, xy);
    }

    private Double getHeightBilinear(MapType mapType, HeightSquare heightSquare) {
        double count = 0;
        double height = 0;
        Double h11 = getHeightForIndexes(mapType, (int) heightSquare.xMin, (int) heightSquare.yMin);
        Double h21 = getHeightForIndexes(mapType, (int) heightSquare.xMax, (int) heightSquare.yMin);
        Double h12 = getHeightForIndexes(mapType, (int) heightSquare.xMin, (int) heightSquare.yMax);
        Double h22 = getHeightForIndexes(mapType, (int) heightSquare.xMax, (int) heightSquare.yMax);

        if (h11 != null) {
            height += heightSquare.getQ11() * h11;
            count += heightSquare.getQ11();
        }
        if (h21 != null) {
            height += heightSquare.getQ21() * h21;
            count += heightSquare.getQ21();
        }
        if (h12 != null) {
            height += heightSquare.getQ12() * h12;
            count += heightSquare.getQ12();
        }
        if (h22 != null) {
            height += heightSquare.getQ22() * h22;
            count += heightSquare.getQ22();
        }

        if (count == 0) {
            return null;
        }

        return height / count;
    }

    private Double getHeightForCoordinate(MapType mapType, HeightSquare heightSquare) {

        if (heightSquare.xMin == heightSquare.xMax && heightSquare.yMin == heightSquare.yMax) {
            return getHeightForIndexes(mapType, (int) heightSquare.xMin, (int) heightSquare.yMin);
        }

        if (heightSquare.xMin != heightSquare.xMax && heightSquare.yMin == heightSquare.yMax) {
            return getHeightForLinearX(mapType, heightSquare);
        }

        if (heightSquare.xMin == heightSquare.xMax && heightSquare.yMin != heightSquare.yMax) {
            return getHeightForLinearY(mapType, heightSquare);
        }

        return getHeightBilinear(mapType, heightSquare);

    }

    public Double getHeightForIndexes(MapType mapType, int x, int y) {

        int dim = getWidthPoints();
        if (x < 0 || x >= dim || y < 0 || y >= dim) {
            return null;
        }
        float[][] data = this.getData(mapType);
        if (data[y][x] <= INVALID_DATA) {
            return null;
        }
        return (double) data[y][x];

    }

    private Double getHeightForLinearX(MapType mapType, HeightSquare heightCoordinate) {
        Double h11 = getHeightForIndexes(mapType, (int) heightCoordinate.xMin, (int) heightCoordinate.yMin);
        Double h21 = getHeightForIndexes(mapType, (int) heightCoordinate.xMax, (int) heightCoordinate.yMin);
        double count = 0;
        double height = 0;

        if (h11 != null) {
            height += heightCoordinate.getX1() * h11;
            count += heightCoordinate.getX1();
        }
        if (h21 != null) {
            height += heightCoordinate.getX2() * h21;
            count += heightCoordinate.getX2();
        }
        if (count == 0) {
            return null;
        }

        return height / count;
    }

    private Double getHeightForLinearY(MapType mapType, HeightSquare heightCoordinate) {
        Double h11 = getHeightForIndexes(mapType, (int) heightCoordinate.xMin, (int) heightCoordinate.yMin);
        Double h12 = getHeightForIndexes(mapType, (int) heightCoordinate.xMin, (int) heightCoordinate.yMax);

        double count = 0;
        double height = 0;

        if (h11 != null) {
            height += heightCoordinate.getY1() * h11;
            count += heightCoordinate.getY1();
        }
        if (h12 != null) {
            height += heightCoordinate.getY2() * h12;
            count += heightCoordinate.getY2();
        }
        if (count == 0) {
            return null;
        }

        return height / count;
    }

    public double getHeightM() {
        return getEnvelope().getHeight();
    }

    public float[][] getOrCreate(MapType mapType) {
        if (mapType == MapType.CURRENT) {
            return getData(mapType, false);
        } else {
            int dim = this.getWidthPoints();
            if (!MathUtils.isDimension(maquetteData, dim, dim)) {
                maquetteData = new float[dim][dim];
                MathUtils.matrixCopy(currentData, maquetteData);

            }
            return maquetteData;

        }
    }

    public double getPointAreaM2() {
        return pointAreaM2;
    }

    public Point getPointForXY(double x, double y) {

        if (envelope == null) {
            envelope = square.getEnvelopeInternal();
        }
        double width = getWidthPoints();
        double px = envelope.getMinX() + x / width * (envelope.getMaxX() - envelope.getMinX());
        double py = envelope.getMinY() + y / width * (envelope.getMaxY() - envelope.getMinY());

        return JTSUtils.createPoint(px, py);
    }

    public double getPointWidth() {
        Setting sectorSizeM = getItem(MapLink.SETTINGS, Setting.Type.SECTOR_SIZE_M);
        return sectorSizeM != null && pointWidth[0] > 0 ? sectorSizeM.getDoubleValue() / pointWidth[0] : DEFAULT_POINT_SIZE_M;

    }

    @Override
    public MultiPolygon[] getQTGeometries() {
        return new MultiPolygon[] { this.getSquare() };
    }

    public String getSatImageLocation(int resoIndex) {

        Setting setting = getItem(MapLink.SETTINGS, Setting.Type.SATELLITE_FILE_NAME);
        String[] names = setting.getStringArrayValue();
        resoIndex = MathUtils.clamp(resoIndex, 0, names.length); // validate resolution index value
        return Setting.SATELLITE_IMAGE_LOCATION + names[resoIndex] + getID() + ".jpg";
    }

    public MultiPolygon getSquare() {
        return this.square;
    }

    public double getStartX() {
        return getEnvelope().getMinX();
    }

    public double getStartY() {
        return getEnvelope().getMinY();
    }

    public double getWidthM() {
        return getEnvelope().getWidth();
    }

    public int getWidthPoints() {
        return getWidthPoints(false);
    }

    public int getWidthPoints(boolean building) {
        return pointWidth[building ? 1 : 0];
    }

    private HeightSquare getXYArray(double wx, double wy) {

        wx -= getStartX();
        wy -= getStartY();
        double width = this.getWidthM();
        double fractionX = MathUtils.clamp(wx / width, 0d, 1d);
        double fractionY = MathUtils.clamp(wy / width, 0d, 1d);

        double x = (getWidthPoints() - 1) * fractionX;
        double y = (getWidthPoints() - 1) * fractionY;

        // expand outwards thus floor/ceil
        return new HeightSquare(x, y, (int) Math.floor(x), (int) Math.floor(y), (int) Math.ceil(x), (int) Math.ceil(y));
    }

    /**
     * Check client side only if there is a difference between the CURRENT and MAQUETTE maps
     * @return
     */
    public boolean isClientSideChanged() {

        if (changed == null) {
            changed = false;

            // check for changes
            int dim = this.getWidthPoints();
            if (MathUtils.isDimension(maquetteData, dim, dim)) {
                for (int y = 0; y < dim; y++) {
                    float[] currentLine = currentData[y];
                    float[] maquetteLine = maquetteData[y];
                    for (int x = 0; x < dim; x++) {
                        if (currentLine[x] != maquetteLine[x]) {
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }
        return changed;
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(square);
    }

    /**
     * Only use for setting data once at start
     * @param mapType
     * @param data
     */
    public void setBaseHeightData(float[][] data) {
        this.setBaseHeightData(data, false);
    }

    public void setBaseHeightData(float[][] data, boolean includeBuilding) {

        if (includeBuilding) {
            this.buildingData = data;
        } else {
            this.currentData = data;
            this.maquetteData = new float[0][0];
        }
    }

    /**
     * Set value and round it off to DECIMALS place
     * @param mapType
     * @param x
     * @param y
     * @param value
     */
    public void setHeight(MapType mapType, int x, int y, double value) {

        float fValue = (float) MathUtils.round(value, DATA_DECIMALS);
        if (mapType == MapType.CURRENT) {
            currentData[y][x] = fValue;
            return;
        }

        // lazy create maquette data
        int dim = this.getWidthPoints();
        if (!MathUtils.isDimension(maquetteData, dim, dim)) {
            maquetteData = new float[dim][dim];
            MathUtils.matrixCopy(currentData, maquetteData);
        }
        maquetteData[y][x] = fValue;
    }

    /**
     * @return Changed height in volume M3
     */
    public final double setSpatialHeight(TerrainSpatial terrainSpatial) {

        MultiPolygon multiPolygon = terrainSpatial.getOuterMultiPolygon();
        double heightChangeM3 = 0;
        int[] boundaries = getBoundaries(multiPolygon);

        if (boundaries == null) {
            return heightChangeM3;
        }

        for (int x = boundaries[0]; x <= boundaries[2]; ++x) {
            for (int y = boundaries[1]; y <= boundaries[3]; ++y) {

                Double prevHeight = getHeightForIndexes(MapType.MAQUETTE, x, y);
                if (prevHeight == null) {
                    continue;
                }

                Point point = getPointForXY(x, y);
                double newHeight = terrainSpatial.getHeight(point, prevHeight);

                if (Math.abs(newHeight - prevHeight) > MIN_HEIGHT_CHANGE_M) {
                    setHeight(MapType.MAQUETTE, x, y, newHeight);
                    Double currentHeight = getHeightForIndexes(MapType.CURRENT, x, y);
                    heightChangeM3 -= pointAreaM2 * Math.abs(prevHeight - currentHeight);
                    heightChangeM3 += pointAreaM2 * Math.abs(newHeight - currentHeight);
                }
            }
        }
        return heightChangeM3;
    }

    public void setSquare(MultiPolygon square) {
        this.square = square;
        this.envelope = square.getEnvelopeInternal();
    }

    @Override
    public String toString() {
        return Integer.toString(getID());
    }

    private void updatePointAreaM2() {
        double pointAreaM = getPointWidth();
        this.pointAreaM2 = pointAreaM * pointAreaM;
    }

    public void updateValues() {

        pointWidth[0] = getData(null, false).length; // terrain
        pointWidth[1] = getData(null, true).length; // buildings
        updatePointAreaM2();
    }

    @Override
    public String validated(boolean startNewSession) {

        updateValues();
        return super.validated(startNewSession);
    }
}
