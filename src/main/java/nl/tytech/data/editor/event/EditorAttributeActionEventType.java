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

import static nl.tytech.core.net.serializable.MapLink.ATTRIBUTE_ACTIONS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(ATTRIBUTE_ACTIONS)
public enum EditorAttributeActionEventType implements EventTypeEnum {

    ADD,

    @EventIDField(links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(params = { "Actions", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { ATTRIBUTE_ACTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Actions", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { ATTRIBUTE_ACTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Actions", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { ATTRIBUTE_ACTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(sameLength = true, links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    SET_DESCRIPTION(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { ATTRIBUTE_ACTIONS }, params = { 0 })
    SET_FIXED_VALUE(Integer[].class, Boolean[].class);

    private final List<Class<?>> classes;

    private EditorAttributeActionEventType(Class<?>... classes) {
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
        return this == ADD ? Integer.class : //
                this == DUPLICATE ? Integer[].class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
