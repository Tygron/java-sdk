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

import static nl.tytech.core.net.serializable.MapLink.EXCEL_SHEETS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import static nl.tytech.core.net.serializable.MapLink.ZONES;
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
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.util.color.TColor;

/**
 * Edit Zoning plan
 *
 * @author Maxim Knepfle
 *
 */
@Linked(ZONES)
public enum EditorZoningEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new Zone, optionally as Category Zone", params = { "As Category Zone" }, response = "Zone ID")
    ADD(Boolean.class),

    @EventParamData(desc = "Add a permitted Function Category to a Category Zone", params = { "Category Zone", "Function Category" })
    @EventIDField(links = { ZONES }, params = { 0 })
    ADD_FUNCTION_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { ZONES }, params = { 0 })
    ADD_POLYGONS(Integer.class, MultiPolygon.class),

    @EventIDField(links = { ZONES }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Import Zone Collection", params = { "Collection of Zone geometries", "Zone Names", "Attribute Names",
            "Numeric Attribute Values", "Stakeholders (permit issuers)", "Category Zone Import",
            "Polygon buffer distance for Points and Lines (optional)", "Source (optional)" }, defaults = { "", "", "", "", "", "",
                    "" + Setting.DEFAULT_PL_BUFFER, "" }, exampleAmount = { 3, 3, 2, 6, 3 }, response = "Zone IDs")
    @EventIDField(links = { STAKEHOLDERS, SOURCES }, params = { 4, 7 }, nullable = { 6, 7 })
    IMPORT(GeometryCollection.class, String[].class, String[].class, double[][].class, Integer[].class, Boolean.class, Double.class,
            Integer.class),

    @EventParamData(desc = "Remove a Zone, and optionally its child Zones", params = { "Zones", "Remove Children" }, response = "Removed")
    @EventIDField(links = { ZONES }, params = { 0 })
    REMOVE(Integer[].class, Boolean.class),

    @EventIDField(links = { ZONES }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove a permitted Function Category from a Category Zone", params = { "Category Zone", "Function Category" })
    @EventIDField(links = { ZONES }, params = { 0 })
    REMOVE_FUNCTION_CATEGORY(Integer.class, Category.class),

    @EventIDField(links = { ZONES }, params = { 0 })
    REMOVE_POLYGONS(Integer.class, MultiPolygon.class),

    @EventParamData(params = { "Zones", "Attribute Name", "Numeric Attribute Values", "Source (optional)" }, response = "Updated")
    @EventIDField(links = { ZONES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Zones", "Attribute Name", "Numeric Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { ZONES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Zones", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { ZONES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set the color of a Zone", params = { "Zones", "Colors" })
    @EventIDField(sameLength = true, links = { ZONES }, params = { 0 })
    SET_COLOR(Integer[].class, TColor[].class),

    @EventParamData(desc = "Set the Excel Sheet used by a Zone for Zoning Permits", params = { "Zone", "Excel Sheet" })
    @EventIDField(links = { ZONES, EXCEL_SHEETS }, params = { 0, 1 })
    SET_EXCEL(Integer.class, Integer.class),

    @EventParamData(desc = "Set the name of a Zone", params = { "Zones", "Names" }, response = "Updated")
    @EventIDField(sameLength = true, links = { ZONES }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the parent Zone of a Zone", params = { "Zone", "Parent Zone" })
    @EventIDField(links = { ZONES }, params = { 0 })
    SET_PARENT(Integer.class, Integer.class),

    @EventParamData(desc = "Set the permitting Stakeholder of a Zone", params = { "Zone",
            "Stakeholder (permit issuer)" }, response = "Updated")
    @EventIDField(links = { ZONES, STAKEHOLDERS }, params = { 0, 1 })
    SET_PERMITTER(Integer.class, Integer.class),

    @EventIDField(sameLength = true, links = { ZONES }, params = { 0 })
    SET_POLYGONS(Integer[].class, MultiPolygon[].class);

    private final List<Class<?>> classes;

    private EditorZoningEventType(Class<?>... classes) {
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

            case DUPLICATE, IMPORT -> Integer[].class;

            case REMOVE, SET_NAME, SET_ATTRIBUTE, SET_POLYGONS, SET_PERMITTER -> Boolean.class;

            default -> null;
        };

    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return ZONES;
    }
}
