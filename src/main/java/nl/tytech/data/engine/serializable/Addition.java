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
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.ModelData;
import nl.tytech.data.engine.other.ModelObject;

/**
 * Model Addition, e.g. extra foliage on trees.
 *
 * @author Maxim Knepfle
 *
 */
public class Addition implements Serializable {

    private static final long serialVersionUID = -2844238108625416190L;

    @XMLValue
    private String name = "";

    @XMLValue
    private Show show = Show.FAR;

    @XMLValue
    private int amount = 1;

    @XMLValue
    private Double animMultiplier = null;

    @XMLValue
    private double randomScale = 0;

    public Addition() {

    }

    public int getAmount() {
        return amount;
    }

    public Double getAnimMultiplier() {
        return animMultiplier;
    }

    public String getFileName(ModelObject parent) {
        return ModelData.DETAILS_DIR + parent.getName().toLowerCase() + "_" + getName();
    }

    public String getName() {
        return name;
    }

    public double getRandomScale() {
        return randomScale;
    }

    public Show getShow() {
        return show;
    }
}
