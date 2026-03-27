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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import nl.tytech.core.client.net.SessionConnection;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;

/**
 * StreamingClientItemMap: Retrieves Items directly from server. Caches same Item request, cache always cleared on any update.
 *
 * @author Maxim Knepfle
 */
public class StreamingClientItemMap<I extends Item> extends ClientItemMap<I> {

    private final Map<Integer, I> cache = new ConcurrentHashMap<>();

    private final MapLink mapLink;

    private final SessionConnection connection;

    public StreamingClientItemMap(MapLink mapLink, SessionConnection connection) {
        this.mapLink = mapLink;
        this.connection = connection;
    }

    @Override
    public boolean add(I item) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean addAll(Collection<? extends I> arg) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void clear() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    @Override
    public boolean contains(Object value) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean containsAll(Collection<?> arg) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean containsKey(Integer id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    @SuppressWarnings("unchecked")
    public I get(Integer id) {

        if (id == null) {
            return null;
        }
        I item = cache.get(id);
        if (item == null) {
            Item[] array = connection.getItems(mapLink, id);
            item = array != null && array.length > 0 ? (I) array[0] : null;
            if (item != null) {
                cache.put(id, item);
            }
        }
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<I> getItems(List<Integer> requestIDs) {

        if (requestIDs == null) {
            return null;
        }
        List<I> items = requestIDs.stream().map(id -> cache.get(id)).filter(i -> i != null).toList();
        if (items.size() != requestIDs.size()) {
            Item[] array = connection.getItems(mapLink, requestIDs.toArray(new Integer[requestIDs.size()]));
            items = array != null ? (List<I>) Arrays.asList(array) : null;
            if (items != null) {
                for (I item : items) {
                    cache.put(item.getID(), item);
                }
            }
        }
        return items;
    }

    @Override
    public int getVersion() {
        Integer version = connection.getVersion(mapLink);
        return version == null ? -1 : version;
    }

    @Override
    public boolean isEmpty() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isLocal(Collection<Integer> requestIDs) {
        return requestIDs.stream().allMatch(id -> cache.containsKey(id));
    }

    @Override
    public Iterator<I> iterator() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public I put(Integer id, I item) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public I remove(Integer id) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean remove(Object item) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean removeAll(Collection<?> arg) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public Stream<I> stream() {
        return values().stream();
    }

    @Override
    public Object[] toArray() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public <T> T[] toArray(T[] arg) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public Collection<I> values() {
        return new ArrayList<>(); // empty
    }
}
