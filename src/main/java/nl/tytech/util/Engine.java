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
package nl.tytech.util;

import nl.tytech.core.net.Network.AppType;
import nl.tytech.data.core.item.Moment;
import nl.tytech.naming.EngineNC;
import se.sawano.java.text.AlphanumericComparator;

/**
 * Engine version definitions class
 *
 * @author Maxim Knepfle
 *
 */
public class Engine {

    /**
     * Clients using API/SDK are compatible on this version level.
     */
    public static final String VERSION_API_COMPATIBLE = "2026.0.10";

    /**
     * Minor version updates, no breaking API/SDK changes here.
     */
    private static final int MINOR_VERSION = 0;

    /**
     * When true this version is flagged as BETA.
     */
    private static final boolean BETA = true;

    /**
     * App version based on API version extended with minor updates
     */
    public static final String VERSION = VERSION_API_COMPATIBLE + "." + MINOR_VERSION;

    /**
     * Redirect all Clients to new Location
     */
    public static final String REDIRECT_SERVER = null;// "LTS https://engine.tygron.com";

    public static String PLATFORM_NAME_TYPED = EngineNC.PLATFORM_NAME;

    public static String SERVER_NAME_TYPED = EngineNC.SERVER_NAME;

    public static String CLIENT_NAME_TYPED = EngineNC.CLIENT_NAME;

    public static String USER_AGENT = EngineNC.PLATFORM_NAME + " (" + VERSION + ")";

    private static final long RELEASE_DATE = Moment.getMillis(2026, 3, 27, 20);

    public static final long NEXT_RELEASE_DATE = Moment.getMillis(2028, 3, 3, 20);

    public static final String UPDATING = "UPDATING";

    private static ServerType serverType;

    public static final String getAddressName(String address) {

        StringBuilder builder = new StringBuilder();
        builder.append(Engine.isPublicAccess() ? "Public " : "Private ");

        // type info
        if (isDevVersion()) {
            builder.append("DEV ");
        } else if (isRCVersion()) {
            builder.append("RC ");
        } else if (isBeta()) {
            builder.append("BETA ");
        } else if (getServerType() != ServerType.LTS) {
            builder.append("GA ");
        }

        builder.append(address);
        return builder.toString();
    }

    public static final ServerType getServerType() {
        return serverType;
    }

    public static final String htmlNext() {

        long timeMS = NEXT_RELEASE_DATE - System.currentTimeMillis();
        return timeMS > 0 ? "Next Update in: " + StringUtils.toSimpleTime(timeMS) : "";
    }

    public static final String htmlRelease() {

        long timeMS = RELEASE_DATE - System.currentTimeMillis();
        if (timeMS < 0) {
            return "Released: " + StringUtils.toSimpleTime(Math.abs(timeMS)) + " ago";
        } else {
            return "Release Deadline: " + StringUtils.toSimpleTime(timeMS);
        }
    }

    public static final boolean isBeta() {
        return BETA && getServerType() == ServerType.Preview;
    }

    public static final boolean isDevVersion() {
        return VERSION.toLowerCase().contains("dev");
    }

    public static final boolean isLower(String thisVersion, String thatVersion) {
        return isLower(thisVersion, thatVersion, "\\.");
    }

    private static final boolean isLower(String thisVersion, String thatVersion, String splitter) {

        if (thisVersion == null) {
            return true;// NULL is lowest possible version
        }
        if (thatVersion == null) {
            return false;// NULL is lowest possible version
        }

        String[] thisParts = thisVersion.toLowerCase().trim().split(splitter);
        String[] thatParts = thatVersion.toLowerCase().trim().split(splitter);

        for (int i = 0; i < thisParts.length; i++) {
            if (i >= thatParts.length) {
                return false;
            }
            if (thisParts[i].equals(thatParts[i])) {
                continue;
            }
            try {
                int thisPart = Integer.parseInt(thisParts[i]);
                int thatPart = Integer.parseInt(thatParts[i]);
                return thisPart < thatPart;
            } catch (NumberFormatException e) {
                // start with same number?
                if (thatParts[i].startsWith(thisParts[i]) || thisParts[i].startsWith(thatParts[i])) {
                    if (thisParts[i].contains(StringUtils.WHITESPACE) && thatParts[i].contains(StringUtils.WHITESPACE)) {
                        return isLower(thisParts[i], thatParts[i], StringUtils.WHITESPACE); // both
                    } else if (!thisParts[i].contains(StringUtils.WHITESPACE) && thatParts[i].contains(StringUtils.WHITESPACE)) {
                        return false; // dev and rc version are not lower then final
                    } else if (thisParts[i].contains(StringUtils.WHITESPACE) && !thatParts[i].contains(StringUtils.WHITESPACE)) {
                        return true; // dev and rc version are lower then final
                    }
                }
                return new AlphanumericComparator().compare(thisParts[i], thatParts[i]) < 0;
            }
        }
        return false;
    }

    public static final boolean isPublicAccess() {
        return serverType != null && serverType.isPublicAccess();
    }

    public static final boolean isRCVersion() {
        return VERSION.toLowerCase().contains("rc");
    }

    public static final void setup(ServerType type, AppType appType) {

        serverType = type;
        PLATFORM_NAME_TYPED = EngineNC.PLATFORM_NAME + " " + type;
        CLIENT_NAME_TYPED = appType.getAppName() + " " + type;
        SERVER_NAME_TYPED = EngineNC.SERVER_NAME + " " + type;
        USER_AGENT = appType.getAppName() + " (" + VERSION + ")";
    }
}
