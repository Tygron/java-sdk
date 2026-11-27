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

import static nl.tytech.core.net.serializable.MapLink.GEO_LINKS;
import static nl.tytech.core.net.serializable.MapLink.GEO_PLUGINS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.data.editor.serializable.GeoLinkType;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(GEO_PLUGINS)
public enum EditorGeoPluginEventType implements EventTypeEnum {

    @EventParamData(desc = "Add a new Geo Plugin of a Geo Link Type", params = { "Geo Link Types" }, response = "Geo Plugin")
    ADD(GeoLinkType.class),

    @EventIDField(links = { GEO_PLUGINS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { GEO_PLUGINS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Set the Source of a Geo Plugin, used to obtain data or features", params = { "Geo Plugins", "Sources" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS, SOURCES }, params = { 0, 1 })
    SET_SOURCE(Integer[].class, Integer[].class),

    @EventParamData(desc = "Remove the Source of a Geo Plugin", params = { "Geo Plugins", "Sources" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS, SOURCES }, params = { 0, 1 })
    REMOVE_SOURCE(Integer[].class, Integer[].class),

    @EventParamData(desc = "Add a Geo Link to a Geo Plugin", params = { "Geo Plugins", "Geo Links" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS, GEO_LINKS }, params = { 0, 1 })
    ADD_LINK(Integer[].class, Integer[].class),

    @EventParamData(desc = "Add a new Geo Link to a Geo Plugin with a provided name", params = { "Geo Plugins",
            "Names" }, response = "Geo Links")
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    ADD_NEW_LINKS(Integer[].class, String[].class),

    @EventParamData(desc = "Remove a Geo Link from a Geo Plugin", params = { "Geo Plugins", "Geo Links" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS, GEO_LINKS }, params = { 0, 1 })
    REMOVE_LINK(Integer[].class, Integer[].class),

    @EventParamData(desc = "Set the image of a Geo Plugin", params = { "Geo Plugins", "Image Names" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_IMAGE(Integer[].class, String[].class),

    @EventParamData(desc = "Set the name of a Geo Plugin", params = { "Geo Plugins", "Names" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set whether a Geo Plugin is active in the New Project Wizard", params = { "Geo Plugins", "Booleans" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_NEW_PROJECT(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set the layer used by a Geo Plugin to obtain data from its Source", params = { "Geo Plugins", "Layer Names" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_LAYER_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Override the CRS used by a Geo Plugin to transform data from its Source", params = { "Geo Plugins",
            "CRS Names" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_CRS(Integer[].class, String[].class),

    @EventParamData(desc = "Override the CRS's Force XY parameter, used by a Geo Plugin to transform data from its Source", params = {
            "Geo Plugins", "Booleans" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_FORCE_XY(Integer[].class, Boolean[].class),

    @EventParamData(desc = "Set the Name Attribute used by a Geo Plugin to retrieve names of features obtained from its Source", params = {
            "Geo Plugins", "Name Attributes" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_NAME_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Set the ID Attribute used by a Geo Plugin to retrieve unique IDs of features obtained from its Source", params = {
            "Geo Plugins", "Name Attributes" })
    @EventIDField(sameLength = true, links = { GEO_PLUGINS }, params = { 0 })
    SET_ID_ATTRIBUTE(Integer[].class, String[].class);

    private final List<Class<?>> classes;

    private EditorGeoPluginEventType(Class<?>... classes) {
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

            case DUPLICATE, ADD_NEW_LINKS -> Integer[].class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
