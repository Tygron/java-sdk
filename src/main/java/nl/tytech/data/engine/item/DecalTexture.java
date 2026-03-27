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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;

/**
 *
 * Decal texture e.g. window or door
 *
 * @author Maxim Knepfle
 *
 */
public class DecalTexture extends Item {

    private static final String DECAL_LOCATION = "Decal/";

    public static final float[] SOLAR_PANELS_DECAL = new float[] { 1.0f, 28, 0.1f, 1f, 1f, 0.1f };

    private static final long serialVersionUID = 5713247932428755798L;

    @XMLValue
    private String name = "";

    @XMLValue
    private String texture = "";

    @XMLValue
    private float[] coordinates = new float[4];

    public float[] getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public String getTextureLocation() {
        return DECAL_LOCATION + texture;
    }

    @Override
    public String toString() {
        return name;
    }
}
