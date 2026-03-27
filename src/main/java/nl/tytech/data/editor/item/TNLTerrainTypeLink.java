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
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;
import nl.tytech.data.engine.item.TerrainType;

/**
 * Top10NL Terrain type linkage
 *
 * @author Frank Baars
 */
public class TNLTerrainTypeLink extends TNLTerrainLink {

    private static final long serialVersionUID = -3030480178840758455L;

    @XMLValue
    @ItemIDField(MapLink.TERRAIN_TYPES)
    private Integer terrainTypeID = Item.NONE;

    @Override
    public Type getDefaultStakeholderType() {
        return Stakeholder.Type.MUNICIPALITY;
    }

    @Override
    public String getName() {
        return getTerrainType().getName();
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

    @Override
    public boolean mustContainHouses() {
        return false;
    }
}
