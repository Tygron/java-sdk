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

import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.color.TColor;

/**
 * Editing Stakeholders
 * @author Maxim Knepfle
 *
 */
@Linked(STAKEHOLDERS)
public enum EditorStakeholderEventType implements IndicatorEventTypeEnum {

    ADD(),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    ADD_LAND_OWNERSHIP(Integer.class, MultiPolygon.class),

    ADD_WITH_TYPE_AND_ACTIVE(Stakeholder.Type.class, Boolean.class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    RESET_WEB_TOKEN(Integer[].class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    RESET_WEB_TOKEN_WITH_LENGTH(Integer[].class, Integer.class),

    @EventParamData(desc = "Select Stakeholder with preferred ID, when not available fallback to next best.", params = {
            "Preferred Stakeholder ID or empty", "Client Token (from Join Session event)" }, response = "Selected Stakeholder ID")
    SELECT_STAKEHOLDER(Integer.class, String.class),

    @EventIDField(sameLength = true, links = { STAKEHOLDERS }, params = { 0 })
    SET_COLOR(Integer[].class, TColor[].class),

    @EventIDField(sameLength = true, links = { STAKEHOLDERS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { STAKEHOLDERS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { STAKEHOLDERS }, params = { 0 })
    SET_ACTIVE(Integer[].class, Boolean[].class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    SET_SHORT_NAME(Integer.class, String.class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    SET_START_BUDGET(Integer.class, Double.class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 0 })
    SET_TYPE(Integer.class, Stakeholder.Type.class),

    /**
     * Transfer ownership of plots and buildings. Optionally also the permitter of zones.
     */
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 0, 1 })
    TRANSFER_OWNERSHIP(Integer.class, Integer.class, Boolean.class);

    private final List<Class<?>> classes;

    private EditorStakeholderEventType(Class<?>... classes) {
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

        if (this == ADD_WITH_TYPE_AND_ACTIVE || this == SELECT_STAKEHOLDER) {
            return Integer.class;
        }
        if (this == DUPLICATE) {
            return Integer[].class;
        }
        if (this == ADD) {
            return Integer.class;
        }
        if (this == REMOVE) {
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

        // ignore visuals only
        return switch (this) {
            case SET_IMAGE, SELECT_STAKEHOLDER -> null;
            case RESET_WEB_TOKEN, RESET_WEB_TOKEN_WITH_LENGTH -> null; // no map changes
            default -> STAKEHOLDERS;
        };
    }
}
