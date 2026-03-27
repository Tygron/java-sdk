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

import java.util.stream.Stream;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;

/**
 * ClientItemMaps containing the item maps and their versions on the client side.
 *
 * @author Maxim Knepfle
 */
public final class ClientItemMaps {

    /**
     * ItemsMaps per mapLink
     */
    @SuppressWarnings("unchecked")
    private final ClientItemMap<? extends Item>[] maps = new ClientItemMap[MapLink.VALUES.length];

    /**
     * Version per MapLink
     */
    private final Integer[] versions = new Integer[MapLink.VALUES.length];

    /**
     * Get the ItemMap instance associated with the given MapLink.
     */
    @SuppressWarnings("unchecked")
    public final <I extends Item> ClientItemMap<I> get(MapLink mapLink) {
        return (ClientItemMap<I>) maps[mapLink.ordinal()];
    }

    /**
     * Get the version of the MapLink Map.
     */
    public final int getVersion(MapLink mapLink) {
        Integer version = versions[mapLink.ordinal()];
        return version != null ? version.intValue() : 0;
    }

    /**
     * Save the ItemMap using the MapLink.
     */
    public final <I extends Item> void put(MapLink mapLink, ClientItemMap<I> map) {
        put(mapLink, map, 0);
    }

    public final <I extends Item> void put(MapLink mapLink, ClientItemMap<I> map, int version) {

        // set version and map
        versions[mapLink.ordinal()] = version;
        maps[mapLink.ordinal()] = map;
    }

    /**
     * Set the version by MapLink
     */
    public final void setVersion(MapLink mapLink, int version) {
        versions[mapLink.ordinal()] = version;
    }

    public final Stream<ClientItemMap<? extends Item>> stream() {
        return Stream.of(maps).filter(m -> m != null);
    }
}
