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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * IMWA building linkage
 *
 * @author Frank Baars
 */
public class IMWABuildingLink extends FunctionGeoLink {

    public enum IMWAType {

        CULVERTS("urn:aquo:Kunstwerktype:ID:3"),;

        private final String aquoName;

        private IMWAType(String aquoName) {
            this.aquoName = aquoName;
        }

        public String getAquoName() {
            return aquoName;
        }
    }

    public static final String IMWA_AQUO_NAME = "typeKunstw";

    private static final long serialVersionUID = -5983242175420379252L;

    @XMLValue
    private String aquoName = StringUtils.EMPTY;

    @XMLValue
    private double bufferWidth = 5d;

    @XMLValue
    private IMWAType type = IMWAType.CULVERTS;

    public IMWABuildingLink() {

    }

    public String getAquoName() {
        if (StringUtils.containsData(aquoName)) {
            return aquoName;
        }
        return type.aquoName;
    }

    public double getBufferWidth() {
        return bufferWidth;
    }

    public IMWAType getType() {
        return type;
    }
}
