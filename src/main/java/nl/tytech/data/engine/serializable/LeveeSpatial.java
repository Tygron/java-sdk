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
package nl.tytech.data.engine.serializable;

import java.util.Map;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Levee;
import nl.tytech.data.engine.item.MapMeasure;
import nl.tytech.data.engine.other.AbstractSpatial;
import nl.tytech.util.JTSUtils;

/**
 * Levee part of measure
 *
 * @author Frank Baars
 */
public class LeveeSpatial extends AbstractSpatial {

    private static final class LeveeFeature extends SpatialItem<LeveeSpatial> {

        private static final long serialVersionUID = -2451587883676059602L;

        public LeveeFeature(MapMeasure mapMeasure, LeveeSpatial spatial) {
            super(mapMeasure, spatial);
        }

        @Override
        public Map<String, Object> getExportAttributes(boolean inherited) {

            Map<String, Object> map = super.getExportAttributes(inherited);

            Levee levee = mapMeasure.getItem(MapLink.LEVEES, spatial.getLeveeID());

            if (levee != null) {
                map.put(LEVEE, levee.getName());
                map.put(ANGLE, spatial.getAngle(levee.getLord()));
                map.put(HEIGHT, spatial.getHeight(levee.getLord()));
                map.put(RELATIVE, spatial.isRelative(levee.getLord()));

                if (spatial.hasAngleOverride()) {
                    map.put(ANGLE_OVERRIDE, spatial.getAngleOverride());
                }
                if (spatial.hasHeightOverride()) {
                    map.put(HEIGHT_OVERRIDE, spatial.getHeightOverride());
                }
                if (spatial.hasRelativeOverride()) {
                    map.put(RELATIVE_OVERRIDE, spatial.getRelativeOverride());
                }
            }

            map.put(LEVEE_ID, spatial.getLeveeID());
            map.put(SPATIAL_ID, spatial.getID());
            map.put(SPATIAL_TYPE, MeasureEditType.LEVEE);
            return map;
        }

    }

    public static final String LEVEE = "LEVEE";
    public static final String LEVEE_ID = "LEVEE_ID";
    public static final String ANGLE = "ANGLE";
    public static final String ANGLE_OVERRIDE = "ANGLE_OVERRIDE";
    public static final String HEIGHT_OVERRIDE = "HEIGHT_OVERRIDE";
    public static final String RELATIVE_OVERRIDE = "RELATIVE_OVERRIDE";

    private static final long serialVersionUID = 3297087351882502145L;

    @XMLValue
    private MultiPolygon multiPolygon = JTSUtils.EMPTY;

    @XMLValue
    @ItemIDField(MapLink.LEVEES)
    private Integer leveeID = Item.NONE;

    @XMLValue
    private Double heightOverride = null;

    @XMLValue
    private Boolean isRelativeOverride = null;

    @XMLValue
    private Double angleOverride = null;

    // only reference locally
    private MultiPolygon topPolygon;
    private MultiPolygon sidePolygon;

    public LeveeSpatial() {
        super(Item.NONE);
    }

    public LeveeSpatial(Integer id, Integer leveeID) {
        super(id);
        this.leveeID = leveeID;
    }

    public void addMultiPolygon(MultiPolygon add) {
        this.multiPolygon = JTSUtils.union(this.multiPolygon, add);
    }

    public double getAngle(Lord lord) {
        if (angleOverride != null) {
            return angleOverride;
        }
        return getLevee(lord).getAngleDegrees();
    }

    public Double getAngleOverride() {
        return angleOverride;
    }

    public SpatialItem<LeveeSpatial> getFeature(MapMeasure mapMeasure) {
        return new LeveeFeature(mapMeasure, this);
    }

    public double getHeight(Lord lord) {
        if (heightOverride != null) {
            return heightOverride;
        }
        return getLevee(lord).getDefaultHeightM();
    }

    public Double getHeightOverride() {
        return heightOverride;
    }

    public Levee getLevee(Lord lord) {
        return lord.<Levee> getMap(MapLink.LEVEES).get(leveeID);
    }

    public Integer getLeveeID() {
        return leveeID;
    }

    @Override
    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public Boolean getRelativeOverride() {
        return isRelativeOverride;
    }

    public MultiPolygon getSideMultiPolygon() {
        return sidePolygon;
    }

    public MultiPolygon getTopMultiPolygon() {
        return topPolygon;
    }

    public boolean hasAngleOverride() {
        return angleOverride != null;
    }

    public boolean hasHeightOverride() {
        return heightOverride != null;
    }

    public boolean hasRelativeOverride() {
        return isRelativeOverride != null;
    }

    public boolean isRelative(Lord lord) {
        if (isRelativeOverride != null) {
            return isRelativeOverride;
        }
        return getLevee(lord).isRelativeIncrease();
    }

    public void removeMultiPolygon(MultiPolygon remove) {
        this.multiPolygon = JTSUtils.difference(this.multiPolygon, remove);
    }

    public void setAngleOverride(Double angleOverride) {
        this.angleOverride = angleOverride;
    }

    public void setHeightOverride(Double heightOverride) {
        this.heightOverride = heightOverride;
    }

    public void setInnerOuter(MultiPolygon inner, MultiPolygon outer) {
        topPolygon = inner;
        sidePolygon = outer;
    }

    public void setLeveeID(Integer leveeID) {
        this.leveeID = leveeID;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public void setRelativeOverride(Boolean relativeOverride) {
        this.isRelativeOverride = relativeOverride;
    }
}
