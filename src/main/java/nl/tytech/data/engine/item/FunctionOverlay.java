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
import nl.tytech.util.color.TColor;

/**
 * Special overlay that can highlight functions
 *
 * @author Maxim Knepfle
 */
public class FunctionOverlay extends Overlay {

    private static final long serialVersionUID = 3137271350131025071L;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private ArrayList<Integer> functionIDs = new ArrayList<>();

    @XMLValue
    private TColor functionsColor = TColor.RED;

    @XMLValue
    private TColor remainderColor = TColor.WHITE;

    public List<Integer> getFunctionIDs() {
        return functionIDs;
    }

    public List<Function> getFunctions() {
        return this.getItems(MapLink.FUNCTIONS, getFunctionIDs());
    }

    public TColor getFunctionsColor() {
        return functionsColor;
    }

    public TColor getRemainderColor() {
        return remainderColor;
    }

    public void setFunctionsColor(TColor functionsColor) {
        this.functionsColor = functionsColor;
    }

    public void setRemainderColor(TColor restColor) {
        this.remainderColor = restColor;
    }

}
