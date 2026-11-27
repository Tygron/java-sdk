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
import static nl.tytech.core.net.serializable.MapLink.UPGRADE_TYPES;
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
@Linked(UPGRADE_TYPES)
public enum EditorUpgradeTypeEventType implements EventTypeEnum {

    @EventParamData(desc = "Add a new Upgrade Type", response = "Upgrade Type ID")
    ADD,

    @EventParamData(desc = "Add an Upgrade Pair to an Upgrade Type", params = { "Upgrade Type" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    ADD_PAIR(Integer.class),

    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Duplicate an Upgrade Pair of an Upgrade Type, given a Source Function", params = { "Upgrade Type",
            "Source Function" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    DUPLICATE_PAIR(Integer.class, Integer.class),

    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove an Upgrade Pair of an Upgrade Type, given a Source Function", params = { "Upgrade Type",
            "Source Function" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    REMOVE_PAIR(Integer.class, Integer.class),

    @EventParamData(desc = "Set the price of an Upgrade Type, per square meter of upgraded floorsize or lotsize", params = { "Upgrade Type",
            "Is Floorsize (false = Lotsize)", "Cost" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    SET_COST_M2(Integer.class, Boolean.class, Double.class),

    @EventParamData(desc = "Set the description of an Upgrade Type", params = { "Upgrade Type", "Description" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventParamData(desc = "Set the image of an Upgrade Type", params = { "Upgrade Types", "Image names" })
    @EventIDField(sameLength = true, links = { UPGRADE_TYPES }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventParamData(desc = "Set whether this Upgrade Type can only be applied on Buildings owned by the performing Stakeholder", params = {
            "Upgrade Type", "Must own Buildings" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    SET_MUST_OWN(Integer.class, Boolean.class),

    @EventParamData(desc = "Set the name of an Upgrade Type", params = { "Upgrade Types", "Names" })
    @EventIDField(sameLength = true, links = { UPGRADE_TYPES }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set a Source or Target function for an Upgrade pair of an Upgrade Type, given a Source Function", params = {
            "Upgrade Type", "Function (source)", "Function (new)", "Adjust Source Function (false = Target)" })
    @EventIDField(links = { UPGRADE_TYPES, FUNCTIONS, FUNCTIONS }, params = { 0, 1, 2 })
    SET_PAIR(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set whether the Upgrade Type requires a Zoning permit", params = { "Upgrade Type", "Zoning permit required" })
    @EventIDField(links = { UPGRADE_TYPES }, params = { 0 })
    SET_ZONE_PERMIT_REQUIRED(Integer.class, Boolean.class);

    private final List<Class<?>> classes;

    private EditorUpgradeTypeEventType(Class<?>... classes) {
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
