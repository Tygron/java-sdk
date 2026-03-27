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
 * @author Jeroen Warmerdam
 *
 */
@Linked(SETTINGS)
public enum EditorSettingEventType implements IndicatorEventTypeEnum {

    ADD_CRS(String.class),

    REMOVE_CRS(String.class),

    SET_CURRENCY(TCurrency.class),

    SET_DECIMALS(Integer.class),

    @Deprecated
    @EventParamData(desc = "Deprcated: replaced by editorevent/set_scheduled_update/. Schedule a long-running Update.", params = {
            "Scheduled time in milliseconds since epoch 1970. Note: use 0 for direct execution or -1 to cancel",
            "Send Email to this address when finished (optional)",
            "Send SMS to this number when finished (optional)" }, defaults = { "0", "", "" })
    @EventIDField(nullable = { 0, 1, 2 })
    SET_SCHEDULED_UPDATE(Long.class, String.class, String.class),

    SET_EXPORT_CRS(String.class),

    SET_IMPORT_CRS(String.class),

    SET_TIMESTAMP_FORMAT(String.class),

    SET_DEFAULT_GEOPLUGIN(MapLink.class, Integer.class),

    SET_SUN_DATES(long[].class),

    @EventParamData(desc = "Change the project's grid cell size, used by active overlays.", params = {
            "The new grid cell size in meters." }, response = "Value is updated")
    SET_GRID_CELL_SIZE(Double.class),

    SET_PANEL_AVAILABILITY(PanelEnum.class, Boolean.class),

    SET_PERMISSION_POPUP_ACTIVE(Boolean.class),

    /**
     * Set region, e.g. Asia.
     */
    SET_REGION(Region.class),

    SET_RASTERIZATION(RasterizationMethod.class),

    @EventParamData(desc = "Set Project setting", params = { "Setting Type", "Value as String (can be a integer, boolean, text, etc.)" })
    SET_SETTING(Setting.Type.class, String.class),

    SET_UNIT_SYSTEM_TYPE(UnitSystemType.class),

    SET_WIND_SPEED(Double.class),

    @EventParamData(desc = "Set Project Map Limit Area", params = { "Limit Polygon" })
    SET_LIMIT_MAP(MultiPolygon.class),

    /**
     * Triggered at the end of the wizard.
     */
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

        switch (this) {
            case GET_SUN_AZIMUTH:
            case GET_SUN_ALTITUDE:
            case GET_SUN_MOTION:
                return Double.class;
            case GET_EPOCH_TIME:
                return Long.class;
            case SET_GRID_CELL_SIZE:
                return Boolean.class;
            default:
                return null;
        }
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
