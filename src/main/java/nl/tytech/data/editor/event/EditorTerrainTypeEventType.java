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

import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import static nl.tytech.core.net.serializable.MapLink.TERRAIN_TYPES;
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
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.item.HeightSector;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(TERRAIN_TYPES)
public enum EditorTerrainTypeEventType implements IndicatorEventTypeEnum {

    ADD(Layer.class),

    @EventParamData(params = { "Terrain Type ID", "Geometry", "Adjust heights when changed from Water to non-water and vice versa." })
    @EventIDField(sameLength = true, links = { TERRAIN_TYPES }, params = { 0 })
    ADD_POLYGONS(Integer[].class, MultiPolygon[].class, Boolean[].class),

    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Import Terrain collection", params = { "Collection of Terrain Geometries", "Terrain Type IDs",
            "Adjust heights when changed from Water to non-water and vice versa.",
            "Angle of Repose (optional or use negative value in array for default)",
            "Override Height (optional or use " + HeightSector.INVALID_DATA + " value in array to ignore)",
            "Buffer for Points and Lines to make Polygons (optional)", "Source (optional)" })
    @EventIDField(links = { TERRAIN_TYPES, SOURCES }, params = { 1, 6 }, nullable = { 3, 4, 5, 6 })
    IMPORT(GeometryCollection.class, Integer[].class, Boolean.class, double[].class, double[].class, Double.class, Integer.class),

    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventIDField(links = { TERRAIN_TYPES, TERRAIN_TYPES }, params = { 0, 1 })
    REMOVE_AND_REPLACE(Integer[].class, Integer[].class),

    @EventParamData(params = { "Terrain Types", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { TERRAIN_TYPES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Terrain Types", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { TERRAIN_TYPES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    SET_CODE(Integer.class, String.class),

    @EventIDField(sameLength = true, links = { TERRAIN_TYPES }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    SET_PARENT(Integer.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorTerrainTypeEventType(Class<?>... classes) {
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
        if (this == DUPLICATE) {
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

        return switch (this) {
            case SET_PARENT -> null; // ignore visuals only
            default -> TERRAIN_TYPES;
        };
    }
}
