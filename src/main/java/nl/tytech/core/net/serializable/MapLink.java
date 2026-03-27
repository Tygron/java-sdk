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
package nl.tytech.core.net.serializable;

import static nl.tytech.core.net.serializable.MapGroup.COMMUNITY;
import static nl.tytech.core.net.serializable.MapGroup.CURRENT_SITUATION;
import static nl.tytech.core.net.serializable.MapGroup.FUTURE_DESIGN;
import static nl.tytech.core.net.serializable.MapGroup.MULTI_SCENARIO;
import static nl.tytech.core.net.serializable.MapGroup.TOOLS;
import static nl.tytech.core.net.serializable.MapGroup.Sub.ACTION;
import static nl.tytech.core.net.serializable.MapGroup.Sub.API;
import static nl.tytech.core.net.serializable.MapGroup.Sub.ASSETS;
import static nl.tytech.core.net.serializable.MapGroup.Sub.CALCULATION;
import static nl.tytech.core.net.serializable.MapGroup.Sub.CONFIGURATION;
import static nl.tytech.core.net.serializable.MapGroup.Sub.CONSTRUCTION;
import static nl.tytech.core.net.serializable.MapGroup.Sub.FINANCIAL;
import static nl.tytech.core.net.serializable.MapGroup.Sub.GEO;
import static nl.tytech.core.net.serializable.MapGroup.Sub.GEOGRAPHY;
import static nl.tytech.core.net.serializable.MapGroup.Sub.INTERACTION;
import static nl.tytech.core.net.serializable.MapGroup.Sub.NETWORK;
import static nl.tytech.core.net.serializable.MapGroup.Sub.URBAN_SUBDIVISION;
import static nl.tytech.core.net.serializable.MapGroup.Sub.VISUALS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.structure.DataLord;
import nl.tytech.util.StringUtils;

/**
 * MapLink
 *
 * Connects the items with each other.
 *
 * @author Maxim Knepfle
 */
public enum MapLink implements EventTypeEnum {

    /**
     * Subscriptions: LAUNCHER, PARTICIPANT, EDITOR, SERVER, TOOLS
     */

    SETTINGS(TOOLS, CONFIGURATION), // import load first

    ACTION_MENUS(FUTURE_DESIGN, ACTION, false),

    ADDRESSES(CURRENT_SITUATION, CONSTRUCTION, false, false, false),

    TERRAINS(CURRENT_SITUATION, GEOGRAPHY, false),

    TERRAIN_TYPES(CURRENT_SITUATION, GEOGRAPHY, false),

    TERRAIN_TYPE_OVERRIDES(CURRENT_SITUATION, GEOGRAPHY, false),

    TEXTURES(CURRENT_SITUATION, CONSTRUCTION, false),

    BUILDINGS(CURRENT_SITUATION, CONSTRUCTION, false),

    CHAT_MESSAGES(COMMUNITY, INTERACTION, false),

    CINEMATIC_DATAS(TOOLS, VISUALS, false),

    CLIENT_WORDS(TOOLS, ASSETS, false),

    CUSTOM_GEOMETRIES(CURRENT_SITUATION, CONSTRUCTION, false, false, false),

    GLOBALS(CURRENT_SITUATION, CALCULATION, false),

    COSTS(FUTURE_DESIGN, FINANCIAL, false),

    DEFAULT_WORDS(null, null, false, false, false),

    LEVEES(FUTURE_DESIGN, GEOGRAPHY, false),

    EVENT_BUNDLES(FUTURE_DESIGN, ACTION, false),

    EXCEL_SHEETS(CURRENT_SITUATION, CALCULATION, false),

    NEURAL_NETWORKS(CURRENT_SITUATION, CALCULATION, false),

    FUNCTION_OVERRIDES(CURRENT_SITUATION, CONSTRUCTION, false),

    FUNCTIONS(CURRENT_SITUATION, CONSTRUCTION, false),

    SCENARIOS(MULTI_SCENARIO, CALCULATION, false),

    GEO_LINKS(TOOLS, GEO, false, false),

    GEO_PLUGINS(TOOLS, GEO, true, false),

    GEO_OPTIONS(TOOLS, GEO),

    HEIGHTS(CURRENT_SITUATION, GEOGRAPHY, false),

    INCOMES(FUTURE_DESIGN, FINANCIAL, false),

    INDICATORS(CURRENT_SITUATION, CALCULATION, false),

    PLOTS(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    MEASUREMENTS(CURRENT_SITUATION, CALCULATION, false),

    MEASURES(FUTURE_DESIGN, CONSTRUCTION, false),

    MODEL_DATAS(CURRENT_SITUATION, CONSTRUCTION, false),

    MODEL_SETS(CURRENT_SITUATION, CONSTRUCTION, false),

    MONEY_TRANSFERS(MULTI_SCENARIO, FINANCIAL, false),

    NEIGHBORHOODS(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    NET_FUNCTIONS(CURRENT_SITUATION, NETWORK, false),

    NET_NODES(CURRENT_SITUATION, NETWORK, false),

    NET_LINES(CURRENT_SITUATION, NETWORK, false),

    NET_LOADS(CURRENT_SITUATION, NETWORK, false),

    NET_CLUSTERS(CURRENT_SITUATION, NETWORK, false),

    NET_SETTINGS(CURRENT_SITUATION, NETWORK, false),

    OVERLAYS(CURRENT_SITUATION, CALCULATION, false),

    PANELS(CURRENT_SITUATION, CALCULATION, false),

    PARTICLE_EMITTERS(TOOLS, VISUALS, false),

    POPUPS(FUTURE_DESIGN, INTERACTION, false),

    ACTION_LOGS(FUTURE_DESIGN, ACTION, false),

    ERROR_LOGS(TOOLS, API, false),

    PROJECT_ASSETS(TOOLS, ASSETS),

    PARAMETRIC_DESIGNS(FUTURE_DESIGN, CONSTRUCTION),

    PARAMETRIC_EXAMPLES(null, null, false, false, false),

    PROGRESS(TOOLS, GEO),

    RECORDINGS(TOOLS, ASSETS, false),

    SERVER_WORDS(null, null, false, false, false),

    SOUNDS(TOOLS, ASSETS, false),

    SOURCES(TOOLS, GEO),

    SPECIAL_EFFECTS(TOOLS, VISUALS, false),

    SPECIAL_OPTIONS(FUTURE_DESIGN, ACTION, false),

    STAKEHOLDERS(MULTI_SCENARIO, INTERACTION, false),

    AREAS(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    ATTRIBUTE_ACTIONS(FUTURE_DESIGN, ACTION, false),

    GEO_TIFFS(CURRENT_SITUATION, GEOGRAPHY, false),

    TIMES(MULTI_SCENARIO, CONFIGURATION, false),

    TRIGGERS(TOOLS, API, false),

    UNIT_DATA_OVERRIDES(CURRENT_SITUATION, CONSTRUCTION, false),

    UNIT_DATAS(CURRENT_SITUATION, CONSTRUCTION, false),

    UPGRADE_TYPES(FUTURE_DESIGN, CONSTRUCTION, false),

    WATER_VALUES(CURRENT_SITUATION, CALCULATION, false),

    WEATHERS(TOOLS, VISUALS, false),

    ZONES(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    ZIP_CODES(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    ;

    private static List<Class<?>> classes = new ArrayList<>();
    /**
     * This position of the event contains the ENTIRE collection of the server control.
     */
    public static final int COMPLETE_COLLECTION = 0;
    /**
     * This position of the event contains only the UPDATED collection of the server control.
     */
    public static final int UPDATED_COLLECTION = 1;
    /**
     * This position of the event contains whether this is an first time update event.
     */
    public static final int FIRST_TIME = 2;

    public static final MapLink[] VALUES = MapLink.values();

    static {
        classes.add(Collection.class);
        classes.add(Collection.class);
        classes.add(Boolean.class);
    }

    public static MapLink valueOfString(final MapLink[] validmapLinks, final String string) {

        /**
         * First test normal String name compare
         */
        for (MapLink mapLink : validmapLinks) {
            if (mapLink.name().equalsIgnoreCase(string)) {
                return mapLink;
            }
        }

        /**
         * Second test human readable string
         */
        for (MapLink mapLink : validmapLinks) {
            if (StringUtils.capitalizeUnderScores(mapLink.name()).equalsIgnoreCase(string)) {
                return mapLink;
            }
        }

        // default
        return null;
    }

    public static MapLink valueOfString(SessionType sessionType, final String string) {

        if (sessionType == null) {
            return null;
        }

        /**
         * Only use mapLinks valid for this session
         */
        return valueOfString(DataLord.getSessionLinks(sessionType), string);

    }

    private final boolean[] subscriptions = new boolean[Network.AppType.values().length];
    private final MapGroup group;
    private final MapGroup.Sub groupSub;

    private MapLink(MapGroup group, MapGroup.Sub subGroup, boolean... argSubscriptions) {

        this.group = group;
        this.groupSub = subGroup;
        for (int i = 0; i < subscriptions.length; i++) {
            if (i < argSubscriptions.length) {
                subscriptions[i] = argSubscriptions[i];
            } else {
                subscriptions[i] = true;
            }
        }
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    public MapGroup getGroup() {
        return group;
    }

    public MapGroup.Sub getGroupSub() {
        return groupSub;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {
        return null;
    }

    public final String getTQLName() {
        return this.name().substring(0, this.name().length() - 1);
    }

    /**
     * When true this MapLink contains Items that can have a location (point, line or geometry) in the world.
     */
    public boolean isGeo() {

        switch (this) {
            case ADDRESSES:
            case AREAS:
            case BUILDINGS:
            case GLOBALS:
            case HEIGHTS:
            case MEASURES:
            case MEASUREMENTS:
            case NEIGHBORHOODS:
            case NET_LINES:
            case NET_LOADS:
            case NET_NODES:
            case PLOTS:
            case POPUPS:
            case TERRAINS:
            case ZIP_CODES:
            case ZONES:
                return true;
            default:
                return false;
        }
    }

    public boolean isRestoreAfterTestrun() {
        return this != PARAMETRIC_DESIGNS;
    }

    @Override
    public boolean isServerSide() {
        return false;
    }

    public boolean isValidForAppType(Network.AppType type) {
        return subscriptions[type.ordinal()];
    }
}
