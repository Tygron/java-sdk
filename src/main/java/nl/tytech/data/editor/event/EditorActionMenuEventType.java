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

import static nl.tytech.core.net.serializable.MapLink.ACTION_MENUS;
import static nl.tytech.core.net.serializable.MapLink.SCENARIOS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;

/**
 * EditorActionMenuEventType
 *
 * @author Maxim Knepfle
 */
@Linked(ACTION_MENUS)
public enum EditorActionMenuEventType implements EventTypeEnum {

    @EventParamData(desc = "Add an Action Menu for a Stakeholder", params = { "Stakeholder" }, response = "Action Menu ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD(Integer.class),

    @EventParamData(desc = "Add an Action to an Action Menu", params = { "Action Menu", "Action's MapLink", "Action's Item ID" })
    @EventIDField(links = { ACTION_MENUS }, params = { 0 })
    ADD_ACTION(Integer.class, MapLink.class, Integer.class),

    @EventIDField(links = { ACTION_MENUS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { ACTION_MENUS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove Actions from Action Menus", params = { "Action Menus", "Actions' MapLink", "Actions' Item ID" })
    @EventIDField(sameLength = true, links = { ACTION_MENUS }, params = { 0 })
    REMOVE_ACTIONS(Integer[].class, MapLink[].class, Integer[].class),

    @EventParamData(desc = "Make an Action Menu active for a Stakeholder in a particular Scenario", params = { "Action Menu", "Stakeholder",
            "Scenario", "Active" })
    @EventIDField(links = { ACTION_MENUS, STAKEHOLDERS, SCENARIOS }, params = { 0, 1, 2 })
    SET_ACTIVE_FOR_STAKEHOLDER_AND_SCENARIO(Integer.class, Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the image of an Action Menu", params = { "Action Menus", "Image names" })
    @EventIDField(sameLength = true, links = { ACTION_MENUS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventParamData(desc = "Set the name of an Action Menu", params = { "Action Menus", "Names" })
    @EventIDField(sameLength = true, links = { ACTION_MENUS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Swap the order of paired Action Menus", params = { "Action Menus (main)", "Action Menus (other)" })
    @EventIDField(sameLength = true, links = { ACTION_MENUS, ACTION_MENUS }, params = { 0, 1 })
    SWAP_ORDER(Integer[].class, Integer[].class);

    private final List<Class<?>> classes;

    private EditorActionMenuEventType(Class<?>... classes) {
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
