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

import static nl.tytech.core.net.serializable.MapLink.FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.FUNCTION_OVERRIDES;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FunctionValue;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(FUNCTION_OVERRIDES)
public enum EditorFunctionOverrideEventType implements IndicatorEventTypeEnum {

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    ADD(Integer[].class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    ADD_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    REMOVE_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    RESET(Integer.class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    RESET_ASSETS(Integer.class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    RESET_VALUES(Integer.class),

    @EventParamData(params = { "Functions", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { FUNCTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Functions", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { FUNCTIONS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(links = { FUNCTIONS }, params = { 0 })
    SET_CATEGORY_VALUE(Integer.class, Category.class, CategoryValue.class, double[].class),

    @EventIDField(links = { FUNCTION_OVERRIDES }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventIDField(links = { FUNCTION_OVERRIDES }, params = { 0 })
    SET_FUNCTION_VALUE(Integer.class, FunctionValue.class, double[].class),

    @EventIDField(sameLength = true, links = { FUNCTIONS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { FUNCTIONS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class);

    private final List<Class<?>> classes;

    private EditorFunctionOverrideEventType(Class<?>... classes) {
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
        if (this == ADD || this == DUPLICATE) {
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
        return FUNCTION_OVERRIDES;
    }
}
