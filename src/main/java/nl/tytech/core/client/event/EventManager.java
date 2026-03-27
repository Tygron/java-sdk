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
package nl.tytech.core.client.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.tytech.core.client.concurrent.UpdateManager;
import nl.tytech.core.client.event.OnEventThread.EventThread;
import nl.tytech.core.client.net.SessionConnection.ComEvent;
import nl.tytech.core.client.net.Status;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ClientItemMap;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.core.util.SettingsManager.RunMode;
import nl.tytech.core.util.WeakList;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.core.item.Word;
import nl.tytech.util.StringUtils;
import nl.tytech.util.concurrent.LocalThreadList;
import nl.tytech.util.logger.TLogger;

/**
 * This class is effectively a centralised ActionEvent redispatcher. It also catches and distributes all communication events.
 *
 * @author Jeroen Warmerdam & Maxim Knepfle
 */
public class EventManager {

    public interface EventExecuter {

        public void enqueue(Runnable runnable);
    }

    public enum ItemManipulationEventType implements EventTypeEnum {

        /**
         * List with the ID's of all deleted items. The first content (ServerUpdateEventType specific Enum) defines in which mapLink list
         * the items are deleted.
         */
        DELETE_ITEMS(MapLink.class, ArrayList.class);

        private List<Class<?>> classes;

        private ItemManipulationEventType(Class<?>... classes) {
            this.classes = Arrays.asList(classes);
        }

        @Override
        public boolean canBePredefined() {
            return false;
        }

        @Override
        public List<Class<?>> getClasses() {
            return classes;
        }

        @Override
        public Class<?> getResponseClass(Object[] args) {
            return null;
        }

        @Override
        public boolean isServerSide() {
            return false;
        }

    }

    private static final class SingletonHolder {

        private static final EventManager INSTANCE = new EventManager();
    }

    private static final Class<?>[] enhumClasses = new Class[] { Event.class, Enum.class };

    private static final Class<?>[] idClasses = new Class[] { Event.class, Integer.class };

    public static final void addEnumListener(EventIDListenerInterface listener, EventTypeEnum type, Enum<?> id) {
        SingletonHolder.INSTANCE._addEnumListener(listener, type, id);
    }

    public static final void addIDListener(EventIDListenerInterface listener, EventTypeEnum type, Integer id) {
        SingletonHolder.INSTANCE._addIDListener(listener, type, id);
    }

    public static final void addListener(EventListenerInterface listener, Class<? extends EventTypeEnum> clazz) {
        SingletonHolder.INSTANCE._addEventClassListener(listener, clazz);
    }

    public static final void addListener(EventListenerInterface listener, EventTypeEnum... types) {
        SingletonHolder.INSTANCE._addEventListeners(false, listener, types);
    }

    public static final void addPriorityListener(EventListenerInterface listener, EventTypeEnum... types) {
        SingletonHolder.INSTANCE._addEventListeners(true, listener, types);
    }

    public static final void fire(Event event) {
        SingletonHolder.INSTANCE.fireEvent(event);
    }

    public static final void fire(final EventTypeEnum type, final Object... contents) {
        SingletonHolder.INSTANCE.fireEvent(new Event(type, contents));
    }

    public static final void fire(final int connectionID, final EventTypeEnum type, final Object... contents) {
        SingletonHolder.INSTANCE.fireEvent(new ConnectionIDEvent(connectionID, type, contents));
    }

    public static final void fire(List<CodedEvent> events) {
        SingletonHolder.INSTANCE.fireCodedEvents(Item.NONE, events);
    }

    public static final Integer getActiveConnectionID() {
        return SingletonHolder.INSTANCE._getActiveConnectionID();
    }

    public static final Network.SessionType getActiveSessionType() {
        return SingletonHolder.INSTANCE._getActiveSessionType();
    }

    public static final Status getActiveStatus() {
        return SingletonHolder.INSTANCE._getActiveStatus();
    }

    public static final EventExecuter getFXExecutor() {
        return SingletonHolder.INSTANCE._getFXExecutor();
    }

    public static final <I extends EnumOrderedItem<G>, G extends Enum<G>> I getItem(Integer connectionID, MapLink mapLink, G id) {
        return SingletonHolder.INSTANCE._getItem(connectionID, mapLink, id);
    }

    public static final <I extends Item> I getItem(Integer connectionID, MapLink mapLink, Integer id) {
        return SingletonHolder.INSTANCE._getItem(connectionID, mapLink, id);
    }

    public static final <I extends EnumOrderedItem<G>, G extends Enum<G>> I getItem(MapLink mapLink, G id) {
        return SingletonHolder.INSTANCE._getItem(mapLink, id);
    }

    public static final <I extends Item> I getItem(MapLink mapLink, Integer id) {
        return SingletonHolder.INSTANCE._getItem(mapLink, id);
    }

    public static final <I extends UniqueNamedItem> I getItem(MapLink mapLink, String id) {
        return SingletonHolder.INSTANCE._getItem(mapLink, id);
    }

    public static final <I extends Item> ClientItemMap<I> getItemMap(Integer connectionID, MapLink mapLink) {
        return SingletonHolder.INSTANCE._getItemMap(connectionID, mapLink);
    }

    public static final <I extends Item> ClientItemMap<I> getItemMap(MapLink mapLink) {
        return SingletonHolder.INSTANCE._getItemMap(mapLink);
    }

    public static final long getSimTimeMillis() {
        return getSimTimeMillis(getActiveConnectionID());
    }

    public static final long getSimTimeMillis(Integer connectionID) {

        Status status = SingletonHolder.INSTANCE._getStatus(connectionID);
        return status != null ? status.getSimTimeMillis() : -1;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <W extends Enum<W>> String getTerm(W term, Object... args) {

        Word word = SingletonHolder.INSTANCE.<Word, W> _getItem(MapLink.CLIENT_WORDS, term);
        String answer = word != null ? word.getTranslation() : StringUtils.EMPTY;
        return args.length > 0 ? StringUtils.formatEnumString(answer, term, args) : answer;
    }

    public static final int getVersion(Integer connectionID, MapLink mapLink) {
        return SingletonHolder.INSTANCE._getVersion(connectionID, mapLink);
    }

    public static final int getVersion(MapLink mapLink) {
        return SingletonHolder.INSTANCE._getVersion(getActiveConnectionID(), mapLink);
    }

    public static final boolean isFirstUpdateFinished() {

        Status status = SingletonHolder.INSTANCE._getActiveStatus();
        return status != null ? status.isFirstUpdateFinished() : false;
    }

    public static final void removeAllListeners(Object potentialListener) {

        if (potentialListener instanceof EventListenerInterface el) {
            removeListener(el);
        }
        if (potentialListener instanceof EventIDListenerInterface il) {
            removeIDListener(il);
        }
    }

    public static final void removeIDListener(EventIDListenerInterface listener) {
        SingletonHolder.INSTANCE._removeEventIDListener(listener);
    }

    public static final void removeIDListener(EventIDListenerInterface listener, EventTypeEnum type) {
        SingletonHolder.INSTANCE._removeEventIDListener(listener, type);
    }

    public static final void removeListener(EventListenerInterface listener) {
        SingletonHolder.INSTANCE._removeEventListener(listener);
    }

    public static final void removeListener(EventListenerInterface listener, EventTypeEnum... type) {
        SingletonHolder.INSTANCE._removeEventListeners(listener, type);
    }

    public static final <T> T request(final EventTypeEnum eventType, Object... args) {
        return SingletonHolder.INSTANCE.<T> _requestInner(eventType, args);
    }

    public static final void reset() {
        SingletonHolder.INSTANCE._reset();
    }

    public static final void setActiveConnectionID(Integer connectionID) {
        if (connectionID != null) {
            SingletonHolder.INSTANCE._setActiveConnection(connectionID);
        }
    }

    public static final void setFXExecutor(EventExecuter executor, Thread thread) {
        SingletonHolder.INSTANCE._setFXExecutor(executor, thread);
    }

    public static final void setOpenGLExecutor(EventExecuter executor, Thread thread) {
        SingletonHolder.INSTANCE._setOpenGLExecutor(executor, thread);
    }

    public static final void setStatus(final Integer connectionID, final Status status) {
        SingletonHolder.INSTANCE._setStatus(connectionID, status);
    }

    private EventExecuter openglExecutor = runnable -> TLogger.warning("No OpenGL Event Executer is defined, Skip runnable!");

    private Thread openglThread = null;

    private EventExecuter fxExecutor = runnable -> TLogger.warning("No FX Executer is defined, Skip runnable!");

    private Thread fxThread = null;

    private final Map<Class<?>, EventThread> eventIDThreads = new HashMap<>();

    private final Map<Class<?>, EventThread> eventEnumThreads = new HashMap<>();

    private final Map<Integer, WeakList<EventListenerInterface>> listUpdateEventListenerList = new HashMap<>();

    private final Map<EventTypeEnum, Map<Integer, WeakList<EventIDListenerInterface>>> idEventList = new HashMap<>();

    private final Map<EventTypeEnum, Map<Enum<?>, WeakList<EventIDListenerInterface>>> enumEventList = new HashMap<>();

    private final HashMap<Integer, Status> statusMap = new HashMap<>();

    private final LocalThreadList<Entry<Enum<?>, WeakList<EventIDListenerInterface>>> tempEnumList = new LocalThreadList<>();

    private final LocalThreadList<Entry<Integer, WeakList<EventIDListenerInterface>>> tempIDList = new LocalThreadList<>();

    private final Map<Class<?>, EventThread> eventThreads = new HashMap<>();

    private final Map<EventTypeEnum, WeakList<EventListenerInterface>> listMap = new HashMap<>();

    private Integer activeConnectionID = Item.NONE;

    private EventManager() {
    }

    private final void _addEnumListener(EventIDListenerInterface listener, EventTypeEnum type, Enum<?> id) {

        synchronized (enumEventList) {
            Map<Enum<?>, WeakList<EventIDListenerInterface>> map = enumEventList.computeIfAbsent(type, t -> new HashMap<>());
            WeakList<EventIDListenerInterface> list = map.computeIfAbsent(id, i -> new WeakList<>());
            list.add(listener);
        }
    }

    private final void _addEventClassListener(EventListenerInterface listener, Class<? extends EventTypeEnum> clazz) {
        this._addEventListeners(false, listener, clazz.getEnumConstants());
    }

    private final void _addEventListeners(boolean priority, EventListenerInterface listener, EventTypeEnum... types) {
        for (EventTypeEnum type : types) {
            addEventListener(priority, listener, type);
        }
    }

    private final void _addIDListener(EventIDListenerInterface listener, EventTypeEnum type, Integer id) {

        synchronized (idEventList) {
            Map<Integer, WeakList<EventIDListenerInterface>> map = idEventList.computeIfAbsent(type, t -> new HashMap<>());
            WeakList<EventIDListenerInterface> list = map.computeIfAbsent(id, i -> new WeakList<>());
            list.add(listener);
        }
    }

    private final Integer _getActiveConnectionID() {
        return this.activeConnectionID;
    }

    private final Network.SessionType _getActiveSessionType() {
        Status activeStatus = _getActiveStatus();
        if (activeStatus == null) {
            TLogger.severe("Trying to access SessionType, however no status is available, please connect first.");
            return null;
        }
        return activeStatus.getSessionType();
    }

    private final Status _getActiveStatus() {
        return this._getStatus(this.activeConnectionID);
    }

    private final EventExecuter _getFXExecutor() {
        return this.fxExecutor;
    }

    private final <I extends EnumOrderedItem<G>, G extends Enum<G>> I _getItem(Integer connectionID, MapLink mapLink, G id) {

        ItemMap<I> items = _getItemMap(connectionID, mapLink);
        return items != null && id != null ? items.get(id) : null;
    }

    private final <I extends Item> I _getItem(Integer connectionID, MapLink mapLink, Integer id) {

        ItemMap<I> items = _getItemMap(connectionID, mapLink);
        return items != null && id != null ? items.get(id) : null;
    }

    private final <I extends EnumOrderedItem<G>, G extends Enum<G>> I _getItem(MapLink mapLink, G id) {
        return _getItem(this.activeConnectionID, mapLink, id);
    }

    private final <I extends Item> I _getItem(MapLink mapLink, Integer id) {
        return _getItem(this.activeConnectionID, mapLink, id);
    }

    private final <I extends UniqueNamedItem> I _getItem(MapLink mapLink, String uniqueName) {

        if (!StringUtils.containsData(uniqueName)) {
            return null;
        }
        ItemMap<I> requestMap = _getItemMap(mapLink);
        if (requestMap == null) {
            return null;
        }
        return requestMap.stream().filter(i -> i instanceof UniqueNamedItem && uniqueName.equals(i.getName())).findAny().orElse(null);
    }

    private final <I extends Item> ClientItemMap<I> _getItemMap(Integer connectionID, MapLink mapLink) {

        Status status = _getStatus(connectionID);
        return status != null ? status.getMap(mapLink) : null;
    }

    private final <I extends Item> ClientItemMap<I> _getItemMap(MapLink mapLink) {
        return _getItemMap(this.activeConnectionID, mapLink);
    }

    private final Status _getStatus(Integer connectionID) {
        return this.statusMap.get(connectionID);
    }

    private int _getVersion(Integer connectionID, MapLink mapLink) {

        Status status = _getStatus(connectionID);
        return status != null ? status.getVersion(mapLink) : 0;
    }

    private final void _removeEventIDListener(EventIDListenerInterface listener) {

        synchronized (enumEventList) {
            for (Map<Enum<?>, WeakList<EventIDListenerInterface>> map : enumEventList.values()) {
                for (WeakList<EventIDListenerInterface> list : map.values()) {
                    list.remove(listener);
                }
            }
        }
        synchronized (idEventList) {
            for (Map<Integer, WeakList<EventIDListenerInterface>> map : idEventList.values()) {
                for (WeakList<EventIDListenerInterface> list : map.values()) {
                    list.remove(listener);
                }
            }
        }
    }

    private final void _removeEventIDListener(EventIDListenerInterface listener, EventTypeEnum type) {

        synchronized (enumEventList) {
            Map<Enum<?>, WeakList<EventIDListenerInterface>> enummap = enumEventList.get(type);
            if (enummap != null) {
                for (WeakList<EventIDListenerInterface> list : enummap.values()) {
                    list.remove(listener);
                }
            }
        }
        synchronized (idEventList) {
            Map<Integer, WeakList<EventIDListenerInterface>> idmap = idEventList.get(type);
            if (idmap != null) {
                for (WeakList<EventIDListenerInterface> list : idmap.values()) {
                    list.remove(listener);
                }
            }
        }
    }

    /**
     * Remove a generic event listener
     */
    private final void _removeEventListener(EventListenerInterface listener) {

        synchronized (listMap) {
            // remove also from all type-only listeners
            for (EventTypeEnum type : new ArrayList<EventTypeEnum>(listMap.keySet())) {
                _removeEventListener(listener, type);
            }
        }
    }

    /**
     * Remove an event listener for this specific type if it has been added in the past
     */
    private final void _removeEventListener(EventListenerInterface listener, EventTypeEnum type) {

        synchronized (listMap) {
            WeakList<EventListenerInterface> list = listMap.get(type);
            if (list == null) {
                TLogger.warning("Attempting to remove listener from non existing type");
                return;
            }
            list.remove(listener);
        }
    }

    /**
     * Remove an event listener for multiple types at once
     */
    private final void _removeEventListeners(EventListenerInterface listener, EventTypeEnum... types) {

        for (EventTypeEnum type : types) {
            _removeEventListener(listener, type);
        }
    }

    private final <T> T _requestInner(final EventTypeEnum eventType, Object... args) {

        Event event = new ClientResponseEvent(eventType, args);
        fireToNormalEvent(event);
        return event.getResponse();
    }

    private final void _reset() {

        this.listMap.clear();
        this.listUpdateEventListenerList.clear();
        this.statusMap.clear();
        this.activeConnectionID = Item.NONE;
    }

    private final void _setActiveConnection(Integer connectionID) {

        if (!this.activeConnectionID.equals(connectionID)) {
            TLogger.info("Changing active connection id from: " + this.activeConnectionID + " to " + connectionID);
            this.activeConnectionID = connectionID;
            Status status = _getStatus(this.activeConnectionID);
            EventManager.fire(ComEvent.MAPLINKS_INITIALIZED, status.getSessionType(), status.getProjectName(), status.getAppType(),
                    SettingsManager.getServerSessionID(this.activeConnectionID));
        }
    }

    private final void _setFXExecutor(EventExecuter executor, Thread thread) {

        this.fxExecutor = executor;
        this.fxThread = thread;
    }

    private final void _setOpenGLExecutor(EventExecuter executor, Thread thread) {

        this.openglExecutor = executor;
        this.openglThread = thread;
    }

    private final void _setStatus(final Integer connectionID, final Status status) {

        this.statusMap.put(connectionID, status);

        // when active connection is no set yet, use this one.
        if (Item.NONE.equals(this.activeConnectionID)) {
            activeConnectionID = connectionID;
        }
    }

    /**
     * Add an event listener for this specific type
     */
    private final void addEventListener(boolean priority, EventListenerInterface listener, EventTypeEnum type) {

        if (type.isServerSide()) {
            if (SettingsManager.getRunMode() == RunMode.RELEASE) {
                TLogger.severe("Event " + type.getClass().getSimpleName() + " " + type
                        + " is a server side event, the client should not listen to this!");
            } else {
                Thread.dumpStack();
                TLogger.showstopper("Event " + type.getClass().getSimpleName() + " " + type
                        + " is a server side event, the client should not listen to this!");
            }
            return;
        }

        synchronized (listMap) {
            WeakList<EventListenerInterface> list = listMap.computeIfAbsent(type, t -> new WeakList<>());

            // prio means put me first on the list
            if (priority) {
                WeakList<EventListenerInterface> newList = new WeakList<>();
                newList.add(listener);
                for (EventListenerInterface old : list.getList()) {
                    newList.add(old);
                }
                listMap.put(type, newList);
            } else {
                list.add(listener);
            }
        }
    }

    private final void fireCodedEvents(Integer stakeholderID, List<CodedEvent> events) {

        if (events == null) {
            return;
        }

        try {
            for (CodedEvent codedEvent : events) {
                List<Object> event = codedEvent.getParameters();

                if (event.size() > 0) {
                    EventTypeEnum ete = null;
                    if (event.getFirst() instanceof EventTypeEnum e) {
                        ete = e;

                    } else if (event.getFirst() instanceof String txt) {
                        String[] classAndValue = txt.split(StringUtils.WHITESPACE);
                        Class<?> c = Class.forName(classAndValue[0]);
                        for (Object value : c.getEnumConstants()) {
                            if (value.toString().equals(classAndValue[1])) {
                                ete = (EventTypeEnum) value;
                                break;
                            }
                        }
                    } else {
                        TLogger.severe("Missing Event Type.");
                        continue;
                    }

                    if (ete != null) {
                        Object[] contents = new Object[event.size() - 1];
                        for (int i = 1; i < event.size(); i++) {
                            contents[i - 1] = event.get(i);
                        }

                        if (!Item.NONE.equals(stakeholderID)) {
                            contents[0] = stakeholderID;
                        }

                        EventManager.fire(ete, contents);
                    }
                }
            }
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
    }

    private final void fireEvent(final Event event) {

        if (event.getType().isServerSide()) {
            if (SettingsManager.getRunMode() == RunMode.RELEASE) {
                TLogger.severe("Cannot fire server event: " + event.getClass().getSimpleName() + "." + event.getType()
                        + ", use CommunicationManager.fireServerEvent() instead");
            } else {
                TLogger.showstopper("Cannot fire server event: " + event.getClass().getSimpleName() + "." + event.getType()
                        + ", use CommunicationManager.fireServerEvent() instead");
            }
        }

        String error = EventValidationUtils.validateEventClasses(event);
        if (StringUtils.containsData(error)) {
            if (SettingsManager.getRunMode() != RunMode.RELEASE) {
                TLogger.showstopper("ClientEvent failure: " + error);
            } else {
                TLogger.severe("ClientEvent failure: " + error);
            }
            return;
        }

        fireToNormalEvent(event);
        fireToIDEvent(event);
        fireToEnumEvent(event);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private final void fireToEnumEvent(Event event) {

        Map<Enum<?>, WeakList<EventIDListenerInterface>> enumMap = enumEventList.get(event.getType());
        if (enumMap == null) {
            return;
        }
        List<Entry<Enum<?>, WeakList<EventIDListenerInterface>>> temp = tempEnumList.get();
        temp.addAll(enumMap.entrySet());

        ItemMap<Item> allItems = event.<ItemMap<Item>> getContent(0);
        Collection<Item> updatedItems = event.<Collection<Item>> getContent(1);
        for (Entry<Enum<?>, WeakList<EventIDListenerInterface>> entry : temp) {
            Enum id = entry.getKey();
            Item item = allItems.get(id);
            if (updatedItems.contains(item)) {
                for (EventIDListenerInterface listener : entry.getValue().getList()) {
                    EventThread threadType = getEventThread(listener, true);
                    if (isOnEventThread(threadType)) {
                        listener.notifyEnumListener(event, id);
                    } else {
                        threadPusher(threadType, () -> listener.notifyEnumListener(event, id));
                    }
                }
                // Do not return here,might be more enums in this list e.g. in settings!
            }
        }
    }

    private final void fireToIDEvent(Event event) {

        Map<Integer, WeakList<EventIDListenerInterface>> idMap = idEventList.get(event.getType());
        if (idMap == null) {
            return;
        }
        List<Entry<Integer, WeakList<EventIDListenerInterface>>> temp = tempIDList.get();
        temp.addAll(idMap.entrySet());

        ItemMap<Item> allItems = event.<ItemMap<Item>> getContent(0);
        Collection<Item> updatedItems = event.<Collection<Item>> getContent(1);
        for (Entry<Integer, WeakList<EventIDListenerInterface>> entry : temp) {
            Integer id = entry.getKey();
            Item item = allItems.get(id);
            if (updatedItems.contains(item)) {
                for (EventIDListenerInterface listener : entry.getValue().getList()) {
                    EventThread threadType = getEventThread(listener, false);
                    if (isOnEventThread(threadType)) {
                        listener.notifyIDListener(event, id);
                    } else {
                        threadPusher(threadType, () -> listener.notifyIDListener(event, id));
                    }
                }
                // Do not return here,might be more enums in this list e.g. in settings!
            }
        }
    }

    private final void fireToNormalEvent(final Event event) {

        // Now, check if we need to throw it to any specific listeners too
        WeakList<EventListenerInterface> list = listMap.get(event.getType());
        if (list == null) {
            return;
        }
        // Fire here too.
        for (EventListenerInterface listener : list.getList()) {
            try {
                EventThread threadType = event instanceof ClientResponseEvent ? EventThread.CALLER : getEventThread(listener);
                if (isOnEventThread(threadType)) {
                    listener.notifyListener(event);
                } else {
                    threadPusher(threadType, () -> listener.notifyListener(event));
                }
            } catch (Exception exp) {
                TLogger.exception(exp);
            }
        }
    }

    private final EventThread getEventThread(EventIDListenerInterface listener, boolean enhum) {

        Class<?> classz = listener.getClass();
        Map<Class<?>, EventThread> map = enhum ? this.eventEnumThreads : this.eventIDThreads;
        EventThread eventThread = map.get(classz);
        if (eventThread != null) {
            return eventThread;
        }

        try { // unknown, lets find out
            Method method = classz.getMethod(enhum ? "notifyEnumListener" : "notifyIDListener", enhum ? enhumClasses : idClasses);
            if (method.isAnnotationPresent(OnEventThread.class)) {
                eventThread = EventThread.valueOf(method.getAnnotation(OnEventThread.class).value());
            } else {
                eventThread = EventThread.PARALLEL;
            }
            map.put(classz, eventThread);
            return eventThread;
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    private final EventThread getEventThread(EventListenerInterface listener) {

        Class<?> classz = listener.getClass();
        EventThread eventThread = eventThreads.get(classz);
        if (eventThread != null) {
            return eventThread;
        }

        try { // unknown, lets find out
            Method method = classz.getMethod("notifyListener", Event.class);
            if (method.isAnnotationPresent(OnEventThread.class)) {
                eventThread = EventThread.valueOf(method.getAnnotation(OnEventThread.class).value());
            } else {
                eventThread = EventThread.PARALLEL;
            }
            this.eventThreads.put(classz, eventThread);
            return eventThread;
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    /**
     * Check if this Thread is requested Thread Type
     */
    private final boolean isOnEventThread(EventThread threadType) {

        switch (threadType) {
            case CALLER:
                return true;
            case PARALLEL:
                return Thread.currentThread() == UpdateManager.PARALLELTHREAD;
            case JAVAFX:
                return Thread.currentThread() == fxThread;
            case OPENGL:
                return Thread.currentThread() == openglThread;
        }
        return false;
    }

    /**
     * Push the runnable to the correct Thread
     */
    private final void threadPusher(EventThread threadType, final Runnable runnable) {

        switch (threadType) {
            case CALLER:
                TLogger.severe("Execute me direct, no need to push!");
                runnable.run();
                return;
            case PARALLEL:
                UpdateManager.exec(runnable);
                return;
            case JAVAFX:
                fxExecutor.enqueue(runnable);
                return;
            case OPENGL:
                openglExecutor.enqueue(runnable);
                return;
        }
    }

}
