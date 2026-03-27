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
package nl.tytech.core.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.Network.SessionType;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.PackageUtils;
import nl.tytech.util.logger.TLogger;

/**
 * OverLord defines the controllers, events, file locations etc of the session.
 *
 * @author Maxim Knepfle
 */
public class DataLord {

    private static final class SingletonHolder {

        private static final DataLord INSTANCE = new DataLord();
    }

    public enum Space {

        CORE,

        ENGINE,

        EDITOR
    }

    /**
     * Default platform specific location of the item classes.
     */
    private static final String PLATFORM_ITEM_LOCATION = "nl.tytech.data.SPACE.item";

    /**
     * Default platform specific location of the Serializable classes.
     */
    private static final String PLATFORM_SERIALIZABLE_LOCATION = "nl.tytech.data.SPACE.serializable";

    /**
     * Default platform specific location of the server-side events
     */

    private static final String PLATFORM_EVENT_LOCATION = "nl.tytech.data.SPACE.event";

    /**
     * Used for internal network synchronization only. Do not call unless you know what you are doing.
     * @return
     */
    public static final List<MapLink> getAllLinks() {
        return SingletonHolder.INSTANCE.allPossibleLinks;
    }

    public static final MapLink[] getAppLinks(SessionType sessionType, AppType appType) {
        return SingletonHolder.INSTANCE._getAppLinks(sessionType, appType);
    }

    @SuppressWarnings("unchecked")
    public static final List<Class<? extends EventTypeEnum>> getEventClasses() {

        List<Class<? extends EventTypeEnum>> classes = new ArrayList<>();
        for (Space space : Space.values()) {
            List<String> classNames = PackageUtils
                    .getPackageClassNames(PLATFORM_EVENT_LOCATION.replaceAll("SPACE", space.name().toLowerCase()));
            for (String className : classNames) {
                try {
                    classes.add((Class<? extends EventTypeEnum>) Class.forName(className));
                } catch (Exception e) {
                    TLogger.exception(e);
                }
            }
        }
        return classes;
    }

    public static final MapLink getLink(EventIDField eventIDField, int parameterIndex) {

        if (eventIDField == null) {
            return null;
        }

        for (int i = 0; i < eventIDField.params().length; i++) {
            int value = eventIDField.params()[i];
            if (value == parameterIndex) {
                return eventIDField.links()[i];
            }
        }
        return null;
    }

    public static final MapLink[] getSessionLinks(SessionType sessionType) {
        return SingletonHolder.INSTANCE._getSessionLinks(sessionType);
    }

    public static void setup(Map<Network.SessionType, MapLink[]> mapLinks) {
        SingletonHolder.INSTANCE._setup(mapLinks);
    }

    private boolean setupNamespace = false;

    /**
     * Do not try to setup the Lord simultaneous you plebs!
     */
    private final Object setupLock = new Object();

    private final List<MapLink> allPossibleLinks = new ArrayList<>();

    private final Map<SessionType, MapLink[]> sessionLinkMap = new EnumMap<>(SessionType.class);

    private final Map<SessionType, EnumMap<AppType, MapLink[]>> appLinkMap = new EnumMap<>(SessionType.class);

    /**
     * The constructor.
     */
    private DataLord() {

    }

    private final MapLink[] _getAppLinks(SessionType sessionType, AppType appType) {

        if (sessionType == null || appType == null) {
            TLogger.severe("Missing info for requesting MapLinks!");
            return null;
        }
        return appLinkMap.get(sessionType).get(appType);
    }

    private final MapLink[] _getSessionLinks(SessionType sessionType) {

        if (sessionType == null) {
            TLogger.severe("Missing info for requesting MapLinks!");
            return null;
        }
        return sessionLinkMap.get(sessionType);
    }

    private void _setup(Map<Network.SessionType, MapLink[]> mapLinks) {

        synchronized (setupLock) {

            // get session type mapLinks
            for (Entry<Network.SessionType, MapLink[]> entry : mapLinks.entrySet()) {

                // only add when sessionType when it is unknown to me
                if (!sessionLinkMap.containsKey(entry.getKey())) {

                    // (Frank) sort on maplink ordinal for dependencies
                    Arrays.sort(entry.getValue());

                    // store in sessions
                    sessionLinkMap.put(entry.getKey(), entry.getValue());

                    // store in ALL types
                    for (MapLink mapLink : entry.getValue()) {
                        // also save all possible variants.
                        if (!allPossibleLinks.contains(mapLink)) {
                            this.allPossibleLinks.add(mapLink);
                        }
                    }

                    // store per session/app type
                    EnumMap<Network.AppType, MapLink[]> appTypeMap = new EnumMap<>(Network.AppType.class);
                    for (AppType appType : AppType.values()) {
                        List<MapLink> newList = new ArrayList<>();
                        MapLink[] oldArray = entry.getValue();
                        for (MapLink link : oldArray) {
                            if (link.isValidForAppType(appType)) {
                                newList.add(link);
                            }
                        }
                        // TLogger.info("Loaded: " + entry.getKey() + ": " + appType + ": " + newList.size());
                        appTypeMap.put(appType, newList.toArray(new MapLink[newList.size()]));
                    }
                    this.appLinkMap.put(entry.getKey(), appTypeMap);
                }
            }

            if (!setupNamespace) {
                // first time, setup name
                setupNamespace = true;

                for (Space space : Space.values()) {
                    if (!ItemNamespace.addPackageClasses(PLATFORM_EVENT_LOCATION.replaceAll("SPACE", space.name().toLowerCase()))) {
                        TLogger.warning("A problem occurred while loading event classes.");
                    }
                    if (!ItemNamespace.addPackageClasses(PLATFORM_SERIALIZABLE_LOCATION.replaceAll("SPACE", space.name().toLowerCase()))) {
                        TLogger.warning("A problem occurred while loading serializable classes.");
                    }
                    if (!ItemNamespace.addPackageClasses(PLATFORM_ITEM_LOCATION.replaceAll("SPACE", space.name().toLowerCase()))) {
                        TLogger.warning("A problem occurred while loading item classes.");
                    }
                }
            }
        }
    }
}
