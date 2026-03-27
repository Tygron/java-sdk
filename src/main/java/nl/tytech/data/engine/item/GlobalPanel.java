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
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;

/**
 * @author Frank Baars
 */
public class GlobalPanel extends Panel {

    private static final long serialVersionUID = 6150792888335566809L;

    private static final double DEFAULT_GLOBAL_WIDTH = 250;

    private static final double DEFAULT_GLOBAL_HEIGHT = 468;
    @XMLValue
    @ItemIDField(MapLink.GLOBALS)
    private ArrayList<Integer> globalIDs = new ArrayList<>();

    public GlobalPanel() {
        super(PanelType.GLOBAL_PANEL);
    }

    public List<Integer> getGlobalIDs() {
        return globalIDs;
    }

    public List<Global> getGlobals() {
        return this.getItems(MapLink.GLOBALS, getGlobalIDs());
    }

    @Override
    public double getHeight() {
        return height == null ? DEFAULT_GLOBAL_HEIGHT : height.doubleValue();
    }

    @Override
    public double getWidth() {
        return width == null ? DEFAULT_GLOBAL_WIDTH : width.doubleValue();
    }

}
