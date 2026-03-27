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
import nl.tytech.data.engine.item.MapMeasure;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.data.engine.other.AbstractSpatial;
import nl.tytech.util.JTSUtils;

/**
 * UpgradeSpatial: contains info about upgrades in measures.
 *
 * @author Frank Baars
 */
public class UpgradeSpatial extends AbstractSpatial {

    private static final class UpgradeFeature extends SpatialItem<UpgradeSpatial> {

        private static final long serialVersionUID = 3569099219860503367L;

        public UpgradeFeature(MapMeasure mapMeasure, UpgradeSpatial spatial) {
            super(mapMeasure, spatial);
        }

        @Override
        public Map<String, Object> getExportAttributes(boolean inherited) {

            Map<String, Object> map = super.getExportAttributes(inherited);

            UpgradeType type = mapMeasure.getItem(MapLink.UPGRADE_TYPES, spatial.getUpgradeID());
            if (type != null) {
                map.put(UPGRADE_TYPE, type.getName());
            }

            map.put(UPGRADE_ID, spatial.getUpgradeID());
            map.put(SPATIAL_ID, spatial.getID());
            map.put(SPATIAL_TYPE, MeasureEditType.UPGRADE);

            return map;
        }
    }

    public static final String UPGRADE_ID = "UPGRADE_ID";
    public static final String UPGRADE_TYPE = "UPGRADE_TYPE";

    private static final long serialVersionUID = 3297087351882502135L;

    @XMLValue
    private MultiPolygon multiPolygon = JTSUtils.EMPTY;

    @XMLValue
    @ItemIDField(MapLink.UPGRADE_TYPES)
    private Integer upgradeID = Item.NONE;

    public UpgradeSpatial() {
        super(Item.NONE);
    }

    public UpgradeSpatial(Integer id, Integer upgradeID) {
        super(id);
        this.upgradeID = upgradeID;
    }

    public void addMultiPolygon(MultiPolygon add) {
        this.multiPolygon = JTSUtils.union(this.multiPolygon, add);
    }

    public SpatialItem<UpgradeSpatial> getFeature(MapMeasure mapMeasure) {
        return new UpgradeFeature(mapMeasure, this);
    }

    @Override
    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public Integer getUpgradeID() {
        return upgradeID;
    }

    public UpgradeType getUpgradeType(Lord lord) {
        return lord.<UpgradeType> getMap(MapLink.UPGRADE_TYPES).get(upgradeID);
    }

    public void removeMultiPolygon(MultiPolygon remove) {
        this.multiPolygon = JTSUtils.difference(this.multiPolygon, remove);
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public void setUpgradeID(Integer upgradeID) {
        this.upgradeID = upgradeID;
    }

}
