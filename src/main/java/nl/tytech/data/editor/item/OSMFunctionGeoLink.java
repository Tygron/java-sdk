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
package nl.tytech.data.editor.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.editor.other.OSMGeoLink;
import nl.tytech.data.editor.serializable.GeoElementType;
import nl.tytech.data.editor.serializable.OSMLayer;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.util.StringUtils;

/**
 * @author Jurrian Hartveldt & Frank Baars
 */
public class OSMFunctionGeoLink extends FunctionGeoLink implements OSMGeoLink {

    private static final long serialVersionUID = -3323471193971434223L;

    @XMLValue
    private HashMap<OSMLayer, List<String>> subTypes = new HashMap<>();

    @XMLValue
    private GeoElementType elementType = GeoElementType.MULTIPOLYGON;

    @XMLValue
    private double defaultWidth = 10;

    @Override
    public double getDefaultWidth() {
        return defaultWidth;
    }

    @Override
    public Map<OSMLayer, List<String>> getSubTypes() {
        return subTypes;
    }

    @Override
    public boolean isForElement(GeoElementType type) {
        return elementType == type;
    }

    public boolean isInRegion(Region region) {
        Function function = getFunction();
        return function == null ? true : function.isInRegion(region);
    }

    public void setSubTypes(Map<OSMLayer, List<String>> subTypes) {
        this.subTypes = new HashMap<>(subTypes);
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY + this.getPriority() + "-OSM) " + getName();
    }

}
