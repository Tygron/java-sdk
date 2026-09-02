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
package nl.tytech.sdk.example;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.SessionConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.editor.item.ChatMessage;
import nl.tytech.data.editor.item.DefaultWord;
import nl.tytech.data.editor.item.GeoLink;
import nl.tytech.data.editor.item.GeoOption;
import nl.tytech.data.editor.item.GeoPlugin;
import nl.tytech.data.editor.item.Progress;
import nl.tytech.data.engine.item.ActionLog;
import nl.tytech.data.engine.item.ActionMenu;
import nl.tytech.data.engine.item.Address;
import nl.tytech.data.engine.item.Area;
import nl.tytech.data.engine.item.AttributeAction;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.item.CinematicData;
import nl.tytech.data.engine.item.ClientWord;
import nl.tytech.data.engine.item.CostBookValue;
import nl.tytech.data.engine.item.CustomGeometry;
import nl.tytech.data.engine.item.DecalTexture;
import nl.tytech.data.engine.item.ErrorLog;
import nl.tytech.data.engine.item.EventBundle;
import nl.tytech.data.engine.item.ExcelSheet;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.FunctionOverride;
import nl.tytech.data.engine.item.GeoTiff;
import nl.tytech.data.engine.item.Global;
import nl.tytech.data.engine.item.HeightSector;
import nl.tytech.data.engine.item.IncomeBookValue;
import nl.tytech.data.engine.item.Indicator;
import nl.tytech.data.engine.item.Levee;
import nl.tytech.data.engine.item.Measure;
import nl.tytech.data.engine.item.Measurement;
import nl.tytech.data.engine.item.ModelData;
import nl.tytech.data.engine.item.ModelSet;
import nl.tytech.data.engine.item.MoneyTransfer;
import nl.tytech.data.engine.item.Neighborhood;
import nl.tytech.data.engine.item.NetCluster;
import nl.tytech.data.engine.item.NetFunction;
import nl.tytech.data.engine.item.NetLine;
import nl.tytech.data.engine.item.NetLoad;
import nl.tytech.data.engine.item.NetNode;
import nl.tytech.data.engine.item.NetSetting;
import nl.tytech.data.engine.item.Overlay;
import nl.tytech.data.engine.item.Panel;
import nl.tytech.data.engine.item.ParametricDesign;
import nl.tytech.data.engine.item.ParticleEmitterModel;
import nl.tytech.data.engine.item.Plot;
import nl.tytech.data.engine.item.PopupData;
import nl.tytech.data.engine.item.ProjectAsset;
import nl.tytech.data.engine.item.Scenario;
import nl.tytech.data.engine.item.ServerWord;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Sound;
import nl.tytech.data.engine.item.SpecialEffect;
import nl.tytech.data.engine.item.SpecialOption;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Terrain;
import nl.tytech.data.engine.item.TerrainType;
import nl.tytech.data.engine.item.TerrainTypeOverride;
import nl.tytech.data.engine.item.UnitData;
import nl.tytech.data.engine.item.UnitDataOverride;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.data.engine.item.WaterBookValue;
import nl.tytech.data.engine.item.Weather;
import nl.tytech.data.engine.item.ZipCode;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.ThreadUtils;

/**
 * Dummy example test that creates a project and retrieves the items.
 *
 * @author Maxim Knepfle
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleItemsTest {

    private static Integer sessionID;

    private static JoinReply reply;

    private static ProjectData data;

    private static SessionConnection sessionConnection;

    private static ExampleAPIConnection apiConnection;

    private static ExampleEventHandler eventHandler;

    @Test
    public void test02Connect() throws Exception {

        apiConnection = new ExampleAPIConnection();
        User user = apiConnection.fireServerEvent(UserServiceEventType.GET_MY_USER);
        assertNotNull(user);

        assertTrue("User access level must be at least EDITOR to run these tests.",
                user.getMaxAccessLevel().ordinal() >= AccessLevel.EDITOR.ordinal());
    }

    @Test
    public void test03CreateNewProject() throws Exception {

        String projectName = "test" + System.currentTimeMillis();
        data = apiConnection.fireServerEvent(IOServiceEventType.ADD_PROJECT, projectName, TLanguage.EN);
        assertNotNull(data);
    }

    @Test
    public void test04StartEditSessionAsEditor() throws Exception {

        sessionID = apiConnection.fireServerEvent(IOServiceEventType.START, SessionType.EDITOR, data.getFileName(), TLanguage.EN);
        assertTrue(sessionID != null && sessionID >= 0);

        reply = apiConnection.fireServerEvent(IOServiceEventType.JOIN, sessionID, AppType.EDITOR);
        assertNotNull(reply);

        sessionConnection = new SessionConnection(apiConnection);
        sessionConnection.initSettings(AppType.EDITOR, SettingsManager.getServerAddress(), sessionID, reply.apiToken,
                reply.client.getClientToken());

        assertTrue(sessionConnection.connect());

        // Add event handler to receive updates
        eventHandler = new ExampleEventHandler();
    }

    @Test
    public void test05doEditSession() throws Exception {

        int mapSizeM = 500;
        sessionConnection.fireServerEvent(true, null, Format.DEFAULT_EVENT, EditorEventType.SET_INITIAL_MAP_SIZE, mapSizeM, mapSizeM);
        sessionConnection.fireServerEvent(true, null, Format.DEFAULT_EVENT, EditorSettingEventType.WIZARD_FINISHED);

        /**
         * Add a civilian stakeholder
         */
        sessionConnection.fireServerEvent(true, null, Format.DEFAULT_EVENT, EditorStakeholderEventType.ADD_WITH_TYPE_AND_ACTIVE,
                Stakeholder.Type.CIVILIAN, true);

        // Wait for initial updates (separate thread)
        boolean updated = false;
        for (int i = 0; i < 60; i++) {
            if (eventHandler.isMapUpdated() && eventHandler.isStakeholderUpdated()) {
                updated = true;
                break;
            }
            ThreadUtils.sleepInterruptible(1000);
        }
        assertTrue(updated);
    }

    @SuppressWarnings("unused")
    public void test06retrieveItemsFromMap() {

        // Geometric and related
        ItemMap<Address> addresses = EventManager.<Address> getItemMap(MapLink.ADDRESSES);
        ItemMap<Area> areas = EventManager.<Area> getItemMap(MapLink.AREAS);
        ItemMap<Building> buildings = EventManager.<Building> getItemMap(MapLink.BUILDINGS);
        ItemMap<Function> functions = EventManager.<Function> getItemMap(MapLink.FUNCTIONS);
        ItemMap<FunctionOverride> functionOverrides = EventManager.<FunctionOverride> getItemMap(MapLink.FUNCTION_OVERRIDES);
        ItemMap<HeightSector> heightSectors = EventManager.<HeightSector> getItemMap(MapLink.HEIGHTS);
        ItemMap<Neighborhood> neighborhoods = EventManager.<Neighborhood> getItemMap(MapLink.NEIGHBORHOODS);
        ItemMap<Plot> plots = EventManager.<Plot> getItemMap(MapLink.PLOTS);
        ItemMap<TerrainType> terrainTypes = EventManager.<TerrainType> getItemMap(MapLink.TERRAIN_TYPES);
        ItemMap<TerrainTypeOverride> terrainTypeOverrides = EventManager.<TerrainTypeOverride> getItemMap(MapLink.TERRAIN_TYPE_OVERRIDES);
        ItemMap<Terrain> terrains = EventManager.<Terrain> getItemMap(MapLink.TERRAINS);

        // Stakeholder and Actions
        ItemMap<ActionLog> actionLogs = EventManager.<ActionLog> getItemMap(MapLink.ACTION_LOGS);
        ItemMap<ActionMenu> actionMenus = EventManager.<ActionMenu> getItemMap(MapLink.ACTION_MENUS);
        ItemMap<AttributeAction> attributeActions = EventManager.<AttributeAction> getItemMap(MapLink.ATTRIBUTE_ACTIONS);
        ItemMap<EventBundle> eventBundles = EventManager.<EventBundle> getItemMap(MapLink.EVENT_BUNDLES);
        ItemMap<Levee> levees = EventManager.<Levee> getItemMap(MapLink.LEVEES);
        ItemMap<Measure> measures = EventManager.<Measure> getItemMap(MapLink.MEASURES);
        ItemMap<ParametricDesign> parametricDesigns = EventManager.<ParametricDesign> getItemMap(MapLink.PARAMETRIC_DESIGNS);
        ItemMap<PopupData> popupDatas = EventManager.<PopupData> getItemMap(MapLink.POPUPS);
        ItemMap<SpecialOption> specialOptions = EventManager.<SpecialOption> getItemMap(MapLink.SPECIAL_OPTIONS);
        ItemMap<Stakeholder> stakeholders = EventManager.<Stakeholder> getItemMap(MapLink.STAKEHOLDERS);
        ItemMap<UpgradeType> upgradeTypes = EventManager.<UpgradeType> getItemMap(MapLink.UPGRADE_TYPES);
        ItemMap<ZipCode> zipCodes = EventManager.<ZipCode> getItemMap(MapLink.ZIP_CODES);
        ItemMap<Zone> zones = EventManager.<Zone> getItemMap(MapLink.ZONES);

        // Indicators, grids and results
        ItemMap<ExcelSheet> excelSheets = EventManager.<ExcelSheet> getItemMap(MapLink.EXCEL_SHEETS);
        ItemMap<GeoTiff> geoTiffs = EventManager.<GeoTiff> getItemMap(MapLink.GEO_TIFFS);
        ItemMap<Global> globals = EventManager.<Global> getItemMap(MapLink.GLOBALS);
        ItemMap<Indicator> indicators = EventManager.<Indicator> getItemMap(MapLink.INDICATORS);
        ItemMap<Measurement> measurement = EventManager.<Measurement> getItemMap(MapLink.MEASUREMENTS);
        ItemMap<Overlay> overlays = EventManager.<Overlay> getItemMap(MapLink.OVERLAYS);
        ItemMap<Panel> panels = EventManager.<Panel> getItemMap(MapLink.PANELS);
        ItemMap<WaterBookValue> waterBookValues = EventManager.<WaterBookValue> getItemMap(MapLink.WATER_VALUES);
        ItemMap<Weather> weathers = EventManager.<Weather> getItemMap(MapLink.WEATHERS);

        // financial
        ItemMap<CostBookValue> costBookValues = EventManager.<CostBookValue> getItemMap(MapLink.COSTS);
        ItemMap<IncomeBookValue> incomeBookValues = EventManager.<IncomeBookValue> getItemMap(MapLink.INCOMES);
        ItemMap<MoneyTransfer> moneyTranfsers = EventManager.<MoneyTransfer> getItemMap(MapLink.MONEY_TRANSFERS);

        // networks
        ItemMap<NetCluster> netClusters = EventManager.<NetCluster> getItemMap(MapLink.NET_CLUSTERS);
        ItemMap<NetFunction> netFunctions = EventManager.<NetFunction> getItemMap(MapLink.NET_FUNCTIONS);
        ItemMap<NetLine> netLines = EventManager.<NetLine> getItemMap(MapLink.NET_LINES);
        ItemMap<NetLoad> netLoads = EventManager.<NetLoad> getItemMap(MapLink.NET_LOADS);
        ItemMap<NetNode> netNodes = EventManager.<NetNode> getItemMap(MapLink.NET_NODES);
        ItemMap<NetSetting> netSettings = EventManager.<NetSetting> getItemMap(MapLink.NET_SETTINGS);

        // Session
        ItemMap<ChatMessage> chatMessages = EventManager.<ChatMessage> getItemMap(MapLink.CHAT_MESSAGES);
        ItemMap<Scenario> scenarios = EventManager.<Scenario> getItemMap(MapLink.SCENARIOS);
        ItemMap<Setting> settings = EventManager.<Setting> getItemMap(MapLink.SETTINGS);
        ItemMap<ErrorLog> errorLogs = EventManager.<ErrorLog> getItemMap(MapLink.ERROR_LOGS);
        ItemMap<Moment> moments = EventManager.<Moment> getItemMap(MapLink.TIMES);

        // Project generation
        ItemMap<GeoLink> geoLinks = EventManager.<GeoLink> getItemMap(MapLink.GEO_LINKS);
        ItemMap<GeoOption> geoOption = EventManager.<GeoOption> getItemMap(MapLink.GEO_OPTIONS);
        ItemMap<GeoPlugin> geoPlugins = EventManager.<GeoPlugin> getItemMap(MapLink.GEO_PLUGINS);
        ItemMap<Progress> progresses = EventManager.<Progress> getItemMap(MapLink.PROGRESS);

        // Visual and audio
        ItemMap<CinematicData> cinematicData = EventManager.<CinematicData> getItemMap(MapLink.CINEMATIC_DATAS);
        ItemMap<CustomGeometry> customGeometries = EventManager.<CustomGeometry> getItemMap(MapLink.CUSTOM_GEOMETRIES);
        ItemMap<ModelData> modelDatass = EventManager.<ModelData> getItemMap(MapLink.MODEL_DATAS);
        ItemMap<ModelSet> modelSets = EventManager.<ModelSet> getItemMap(MapLink.MODEL_SETS);
        ItemMap<ParticleEmitterModel> particleEmitters = EventManager.<ParticleEmitterModel> getItemMap(MapLink.PARTICLE_EMITTERS);
        ItemMap<Sound> sounds = EventManager.<Sound> getItemMap(MapLink.SOUNDS);
        ItemMap<SpecialEffect> specialEffects = EventManager.<SpecialEffect> getItemMap(MapLink.SPECIAL_EFFECTS);
        ItemMap<DecalTexture> decalTextures = EventManager.<DecalTexture> getItemMap(MapLink.TEXTURES);
        ItemMap<UnitDataOverride> unitDataOverrides = EventManager.<UnitDataOverride> getItemMap(MapLink.UNIT_DATA_OVERRIDES);
        ItemMap<UnitData> unitDatas = EventManager.<UnitData> getItemMap(MapLink.UNIT_DATAS);

        // Additional
        ItemMap<ClientWord> clientWords = EventManager.<ClientWord> getItemMap(MapLink.CLIENT_WORDS);
        ItemMap<DefaultWord> defaultWords = EventManager.<DefaultWord> getItemMap(MapLink.DEFAULT_WORDS);
        ItemMap<ProjectAsset> projectAssets = EventManager.<ProjectAsset> getItemMap(MapLink.PROJECT_ASSETS);
        ItemMap<ServerWord> serverWords = EventManager.<ServerWord> getItemMap(MapLink.SERVER_WORDS);

    }

    @Test
    public void test07closeEditSession() throws Exception {

        /**
         * Save project in our sesionID
         */
        Boolean result = apiConnection.fireServerEvent(IOServiceEventType.SAVE_PROJECT, sessionID);
        assertTrue(result);
        /**
         * Disconnect from sesionID
         */
        sessionConnection.disconnect(false);
    }

    @Test
    public void test08deleteProject() throws Exception {

        apiConnection.fireServerEvent(IOServiceEventType.SET_PROJECT_TRASHED, data.getFileName(), true);
        data = apiConnection.fireServerEvent(IOServiceEventType.GET_PROJECT, data.getFileName());
        assertTrue(data.isTrashed());
    }
}
