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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;

/**
 * Overlay measurement in line form
 *
 * @author Frank Baars
 */
public class LineMeasurement extends Measurement {

    private static final long serialVersionUID = 3114836218866255111L;

    public static final Integer LAST_TIME_FRAME = -1;

    @XMLValue
    private LineString lineString = null;

    @XMLValue
    private double[] distances = new double[0];

    @XMLValue
    private HashMap<Integer, ArrayList<float[]>> values = new HashMap<>();

    @XMLValue
    private HashMap<Integer, ArrayList<float[]>> maqValues = new HashMap<>();

    private LineSegment lineSegment = null;

    @XMLValue
    private double min = Double.MAX_VALUE;

    @XMLValue
    private double max = -Double.MAX_VALUE;

    @XMLValue
    private double maqMin = Double.MAX_VALUE;

    @XMLValue
    private double maqMax = -Double.MAX_VALUE;

    public LineMeasurement() {

    }

    public LineMeasurement(Integer[] overlayIDs, String name, LineString lineString, boolean saveToInit, boolean sum) {
        super(overlayIDs, name, saveToInit, sum);
        this.lineString = lineString;
    }

    @Override
    public Point getCenterPoint() {
        return lineString == null ? null : lineString.getCentroid();
    }

    public double[] getDistances() {
        return distances;
    }

    public float[] getDistanceValues(MapType mapType, Integer overlayID, int timeframe) {

        ArrayList<float[]> timeframeValues = getValues(mapType, overlayID);
        if (timeframeValues == null || timeframeValues.isEmpty()) {
            return null;
        }
        int mytimeframe = MathUtils.clamp(timeframe, 0, timeframeValues.size() - 1);
        return timeframeValues.get(mytimeframe);
    }

    @Override
    public Geometry getExportGeometry() {
        double buffer = getOverlay() instanceof GridOverlay go ? go.getCellSizeM() : 1;
        return JTSUtils.bufferSimple(lineString, buffer * 0.5);
    }

    public LineSegment getLineSegment() {
        if (lineSegment == null && lineString != null) {
            lineSegment = new LineSegment(new Coordinate(lineString.getCoordinateN(0)),
                    new Coordinate(lineString.getCoordinateN(lineString.getNumPoints() - 1)));
        }
        return lineSegment;
    }

    public LineString getLineString() {
        return lineString;
    }

    public double getMax(MapType mapType) {
        return mapType == MapType.CURRENT ? max : maqMax;
    }

    public double getMin(MapType mapType) {
        return mapType == MapType.CURRENT ? min : maqMin;
    }

    @Override
    public GeometryCollection[] getQTGeometries() {
        if (lineString == null) {
            return new GeometryCollection[0];
        }
        return new GeometryCollection[] { JTSUtils.createCollection(lineString) };
    }

    @Override
    public MeasurementType getType() {
        return MeasurementType.LINE;
    }

    public ArrayList<float[]> getValues(MapType mapType, Integer overlayID) {
        Map<Integer, ArrayList<float[]>> map = mapType == MapType.CURRENT ? values : maqValues;
        return map.get(overlayID);
    }

    @Override
    protected void removeOverlayData(Integer overlayID) {
        values.remove(overlayID);
        maqValues.remove(overlayID);
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(lineString);
    }

    public void setDistances(double[] distances) {
        this.distances = distances;
    }

    public void setValues(MapType mapType, Integer overlayID, ArrayList<float[]> timeframeValues) {
        if (!hasOverlay(overlayID)) {
            return;
        }

        if (mapType == MapType.CURRENT) {
            this.values.put(overlayID, timeframeValues);
        } else {
            this.maqValues.put(overlayID, timeframeValues);
        }

    }

    @Override
    public void updateMinMax(MapType mapType) {

        List<Integer> overlayIDs = getOverlayIDs();
        if (overlayIDs.isEmpty()) {
            return;
        }
        ArrayList<float[]> timeframeValues = getValues(mapType, overlayIDs.get(0));
        if (timeframeValues == null || timeframeValues.isEmpty()) {
            return;
        }

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (int t = 0; t < timeframeValues.size(); t++) {

            for (int i = 0; i < distances.length; i++) {

                double sumValue = 0;

                for (int o = overlayIDs.size() - 1; o >= 0; o--) {

                    Integer overlayID = overlayIDs.get(o);
                    ArrayList<float[]> overlayValues = getValues(mapType, overlayID);

                    if (overlayValues != null && !overlayValues.isEmpty()) {

                        float[] values = overlayValues.get(Math.min(t, overlayValues.size() - 1));

                        if (values != null && i < values.length) {

                            double value = values[i];

                            if (value > GridOverlay.NO_DATA) {

                                sumValue = o == overlayIDs.size() - 1 ? value : sumValue + value;

                                if (isSum()) {
                                    // TODO: This does not work when the gridsizes of both overlays are different
                                    min = Math.min(sumValue, min);
                                    max = Math.max(sumValue, max);
                                } else {
                                    min = Math.min(value, min);
                                    max = Math.max(value, max);

                                }
                            }
                        }
                    }
                }

            }
        }
        if (mapType == MapType.MAQUETTE) {
            this.maqMin = min;
            this.maqMax = max;
        } else {
            this.min = min;
            this.max = max;
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        // convert old double to floats
        for (ArrayList value : values.values()) {
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i) instanceof double[]) {
                    double[] old = (double[]) value.get(i);
                    float[] updated = new float[old.length];
                    for (int x = 0; x < old.length; x++) {
                        updated[x] = (float) old[x];
                    }
                    value.set(i, updated);
                }
            }
        }
        for (ArrayList value : maqValues.values()) {
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i) instanceof double[]) {
                    double[] old = (double[]) value.get(i);
                    float[] updated = new float[old.length];
                    for (int x = 0; x < old.length; x++) {
                        updated[x] = (float) old[x];
                    }
                    value.set(i, updated);
                }
            }
        }
        return result;
    }
}
