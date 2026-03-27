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

import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.util.StringUtils;

/**
 * @author Maxim Knepfle
 */
public class MoneyTransfer extends UniqueNamedItem implements ActiveItem {

    public enum Type {
        SUBSIDY, LAND, TRANSFER
    }

    private static final long serialVersionUID = -7075877109212624952L;

    @XMLValue
    private double amount = 0;

    @XMLValue
    private Type type = Type.TRANSFER;

    @ItemIDField(MapLink.STAKEHOLDERS)
    @XMLValue
    private Integer senderID = NONE;

    @ItemIDField(MapLink.STAKEHOLDERS)
    @XMLValue
    private Integer receiverID = NONE;

    @XMLValue
    private String moneyMessageDescription = StringUtils.EMPTY;

    @XMLValue
    private boolean active = false;

    public MoneyTransfer() {
    }

    public MoneyTransfer(Type type, Integer senderID, Integer receiverID, String name, double amount) {
        this.type = type;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
        this.setName(name);
    }

    /**
     * @return the amount
     */
    public final double getAmount() {
        return this.amount;
    }

    public String getMoneyTransferDescription() {
        return moneyMessageDescription;
    }

    public Stakeholder getReceiver() {
        return this.getItem(MapLink.STAKEHOLDERS, receiverID);
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public Stakeholder getSender() {
        return this.getItem(MapLink.STAKEHOLDERS, senderID);
    }

    public Integer getSenderID() {
        return senderID;
    }

    public Type getType() {
        return type;
    }

    /**
     * @return the active
     */
    @Override
    public final boolean isActive() {
        return this.active;
    }

    /**
     * @return the active
     */
    public final String isActiveString() {

        if (this.isActive()) {
            return this.getWord(MapLink.CLIENT_WORDS, ClientTerms.SUBSIDY_GRANTED);
        }
        return this.getWord(MapLink.CLIENT_WORDS, ClientTerms.SUBSIDY_NOT_GRANTED);
    }

    /**
     * @param active the active to set
     */
    public final void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getName();
    }
}
