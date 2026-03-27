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

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.TerrainType;
import nl.tytech.util.StringUtils;

/**
 * @author Frank Baars
 */
public class TerrainGeoLink extends GeoLink {

    private static final long serialVersionUID = 3227809616030984811L;

    @XMLValue
    @ItemIDField(MapLink.TERRAIN_TYPES)
    private Integer terrainTypeID;

    @Override
    public Stakeholder.Type getDefaultStakeholderType() {
        /**
         * Empty space is almost certainly owned by the municipality (both water and land).
         */
        return Stakeholder.Type.MUNICIPALITY;
    }

    @Override
    public String getName() {
        return StringUtils.EMPTY + getTerrainType();
    }

    public TerrainType getTerrainType() {
        return getItem(MapLink.TERRAIN_TYPES, terrainTypeID);
    }

    public Integer getTerrainTypeID() {
        return terrainTypeID;
    }

    @Override
    public boolean isWater() {
        return getTerrainType().isWater();
    }

    public void setTerrainTypeID(Integer terrainTypeID) {
        this.terrainTypeID = terrainTypeID;
    }

    @Override
    public String toString() {
        return this.getPriority() + ") " + getName();
    }
}
