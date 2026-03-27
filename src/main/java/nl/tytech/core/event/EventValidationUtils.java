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
package nl.tytech.core.event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * EventValidationUtils contains a number of methods used in various validation tasks in the Items.
 *
 * @author Marijn van Zanten & Maxim Knepfle
 */
public class EventValidationUtils {

    private static final Object[] convertCodedParams(CodedEvent codedEvent) {

        List<Object> event = codedEvent.getParameters();
        // Put the rest of the contents into a list
        List<Object> contents = new ArrayList<>();
        for (int i = 1; i < event.size(); i++) {
            Object par = event.get(i);
            // filter out itemID's
            if (par instanceof ItemID item) {
                par = item.getID();
            }
            contents.add(par);
        }
        return contents.toArray(new Object[contents.size()]);
    }

    public static final Event convertEvent(CodedEvent codedEvent) {

        if (codedEvent.getParameters().isEmpty()) {
            return new Event(null);
        }
        return new Event((EventTypeEnum) codedEvent.getParameter(0), convertCodedParams(codedEvent));
    }

    public static final String validateCodedEvents(Item source, List<CodedEvent> events, boolean serverSide) {

        String result = StringUtils.EMPTY;
        Lord lord = source.getLord();
        List<CodedEvent> removables = new ArrayList<>();

        for (CodedEvent codedEvent : events) {
            List<Object> event = codedEvent.getParameters();

            if (event.size() == 0) {
                TLogger.warning("Missing event data for CodedEvent in " + source.getClass().getSimpleName() + " " + source + " (ID: "
                        + source.getID() + "). Removing empty event to fix it!");
                removables.add(codedEvent);
                // result = false;
                continue;
            }
            // Put the rest of the contents into a list
            EventTypeEnum ete = null;
            try {
                ete = (EventTypeEnum) event.getFirst();
            } catch (Exception e) {
                result += "\nThe event type should be an implementation of the EventTypeEnum interface, not "
                        + event.getFirst().getClass().getSimpleName();
                continue;
            }

            if (ete.isServerSide() != serverSide) {
                if (serverSide) {
                    result += "\nExpected a server side event in: " + source.getClass().getSimpleName() + " " + source + " (ID: "
                            + source.getID() + "), but " + event.get(0).getClass().getSimpleName() + " is a client side event.";
                } else {
                    result += "\nExpected a client side event in: " + source.getClass().getSimpleName() + " " + source + " (ID: "
                            + source.getID() + "), but " + event.get(0).getClass().getSimpleName() + " is a server side event.";
                }
                continue;
            }

            Event dummyEvent = new Event(ete, convertCodedParams(codedEvent));
            String classError = validateEventClasses(dummyEvent);
            if (StringUtils.containsData(classError)) {
                result += "\n" + classError + "\nFailing event is located in: " + source.getClass().getSimpleName() + " " + source
                        + " (ID: " + source.getID() + ").";

            } else if (lord != null) {
                String contentError = lord.validateEventContents(dummyEvent);
                if (StringUtils.containsData(contentError)) {
                    TLogger.warning("\n" + contentError + "\nFailing event is located in: " + source.getClass().getSimpleName() + ": "
                            + source + " (ID: " + source.getID() + "). Removing event to fix this!");
                    removables.add(codedEvent);
                    continue;
                }
            }
        }

        for (CodedEvent removable : removables) {
            events.remove(removable);
        }
        return result;
    }

    /**
     * Validate events. An event is valid when it has the same classes in its contents as defined in EventTypeEnum.
     *
     * @param event
     * @return
     */
    public static final String validateEventClasses(Event event) {
        return validateEventClasses(event, false);
    }

    public static final String validateEventClasses(Event event, boolean acceptSingleValueAsArray) {

        EventTypeEnum type = event.getType();
        Object[] params = event.getContents();
        List<Class<?>> classes = type.getClasses();
        if (classes == null) {
            return "Event of type " + Event.getEventName(type) + " is missing Classes definition.";
        }
        if (classes.size() == 0 && params.length == 0) {
            return StringUtils.EMPTY;
        }
        if (params.length != classes.size()) {
            String contentClasses = "( ";
            boolean first = true;

            for (Class<?> classz : classes) {
                if (classz != null) {
                    if (!first) {
                        contentClasses += ", ";
                    } else {
                        first = false;
                    }
                    contentClasses += classz.getSimpleName();
                }
            }
            contentClasses += " )";

            String[] data = new String[params.length];
            for (int i = 0; i < data.length; i++) {
                Object content = params[i];
                if (content != null) {
                    Class<?> classz = content.getClass();
                    data[i] = classz.getSimpleName();
                } else {
                    data[i] = "NULL";
                }
            }
            String sourceClasses = "( " + StringUtils.implode(data) + " )";
            return "Event " + Event.getEventName(type) + " has incorrect parameters, required are: " + contentClasses + ". Present are: "
                    + sourceClasses;
        }

        for (int i = 0; i < params.length; i++) {
            Object content = params[i];
            Class<?> classz = classes.get(i);
            if (content != null && !classz.isAssignableFrom(content.getClass())) {
                // IGNORE: allow single values as array (json parsers converts this anyway)
                if (!acceptSingleValueAsArray || !ObjectUtils.isSingleValueOfArray(classes.get(i), content.getClass())) {
                    return "Parameter " + i + " of Event " + Event.getEventName(type) + " is of type: " + content.getClass().getSimpleName()
                            + ". Type " + classes.get(i).getSimpleName() + " is expected.";
                }
            }
        }

        /**
         * Check for (allowed) NULL values and array length for server events
         */
        if (type.isServerSide()) {
            EventIDField eventIDField = ObjectUtils.getEnumAnnotation((Enum<?>) type, EventIDField.class);
            int arrayLength = -1;
            loop: for (int index = 0; index < event.getContents().length; index++) {
                Object content = event.getContent(index);
                if (content == null) {
                    if (eventIDField != null) {
                        for (int allowed : eventIDField.nullable()) {
                            if (allowed == index) {
                                continue loop;
                            }
                        }
                    }
                    return "Event " + Event.getEventName(type) + " parameter of Type: "
                            + event.getType().getClasses().get(index).getSimpleName() + " at Index: " + index
                            + " is required, value may not be empty or NULL.\n";

                } else if (eventIDField != null && eventIDField.sameLength() && content.getClass().isArray()) {
                    if (arrayLength > 0 && arrayLength != Array.getLength(content)) {
                        return "Event " + Event.getEventName(type) + " parameter of Type: "
                                + event.getType().getClasses().get(index).getSimpleName() + " at Index: " + index + " must be of length: "
                                + arrayLength + ".\n";
                    } else {
                        arrayLength = Array.getLength(content);
                    }
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
