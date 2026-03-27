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
import static nl.tytech.core.net.serializable.MapLink.GLOBALS;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.PANELS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item.Timing;
import nl.tytech.data.engine.item.Panel.PanelType;
import nl.tytech.data.engine.item.TemplateExcelPanel.PanelTemplate;
import nl.tytech.data.engine.serializable.PopupModelType;
import nl.tytech.data.engine.serializable.Relation;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(PANELS)
public enum EditorPanelEventType implements IndicatorEventTypeEnum {

    ADD(PanelType.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    ADD_ANSWER(Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    ADD_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class),

    @EventIDField(links = { PANELS, GLOBALS }, params = { 0, 1 })
    ADD_GLOBAL(Integer.class, Integer[].class),

    ADD_TEMPLATE(PanelTemplate.class),

    @EventIDField(links = { OVERLAYS }, params = { 1 })
    ADD_OVERLAY_TEMPLATE(PanelTemplate.class, Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    APPLY_TEMPLATE_PANELS(Integer[].class),

    @EventIDField(links = { PANELS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(sameLength = true, links = { PANELS }, params = { 0 })
    DUPLICATE_ANSWER(Integer[].class, Integer[].class),

    @EventIDField(links = { PANELS }, params = { 0 })
    EDIT_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class, CodedEvent.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    EXPORT_DEBUG_EXCEL(Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    EXPORT_EXCEL_CALL_TREE(Integer.class),

    IMPORT_WIKIPEDIA_POINTS(),

    @EventIDField(links = { PANELS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(sameLength = true, links = { PANELS }, params = { 0 })
    REMOVE_ANSWER(Integer[].class, Integer[].class),

    @EventIDField(links = { PANELS }, params = { 0 })
    REMOVE_ANSWER_EVENT(Integer.class, Integer.class, Boolean.class, Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(links = { PANELS, GLOBALS }, params = { 0, 1 })
    REMOVE_GLOBAL(Integer.class, Integer[].class),

    @EventIDField(links = { PANELS }, params = { 0 })
    REMOVE_OVERLAY(Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_ANSWER_CONTENTS(Integer.class, Integer.class, String.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_ATTENTION(Integer.class, Boolean.class),

    @EventParamData(params = { "Panels", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { PANELS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Panels", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { PANELS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Panels", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { PANELS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(links = { PANELS, EXCEL_SHEETS }, params = { 0, 1 })
    SET_EXCEL(Integer.class, Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_FOR_EVERYONE(Integer.class),

    @EventIDField(links = { PANELS, STAKEHOLDERS }, params = { 0, 1 })
    SET_FOR_STAKEHOLDER(Integer.class, Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_LAYOUT_XY(Integer.class, Double.class, Double.class),

    @EventIDField(sameLength = true, links = { PANELS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(links = { PANELS, OVERLAYS }, params = { 0, 1 })
    SET_OVERLAY(Integer.class, Integer.class),

    @EventIDField(links = { PANELS }, params = { 0 }, nullable = { 1 })
    SET_POINT(Integer.class, Point.class),

    @EventIDField(sameLength = true, links = { PANELS }, params = { 0 })
    SET_EXCEL_UPDATE_TIMING(Integer[].class, Timing[].class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_POPUP_MODEL_TYPE(Integer.class, PopupModelType.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_SIZE(Integer.class, Double.class, Double.class),

    @EventParamData(params = { "Template panels", "Apply automatically at start of session" })
    @EventIDField(links = { PANELS }, params = { 0 })
    SET_TEMPLATE_APPLY_AUTOMATICALLY(Integer[].class, Boolean.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_TEMPLATE_ATTRIBUTE(Integer.class, String.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_TEMPLATE_PANEL_MAPLINK(Integer.class, MapLink.class),

    @EventIDField(links = { PANELS }, params = { 0 }, nullable = { 1 })
    SET_TEMPLATE_RELATION(Integer.class, Relation.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_TEMPLATE_USE_OWNER(Integer.class, Boolean.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_TEXT(Integer.class, String.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_URL(Integer.class, String.class),

    @EventIDField(links = { PANELS }, params = { 0 })
    SET_VISIBLE(Integer.class, Boolean.class);

    private final List<Class<?>> classes;

    private EditorPanelEventType(Class<?>... classes) {
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

        if (this == SET_EXCEL || this == SET_URL || this == ADD_GLOBAL || this == SET_TEXT) {
            return String.class;
        }
        if (this == EXPORT_DEBUG_EXCEL || this == EXPORT_EXCEL_CALL_TREE) {
            return byte[].class;
        }
        if (this == ADD || this == ADD_TEMPLATE || this == ADD_OVERLAY_TEMPLATE) {
            return Integer.class;
        }
        if (this == DUPLICATE) {
            return Integer[].class;
        }
        if (this == REMOVE || this == REMOVE_GLOBAL) {
            return Boolean.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {

        return switch (this) {
            // ignore templates (not in map)
            case SET_TEMPLATE_APPLY_AUTOMATICALLY, SET_TEMPLATE_ATTRIBUTE, SET_TEMPLATE_PANEL_MAPLINK, SET_TEMPLATE_RELATION, SET_TEMPLATE_USE_OWNER -> null;
            // ignore visuals
            case SET_LAYOUT_XY, SET_SIZE -> null;
            default -> PANELS;
        };
    }
}
