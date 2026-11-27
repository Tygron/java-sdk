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

import static nl.tytech.core.net.serializable.MapLink.PARTICLE_EMITTERS;
import static nl.tytech.core.net.serializable.MapLink.SPECIAL_EFFECTS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;

/**
 * @author Jeroen Warmerdam
 *
 */
@Linked(SPECIAL_EFFECTS)
public enum EditorSpecialEffectEventType implements EventTypeEnum {

    @EventParamData(desc = "Add a new Special Effect at a location with an optional Particle Emitter", params = { "Location (optional)",
            "Particle Emitter (optional)" }, response = "Special Effect ID")
    @EventIDField(links = { PARTICLE_EMITTERS }, params = { 1 }, nullable = { 0, 1 })
    ADD(Point.class, Integer.class),

    @EventParamData(desc = "Add a new Particle Emitter to a Special Effect", params = { "Special Effect" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    ADD_PARTICLE_PAIR(Integer.class),

    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Duplicate a Particle Emitter Pair for a Special Effect", params = { "Special Effect",
            "Particle Emitter Pair ID" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    DUPLICATE_PAIR(Integer.class, Integer.class),

    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove a Particle Emitter Pair from a Special Effect", params = { "Special Effects",
            "Particle Emitter Pair IDs" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    REMOVE_PARTICLE_PAIR(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set a Special Effect active", params = { "Special Effects", "Active Array" })
    @EventIDField(sameLength = true, links = { SPECIAL_EFFECTS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set the location of a Special Effect", params = { "Special Effect", "Point" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    SET_LOCATION(Integer.class, Point.class),

    @EventParamData(desc = "Set the name of a Special Effect", params = { "Special Effects", "Names" })
    @EventIDField(sameLength = true, links = { SPECIAL_EFFECTS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the offset (x is east, y is up, z is north) for a Particle Emitter Pair of a Special Effect", params = {
            "Special Effect", "Particle Emitter Pair ID", "XYZ-values" })
    @EventIDField(links = { SPECIAL_EFFECTS }, params = { 0 })
    SET_PARTICLE_PAIR_LOCATION(Integer.class, Integer.class, double[].class),

    @EventParamData(desc = "Set the Particle Emitter for a Particle Emitter Pair of a Special Effect", params = { "Special Effect",
            "Particle Emitter Pair ID", "Particle Emitter" })
    @EventIDField(links = { SPECIAL_EFFECTS, PARTICLE_EMITTERS }, params = { 0, 2 })
    SET_PARTICLE_PAIR_TYPE(Integer.class, Integer.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorSpecialEffectEventType(Class<?>... classes) {

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

            case REMOVE -> Boolean.class;

            default -> null;
        };

    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
