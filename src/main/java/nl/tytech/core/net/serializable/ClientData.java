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
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.core.net.Network;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Client data
 *
 * THREAD SAFE: This class keeps track of a client computer connected to one of the sessions.
 *
 * @author Maxim Knepfle
 */
public class ClientData implements Serializable {

    /**
     * The state of connection of the client belonging to this stakeholder.
     */
    public enum ConnectionState {

        /** normal heartbeat received */
        CONNECTED(TColor.GREEN, "Connected", 0),
        /** if the heartbeat is longer than failing but less then lost */
        FAILING(TColor.YELLOW, "Failing...", Network.UPDATEFREQ * 40),
        /** client heartbeat took longer than timeout */
        LOST(TColor.RED, "Lost Connection!", Network.UPDATEFREQ * 60),
        /** client is released from session */
        RELEASED(TColor.BLACK, "No Client", 0);

        private final TColor color;

        private final String label;

        private final int timeout;

        private final int maxWait;

        private ConnectionState(TColor color, String label, int timeout) {

            this.color = color;
            this.label = label;
            this.timeout = timeout;
            this.maxWait = (int) (timeout * 0.8d); // set to 80% of failing
        }

        public TColor getColor() {
            return color;
        }

        public String getLabel() {

            return label;
        }

        public int getMaxWaitingTime() {
            return maxWait;
        }

        public int getTimeout() {
            return timeout;
        }
    }

    private static final long serialVersionUID = -1861382127096953800L;

    private String address;

    private String userAgent;

    private String computerName;

    private String fullName;

    private String userName;

    private Network.AppType subscription;

    private String clientToken;

    /**
     * THREAD SAFE: Last time in ms since 1970 the client belonging to this stakeholder connected to the server.
     */
    private volatile long lastSeen = 0;

    /**
     * THREAD SAFE:
     */
    private volatile boolean updated = false;

    /**
     * THREAD SAFE:The state of connection of the client belonging to this stakeholder.
     */
    private volatile ConnectionState connectionState = ConnectionState.CONNECTED;

    public ClientData() {

    }

    /**
     * @param clientType
     * @param clientAddress
     * @param clientName
     * @param id
     * @param clientToken
     */
    public ClientData(Network.AppType clientType, String userName, String fullName, final String clientAddress, String userAgent,
            final String clientName) {

        this.subscription = clientType;
        this.address = clientAddress;
        this.userAgent = userAgent;
        this.computerName = StringUtils.containsData(clientName) ? clientName : "Unknown";
        this.userName = userName;
        this.lastSeen = System.currentTimeMillis();
        this.clientToken = StringUtils.randomToken();
        this.fullName = fullName;

        // log
        TLogger.info("Client started: " + this.toString());
    }

    public void connect() {
        synchronized (this) {
            this.connectionState = ConnectionState.CONNECTED;
        }
    }

    /**
     * Get the IP address of the client.
     *
     * @return
     */
    public final String getAddress() {
        return address;
    }

    /**
     * @return the clientType
     */
    public final Network.AppType getAppType() {
        return subscription;
    }

    /**
     * @return the token
     */
    public final String getClientToken() {
        return clientToken;
    }

    /**
     * Get the name of the client computer.
     *
     * @return
     */
    public final String getComputerName() {
        return computerName;
    }

    /**
     * @return the connectionState
     */
    public final ConnectionState getConnectionState() {
        return this.connectionState;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * THREAD SAFE: Get last seen time
     * @return
     */
    public long getLastSeen() {
        return lastSeen;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * True when connection is connected or failing
     */
    public final boolean isActive() {
        return getConnectionState() == ConnectionState.CONNECTED || getConnectionState() == ConnectionState.FAILING;
    }

    /**
     * @return the updated
     */
    public final boolean isUpdated() {
        return this.updated;
    }

    /**
     * Ping is called each time the client connects.
     */
    public final void ping(final boolean command) {
        synchronized (this) {
            lastSeen = System.currentTimeMillis();
        }
    }

    public void release() {
        synchronized (this) {
            this.connectionState = ConnectionState.RELEASED;
        }
    }

    public boolean releaseCheck() {
        synchronized (this) {
            return this.connectionState == ConnectionState.RELEASED;
        }
    }

    /**
     * @param updated the updated to set
     */
    public final void setUpdated(boolean updated) {
        synchronized (this) {
            this.updated = updated;
        }
    }

    @Override
    public final String toString() {
        return getAppType().toString() + "-" + " (token: " + getClientToken() + ", name: " + getComputerName() + ", address: "
                + getAddress() + ")";
    }

    public final void update(String receivedName, String receivedAddress, String receivedUserAgent) {

        synchronized (this) {

            if (!receivedAddress.equals(address)) {
                TLogger.warning(
                        "IP Address change from " + this.address + " to " + receivedAddress + " for client " + getClientToken() + ".");
                this.address = receivedAddress;
            }
            if (!receivedName.equals(computerName)) {
                TLogger.warning(
                        "Computer name change from " + this.computerName + " to " + receivedName + " for client " + getClientToken() + ".");
                this.computerName = receivedName;
            }
            if (!receivedUserAgent.equals(userAgent)) {
                TLogger.warning(
                        "User Agent change from " + this.userAgent + " to " + receivedUserAgent + " for client " + getClientToken() + ".");
                this.computerName = receivedName;
            }
            this.connectionState = ConnectionState.CONNECTED;
        }
    }

    public final void updateState() {
        synchronized (this) {
            // check last update time.
            long now = System.currentTimeMillis();
            switch (connectionState) {
                case RELEASED:
                    break;
                case CONNECTED:
                    if (now - lastSeen > ConnectionState.FAILING.getTimeout()) {
                        // not active?
                        connectionState = ConnectionState.FAILING;
                        updated = true;
                        TLogger.info("Client connection failing: " + this.toString());
                    }
                    break;
                case FAILING:
                    if (now - lastSeen < ConnectionState.FAILING.getTimeout()) {
                        // active?
                        connectionState = ConnectionState.CONNECTED;
                        updated = true;
                        TLogger.info("Client connection restored: " + this.toString());
                    } else if (now - lastSeen > ConnectionState.LOST.getTimeout()) {
                        // lost?
                        connectionState = ConnectionState.LOST;
                        updated = true;
                        TLogger.info("Client connection lost: " + this.toString());
                    }
                    break;
                case LOST:
                    if (now - lastSeen < ConnectionState.FAILING.getTimeout()) {
                        // active?
                        connectionState = ConnectionState.CONNECTED;
                        updated = true;
                        // log
                        TLogger.info("Client connection restored: " + this.toString());
                    }
                    break;
            }
        }
    }
}
