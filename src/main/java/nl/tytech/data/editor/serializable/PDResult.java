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
package nl.tytech.data.editor.serializable;

import static nl.tytech.data.engine.item.ParametricDesign.DEFAULT_VARIANT_ID;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.locationtech.jts.geom.MultiPolygon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.data.engine.item.ParametricDesign;
import nl.tytech.data.engine.item.ParametricDesign.Alignment;
import nl.tytech.data.engine.item.ParametricDesign.DesignAttribute;
import nl.tytech.data.engine.item.ParametricDesign.FunctionType;
import nl.tytech.data.engine.serializable.PlotDesign;
import nl.tytech.data.engine.serializable.PlotDesign.PlotDesignAttribute;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Result of Parametric Design
 * @author Maxim Knepfle
 *
 */
public class PDResult {

    @JsonIgnore
    private transient ParametricDesign design = null;

    private Map<FunctionType, Map<Integer, List<MultiPolygon>>> bandMPs = new EnumMap<>(FunctionType.class);
    private Map<FunctionType, Map<Integer, List<MultiPolygon>>> detailMPs = new EnumMap<>(FunctionType.class);

    private Map<FunctionType, Double> areaExisting = new EnumMap<>(FunctionType.class);

    public PDResult() {

    }

    public PDResult(ParametricDesign design) {
        this.design = ObjectUtils.deepCopy(design);
        this.design.setLord(design.getLord());
    }

    public PDResult(PDResult other) {
        this(other.design);
        areaExisting.putAll(other.areaExisting);
    }

    public void addPolygons(boolean detail, FunctionType type, List<MultiPolygon> polygons) {
        addPolygons(detail, DEFAULT_VARIANT_ID, type, polygons);
    }

    public synchronized void addPolygons(boolean detail, Integer variantID, FunctionType type, List<MultiPolygon> polygons) {

        Map<FunctionType, Map<Integer, List<MultiPolygon>>> map = getMap(detail);
        Map<Integer, List<MultiPolygon>> old = map.computeIfAbsent(type, k -> new TreeMap<>());
        List<MultiPolygon> list = old.get(variantID);
        if (list != null) {
            // add to existing
            list.addAll(polygons);
        } else {
            // put as new value
            old.put(variantID, polygons);
        }
    }

    public List<FunctionType> getActiveFunctionTypes() {

        List<FunctionType> list = new ArrayList<>();
        for (FunctionType type : FunctionType.values()) {
            if (design.isActive(type)) {
                list.add(type);
            }
        }
        return list;
    }

    public double getAdjustableFraction() {
        double sum = getBaseArea(FunctionType.VALUES);
        double area = design.getAreaM2();
        return sum >= area ? 0 : area / (area - sum);
    }

    public Alignment getAlignment() {
        return design.getAlignment();
    }

    public double getArea(boolean detail, FunctionType... types) {
        return getArea(detail, DEFAULT_VARIANT_ID, types);
    }

    public double getArea(boolean detail, Integer variantID, FunctionType... types) {
        return getBaseArea(types) + JTSUtils.getArea(getPolygons(detail, variantID, types));
    }

    public double getAttribute(DesignAttribute detail) {
        return getAttribute(detail, 0);
    }

    public double getAttribute(DesignAttribute detail, double min) {
        return getAttribute(detail, min, Double.MAX_VALUE);
    }

    public double getAttribute(DesignAttribute attribute, double min, double max) {
        return design != null && design.hasAttribute(attribute)
                ? design.getClampedAttribute(design.getDefaultMap(), attribute.name(), min, max)
                : MathUtils.clamp(attribute.defaultValue(), min, max);
    }

    public double getBaseArea(FunctionType... types) {
        double result = 0;
        for (FunctionType type : types) {
            result += areaExisting.getOrDefault(type, 0d);
        }
        return result;
    }

    public ParametricDesign getDesign() {
        return design;
    }

    public double getDesiredFraction(DesignAttribute attribute) {
        return MathUtils.clamp(getAttribute(attribute), 0.0, 1.0);
    }

    public final Map<DesignAttribute, Double> getFractions() {

        double remainder = 1.0;
        double area = design.getAreaM2();
        Map<DesignAttribute, Double> fractions = new TreeMap<>();

        for (FunctionType type : FunctionType.values()) {
            if (design.isActive(type)) {

                double detailArea = getArea(true, type);
                fractions.put(type.getFraction(), detailArea / area);
                remainder -= detailArea / area;

            }
        }

        if (remainder < 0) {
            if (remainder < -ParametricDesign.MARGIN) {
                TLogger.severe("Adjusting remainder from: " + remainder + " -> 0");
            }
            remainder = 0;
        }
        return fractions;
    }

    private Map<FunctionType, Map<Integer, List<MultiPolygon>>> getMap(boolean detail) {
        return detail ? this.detailMPs : this.bandMPs;
    }

    public double getPlotDepth() {

        double depth = 0;
        if (design.isActive(FunctionType.ROAD)) {
            depth += design.getOrDefault(DesignAttribute.ROAD_WIDTH_M) / 2.0;
        }
        if (design.isActive(FunctionType.PARKING)) {
            depth += design.getOrDefault(DesignAttribute.PARKING_WIDTH_M);
        }
        if (design.isActive(FunctionType.SIDEWALK)) {
            depth += design.getOrDefault(DesignAttribute.SIDEWALK_WIDTH_M);
        }
        if (design.isActive(FunctionType.BUILDING)) {
            double buildingDist = 0;
            for (PlotDesign pd : design.getPlotDesigns()) {
                double roadDistance = pd.getOrDefault(PlotDesignAttribute.ROAD_DISTANCE_M);
                double plotDepth = pd.getPlotDepth();
                double backyardDistanceM = design.getOrDefault(DesignAttribute.BACKYARD_DISTANCE_M);
                buildingDist = Math.max(buildingDist, roadDistance + plotDepth + backyardDistanceM);
            }
            depth += buildingDist;

        }
        return depth;
    }

    public List<PlotDesign> getPlotDesigns() {
        return design.getPlotDesigns();
    }

    public double getPlotWidth() {
        double depth = 0;
        if (design.isActive(FunctionType.ROAD)) {
            depth += design.getAttribute(DesignAttribute.ROAD_WIDTH_M) / 2.0;
        }
        if (design.isActive(FunctionType.PARKING)) {
            depth += design.getAttribute(DesignAttribute.PARKING_WIDTH_M);
        }
        if (design.isActive(FunctionType.SIDEWALK)) {
            depth += design.getAttribute(DesignAttribute.SIDEWALK_WIDTH_M);
        }
        if (design.isActive(FunctionType.BUILDING)) {
            double buildingDist = 0;
            for (PlotDesign pd : design.getPlotDesigns()) {
                double roadDistance = pd.getOrDefault(PlotDesignAttribute.ROAD_DISTANCE_M);
                double plotWidth = pd.getPlotWidth();
                double betweenDistance = pd.getOrDefault(PlotDesignAttribute.SIDE_DISTANCE_M);
                buildingDist = Math.max(buildingDist, roadDistance + plotWidth + betweenDistance);
            }
            depth += buildingDist;

        }
        return depth;
    }

    public List<MultiPolygon> getPolygons(boolean detail, FunctionType... types) {
        return getPolygons(detail, DEFAULT_VARIANT_ID, types);
    }

    public List<MultiPolygon> getPolygons(boolean detail, Integer variantID, FunctionType... types) {

        Map<FunctionType, Map<Integer, List<MultiPolygon>>> map = getMap(detail);
        List<MultiPolygon> mps = new ArrayList<>();
        for (FunctionType type : types) {

            Map<Integer, List<MultiPolygon>> variantMap = map.get(type);
            if (variantMap != null) {
                for (Entry<Integer, List<MultiPolygon>> entry : variantMap.entrySet()) {
                    if (DEFAULT_VARIANT_ID.equals(variantID) || entry.getKey().equals(variantID)) {
                        List<MultiPolygon> mpList = variantMap.get(entry.getKey());
                        if (mpList != null) {
                            mps.addAll(mpList);
                        }
                    }

                }
            }
        }
        return mps;
    }

    public double getPriorityDistance() {

        double score = 0.0;
        double area = design.getAreaM2();

        if (area != 0.0) {
            for (DesignAttribute attribute : DesignAttribute.getFractionValues()) {
                if (!attribute.isFraction()) {
                    continue;
                }
                List<FunctionType> list = new ArrayList<>();
                for (FunctionType type : FunctionType.VALUES) {
                    if (type.getFraction() == attribute && design.isActive(type)) {
                        list.add(type);
                    }
                }
                FunctionType[] types = list.toArray(new FunctionType[list.size()]);

                if (types.length > 0) {
                    double a = getArea(true, types) / area;
                    double d = getDesiredFraction(attribute);
                    score += types[0].getPriority() * Math.abs(a - d);

                }
            }
        }
        return score;
    }

    public boolean isChanged(ParametricDesign item) {

        if (this.design == null || item == null) {
            return item != design;
        }

        if (!this.design.getID().equals(item.getID())) {
            return true;
        }

        return design.getVersion() != item.getVersion();
    }

    public boolean isChanged(PDResult itemDesign) {
        return itemDesign != null && isChanged(itemDesign.design);
    }

    public void setAttribute(DesignAttribute attribute, double value) {
        design.setAttribute(attribute, value);
    }

    public void setExistingArea(FunctionType type, double area) {
        areaExisting.put(type, area);
    }
}
