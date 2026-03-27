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

import static nl.tytech.core.net.serializable.MapLink.CINEMATIC_DATAS;
import static nl.tytech.core.net.serializable.MapLink.MEASURES;
import static nl.tytech.core.net.serializable.MapLink.NEIGHBORHOODS;
import static nl.tytech.core.net.serializable.MapLink.SCENARIOS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.engine.item.Scenario.LimitType;

/**
 * @author Jeroen Warmerdam
 *
 */
@Linked(SCENARIOS)
public enum EditorScenarioEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add new Scenario.", response = "Scenario ID")
    ADD,

    @EventParamData(desc = "Add empty dummy event that is triggered on Scenario activation.", params = { "Scenario ID" })
    @EventIDField(links = { SCENARIOS }, params = { 0 })
    ADD_EVENT(Integer.class),

    @EventParamData(desc = "Add a Measure that is triggered on Scenario activation.", params = { "Scenario ID", "Measure ID" })
    @EventIDField(links = { SCENARIOS, MEASURES }, params = { 0, 1 })
    ADD_MEASURE(Integer.class, Integer.class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { SCENARIOS, STAKEHOLDERS }, params = { 0, 1 })
    REMOVE_CINEMATIC(Integer.class, Integer.class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    REMOVE_EVENT(Integer.class, Integer.class),

    @EventIDField(links = { SCENARIOS, MEASURES }, params = { 0, 1 })
    REMOVE_MEASURE(Integer.class, Integer.class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Activate Scenario, when testrun is active the Scenerio can restart it.", params = { "Scenario ID" })
    @EventIDField(links = { SCENARIOS }, params = { 0 })
    SET_ACTIVE(Integer.class),

    @EventIDField(links = { SCENARIOS, STAKEHOLDERS, CINEMATIC_DATAS }, params = { 0, 1, 2 })
    SET_CINEMATIC(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    SET_EVENT(Integer.class, CodedEvent.class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { SCENARIOS }, params = { 0 })
    SET_LIMIT_TYPE(Integer.class, LimitType.class),

    @EventIDField(links = { SCENARIOS, NEIGHBORHOODS }, params = { 0, 1 })
    SET_LIMIT_NEIGHBORHOODS(Integer.class, Integer[].class),

    @EventIDField(sameLength = true, links = { SCENARIOS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "When true test run is restarted on Scenario activation. This option will be deprecated in the future and replaced by multiple maps.", params = {
            "Scenario ID", "Restart required" })
    @EventIDField(sameLength = true, links = { SCENARIOS }, params = { 0 })
    SET_RESTART_TESTRUN(Integer[].class, Boolean[].class),

    @EventParamData(params = { "Scenarios", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { SCENARIOS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Scenarios", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { SCENARIOS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Scenarios", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { SCENARIOS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class);

    private final List<Class<?>> classes;

    private EditorScenarioEventType(Class<?>... classes) {
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

        if (this == REMOVE) {
            return Boolean.class;
        }
        return this == ADD ? Integer.class : this == DUPLICATE ? Integer[].class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {

        return switch (this) {
            // can be called from (X Query) TQL thus update
            case ADD, REMOVE, DUPLICATE, SET_NAME -> SCENARIOS;
            default -> null;
        };
    }

}
