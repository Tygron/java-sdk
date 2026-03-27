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
import java.util.Map;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.Source.BaseSourceInterface;
import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.StringUtils;

/**
 * Base for W(M)S and W(C)S Service Overlays.
 *
 * @author Maxim Knepfle
 *
 */
public abstract class ServiceOverlay<R extends ResultType, P extends PrequelType> extends ResultParentOverlay<R, P>
        implements BaseSourceInterface {

    private static final long serialVersionUID = -7863651769006911316L;

    @XMLValue
    @ItemIDField(MapLink.SOURCES)
    private Integer sourceID = Item.NONE;

    @XMLValue
    @Deprecated
    private String layerName = null;

    @XMLValue
    private ArrayList<String> layerNames = new ArrayList<>();

    @XMLValue
    @NoDefaultText
    private String crs = StringUtils.EMPTY;

    @XMLValue
    private boolean forceXY = false;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return Math.max(1, layerNames.size());
    }

    public boolean equalsLayerNames(String[] layerNames) {

        if (layerNames.length != this.layerNames.size()) {
            return false;
        }
        for (int i = 0; i < layerNames.length; i++) {
            if (!this.layerNames.get(i).equals(layerNames[i])) {
                return false;
            }
        }
        return true;
    }

    public String getCrsName() {
        return crs;
    }

    public String getLayerName(int timeframe) {
        if (timeframe >= 0 && timeframe < layerNames.size()) {
            return layerNames.get(timeframe);
        }
        return StringUtils.EMPTY;
    }

    public List<String> getLayerNames() {
        return layerNames;
    }

    public Source getSource() {
        return getItem(MapLink.SOURCES, getSourceID());
    }

    public Integer getSourceID() {
        return sourceID;
    }

    @Override
    public List<Source> getSources() {
        return Arrays.asList(getSource());
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {
        return timeframe >= 0 && timeframe < layerNames.size() ? layerNames.get(timeframe) : "-";
    }

    public boolean isForceXY() {
        return forceXY;
    }

    public void setCrsName(String crsUrn) {
        this.crs = crsUrn;
    }

    public void setForceXY(boolean forceXY) {
        this.forceXY = forceXY;
    }

    public void setLayerNames(String[] layerNames) {
        this.layerNames.clear();
        for (int i = 0; i < layerNames.length; i++) {
            if (StringUtils.containsData(layerNames[i])) {
                this.layerNames.add(layerNames[i]);
            }
        }
    }

    public void setSourceID(Integer sourceID) {
        this.sourceID = sourceID;
    }

    public abstract boolean supports(GeoFormat format);

    @Override
    public String validated(boolean startSession) {

        if (layerName != null) {
            layerNames.add(layerName);
            layerName = null;
        }
        if (hasAttribute("FORCE_XY")) {
            this.forceXY = getAttribute("FORCE_XY") > 0;
            removeAttribute("FORCE_XY", true);
        }
        return super.validated(startSession);
    }
}
