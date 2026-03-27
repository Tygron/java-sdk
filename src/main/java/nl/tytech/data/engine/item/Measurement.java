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
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.GeometryCollection;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.SourcedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.locale.unit.UnitType;

/**
 * Measurement: contains measured overlay data.
 *
 * @author Frank Baars
 */
public abstract class Measurement extends SourcedItem implements GeometryItem<GeometryCollection> {

    public enum MeasurementType {

        POINT, LINE, BLOCK;

        public static final MeasurementType[] VALUES = values();

    }

    private static final long serialVersionUID = 5410093499178446967L;

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private ArrayList<Integer> overlayIDs = new ArrayList<>();

    @XMLValue
    private boolean save = true;

    @XMLValue
    private boolean sum = true;

    public Measurement() {

    }

    public Measurement(Integer[] overlayIDs, String name, boolean save, boolean sum) {
        setName(name);
        this.overlayIDs.addAll(Arrays.asList(overlayIDs));
        this.save = save;
        this.sum = sum;
    }

    public void addOverlay(Integer overlayID) {
        if (!hasOverlay(overlayID)) {
            overlayIDs.add(overlayID);
        }
    }

    public boolean equalsOverlays(Integer[] newIDs) {
        List<Integer> uniques = new ArrayList<>();
        for (Integer baseOverlayID : newIDs) {
            if (!uniques.contains(baseOverlayID)) {
                uniques.add(baseOverlayID);
            }
        }

        if (uniques.size() != overlayIDs.size()) {
            return false;
        }
        for (int i = 0; i < overlayIDs.size(); i++) {
            if (!uniques.get(i).equals(overlayIDs.get(i))) {
                return false;
            }
        }
        return true;
    }

    public Overlay getOverlay() {
        return getItem(MapLink.OVERLAYS, getOverlayID());
    }

    public Integer getOverlayID() {
        return overlayIDs.isEmpty() ? Item.NONE : overlayIDs.get(0);
    }

    public List<Integer> getOverlayIDs() {
        return overlayIDs;
    }

    public abstract MeasurementType getType();

    public UnitType getUnitType() {
        return UnitType.NONE;
    }

    public boolean hasOverlappingKeys() {
        return false;
    }

    public boolean hasOverlay(Integer overlayID) {
        return indexOf(overlayID) != -1;
    }

    private int indexOf(Integer overlayID) {
        return overlayIDs.indexOf(overlayID);
    }

    public boolean isEditor() {
        return isSave() && getType() == MeasurementType.POINT;
    }

    public boolean isSave() {
        return save;
    }

    public boolean isSum() {
        return sum;
    }

    public void removeBaseOverlay(Integer overlayID) {
        overlayIDs.remove(overlayID);
        removeOverlayData(overlayID);
    }

    protected abstract void removeOverlayData(Integer overlay);

    public void setOverlayID(Integer overlayID) {
        if (overlayIDs.isEmpty()) {
            overlayIDs.add(overlayID);
        } else {
            overlayIDs.set(0, overlayID);
        }
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public void setSum(boolean sum) {
        this.sum = sum;
    }

    public abstract void updateMinMax(MapType mapType);
}
