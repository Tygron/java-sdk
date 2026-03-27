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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;

/**
 * Base map to store item objects
 *
 * @author Maxim Knepfle
 */
public abstract class AbstractItemMap<I extends Item> implements ItemMap<I> {

    private static final int START1 = 0;
    private static final int END1 = 1000;

    private static final int START2 = Item.SPECIFIC_START_ID;
    private static final int END2 = START2 + END1;

    private final List<I> cache1 = new ArrayList<>();
    private final List<I> cache2 = new ArrayList<>();

    // XXX: replace with it.unimi.dsi.fastutil.ints.Int2ObjectMap?
    protected final TreeMap<Integer, I> map;

    public AbstractItemMap() {
        map = new TreeMap<>();
    }

    public AbstractItemMap(TreeMap<Integer, I> oldMap) {

        // create tree
        map = new TreeMap<>(oldMap);

        // set initial cache
        for (Entry<Integer, I> entry : map.entrySet()) {
            cache(entry.getKey(), entry.getValue());
        }
    }

    private final void cache(int index, I item) {

        // cache 1: Regular items
        if (index >= START1 && index < END1) {
            cache(cache1, index - START1, item);
            return;
        }
        // cache 2: Specific items
        if (index >= START2 && index < END2) {
            cache(cache2, index - START2, item);
            return;
        }
    }

    private final void cache(List<I> cache, int index, I item) {

        // ensure size
        while (index >= cache.size()) {
            cache.add(null);
        }
        // set value
        cache.set(index, item);
    }

    @Override
    public boolean containsKey(Integer id) {
        return get(id) != null; // Note: Both Key and Value may never be NULL in Item Maps.
    }

    public void destroy() {

        map.clear();
        cache1.clear();
        cache2.clear();
    }

    @Override
    public I get(Integer id) {

        // check null ID
        if (id == null) {
            return null;
        }
        // check valid ID
        int index = id.intValue();
        if (index < 0) {
            return null;
        }
        // cache 1: Regular items
        if (index >= START1 && index < END1) {
            int i = index - START1;
            return cache1.size() > i ? cache1.get(i) : null;
        }
        // cache 2: Specific items
        if (index >= START2 && index < END2) {
            int i = index - START2;
            return cache2.size() > i ? cache2.get(i) : null;
        }
        // others
        return map.get(id);
    }

    @Override
    public <IE extends EnumOrderedItem<T>, T extends Enum<T>> List<I> getEnumItems(Collection<T> requestEnums) {

        // return null when requesting null
        if (requestEnums == null) {
            return null;
        }
        return requestEnums.stream().map(id -> get(id)).filter(i -> i != null).collect(Collectors.toList());
    }

    @Override
    public I getFirst() {
        return sequencedValues().getFirst();
    }

    @Override
    public List<I> getItems(List<Integer> requestIDs) {

        // return null when requesting null
        if (requestIDs == null) {
            return null;
        }

        // set with initial capacity and fill when not null
        List<I> items = new ArrayList<>(requestIDs.size());
        for (int i = 0; i < requestIDs.size(); i++) {
            I item = get(requestIDs.get(i));
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public Iterator<I> iterator() {
        return values().iterator();
    }

    public Set<Integer> keySet() {
        return map.keySet();
    }

    /**
     * Returns the last ID/key.
     */
    public final Integer lastExistingID() {
        return map.size() > 0 ? map.lastKey() : Item.NONE;
    }

    @Override
    public I put(Integer id, I item) {

        cache(id, item);
        return map.put(id, item);
    }

    @Override
    public I remove(Integer id) {

        cache(id, null);
        return map.remove(id);
    }

    @Override
    public SequencedCollection<I> sequencedValues() {
        return map.sequencedValues();
    }

    @Override
    public Stream<I> stream() {
        return values().stream();
    }

    public Object[] toArray() {
        return values().toArray();
    }

    public <T> T[] toArray(T[] typeArray) {
        return values().toArray(typeArray);
    }

    /**
     * Returns efficient immutable array list
     */
    @SuppressWarnings("unchecked")
    public List<I> toList() {
        return (List<I>) Arrays.asList(values().toArray(Item[]::new));
    }

    @Override
    public List<I> toList(int fromIndex) {
        return stream().filter(i -> i.getID().intValue() >= fromIndex).collect(Collectors.toList());
    }

    @Override
    public Collection<I> values() {
        return map.values();
    }
}
