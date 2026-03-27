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

import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.serializable.TLicense;
import nl.tytech.naming.EngineNC;
import nl.tytech.util.StringUtils;
import nl.tytech.util.TStatus;

/**
 * Collection of shared small networking classes
 *
 * @author Maxim Knepfle
 *
 */
public interface Network {

    /**
     * Defines the type of communication. Each client (client, beam and control) have unique subscriptions to events and priorities.
     */
    public enum AppType {

        /**
         * The Client app launcher.
         */
        LAUNCHER,
        /**
         * Application for the session participants.
         */
        PARTICIPANT,
        /**
         * The editor application.
         */
        EDITOR,
        /**
         * Server app running logic
         */
        SERVER,
        /**
         * GeoShare client application
         */
        SHARE,
        /**
         * Tools devscripts applications
         */
        TOOLS;

        public final String getAppName() {
            return switch (this) {
                case SERVER -> EngineNC.SERVER_NAME;
                case SHARE -> EngineNC.SHARE_NAME;
                default -> EngineNC.CLIENT_NAME;
            };
        }

        public final int getDefaultHeight() {
            return switch (this) {
                case SHARE, TOOLS -> 600;
                default -> 800;
            };
        }

        public final int getDefaultWidth() {
            return switch (this) {
                case SHARE, TOOLS -> 800;
                default -> 1280;
            };
        }

        /**
         * Name used for the final end user in documentation etc.
         */
        public String getEndUserNaming() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum ClientConnectionState {

        OFFLINE(false), CONNECTING(true), DISCONNECTING(true), CONNECTED(false);

        private final boolean busy;

        private ClientConnectionState(boolean busy) {
            this.busy = busy;
        }

        public boolean isBusy() {
            return busy;
        }
    }

    public enum ConnectionEvent implements EventTypeEnum {

        //
        NO_INTERNET("Connection failed (Unable to determine Server Address)...", String.class, Exception.class),
        //
        NULL_POINTER("Null Pointer", String.class, Exception.class),
        //
        SERVER_DIFFERENT_SESSION_TYPE("Server running different Session Type!"),
        //
        SERVER_EXECUTION("Error during execution server side.", String.class, Exception.class),
        //
        SERVER_NO_SESSION("No project loaded on the server.", String.class, Exception.class),
        //
        SERVER_FAIR_CAPACITY(TStatus.FAIR_CAPACITY.getDescription(), String.class, Exception.class),
        //
        SERVER_MAX_CAPACITY(TStatus.MAX_CAPACITY.getDescription(), String.class, Exception.class),
        //
        SERVER_UNAVAILABLE(
                "Disconnected from Server. Please verify your internet connection and try again.\nThe Server might also be down for Maintenance.",
                String.class, Exception.class),
        //
        SERVER_TOKEN("Server running with different token! Please restart.", String.class, Exception.class),
        //
        THREAD_INTERRUPT("Cannot run client command, thread was interrupted.", String.class, Exception.class),
        //
        FIRST_UPDATE_STARTED("Client is update is starting."),
        //
        FIRST_UPDATE_DONE("Client is updated for the first time."),
        //
        FIRST_UPDATE_EVENT_HANDLED("First update event result is handled."),
        //
        TIMED_OUT("Connection timed out.", String.class, Exception.class),
        //
        UNKNOWN("Connection failed (Unknown)", String.class, Exception.class),
        //
        CONNECTION_ATTEMPT_RETRY("Former connection attempt failed, forcing retry.", Integer.class),

        CONNECTION_LATENCY("Connection latency.", Long.class),

        CONNECTION_FAILURE("Failed to establish a Secure Connection. Please verify your internet connection and try again.", String.class,
                Exception.class),

        THREAD_EXCECUTION("Failure during execution of thread.", String.class, Exception.class),

        RELEASED_FROM_SESSION("Stakeholder was released from the session.", String.class, Exception.class),

        CONNECTION_STATE_CHANGE("Connection changed from state.", Network.ClientConnectionState.class, Boolean.class),

        AUTHENTICATION_FAIL("Username & Password combination is incorrect.", String.class, Exception.class),

        SERVER_REBOOT("Server rebooted, restart app.", String.class, Exception.class);

        private final String details;

        private List<Class<?>> classes = null;

        private ConnectionEvent(final String details, Class<?>... classes) {
            this.details = details;
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

        public String getDetails() {
            return this.details;
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

    /**
     * Defines the type of session. This can be local, WAN, LAN
     */
    public static enum SessionType {

        /**
         * A single user
         */
        SINGLE,
        /**
         * A Multi user session on a network (LAN or WAN)
         */
        MULTI,
        /**
         * A project launched in edit mode.
         */
        EDITOR;

    }

    ;

    /**
     * Update frequency when connected to the server.
     */
    public static final int UPDATEFREQ = 500;

    public static final String REQUEST_SMS = "SMS";

    public static final String WWW_AUTH_REALM = "Basic realm=";

    public static final String TWO_FACTOR_REQUEST = "Two Factor authentication SMS code is requested.";

    public static final String TWO_FACTOR_INCORRECT = "Two Factor authorization failed.";

    public static final String LOGIN_KEY_EXPIRED = "Your login key has expired, please log in again using your password.";

    public static final String SERVER_BOOT_HEADER = "TygronBootTime";

    public static final String CLIENT_NAME_HEADER = "ClientName";

    public static final String REFERER_HEADER = "Referer"; // Note: The word "referrer" has been misspelled in the RFC

    public static final String SERVER_TOKEN_HEADER = "token";

    public static final String CLIENT_TOKEN_HEADER = "ClientToken";

    public static final String RESOLUTION_HEADER = "res";

    public static final String BETA = TLicense.LIMIT.formatted("Domain is not allowed access Beta Server.");

    public static final int MIN_TOKEN_UNIQUE_LENGTH = 6;

    /**
     * Hexadecimal (8 chars) representation of Integer ID
     */
    public static final int TOKEN_HEX_ID_LENGTH = StringUtils.INTEGER_HEX_CHARS_LENGTH;

    /**
     * Random token key length
     */
    public static final int TOKEN_KEY_LENGTH = 24;

    /**
     * Combined Hexadecimal Integer ID with key length
     */
    public static final int TOKEN_LENGTH = TOKEN_HEX_ID_LENGTH + TOKEN_KEY_LENGTH;

}
