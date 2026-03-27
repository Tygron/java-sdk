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
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * A special type used in the Tools and XML that replaces an Integer when that Integer is actually an Item ID. When ItemID is found in an
 * XML file the Tools will represent the ID as a special ItemIDField.
 *
 * Normal fields can use @ItemIDField, and should use this annotation, however events specified in the XML files can't use this annotation.
 * For those cases the ItemID type is used instead.
 *
 * ItemID is only used in the Tools and when loaded in the sessions. When an event is made the ItemID is replaced with the Integer
 * representing the ID.
 *
 * @author Alexander Hofstede & Marijn van Zanten
 */
public final class ItemID implements Serializable {

    private static final long serialVersionUID = 1265568704999624259L;

    private Integer id = Item.NONE;

    private MapLink mapLink = null;

    public ItemID() {

    }

    /**
     * Constructor, requires the linked Item ID and the controller of that Item
     *
     * @param id
     * @param mapLink
     */
    public ItemID(Integer id, MapLink mapLink) {
        this.mapLink = mapLink;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ItemID other = (ItemID) obj;
        if (!id.equals(other.id)) {
            return false;
        }
        if (mapLink == null) {
            if (other.mapLink != null) {
                return false;
            }
        } else if (!mapLink.equals(other.mapLink)) {
            return false;
        }
        return true;
    }

    /**
     * @return the item ID
     */
    public Integer getID() {
        return id;
    }

    /**
     * @return the MapLink associated with this itemID
     */
    public MapLink getMapLink() {
        return mapLink;
    }

    /**
     * @return the value that the Tools use to put in the XML files
     */
    public String getString() {
        return id + StringUtils.WHITESPACE + mapLink.name();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + (mapLink == null ? 0 : mapLink.hashCode());
        return result;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setMapLink(MapLink mapLink) {
        this.mapLink = mapLink;
    }

    @Override
    public String toString() {
        return "ItemID [ID=" + id + ", " + MapLink.class.getSimpleName() + "=" + mapLink + "]";
    }
}
