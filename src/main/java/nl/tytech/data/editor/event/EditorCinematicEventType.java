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

import static nl.tytech.core.net.serializable.MapLink.CINEMATIC_DATAS;
import static nl.tytech.core.net.serializable.MapLink.EVENT_BUNDLES;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;

/**
 * @author Frank Baars, Jeroen Warmerdam
 */
@Linked(CINEMATIC_DATAS)
public enum EditorCinematicEventType implements EventTypeEnum {

    @EventParamData(desc = "Add a Cinematic for a Stakeholder", params = { "Stakeholder" }, response = "Cinematic Data ID")
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD(Integer.class),

    @EventParamData(desc = "Add a Key Point to a Cinematic", params = { "Cinematic Data" }, response = "Key Point ID")
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    ADD_KEYPOINT(Integer.class),

    @EventParamData(desc = "Add an Event Bundle to a Key Point of a Cinematic", params = { "Cinematic Data", "Key Point ID",
            "Event Bundle" })
    @EventIDField(links = { CINEMATIC_DATAS, EVENT_BUNDLES }, params = { 0, 2 })
    ADD_KEYPOINT_EVENT_BUNDLE(Integer.class, Integer.class, Integer.class),

    @EventParamData(desc = "Add a Key Point with the provided camera location, orientation and lookat in 3D Visualization coordinates (X is east, Y is up, Z is south)", params = {
            "Cinematic Data", "Key Point ID", "Camera Location", "Up Vector",
            "Lookat location" }, defaults = { "", "", "", "", "" }, response = "Key Point ID")
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    ADD_KEYPOINT_WITH_PARAMETERS(Integer.class, Integer.class, Float[].class, Float[].class, Float[].class),

    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(desc = "Duplicate a Key Point of a Cinematic", params = { "Cinematic Data", "Key Point ID" }, response = "Key Point ID")
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    DUPLICATE_KEYPOINT(Integer.class, Integer.class),

    @EventParamData(desc = "Move a Key Point to an index of the array of Key Points of a Cinematic", params = { "Cinematic Data",
            "Key Point ID", "Index" })
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    MOVE_KEYPOINT(Integer.class, Integer.class, Integer.class),

    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove an Event Bundle from a Key Point of a Cinematic", params = { "Cinematic Datas", "Key Point IDs",
            "Event Bundles" })
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    REMOVE_KEYPOINT_EVENT_BUNDLE(Integer[].class, Integer[].class, Integer[].class),

    @EventParamData(desc = "Remove Key Points from Cinematics", params = { "Cinematic Datas", "Key Point IDs" })
    @EventIDField(sameLength = true, links = { CINEMATIC_DATAS }, params = { 0 })
    REMOVE_KEYPOINT(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the description of a Key Point of a Cinematic", params = { "Cinematic Data", "Key Point ID",
            "Description" })
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    SET_KEYPOINT_DESCRIPTION(Integer.class, Integer.class, String.class),

    @EventParamData(desc = "Set the camera location, orientation and lookat of a Key Point in 3D Visualization coordinates (X is east, Y is up, Z is south)", params = {
            "Cinematic Data", "Key Point ID", "Camera Location", "Up Vector", "Lookat location" }, defaults = { "", "", "", "", "" })
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    SET_KEYPOINT_ORIENTATION(Integer.class, Integer.class, Float[].class, Float[].class, Float[].class),

    @EventParamData(desc = "Let the Key Point of a Cinematic pause the Cinematic once it is reached", params = { "Cinematic Data",
            "Key Point ID", "Paused" })
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    SET_KEYPOINT_PAUSED(Integer.class, Integer.class, Boolean.class),

    @EventParamData(desc = "Set the time it takes to move from the specified Key Point of a Cinematic to the next", params = {
            "Cinematic Data", "Key Point ID", "Time in seconds" })
    @EventIDField(links = { CINEMATIC_DATAS }, params = { 0 })
    SET_KEYPOINT_TIME(Integer.class, Integer.class, Double.class),

    @EventParamData(desc = "Set the name of a Cinematic", params = { "Cinematic Datas", "Names" })
    @EventIDField(sameLength = true, links = { CINEMATIC_DATAS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the voice used for text-to-speech of Keypoint descriptions of a Cinematic", params = { "Cinematic Datas",
            "Voice Names" })
    @EventIDField(sameLength = true, links = { CINEMATIC_DATAS }, params = { 0 })
    SET_VOICE(Integer[].class, String[].class);

    private final List<Class<?>> classes;

    private EditorCinematicEventType(Class<?>... classes) {
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

            case ADD, ADD_KEYPOINT, ADD_KEYPOINT_WITH_PARAMETERS, DUPLICATE_KEYPOINT -> Integer.class;

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
