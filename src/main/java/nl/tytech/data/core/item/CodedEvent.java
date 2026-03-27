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
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * CodedEvent: Contains the event type and the required parameters.
 *
 * @author Maxim Knepfle
 */
public class CodedEvent implements Serializable {

    private static final int TYPE_PARAMETER_INDEX = 0;

    private static final long serialVersionUID = 7896165758278561410L;

    public static final CodedEvent createUniqueIDEvent(List<CodedEvent> events, EventTypeEnum type, Object[] objects) {

        int uniqueID = 0;
        // Find next unique id for this list
        for (CodedEvent event : events) {
            if (event.getID().intValue() >= uniqueID) {
                uniqueID = event.getID() + 1;
            }
        }
        return new CodedEvent(uniqueID, type, objects);
    }

    @XMLValue
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME) // need type info to recognize classes here
    private ArrayList<Object> parameters = new ArrayList<>();

    @XMLValue
    private Integer id = Item.NONE;

    public CodedEvent() {

    }

    public CodedEvent(CodedEvent other) {

        this.id = other.getID();
        this.parameters = new ArrayList<>(other.parameters);
    }

    public CodedEvent(Integer id, EventTypeEnum type, final Object... contentsArgs) {

        this.id = id;
        this.parameters.add(type);
        if (contentsArgs != null && contentsArgs.length > 0) {
            this.parameters.addAll(Arrays.asList(contentsArgs));
        }
    }

    public CodedEvent(List<Object> event) {
        parameters = new ArrayList<>(event);
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
        CodedEvent other = (CodedEvent) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        return true;
    }

    public Integer getID() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(int i) {
        return (T) parameters.get(i);
    }

    public ArrayList<Object> getParameters() {
        return parameters;
    }

    public EventTypeEnum getType() {

        if (parameters.size() > 0) {
            return (EventTypeEnum) parameters.get(TYPE_PARAMETER_INDEX);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (parameters == null ? 0 : parameters.hashCode());
        return result;
    }

    public void set(CodedEvent event) {
        this.parameters = event.getParameters();
        this.id = event.getID();
    }

    public void setID(Integer id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public void setType(EventTypeEnum type) {

        if (this.parameters.size() > 0 && this.parameters.getFirst() != null) {
            Object obj = this.parameters.getFirst();
            if (obj.toString().equals(type.toString())) {
                this.parameters.set(0, type);
                return;
            }
        }

        if (parameters.size() > 0) {
            parameters.clear();
        }
        parameters.add(type);

        // Prefill with defaults
        EventIDField eventIDField = ObjectUtils.getEnumAnnotation((Enum<?>) type, EventIDField.class);
        List<Integer> paramsList = new ArrayList<>();
        if (eventIDField != null) {
            int[] params = eventIDField.params();
            for (int param : params) {
                paramsList.add(param);
            }
        }

        for (int i = 0; i < type.getClasses().size(); i++) {
            Class<?> parameterClass = type.getClasses().get(i);
            if (parameterClass.equals(Integer.class)) {
                int paramIndex = paramsList.indexOf(i);
                if (paramIndex >= 0) {
                    parameters.add(new ItemID(Item.NONE, eventIDField.links()[paramIndex]));
                } else {
                    parameters.add(Item.NONE);
                }
            } else if (parameterClass.equals(Boolean.class)) {
                parameters.add(true);
            } else if (parameterClass.equals(String.class)) {
                parameters.add(StringUtils.EMPTY);
            } else if (parameterClass.equals(Float.class)) {
                parameters.add(Float.valueOf(0));
            } else if (parameterClass.isEnum()) {
                Enum<?> constant = ((Class<Enum<?>>) parameterClass).getEnumConstants()[0];
                parameters.add(constant);
            } else {
                parameters.add(null);
            }
        }
    }

    @Override
    public String toString() {
        return Event.toString(getType(), parameters.subList(Math.min(parameters.size(), 1), parameters.size()));
    }
}
