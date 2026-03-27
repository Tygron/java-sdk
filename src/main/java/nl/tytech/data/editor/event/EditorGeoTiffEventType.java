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
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;

/**
 *
 * @author Frank Baars
 *
 */

@Linked(GEO_TIFFS)
public enum EditorGeoTiffEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add new GeoTIFF Item with provided file. For files larger than 2GB, use the Stream API or Web URL.", params = {
            "GeoTIFF Name", "File bytes (< 2GB)", "Uploader", "CRS Override (optional) " }, response = "GeoTIFF ID")
    @EventIDField(nullable = { 3 })
    ADD(String.class, byte[].class, String.class, String.class),

    @EventParamData(desc = "Add new GeoTIFF Item from Web URL.", params = { "GeoTIFF Name", "Web URL", "Uploader",
            "CRS Override (optional)" }, response = "GeoTIFF IDs")
    @EventIDField(nullable = { 3 }, sameLength = true)
    ADD_URL(String[].class, String[].class, String[].class, String[].class),

    @EventParamData(desc = "Add new GeoTIFF Item with provided ASCII file. For files larger than 2GB, use the Stream API or Web URL.", params = {
            "GeoTIFF Name", "ASCII File bytes (< 2GB)", "Uploader", "CRS" }, response = "GeoTIFF ID")
    ADD_ASCII(String.class, byte[].class, String.class, String.class),

    @EventParamData(desc = "Add new GeoTIFF Item from Web ASCII URL.", params = { "GeoTIFF Name", "Web ASCII URL", "Uploader",
            "CRS" }, response = "GeoTIFF IDs")
    @EventIDField(sameLength = true)
    ADD_ASCII_URL(String[].class, String[].class, String[].class, String[].class),

    @EventParamData(desc = "Export GeoTIFF. For files larger than 2GB, use the Stream API.", params = { "GeoTIFF ID" })
    @EventIDField(links = { GEO_TIFFS }, params = { 0 })
    EXPORT(Integer.class),

    @EventIDField(links = { GEO_TIFFS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Update existing GeoTIFF Item with provided file. For files larger than 2GB, use the Stream API or Web URL.", params = {
            "GeoTIFF ID", "File bytes (< 2GB)", "Uploader", "CRS Override (optional)" })
    @EventIDField(links = { GEO_TIFFS }, params = { 0 }, nullable = { 3 })
    SET_GEOTIFF(Integer.class, byte[].class, String.class, String.class),

    @EventParamData(desc = "Update existing GeoTIFF Item from Web URL.", params = { "GeoTIFF ID", "Web URL", "Uploader",
            "CRS Override (optional)" })
    @EventIDField(links = { GEO_TIFFS }, params = { 0 }, nullable = { 3 }, sameLength = true)
    SET_GEOTIFF_URL(Integer[].class, String[].class, String[].class, String[].class);

    private final List<Class<?>> classes;

    private EditorGeoTiffEventType(Class<?>... classes) {
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

        if (this == ADD || this == ADD_ASCII) {
            return Integer.class;
        } else if (this == ADD_URL || this == ADD_ASCII_URL) {
            return Integer[].class;
        } else if (this == EXPORT) {
            return byte[].class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return GEO_TIFFS;
    }
}
