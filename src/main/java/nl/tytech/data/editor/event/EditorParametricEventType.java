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
import static nl.tytech.core.net.serializable.MapLink.PARAMETRIC_DESIGNS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.editor.serializable.PDResult;
import nl.tytech.data.engine.item.ParametricDesign;
import nl.tytech.data.engine.item.ParametricDesign.Example;
import nl.tytech.data.engine.item.ParametricDesign.FunctionType;

/**
 * Edit Parametric Designs
 *
 * @author Maxim Knepfle
 *
 */
@Linked(PARAMETRIC_DESIGNS)
public enum EditorParametricEventType implements IndicatorEventTypeEnum {

    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    GENERATE(Integer.class),

    ADD(ParametricDesign.Alignment.class),

    ADD_EXAMPLE(Example.class),

    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, }, params = { 0 })
    SET_FUNCTION(Integer[].class, FunctionType[].class, Integer[].class),

    @EventIDField(links = { PARAMETRIC_DESIGNS, STAKEHOLDERS }, params = { 0, 1 })
    APPLY(Integer.class, Integer.class, FunctionType[].class, Integer[].class, MultiPolygon[][].class),

    @EventIDField(links = { PARAMETRIC_DESIGNS, STAKEHOLDERS }, params = { 0, 1 })
    SAVE_AS_MEASURE(Integer.class, Integer.class, FunctionType[].class, Integer[].class, MultiPolygon[][].class),

    @EventParamData(params = { "Parametric Design", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { PARAMETRIC_DESIGNS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Parametric Design", "Attribute name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { PARAMETRIC_DESIGNS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Parametric Design", "Attribute names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(params = { "Parametric Design", "Attribute name" })
    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    SET_ALIGNMENT(Integer[].class, ParametricDesign.Alignment[].class),

    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    SET_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventParamData(params = { "Parametric Design", "Function" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, FUNCTIONS }, params = { 0, 1 })
    ADD_PLOT(Integer[].class, Integer[].class),

    @EventParamData(params = { "Parametric Design", "Plot" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE_PLOT(Integer[].class, Integer[].class),

    @EventParamData(params = { "Parametric Design", "Plot", "Function" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, FUNCTIONS }, params = { 0, 2 })
    SET_PLOT_FUNCTION(Integer[].class, Integer[].class, Integer[].class),

    @EventParamData(params = { "Parametric Design", "Plot", "Attribute name", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 }, nullable = { 4 })
    SET_PLOT_ATTRIBUTES(Integer[].class, Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(params = { "Parametric Design", "Plot", "Attribute name" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE_PLOT_ATTRIBUTES(Integer[].class, Integer[].class, String[].class);

    private final List<Class<?>> classes;

    private EditorParametricEventType(Class<?>... classes) {
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

        if (this == GENERATE) {
            return PDResult.class;
        }
        if (this == APPLY || this == DUPLICATE) {
            return Integer[].class;
        }
        if (this == ADD || this == SAVE_AS_MEASURE) {
            return Integer.class;
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
        // only apply trigger change to map
        return this == APPLY ? PARAMETRIC_DESIGNS : null;
    }
}
