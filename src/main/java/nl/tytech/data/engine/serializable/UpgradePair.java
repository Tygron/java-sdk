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
 * UpgradePair: Defines a pair of functions types can be upgraded. FROM (source) -> TO (target)
 *
 * @author Maxim Knepfle, Frank Baars
 */
public class UpgradePair implements Serializable {

    private static final long serialVersionUID = 4505099981435018509L;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer sourceFunctionID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer targetFunctionID = Item.NONE;

    public Integer getSourceFunctionID() {
        return sourceFunctionID;
    }

    public Integer getTargetFunctionID() {
        return targetFunctionID;
    }

    public void setSourceFunctionID(Integer id) {
        sourceFunctionID = id;
    }

    public void setTargetFunctionID(Integer id) {
        targetFunctionID = id;
    }

}
