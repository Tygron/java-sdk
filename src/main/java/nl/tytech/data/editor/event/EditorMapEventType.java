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

import static nl.tytech.core.net.serializable.MapLink.GEO_TIFFS;
import static nl.tytech.core.net.serializable.MapLink.HEIGHTS;
import static nl.tytech.core.net.serializable.MapLink.OVERLAYS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;

/**
 * @author Frank Baars
 */

@Linked(HEIGHTS)
public enum EditorMapEventType implements IndicatorEventTypeEnum {

    @EventIDField(links = { GEO_TIFFS }, params = { 0 })
    SET_HEIGHT_GEOTIFF(Integer[].class),

    @EventParamData(desc = "Update Elevation Model from Grid Overlay", params = { "Overlay ID", "Timeframe (optional)" }, defaults = { "",
            "0" }, response = "Amount of sectors updated")
    @EventIDField(links = { OVERLAYS }, params = { 0 }, nullable = { 1 })
    SET_HEIGHT_OVERLAY(Integer.class, Integer.class),

    @EventParamData(desc = "Update Heightmap Sector Data", params = { "Height Sector ID",
            "Header: sector widthPoints, columns, rows, baseX, baseY", "Matrix Data Array", })
    @EventIDField(links = { HEIGHTS }, params = { 0 })
    SET_HEIGHT_SECTOR(Integer.class, int[].class, float[].class);

    private final List<Class<?>> classes;

    private EditorMapEventType(Class<?>... classes) {

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
        return this == SET_HEIGHT_GEOTIFF || this == SET_HEIGHT_OVERLAY ? Integer.class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return MapLink.HEIGHTS;
    }
}
