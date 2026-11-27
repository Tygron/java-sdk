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

import static nl.tytech.core.net.serializable.MapLink.NET_FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.NetLine.NetType;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(NET_FUNCTIONS)
public enum EditorNetFunctionEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a Net Function with a particular Net Type", params = { "Net Type" }, response = "Net Function ID")
    ADD(NetType.class),

    @EventParamData(desc = "Import Net Functions by providing names, Net Types and Attributes with accompanying values", params = { "Names",
            "Net Types", "Attributes", "Attribute Values", "Source" })
    @EventIDField(links = { SOURCES }, params = { 4 })
    IMPORT(String[].class, NetType[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(links = { NET_FUNCTIONS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove all Attributes of a Net Function", params = { "Net Functions" })
    @EventIDField(links = { NET_FUNCTIONS }, params = { 0 })
    REMOVE_ALL_ATTRIBUTES(Integer[].class),

    @EventIDField(links = { NET_FUNCTIONS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(params = { "Net Functions", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { NET_FUNCTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Net Functions", "Attribute Name", "Numeric Attribute Values appended to existing values",
            "Source (optional)" })
    @EventIDField(links = { NET_FUNCTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Net Functions", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { NET_FUNCTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set the name of a Net Function", params = { "Net Functions", "Names" })
    @EventIDField(sameLength = true, links = { NET_FUNCTIONS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the Net Type of a Net Function", params = { "Net Function", "Net Type" })
    @EventIDField(links = { NET_FUNCTIONS }, params = { 0 })
    SET_TYPE(Integer.class, NetType.class);

    private final List<Class<?>> classes;

    private EditorNetFunctionEventType(Class<?>... classes) {
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
        return NET_FUNCTIONS;
    }
}
