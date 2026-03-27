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
package nl.tytech.data.engine.event;

import static nl.tytech.core.net.serializable.MapLink.ACTION_MENUS;
import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.INDICATORS;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.PANELS;
import static nl.tytech.core.net.serializable.MapLink.WEATHERS;
import static nl.tytech.core.net.serializable.MapLink.ZONES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.data.engine.serializable.PanelEnum;

/**
 * This class has been placed here so that it can be easily located for the xml serialization.
 *
 * @author Jeroen Warmerdam, Frank Baars
 */
public enum ClientEventType implements EventTypeEnum {

    @EventParamData(editor = true, desc = "Weather to show", params = { "Weather ID" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    ACTIVATE_WEATHER(Integer.class),

    @EventParamData(editor = true, desc = "Blink ActionMenu in left menu panel", params = { "ActionMenu", "Blinking on" })
    @EventIDField(links = { ACTION_MENUS }, params = { 0 })
    BLINK_CATEGORY(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Blink Indicator icon in top menu", params = { "Indicator ID" })
    @EventIDField(links = { INDICATORS }, params = { 0 })
    BLINK_INDICATOR(Integer.class),

    @EventParamData(editor = true, desc = "Blink Overlay icon below map panel", params = { "Overlay ID" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    BLINK_OVERLAY(Integer.class),

    @EventParamData(editor = true, desc = "Stop all blinking Indicators and ActionMenus", params = {})
    BLINK_STOP_ALL(),

    @EventParamData(editor = true, desc = "Change a zoning permit for a building, providing the function of the building and the permission feedback", params = {
            "Zone ID", "Function ID", "Permission feedback" })
    @EventIDField(links = { ZONES, FUNCTIONS }, params = { 0, 1 })
    CHANGE_ZONING_PERMIT(Integer[].class, Integer.class, String[].class),

    @EventParamData(editor = true, desc = "Hide the feedback text", params = {})
    FEEDBACK_PANEL_HIDE_TEXT(),

    @EventParamData(editor = true, desc = "Text to show", params = { "Text" })
    FEEDBACK_PANEL_SHOW_TEXT(String.class),

    @EventParamData(editor = true, desc = "Warning to show", params = { "Warning" })
    FEEDBACK_PANEL_SHOW_WARNING(String.class),

    @EventParamData(editor = false, desc = "Goto start location", params = {})
    @Deprecated
    GOTO_START_LOCATION(),

    @EventParamData(editor = true, desc = "Respond to a Panel with chosen answer", params = { "Panel ID", "Answer ID" })
    @EventIDField(links = { PANELS }, params = { 0 })
    PANEL_ANSWER(Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Respond to a popup with a given change with the first answer of that popup", params = {
            "Chance to respond between 0 and 1" })
    RANDOM_ACTIVATE_POPUP_PERCENTAGE(Double.class),

    @EventParamData(editor = true, desc = "Change the visualisation speed", params = { "Speed factor" })
    SET_VISUALISATION_SPEED(Double.class),

    @EventParamData(editor = true, desc = "Show the browser for ESRI Layers applicable to the city area", params = {})
    SHOW_BROWSER_MAP,

    @EventParamData(editor = true, desc = "Show a specific panel", params = { "Panel ID", "Visible" })
    @EventIDField(links = { PANELS }, params = { 0 })
    SHOW_CUSTOM_PANEL(Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Show a specific overlay", params = { "Overlay ID", "Timeframe", "Play Timeframes" }, defaults = {
            "", "0", "false" })
    @EventIDField(links = { OVERLAYS }, params = { 0 })
    SHOW_OVERLAY(Integer.class, Integer.class, Boolean.class),

    @EventParamData(editor = true, desc = "Set a particular panel visible/invisible", params = { "Particular panel", "Visible" })
    SHOW_PANEL(PanelEnum.class, Boolean.class),

    @EventParamData(editor = true, desc = "Stop any weather that is visually active on a client", params = {})
    STOP_WEATHER(),

    @EventParamData(editor = true, desc = "Gives visual attention for a given point and amount of seconds", params = { "Point in the city",
            "Amount of seconds" })
    TILE_ATTENTION(Point.class, Double.class);

    private final List<Class<?>> classes;

    private ClientEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return true;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {
        return null;
    }

    @Override
    public boolean isServerSide() {
        return false;
    }

}
