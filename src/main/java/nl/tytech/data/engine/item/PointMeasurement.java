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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.util.JTSUtils;

/**
 * Overlay measurement with point data
 *
 * @author Frank Baars
 */
public class PointMeasurement extends Measurement {

    private static final long serialVersionUID = 3114836218866255001L;

    @XMLValue
    private Point point = null;

    @XMLValue
    private HashMap<Integer, float[]> overlayValues = new HashMap<>();

    @XMLValue
    private HashMap<Integer, float[]> overlayMaqValues = new HashMap<>();

    @XMLValue
    private double min = Double.MAX_VALUE;

    @XMLValue
    private double max = -Double.MAX_VALUE;

    @XMLValue
    private double maqMin = Double.MAX_VALUE;

    @XMLValue
    private double maqMax = -Double.MAX_VALUE;

    public PointMeasurement() {

    }

    public PointMeasurement(Integer[] overlayIDs, String name, Point point, boolean saveToInit, boolean sum) {
        super(overlayIDs, name, saveToInit, sum);
        this.point = point;
    }

    @Override
    public Point getCenterPoint() {
        return point;
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {

        Map<String, Object> map = new LinkedHashMap<>();
        Overlay overlay = getOverlay();
        if (overlay == null) {
            return map;
        }

        float[] tvalues = getOverlayValues(getDefaultMap(), overlay.getID());
        float[] sumValues = null;
        if (isSum() && getOverlayIDs().size() > 1) {
            sumValues = getOverlayValues(getDefaultMap(), getOverlayIDs().get(1));
        }

        // always create unique keys
        for (int i = 0; i < tvalues.length && i < overlay.getTimeframes(); i++) {
            String txt = overlay.getTimeframeText(i);
            String key = txt;
            for (int e = 2; e < Integer.MAX_VALUE && map.containsKey(key); e++) {
                key = txt + " " + e;
            }
            map.put(key, sumValues != null && sumValues.length > i ? tvalues[i] + sumValues[i] : tvalues[i]);
        }
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        double buffer = getOverlay() instanceof GridOverlay go ? go.getCellSizeM() : 1;
        return JTSUtils.createSquare(point, buffer * 0.5);
    }

    public double getMax(MapType mapType) {
        return mapType == MapType.CURRENT ? max : maqMax;
    }

    public double getMin(MapType mapType) {
        return mapType == MapType.CURRENT ? min : maqMin;
    }

    public float[] getOverlayValues(MapType mapType, Integer overlayID) {
        return (mapType == MapType.CURRENT ? overlayValues : overlayMaqValues).get(overlayID);
    }

    @Override
    public GeometryCollection[] getQTGeometries() {
        return point == null ? new GeometryCollection[0] : new GeometryCollection[] { JTSUtils.createCollection(point) };
    }

    @Override
    public MeasurementType getType() {
        return MeasurementType.POINT;
    }

    @Override
    public boolean hasOverlappingKeys() {

        Overlay overlay = getOverlay();
        if (overlay == null) {
            return false;
        }
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < overlay.getTimeframes(); i++) {
            if (!keys.add(overlay.getTimeframeText(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void removeOverlayData(Integer overlayID) {
        overlayValues.remove(overlayID);
        overlayMaqValues.remove(overlayID);
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(point);
    }

    public void setTimeframeValues(MapType mapType, Integer overlayID, float[] timeframeValues) {

        if (!hasOverlay(overlayID)) {
            return;
        }
        if (mapType == MapType.CURRENT) {
            this.overlayValues.put(overlayID, timeframeValues);
        } else {
            this.overlayMaqValues.put(overlayID, timeframeValues);
        }
    }

    @Override
    public void updateMinMax(MapType mapType) {

        List<Integer> overlayIDs = getOverlayIDs();
        if (overlayIDs.isEmpty()) {
            return;
        }
        float[] tfvalues = getOverlayValues(mapType, overlayIDs.get(0));
        if (tfvalues == null || tfvalues.length == 0) {
            return;
        }

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (int t = 0; t < tfvalues.length; t++) {

            double sumValue = 0;
            for (int i = overlayIDs.size() - 1; i >= 0; i--) {

                Integer overlayID = overlayIDs.get(i);
                float[] tboValues = mapType == MapType.CURRENT ? overlayValues.get(overlayID) : overlayMaqValues.get(overlayID);

                if (tboValues != null && tboValues.length > 0) {

                    double value = tboValues[Math.min(t, tboValues.length - 1)];

                    if (value > GridOverlay.NO_DATA) {

                        sumValue = i == overlayIDs.size() - 1 ? value : sumValue + value;

                        if (isSum()) {
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
        if (mapType == MapType.MAQUETTE) {
            this.maqMin = min;
            this.maqMax = max;
        } else {
            this.min = min;
            this.max = max;
        }
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        // convert old double to floats
        for (Entry<Integer, ?> entry : new ArrayList<>(overlayValues.entrySet())) {
            if (entry.getValue() instanceof double[] old) {
                float[] updated = new float[old.length];
                for (int x = 0; x < old.length; x++) {
                    updated[x] = (float) old[x];
                }
                overlayValues.put(entry.getKey(), updated);
            }
        }
        for (Entry<Integer, ?> entry : new ArrayList<>(overlayMaqValues.entrySet())) {
            if (entry.getValue() instanceof double[] old) {
                float[] updated = new float[old.length];
                for (int x = 0; x < old.length; x++) {
                    updated[x] = (float) old[x];
                }
                overlayMaqValues.put(entry.getKey(), updated);
            }
        }
        return result;
    }
}
