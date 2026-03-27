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

import nl.tytech.data.editor.serializable.GeoLinkType;
import nl.tytech.data.engine.item.Stakeholder.Type;

/**
 *
 * @author Frank Baars
 *
 */
public class AreaGeoLink extends CustomGeoLink {

    /**
    *
    */
    private static final long serialVersionUID = 1868688041101692036L;

    public AreaGeoLink() {

    }

    public AreaGeoLink(String name) {
        setName(name);
    }

    @Override
    public Type getDefaultStakeholderType() {
        return null;
    }

    @Override
    public GeoLinkType getGeoLinkType() {
        return GeoLinkType.AREA;
    }

    @Override
    public boolean isWater() {
        return false;
    }

}
