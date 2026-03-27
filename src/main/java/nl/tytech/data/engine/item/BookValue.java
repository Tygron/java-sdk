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
import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 * @author Maxim Knepfle
 */
public abstract class BookValue extends Item {

    public enum ChangeType {
        OVERRIDE, ADD, SUBSTRACT;
    }

    public interface Type {
        public ClientTerms getTranslationTerm();
    }

    private static final long serialVersionUID = -8755571740380078801L;

    @XMLValue
    private String name = "No Name";

    @XMLValue
    private double value = 0;

    @XMLValue
    private MapLink mapLink = null;

    @XMLValue
    private Integer linkID = Item.NONE;

    public BookValue() {
    }

    public BookValue(final MapLink mapLink, Integer linkID, final String name, final double value) {

        this.name = name;
        this.value = value;
        this.mapLink = mapLink;
        this.linkID = linkID;
    }

    public <T extends Item> T getContentItem() {
        return this.getItem(mapLink, linkID);
    }

    public Integer getContentLinkID() {
        return linkID;
    }

    public MapLink getContentMapLink() {
        return mapLink;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    public abstract Type getType();

    public String getTypeName() {
        return this.getWord(MapLink.CLIENT_WORDS, this.getType().getTranslationTerm());
    }

    public double getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getType() + " " + getName() + " " + getValue();
    }
}
