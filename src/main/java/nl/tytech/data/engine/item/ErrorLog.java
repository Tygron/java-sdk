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
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Log of an (user/editor) error
 * @author Maxim Knepfle
 */
public class ErrorLog extends Item {

    public enum Type {

        EXCEL,

        DATA,

        REMOTE,

        TQL,

        GPU;

        @Override
        public String toString() {

            if (this == TQL) {
                return this.name() + " Query";
            } else if (this == GPU) {
                return this.name();
            } else {
                return StringUtils.capitalizeWithSpacedUnderScores(this);
            }
        }
    }

    private static final long serialVersionUID = -4253782310241563578L;

    @XMLValue
    private Integer itemID = Item.NONE;

    @XMLValue
    private MapLink mapLink = null;

    @XMLValue
    private long time = System.currentTimeMillis();

    @XMLValue
    private String message = StringUtils.EMPTY;

    @XMLValue
    private Type type;

    @XMLValue
    private boolean dismissed;

    public ErrorLog() {

    }

    public ErrorLog(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Item getContentItem() {

        if (mapLink == null || Item.NONE.equals(itemID)) {
            return null;
        }
        return this.getItem(mapLink, itemID);
    }

    public MapLink getMapLink() {
        return mapLink;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public Type getType() {
        return type;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    public void setItem(MapLink mapLink, Integer itemID) {
        this.mapLink = mapLink;
        this.itemID = itemID;
    }

    @Override
    public String toString() {
        return type + ": " + message;
    }
}
