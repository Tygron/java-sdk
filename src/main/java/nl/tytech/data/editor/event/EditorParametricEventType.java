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
import nl.tytech.core.item.annotations.Internal;
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

    @EventParamData(desc = "Generate a result for a Parametric Design", params = {
            "Parametric Design" }, response = "Parametric Design Result")
    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    GENERATE(Integer.class),

    @EventParamData(desc = "Add a Parametric Design of a particular Alignment", params = { "Alignment" }, response = "Parametric Design ID")
    ADD(ParametricDesign.Alignment.class),

    @EventParamData(desc = "Add a Parametric Design using a particular example", params = { "Example" }, response = "Parametric Design ID")
    ADD_EXAMPLE(Example.class),

    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Set a Function of a Function Type for a Parametric Design. Use Function ID -1 to remove it for a Function Type", params = {
            "Parametric Designs", "Function Type", "Function ID" }, response = "Parametric Design ID")
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, }, params = { 0 })
    SET_FUNCTION(Integer[].class, FunctionType[].class, Integer[].class),

    @EventParamData(desc = "Apply a Parametric Design for a Stakeholder, Function Types, Function IDs and one or more MultiPolygons per Function Type", params = {
            "Parametric Design", "Stakeholder (owner)", "Function Types", "Function IDs",
            "Array of MultiPolygons per Function Type" }, response = "Building IDs")
    @EventIDField(links = { PARAMETRIC_DESIGNS, STAKEHOLDERS }, params = { 0, 1 })
    APPLY(Integer.class, Integer.class, FunctionType[].class, Integer[].class, MultiPolygon[][].class),

    @EventParamData(desc = "Save a Parametric Design as a new Measure for a Stakeholder, using Function Types, Function IDs and one or more MultiPolygons per Function Type", params = {
            "Parametric Design", "Stakeholder (owner)", "Function Types", "Function IDs",
            "Array of MultiPolygons per Function Type" }, response = "Measure ID")
    @EventIDField(links = { PARAMETRIC_DESIGNS, STAKEHOLDERS }, params = { 0, 1 })
    SAVE_AS_MEASURE(Integer.class, Integer.class, FunctionType[].class, Integer[].class, MultiPolygon[][].class),

    @EventParamData(params = { "Parametric Designs", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { PARAMETRIC_DESIGNS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Parametric Designs", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { PARAMETRIC_DESIGNS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Parametric Designs", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(params = { "Parametric Designs", "Attribute Names" })
    @EventIDField(links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Set the alignment of a Parametric Design", params = { "Parametric Designs", "Names" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    SET_ALIGNMENT(Integer[].class, ParametricDesign.Alignment[].class),

    @EventParamData(desc = "Set the name of a Parametric Design", params = { "Parametric Designs", "Names" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the prompt for Parametric Designs", params = { "Prompt" })
    @Internal
    SET_PROMPT(String.class),

    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    SET_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventParamData(desc = "Add a Plot Design of a particular Function to a Parametric Design", params = { "Parametric Designs",
            "Functions" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, FUNCTIONS }, params = { 0, 1 })
    ADD_PLOT(Integer[].class, Integer[].class),

    @EventParamData(desc = "Remove a Plot Design of a Parametric Design", params = { "Parametric Designs", "Plot Design IDs" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 })
    REMOVE_PLOT(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the Function of a Plot Design of a Parametric Design", params = { "Parametric Designs", "Plot Design IDs",
            "Functions" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS, FUNCTIONS }, params = { 0, 2 })
    SET_PLOT_FUNCTION(Integer[].class, Integer[].class, Integer[].class),

    @EventParamData(desc = "Set Attributes of a Plot Design of a Parametric Design", params = { "Parametric Designs", "Plot Design IDs",
            "Attribute Names", "Numeric Attribute Values Array", "Source (optional)" })
    @EventIDField(sameLength = true, links = { PARAMETRIC_DESIGNS }, params = { 0 }, nullable = { 4 })
    SET_PLOT_ATTRIBUTES(Integer[].class, Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Remove Attributes from a Plot Design of a Parametric Design", params = { "Parametric Designs", "Plots",
            "Attribute Names" })
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

        return switch (this) {

            case GENERATE -> PDResult.class;

            case APPLY, DUPLICATE -> Integer[].class;

            case ADD, SAVE_AS_MEASURE -> Integer.class;

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
        // only apply trigger change to map
        return this == APPLY ? PARAMETRIC_DESIGNS : null;
    }
}
