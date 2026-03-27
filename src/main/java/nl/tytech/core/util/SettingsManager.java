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
package nl.tytech.core.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.serializable.TokenPair;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.TLanguage;
import nl.tytech.locale.unit.UnitSystemType;
import nl.tytech.util.Base64;
import nl.tytech.util.Engine;
import nl.tytech.util.OSUtils;
import nl.tytech.util.ServerConfig;
import nl.tytech.util.ServerType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.WebUtils;
import nl.tytech.util.logger.TLogger;

/**
 * App settings are stored here
 *
 * @author Jeroen Warmerdam
 */
public class SettingsManager {

    public enum AssetType {

        /**
         * Assets that are packed into the client app. These assets should always be used (e.g. panel xmls and fonts that are always used).
         */
        PACKED,

        /**
         * Assets that are streamed over the Internet. These assets are used in specific project (e.g. models, images).
         */
        STREAM,

        /**
         * Assets that are only used by the Server app. Thinks like project xmls or geo data.
         */
        SERVER,

        /**
         * Assets that are uploaded by the users.
         */
        UPLOAD;

        private static final String DEFAULT_ECLIPSE_GENERIC_ASSETS_FOLDER = ".." + File.separator + "assets" + File.separator;

        private static final String DEFAULT_ECLIPSE_ASSETS_FOLDER = ".." + File.separator + "distribution" + File.separator;

        public String getDevFolder() {
            return DEFAULT_ECLIPSE_ASSETS_FOLDER + StringUtils.capitalize(this.name()) + File.separator;
        }

        public String getDevGenericDefault() {
            return DEFAULT_ECLIPSE_GENERIC_ASSETS_FOLDER + StringUtils.capitalize(this.name()) + File.separator;
        }

        public boolean isDevGenericDefault() {
            return new File(getDevGenericDefault()).exists();
        }
    }

    /**
     * End-User Conditions
     *
     */
    public enum Conditions {

        NL("end_user_conditions_nl.txt"),

        EN("end_user_conditions_en.txt"),;

        private String location;

        private Conditions(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }

    /**
     * Three levels of run mode
     *
     * @author Maxim Knepfle
     */
    public enum RunMode {

        /**
         * Release version, goes to the customer
         */
        RELEASE,

        /**
         * Normal mode used for development in Eclipse/IDE
         */
        DEV,

        /**
         * Mode when running tests, e.g. in Jenkins
         */
        TEST,

        /**
         * Shows the model batches in the world with random colours.
         */
        BATCHING;
    }

    /**
     * Save a timestamp to the windows reg every X secs.
     */
    private static class SettingsActive extends Thread {

        private static final int PERIOD = (int) Moment.SECOND;

        private final File stampFile;

        public volatile boolean active = true;

        private SettingsActive(String instanceID) {
            this.stampFile = new File(TIMESTAMP_DIR + instanceID);
            this.setName("Daemon-SettingsSync");
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (active) {
                saveTimeStamp();
                ThreadUtils.sleepInterruptible(PERIOD);
            }
            TLogger.info("Stopped saving timestamps!");
        }

        private final void saveTimeStamp() {
            try {
                stampFile.getParentFile().mkdirs();
                stampFile.createNewFile();
                stampFile.setLastModified(System.currentTimeMillis());
                return;
            } catch (Exception e) {
                TLogger.exception(e);
            }
        }
    }

    /**
     * Event signaling that a _setting is updated. This event contains the _setting type and the new value.
     */
    public static enum SettingsEventType implements EventTypeEnum {

        SETTINGS_UPDATED(SettingsType.class, Object.class),

        PROJECT_NAME_UPDATED(String.class);

        private List<Class<?>> classes;

        private SettingsEventType(Class<?>... classes) {
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
            return null;
        }

        @Override
        public boolean isServerSide() {
            return false;
        }

    }

    /**
     * An enum defining the type of setting. This is used as a key in the properties file.
     */
    public static enum SettingsType {

        CONDITIONS_CHECKSUM(""),

        CONDITIONS_REGION(""),

        ASSET_CONFIG_URL("Config/AssetConfig.cfg"),

        WATERMARK("Copyright TyTech BV"),

        CLIENT_TOKEN(StringUtils.randomToken()),

        CAMERA_STYLE("ANGLED"),

        STORED_USERNAME(""),

        STORED_KEY(""),

        STORED_KEY_EXPIRE_DATE(-1L),

        TEMP_RUNMODE(RunMode.RELEASE),

        FULLSCREEN(true),

        LOGTOFILE(false),

        SERVER_SESSION_ID(-1),

        SERVER_AMOUNT_OF_CONNECTIONS(-1),

        API_TOKEN(StringUtils.randomToken()),

        SCREEN_POSX(-1),

        SCREEN_POSY(0),

        SCREEN_SHADOWS(1),

        REQUESTED_SCALE(-1.0),

        SCREEN_ANTI_ALIASING(0),

        SCREEN_BLOOM(0),

        SCREEN_WATER(1),

        SCREEN_DOF(0),

        SCREEN_MIN_AMOUNT_MODELS(800),

        SCREEN_ACTUAL_AMOUNT_MODELS(800),

        SCREEN_SSAO(0),

        SESSION_TYPE(Network.SessionType.SINGLE),

        TLANGUAGE(TLanguage.EN),

        SOUND_PERCENTAGE(1f),

        FIRST_TIME(true),

        FIRST_EDITOR_2026(true),

        SCREEN_TEXTURE_SIZE(TextureSize.MEDIUM),

        SCREEN_CARTOON(0),

        SCREEN_HDR(0),

        SCREEN_SCATTERING(1),

        IGNORE_UNKNOWN_HARDWARE(false),

        /**
         * OVerride AppSettings renderer
         */
        RENDERER("LWJGL-OpenGL2"),

        VIDEO_DIRECTORY(OSUtils.getHomeDir("Videos")),

        PICTURE_DIRECTORY(OSUtils.getHomeDir("Pictures")),

        LAST_EDITOR_TLANGUAGE(TLanguage.NL),

        LAST_EDITOR_UNIT_SYSTEM_TYPE(UnitSystemType.SI),

        LAST_EDITOR_TCURRENCY(TCurrency.EURO),

        INSTALLER_VERSION_SKIPPED(-1),

        RELEASE_VERSION(StringUtils.EMPTY),

        LAST_GRAPHICS_QUALITY("LOW"),

        ;

        private Object defaultValue;

        private SettingsType(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return this.defaultValue;
        }
    }

    protected static final class SingletonHolder {

        private static SettingsManager INSTANCE;

        public static SettingsManager getInstance() {
            if (INSTANCE == null) {
                TLogger.showstopper("SettingsManager has not been setup yet. Use SettingsManager.setup() before calling any other method.");
            }
            return INSTANCE;
        }
    }

    /**
     * Size of a texture (Width x Height) in three versions.
     */
    public enum TextureSize {

        /**
         * Texture at 25% of the original Width and Height.
         */
        SMALL(64),
        /**
         * Texture at 50% of the original Width and Height.
         */
        MEDIUM(128),
        /**
         * Texture with same size (Width and Height) as the original.
         */
        LARGE(256);

        public static boolean largeMap = false;

        public static final String CUSTOM_EXTENSION = "jpg";

        private int pixelSize;

        private TextureSize(int pixelSize) {
            this.pixelSize = pixelSize;
        }

        public int getCustomTextureSize() {
            return pixelSize * 4;
        }

        public String getFileName(String orignalFileName) {

            // XXX: Temp hack for large maps
            TextureSize ts = this;
            if (largeMap) {
                if (ts == TextureSize.LARGE) {
                    ts = TextureSize.MEDIUM;
                } else if (ts == TextureSize.MEDIUM) {
                    ts = TextureSize.SMALL;
                }
            }
            return orignalFileName.replace(".", "_" + ts.name().toLowerCase() + ".");
        }

        public int getPixelSize() {
            return pixelSize;
        }
    }

    public static final String ROOT_LOCATION = "TyTech";

    private static final String TIMESTAMP_DIR = OSUtils.STORAGE_DIRECTORY + "Timestamps" + File.separator
            + StringUtils.internalTrim(Engine.PLATFORM_NAME_TYPED, true) + File.separator;

    public static int getAmountOfConnections() {
        return SingletonHolder.getInstance()._getAmountOfConnections();
    }

    public static String getApiToken(int connectionID) {
        return SingletonHolder.getInstance()._getApiToken(connectionID);
    }

    public static AppType getAppType() {
        return SingletonHolder.getInstance()._getAppType();
    }

    public static String getCameraStyle() {
        return SingletonHolder.getInstance()._getCameraStyle();
    }

    public static String getClientToken(int connectionID) {
        return SingletonHolder.getInstance()._getClientToken(connectionID);
    }

    public static String getConditionsChecksum() {
        return SingletonHolder.getInstance()._getConditionsChecksum();
    }

    public static Conditions getConditionsRegion() {
        return SingletonHolder.getInstance()._getConditionsRegion();
    }

    public static int getInstallerSkippedVersion() {
        return SingletonHolder.getInstance()._getInstallerSkippedVersion();
    }

    public static String getInstanceID() {
        return SingletonHolder.getInstance()._getInstanceID();
    }

    public static TLanguage getLanguage() {
        return SingletonHolder.getInstance()._getLanguage();
    }

    public static TCurrency getLastEditorTCurrency() {
        return SingletonHolder.getInstance()._getLastEditorTCurrency();
    }

    public static TLanguage getLastEditorTLanguage() {
        return SingletonHolder.getInstance()._getLastEditorTLanguage();
    }

    public static UnitSystemType getLastEditorUnitSystemType() {
        return SingletonHolder.getInstance()._getLastEditorUnitSystemType();
    }

    public static String getLastGraphicsQuality() {
        return SingletonHolder.getInstance()._getLastGraphicsQuality();
    }

    public static String getPictureDirectory() {
        return SingletonHolder.getInstance()._getPictureDirectory();
    }

    public static String getProjectName() {
        return SingletonHolder.getInstance()._getProjectName();
    }

    protected static <T> T getPropertyDirect(String key, Class<T> classz) {
        return SingletonHolder.getInstance().getProperty(key, classz);
    }

    public static String getReleaseVersion() {
        return SingletonHolder.getInstance()._getReleaseVersion();
    }

    public static String getRenderer() {
        return SingletonHolder.getInstance()._getRenderer();
    }

    public static double getRequestedScale() {
        return SingletonHolder.getInstance()._getRequestedScale();
    }

    public static RunMode getRunMode() {
        return SingletonHolder.getInstance()._getRunMode();
    }

    public static int getScreenActualAmountModels() {
        return SingletonHolder.getInstance()._getScreenActualAmountModels();
    }

    public static int getScreenAntiAliasing() {
        return SingletonHolder.getInstance()._getScreenAntiAliasing();
    }

    public static int getScreenBloom() {
        return SingletonHolder.getInstance()._getScreenBloom();
    }

    public static int getScreenCartoon() {
        return SingletonHolder.getInstance()._getScreenCartoon();
    }

    public static int getScreenDoF() {
        return SingletonHolder.getInstance()._getScreenDoF();
    }

    public static int getScreenHDR() {
        return SingletonHolder.getInstance()._getScreenHDR();
    }

    public static int getScreenHeight() {
        return SingletonHolder.getInstance()._getScreenHeight();
    }

    public static int getScreenMinAmountModels() {
        return SingletonHolder.getInstance()._getScreenMinAmountModels();
    }

    public static int getScreenPositionX() {
        return SingletonHolder.getInstance()._getScreenPositionX();
    }

    public static int getScreenPositionY() {
        return SingletonHolder.getInstance()._getScreenPositionY();
    }

    public static int getScreenScattering() {
        return SingletonHolder.getInstance()._getScreenScattering();
    }

    public static int getScreenShadows() {
        return SingletonHolder.getInstance()._getScreenShadows();
    }

    public static int getScreenSSAO() {
        return SingletonHolder.getInstance()._getScreenSSAO();
    }

    public static TextureSize getScreenTextureSize() {
        return SingletonHolder.getInstance()._getScreenTextureSize();
    }

    public static int getScreenWater() {
        return SingletonHolder.getInstance()._getScreenWater();
    }

    public static int getScreenWidth() {
        return SingletonHolder.getInstance()._getScreenWidth();
    }

    public static final String getServerAddress() {
        String[] adresses = SingletonHolder.getInstance()._getServerAddresses();
        return adresses != null && adresses.length > 0 ? adresses[0] : null;
    }

    public static String[] getServerAddresses() {
        return SingletonHolder.getInstance()._getServerAddresses();
    }

    public static final ServerConfig getServerConfig() {
        return ServerConfig.getForAddress(getServerAddress());
    }

    public static Integer getServerSessionID(int connectionID) {
        return SingletonHolder.getInstance()._getServerSessionID(connectionID);
    }

    public static ServerType getServerType() {
        return SingletonHolder.getInstance()._getServerType();
    }

    public static String getServerWebAddress() {
        return WebUtils.HTTPS + getServerAddress() + "/";
    }

    public static String getShareAddress() {
        return getServerConfig().getShareAddress();
    }

    public static String getShareWebAddress() {
        return WebUtils.HTTPS + getShareAddress() + "/";
    }

    public static float getSoundVolumePercentage() {
        return SingletonHolder.getInstance()._getSoundVolumePercentage();
    }

    public static Map<String, Object> getStorage() {
        return SingletonHolder.getInstance()._getStorage();
    }

    public static String getStoredKey() {
        return SingletonHolder.getInstance()._getStoredKey();
    }

    public static String getStoredUserName() {
        return SingletonHolder.getInstance()._getStoredUsername();
    }

    public static TokenPair[] getTokenPairs() {
        return SingletonHolder.getInstance()._getTokenPairs();
    }

    public static String getVideoDirectory() {
        return SingletonHolder.getInstance()._getVideoDirectory();
    }

    private static final boolean isEclipse() {
        try {
            // check for eclipse metadata directory
            File meta = new File("../.metadata/");
            return meta.exists() && meta.isDirectory();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return true;
    }

    public static boolean isFirstEditor2026() {
        return SingletonHolder.getInstance()._isFirstEditor2026();
    }

    public static boolean isFirstTime() {
        return SingletonHolder.getInstance()._isFirstTime();
    }

    public static boolean isIgnoreUnknownHardware() {
        return SingletonHolder.getInstance()._isIgnoreUnknownHardware();
    }

    public static boolean isLogToFile() {
        return SingletonHolder.getInstance()._isLogToFile();
    }

    public static boolean isStoredKeyExpired() {
        return SingletonHolder.getInstance()._isStoredKeyExpired();
    }

    private static final boolean isTestWorkspace() {
        try {
            // check for SVN and devscripts project directory
            File svn = new File("../.svn/");
            File devscripts = new File("../devscripts/");
            return svn.exists() && svn.isDirectory() && devscripts.exists() && devscripts.isDirectory();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return true;
    }

    /**
     * When true app supports a Virtual reality mode
     */
    public static boolean isVirtualRealityMode() {
        return SingletonHolder.getInstance()._isVirtualRealityMode();
    }

    public static void setAmountOfConnections(int noSessions) {
        SingletonHolder.getInstance()._setAmountOfConnections(noSessions);
    }

    public static void setApiToken(int connectionID, String token) {
        SingletonHolder.getInstance()._setApiToken(connectionID, token);
    }

    public static void setCameraStyle(String style) {
        SingletonHolder.getInstance()._setCameraStyle(style);
    }

    public static void setClientToken(int connectionID, String clientToken) {
        SingletonHolder.getInstance()._setClientToken(connectionID, clientToken);
    }

    public static void setConditionsChecksum(Conditions conditions, String eulaChecksum) {
        SingletonHolder.getInstance()._setConditionsChecksum(conditions, eulaChecksum);
    }

    public static void setFirstEditor2026(boolean firstTime) {
        SingletonHolder.getInstance()._setFirstEditor2026(firstTime);
    }

    public static void setFirstTime(boolean firstTime) {
        SingletonHolder.getInstance()._setFirstTime(firstTime);
    }

    public static void setIgnoreUnknownHardware(boolean ignore) {
        SingletonHolder.getInstance()._setIgnoreUnknownHardware(ignore);
    }

    public static void setInstallerSkippedVersion(int version) {
        SingletonHolder.getInstance()._setInstallerSkippedVersion(version);
    }

    public static void setLanguage(TLanguage language) {
        SingletonHolder.getInstance()._setLanguage(language);
    }

    public static void setLastEditorTCurrency(TCurrency currency) {
        SingletonHolder.getInstance()._setLastEditorTCurrency(currency);
    }

    public static void setLastEditorTLanguage(TLanguage language) {
        SingletonHolder.getInstance()._setLastEditorTLanguage(language);
    }

    public static void setLastEditorUnitSystemType(UnitSystemType unitSystemType) {
        SingletonHolder.getInstance()._setLastEditorUnitSystemType(unitSystemType);
    }

    public static void setLastGraphicsQuality(String quality) {
        SingletonHolder.getInstance()._setLastGraphicsQuality(quality);
    }

    public static void setPictureDirectory(String path) {
        SingletonHolder.getInstance()._setPictureDirectory(path);
    }

    public static void setProjectName(String projectName) {
        SingletonHolder.getInstance()._setProjectName(projectName);
    }

    protected static void setPropertyDirect(String key, Object value) {
        SingletonHolder.getInstance().setProperty(key, value);
    }

    public static void setReleaseVersion(String version) {
        SingletonHolder.getInstance()._setReleaseVersion(version);
    }

    public static void setRenderer(String renderer) {
        SingletonHolder.getInstance()._setRenderer(renderer);
    }

    public static void setRequestedScale(double scale) {
        SingletonHolder.getInstance()._setRequestedScale(scale);
    }

    public static void setRunMode(RunMode level) {
        SingletonHolder.getInstance()._setRunMode(level);
    }

    public static void setScreenActualAmountModels(int drawDistance) {
        SingletonHolder.getInstance()._setScreenActualAmountModels(drawDistance);
    }

    public static void setScreenAntiAliasing(int antiAliasing) {
        SingletonHolder.getInstance()._setScreenAntiAliasing(antiAliasing);
    }

    public static void setScreenBloom(int bloom) {
        SingletonHolder.getInstance()._setScreenBloom(bloom);
    }

    public static void setScreenCartoon(int hdr) {
        SingletonHolder.getInstance()._setScreenCartoon(hdr);
    }

    public static void setScreenDoF(int dof) {
        SingletonHolder.getInstance()._setScreenDoF(dof);
    }

    public static void setScreenHDR(int hdr) {
        SingletonHolder.getInstance()._setScreenHDR(hdr);
    }

    public static void setScreenMinAmountModels(int drawDistance) {
        SingletonHolder.getInstance()._setScreenMinAmountModels(drawDistance);
    }

    public static void setScreenPosition(int posx, int posy) {
        SingletonHolder.getInstance()._setScreenPosition(posx, posy);
    }

    public static void setScreenScattering(int value) {
        SingletonHolder.getInstance()._setScreenScattering(value);
    }

    public static void setScreenShadows(int shadows) {
        SingletonHolder.getInstance()._setScreenShadows(shadows);
    }

    public static void setScreenSize(int width, int height) {
        SingletonHolder.getInstance()._setScreenSize(width, height);
    }

    public static void setScreenSSAO(int bloom) {
        SingletonHolder.getInstance()._setScreenSSAO(bloom);
    }

    public static void setScreenTextureSize(TextureSize textureQuality) {
        SingletonHolder.getInstance()._setScreenTextureSize(textureQuality);
    }

    public static void setScreenWater(int waterQuality) {
        SingletonHolder.getInstance()._setScreenWater(waterQuality);
    }

    public static void setServer(ServerType type, String... address) {
        SingletonHolder.getInstance()._setServer(type, address);
    }

    public static void setServerSessionID(int connectionID, Integer serverSessionID) {
        SingletonHolder.getInstance()._setServerSessionID(connectionID, serverSessionID);
    }

    public static void setSoundVolumePercentage(Float percentage) {
        SingletonHolder.getInstance()._setSoundVolumePercentage(percentage);
    }

    public static void setStoredKey(String key, long expireDate) {
        SingletonHolder.getInstance()._setStoredKey(key, expireDate);
    }

    public static void setStoredUserName(String username) {
        SingletonHolder.getInstance()._setStoredUserName(username);
    }

    public static synchronized void setup(Class<? extends SettingsManager> classz, AppType appType) {

        if (SingletonHolder.INSTANCE != null && SingletonHolder.INSTANCE.getClass() == classz
                && SingletonHolder.INSTANCE._getAppType() == appType) {
            TLogger.severe(SettingsManager.class.getSimpleName() + " already setup.");
            return;
        }

        try {
            // get empty constructor
            Constructor<? extends SettingsManager> constructor = classz.getDeclaredConstructor();
            constructor.setAccessible(true);
            SettingsManager newInstance = constructor.newInstance();
            constructor.setAccessible(false);
            newInstance.appType = appType;

            if (SingletonHolder.INSTANCE == null) {
                // first time init.
                newInstance.firstTimeInit();
            } else {
                // second time recyle storage and storageID
                newInstance.storage.putAll(SingletonHolder.INSTANCE.storage);
                newInstance.storageID = SingletonHolder.INSTANCE.storageID;
                newInstance.instanceID = SingletonHolder.INSTANCE.instanceID;
                newInstance.serverAddresses = SingletonHolder.INSTANCE.serverAddresses;
                newInstance.serverType = SingletonHolder.INSTANCE.serverType;
                newInstance.virtualRealityMode = SingletonHolder.INSTANCE.virtualRealityMode;
                newInstance.screenWidth = SingletonHolder.INSTANCE.screenWidth;
                newInstance.screenHeight = SingletonHolder.INSTANCE.screenHeight;
            }
            SingletonHolder.INSTANCE = newInstance;

        } catch (Exception e) {
            TLogger.exception(e, "Cannot load SettingsManager.");
        }

        if (isEclipse()) {
            setRunMode(RunMode.DEV); // in Eclipse always run in DEV mode

        } else if (isTestWorkspace()) {
            setRunMode(RunMode.TEST); // in test workspace, run as TEST mode

        } else {
            // check run Mode setting for this Run only
            RunMode prefMode = SingletonHolder.getInstance().getProperty(SettingsType.TEMP_RUNMODE, RunMode.class);
            if (prefMode != RunMode.RELEASE) {
                TLogger.warning("The App is started with Temporay Runmode: " + prefMode + ".");
                setRunMode(prefMode);

                // set back to RELEASE for next start
                SingletonHolder.getInstance().setProperty(SettingsType.TEMP_RUNMODE, RunMode.RELEASE);
            }
        }
        TLogger.info("Starting: " + appType + " in Mode: " + getRunMode() + ".");
        TLogger.setLogToFile(isLogToFile());
    }

    public static void setVideoDirectory(String path) {
        SingletonHolder.getInstance()._setVideoDirectory(path);
    }

    public static void setVirtualRealityMode(boolean vr) {
        SingletonHolder.getInstance()._setVirtualRealityMode(vr);
    }

    public static void stopSlotKeeper() {
        SingletonHolder.getInstance()._stopSlotKeeper();
    }

    protected String storageID;

    protected String instanceID;

    protected Integer screenWidth = null;

    protected Integer screenHeight = null;

    protected final Map<String, Object> storage;

    private AppType appType;

    private String projectName = null;

    private volatile String[] serverAddresses = new String[] { "localhost" };

    private volatile ServerType serverType = null;

    private RunMode runMode = RunMode.RELEASE;

    private SettingsActive settingsActive;

    private volatile boolean virtualRealityMode = false;

    protected SettingsManager() {
        this(new ConcurrentHashMap<>());
    }

    protected SettingsManager(Map<String, Object> storage) {
        this.storage = storage;
    }

    private int _getAmountOfConnections() {

        /**
         * Viewer and editor are always 1 connection
         */
        AppType appType = _getAppType();
        if (appType == AppType.PARTICIPANT || appType == AppType.EDITOR) {
            return 1;
        }
        return getProperty(SettingsType.SERVER_AMOUNT_OF_CONNECTIONS, Integer.class);
    }

    /**
     * _get the server token
     *
     * @return token
     */
    private String _getApiToken(int connectionID) {
        return getProperty(SettingsType.API_TOKEN, connectionID, String.class);
    }

    private AppType _getAppType() {
        return this.appType;
    }

    private String _getCameraStyle() {
        return getProperty(SettingsType.CAMERA_STYLE, String.class);
    }

    /**
     * _get the client token
     *
     * @return token
     */
    private String _getClientToken(int connectionID) {
        return getProperty(SettingsType.CLIENT_TOKEN, connectionID, String.class);
    }

    private String _getConditionsChecksum() {
        return getProperty(SettingsType.CONDITIONS_CHECKSUM, String.class);
    }

    private Conditions _getConditionsRegion() {
        String data = getProperty(SettingsType.CONDITIONS_REGION, String.class);
        try {
            return Conditions.valueOf(data);
        } catch (Exception e) {
            return null;
        }
    }

    private int _getInstallerSkippedVersion() {
        return getProperty(SettingsType.INSTALLER_VERSION_SKIPPED, Integer.class);
    }

    private String _getInstanceID() {
        return instanceID;
    }

    private TLanguage _getLanguage() {
        return getProperty(SettingsType.TLANGUAGE, TLanguage.class);
    }

    private TCurrency _getLastEditorTCurrency() {
        return getProperty(SettingsType.LAST_EDITOR_TCURRENCY, TCurrency.class);
    }

    private TLanguage _getLastEditorTLanguage() {
        return getProperty(SettingsType.LAST_EDITOR_TLANGUAGE, TLanguage.class);
    }

    private UnitSystemType _getLastEditorUnitSystemType() {
        return getProperty(SettingsType.LAST_EDITOR_UNIT_SYSTEM_TYPE, UnitSystemType.class);
    }

    private String _getLastGraphicsQuality() {
        return getProperty(SettingsType.LAST_GRAPHICS_QUALITY, String.class);
    }

    private String _getPictureDirectory() {
        return getProperty(SettingsType.PICTURE_DIRECTORY, String.class);
    }

    private String _getProjectName() {
        return projectName;
    }

    private String _getReleaseVersion() {
        return getProperty(SettingsType.RELEASE_VERSION, String.class);
    }

    private String _getRenderer() {
        return getProperty(SettingsType.RENDERER, String.class);
    }

    private double _getRequestedScale() {
        return getProperty(SettingsType.REQUESTED_SCALE, Double.class);
    }

    private RunMode _getRunMode() {
        return runMode;
    }

    private int _getScreenActualAmountModels() {
        return getProperty(SettingsType.SCREEN_ACTUAL_AMOUNT_MODELS, Integer.class);
    }

    private int _getScreenAntiAliasing() {
        return getProperty(SettingsType.SCREEN_ANTI_ALIASING, Integer.class);
    }

    private int _getScreenBloom() {
        return getProperty(SettingsType.SCREEN_BLOOM, Integer.class);
    }

    private int _getScreenCartoon() {
        return getProperty(SettingsType.SCREEN_CARTOON, Integer.class);
    }

    private int _getScreenDoF() {
        return getProperty(SettingsType.SCREEN_DOF, Integer.class);
    }

    private int _getScreenHDR() {
        return getProperty(SettingsType.SCREEN_HDR, Integer.class);
    }

    private int _getScreenHeight() {
        return screenHeight == null ? appType.getDefaultHeight() : screenHeight.intValue();
    }

    private int _getScreenMinAmountModels() {
        return getProperty(SettingsType.SCREEN_MIN_AMOUNT_MODELS, Integer.class);
    }

    private int _getScreenPositionX() {
        return getProperty(SettingsType.SCREEN_POSX, Integer.class);
    }

    private int _getScreenPositionY() {
        return getProperty(SettingsType.SCREEN_POSY, Integer.class);
    }

    private int _getScreenScattering() {
        return getProperty(SettingsType.SCREEN_SCATTERING, Integer.class);
    }

    private int _getScreenShadows() {
        return getProperty(SettingsType.SCREEN_SHADOWS, Integer.class);
    }

    private int _getScreenSSAO() {
        return getProperty(SettingsType.SCREEN_SSAO, Integer.class);
    }

    private TextureSize _getScreenTextureSize() {
        return getProperty(SettingsType.SCREEN_TEXTURE_SIZE, TextureSize.class);
    }

    private int _getScreenWater() {
        return getProperty(SettingsType.SCREEN_WATER, Integer.class);
    }

    private int _getScreenWidth() {
        return screenWidth == null ? appType.getDefaultWidth() : screenWidth.intValue();
    }

    private String[] _getServerAddresses() {
        return serverAddresses;
    }

    private Integer _getServerSessionID(int connectionID) {
        return getProperty(SettingsType.SERVER_SESSION_ID, connectionID, Integer.class);
    }

    private ServerType _getServerType() {
        return serverType;
    }

    private float _getSoundVolumePercentage() {
        return getProperty(SettingsType.SOUND_PERCENTAGE, Float.class);
    }

    protected Map<String, Object> _getStorage() {
        return storage;
    }

    /**
     * Key is stored in base64,
     */
    private String _getStoredKey() {

        String value = getProperty(SettingsType.STORED_KEY, String.class);
        if (value == null) {
            return StringUtils.EMPTY;
        }

        try {
            return new String(Base64.decode(value));
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    private String _getStoredUsername() {
        return getProperty(SettingsType.STORED_USERNAME, String.class);
    }

    private TokenPair[] _getTokenPairs() {

        List<TokenPair> tokenPairs = new ArrayList<>();
        int connections = this._getAmountOfConnections() > 0 ? this._getAmountOfConnections() : 1;
        Preferences prefs = Preferences.userRoot().node(storageID);

        try {
            String[] keys = prefs.keys();

            for (int i = 0; i < connections; i++) {

                String clientTokenKey = StringUtils.capitalizeUnderScores(SettingsType.CLIENT_TOKEN.name()) + "-" + i;
                String apiTokenKey = StringUtils.capitalizeUnderScores(SettingsType.API_TOKEN.name()) + "-" + i;

                String clientToken = null;
                String apiToken = null;

                for (String key : keys) {
                    if (key.contains(clientTokenKey)) {
                        clientToken = prefs.get(key, null);
                    }
                    if (key.contains(apiTokenKey)) {
                        apiToken = prefs.get(key, null);
                    }
                }
                if (clientToken != null && apiToken != null) {
                    tokenPairs.add(new TokenPair(apiToken, clientToken));
                }
            }
        } catch (Exception e) {
        }
        return tokenPairs.toArray(new TokenPair[tokenPairs.size()]);
    }

    private String _getVideoDirectory() {
        return getProperty(SettingsType.VIDEO_DIRECTORY, String.class);
    }

    private boolean _isFirstEditor2026() {
        return getProperty(SettingsType.FIRST_EDITOR_2026, Boolean.class);
    }

    private boolean _isFirstTime() {
        return getProperty(SettingsType.FIRST_TIME, Boolean.class);
    }

    private boolean _isIgnoreUnknownHardware() {
        return getProperty(SettingsType.IGNORE_UNKNOWN_HARDWARE, Boolean.class);
    }

    private boolean _isLogToFile() {
        return getProperty(SettingsType.LOGTOFILE, Boolean.class);
    }

    private boolean _isStoredKeyExpired() {
        return getProperty(SettingsType.STORED_KEY_EXPIRE_DATE, Long.class).longValue() < System.currentTimeMillis();
    }

    private boolean _isVirtualRealityMode() {
        return virtualRealityMode;
    }

    private void _setAmountOfConnections(int noSessions) {
        setProperty(SettingsType.SERVER_AMOUNT_OF_CONNECTIONS, noSessions);
    }

    private void _setApiToken(int connectionID, String token) {
        setProperty(SettingsType.API_TOKEN, connectionID, token);
    }

    private void _setCameraStyle(String style) {
        setProperty(SettingsType.CAMERA_STYLE, style);
    }

    private void _setClientToken(int connectionID, String clientToken) {
        setProperty(SettingsType.CLIENT_TOKEN, connectionID, clientToken);
    }

    private void _setConditionsChecksum(Conditions conditions, String eulaChecksum) {
        setProperty(SettingsType.CONDITIONS_CHECKSUM, eulaChecksum);
        setProperty(SettingsType.CONDITIONS_REGION, conditions.name());
    }

    private void _setFirstEditor2026(boolean firstTime) {
        setProperty(SettingsType.FIRST_EDITOR_2026, firstTime);
    }

    private void _setFirstTime(boolean firstTime) {
        setProperty(SettingsType.FIRST_TIME, firstTime);
    }

    private void _setIgnoreUnknownHardware(boolean ignore) {
        setProperty(SettingsType.IGNORE_UNKNOWN_HARDWARE, ignore);
    }

    private void _setInstallerSkippedVersion(int version) {
        setProperty(SettingsType.INSTALLER_VERSION_SKIPPED, version);
    }

    private void _setLanguage(TLanguage language) {
        setProperty(SettingsType.TLANGUAGE, language);
    }

    private void _setLastEditorTCurrency(TCurrency currency) {
        setProperty(SettingsType.LAST_EDITOR_TCURRENCY, currency);
    }

    private void _setLastEditorTLanguage(TLanguage language) {
        setProperty(SettingsType.LAST_EDITOR_TLANGUAGE, language);
    }

    private void _setLastEditorUnitSystemType(UnitSystemType unitSystemType) {
        setProperty(SettingsType.LAST_EDITOR_UNIT_SYSTEM_TYPE, unitSystemType);
    }

    private void _setLastGraphicsQuality(String quality) {
        setProperty(SettingsType.LAST_GRAPHICS_QUALITY, quality);
    }

    private void _setPictureDirectory(String path) {
        setProperty(SettingsType.PICTURE_DIRECTORY, path);
    }

    protected void _setProjectName(String projectName) {
        this.projectName = projectName;
        EventManager.fire(SettingsEventType.PROJECT_NAME_UPDATED, projectName);
    }

    private void _setReleaseVersion(String version) {
        setProperty(SettingsType.RELEASE_VERSION, version);
    }

    private void _setRenderer(String renderer) {
        setProperty(SettingsType.RENDERER, renderer);
    }

    private void _setRequestedScale(double scale) {
        setProperty(SettingsType.REQUESTED_SCALE, scale);
    }

    protected void _setRunMode(RunMode level) {
        this.runMode = level;
    }

    private void _setScreenActualAmountModels(int drawDistance) {
        setProperty(SettingsType.SCREEN_ACTUAL_AMOUNT_MODELS, drawDistance);
    }

    protected void _setScreenAntiAliasing(int antiAliasing) {

        setProperty(SettingsType.SCREEN_ANTI_ALIASING, antiAliasing);

    }

    protected void _setScreenBloom(int bloom) {
        setProperty(SettingsType.SCREEN_BLOOM, bloom);
    }

    protected void _setScreenCartoon(int cartoon) {
        setProperty(SettingsType.SCREEN_CARTOON, cartoon);
    }

    protected void _setScreenDoF(int bloom) {
        setProperty(SettingsType.SCREEN_DOF, bloom);
    }

    private void _setScreenHDR(int hdr) {
        setProperty(SettingsType.SCREEN_HDR, hdr);
    }

    private void _setScreenMinAmountModels(int drawDistance) {
        setProperty(SettingsType.SCREEN_MIN_AMOUNT_MODELS, drawDistance);
    }

    private void _setScreenPosition(int posx, int posy) {
        setProperty(SettingsType.SCREEN_POSX, posx);
        setProperty(SettingsType.SCREEN_POSY, posy);
    }

    private void _setScreenScattering(int scatter) {
        setProperty(SettingsType.SCREEN_SCATTERING, scatter);
    }

    private void _setScreenShadows(int shadows) {
        setProperty(SettingsType.SCREEN_SHADOWS, shadows);
    }

    private void _setScreenSize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    private void _setScreenSSAO(int bloom) {
        setProperty(SettingsType.SCREEN_SSAO, bloom);
    }

    private void _setScreenTextureSize(TextureSize textureQuality) {
        setProperty(SettingsType.SCREEN_TEXTURE_SIZE, textureQuality);
    }

    private void _setScreenWater(int waterQuality) {
        setProperty(SettingsType.SCREEN_WATER, waterQuality);
    }

    private void _setServer(ServerType type, String... address) {

        this.serverType = type;
        this.serverAddresses = address;
    }

    private void _setServerSessionID(int connectionID, Integer serverSessionID) {
        setProperty(SettingsType.SERVER_SESSION_ID, connectionID, serverSessionID);
    }

    private void _setSoundVolumePercentage(Float percentage) {
        setProperty(SettingsType.SOUND_PERCENTAGE, percentage);
    }

    /**
     * key is stored in base64
     */
    private void _setStoredKey(String key, long expireDate) {
        try {
            setProperty(SettingsType.STORED_KEY, Base64.encode(key));
            setProperty(SettingsType.STORED_KEY_EXPIRE_DATE, expireDate);
        } catch (Exception e) {
        }
    }

    private void _setStoredUserName(String username) {
        setProperty(SettingsType.STORED_USERNAME, username);
    }

    private void _setVideoDirectory(String path) {
        setProperty(SettingsType.VIDEO_DIRECTORY, path);
    }

    private void _setVirtualRealityMode(boolean vr) {
        this.virtualRealityMode = vr;
    }

    private void _stopSlotKeeper() {
        if (settingsActive != null) {
            settingsActive.active = false;
        }
    }

    private boolean containsProperty(SettingsType type, int id) {
        String key = convertToAppSettingsKey(type, id);
        return storage.containsKey(key);
    }

    private String convertToAppSettingsKey(SettingsType type, int id) {

        String key = type.name().toLowerCase();
        StringTokenizer tokenizer = new StringTokenizer(key, "_");
        key = StringUtils.EMPTY;
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            token = token.substring(0, 1).toUpperCase() + token.substring(1);
            key += token;
        }
        if (id >= 0) {
            key += "-" + id;
        }
        return key;
    }

    private void firstTimeInit() {

        String id;
        if (this.appType == AppType.SERVER) {
            id = AppType.SERVER.name().toLowerCase(); // single instance always
        } else {
            int i = 0;
            while (isSettingsSlotActive(i)) {
                i++;
            }
            id = Integer.toString(i);
        }
        this.storageID = ROOT_LOCATION + "/" + StringUtils.internalTrim(Engine.PLATFORM_NAME_TYPED, true) + "/" + id;
        this.instanceID = id;
        this.load(storageID);

        // save every sec
        if (this.appType != AppType.SERVER) {
            settingsActive = new SettingsActive(instanceID);
            settingsActive.start();
        }
        TLogger.info("SettingsManager Instance ID: " + id);
    }

    private <T> T getProperty(SettingsType type, Class<T> classz) {
        return getProperty(type, -1, classz);

    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(SettingsType type, int id, Class<T> classz) {

        // _get properties
        String key = convertToAppSettingsKey(type, id);
        Object result = getProperty(key, classz);
        if (result == null) {
            TLogger.info("No Setting for " + key + " (" + key + "), returning default value.");
            // save first
            this.setProperty(type, id, type.getDefaultValue());
            // return default
            result = type.getDefaultValue();
        }
        return (T) result;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private synchronized <T> T getProperty(String key, Class<T> classz) {
        Object result = storage.get(key);

        if (result == null) {
            return null;
        }

        if (classz == null) {
            TLogger.warning("No Setting class defined for " + key + ".");
            return (T) result;
        }

        if (classz.equals(Integer.class)) {
            try {
                return (T) Integer.valueOf(StringUtils.EMPTY + result);
            } catch (Exception exp) {
                TLogger.severe("Value: " + result + " for _setting type " + key + " is not a " + classz.getSimpleName() + ".");
                return (T) Integer.valueOf(Item.NONE);
            }
        }

        else if (classz.isEnum()) {
            try {
                return (T) Enum.valueOf((Class<Enum>) classz, result.toString());
            } catch (Exception e) {
                TLogger.warning("Could not convert settings [" + key + "] into an Enum of type [" + classz.getSimpleName() + "]");
                return null;
            }
        }

        else if (!result.getClass().equals(classz)) {
            // Attempt to 'value of'
            try {
                Method method = classz.getMethod("valueOf", String.class);
                method.setAccessible(true);
                result = method.invoke(null, result.toString());
            } catch (Exception e) {
                TLogger.severe("Could not convert settings [" + key + "] into an Object of type [" + classz.getSimpleName() + "]");
            }

        }
        return (T) result;
    }

    private final boolean isSettingsSlotActive(int id) {

        try {
            File file = new File(TIMESTAMP_DIR + id);
            return file.exists() && System.currentTimeMillis() - file.lastModified() < SettingsActive.PERIOD * 2;
        } catch (Exception e) {
        }
        return false;
    }

    private void load(String storageID) {
        try {
            this.storage.putAll(this.loadStorage(storageID));
            // Check if everything has been set
            if (!containsProperty(SettingsType.WATERMARK, -1)) {
                // load defaults
                for (SettingsType settingsType : SettingsType.values()) {
                    setProperty(settingsType, settingsType.getDefaultValue());
                }
                // save them
                this.save(storageID);
            }
        } catch (BackingStoreException e) {
            TLogger.exception(e, "Error loading from Registry.");
        }
    }

    private Map<String, Object> loadStorage(String preferencesKey) throws BackingStoreException {

        Preferences prefs = Preferences.userRoot().node(preferencesKey);
        String[] keys = prefs.keys();
        Map<String, Object> storage = new ConcurrentHashMap<>();

        if (keys != null) {
            for (String key : keys) {
                switch (key.charAt(0)) {
                    case 'I' -> storage.put(key.substring(2), prefs.getInt(key, 0));
                    case 'F' -> storage.put(key.substring(2), prefs.getFloat(key, 0f));
                    case 'S' -> storage.put(key.substring(2), prefs.get(key, (String) null));
                    case 'B' -> storage.put(key.substring(2), prefs.getBoolean(key, false));
                    default -> throw new UnsupportedOperationException("Undefined setting type: " + key.charAt(0));
                }
            }
        }
        return storage;
    }

    private void save(String storageID) {
        try {
            this.saveStorage(storage, storageID);
        } catch (BackingStoreException e) {
            TLogger.exception(e, "Error saving to Registry.");
        }
    }

    private void saveStorage(Map<String, Object> storage, String preferencesKey) throws BackingStoreException {

        Preferences prefs = Preferences.userRoot().node(preferencesKey);
        prefs.clear();

        for (String key : storage.keySet()) {
            Object val = storage.get(key);
            if (val instanceof Integer i) {
                prefs.putInt("I_" + key, i);
            } else if (val instanceof Float f) {
                prefs.putFloat("F_" + key, f);
            } else if (val instanceof String s) {
                prefs.put("S_" + key, s);
            } else if (val instanceof Boolean b) {
                prefs.putBoolean("B_" + key, b);
            }
        }
        prefs.sync();
    }

    private boolean setProperty(SettingsType type, int id, Object value) {

        if (type == null) {
            TLogger.severe("Missing _setting type.");
            return false;
        }

        if (value == null) {
            TLogger.warning("Cannot set setting " + type + " to null");
            return false;
        }

        // First
        String key = convertToAppSettingsKey(type, id);
        return setProperty(key, value);
    }

    protected boolean setProperty(SettingsType type, Object value) {
        return setProperty(type, -1, value);
    }

    private synchronized boolean setProperty(String key, Object value) {

        // compare based on Strings; (e.g. Integer (new value) and String (from registry) can never be equal!)
        String oldValue = StringUtils.EMPTY + storage.get(key);

        // Enum should use name() instead of toString()
        String newValue;
        if (value instanceof Enum) {
            newValue = ((Enum<?>) value).name();
        } else {
            newValue = value == null ? StringUtils.EMPTY : value.toString();
        }

        // only update changes
        if (newValue.equals(oldValue)) {
            return false;
        }

        // types supported by JME
        if (value instanceof Integer || value instanceof Float || value instanceof Boolean || value instanceof String) {
            // save in properties
            storage.put(key, value);
        } else {
            // other types are converted to a String
            storage.put(key, newValue);
        }
        save(this.storageID);

        // fire event with type and value
        return true;
    }
}
