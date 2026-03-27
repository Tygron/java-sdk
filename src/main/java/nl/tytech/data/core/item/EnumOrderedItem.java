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
package nl.tytech.data.core.item;

/**
 * EnumOrderedItem: This Item is has a enum as key/location in the map/list.
 *
 * @author Maxim Knepfle
 */
public abstract class EnumOrderedItem<E extends Enum<E>> extends Item {

    private static final long serialVersionUID = 1596022551151222427L;

    /**
     * Get the enum keys associated with this item.
     */
    public abstract E[] getEnumValues();

    /**
     * Returns the enum (type) of this item.
     */
    public final E getType() {

        // safety check
        if (this.getID().equals(Item.NONE)) {
            return null;
        }
        return this.getEnumValues()[this.getID()];
    }

    @Override
    public String toString() {

        if (this.getID() > Item.NONE && this.getID() < this.getEnumValues().length) {
            return this.getEnumValues()[this.getID()].toString();
        }
        return "Unknown type [ID: " + getID() + "]";
    }
}
