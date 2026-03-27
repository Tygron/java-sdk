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
package nl.tytech.core.client.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Geometry;
import nl.tytech.core.client.concurrent.ParallelUpdatable;
import nl.tytech.core.client.concurrent.UpdateManager;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.event.EventManager.ItemManipulationEventType;
import nl.tytech.core.client.net.SessionConnection.Updater;
import nl.tytech.core.event.Event;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.ConnectionEvent;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.serializable.DeletedItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.UpdateResult;
import nl.tytech.core.structure.ClientItemMap;
import nl.tytech.core.structure.ClientItemMaps;
import nl.tytech.core.structure.DataLord;
import nl.tytech.core.structure.StreamingClientItemMap;
import nl.tytech.core.util.PowerShare;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.core.serializable.SimState;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.util.logger.TLogger;

/**
 * Status is a lord containing a lists on the client side. Status fires events when new updates of lists are received. Status is Thread safe
 * using the Priority lock.
 *
 * @author Maxim Knepfle
 */
public class Status implements Lord, ParallelUpdatable {

    private static final float SIMTIME_UPDATE_PERIOD = 1;

    /**
     * ClientMap containing the local lists. These are identified with their Control class type.
     */
    protected ClientItemMaps maps;

    /**
     * Type of app (launcher, participant, editor).
     */
    protected Network.AppType appType;

    /**
     * Only do things when there is an update available.
     */
    private boolean updated = true;

    /**
     * First time fire always.
     */
    private boolean firstTime = true;

    /**
     * Status is allowed to fire events?
     */
    private volatile boolean fireEvents = true;

    /**
     * Local versions to be sent to the server for comparison. This is cached.
     */
    private HashMap<MapLink, Integer> versionRequest = null;

    /**
     * Status allows this sessionType. After new connection is made status is reset.
     */
    private Network.SessionType sessionType = null;

    /**
     * Current Simulation time variables.
     */
    private volatile Long simTimeMillis = 0l;

    private volatile float updateTime = 0;

    private volatile boolean simTimeActive = true;

    private volatile long lastTimeUpdate = System.currentTimeMillis();

    private volatile SimState state = SimState.NOTHING;

    private volatile long maxGridCells = 0;

    private volatile double minCellM = 0;

    private String projectName = null;

    private final SessionConnection connection;

    protected <I extends Item> Status(final Network.AppType argSubscription, SessionConnection aconnection) {

        appType = argSubscription;
        connection = aconnection;
        maps = new ClientItemMaps();
        UpdateManager.addParallel(this);
    }

    protected void deactivate() {
        fireEvents = false;
    }

    @Override
    public void exception(Throwable exp) {
        TLogger.exception(exp);
    }

    /**
     * Fire a list with the deleted ID's
     *
     * @param mapLink Type of list.
     * @param deleted items.
     */
    private <I extends Item> void fireListDeleteEvent(final MapLink mapLink, final I[] argDeletedList) {

        if (mapLink == null) {
            TLogger.severe("Cannot fire event for controller, no event enum constant is defined, maybe a server only control?");
            return;
        }

        // Retrieve the ID's and fire event, skip map reset item
        final List<Integer> deletedIDs = new ArrayList<>();
        for (Item item : argDeletedList) {
            if (item.getID().equals(DeletedItem.MAP_RESET)) {
                TLogger.info("Received a map reset for: " + mapLink);
            } else {
                deletedIDs.add(item.getID());
            }
        }
        if (deletedIDs.size() == 0) {
            return;
        }
        // fire using manager
        if (fireEvents) {
            EventManager.fire(connection.getID(), ItemManipulationEventType.DELETE_ITEMS, mapLink, deletedIDs);
        }
    }

    private <I extends Item> void fireListUpdateEvent(final MapLink type, final I[] argUpdatedList) {

        if (type == null) {
            TLogger.severe("Cannot fire event for controller no event enum constant is defined, maybe a server only control?");
            return;
        }

        final List<I> updatedList = argUpdatedList == null ? new ArrayList<>() : Arrays.asList(argUpdatedList);

        // fire using manager
        if (fireEvents) {
            EventManager.fire(connection.getID(), type, getMap(type), updatedList, firstTime);
        }
    }

    /**
     * @return the Subscription
     */
    public Network.AppType getAppType() {
        return appType;
    }

    @Override
    public MapType getDefaultMap() {

        if (this.sessionType == SessionType.EDITOR && this.getState() != SimState.TESTRUN) {
            return MapType.CURRENT;
        }
        return MapType.MAQUETTE;
    }

    @Override
    public <I extends Item> Stream<I> getItems(MapLink mapLink, Geometry geometry) {
        return this.<I> getMap(mapLink).stream();
    }

    @Override
    public <I extends Item> Stream<I> getItems(MapLink mapLink, Geometry g, Predicate<I> predicate) {

        // Filter with predicate and sort on ID
        return this.<I> getItems(mapLink, null).filter(i -> predicate == null || predicate.test(i)).sorted(Item.ID_SORT);
    }

    @Override
    public <I extends Item> ClientItemMap<I> getMap(MapLink type) {

        // check for null's
        if (type == null) {
            TLogger.severe("Status does not contain a item map named NULL.");
            return null;
        }

        ClientItemMap<I> map = maps.get(type);
        if (map == null) {
            if (!type.isValidForAppType(appType)) {
                map = appType == AppType.LAUNCHER ? new ClientItemMap<>() : new StreamingClientItemMap<>(type, connection);
                maps.put(type, map);
            } else {
                TLogger.warning("Status does not (yet) contain a item map named " + type.name() + ".");
            }
        }
        return map;
    }

    @Override
    public double getMinCellM() {
        return minCellM;
    }

    @Override
    public String getPoolGroupID() {
        return "ClientShare-" + getSessionID();
    }

    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public Integer getSessionID() {
        return SettingsManager.getServerSessionID(connection.getID());
    }

    @Override
    public Network.SessionType getSessionType() {
        if (sessionType == null) {
            TLogger.warning("Trying to access status SessionType, however no connection was made, please connect first.");
            return null;
        }
        return sessionType;
    }

    @Override
    public long getSimTimeMillis() {
        return simTimeMillis;
    }

    @Override
    public SimState getState() {
        return state;
    }

    @Override
    public long getTotalMaxGridCells() {
        return maxGridCells;
    }

    public int getVersion(MapLink mapLink) {
        return maps.getVersion(mapLink);
    }

    /**
     * ONLY the Updater thread may call this method! Get the version request with the total version and per list version.
     *
     * @return Version request.
     */
    protected final HashMap<MapLink, Integer> getVersionRequest() {

        if (!(Thread.currentThread() instanceof Updater)) {
            TLogger.severe("Only the updater thread may call this method!");
            return null;
        }

        if (updated && DataLord.getAppLinks(sessionType, appType).length > 0) {
            updated = false;
            versionRequest = new HashMap<>();
            MapLink[] mapLinks = DataLord.getAppLinks(sessionType, appType);
            for (int i = 0; i < mapLinks.length; i++) {
                versionRequest.put(mapLinks[i], maps.getVersion(mapLinks[i]));
            }
        }
        return versionRequest;
    }

    private void interpolateSimTime() {

        synchronized (Status.this) {
            // interpolate the old simtime.
            this.simTimeMillis += System.currentTimeMillis() - lastTimeUpdate;
            this.lastTimeUpdate = System.currentTimeMillis();
        }
    }

    public boolean isFirstUpdateFinished() {
        return !firstTime;
    }

    @Override
    public boolean isServerSide() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    private <I extends Item> void removeFromMap(final MapLink type, final I[] update) {

        if (update != null) {
            // create a new map to prevent concurrent modifications.
            ClientItemMap<Item> map = new ClientItemMap<>(maps.get(type));
            int version = 0;

            for (I item : update) {
                map.remove(item.getID());
                // version update
                if (version < item.getVersion()) {
                    version = item.getVersion();
                }
            }
            maps.put(type, map, version);
        }
    }

    /**
     * Session type is set from connection for this specific session.
     * @param sesionType
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void setSessionType(Network.SessionType sessionType, String projectName) {
        this.sessionType = sessionType;
        this.projectName = projectName;

        // read in maps.
        for (MapLink mapLink : DataLord.getAppLinks(sessionType, appType)) {
            this.maps.put(mapLink, new ClientItemMap());
        }
    }

    /**
     * Sync client simtime with servers simtime.
     * @param updateMoments
     */
    private void syncServerClientTime() {

        Moment moment = this.<Moment> getMap(MapLink.TIMES).get(Moment.CURRENT_POSTION);
        Moment start = this.<Moment> getMap(MapLink.TIMES).get(Moment.SIMULATION_START_POSTION);

        synchronized (Status.this) {
            this.simTimeMillis = moment.getMillis();
            this.lastTimeUpdate = System.currentTimeMillis();
            this.simTimeActive = moment.getMillis() > start.getMillis();
            this.updateTime = 0;
        }
    }

    private final void syncSettings() {

        Setting setting = this.<Setting> getMap(MapLink.SETTINGS).get(Setting.Type.STATE);
        state = setting.getEnumValue(SimState.class);

        setting = this.<Setting> getMap(MapLink.SETTINGS).get(Setting.Type.MAX_TOTAL_GRIDCELLS);
        maxGridCells = setting.getLongValue();

        setting = this.<Setting> getMap(MapLink.SETTINGS).get(Setting.Type.MIN_CELL_M);
        minCellM = setting.getDoubleValue();

    }

    /**
     * Update a specific list.
     *
     * @param type The list type to update.
     * @param update The updated items.
     */

    private void updateMap(final MapLink type, final Item[] update) {

        if (update != null) {
            // create a new map to prevent concurrent modifications.
            ClientItemMap<Item> map = new ClientItemMap<>(maps.get(type));
            int version = 0;
            for (Item item : update) {
                // add status
                item.setLord(this);
                map.put(item.getID(), item);

                // version update
                if (version < item.getVersion()) {
                    version = item.getVersion();
                }
            }
            maps.put(type, map, version);
        }
    }

    @Override
    public void updateParallel(float tpf) {

        updateTime += tpf;
        if (simTimeActive && updateTime > SIMTIME_UPDATE_PERIOD) {
            interpolateSimTime();
            updateTime = 0;
        }
    }

    /**
     * ONLY the UpdaterThread may call this method!
     *
     * @param <C>
     * @param <I>
     * @param serverVersion
     */
    @SuppressWarnings("unchecked")
    protected synchronized <I extends Item> void updateVersions(final UpdateResult serverVersion) {

        try {
            if (serverVersion != null) {
                updated = true;

                // dump cached objects
                maps.stream().forEach(m -> m.clearCache());

                // update maps
                for (Entry<String, Item[]> entry : serverVersion.getItems().entrySet()) {
                    updateMap(MapLink.valueOf(entry.getKey()), entry.getValue());
                }

                // deleted items
                for (Entry<String, Item[]> entry : serverVersion.getDeletes().entrySet()) {
                    removeFromMap(MapLink.valueOf(entry.getKey()), entry.getValue());
                }

                // set my stakeholder and time first!
                for (Entry<String, Item[]> entry : serverVersion.getItems().entrySet()) {
                    MapLink mapLink = MapLink.valueOf(entry.getKey());
                    if (mapLink.equals(MapLink.STAKEHOLDERS)) {
                        connection.setMyStakeholder(entry.getValue());
                    } else if (mapLink.equals(MapLink.TIMES)) {
                        syncServerClientTime();
                    } else if (mapLink.equals(MapLink.SETTINGS)) {
                        syncSettings();
                    }
                }

                UpdateManager.exec(() -> {
                    if (firstTime && fireEvents) {
                        EventManager.fire(connection.getID(), ConnectionEvent.FIRST_UPDATE_STARTED);
                    }

                    // fire events
                    MapLink[] mapLinks = DataLord.getAppLinks(sessionType, appType);
                    for (int i = 0; i < mapLinks.length; i++) {
                        MapLink type = mapLinks[i];
                        Item[] update = serverVersion.getItems().get(type.name());
                        Item[] deleted = serverVersion.getDeletes().get(type.name());

                        // fire event when updated and when items are deleted
                        if (firstTime || update != null || deleted != null) {
                            fireListUpdateEvent(type, (I[]) update);
                        }
                        // for deletes also fire deleted item event
                        if (deleted != null) {
                            fireListDeleteEvent(type, (I[]) deleted);
                        }
                    }

                    if (firstTime && fireEvents) {
                        EventManager.fire(connection.getID(), ConnectionEvent.FIRST_UPDATE_DONE);

                        PowerShare.execute(() -> {
                            EventManager.fire(connection.getID(), ConnectionEvent.FIRST_UPDATE_EVENT_HANDLED);
                        });
                    }

                    firstTime = false;
                });
            }
        } catch (RuntimeException e) {
            // this is run from a future, so there is no notification of runtime exceptions
            // this is added just to notify the developer something is wrong
            TLogger.exception(e);
            throw e;
        }
    }

    @Override
    public String validateEventContents(Event event) {
        // must always be true, cannot do full validation client-side due to missing maps.
        return null;
    }
}
