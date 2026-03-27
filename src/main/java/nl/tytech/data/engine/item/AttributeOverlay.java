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

import java.util.function.Predicate;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Overlay that can show areas, zones, buildings based on specified attribute
 *
 * @author Frank Baars & Maxim Knepfle
 */
public class AttributeOverlay extends Overlay {

    public static final class AttributeOverlayPredicate implements Predicate<Item> {

        private final MapLink mapLink;
        private final String attribute;

        public AttributeOverlayPredicate(AttributeOverlay overlay) {
            this.mapLink = overlay.getMapLink();
            this.attribute = overlay.getAttribute();
        }

        @Override
        public boolean test(Item item) {

            switch (mapLink) {
                case AREAS:
                    Area area = (Area) item;
                    return area.isActive() && area.getAttribute(attribute) != 0.0;
                case BUILDINGS:
                    Building building = (Building) item;
                    return building.getAttribute(attribute) != 0.0;
                case NEIGHBORHOODS:
                    Neighborhood hood = (Neighborhood) item;
                    return hood.isActive() && hood.getAttribute(attribute) != 0.0;
                case ZONES:
                    Zone zone = (Zone) item;
                    return zone.getAttribute(attribute) != 0.0;
                case NET_LOADS:
                    NetLoad load = (NetLoad) item;
                    return load.getAttribute(attribute) != 0.0;
                case TERRAINS:
                    Terrain terrain = (Terrain) item;
                    TerrainType type = terrain.getType();
                    return type != null && type.getAttribute(attribute) != 0.0;
                default:
                    return false;
            }
        }
    }

    /**
    *
    */
    private static final long serialVersionUID = 2278041675288910921L;

    @XMLValue
    private String attribute = StringUtils.EMPTY;

    @XMLValue
    @NoDefaultText
    private String colorAttribute = null;

    @XMLValue
    private TColor restColor = null;

    @XMLValue
    private TColor attributeColor = TColor.RED;

    @XMLValue
    private MapLink mapLink = MapLink.AREAS;

    public String getAttribute() {
        return attribute;
    }

    public TColor getAttributeColor() {
        return attributeColor;
    }

    public String getColorAttribute() {
        return colorAttribute;
    }

    public MapLink getMapLink() {
        return mapLink;
    }

    public TColor getRestColor() {
        return restColor;
    }

    public void setAttribute(String areaAttribute) {
        this.attribute = areaAttribute;
    }

    public void setAttributeColor(TColor attributeColor) {
        this.attributeColor = attributeColor;
    }

    public void setColorAttribute(String colorAttribute) {
        this.colorAttribute = colorAttribute;
    }

    public void setMapLink(MapLink mapLink) {
        this.mapLink = mapLink;
    }

    public void setRestColor(TColor restColor) {
        this.restColor = restColor;
    }
}
