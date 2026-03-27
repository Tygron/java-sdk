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
package nl.tytech.core.net.event;

import nl.tytech.core.event.Event;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.util.StringUtils;

/**
 * Service events that are used in root API for example to start a Project or edit a User.
 *
 * @author Maxim Knepfle
 */
public class RemoteServicesEvent extends Event {

    public interface ServiceEventType extends EventTypeEnum {

        public boolean canDomainOverride();

        public AccessLevel getAccessLevel();

        public Integer getTimeoutOverride();

        public boolean isRoutingServerOnly();

    }

    public static final String QUERY_DOMAIN = "domain";

    public static final String OPTIONAL_DOMAIN_NAME = "Domain Name (optional, leave empty by default)";

    public static final String THREAD_NAME = Event.toSimpleName(RemoteServicesEvent.class) + "-";

    private static final long serialVersionUID = 4815797996076386179L;

    private final String clientName;
    private final String clientAddress;
    private final String userAgent;
    private final String domainOverride;

    public RemoteServicesEvent(String clientName, String clientAddress, String userAgent, String domainOverride, ServiceEventType type,
            Object[] arguments) {

        super(type, arguments);
        this.clientName = StringUtils.containsData(clientName) ? clientName : StringUtils.EMPTY;
        this.clientAddress = StringUtils.containsData(clientAddress) ? clientAddress : StringUtils.EMPTY;
        this.userAgent = StringUtils.containsData(userAgent) ? userAgent : StringUtils.EMPTY;
        this.domainOverride = type != null && type.canDomainOverride() ? domainOverride : null;
    }

    public AccessLevel getAccessLevel() {
        return ((ServiceEventType) this.getType()).getAccessLevel();
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDomainOverride() {
        return domainOverride;
    }

    public String getFullClientName() {
        return getClientName() + " (" + getClientAddress() + ")";
    }

    public String getUserAgent() {
        return userAgent;
    }
}
