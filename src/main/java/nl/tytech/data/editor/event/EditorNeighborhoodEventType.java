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

import static nl.tytech.core.net.serializable.MapLink.NEIGHBORHOODS;
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
import nl.tytech.util.color.TColor;

/**
 * Edit Neighborhoods
 *
 * @author Maxim Knepfle
 */
@Linked(NEIGHBORHOODS)
public enum EditorNeighborhoodEventType implements IndicatorEventTypeEnum {

    ADD(),

    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 })
    ADD_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Import Neighborhood Collection", params = { "Collection of Neighborhood geometries", "Neighborhood Names",
            "Attribute Names", "Attribute Values", "Buffer for Points and Lines to make Polygons (optional)",
            "Source (optional)" }, defaults = { "", "", "", "", "" + Setting.DEFAULT_PL_BUFFER, "" }, exampleAmount = { 3, 3, 2, 6 })
    @EventIDField(links = { SOURCES }, params = { 5 }, nullable = { 4, 5 })
    IMPORT(GeometryCollection.class, String[].class, String[].class, double[][].class, Double.class, Integer.class),

    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(links = { NEIGHBORHOODS }, params = { 0 })
    REMOVE_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(sameLength = true, links = { NEIGHBORHOODS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(params = { "Neighborhoods", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { NEIGHBORHOODS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Neighborhoods", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { NEIGHBORHOODS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Neighborhoods", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { NEIGHBORHOODS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(sameLength = true, links = { NEIGHBORHOODS }, params = { 0 })
    SET_COLOR(Integer[].class, TColor[].class),

    @EventIDField(sameLength = true, links = { NEIGHBORHOODS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { NEIGHBORHOODS }, params = { 0 })
    SET_POLYGONS(Integer[].class, MultiPolygon[].class);

    private final List<Class<?>> classes;

    private EditorNeighborhoodEventType(Class<?>... classes) {
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
        if (this == DUPLICATE || this == IMPORT) {
            return Integer[].class;
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
        return MapLink.NEIGHBORHOODS;
    }
}
