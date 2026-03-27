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

import nl.tytech.data.core.item.Item;

/**
 * DeletedItem: When an item is deleted this object is sent to the clients to let them know something is deleted. It also has a version just
 * like the normal items.
 *
 * @author Maxim Knepfle
 */
public class DeletedItem<T extends Item> extends Item {

    /**
     * Map reset ID
     */
    public static final Integer MAP_RESET = -1;

    private static final long serialVersionUID = 1669501789391939169L;

    /**
     * Used for total map reset
     */
    public DeletedItem() {
        this.setId(MAP_RESET);
    }

    public DeletedItem(final T item) {
        this.setId(item.getID());
    }

    @Override
    public String toString() {
        return this.getID().toString();
    }
}
