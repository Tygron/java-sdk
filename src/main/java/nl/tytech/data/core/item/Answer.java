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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.core.net.serializable.MapLink;

/**
 * Answer: This class keeps track of the popup answer
 *
 * @author Maxim Knepfle
 */
public class Answer implements Serializable {

    private static final long serialVersionUID = -3341584447107211864L;

    @XMLValue
    private boolean selected = false;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    private String contents = "No Contents";

    @XMLValue
    @EventList(serverSide = true)
    private ArrayList<CodedEvent> events = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = false)
    private ArrayList<CodedEvent> clientEvents = new ArrayList<>();

    public Answer() {

    }

    public Answer(final String contents) {

        this.contents = contents;
    }

    public CodedEvent addEvent(EventTypeEnum type, Object... objects) {
        boolean isServerSide = type.isServerSide();
        CodedEvent event = CodedEvent.createUniqueIDEvent(getCodedEvents(isServerSide), type, objects);
        getCodedEvents(isServerSide).add(event);
        return event;
    }

    public List<CodedEvent> getClientEvents() {
        return clientEvents;
    }

    public CodedEvent getCodedEventForID(boolean isServerSide, Integer id) {

        for (CodedEvent codedEvent : getCodedEvents(isServerSide)) {
            if (codedEvent.getID().equals(id)) {
                return codedEvent;
            }
        }
        return null;
    }

    public List<CodedEvent> getCodedEvents(boolean isServerSide) {
        return isServerSide ? events : clientEvents;
    }

    public final String getContents() {
        return this.contents;
    }

    public List<CodedEvent> getEvents() {
        return events;
    }

    public final Integer getID() {
        return this.id;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean removeEvent(boolean isServerSide, Integer eventID) {
        CodedEvent codedEvent = getCodedEventForID(isServerSide, eventID);
        if (codedEvent == null) {
            return false;
        }
        return getCodedEvents(isServerSide).remove(codedEvent);
    }

    private void replaceAllID(List<CodedEvent> eventList, MapLink mapLink, Integer id) {

        for (CodedEvent event : eventList) {
            for (Object param : event.getParameters()) {
                if (param instanceof ItemID itemID && itemID.getMapLink() == mapLink) {
                    itemID.setID(id);
                }
            }
        }
    }

    public void replaceAllID(MapLink mapLink, Integer id) {
        replaceAllID(events, mapLink, id);
        replaceAllID(clientEvents, mapLink, id);
    }

    public boolean replaceEvent(boolean isServerSide, CodedEvent codedEvent) {
        List<CodedEvent> eventList = getCodedEvents(isServerSide);

        int i = 0;
        for (; i < eventList.size(); ++i) {
            if (codedEvent.getID().equals(eventList.get(i).getID())) {
                break;
            }
        }

        if (i >= eventList.size()) {
            return false;
        }

        eventList.set(i, codedEvent);
        return true;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return getContents();
    }
}
