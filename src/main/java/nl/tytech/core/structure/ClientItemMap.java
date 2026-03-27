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
package nl.tytech.core.structure;

import java.util.Collection;
import java.util.List;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.logger.TLogger;

/**
 * ClientItemMap containing the items and their versions on the client side.
 *
 * @author Maxim Knepfle
 */
public class ClientItemMap<I extends Item> extends AbstractItemMap<I> implements Collection<I> {

    /**
     * When this is an enum map it is based on this enum class type.
     */
    private Class<?> enumType = null;

    /**
     * When true this map uses enums ordinals to order the items
     */
    private Boolean enumOrdered = null;

    public ClientItemMap() {

    }

    public ClientItemMap(ClientItemMap<I> oldMap) {
        super(oldMap.map);
    }

    @Override
    public boolean add(I item) {
        return put(this.size(), item) != null;
    }

    @Override
    public boolean addAll(Collection<? extends I> arg) {

        for (I item : arg) {
            this.add(item);
        }
        return true;
    }

    @Override
    public void clear() {
        TLogger.severe("Cannot clear an ItemMap");
    }

    public void clearCache() {
        // ignored
    }

    @Override
    public boolean contains(Object value) {
        return map.containsValue(value);
    }

    @Override
    public boolean containsAll(Collection<?> arg) {
        return false;
    }

    /**
     * It is also possible to retrieve an item from the map by using a enum value. The ordinal is the key ord ID.
     *
     * @param enumerator
     * @return Item.
     */
    @Override
    public <TE extends EnumOrderedItem<G>, G extends Enum<G>> I get(G enumerator) {

        // get first item and check if this is an enum ordered item.
        if (enumOrdered == null) {
            for (I item : values()) {
                if (item != null) {
                    if (item instanceof EnumOrderedItem<?> eItem) {
                        enumType = eItem.getType().getClass();
                        enumOrdered = true;
                    } else {
                        enumOrdered = false;
                    }
                    break;
                }
            }
        }

        // map must be empty! or key is empty
        if (enumOrdered == null || enumerator == null) {
            return null;
        }

        // when not enum based, return
        if (!enumOrdered) {
            TLogger.severe("Trying to get an item based on the enum ordinal value, however map is not enum ordered.");
            return null;
        }

        // check if this is the correct enum class type.
        if (enumerator.getClass() != enumType) {
            TLogger.severe("Trying to get an item based on enum: " + enumerator.getClass().getSimpleName()
                    + ", however this does not match map enum type: " + enumType.getSimpleName() + ".");
            return null;
        }
        // okay get item
        return this.get(enumerator.ordinal());
    }

    public List<I> getItems(Collection<Integer> requestIDs) {
        return requestIDs != null ? getItems(requestIDs.stream().toList()) : null;
    }

    @Override
    public int getVersion() {
        return stream().mapToInt(i -> i.getVersion()).max().orElse(0);
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * When true data is stored on local Client otherwise needs to be requested from server (streaming).
     */
    public boolean isLocal() {
        return true; // always stored local
    }

    public boolean isLocal(Collection<Integer> requestIDs) {
        return true; // always stored local
    }

    @Override
    public boolean remove(Object item) {
        return item instanceof Item itemObject ? remove(itemObject.getID()) != null : false;
    }

    @Override
    public boolean removeAll(Collection<?> arg) {

        for (Object item : arg) {
            remove(item);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        TLogger.severe("Cannot retain all on an ItemMap");
        return false;
    }

    @Override
    public final int size() {

        // is 0
        if (map.size() == 0) {
            return 0;
        }
        // map can contain null's, lastkey is the size
        return map.lastKey() + 1;
    }
}
