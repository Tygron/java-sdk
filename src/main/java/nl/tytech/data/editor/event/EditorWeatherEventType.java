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

import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.WEATHERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Weather.WeatherTypeEffect;

/**
 * Edit weather in Simulation
 * @author Maxim Knepfle
 *
 */
@Linked(WEATHERS)
public enum EditorWeatherEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new weather of a specified type.", params = { "Weather Type Effect" }, response = "Weather ID")
    ADD(WeatherTypeEffect.class),

    @EventParamData(desc = "Create a copy of each specified weather. The copies will have the same settings and attributes, but can be edited independently of the originals.", params = {
            "Weather IDs" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Remove the specified weathers.", params = { "Weather IDs" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove the specified attributes of the specified weather. The attributes, including attributes which are added to a weather by default, are removed entirely.", params = {
            "Weather ID", "Attributes to remove" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove the specified visualization overlay from specified weather.", params = { "Weather ID" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    REMOVE_FLOOD_OVERLAY(Integer[].class),

    @EventParamData(params = { "Weathers", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { WEATHERS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Weathers", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { WEATHERS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Weathers", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { WEATHERS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set how long the specified weather's visualization lasts when it is triggered.", params = { "Weather ID",
            "Duration of visualization in seconds" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    SET_DURATION(Integer.class, Double.class),

    @EventParamData(desc = "Set what kind of weather the specified weather is.", params = { "Weather ID", "Weather Type Effect" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    SET_EFFECT(Integer.class, WeatherTypeEffect.class),

    @EventParamData(desc = "Set the name for a specified weather.", params = { "Weather ID", "Name for the weather" })
    @EventIDField(sameLength = true, links = { WEATHERS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set a specific visualization overlay for a specified weather.", params = { "Weather ID",
            "Overlay ID allowed for weather visualization input" })
    @EventIDField(sameLength = true, links = { WEATHERS, OVERLAYS }, params = { 0, 1 })
    SET_FLOOD_OVERLAY(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set whether the specified weather's visualization triggers automatically.", params = { "Weather ID",
            "Auto-trigger (false means the weather's visualization does not trigger automatically)" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    SET_TRIGGER_SEC(Integer.class, Double.class);

    private final List<Class<?>> classes;

    private EditorWeatherEventType(Class<?>... classes) {
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

        if (this == ADD) {
            return Integer.class;
        }
        if (this == DUPLICATE) {
            return Integer[].class;
        }
        if (this == REMOVE) {
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

        // visual only
        return switch (this) {
            case SET_EFFECT, SET_DURATION, SET_TRIGGER_SEC -> null;
            default -> WEATHERS;
        };
    }
}
