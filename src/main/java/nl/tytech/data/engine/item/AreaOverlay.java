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
import java.util.HashSet;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.color.TColor;

/**
 * Overlay that can show specified areas
 *
 * @author Frank Baars
 */
public class AreaOverlay extends Overlay {

    private static final long serialVersionUID = 2278041675288910922L;

    @XMLValue
    @ItemIDField(MapLink.AREAS)
    private ArrayList<Integer> areaIDs = new ArrayList<>();

    @XMLValue
    private TColor remainderColor = TColor.WHITE;

    @JsonIgnore
    private transient HashSet<Integer> areaIDCache = null;

    public boolean addArea(Integer areaID) {

        if (getCache().add(areaID)) {
            areaIDs = new ArrayList<>(getCache());
            return true;
        } else {
            return false;
        }
    }

    public void clearAreas() {

        areaIDCache = null;
        areaIDs.clear();
    }

    public boolean containsArea(Integer areaID) {
        return getCache().contains(areaID);
    }

    public List<Area> getAreas() {
        return this.getItems(MapLink.AREAS, areaIDs);
    }

    private final HashSet<Integer> getCache() {

        if (areaIDCache == null) {
            areaIDCache = new HashSet<>(areaIDs);
        }
        return areaIDCache;
    }

    public TColor getRemainderColor() {
        return remainderColor;
    }

    public boolean removeArea(Integer areaID) {

        if (getCache().remove(areaID)) {
            areaIDs = new ArrayList<>(getCache());
            return true;
        } else {
            return false;
        }
    }

    public void setRemainderColor(TColor restColor) {
        this.remainderColor = restColor;
    }
}
