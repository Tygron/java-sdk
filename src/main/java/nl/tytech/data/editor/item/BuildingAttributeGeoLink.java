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
import nl.tytech.data.engine.item.Stakeholder.Type;

/**
 * Building attribute linkage
 *
 * @author Frank Baars
 */
public class BuildingAttributeGeoLink extends GeoLink {

    private static final long serialVersionUID = -8856849051265817537L;

    @XMLValue
    private String attribute;

    @XMLValue
    private Double value = null;

    @XMLValue
    private HashMap<String, List<String>> featureAttributes = new HashMap<>();

    public String getAttribute() {
        return attribute;
    }

    public Map<String, List<String>> getAttributes() {
        return featureAttributes;
    }

    @Override
    public Type getDefaultStakeholderType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public boolean isWater() {
        return false;
    }

}
