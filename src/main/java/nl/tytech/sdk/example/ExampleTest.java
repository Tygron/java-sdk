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
import org.locationtech.jts.geom.MultiPolygon;
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
import nl.tytech.data.editor.event.EditorEventType;
import nl.tytech.data.editor.event.EditorSettingEventType;
import nl.tytech.data.editor.event.EditorStakeholderEventType;
import nl.tytech.data.engine.event.LogicEventType;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Dummy example test that creates a project and executes some actions.
 *
 * @author Maxim Knepfle
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleTest {

    private static Integer sessionID;

    private static JoinReply reply;

    private static ProjectData data;

    private static SessionConnection sessionConnection;

    private static ExampleAPIConnection apiConnection;

    private static ExampleEventHandler eventHandler;

    private static Integer stakeholderID = 0;

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

        // wait on first updates (separate thread)
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

    @Test
    public void test06closeEditSession() throws Exception {

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
    public void test07startRegularSessionAsParticipant() throws Exception {

        sessionID = apiConnection.fireServerEvent(IOServiceEventType.START, SessionType.SINGLE, data.getFileName(), TLanguage.EN);
        assertTrue(sessionID != null && sessionID >= 0);

        reply = apiConnection.fireServerEvent(IOServiceEventType.JOIN, sessionID, AppType.PARTICIPANT);
        assertNotNull(reply);

        sessionConnection = new SessionConnection(apiConnection);
        sessionConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerAddress(), sessionID, reply.apiToken,
                reply.client.getClientToken());

        assertTrue(sessionConnection.connect());

        // Add event handler to receive updates
        eventHandler = new ExampleEventHandler();
    }

    @Test
    public void test08selectStakeholderToPlay() throws Exception {

        stakeholderID = 0;
        ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);
        for (Stakeholder stakeholder : stakeholders) {
            stakeholderID = stakeholder.getID();
            TLogger.info("Selecting stakeholder: " + stakeholder.getName());
            break;
        }
        sessionConnection.fireServerEvent(true, null, Format.DEFAULT_EVENT, LogicEventType.STAKEHOLDER_SELECT, stakeholderID,
                reply.client.getClientToken());
    }

    @Test
    public void test09planBuilding() throws Exception {

        /**
         * Plan an new ROAD construction
         */
        Integer functionID = 0;
        int floors = 1;
        ItemMap<Function> functions = EventManager.getItemMap(MapLink.FUNCTIONS);
        for (Function function : functions) {
            if (function.getCategories().contains(Category.ROAD)) {
                functionID = function.getID();
                TLogger.info("Selecting road function: " + function.getName());
                break;
            }
        }

        /**
         * Shape of my new road
         */
        MultiPolygon roadMultiPolygon = JTSUtils.createRectangle(10, 10, 200, 10);

        Integer newBuildingID = sessionConnection.fireServerEvent(true, null, Format.DEFAULT_EVENT,
                ParticipantEventType.BUILDING_PLAN_CONSTRUCTION, stakeholderID, functionID, floors, roadMultiPolygon);

        assertTrue(newBuildingID.intValue() >= 0);

    }

    @Test
    public void test10closeRegularSession() throws Exception {
        sessionConnection.disconnect(false);
    }

    @Test
    public void test11deleteProject() throws Exception {

        apiConnection.fireServerEvent(IOServiceEventType.SET_PROJECT_TRASHED, data.getFileName(), true);
        data = apiConnection.fireServerEvent(IOServiceEventType.GET_PROJECT, data.getFileName());
        assertTrue(data.isTrashed());
    }
}
