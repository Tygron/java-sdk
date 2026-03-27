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
import java.util.SequencedCollection;
import java.util.stream.Stream;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;

/**
 * ItemMap: Interface uniting the server and client item maps.
 *
 * @author Maxim Knepfle
 */
public interface ItemMap<I extends Item> extends Iterable<I> {

    public boolean containsKey(Integer id);

    /**
     * Get an item from the map using the ID
     *
     */
    public I get(Integer id);

    /**
     * Get an item from the map using the enum ordinal ID
     *
     */
    public <IE extends EnumOrderedItem<T>, T extends Enum<T>> I get(T e);

    public <IE extends EnumOrderedItem<T>, T extends Enum<T>> List<I> getEnumItems(Collection<T> requestEnums);

    public I getFirst();

    public List<I> getItems(List<Integer> requestIDs);

    /**
     * Overall version of map.
     */
    public int getVersion();

    /**
     * Put an Item in the map using the ID for key.
     */
    public I put(Integer id, I item);

    /**
     * Remove an item from the map using the ID as key.
     */
    public I remove(Integer id);

    /**
     * Returns an Sequenced Collection of the items contained in the map.
     */
    public SequencedCollection<I> sequencedValues();

    /**
     * The capacity size of the map.
     *
     * WARNING: this is the last item ID +1, the map does not guarantee that all ID's between 0 and size() are used!
     */
    public int size();

    public Stream<I> stream();

    public List<I> toList(int fromIndex);

    /**
     * Returns an Collection of the items contained in the map.
     */
    public Collection<I> values();
}
