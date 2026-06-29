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
import nl.tytech.core.item.annotations.Description;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.structure.DataLord;
import nl.tytech.naming.EngineNC;
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
    @Description("Project configuration Settings")
    SETTINGS(TOOLS, CONFIGURATION), // import load first

    @Description("Group of Actions, available to Stakeholders in a Test Run")
    ACTION_MENUS(FUTURE_DESIGN, ACTION, false),

    @Description("Unique Addresses with properties of units inside a Building")
    ADDRESSES(CURRENT_SITUATION, CONSTRUCTION, false, false, false),

    @Description("Spatial definition of a Terrain, situated either on the surface (above-ground) or sub-surface (underground)")
    TERRAINS(CURRENT_SITUATION, GEOGRAPHY, false),

    @Description("Basic properties of a type of Terrain")
    TERRAIN_TYPES(CURRENT_SITUATION, GEOGRAPHY, false),

    @Description("Stores changes made to properties of a Terrain Type")
    TERRAIN_TYPE_OVERRIDES(CURRENT_SITUATION, GEOGRAPHY, false),

    @Description("Properties of Textures used by 3D Models")
    TEXTURES(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Physical objects (e.g. houses, trees, roads) present on the surface and below ground")
    BUILDINGS(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Individual messages posted in Chat Channels")
    CHAT_MESSAGES(COMMUNITY, INTERACTION, false),

    @Description("Definitions of Chat Channels")
    CHAT_CHANNELS(COMMUNITY, INTERACTION, false),

    @Description("Definitions of automated movement of the viewport-camera in the 3D Visualization along predefined Key Points")
    CINEMATIC_DATAS(TOOLS, VISUALS, false),

    @Description("Stored texts in different languages, used in the Tygron Client")
    CLIENT_WORDS(TOOLS, ASSETS, false),

    @Description("3D Model representations of Buildings")
    CUSTOM_GEOMETRIES(CURRENT_SITUATION, CONSTRUCTION, false, false, false),

    @Description("Uniquely named Global values")
    GLOBALS(CURRENT_SITUATION, CALCULATION, false),

    @Description("Quantity of currency needed by performed Actions in a Test Run")
    COSTS(FUTURE_DESIGN, FINANCIAL, false),

    @Description("Stores translations of text parameters used in the creation of other Items")
    DEFAULT_WORDS(null, null, false, false, false),

    @Description("A definition of an artificial slope or wall to regulate water levels")
    LEVEES(FUTURE_DESIGN, GEOGRAPHY, false),

    @Description("Collection of Events that can be triggered as one package")
    EVENT_BUNDLES(FUTURE_DESIGN, ACTION, false),

    @Description("Properties of imported Excel Sheets")
    EXCEL_SHEETS(CURRENT_SITUATION, CALCULATION, false),

    @Description("Properties of imported Neural Networks (ONNX)")
    NEURAL_NETWORKS(CURRENT_SITUATION, CALCULATION, false),

    @Description("Stores changes made to properties of a Function")
    FUNCTION_OVERRIDES(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Basic properties of blueprints for Buildings")
    FUNCTIONS(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("A Scenario consists series of Settings, Measures and Events that can be activated in a Test Run")
    SCENARIOS(MULTI_SCENARIO, CALCULATION, false),

    @Description("Mapping and matching configurations for Feature to Item")
    GEO_LINKS(TOOLS, GEO, false, false),

    @Description("Configurations for importing and exporting Geo Data")
    GEO_PLUGINS(TOOLS, GEO, true, false),

    @Description("Settings related to creating a new Project")
    GEO_OPTIONS(TOOLS, GEO),

    @Description("Tiles that create the Digital Terrain Elevation Model (DTM)")
    HEIGHTS(CURRENT_SITUATION, GEOGRAPHY, false),

    @Description("The incoming money flow per Stakeholder ")
    INCOMES(FUTURE_DESIGN, FINANCIAL, false),

    @Description("Key Performance Indicators, which serve as a means to identify potential improvements and as a metric for the degree to which those improvements have been successful")
    INDICATORS(CURRENT_SITUATION, CALCULATION, false),

    @Description("Plots of land owned by a particular Stakeholder")
    PLOTS(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    @Description("Record and inspect Grid Overlay data")
    MEASUREMENTS(CURRENT_SITUATION, CALCULATION, false),

    @Description("Predefined actions that can be performed by Stakeholders")
    MEASURES(FUTURE_DESIGN, CONSTRUCTION, false),

    @Description("Definition of a 3D Model")
    MODEL_DATAS(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Set of Model Datas")
    MODEL_SETS(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Monetary transactions between Stakeholders")
    MONEY_TRANSFERS(MULTI_SCENARIO, FINANCIAL, false),

    @Description("Mutual exclusive spatial areas that divide a city or village")
    NEIGHBORHOODS(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    @Description("Shared properties of Net Lines")
    NET_FUNCTIONS(CURRENT_SITUATION, NETWORK, false),

    @Description("Connection points of Net Lines")
    NET_NODES(CURRENT_SITUATION, NETWORK, false),

    @Description("Connection of a particular type between Net Nodes")
    NET_LINES(CURRENT_SITUATION, NETWORK, false),

    @Description("Connection of a particular type between Net Nodes")
    NET_LOADS(CURRENT_SITUATION, NETWORK, false),

    @Description("Definitions of supply or demand, connected to a Net Node")
    NET_CLUSTERS(CURRENT_SITUATION, NETWORK, false),

    @Description("Configuration of properties related to a type of Network")
    NET_SETTINGS(CURRENT_SITUATION, NETWORK, false),

    @Description("Definitions of calculatable raster and vector data layers")
    OVERLAYS(CURRENT_SITUATION, CALCULATION, false),

    @Description("Customizable and calculatable interface Panels")
    PANELS(CURRENT_SITUATION, CALCULATION, false),

    @Description("3D Visual appearances that repeatedly emits particles according to a predefined pattern")
    PARTICLE_EMITTERS(TOOLS, VISUALS, false),

    @Description("Spatial definitions of Popup that require Stakeholder attention")
    POPUPS(FUTURE_DESIGN, INTERACTION, false),

    @Description("History of performed actions")
    ACTION_LOGS(FUTURE_DESIGN, ACTION, false),

    @Description("History of occurred errors")
    ERROR_LOGS(TOOLS, API, false),

    @Description("Definitions of uploaded assets")
    PROJECT_ASSETS(TOOLS, ASSETS),

    @Description("Configuration for generating spatial plans")
    PARAMETRIC_DESIGNS(FUTURE_DESIGN, CONSTRUCTION),

    @Description("Stored generated patial plans")
    PARAMETRIC_EXAMPLES(null, null, false, false, false),

    @Description("Stores the progress made by Geo Plugins during Project creation")
    PROGRESS(TOOLS, GEO),

    @Description("Stores execution time of processes in the " + EngineNC.PLATFORM_NAME)
    RECORDINGS(TOOLS, ASSETS, false),

    @Description("Stored texts in different languages, used in the " + EngineNC.PLATFORM_NAME)
    SERVER_WORDS(null, null, false, false, false),

    @Description("References to audio assets")
    SOUNDS(TOOLS, ASSETS, false),

    @Description("Definitions of Web Services and user imports")
    SOURCES(TOOLS, GEO),

    @Description("Collection of one or more particle emitters")
    SPECIAL_EFFECTS(TOOLS, VISUALS, false),

    @Description("Definitions of special Actions")
    SPECIAL_OPTIONS(FUTURE_DESIGN, ACTION, false),

    @Description("A stakeholder is any individual or organization that is affected by or can affect the outcome of a Project")
    STAKEHOLDERS(MULTI_SCENARIO, INTERACTION, false),

    @Description("Generic definitions of spatial Features with properties")
    AREAS(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    @Description("Actions to add or update multiple building attributes")
    ATTRIBUTE_ACTIONS(FUTURE_DESIGN, ACTION, false),

    @Description("Properties of imported GeoTIFFs")
    GEO_TIFFS(CURRENT_SITUATION, GEOGRAPHY, false),

    @Description("Stores the start time of a session")
    TIMES(MULTI_SCENARIO, CONFIGURATION, false),

    @Description("Definitions of executable external API scripts during project recalculation")
    TRIGGERS(TOOLS, API, false),

    @Description("Stores changes made to properties of Unit Data")
    UNIT_DATA_OVERRIDES(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Basic definition of a Type of Traffic Unit")
    UNIT_DATAS(CURRENT_SITUATION, CONSTRUCTION, false),

    @Description("Definitions of actions that change the Function of a Building")
    UPGRADE_TYPES(FUTURE_DESIGN, CONSTRUCTION, false),

    @Description("Stores categorized water volumes of Water Overlay calculations")
    WATER_VALUES(CURRENT_SITUATION, CALCULATION, false),

    @Description("Definition and properties of Weather events")
    WEATHERS(TOOLS, VISUALS, false),

    @Description("Spatial areas that can restrict and provide feedback on actions performed by Stakeholders")
    ZONES(CURRENT_SITUATION, URBAN_SUBDIVISION, false),

    @Description("Spatial areas of Zip Code areas")
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

        switch (this) {
            case PARAMETRIC_DESIGNS:
            case CHAT_CHANNELS:
            case CHAT_MESSAGES:
                return false;
            default:
                return true;
        }
    }

    @Override
    public boolean isServerSide() {
        return false;
    }

    public boolean isValidForAppType(Network.AppType type) {
        return subscriptions[type.ordinal()];
    }
}
