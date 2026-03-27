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
package nl.tytech.core.net;

import java.util.function.Predicate;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Geometry;
import nl.tytech.core.event.Event;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.PowerShare.PoolGroup;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.core.serializable.SimState;

/**
 * Lord
 *
 * Object implementing Lord interface must provide access to the other controller lists.
 *
 * @author Maxim Knepfle
 */
public interface Lord extends PoolGroup {

    public MapType getDefaultMap();

    public <I extends Item> Stream<I> getItems(final MapLink mapLink, Geometry geometry);

    public <I extends Item> Stream<I> getItems(MapLink mapLink, Geometry g, Predicate<I> predicate);

    public <I extends Item> ItemMap<I> getMap(final MapLink mapLink);

    public double getMinCellM();

    public Integer getSessionID();

    public Network.SessionType getSessionType();

    public long getSimTimeMillis();

    public SimState getState();

    public long getTotalMaxGridCells();

    public boolean isServerSide();

    @Override
    public boolean isShutdown();

    public String validateEventContents(Event event);

}
