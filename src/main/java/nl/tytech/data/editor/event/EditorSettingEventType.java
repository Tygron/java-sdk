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

import static nl.tytech.core.net.serializable.MapLink.SETTINGS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.GridOverlay.RasterizationMethod;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.serializable.PanelEnum;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystemType;

/**
 * @author Frank Baars, Jeroen Warmerdam
 *
 */
@Linked(SETTINGS)
public enum EditorSettingEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Set a CRS to the recently used CRSs array", params = { "EPSG CRS Code" })
    ADD_CRS(String.class),

    @EventParamData(desc = "Remove a CRS from the recently used CRSs array", params = { "EPSG CRS Code" })
    REMOVE_CRS(String.class),

    @EventParamData(desc = "Set which the Currency is displayed in the interface", params = { "Number of decimal places" })
    SET_CURRENCY(TCurrency.class),

    @EventParamData(desc = "Set if AI Agents are allowed to edit Project data", params = { "True for allow, false to disable editing" })
    SET_AI_EDITING(Boolean.class),

    @EventParamData(desc = "Set if TQL Queries are allowed access the API Token", params = {
            "True for allow, false to disable TQL API Tokens" })
    SET_TQL_TOKEN(Boolean.class),

    @EventParamData(desc = "Set how many decimal places Attributes are allowed to have", params = { "Number of decimal places" })
    SET_DECIMALS(Integer.class),

    @Deprecated
    @EventParamData(desc = "Deprecated: replaced by editorevent/set_scheduled_update/. Schedule a long-running Update", params = {
            "Scheduled time in milliseconds since epoch 1970 (0 for direct execution, -1 to cancel)",
            "Send Email to this address when finished (optional)",
            "Send SMS to this number when finished (optional)" }, defaults = { "0", "", "" })
    @EventIDField(nullable = { 0, 1, 2 })
    SET_SCHEDULED_UPDATE(Long.class, String.class, String.class),

    @EventParamData(desc = "Set the Default Export CRS, used in Item Export Panels", params = { "EPSG CRS Code" })
    SET_EXPORT_CRS(String.class),

    @EventParamData(desc = "Set the Default Import CRS, used in the Geo Import Wizard", params = { "EPSG CRS Code" })
    SET_IMPORT_CRS(String.class),

    @EventParamData(desc = "Set the Timestamp format for Dates and Times, used for displaying Dates and Times", params = {
            "Date-time format" })
    SET_TIMESTAMP_FORMAT(String.class),

    @EventParamData(desc = "Set the Default Geo Plugin of a MapLink, used in the Geo Import Wizard", params = { "MapLink",
            "Geo Plugin ID" })
    SET_DEFAULT_GEOPLUGIN(MapLink.class, Integer.class),

    @EventParamData(desc = "Set the Dates between which the Sun movement is interpolated in the 3D Visualization", params = {
            "Array of times in milliseconds since epoch (Milliseconds since 00:00:00 UTC on 1 January 1970)" })
    SET_SUN_DATES(long[].class),

    @EventParamData(desc = "Change the Project's grid cell size, used by active Overlays", params = {
            "Grid cell size in meters" }, response = "Value is updated")
    SET_GRID_CELL_SIZE(Double.class),

    @EventParamData(desc = "Restrict which Panels are active or inactive in a Project", params = { "Panel Enum", "Active" })
    SET_PANEL_AVAILABILITY(PanelEnum.class, Boolean.class),

    @EventParamData(desc = "Set whether permission Popups for spatial actions are active in a Project", params = {
            "Permission Popup Active" })
    SET_PERMISSION_POPUP_ACTIVE(Boolean.class),

    @EventParamData(desc = "Set the Region of a Project", params = { "Region" })
    SET_REGION(Region.class),

    @EventParamData(desc = "Set the rasterization method used by the Rasterizers in a Project", params = { "Rasterization Method" })
    SET_RASTERIZATION(RasterizationMethod.class),

    @EventParamData(desc = "Set Project wide Setting", params = { "Setting Type",
            "Value as String (can be an integer, boolean, text, etc.)" })
    SET_SETTING(Setting.Type.class, String.class),

    @EventParamData(desc = "Set the Unit System of a Project, used in the interface", params = { "Unit System Type" })
    SET_UNIT_SYSTEM_TYPE(UnitSystemType.class),

    @EventParamData(desc = "Set the Wind Speed", params = { "Wind Speed (m/s)" })
    SET_WIND_SPEED(Double.class),

    @EventParamData(desc = "Set Project Map Limit Area", params = { "Limit Polygon" })
    SET_LIMIT_MAP(MultiPolygon.class),

    @EventParamData(desc = "Manually finish the Geo Wizard process. (Triggered automatically at the end of the New Project Wizard.)")
    WIZARD_FINISHED(),

    @EventParamData(desc = "Get Sun Azimuth value for local time in this Project", params = { "Year", "Month", "Day", "Hour", "Minute",
            "Second" }, defaults = { "2013", "8", "2", "12", "0", "0" }, response = "Sun Azimuth value")
    GET_SUN_AZIMUTH(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Get Sun Altitude value for local time in this Project", params = { "Year", "Month", "Day", "Hour", "Minute",
            "Second" }, defaults = { "2013", "8", "2", "12", "0", "0" }, response = "Sun Altitude value")
    GET_SUN_ALTITUDE(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Get Sun Motion value for local time in this Project (The Netherlands only)", params = { "Year", "Month", "Day",
            "Hour", "Minute", "Second" }, defaults = { "2013", "8", "2", "12", "0", "0" }, response = "Sun Motion value")
    GET_SUN_MOTION(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Get UTC Epoch Time (Milliseconds since 00:00:00 UTC on 1 January 1970) for local time in this Project", params = {
            "Year", "Month", "Day", "Hour", "Minute", "Second" }, defaults = { "2005", "9", "1", "0", "0",
                    "0" }, response = "UTC Epoch Time (Milliseconds since 00:00:00 UTC on 1 January 1970)")
    GET_EPOCH_TIME(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorSettingEventType(Class<?>... classes) {

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

            case GET_SUN_AZIMUTH, GET_SUN_ALTITUDE, GET_SUN_MOTION -> Double.class;

            case GET_EPOCH_TIME -> Long.class;

            case SET_GRID_CELL_SIZE -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        // only some change indicators
        return this == SET_WIND_SPEED || this == SET_GRID_CELL_SIZE || this == SET_LIMIT_MAP ? SETTINGS : null;
    }

}
