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
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.data.core.serializable.GeoCatalog;
import nl.tytech.data.core.serializable.GeoFormat;

/**
 * Events related to editing sources.
 * @author Maxim Knepfle
 *
 */
@Linked(SOURCES)
public enum EditorSourceEventType implements EventTypeEnum {

    @EventParamData(desc = "Add a new Catalog Source, given a Geo Catalog Type, Name and URL", params = { "Geo Catalog Type", "Name",
            "URL" }, response = "Catalog Source ID")
    ADD_CATALOG(GeoCatalog.class, String.class, String.class),

    @EventParamData(desc = "Add a new File Source, given a Name and Uploader", params = { "File Name",
            "Uploader Name" }, response = "File Source ID")
    ADD_FILE(String.class, String.class),

    @EventParamData(desc = "Add a new Service Source, given a Geo Format, Name, URL and Uploader", params = { "Geo Format", "Name", "URL",
            "Uploader Name" }, response = "Service Source ID")
    ADD_SERVICE(GeoFormat.class, String.class, String.class, String.class),

    @EventIDField(links = { SOURCES }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Set the description of a Source", params = { "Source", "Description" })
    @EventIDField(links = { SOURCES }, params = { 0 })
    SET_DESCRIPTION(Integer.class, String.class),

    @EventParamData(desc = "Set the name of a Source", params = { "Sources", "Names" })
    @EventIDField(sameLength = true, links = { SOURCES }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the uploader name of a Source", params = { "Source", "Uploader Name" })
    @EventIDField(links = { SOURCES }, params = { 0 })
    SET_UPLOADER(Integer.class, String.class),

    @EventParamData(desc = "Set the URL of a Service or Catalog Source", params = { "Source (service or catalog)", "URL" })
    @EventIDField(links = { SOURCES }, params = { 0 })
    SET_URL(Integer.class, String.class);

    private final List<Class<?>> classes;

    private EditorSourceEventType(Class<?>... classes) {
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

            case ADD_CATALOG, ADD_FILE, ADD_SERVICE -> Integer.class;

            case REMOVE -> Boolean.class;

            default -> null;
        };

    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
