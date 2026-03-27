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

import nl.tytech.core.event.Event;

/**
 * ConnectionIDEvent allow events to be fired for specific sessions. This is only usefull when running multiple sessions on one system.
 *
 * @author William van Velzen
 */
public class ConnectionIDEvent extends Event {

    private static final long serialVersionUID = -1934512728855774874L;

    /**
     * When true this event is related to the given connectionID.
     * @param event
     * @param filterConnectionID
     * @return
     */
    public static boolean filter(Event event, Integer filterConnectionID) {
        return event instanceof ConnectionIDEvent sevent && sevent.getConnectionID().equals(filterConnectionID);
    }

    /**
     * Extra var indicating the connection ID with the server. Note: not to be confused with the server's session ID.
     */
    private final Integer connectionID;

    public ConnectionIDEvent(Integer connectionID, EventTypeEnum type, final Object... contentsArgs) {
        super(type, contentsArgs);
        this.connectionID = connectionID;
    }

    public Integer getConnectionID() {
        return this.connectionID;
    }
}
