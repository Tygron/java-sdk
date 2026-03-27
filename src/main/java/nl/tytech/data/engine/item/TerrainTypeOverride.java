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
import nl.tytech.data.engine.item.TerrainType.TerrainAttribute;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Override values of a terrain
 *
 * @author Frank Baars
 */
public class TerrainTypeOverride extends AttributeItem {

    private static final long serialVersionUID = 7584336450411707178L;

    @XMLValue
    private String code = StringUtils.EMPTY;// default is NO code

    public TerrainTypeOverride() {

    }

    public String getCode() {
        return code;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return TerrainAttribute.VALUES;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (hasAttribute("WATER_EVAPORATION_FACTOR") && !hasAttribute(TerrainAttribute.WATER_TRANSPIRATION_FACTOR)) {
            setAttributeArray(TerrainAttribute.WATER_TRANSPIRATION_FACTOR, getAttributeArray("WATER_EVAPORATION_FACTOR"));
            this.removeAttribute("WATER_EVAPORATION_FACTOR", true);
            TLogger.warning(getName() + " Converted WATER_EVAPORATION_FACTOR -> " + TerrainAttribute.WATER_TRANSPIRATION_FACTOR);
        }
        return result;
    }
}
