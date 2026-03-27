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

import static nl.tytech.core.net.serializable.MapLink.MEASUREMENTS;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.GeometryCollection;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;

/**
 *
 * @author Maxim Knepfle
 *
 */
@Linked(MEASUREMENTS)
public enum EditorMeasurementEventType implements EventTypeEnum {

    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventParamData(params = { "Measurements", "Measurement Names", "Overlays", "Source (optional)" })
    @EventIDField(links = { OVERLAYS, SOURCES }, params = { 2, 3 }, nullable = { 3 })
    IMPORT(GeometryCollection.class, String[].class, Integer[].class, Integer.class),

    @EventIDField(links = { MEASUREMENTS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(sameLength = true, links = { MEASUREMENTS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class);

    private final List<Class<?>> classes;

    private EditorMeasurementEventType(Class<?>... classes) {
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

        if (this == DUPLICATE) {
            return Integer[].class;
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
}
