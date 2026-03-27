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

import java.util.Collection;
import java.util.HashMap;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Source;
import nl.tytech.util.color.TColor;

/**
 * Special overlay that can highlight (geo) sources
 *
 * @author Maxim Knepfle
 */
public class SourceOverlay extends Overlay {

    private static final long serialVersionUID = 3137271350161025071L;

    @XMLValue
    private HashMap<Integer, Integer> sources = new HashMap<>();

    @XMLValue
    private TColor restColor = TColor.WHITE;

    public boolean addSource(Integer sourceID, TColor color) {

        if (sourceID != null && color != null && !this.sources.containsKey(sourceID)) {
            sources.put(sourceID, color.getARGB());
            return true;
        }
        return false;
    }

    public TColor getRestColor() {
        return restColor;
    }

    public TColor getSourceColor(Integer id) {

        Integer color = sources.get(id);
        return color == null ? TColor.RED : new TColor(color);
    }

    public Collection<Integer> getSourceIDs() {
        return sources.keySet();
    }

    public Collection<Source> getSources() {
        return getItems(MapLink.SOURCES, getSourceIDs().stream().toList());
    }

    public boolean removeSource(Integer sourceID) {

        if (sourceID != null) {
            return sources.remove(sourceID) != null;
        }
        return false;
    }
}
