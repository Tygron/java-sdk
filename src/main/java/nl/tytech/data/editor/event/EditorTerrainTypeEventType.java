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

    @EventParamData(desc = "Add a new Surface or Underground Terrain Type", params = {
            "Layer (Surface or Underground)" }, response = "Terrain Type ID")
    ADD(Layer.class),

    @EventParamData(desc = "Add Terrain of a particular Terrain Type for a MultiPolygon. Optionally adjust the terrain height for transitions between Water and non-water Terrains", params = {
            "Terrain Types", "MultiPolygons", "Array of height adjustments for Water/non-Water transitions" })
    @EventIDField(sameLength = true, links = { TERRAIN_TYPES }, params = { 0 })
    ADD_POLYGONS(Integer[].class, MultiPolygon[].class, Boolean[].class),

    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Import Terrain collection", params = { "Collection of Terrain Geometries", "Terrain Types",
            "Height adjustment for Water/non-Water transitions", "Angle of Repose (optional; negative values use default)",
            "Override Height (optional; use " + HeightSector.INVALID_DATA + " to ignore)",
            "Buffer for Points and Lines to make Polygons (optional)", "Source (optional)" })
    @EventIDField(links = { TERRAIN_TYPES, SOURCES }, params = { 1, 6 }, nullable = { 3, 4, 5, 6 })
    IMPORT(GeometryCollection.class, Integer[].class, Boolean.class, double[].class, double[].class, Double.class, Integer.class),

    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove a Terrain Type and replace it with another", params = { "Terrain Types (removed)",
            "Terrain Types (replacement)" })
    @EventIDField(links = { TERRAIN_TYPES, TERRAIN_TYPES }, params = { 0, 1 })
    REMOVE_AND_REPLACE(Integer[].class, Integer[].class),

    @EventParamData(params = { "Terrain Types", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { TERRAIN_TYPES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Terrain Types", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { TERRAIN_TYPES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set the code name of a Terrain Type", params = { "Terrain Type", "Code name" })
    @EventIDField(links = { TERRAIN_TYPES }, params = { 0 })
    SET_CODE(Integer.class, String.class),

    @EventParamData(desc = "Set the name of a Terrain Type", params = { "Terrain Types", "Names" })
    @EventIDField(sameLength = true, links = { TERRAIN_TYPES }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the Terrain Type parent of a Terrain Type", params = { "Terrain Type", "Parent Terrain Type" })
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

        return switch (this) {

            case ADD -> Integer.class;

            case DUPLICATE -> Integer[].class;

            default -> null;
        };

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
