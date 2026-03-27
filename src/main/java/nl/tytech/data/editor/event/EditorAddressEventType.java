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

import static nl.tytech.core.net.serializable.MapLink.ADDRESSES;
import static nl.tytech.core.net.serializable.MapLink.BUILDINGS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;

/**
 * Events related to editing buildings.
 * @author Maxim Knepfle
 *
 */
@Linked(ADDRESSES)
public enum EditorAddressEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Building address belong to", params = { "Building", "Street name" })
    @EventIDField(sameLength = true, links = { BUILDINGS }, params = { 0 })
    ADD(Integer[].class, String[].class),

    @EventIDField(links = { ADDRESSES }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { ADDRESSES }, params = { 0 })
    GET_BUILDING(Integer[].class),

    @EventIDField(links = { ADDRESSES }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { ADDRESSES }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_ADDITION(Integer[].class, String[].class),

    @EventParamData(params = { "Addresses", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { ADDRESSES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Addresses", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { ADDRESSES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Addresses", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { ADDRESSES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_LETTER(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_NUMBER(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the point of an address", params = { "Address ID", "Point" }, defaults = { "", "" })
    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_POINT(Integer[].class, Point[].class),

    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_STREET(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_SURFACE_SIZE(Integer[].class, double[].class),

    @EventIDField(sameLength = true, links = { ADDRESSES }, params = { 0 })
    SET_ZIP_CODE(Integer[].class, String[].class),

    @EventParamData(desc = "Search addresses", params = { "Serach Query" })
    SEARCH(String.class);

    private final List<Class<?>> classes;

    private EditorAddressEventType(Class<?>... classes) {
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

        if (this == ADD || this == GET_BUILDING || this == DUPLICATE || this == SEARCH) {
            return Integer[].class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return ADDRESSES;
    }
}
