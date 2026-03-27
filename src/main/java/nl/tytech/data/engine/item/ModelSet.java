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
import nl.tytech.data.core.item.Item;

/**
 *
 * A collection of 3D models that belong together
 *
 * @author Maxim Knepfle
 *
 */
public class ModelSet extends Item {

    private static final long serialVersionUID = 5713247932428755794L;

    public static final Integer SOLAR_PANELS_ID = 147;

    /**
     * Aligned to largest side of building, fixed order
     */
    public static final float ANGLE_ALIGNED = -1;

    /**
     * Aligned to largest side of building, but randomly placed
     */
    public static final float ANGLE_ALIGNED_RANDOM = -2;

    /**
     * Random angle and placement.
     */
    public static final float ANGLE_FREE_RANDOM = -3;

    @XMLValue
    @ItemIDField(MapLink.MODEL_DATAS)
    private ArrayList<Integer> modelIDs = new ArrayList<Integer>();

    @XMLValue
    private String name = "0_new modelset";

    @XMLValue
    private double roofInset = 0;

    @XMLValue
    private float angle = ANGLE_FREE_RANDOM;

    public float getAngle() {
        return angle;
    }

    public List<Integer> getModelIDs() {
        return modelIDs;
    }

    public String getName() {
        return name;
    }

    public double getRoofInset() {
        return roofInset;
    }

    @Override
    public String toString() {
        return name;
    }
}
