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
package nl.tytech.core.net;

import nl.tytech.core.net.serializable.MapLink;

/**
 * Tygron Rest directory def.
 *
 * @author Maxim Knepfle
 *
 */
public class Rest {

    public static final String API = "api/";

    public static final String ITEMS = "items/";

    public static final String EVENT = "event/";

    public static final String WEB = "web/";

    public static final String DEVELOPER = "developer/";

    public static final String PROJECTS = "projects/";

    public static final String ASSETS = "assets/";

    public static final String STREETVIEW = "streetview/";

    public static final String LOGS = "logs/";

    public static final String SESSIONS = "sessions/";

    public static final String AUTH = "auth/";

    public static final String SESSION = "session/";

    public static final String SHARE = "share/";

    public static final String STREAM = "stream/";

    public static final String DOMAINS = "domains/";

    public static final String USERS = "users/";

    public static final String MY_USER = "myuser/";

    public static final String GPU = "gpu/";

    public static final String JOBS = "jobs";

    public static final String CLUSTERS = "clusters";

    public static final String INFO = "info";

    public static final String LOCATION = "location";

    public static final String TOKEN_TAG = "$TOKEN";

    public static final String TQL = "query/";

    public static final String POLL = "poll";

    public static final String CODES = "codes/";

    public static final String WFS = "wfs";

    public static final String WMS = "wms";

    public static final String OVERLAY = "overlay";

    public static final String RESET_TIMEOUT = "resettimeout";

    public static final String TILES3D = "3dtiles/";

    public static final String TILES3D_ENDPOINT = "tileset.json";

    /**
     * Tag for both deprecated SLOT name and newer SESSION name
     */
    public static final String SESSION_TAGS = "(\\$SLOT|\\$SESSION)";

    public static final String CLOSE_PANEL_TAG = "$CLOSE_PANEL";
    public static final String CLOSE_PANEL_WITH_ANSWER_TAG = "$CLOSE_PANEL_WITH_ANSWER_ID_";

    /**
     * MapLink accessible via WFS
     */
    public static final MapLink[] WFS_MAPLINKS = new MapLink[] { MapLink.AREAS, MapLink.BUILDINGS, MapLink.NEIGHBORHOODS, MapLink.NET_LINES,
            MapLink.NET_LOADS, MapLink.TERRAINS, MapLink.ZONES, MapLink.PLOTS, MapLink.MEASURES, MapLink.MEASUREMENTS };

}
