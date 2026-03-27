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

import static nl.tytech.core.net.serializable.MapLink.AREAS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.color.TColor;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(AREAS)
public enum EditorAreaEventType implements IndicatorEventTypeEnum {

    @EventParamData(params = { "Amount (optional)" }, response = "Area IDs")
    @EventIDField(nullable = { 0 })
    ADD(Integer.class),

    @EventParamData(params = { "Attribute name to group Areas by" })
    ADD_GROUP(String.class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    ADD_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    ADD_RELATION(Integer.class, Relation.class),

    ADD_WITH_ATTRIBUTE(String.class, Double.class),

    @EventIDField(links = { AREAS }, params = { 0 })
    DUPLICATE(Integer[].class),

    GENERATE_BORDER_AREAS(String.class, String.class, Double.class),

    GENERATE_INUNDATION_AREAS(Double.class),

    GENERATE_SEWER_AREAS(Integer.class, Double.class, Double.class, Double.class, String.class),

    GENERATE_WATER_AREAS(Double.class),

    @EventParamData(desc = "Import Area Collection", params = { "Collection of Area geometries", "Area Names", "Attribute Names",
            "Attribute Values", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, defaults = { "", "", "", "", "" + Setting.DEFAULT_PL_BUFFER, "" }, exampleAmount = { 3, 3, 2, 6 })
    @EventIDField(links = { SOURCES }, params = { 5 }, nullable = { 4, 5 })
    IMPORT(GeometryCollection.class, String[].class, String[].class, double[][].class, Double.class, Integer.class),

    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE_ALL_ATTRIBUTES(Integer[].class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    REMOVE_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @Deprecated
    @EventParamData(desc = "Use REMOVE_GROUP instead for identical result.", params = { "Group name" })
    REMOVE_FILTER(String[].class),

    REMOVE_GROUP(String[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    REMOVE_RELATIONS(Integer.class, Relation[].class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(params = { "Areas", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { AREAS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Areas", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { AREAS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Areas", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { AREAS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_COLOR(Integer[].class, TColor[].class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { AREAS }, params = { 0 })
    SET_POLYGONS(Integer[].class, MultiPolygon[].class),

    @EventIDField(links = { AREAS }, params = { 0 })
    SET_RELATION(Integer.class, Relation.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorAreaEventType(Class<?>... classes) {
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

        if (this == ADD || this == DUPLICATE || this == IMPORT) {
            return Integer[].class;
        }
        if (this == ADD_WITH_ATTRIBUTE || this == GENERATE_SEWER_AREAS) {
            return Integer.class;
        }
        if (this == REMOVE || this == SET_NAME || this == SET_ATTRIBUTE || this == SET_POLYGONS) {
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
        // add group does not change the map
        return this != ADD_GROUP ? AREAS : null;
    }
}
