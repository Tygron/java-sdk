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

import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.ClassDescription;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;

/**
 * EditorEventType: EditorEventType defines all events related to the Editor
 *
 * @author Maxim Knepfle
 */
@ClassDescription("Generic Editor events for creating a Project and updating.")
public enum EditorEventType implements EventTypeEnum {

    /**
     * When true activate the testrun and when false restore to previous point.
     */
    @EventParamData(desc = "Start or stop a Scenario Testrun.", params = { "Start the Scenario Testrun (false means stop)",
            "DEPRECATED (history graph)" }, hidden = 1)
    @EventIDField(nullable = { 1 })
    ACTIVATE_TESTRUN(Boolean.class, Boolean.class),

    /**
     * Delete entire map set true to delete all (e.g. dimensions and do not fire events)
     */
    @EventParamData(desc = "Remove all geospatial data of the Project, such as buildings, areas, etc.", params = {
            "Delete the map size and georeference too (false means deleting only the geodata)", })
    CLEAR_MAP(Boolean.class),

    /**
     * Set the initial (empty) map size before adding stuff to it
     */
    @EventParamData(desc = "Set the size of the map. This can only be done once.", params = { "Width of the map", "Height of the map", })
    SET_INITIAL_MAP_SIZE(Integer.class, Integer.class),

    @EventParamData(desc = "Set the Project's georeference center point to the provided coordinates and begin the process of automatically load in available data.", params = {
            "Center point (Longitude or X coordinate)", "Center point (Latitude or Y coordinate)",
            "Optional: Limit data loading to this Polygon", "Optional: Area of Interest Polygons" })
    @EventIDField(nullable = { 2, 3 })
    START_MAP_CREATION(Double.class, Double.class, MultiPolygon.class, MultiPolygon[].class),

    @EventParamData(desc = "Waits for update of all active overlays, indicators, panels and triggers. "
            + "Note: for long-running updates use: set_scheduled_update/ to schedule an update.", params = {
                    "Reset all X-Queries (optional)", }, defaults = { "false", })
    @EventIDField(nullable = { 0 })
    UPDATE(Boolean.class),

    @EventParamData(desc = "Waits for update of all active overlays, indicators, panels and triggers with unique ID. "
            + "Note: for long-running updates use: set_scheduled_update/ to schedule an update.", params = { "Reset all X-Queries",
                    "Unique Update ID to prevent multiple updates" })
    UPDATE_WITH_ID(Boolean.class, Long.class),

    @EventParamData(desc = "Schedule a long-running update of all active overlays, indicators, panels and triggers.", params = {
            "Scheduled time in milliseconds since epoch 1970. Note: use 0 for direct execution or -1 to cancel",
            "Send Email to this address when finished (optional)",
            "Send SMS to this number when finished (optional)" }, defaults = { "0", "", "" })
    @EventIDField(nullable = { 0, 1, 2 })
    SET_SCHEDULED_UPDATE(Long.class, String.class, String.class);

    private final List<Class<?>> classes;

    private EditorEventType(Class<?>... classes) {
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

        if (UPDATE == this || UPDATE_WITH_ID == this) {
            return Boolean.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
