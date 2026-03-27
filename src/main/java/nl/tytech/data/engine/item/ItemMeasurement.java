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

import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.IterationOverlay.IterationPrequel;
import nl.tytech.data.engine.item.WaterOverlay.WaterKey;
import nl.tytech.data.engine.item.WaterOverlay.WaterResult;
import nl.tytech.locale.unit.UnitType;

/**
 *
 * @author Frank Baars
 */
public class ItemMeasurement extends PointMeasurement {

    private static final long serialVersionUID = -306736041553174853L;

    public static final String getAttribute(Overlay overlay, GridOverlay.Key key) {

        if (overlay instanceof WaterOverlay wo) {
            return wo.getKeyOrDefault(key);
        }
        if (overlay instanceof ResultChildOverlay && overlay.getParent() instanceof WaterOverlay wo) {
            return wo.getKeyOrDefault(key);
        }
        if (overlay instanceof IterationOverlay io) {
            return getAttribute(io.getPrequel(IterationPrequel.ITERATION), key);
        }
        return null;
    }

    public static final GridOverlay.Key getKey(Overlay overlay, String key) {

        GridOverlay.Key[] keys = new GridOverlay.Key[0];
        if (overlay instanceof GridOverlay go) {
            if (go.getResultType() instanceof WaterResult) {
                keys = WaterKey.values();
            }
            if (go instanceof IterationOverlay io) {
                GridOverlay<?, ?> prequel = io.getPrequel(IterationPrequel.ITERATION);
                if (prequel != null && prequel.getResultType() instanceof WaterResult) {
                    keys = WaterKey.values();
                }
            }
        }

        for (GridOverlay.Key k : keys) {
            if (k.name().equalsIgnoreCase(key)) {
                return k;
            }
        }
        return null;
    }

    @XMLValue
    private Integer itemID = Item.NONE;

    @XMLValue
    private MapLink mapLink = null;

    @XMLValue
    private String key = WaterKey.OBJECT_FLOW_OUTPUT.name(); // note: FLOW is only used attribute before September 2022

    public ItemMeasurement() {

    }

    public ItemMeasurement(Integer overlayID, MapLink mapLink, Integer itemID, String key, String name, Point point, boolean save) {
        super(new Integer[] { overlayID }, name, point, save, false);
        this.mapLink = mapLink;
        this.itemID = itemID;
        this.key = key;
    }

    public String getAttribute() {
        return getKey() != null ? getAttribute(getOverlay(), getKey()) : null;
    }

    public Item getAttributeItem() {
        return getItem(getMapLink(), getItemID());
    }

    public Integer getItemID() {
        return itemID;
    }

    public GridOverlay.Key getKey() {
        return getKey(getOverlay(), key);
    }

    public MapLink getMapLink() {
        return mapLink;
    }

    @Override
    public UnitType getUnitType() {
        return getKey() != null ? getKey().getUnitType() : UnitType.NONE;
    }

    public float[] getValues(MapType mapType) {
        return getOverlayValues(mapType, getOverlayID());
    }
}
