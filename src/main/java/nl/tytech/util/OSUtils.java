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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import nl.tytech.util.logger.TLogger;

/**
 * Util class to detect OS related settings
 *
 * @author Maxim Knepfle
 */
public class OSUtils {

    /**
     * Operating System tag
     */
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    /**
     * TyTech Engine dir.
     */
    public static final String STORAGE_DIRECTORY;
    static {
        STORAGE_DIRECTORY = getLocalAppDirectory() + File.separator + getLocalFileName() + File.separator;
    }
    /**
     * Major Java Version
     */
    public static final int JAVA_VERSION;
    static {
        String[] version = System.getProperty("java.version").split("-")[0].split("\\."); // split - due to e.g. 21-beta
        if (version.length >= 2 && version[0].equals("1")) {
            JAVA_VERSION = Integer.parseInt(version[1]); // java 8 is coded as 1.8.x
        } else if (version.length >= 1 && !version[0].equals("1")) {
            JAVA_VERSION = Integer.parseInt(version[0]); // java 11 is coded as 11.x
        } else {
            JAVA_VERSION = -1;
        }
    }

    public static final String getDisplayResolution() {

        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            return Integer.toString((int) screenSize.getWidth()) + "x" + Integer.toString((int) screenSize.getHeight());
        } catch (HeadlessException e) {
            // no desktop active
            return "headless";
        } catch (Exception e) {
            TLogger.exception(e);
            return "unknown";
        }
    }

    public static final String getHomeDir(String subdir) {

        String home = System.getProperty("user.home");

        File file = new File(home + File.separator + "My " + subdir);
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        file = new File(home + File.separator + subdir);
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        // fallback to home
        return home;
    }

    public static final String getLocalAppDirectory() {

        String userHome = System.getProperty("user.home");

        if (isWindows()) {
            /**
             * Try local appdata first
             */
            String localAppDataLocation = System.getenv("LOCALAPPDATA");
            if (localAppDataLocation != null) {
                File file = new File(localAppDataLocation);
                if (file.exists() && file.canWrite()) {
                    return localAppDataLocation;
                }
            }

            /**
             * Secondly try normal (roaming) appdata
             */
            localAppDataLocation = System.getenv("APPDATA");
            if (localAppDataLocation != null) {
                File file = new File(localAppDataLocation);
                if (file.exists() && file.canWrite()) {
                    return localAppDataLocation;
                }
            }
        } else if (isMac()) {
            // macs store it in app support dir
            return userHome + File.separator + "Library" + File.separator + "Application Support";
        }

        /**
         * Fall back to default user dir.
         */
        return userHome;
    }

    private static final String getLocalFileName() {

        String fileName = "TyTech";
        if (isLinux()) {
            // linux stores data is hidden directory
            return "." + fileName;
        }
        return fileName;
    }

    /**
     * Returns a list of MAC addresses for this system.
     * @return
     */
    public static final List<String> getMacAddresses() {

        List<String> macAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            if (nis != null) {
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = nis.nextElement();

                    // interface should not be null and not be a tunnel or something else.
                    if (ni != null && !ni.isPointToPoint()) {
                        byte[] mac = ni.getHardwareAddress();
                        if (mac != null) {
                            StringBuilder macAddress = new StringBuilder();
                            for (int i = 0; i < mac.length; i++) {
                                macAddress.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
                            }
                            // should be more then at least 2 chars.
                            if (macAddress.length() > 2) {
                                macAddresses.add(macAddress.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
        return macAddresses;
    }

    public static final boolean isAndroid() {
        return OS_NAME.contains("android");
    }

    public static final boolean isLinux() {
        return OS_NAME.contains("linux");
    }

    public static final boolean isMac() {
        return OS_NAME.contains("mac");
    }

    public static final boolean isSolaris() {
        return OS_NAME.contains("sunos");
    }

    public static final boolean isWindows() {
        return OS_NAME.contains("win");
    }

    public static final String readConsole(String text) {

        try {
            Console console = System.console();
            if (console == null) {
                System.out.println(text);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                    return reader.readLine().trim();
                }
            } else {
                return console.readLine(text).trim();
            }
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }
}
