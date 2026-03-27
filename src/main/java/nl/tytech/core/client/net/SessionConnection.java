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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.ClientConnectionState;
import nl.tytech.core.net.Network.ConnectionEvent;
import nl.tytech.core.net.Rest;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.event.InputException;
import nl.tytech.core.net.serializable.ClientData;
import nl.tytech.core.net.serializable.ClientData.ConnectionState;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.UpdateResult;
import nl.tytech.core.structure.DataLord;
import nl.tytech.core.util.ItemTypeResolverBuilder;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.core.util.SettingsManager.RunMode;
import nl.tytech.data.core.item.CoreStakeholder;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.Crs;
import nl.tytech.util.JsonMapper;
import nl.tytech.util.OSUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.RestManager;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.RestManager.TWebApplicationException;
import nl.tytech.util.StringUtils;
import nl.tytech.util.TExecutors;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.ZipUtils;
import nl.tytech.util.concurrent.ThreadPriorities;
import nl.tytech.util.logger.TLogger;

/**
 * Connection: Base Client-Server Session connection class.
 *
 * @author Maxim Knepfle
 */
public class SessionConnection {

    public enum ComEvent implements EventTypeEnum {

        /**
         * Please connect to this server using the settings from settingsmanager
         */
        DIRECT_CONNECT(),

        COMMUNICATION_STAKEHOLDER_SET(CoreStakeholder.class),

        MAPLINKS_INITIALIZED(Network.SessionType.class, String.class, AppType.class, Integer.class);

        private final List<Class<?>> classes = new ArrayList<>();

        private ComEvent(Class<?>... c) {
            Collections.addAll(classes, c);
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

    public enum Processing implements EventTypeEnum {

        START, DONE;

        private final List<Class<?>> classes = new ArrayList<>();

        private Processing(Class<?>... c) {

            Collections.addAll(classes, c);
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

    public class Updater extends Thread {

        private static final int UPDATE_TIMOUT = (int) (10 * Moment.MINUTE);

        private static final String UPDATE_PATH = "update/";

        private Updater(int counter) {
            this.setName(SessionConnection.THREAD_NAME + "-" + counter);
            this.setPriority(ThreadPriorities.MEDIUM);
            this.setDaemon(true);
            updateStart = 0;
            updateEnd = 0;
        }

        @Override
        public final void run() {

            while (updater == this) {
                if (firstConnection) {
                    // set time diff between server and client.
                    updateServerTimeDiff();
                    // wait for first update
                    TLogger.info("Processing first item update...");
                    // feedback
                    EventManager.fire(LoadingEventType.PROGRESS, LoadingStage.DOWNLOAD, 35);
                    waitForUpdate();
                    // feedback
                    EventManager.fire(LoadingEventType.PROGRESS, LoadingStage.DOWNLOAD, 50);
                    TLogger.info("Finished first item update!");

                    // (Frank) To prevent a racing condition with disconnect,
                    // since firstConnection is used as a control variable...
                    if (updater == this) {
                        firstConnection = false;
                    }
                } else {
                    // wait for update
                    waitForUpdate();
                    // yield thread to allow others, (not really required).
                    Thread.yield();
                }
            }
            TLogger.info("Killed " + this.getName() + " thread.");
        }

        private final boolean waitForUpdate() {

            if (status == null) {
                TLogger.severe("Cannot perform operation, initconnection is not started!");
                return false;
            }
            // default false
            boolean succes = false;
            UpdateResult serverVersion = null;
            // while command was not successful retry
            while (!succes && updater == this) {
                try {
                    HashMap<MapLink, Integer> request = status.getVersionRequest();

                    updateStart = System.currentTimeMillis();
                    serverVersion = RestManager.post(sessionApiTarget, UPDATE_PATH, firstConnection ? firstParams : secondParams, request,
                            UpdateResult.class, Format.DEFAULT_EVENT, Format.DEFAULT_ITEMS, UPDATE_TIMOUT);
                    if (updater == this) {
                        updateEnd = System.currentTimeMillis();
                        succes = true;
                        // stop processing
                        setProcessing(false);
                    }

                } catch (Exception exp) {
                    succes = updater == this ? handle(exp, sessionApiTarget + UPDATE_PATH) : true;
                    try {
                        Thread.sleep(Network.UPDATEFREQ);
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (updater == this && serverVersion != null && state == Network.ClientConnectionState.CONNECTED) {

                // calculate latency
                long serverTime = System.currentTimeMillis() - timeDiff;
                latency = serverTime - serverVersion.getTimeStamp();
                EventManager.fire(ConnectionEvent.CONNECTION_LATENCY, latency);

                // update local versions
                status.updateVersions(serverVersion);

                return true;
            }
            return false;
        }
    }

    /**
     * Special thread that checks connection for freezing and server IP changes.
     *
     * @author Maxim Knepfle
     */
    private class UpdaterChecker extends Thread {

        private UpdaterChecker(int connectionID) {
            this.setName("Client-" + UpdaterChecker.class.getSimpleName() + "-" + connectionID);
            this.setPriority(ThreadPriorities.MEDIUM);
            this.setDaemon(true);
        }

        @Override
        public final void run() {
            while (true) {
                if (state == Network.ClientConnectionState.CONNECTED) {
                    long updateDif = System.currentTimeMillis() - updateStart;
                    // allow more time on first connect
                    long lostTime = firstConnection ? FIRST_CONNECT_TIMEOUT : ConnectionState.LOST.getMaxWaitingTime();

                    if (updateEnd > 0 && updateDif > lostTime && !state.isBusy()) {
                        setState(Network.ClientConnectionState.OFFLINE, false);
                    }
                }

                if (state == Network.ClientConnectionState.OFFLINE && !firstConnection) {
                    connect();
                }

                try {
                    Thread.sleep(Network.UPDATEFREQ);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static final String[] DEFAULT_PARAMS = new String[] { Format.CRS, Crs.LOCAL_CODE };

    private static final String[] firstParams = new String[] { Format.GZIP, Integer.toString(ZipUtils.DEFAULT_COMPRESSION), Format.CRS,
            Crs.LOCAL_CODE, "force", "true" };

    private static final String[] secondParams = new String[] { Format.GZIP, Integer.toString(Deflater.BEST_SPEED), Format.CRS,
            Crs.LOCAL_CODE };

    private static final String THREAD_NAME = "Client-" + Updater.class.getSimpleName();

    private static final Integer EVENT_TIMEOUT = (int) (10 * Moment.MINUTE);

    private static final long FIRST_CONNECT_TIMEOUT = EVENT_TIMEOUT.longValue();

    public static final Integer DEFAULT_ID = 0;

    private CoreStakeholder myStakeholder = null;

    /**
     * Last time in MS the server was successfully connected.
     */
    private volatile long updateStart = 0;

    private volatile long updateEnd = 0;

    /**
     * Time difference between Server and Client at connection. (used as reference).
     */
    private long timeDiff = 0;

    /**
     * the token used by the client to reconnect to an old session
     */
    private String clientToken = null;

    /**
     * A single worker thread executes the runnable commands.
     */
    private final ExecutorService eventServive = TExecutors.newSingleThreadExecutor("Client-FireServerEvent");

    /**
     * The state of this connection object.
     */
    private volatile Network.ClientConnectionState state = Network.ClientConnectionState.OFFLINE;

    /**
     * The ID of the connection.
     */
    private final Integer connectionID;

    /**
     * Only true when the client has successfully not connected before to server.
     */
    private volatile boolean firstConnection = true;

    /**
     * The unique session ID. A server can run multiple sessions each identified with an ID.
     */
    private Integer serverSessionID;

    /**
     * The server's address, usualy an IP.
     */
    private String serverAddress;

    /**
     * the token used by the client to reconnect to an old session
     */
    private String apiToken = null;

    /**
     * Clients receive a unique Session when connected to a server.
     */
    private ClientData client = null;

    /**
     * The status object keeps track of the local version of the items.
     */
    private Status status;

    private String sessionApiTarget = null;
    private String sessionWebTarget = null;

    private long latency = 0;

    private volatile Updater updater = null;

    private int updaterThreadCounter = 0;

    private volatile Integer processCounter = null;

    private final Thread guiThread;

    private final ServicesConnection rootServices;

    protected SessionConnection(Integer connectionID, Thread guiThread, ServicesConnection rootServices) {

        this.connectionID = connectionID;
        this.guiThread = guiThread;
        this.rootServices = rootServices;

        // start connection check
        UpdaterChecker checker = new UpdaterChecker(connectionID);
        checker.start();
    }

    public SessionConnection(ServicesConnection rootServices) {
        this(DEFAULT_ID, null, rootServices); // default connection ID
    }

    /**
     * Init connection with the brain. Can be by rmi or local. Loop true is only used when reconnecting and thus private.
     *
     * @return succes of connection.
     */
    public final synchronized boolean connect() {

        // already connected?
        if (state != ClientConnectionState.OFFLINE) {
            TLogger.warning("Cannot run connect when connection is in state: " + state);
            return false;
        }
        killUpdater();
        setState(Network.ClientConnectionState.CONNECTING, true);

        // try it a few times
        int counter = 1;

        // keep on trying
        while (!innerConnect(counter)) {
            // start checking for other servers (IP change?)
            EventManager.fire(ConnectionEvent.CONNECTION_ATTEMPT_RETRY, counter);

            // try a few times
            if (counter >= 10) {
                TLogger.warning("Unable to connect.");
                setState(Network.ClientConnectionState.OFFLINE, false);
                return false;
            }
            counter++;
            // sleep for a sec.
            ThreadUtils.sleepInterruptible(1000);
        }
        // Notify controls we're connected
        setState(Network.ClientConnectionState.CONNECTED, true);
        startUpdater();
        return true;
    }

    public void disconnect(boolean keepServerAlive) {

        if (status == null) {
            TLogger.info("Connection: " + connectionID + " was never connected.");
            return;
        }

        TLogger.info("Disconnecting: " + connectionID + " from Server...");

        try { // wait for events to finish
            if (Boolean.TRUE.equals(eventServive.submit(() -> Boolean.TRUE).get(1, TimeUnit.SECONDS))) {
                TLogger.info("All Server events finished.");
            }
        } catch (Exception e) {
            TLogger.warning(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }

        // disconnect updater thread
        killUpdater();
        setState(Network.ClientConnectionState.DISCONNECTING, true);
        status.deactivate();

        Integer sessionID = SettingsManager.getServerSessionID(connectionID);
        String clientToken = SettingsManager.getClientToken(connectionID);

        Boolean released = rootServices.fireServerEvent(IOServiceEventType.CLOSE, sessionID, clientToken, keepServerAlive);

        this.firstConnection = true;
        this.apiToken = null;
        this.clientToken = null;

        // Only when released the client token is removed (for next time)
        if (Boolean.TRUE.equals(released)) {
            TLogger.info("Client was released from Session!");
            SettingsManager.setApiToken(connectionID, StringUtils.randomToken());
            SettingsManager.setClientToken(connectionID, StringUtils.randomToken());
        }

        // create new status
        status = new Status(status.getAppType(), this);
        // give new status to event manager
        EventManager.setStatus(this.connectionID, status);

        setState(Network.ClientConnectionState.OFFLINE, true);
        TLogger.info("Disconnected!");
    }

    public final <T> T fireServerEvent(final boolean wait, final Integer timeout, final Format inputFormat, final EventTypeEnum type,
            final Object... arguments) {
        return fireServerEvent(wait, timeout, inputFormat, DEFAULT_PARAMS, type, arguments);
    }

    @SuppressWarnings("unchecked")
    public final <T> T fireServerEvent(final boolean wait, final Integer timeout, final Format inputFormat, final String[] params,
            final EventTypeEnum type, final Object... arguments) {

        if (status == null) {
            TLogger.severe("Cannot perform operation, initconnection is not started!");
            return null;
        }
        if (inputFormat.isTyped()) {
            TLogger.severe("Typed input format: " + inputFormat + " is not allowed for events.");
            return null;
        }

        Event event = new Event(type, ObjectUtils.deepCopy(arguments));
        String error = EventValidationUtils.validateEventClasses(event, true); // Note: JSON Mapper allows: ACCEPT_SINGLE_VALUE_AS_ARRAY
        if (StringUtils.containsData(error)) {
            if (SettingsManager.getRunMode() != RunMode.RELEASE) {
                TLogger.showstopper("SessionEvent failure: " + error);
            } else {
                TLogger.severe("SessionEvent failure: " + error);
            }
            return null;
        }
        if (wait && Thread.currentThread() == guiThread) {
            TLogger.severe("Event: " + type + " is not allowed on GUI Thread: " + guiThread.getName()
                    + ", use CommunicationManager.execute() instead.");
            Thread.dumpStack();
        }

        final Callable<Object> command = () -> {

            setProcessing(true);

            // create the event
            Object result = null;
            // default false
            boolean succes = false;
            // while command was not successful retry
            while (!succes) {
                String path = Rest.EVENT + Event.toSimpleName(type.getClass()) + "/" + type.toString();
                try {
                    Object[] args = event.getContents();
                    result = RestManager.post(sessionApiTarget, path, params, args, type.getResponseClass(args), inputFormat,
                            Event.getResponseFormat(type, args), timeout != null ? timeout : EVENT_TIMEOUT);
                    succes = true;
                } catch (Exception exp) {
                    succes = handle(exp, sessionApiTarget + path + " input: " + inputFormat);
                }
            }
            setProcessing(false);
            return result;
        };

        try {
            if (wait) {
                /**
                 * This is temp construction but seems to fix the interrupt problem. When the executor thread is interrupted while it is
                 * waiting for another executor thread it should not return. By adding this thread it does not go to sleep mode but to
                 * blocked mode.
                 */
                boolean succes = false;
                Object result = null;
                while (!succes) {
                    try {
                        Thread.yield();
                        result = eventServive.submit(command).get();
                        succes = true;
                    } catch (InterruptedException exp) {
                        // do nothing
                    } catch (Exception exp) {
                        succes = true;
                        if (exp.getCause() instanceof InputException ie) {
                            throw ie;
                        } else {
                            TLogger.exception(exp);
                        }
                    }
                }
                return (T) result;
            }
            // execute the command and continue.
            eventServive.submit(command);
        } catch (InputException ie) {
            throw ie;
        } catch (Exception exp) {
            TLogger.exception(exp);
            return null;
        }
        return null;
    }

    /**
     * @return the serverToken
     */
    protected final String getApiToken() {
        return this.apiToken;
    }

    protected final ClientData getClientData() {
        return client;
    }

    protected long getConnectionLatency() {
        return latency;
    }

    public Integer getID() {
        return connectionID;
    }

    public final Item[] getItems(MapLink mapLink, Integer... ids) {

        if (Thread.currentThread() == guiThread) {
            TLogger.severe("Streaming: " + mapLink + " is not allowed on GUI Thread: " + guiThread.getName());
            Thread.dumpStack();
        }

        long start = System.currentTimeMillis();
        String path = Rest.ITEMS + StringUtils.capitalizeUnderScores(mapLink.name());
        Item[] features = RestManager.post(sessionApiTarget, path, null, ids, Item[].class, Format.DEFAULT_EVENT, Format.DEFAULT_ITEMS,
                null);
        if (TLogger.isAll()) {
            TLogger.debug("Requested: " + ids.length + " Remote Items in: " + StringUtils.toSimpleTimePast(start) + ".");
        }
        return features;
    }

    protected CoreStakeholder getMyStakeholder() {
        return myStakeholder;
    }

    public final Integer getVersion(MapLink mapLink) {
        String path = Rest.ITEMS + StringUtils.capitalizeUnderScores(mapLink.name()) + "/version";
        return RestManager.get(sessionApiTarget, path, null, Integer.class, Format.JSON);
    }

    /**
     * Handles server exceptions and fires them as events.
     *
     * @param exp
     */
    private boolean handle(Exception exp, String eventInfo) {

        ConnectionEvent type = ConnectionEvent.UNKNOWN;
        // exception is fatal when there is no way to recover from this.
        boolean fatalException = false;
        boolean offline = false;
        // only server exception keep the connection alive.
        if (exp instanceof TWebApplicationException wexp) {
            switch (wexp.getTStatus()) {
                case BAD_REQUEST:
                    // throw invalid input
                    setProcessing(false);
                    throw new InputException(wexp.getMessage());
                case CLIENT_RELEASED:
                    type = ConnectionEvent.RELEASED_FROM_SESSION;
                    offline = true;
                    break;
                case GATEWAY_TIMEOUT:
                    TLogger.warning(wexp.getMessage());
                    setProcessing(true);// server session is busy, try again later
                    return false;
                case SSL_HANDSHAKE_FAILED:
                    type = ConnectionEvent.CONNECTION_FAILURE;
                    offline = true;
                    break;
                case INTERNAL_SERVER_ERROR:
                    if (ConnectionEvent.TIMED_OUT.getDetails().equals(wexp.getMessage())) {
                        type = ConnectionEvent.TIMED_OUT; // request timed out, try again...
                        offline = true;
                    } else {
                        type = ConnectionEvent.SERVER_EXECUTION;
                        fatalException = true;
                        // server error also log!
                        TLogger.severe(wexp.getMessage());
                    }
                    break;
                case INVALID_TOKEN:
                    type = ConnectionEvent.SERVER_TOKEN;
                    offline = true;
                    break;
                case NO_SESSION:
                    type = ConnectionEvent.SERVER_NO_SESSION;
                    offline = true;
                    break;
                case REQUEST_TIMEOUT:
                    type = ConnectionEvent.TIMED_OUT; // request timed out, try again...
                    offline = true;
                    break;
                case SERVICE_UNAVAILABLE:
                    type = ConnectionEvent.SERVER_UNAVAILABLE; // disconnect
                    offline = true;
                    break;
                default:
                    type = ConnectionEvent.UNKNOWN; // fallback, unknown?
                    TLogger.severe("Unknown error at event: " + eventInfo + " message: " + wexp.getMessage());
                    offline = true;
                    break;
            }

        } else // display a message accroding to the error type.
        if (exp instanceof InterruptedException) {
            type = ConnectionEvent.THREAD_INTERRUPT;
        } else if (exp instanceof ExecutionException) {
            type = ConnectionEvent.THREAD_EXCECUTION;
        } else if (exp instanceof NullPointerException) {
            type = ConnectionEvent.NULL_POINTER;
            TLogger.exception(exp);
        } else if (exp instanceof ProcessingException) {
            type = ConnectionEvent.SERVER_UNAVAILABLE;
            offline = true;
        }
        if (offline && !state.isBusy()) {
            setState(Network.ClientConnectionState.OFFLINE, false);
        }
        // fire event and small log
        String details = type != null ? type.getDetails() : "Unknown";
        TLogger.warning(details + " -> " + exp);
        EventManager.fire(type, details, exp);

        return fatalException || offline;
    }

    public final void initSettings(final Network.AppType appType, final String argServerAddress, final Integer serverSessionID,
            final String apiToken, final String clientToken) {

        // create status
        this.status = new Status(appType, this);

        // give status to event manager
        EventManager.setStatus(this.connectionID, status);

        // set the address and token
        this.serverAddress = argServerAddress;
        this.serverSessionID = serverSessionID;
        this.clientToken = clientToken;
        this.apiToken = apiToken;

        /**
         * Create target URL
         */
        this.sessionApiTarget = "https://" + serverAddress + "/" + Rest.API + Rest.SESSION;
        this.sessionWebTarget = "https://" + serverAddress + "/" + Rest.WEB;

        /**
         * Create unique target when more the 1 connection is established for RestManager
         */
        if (!DEFAULT_ID.equals(connectionID)) {
            sessionApiTarget += "?connection=" + connectionID;
            sessionWebTarget += "?connection=" + connectionID;
        }
    }

    /**
     * Init connection with the brain. Can be by rmi or local
     *
     * @return succes of connection.
     */
    private boolean innerConnect(int counter) {

        try {

            // feedback
            EventManager.fire(LoadingEventType.PROGRESS, LoadingStage.CONNECT, 20);
            TLogger.info("Connecting to Server Session: " + this.serverSessionID + " (Attempt " + counter + ")");

            String clientName = "Unknown";
            try {
                // get the address of this host.
                clientName = InetAddress.getLocalHost().getHostName();
            } catch (Exception exp) {
                TLogger.exception(exp);
            }

            JoinReply reply = rootServices.fireServerEvent(IOServiceEventType.JOIN, serverSessionID, status.getAppType(), clientName,
                    clientToken);

            if (reply == null) {
                return false;
            }

            client = reply.client;
            clientToken = reply.client.getClientToken();
            apiToken = reply.apiToken;

            /**
             * Set headers for this connection target
             */
            MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
            headers.putSingle(Network.CLIENT_TOKEN_HEADER, clientToken);
            headers.putSingle(Network.SERVER_TOKEN_HEADER, apiToken);
            headers.putSingle(Network.RESOLUTION_HEADER, OSUtils.getDisplayResolution());

            RestManager.setHeaders(this.sessionApiTarget, headers);
            RestManager.setHeaders(this.sessionWebTarget, headers);

            /**
             * Setup client
             */
            if (firstConnection) {
                Map<Network.SessionType, MapLink[]> mapList = new HashMap<>();
                mapList.put(reply.sessionType, reply.lists);
                DataLord.setup(mapList);
                JsonMapper.setDefaultTyping(new ItemTypeResolverBuilder());

                // add settings after successful first connect
                SettingsManager.setProjectName(reply.project);
                SettingsManager.setLanguage(reply.languague);
                SettingsManager.setClientToken(connectionID, clientToken);
                SettingsManager.setApiToken(connectionID, apiToken);
                SettingsManager.setServerSessionID(connectionID, serverSessionID);

                // set sessionType for this session/connection
                status.setSessionType(reply.sessionType, reply.project);
                TLogger.info("Client MapLinks for: " + reply.sessionType + ": " + status.getAppType() + ": " + reply.project + ": "
                        + DataLord.getAppLinks(reply.sessionType, status.getAppType()).length);
                EventManager.fire(connectionID, ComEvent.MAPLINKS_INITIALIZED, reply.sessionType, reply.project, status.getAppType(),
                        serverSessionID);
            }

        } catch (Exception exp) {
            handle(exp, "join session");
            return false;
        }
        TLogger.info("Connected with API Token: " + apiToken);
        return true;
    }

    public boolean isConnected() {
        return state == Network.ClientConnectionState.CONNECTED;
    }

    /**
     * @return the connected
     */
    public final boolean isConnectedOrConnecting() {
        return state == Network.ClientConnectionState.CONNECTED || state == Network.ClientConnectionState.CONNECTING;
    }

    private void killUpdater() {
        // zombiefy old thread
        if (updater != null) {
            Updater zombie = this.updater;
            zombie.setName(zombie.getName() + "-Zombie");
            updater = null;
            zombie.interrupt();
            updateStart = 0;
            updateEnd = 0;
        }
    }

    protected void setMyStakeholder(final Item[] items) {

        if (!StringUtils.containsData(clientToken)) {
            return;
        }

        for (Item item : items) {
            CoreStakeholder stakeholder = (CoreStakeholder) item;
            if (clientToken.equals(stakeholder.getClientToken())) {
                if (myStakeholder == null || !myStakeholder.getID().equals(stakeholder.getID())) {
                    this.myStakeholder = stakeholder;
                    EventManager.fire(ComEvent.COMMUNICATION_STAKEHOLDER_SET, myStakeholder);
                } else {
                    this.myStakeholder = stakeholder;
                }
            }
        }
    }

    private final synchronized void setProcessing(boolean processing) {

        if (processing && (processCounter == null || processCounter <= 0)) {
            processCounter = 10;
            EventManager.fire(Processing.START);

        } else if (!processing && processCounter != null) {
            processCounter = null;
            EventManager.fire(Processing.DONE);

        } else if (processCounter != null) {
            processCounter--; // trigger again every X calls
        }
    }

    /**
     * Set connection to a certain state.
     */
    private final void setState(Network.ClientConnectionState argState, boolean onPurpose) {

        if (argState != state) {
            // first set to connected flag
            state = argState;
            EventManager.fire(connectionID, ConnectionEvent.CONNECTION_STATE_CHANGE, state, onPurpose);
        }
    }

    protected Integer startOrLoadSessionOnServer(final Network.SessionType sessionType, final String projectName, final String sessionName,
            String groupToken, final String saveName, final TLanguage language, final Integer prefSessionID) {

        // only setup when session is not already connected.
        if (state != ClientConnectionState.OFFLINE) {
            TLogger.severe("Cannot run setup when connection is in state: " + state);
            return null;
        }

        Integer sessionID;
        if (StringUtils.containsData(sessionName) && StringUtils.containsData(saveName)) {
            sessionID = (Integer) rootServices.fireServerEvent(IOServiceEventType.LOAD_SAVED_SESSION, sessionType, projectName, sessionName,
                    saveName, groupToken, prefSessionID);
        } else {
            sessionID = (Integer) rootServices.fireServerEvent(IOServiceEventType.START, sessionType, projectName, language, null,
                    groupToken);
        }
        SettingsManager.setServerSessionID(this.connectionID, sessionID);
        return sessionID;
    }

    private void startUpdater() {
        // start updating the client
        updater = new Updater(updaterThreadCounter);
        updaterThreadCounter++;
        updater.start();
    }

    private void updateServerTimeDiff() {
        try {
            // calc value
            Long serverTime = rootServices.fireServerEvent(IOServiceEventType.GET_SERVER_TIME);
            if (serverTime != null) {
                this.timeDiff = System.currentTimeMillis() - serverTime;
            }
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
    }
}
