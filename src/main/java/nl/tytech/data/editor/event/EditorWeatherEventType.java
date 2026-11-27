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

    @EventParamData(desc = "Add a new Weather with a Weather Type Effect", params = { "Weather Type Effect" }, response = "Weather ID")
    ADD(WeatherTypeEffect.class),

    @EventParamData(desc = "Duplicate Weathers", params = { "Weathers" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Remove Weathers", params = { "Weathers" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove specified Attributes a Weather, including default attributes", params = { "Weathers",
            "Attributes to remove" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove the visualization Overlay from a Weather", params = { "Weathers" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    REMOVE_FLOOD_OVERLAY(Integer[].class),

    @EventParamData(params = { "Weathers", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { WEATHERS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Weathers", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { WEATHERS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Weathers", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { WEATHERS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set the duration of the Weather's visualization", params = { "Weather",
            "Duration of visualization in seconds" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    SET_DURATION(Integer.class, Double.class),

    @EventParamData(desc = "Set the Weather Type Effect of a Weather", params = { "Weather", "Weather Type Effect" })
    @EventIDField(links = { WEATHERS }, params = { 0 })
    SET_EFFECT(Integer.class, WeatherTypeEffect.class),

    @EventParamData(desc = "Set the name of a Weather", params = { "Weathers", "Names" })
    @EventIDField(sameLength = true, links = { WEATHERS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set a specific visualization Overlay for a Weather", params = { "Weathers", "Water Overlays" })
    @EventIDField(sameLength = true, links = { WEATHERS, OVERLAYS }, params = { 0, 1 })
    SET_FLOOD_OVERLAY(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the trigger time for the specified Weather's visualization", params = { "Weather",
            "Auto-trigger time in seconds" })
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

        return switch (this) {

            case ADD -> Integer.class;

            case DUPLICATE -> Integer[].class;

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

        // visual only
        return switch (this) {
            case SET_EFFECT, SET_DURATION, SET_TRIGGER_SEC -> null;
            default -> WEATHERS;
        };
    }
}
