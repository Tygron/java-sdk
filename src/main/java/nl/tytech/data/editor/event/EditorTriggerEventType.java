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
package nl.tytech.data.editor.event;

import static nl.tytech.core.net.serializable.MapLink.TRIGGERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item.Timing;
import nl.tytech.data.engine.item.Trigger;

/**
 *
 * @author Maxim Knepfle
 *
 */
@Linked(TRIGGERS)
public enum EditorTriggerEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new Trigger with a particular Timing", params = { "Timing" }, response = "Trigger ID")
    @EventIDField(nullable = { 0 })
    ADD(Trigger.Timing[].class),

    @EventIDField(links = { TRIGGERS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { TRIGGERS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Set whether a Trigger is active", params = { "Triggers", "Active Array" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set the name of an Trigger", params = { "Triggers", "Names" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the Request Method of a Trigger", params = { "Triggers", "Request Methods" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_METHOD(Integer[].class, Trigger.Method[].class),

    @EventParamData(desc = "Set the calculation Timing of a Trigger", params = { "Triggers", "Timings" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_TIMING(Integer[].class, Timing[].class),

    @EventParamData(desc = "Add the Trigger Type of a Trigger", params = { "Triggers", "Trigger Types" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_TYPE(Integer[].class, Trigger.Type[].class),

    @EventParamData(desc = "Add the URL of a Trigger", params = { "Triggers", "URLs" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_URL(Integer[].class, String[].class),

    @EventParamData(desc = "Set the timeout of a Trigger", params = { "Triggers", "Array of Timeouts in seconds" })
    @EventIDField(sameLength = true, links = { TRIGGERS }, params = { 0 })
    SET_TIMEOUT(Integer[].class, Integer[].class);

    private final List<Class<?>> classes;

    private EditorTriggerEventType(Class<?>... classes) {
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

    @Override
    public Class<?> getResponseClass(Object[] args) {

        return switch (this) {

            case ADD, DUPLICATE -> Integer[].class;

            case REMOVE -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return TRIGGERS;
    }
}
