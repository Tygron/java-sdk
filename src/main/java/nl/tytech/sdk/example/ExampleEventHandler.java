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

import java.util.Collection;
import nl.tytech.core.client.event.EventIDListenerInterface;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.logger.TLogger;

/**
 * Example Event handler
 *
 * @author Maxim Knepfle
 */
public class ExampleEventHandler implements EventListenerInterface, EventIDListenerInterface {

    private boolean stakeholderUpdate = false, mapUpdate = false;

    public ExampleEventHandler() {
        EventManager.addListener(this, MapLink.STAKEHOLDERS);
        EventManager.addEnumListener(this, MapLink.SETTINGS, Setting.Type.MAP_SIZE_M);
    }

    public boolean isMapUpdated() {
        return mapUpdate;
    }

    public boolean isStakeholderUpdated() {
        return stakeholderUpdate;
    }

    @Override
    public void notifyEnumListener(Event event, Enum<?> enhum) {

        if (enhum == Setting.Type.MAP_SIZE_M) {
            Setting setting = EventManager.getItem(MapLink.SETTINGS, Setting.Type.MAP_SIZE_M);
            int[] values = setting.getIntArrayValue();
            TLogger.info("Map Size set to: " + values[Item.X] + "x" + values[Item.Y]);
            mapUpdate = true;
        }
    }

    @Override
    public void notifyIDListener(Event arg0, Integer arg1) {

    }

    @Override
    public void notifyListener(Event event) {

        if (event.getType() == MapLink.STAKEHOLDERS) {
            Collection<Stakeholder> updates = event.getContent(MapLink.UPDATED_COLLECTION);
            TLogger.info("Updated stakeholders: " + updates);
            stakeholderUpdate = true;
        }
    }

}
