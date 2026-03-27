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
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.ClientData;
import nl.tytech.core.net.serializable.ClientData.ConnectionState;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Stakeholder: This class keeps track of the stakeholder. The stakeholder is used by a participant, identified over the network with a
 * client ID.
 *
 * @author Maxim Knepfle
 */
public class CoreStakeholder extends UniqueNamedItem {

    /**
     * Serial
     */
    private static final long serialVersionUID = 178858230577513380L;

    @XMLValue
    private boolean active = false;

    @XMLValue
    private TColor color = new TColor(255, 255, 255);

    /**
     * Unique token that remains the same until reset
     */
    @XMLValue
    @NoDefaultText
    private String webToken = StringUtils.randomHumanString(Network.TOKEN_KEY_LENGTH);

    @XMLValue
    private Long lastSelected = null;

    /**
     * The network session belonging to this stakeholder.
     */
    private ClientData client;

    /**
     * Empty constructor.
     */
    public CoreStakeholder() {

    }

    /**
     * @return the session
     */
    public final ClientData getClient() {
        return this.client;
    }

    /**
     * @return the clientAddress
     */
    public final String getClientAddress() {
        return client != null ? client.getAddress() : StringUtils.EMPTY;
    }

    public final String getClientComputerName() {
        return client != null ? client.getComputerName() : "-";
    }

    /**
     * Returns the ID of the client belonging to this stakeholder.
     *
     * @return
     */
    public final String getClientToken() {
        return client != null ? client.getClientToken() : null;
    }

    /**
     * @return the color
     */
    public final TColor getColor() {
        return this.color;
    }

    /**
     * @return the state of the connection with the client playing this stakeholder.
     */
    public final ConnectionState getConnectionState() {
        return client != null ? client.getConnectionState() : ConnectionState.RELEASED;
    }

    public Long getLastSelected() {
        return lastSelected;
    }

    public String getWebToken() {
        return StringUtils.toHex(getLord().getSessionID()) + webToken;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Release the client currently belonging to this stakeholder.
     */
    public final void releaseSession() {

        if (this.client != null) {
            this.client.release();
        }
        this.client = null;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @param color
     */
    public void setColor(TColor color) {
        this.color = color;
    }

    public final void setLastSelected(Long lastSelected) {
        this.lastSelected = lastSelected;
    }

    public final void setSession(ClientData session) {
        this.client = session;
        if (session != null) {
            lastSelected = System.currentTimeMillis();
        }
    }

    public void setWebToken(String webToken) {
        this.webToken = webToken;
    }

    @Override
    public final String toString() {
        return getName();
    }
}
