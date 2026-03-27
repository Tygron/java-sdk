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

import static nl.tytech.core.net.serializable.MapLink.EXCEL_SHEETS;
import static nl.tytech.core.net.serializable.MapLink.INDICATORS;
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
import nl.tytech.data.core.item.Item.Timing;
import nl.tytech.util.color.TColor;

/**
 * @author Jeroen Warmerdam, Frank Baars
 */

@Linked(INDICATORS)
public enum EditorIndicatorEventType implements IndicatorEventTypeEnum {

    @EventIDField()
    ADD(),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @Deprecated
    @EventParamData(desc = "Use DUPLICATE instead for alsmost (no stakeholder option) identical result.", params = { "Indicator",
            "Stakeholder" })
    @EventIDField(links = { INDICATORS }, params = { 0 })
    DUPLICATE_INDICATOR(Integer.class, Integer.class),

    EXCEL_MIN_LOG_TIME(Long.class),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    EXPORT_DEBUG_EXCEL(Integer.class),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    EXPORT_EXCEL_CALL_TREE(Integer.class),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    REMOVE_STAKEHOLDER(Integer.class),

    @Deprecated
    @EventParamData(desc = "Replaced by editor/update/", params = { "Reset all X-Queries (optional)", }, defaults = { "false", })
    @EventIDField(nullable = { 0 })
    RESET_INDICATORS(Boolean.class),

    @Deprecated
    @EventParamData(desc = "Replaced by editor/update_with_id/", params = { "Reset all X-Queries",
            "Unique Update ID to prevent multiple updates" })
    RESET_INDICATORS_WITH_ID(Boolean.class, Long.class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_ABSOLUTE(Integer[].class, Boolean[].class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(params = { "Indicators", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { INDICATORS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Indicators", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { INDICATORS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Indicators", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { INDICATORS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_COLOR(Integer[].class, TColor[].class),

    @EventIDField(links = { INDICATORS }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { INDICATORS, EXCEL_SHEETS }, params = { 0, 1 })
    SET_EXCEL(Integer.class, Integer.class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_UPDATE_TIMING(Integer[].class, Timing[].class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set a parent Indicator for a specified Indicator. This will cause the specified Indicator to be a subselectable Indicator of the parent. It is not possible to set an Indicator which has a parent itself as a parent of another Indicator.", params = {
            "Indicator ID", "Indicator ID of parent (indicators without a parent should have their parent Indicator ID set to -1)" })
    @EventIDField(links = { INDICATORS }, params = { 0 })
    SET_PARENT(Integer.class, Integer.class),

    @EventIDField(sameLength = true, links = { INDICATORS }, params = { 0 })
    SET_SHORT_NAME(Integer[].class, String[].class),

    @EventIDField(links = { INDICATORS, STAKEHOLDERS }, params = { 0, 1 })
    SET_STAKEHOLDER(Integer.class, Integer.class),

    @EventParamData(desc = "Set a target value of an Indicator for a Sceanrio. A target index value is required.", params = { "Scenario ID",
            "Indicator ID", "Target index", "Target value" })
    @EventIDField(links = { SCENARIOS, INDICATORS }, params = { 0, 1 })
    SET_TARGET(Integer.class, Integer.class, Integer.class, Double.class),

    @EventIDField(sameLength = true, links = { INDICATORS, INDICATORS }, params = { 0, 1 })
    SWAP_ORDER(Integer[].class, Integer[].class);

    private final List<Class<?>> classes;

    private EditorIndicatorEventType(Class<?>... classes) {
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

        if (RESET_INDICATORS == this || RESET_INDICATORS_WITH_ID == this || EXCEL_MIN_LOG_TIME == this) {
            return Boolean.class;
        }
        if (this == EXPORT_DEBUG_EXCEL || this == EXPORT_EXCEL_CALL_TREE) {
            return byte[].class;
        }
        if (this == REMOVE) {
            return Boolean.class;
        }
        if (this == DUPLICATE) {
            return Integer[].class;
        }
        return this == ADD ? Integer.class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {

        return switch (this) {
            case SET_IMAGE, SET_PARENT, SWAP_ORDER, SET_DESCRIPTION, SET_ABSOLUTE -> null; // ignore visuals only
            case EXPORT_DEBUG_EXCEL, EXPORT_EXCEL_CALL_TREE, RESET_INDICATORS, RESET_INDICATORS_WITH_ID -> null; // ignore no effects
            default -> INDICATORS;
        };
    }
}
