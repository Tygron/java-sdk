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

import java.io.Serializable;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;

/**
 * Link to prequel with optional timeframe
 * @author Maxim Knepfle
 *
 */
public class PrequelLink implements Serializable {

    private static final long serialVersionUID = -1485647872999036439L;

    public static final Integer ALL = -2;

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private Integer overlayID = Item.NONE;

    @XMLValue
    private Integer timeframe = null;

    @XMLValue
    private boolean iteration = false;

    public PrequelLink() {

    }

    public PrequelLink(Integer overlayID, Integer timeframe, boolean previousIteration) {
        this.overlayID = overlayID;
        this.timeframe = timeframe;
        this.iteration = previousIteration;
    }

    public Integer getOverlayID() {
        return overlayID;
    }

    /**
     * Timeframe is optional maybe NULL
     */
    public Integer getTimeframe() {
        return timeframe;
    }

    public boolean isAllTimeframes() {
        return ALL.equals(timeframe);
    }

    public boolean isPreviousIteration() {
        return iteration;
    }

    @Override
    public String toString() {
        return "Link: " + overlayID + " " + iteration;
    }
}
