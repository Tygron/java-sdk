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
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.util.StringUtils;

/**
 * @author Jeroen Warmerdam
 */
public class EventBundle extends UniqueNamedItem implements Action, ImageItem {

    private static final long serialVersionUID = -5779733422240767368L;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @EventList(serverSide = true)
    @ListOfClass(CodedEvent.class)
    public ArrayList<CodedEvent> serverEvents = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = false)
    @ListOfClass(CodedEvent.class)
    public ArrayList<CodedEvent> clientEvents = new ArrayList<>();

    @XMLValue
    @AssetDirectory(ACTION_IMAGE_LOCATION)
    private String imageName = DEFAULT_IMAGE;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @ItemIDField(MapLink.EVENT_BUNDLES)
    @XMLValue
    private ArrayList<Integer> compoundEventBundleIDs = new ArrayList<>();

    public CodedEvent addCodedEvent(EventTypeEnum type, Object... objects) {
        boolean isServerSide = type.isServerSide();
        CodedEvent event = CodedEvent.createUniqueIDEvent(getCodedEvents(isServerSide), type, objects);
        getCodedEvents(isServerSide).add(event);
        return event;
    }

    public List<CodedEvent> getClientEvents() {
        if (compoundEventBundleIDs.size() > 0) {
            List<CodedEvent> events = new ArrayList<>();

            List<EventBundle> bundles = this.getItems(MapLink.EVENT_BUNDLES, compoundEventBundleIDs);
            for (EventBundle bundle : bundles) {
                events.addAll(bundle.getClientEvents());
            }

            events.addAll(clientEvents);
            return events;
        }
        return clientEvents;
    }

    public CodedEvent getCodedEventForID(boolean isServerSide, Integer codedEventID) {
        List<CodedEvent> events = getCodedEvents(isServerSide);
        for (CodedEvent codedEvent : events) {
            if (codedEvent.getID().equals(codedEventID)) {
                return codedEvent;
            }
        }
        return null;
    }

    public List<CodedEvent> getCodedEvents(boolean isServerSide) {
        return isServerSide ? serverEvents : clientEvents;
    }

    public List<Integer> getCompoundEventBundleIDs() {
        return compoundEventBundleIDs;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageLocation() {
        return Action.ACTION_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageVersion() {
        return imageVersion;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.EVENT_BUNDLES;
    }

    public List<CodedEvent> getServerEvents() {
        if (compoundEventBundleIDs.size() > 0) {
            List<CodedEvent> events = new ArrayList<>();

            List<EventBundle> bundles = this.getItems(MapLink.EVENT_BUNDLES, compoundEventBundleIDs);
            for (EventBundle bundle : bundles) {
                events.addAll(bundle.getServerEvents());
            }

            events.addAll(serverEvents);
            return events;
        }
        return serverEvents;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public CodedEvent removeCodedEvent(boolean isServerSide, Integer eventID) {
        CodedEvent codedEvent = null;
        Iterator<CodedEvent> eventIterator = getCodedEvents(isServerSide).iterator();
        while (eventIterator.hasNext()) {
            CodedEvent event = eventIterator.next();
            if (event.getID().equals(eventID)) {
                codedEvent = event;
                eventIterator.remove();
                break;
            }
        }
        return codedEvent;
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

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setImageName(String name) {
        this.imageName = name;
        this.imageVersion++;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String validated(boolean startNewSession) {
        return EventValidationUtils.validateCodedEvents(this, serverEvents, true)
                + EventValidationUtils.validateCodedEvents(this, clientEvents, false);

    }
}
