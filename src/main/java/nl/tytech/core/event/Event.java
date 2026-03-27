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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.StringUtils;

/**
 * Event
 *
 * Basic event object. It has a source, priority (used by server to facilitate the important clients) and contents. The contents can be one
 * or multiple objects. The event takes a EventType to identify itself.
 *
 * @author Jeroen Warmerdam & Maxim Knepfle
 */
public class Event implements Serializable {

    /**
     * Enum defining the type of the event. All used enums should implement this.
     */
    public interface EventTypeEnum {

        /**
         * When true this event can be placed inside CodedEvents in XML files before the session is started. NOTE: when true this Enum must
         * be inside the Item Namespace!
         * @return
         */
        public boolean canBePredefined();

        /**
         * Returns the list of classes that the event expects (used for event content checking).
         *
         * @return List<Class<?>>
         */
        public List<Class<?>> getClasses();

        /**
         * When not NULL we expect this class type back
         * @return
         */

        public Class<?> getResponseClass(Object[] args);

        /**
         * When true this event must be fired server side, false means it's an client event, that must be fired clientside.
         * @return
         */
        public boolean isServerSide();

        /**
         * Enum name
         * @return
         */
        public String name();
    }

    /**
     * These events can trigger an Indicator Update
     */
    public interface IndicatorEventTypeEnum extends EventTypeEnum {

        /**
         * When true this event triggers a indicator update for this MapLink
         * @return
         */
        public MapLink triggerUpdate(Event event);

    }

    /**
     * These events can cause the session to trigger a test run when running in editor.
     */
    public interface SessionEventTypeEnum extends IndicatorEventTypeEnum {

        /**
         * When true this event triggers a test run in editor mode.
         */
        public boolean triggerTestRun();

    }

    /**
     * When this interface is implemented the event always starts with his personal stakeholder ID as first content value! This values can
     * be Overridden.
     *
     */
    public interface StartWithMyStakeholderEvent {

    }

    public static final String NOT_ALLOWED = "Not allowed to execute event: ";

    /**
     * Serial
     */
    private static final long serialVersionUID = -2306024519680983784L;

    private static final String[] EXTS = new String[] { "ServiceEventType", "EventType", "Event" };

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Object getDefault(EventTypeEnum type, int index) {

        EventParamData eventParamData = ObjectUtils.getEnumAnnotation((Enum<?>) type, EventParamData.class);
        if (eventParamData != null && eventParamData.defaults().length > index
                && StringUtils.containsData(eventParamData.defaults()[index])) {

            Class<?> classz = type.getClasses().get(index);
            String v = eventParamData.defaults()[index];

            if (classz == Integer.class) {
                return Integer.parseInt(v);
            } else if (classz == Long.class) {
                return Long.parseLong(v);
            } else if (classz == Float.class) {
                return Float.parseFloat(v);
            } else if (classz == Double.class) {
                return Double.parseDouble(v);
            } else if (classz == String.class) {
                return v;
            } else if (classz == String[].class) {
                return StringUtils.split(v);
            } else if (classz == Boolean.class) {
                return Boolean.parseBoolean(v);
            } else if (classz.isEnum()) {
                return Enum.valueOf((Class<Enum>) classz, v);
            }
        }
        return null;
    }

    public static String getEventName(EventTypeEnum eventType) {
        return toSimpleName(eventType.getClass()).toLowerCase() + "/" + eventType.name().toLowerCase();
    }

    /**
     * Provide appropriate response format depending on response class.
     */
    public static final Format getResponseFormat(EventTypeEnum eventType, Object[] args) {

        Class<?> r = eventType.getResponseClass(args);
        if (r == null) {
            return Format.DEFAULT_EVENT; // no response
        }
        if (r == String.class || r == Boolean.class || Number.class.isAssignableFrom(r)) {
            return Format.DEFAULT_EVENT; // single number, boolean or string response
        }
        if (r.isArray() && Number.class.isAssignableFrom(r.getComponentType())) {
            return Format.DEFAULT_EVENT; // number array
        }
        return Format.DEFAULT_ITEMS; // response contains more complex data
    }

    public static final boolean isDim3(EventTypeEnum type, int index) {

        EventParamData eventParamData = ObjectUtils.getEnumAnnotation((Enum<?>) type, EventParamData.class);
        if (eventParamData != null) {
            for (int i = 0; i < eventParamData.dim3().length; i++) {
                if (eventParamData.dim3()[i] == index) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final String toSimpleName(Class<?> eventClass) {

        String name = eventClass.getSimpleName();
        for (String ext : EXTS) {
            int index = name.lastIndexOf(ext);
            if (index > 0) {
                name = name.substring(0, index);
                break;
            }
        }
        return name;
    }

    public static String toString(EventTypeEnum eventType, List<Object> contents) {

        StringBuilder builder = new StringBuilder();
        builder.append(getEventName(eventType));
        builder.append(" { ");

        if (eventType == UserServiceEventType.SET_USER_PASSWD) {
            builder.append("XXX"); // never show this

        } else {
            for (int i = 0; i < contents.size(); i++) {
                Object content = contents.get(i);
                if (content != null && content.getClass().isArray()) {
                    builder.append("[");
                    builder.append(StringUtils.arrayToHumanString(content, StringUtils.EMPTY));
                    builder.append("]");
                } else {
                    builder.append(content);
                }
                if (i < contents.size() - 1) {
                    builder.append(", ");
                }
            }
        }

        builder.append(" }");
        return builder.toString();
    }

    /**
     * The event type. To identify the event. Can be any kind of enumeration.
     */
    private final EventTypeEnum type;

    /**
     * List carrying the contents of the event.
     */
    private final List<Object> contents;

    /**
     * Optional response to event
     */
    private Object response = null;

    /**
     * Construct an event
     *
     * @param type Type of event
     * @param contents Contents can be multiple objects.
     */
    public Event(EventTypeEnum type, final Object... args) {

        this.type = type;
        this.contents = new ArrayList<>();
        List<Class<?>> classes = type.getClasses();

        for (int i = 0; i < classes.size(); i++) {
            // use a provided non-null argument
            if (args.length > i && args[i] != null) {
                Object arg = args[i];
                Class<?> classz = classes.get(i);
                if (arg instanceof ItemID item) {
                    contents.add(item.getID()); // convert ItemID to Integer
                } else if (arg instanceof Float f && classz.equals(Double.class)) {
                    contents.add(f.floatValue()); // convert float to double
                } else if (arg instanceof Double d && classz.equals(double[].class)) {
                    contents.add(ObjectUtils.toPrimitiveArray(d)); // convert Double to double[]
                } else if (arg != null && !arg.getClass().isArray() && classz.isArray()) {
                    contents.add(ObjectUtils.toObjectArray(arg)); // convert single Object to Object Array
                } else {
                    contents.add(arg); // just add
                }
            } else {
                // fallback to default argument or NULL
                contents.add(getDefault(type, i));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        if (contents == null) {
            if (other.contents != null) {
                return false;
            }
        } else if (!contents.equals(other.contents)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /**
     * @return the first entry
     */
    public <T> T getContent() {
        return this.<T> getContent(0);
    }

    /**
     * Get the content of the event
     *
     * @param <T>
     * @param i Index
     * @return Object at i-th place
     */
    @SuppressWarnings("unchecked")
    public <T> T getContent(int i) {
        return i < contents.size() ? (T) contents.get(i) : null;
    }

    /**
     * @return the content
     */
    public Object[] getContents() {
        return contents.toArray();
    }

    public String getEventName() {
        return getEventName(getType());
    }

    /**
     * @return the response
     */
    @SuppressWarnings("unchecked")
    public final <T> T getResponse() {
        return (T) this.response;
    }

    /**
     * @return the type
     */
    public EventTypeEnum getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (contents == null ? 0 : contents.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        return result;
    }

    /**
     * Set event response
     */
    public final void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return toString(type, contents);
    }
}
