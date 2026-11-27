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

import static nl.tytech.core.net.serializable.MapLink.GLOBALS;
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
import nl.tytech.data.core.item.Item.Timing;

/**
 * @author Maxim Knepfle
 *
 */

@Linked(GLOBALS)
public enum EditorGlobalEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new Global with a default name", response = "Global")
    ADD(),

    @EventParamData(desc = "Add a new Global with a TQL Query", params = { "TQL Query" }, response = "Global")
    ADD_QUERY(String.class),

    @EventIDField(links = { GLOBALS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { GLOBALS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove the Book Value of a Global", params = { "Global" })
    @EventIDField(links = { GLOBALS }, params = { 0 })
    REMOVE_BOOK_VALUE(Integer.class),

    @EventIDField(links = { GLOBALS }, params = { 0 })
    REMOVE_POINT(Integer.class),

    @EventParamData(desc = "Assign the Global as a Book Value for a Stakeholder", params = { "Global", "Stakeholder" })
    @EventIDField(links = { GLOBALS, STAKEHOLDERS }, params = { 0, 1 })
    SET_BOOK_VALUE(Integer.class, Integer.class),

    @EventParamData(desc = "Set the name of a Global", params = { "Globals", "Names" })
    @EventIDField(sameLength = true, links = { GLOBALS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the position of a Global", params = { "Global", "Point" })
    @EventIDField(links = { GLOBALS }, params = { 0 })
    SET_POINT(Integer.class, Point.class),

    @EventParamData(desc = "Set the calculation Timing of a TQL Query Global", params = { "Globals", "Timings" })
    @EventIDField(sameLength = true, links = { GLOBALS }, params = { 0 })
    SET_QUERY_TIMING(Integer[].class, Timing[].class),

    @EventParamData(desc = "Set the TQL Query of a Global", params = { "Global", "TQL Query" })
    @EventIDField(links = { GLOBALS }, params = { 0 })
    SET_QUERY(Integer.class, String.class),

    @EventParamData(desc = "Set the initial values of a Global", params = { "Globals", "Array of values" })
    @EventIDField(sameLength = true, links = { GLOBALS }, params = { 0 })
    SET_START_VALUE(Integer[].class, double[][].class),

    @EventParamData(desc = "Set the name of a Global shown in the 3D Visualization", params = { "Global", "Name" })
    @EventIDField(links = { GLOBALS }, params = { 0 })
    SET_VISUALISATION_NAME(Integer.class, String.class);

    private final List<Class<?>> classes;

    private EditorGlobalEventType(Class<?>... classes) {
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

            case ADD, ADD_QUERY -> Integer.class;

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
        return GLOBALS;
    }
}
