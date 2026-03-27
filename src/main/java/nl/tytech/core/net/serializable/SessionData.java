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
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.ClientData.ConnectionState;
import nl.tytech.util.ObjectUtils;

/**
 * Stores general data about the running Session. Can be used in e.g. the launch menu.
 *
 * @author Maxim Knepfle
 */
public class SessionData implements Serializable {

    private static final long serialVersionUID = -3326616812363816703L;

    private ArrayList<ClientData> clients;

    private Integer sessionID;

    private String name;

    private KeepAlive keepAlive;

    private Network.SessionType sessionType;

    public SessionData() {

    }

    public SessionData(Integer sessionID, Network.SessionType sessionType, String name, KeepAlive keepAlive, List<ClientData> clients) {
        this.sessionID = sessionID;
        this.sessionType = sessionType;
        this.name = name;
        this.keepAlive = keepAlive;
        this.clients = ObjectUtils.toArrayList(clients);
    }

    public boolean areClientsActive() {
        return getNumberOfActiveClients() > 0;
    }

    public List<ClientData> getClients() {
        return clients;
    }

    public KeepAlive getKeepAlive() {
        return keepAlive;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfActiveClients() {
        int i = 0;
        for (ClientData client : clients) {
            if (client.getConnectionState() == ConnectionState.CONNECTED) {
                i++;
            }
        }
        return i;
    }

    public Integer getSessionID() {
        return sessionID;
    }

    public Network.SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public String toString() {
        return name;
    }
}
