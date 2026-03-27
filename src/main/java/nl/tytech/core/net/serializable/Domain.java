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

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import nl.tytech.core.net.serializable.TLicense.Variable;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.locale.TLanguage;
import nl.tytech.naming.EngineNC;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ServerConfig;
import nl.tytech.util.ServerType;
import nl.tytech.util.StringUtils;

/**
 * Domain contains a collection of Users that share projects an rights.
 * @author Maxim Knepfle
 *
 */
public class Domain implements Serializable {

    public enum Filter {

        ALL,

        EXCEPT_TYGRON,

        EDUCATIONAL,

        COMMERCIAL,

        EXPIRED,

        ACTIVE,

        TRIAL,

        TRIAL_LICENCE_REQUESTED,
    }

    public enum TState {

        REQUESTED,

        EMAIL_VERIFIED,

        LICENSE_REQUESTED,

        LICENSED;

    }

    public enum Usage {

        NEW("New Projects per day", "Nieuwe projecten per dag"),

        PROJECT_COUNT("Project count", "Aantal projecten"),

        DOMAIN_AREA("Total Area (km2)", "Totaal aantal (km2)"),

        SUBDOMAIN_AREA("Total Area (km2)", "Totaal aantal (km2)"),

        SHARE_MB(EngineNC.SHARE + " Storage (MB)", EngineNC.SHARE + " opslag (MB)"),

        DOMAIN_ARCHIVE("Total Archive (km2)", "Totaal archief (km2)");

        public static final Integer[] NONE = new Integer[Usage.values().length];

        private final String en;
        private final String nl;

        private Usage(String en, String nl) {
            this.en = en;
            this.nl = nl;
        }

        public String getTitle(TLanguage lang) {

            return switch (lang) {
                case NL -> nl;
                default -> en;
            };
        }

        @Override
        public String toString() {
            return getTitle(TLanguage.DEFAULT);
        }
    }

    public static final long REMOVAL_PERIOD = 3l * Moment.MONTH_AVG;

    public static final long REMOVAL_SOON_PERIOD = REMOVAL_PERIOD - Moment.MONTH_AVG;

    public static final long DEFAULT_MAX_PROJECT_DIM_M = 60_000;

    public static final int MIN_NAME_LENGTH = 3;

    public static final int MAX_NAME_LENGTH = 50;

    public static final long MIN_KEY_EXPIRATION = 10l * Moment.MINUTE;

    public static final long MAX_KEY_EXPIRATION = Moment.MONTH_AVG;

    private static final long serialVersionUID = 945988661600523150L;

    public static final String PUBLIC = "public";

    public static final String ALL_SUB_DOMAINS = "All Sub Domains";

    public static final long MAX_ICON_BYTES = 100l * 1024l; // 100K

    public static final long EXPIRE_SOON_PERIOD = 6l * Moment.WEEK;

    public static final String VALID_NAME = "[a-z0-9@._-]{" + MIN_NAME_LENGTH + "," + MAX_NAME_LENGTH + "}";

    private static final Pattern REGEX_NAME = Pattern.compile(VALID_NAME);

    private static final Pattern REGEX_SUB_NAME = Pattern.compile("[a-zA-Z0-9@._-]{" + MIN_NAME_LENGTH + "," + MAX_NAME_LENGTH + "}");

    public static final String getName(String name) {
        return StringUtils.toLowerCaseUnderscore(name);
    }

    public static final boolean isRootDomain(String name) {
        return name != null && name.startsWith("tygron");
    }

    public static final boolean isValidDomainName(String name) {
        return name != null && REGEX_NAME.matcher(name).matches();
    }

    public static final boolean isValidSubDomainName(String name) {
        return name != null && REGEX_SUB_NAME.matcher(name).matches();
    }

    private TState state = TState.LICENSED;

    private TLicense license = TLicense.STARTER;

    protected HashMap<Variable, Long> licenseVariables = new HashMap<>();

    private String name = StringUtils.EMPTY;

    private String contactEmail = StringUtils.EMPTY;

    private String licenseNumber = StringUtils.EMPTY;

    private String contactPhone = StringUtils.EMPTY;

    private String contactFirstname = StringUtils.EMPTY;

    private String contactLastname = StringUtils.EMPTY;

    private String organisation = StringUtils.EMPTY;

    private String address = StringUtils.EMPTY;

    private String zipCode = StringUtils.EMPTY;

    private String city = StringUtils.EMPTY;

    private String country = StringUtils.EMPTY;

    private HashMap<ServerType, ServerConfig> routingMap = new HashMap<>();

    @Deprecated
    private HashMap<String, SubDomain> subDomainMap = new HashMap<>();

    private long expireDate = 1;

    private int activationCode = Item.NONE;

    private long creationDate = 0;

    private AccessLevel twoFactorLevel = AccessLevel.SUPER_USER;

    private int minPasswdLength = User.PASSWD_MIN_LENGHT;

    private long loginKeyExpiration = Moment.WEEK;

    /**
     * Unique ID to identify domains
     */
    private Long id = -1l;

    /**
     * Project saves per server
     */
    private HashMap<String, Integer> saves = new HashMap<>();

    /**
     * Project save area per server in KM2
     */
    private HashMap<String, Integer> saveAreas = new HashMap<>();

    /**
     * Project archive area per server in KM2
     */
    private HashMap<String, Integer> archiveAreas = new HashMap<>();

    /**
     * GeoShare stored per server in MB
     */
    private HashMap<String, Integer> shareStorages = new HashMap<>();

    /**
     * Give support access to share
     */
    private boolean shareSupport = false;

    public Domain() {

    }

    public int getActivationCode() {
        return activationCode;
    }

    public String getAddress() {
        return address;
    }

    public Map<String, Integer> getArchiveAreas() {
        return archiveAreas;
    }

    public String getCity() {
        return city;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public String getContactFirstname() {
        return contactFirstname;
    }

    public String getContactFullName() {
        return (this.getContactFirstname() + " " + this.getContactLastname()).trim();
    }

    public String getContactLastname() {
        return contactLastname;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getCountry() {
        return country;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public Long getId() {
        return id;
    }

    public long[] getLicenseAllowed(String subDomain) {

        final long[] allowed = new long[Usage.values().length];
        allowed[Usage.PROJECT_COUNT.ordinal()] = Long.MAX_VALUE; // unlimited
        allowed[Usage.NEW.ordinal()] = getLicenseVariable(Variable.MAX_NEW_PROJECTS, subDomain);
        allowed[Usage.DOMAIN_AREA.ordinal()] = getLicenseVariable(Variable.MAX_AREA_KM2, subDomain);
        allowed[Usage.SUBDOMAIN_AREA.ordinal()] = getLicenseVariable(Variable.MAX_AREA_KM2, subDomain);
        allowed[Usage.SHARE_MB.ordinal()] = getLicenseVariable(Variable.MAX_SHARE_MB, subDomain);
        allowed[Usage.DOMAIN_ARCHIVE.ordinal()] = getLicenseVariable(Variable.MAX_ARCHIVE_KM2, subDomain);
        return allowed;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public long getLicenseVariable(Variable variable) {
        return getLicenseVariable(variable, ALL_SUB_DOMAINS);
    }

    public long getLicenseVariable(Variable variable, String subDomain) {

        // public domain override
        if (variable == Variable.ALLOW_PREVIEW && isPublic()) {
            return 1;
        }

        // sub domain override
        if (!Domain.ALL_SUB_DOMAINS.equals(subDomain)) {
            if (variable == Variable.MAX_AREA_KM2) {
                Integer subLimit = subDomainMap.get(subDomain) != null ? subDomainMap.get(subDomain).getMaxArea() : null;
                if (subLimit != null) {
                    return Math.min(subLimit.longValue(), getLicenseVariable(variable));
                }
            } else if (variable == Variable.MAX_SHARE_MB) {
                return 0; // sub domain get no geo share space
            }
        }

        // domain specific override
        Long domainOverride = licenseVariables.get(variable);
        if (domainOverride != null) {
            return domainOverride.longValue();
        }

        // default license value
        return license.getVariable(variable);
    }

    public double getLicenseVariablePrice(Variable variable) {

        double price = license.getVariable(Variable.PRICE_EUR);
        if (license.getVariable(variable) == 0) {
            return 0;
        }
        return switch (variable) {
            case MAX_AREA_KM2 -> price / license.getVariable(variable);
            case MAX_ARCHIVE_KM2 -> 0.1 * getLicenseVariablePrice(Variable.MAX_AREA_KM2);
            case MAX_SHARE_MB -> 0.1 * price / license.getVariable(variable);
            default -> 0;
        };
    }

    public double getLicenseVariablePrice(Variable variable, long months) {

        // less then a year is 200% on the price
        double yearPrice = getLicenseVariablePrice(variable);
        return months >= 12 ? yearPrice / 12.0 * months : yearPrice / 6.0 * months;
    }

    public HashMap<Variable, Long> getLicenseVariables() {
        return licenseVariables;
    }

    public long getLicenseYearEnd() {
        return getLicenseYearStart() + Moment.YEAR_AVG;
    }

    public long getLicenseYearStart() {

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(expireDate);
        // walk back time to current license year
        while (start.getTimeInMillis() > System.currentTimeMillis()) {
            start.add(Calendar.YEAR, -1);
        }
        return start.getTimeInMillis();
    }

    public long getLoginKeyExpiration() {
        return loginKeyExpiration;
    }

    public int getMinPasswdLength() {
        return minPasswdLength;
    }

    public String getName() {
        return name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public ServerConfig getRouting(ServerType type) {
        ServerConfig routing = routingMap.get(type);
        return routing == null ? ServerConfig.getDefault(type) : routing;
    }

    public Map<ServerType, ServerConfig> getRoutingMap() {
        return routingMap;
    }

    public Map<String, Integer> getSaveAreas() {
        return saveAreas;
    }

    public Map<String, Integer> getSaves() {
        return saves;
    }

    public Map<String, Integer> getShareStorages() {
        return shareStorages;
    }

    public TState getState() {
        return state;
    }

    @Deprecated
    public HashMap<String, SubDomain> getSubDomainMap() {
        return subDomainMap;
    }

    public long getSubDomainMaxProjectAreaM2(String subDomain, Integer domainUsedAreaKM2, Integer subUsedAreaKM2) {

        long maxProjectAreaM2 = getLicenseVariable(Variable.MAX_PROJECT_DIM_M) * getLicenseVariable(Variable.MAX_PROJECT_DIM_M); // project
        long maxAreaM2 = Math.max(0, getLicenseVariable(Variable.MAX_AREA_KM2) * MathUtils.KM2 // max stored km2
                - (domainUsedAreaKM2 != null ? domainUsedAreaKM2.longValue() * MathUtils.KM2 : 0)); // used
        long domainAreaM2 = Math.min(maxAreaM2, maxProjectAreaM2);

        if (!Domain.ALL_SUB_DOMAINS.equals(subDomain)) {
            Long subAreaM2 = subDomainMap.get(subDomain) != null ? subDomainMap.get(subDomain).getMaxProjectArea(subUsedAreaKM2) : null;
            if (subAreaM2 != null) {
                return Math.min(domainAreaM2, subAreaM2);
            }
        }
        return domainAreaM2;
    }

    public TLicense getTLicense() {
        return license;
    }

    public AccessLevel getTwoFactorLevel() {
        return twoFactorLevel;
    }

    public String getZipCode() {
        return zipCode;
    }

    public boolean isEducation() {
        return license.isEducation();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpireDate();
    }

    public boolean isInfoComplete() {

        return StringUtils.containsData(this.contactEmail) && StringUtils.containsData(this.contactFirstname)
                && StringUtils.containsData(this.contactLastname) && StringUtils.containsData(this.organisation);

    }

    public boolean isPublic() {
        return Domain.PUBLIC.equals(name);
    }

    public final boolean isRootDomain() {
        return isRootDomain(getName());
    }

    public boolean isShareSupport() {
        return shareSupport;
    }

    public void setActivationCode(int activationCode) {
        this.activationCode = activationCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setArchiveAreas(HashMap<String, Integer> archiveAreas) {
        this.archiveAreas = archiveAreas;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactFirstname(String contactFirstname) {
        this.contactFirstname = contactFirstname;
    }

    public void setContactLastname(String contactLastname) {
        this.contactLastname = contactLastname;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setLicenseVariables(HashMap<Variable, Long> licenseVariables) {
        this.licenseVariables = licenseVariables;
    }

    public void setLoginKeyExpiration(long loginKeyExpiration) {
        this.loginKeyExpiration = loginKeyExpiration;
    }

    public void setMinPasswdLength(int minPasswdLength) {
        this.minPasswdLength = minPasswdLength;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setRoutingMap(HashMap<ServerType, ServerConfig> routingMap) {
        this.routingMap = routingMap;
    }

    public void setSaveAreas(HashMap<String, Integer> saveAreas) {
        this.saveAreas = saveAreas;
    }

    public void setSaves(HashMap<String, Integer> saves) {
        this.saves = saves;
    }

    public void setShareStorages(HashMap<String, Integer> shareStorages) {
        this.shareStorages = shareStorages;
    }

    public void setShareSupport(boolean shareSupport) {
        this.shareSupport = shareSupport;
    }

    public void setState(TState state) {
        this.state = state;
    }

    @Deprecated
    public void setSubDomainMap(HashMap<String, SubDomain> subDomainMap) {
        this.subDomainMap = subDomainMap;
    }

    public void setTLicense(TLicense license) {
        this.license = license;
    }

    public void setTwoFactorLevel(AccessLevel twoFactorLevel) {
        this.twoFactorLevel = twoFactorLevel;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
