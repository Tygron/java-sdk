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
package nl.tytech.data.engine.event;

import static nl.tytech.core.net.serializable.MapLink.SOUNDS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;

/**
 *
 * @author Jeroen Warmerdam
 *
 */
public enum SoundEventType implements EventTypeEnum {

    @EventParamData(editor = true, desc = "Pause a particular sound", params = { "Sound" })
    @EventIDField(links = { SOUNDS }, params = { 0 })
    PAUSE(Integer.class),

    @EventParamData(editor = true, desc = "Start a particular sound for a particular Stakeholder", params = { "Start for Stakeholder",
            "Sound" })
    @EventIDField(links = { STAKEHOLDERS, SOUNDS }, params = { 0, 1 })
    START(Integer.class, Integer.class),

    @EventIDField(links = { SOUNDS }, params = { 0 })
    STARTED(Integer.class, Double.class),

    @EventParamData(editor = true, desc = "Stop a particular sound for a particular Stakeholder", params = { "Stop for Stakeholder",
            "Sound" })
    @EventIDField(links = { STAKEHOLDERS, SOUNDS }, params = { 0, 1 })
    STOP(Integer.class, Integer.class),

    @EventParamData(editor = true, desc = "Stop all sounds for a particular Stakeholder", params = { "Stop for Stakeholder",
            "Stop background music" })
    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    STOP_ALL(Integer.class, Boolean.class),

    @EventIDField(links = { SOUNDS }, params = { 0 })
    STOPPED(Integer.class),

    @EventParamData(editor = true, desc = "Unpause a particular sound", params = { "Sound" })
    @EventIDField(links = { SOUNDS }, params = { 0 })
    UNPAUSE(Integer.class);

    private final List<Class<?>> classes;

    private SoundEventType(Class<?>... classes) {
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
        return null;
    }

    @Override
    public boolean isServerSide() {
        return false;
    }
}
