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

import static nl.tytech.core.net.serializable.Domain.DEFAULT_MAX_PROJECT_DIM_M;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.net.serializable.Domain.Usage;
import nl.tytech.locale.TLanguage;
import nl.tytech.util.MathUtils;
import nl.tytech.util.MemoryUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Domain license types
 * @author Maxim Knepfle
 *
 */
public enum TLicense {

    NONE(1, 0, 0, 0, 0, 0, TColor.WHITE, 0),

    TRIAL(1, 6, 0, 5_000, 75, 0, new TColor(240, 85, 35), 100l * MemoryUtils.MB),

    STARTER(1, 6, 0, 5_000, 75, 0, new TColor(214, 122, 78), 100l * MemoryUtils.MB),

    @Deprecated
    BRONZE(1000, 6, 6_100, 5_000, 75, 0, new TColor(214, 122, 78), 100l * MemoryUtils.MB),

    @Deprecated
    SINGLE(1, 2, 6_250, DEFAULT_MAX_PROJECT_DIM_M, 75, 0, new TColor(214, 122, 78), 0),

    @Deprecated
    SILVER(1000, 20, 18_200, 10_000, 250, 0, new TColor(220, 220, 220), 1l * MemoryUtils.GB),

    @Deprecated
    PROFESSIONAL(5, 10, 18_750, DEFAULT_MAX_PROJECT_DIM_M, 150, 0, new TColor(220, 220, 220), 10l * MemoryUtils.GB),

    @Deprecated
    PROFESSIONAL_REGION(5, 10, 36_400, DEFAULT_MAX_PROJECT_DIM_M, 1250, 0, new TColor(220, 220, 220), 100l * MemoryUtils.GB),

    @Deprecated
    GOLD(1000, 100, 36_400, 30_000, 1250, 0, new TColor(238, 183, 68), 10l * MemoryUtils.GB),

    @Deprecated
    ENTERPRISE(20, 40, 38_000, DEFAULT_MAX_PROJECT_DIM_M, 300, 0, new TColor(238, 183, 68), 40l * MemoryUtils.GB),

    @Deprecated
    ENTERPRISE_REGION(20, 40, 54_000, DEFAULT_MAX_PROJECT_DIM_M, 2500, 0, new TColor(238, 183, 68), 400l * MemoryUtils.GB),

    @Deprecated
    PLATINUM(1000, 400, 67_100, DEFAULT_MAX_PROJECT_DIM_M, 5_000, 0, new TColor(164, 193, 201), 100l * MemoryUtils.GB),

    @Deprecated
    DIAMOND(1000, 2_000, 117_000, DEFAULT_MAX_PROJECT_DIM_M, 25_000, 0, new TColor(120, 168, 164), MemoryUtils.TB),

    @Deprecated
    EDU_FREE(1000, 30, 0, 1_000, 50, 0, new TColor(56, 86, 129), 100l * MemoryUtils.MB),

    @Deprecated
    EDU_PRO(1000, 50, 6_100, 10_000, 250, 0, new TColor(56, 86, 129), 1l * MemoryUtils.GB),

    // Table 2025 series with 2026 prices

    T25_BRONZE(5, 2, 6_430, DEFAULT_MAX_PROJECT_DIM_M, 75, 0, new TColor(214, 122, 78), 1l * MemoryUtils.GB),

    T25_SILVER(20, 10, 19_290, DEFAULT_MAX_PROJECT_DIM_M, 150, 0, new TColor(220, 220, 220), 10l * MemoryUtils.GB),

    T25_GOLD(50, 40, 37_450, DEFAULT_MAX_PROJECT_DIM_M, 1000, 500, new TColor(238, 183, 68), 40l * MemoryUtils.GB),

    T25_PLATINUM(100, 80, 55_550, DEFAULT_MAX_PROJECT_DIM_M, 2500, 1000, new TColor(164, 193, 201), 200l * MemoryUtils.GB),

    T25_DIAMOND(100, 100, 69_000, DEFAULT_MAX_PROJECT_DIM_M, 4000, 2000, new TColor(120, 168, 164), 400l * MemoryUtils.GB),

    T25_EDU_FREE(1000, 30, 0, 1_000, 50, 0, new TColor(56, 86, 129), 1l * MemoryUtils.GB),

    T25_EDU_PRO(1000, 50, 6_275, DEFAULT_MAX_PROJECT_DIM_M, 250, 0, new TColor(56, 86, 129), 1l * MemoryUtils.GB),

    ;

    public enum Archive {

        NONE, ARCHIVE, INSTANT;

        public static final Archive getValue(long licenseVariable) {

            for (Archive s : Archive.values()) {
                if (s.ordinal() == licenseVariable) {
                    return s;
                }
            }
            return NONE;
        }
    }

    public enum Support {

        COMMUNITY, PREMIUM, ADVANCED;

        public static final Support getValue(long licenseVariable) {

            for (Support s : Support.values()) {
                if (s.ordinal() == licenseVariable) {
                    return s;
                }
            }
            return PREMIUM;
        }
    }

    public enum Variable {

        MAX_USERS(1, 10_000),

        MAX_NEW_PROJECTS(0, 10_000),

        MAX_AREA_KM2(0, 1_000_000),

        MAX_ARCHIVE_KM2(0, 1_000_000),

        MAX_PROJECT_DIM_M(0, 75_000),

        MAX_PROJECT_VERSIONS(1, 20),

        MAX_SHARE_MB(0, MemoryUtils.TB / MemoryUtils.MB),

        ARCHIVE(Archive.NONE.ordinal(), Archive.INSTANT.ordinal()),

        SUPPORT(Support.COMMUNITY.ordinal(), Support.ADVANCED.ordinal()),

        MIN_CELL_CM(10, 1_000_000),

        MIN_PROJECT_CELLS(1_000, 120_000_000_000l),

        MAX_PROJECT_CELLS(1_000, 120_000_000_000l),

        ALLOW_BASIC(0, 1),

        ALLOW_PREVIEW(0, 1),

        @Deprecated
        ALLOW_SUBDOMAINS(0, 1),

        ALLOW_LONG_KEEPALIVE(0, 1),

        PRICE_EUR(0, Long.MAX_VALUE);

        private final long min, max;

        private Variable(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public long clamp(long value) {
            return MathUtils.clamp(value, min, max);
        }

        public String getBooleanValue(TLanguage lang, long value) {

            if (lang == TLanguage.NL) {
                return value > 0 ? "Ja" : "Nee";
            } else {
                return value > 0 ? "Yes" : "No";
            }
        }

        public long getMax() {
            return max;
        }

        public long getMin() {
            return min;
        }

        public String getTitle() {
            return getTitle(TLanguage.DEFAULT);
        }

        public String getTitle(TLanguage lang) {

            switch (this) {
                case ALLOW_BASIC:
                    return "Basic Mode";
                case ARCHIVE:
                    return lang == TLanguage.NL ? "Archief" : "Archive";
                case ALLOW_LONG_KEEPALIVE:
                    return lang == TLanguage.NL ? "Langdurige Keep-Alive" : "Long Keep-Alive";
                case ALLOW_PREVIEW:
                    return lang == TLanguage.NL ? "Toegang to Preview" : "Access to Preview";
                case ALLOW_SUBDOMAINS:
                    return lang == TLanguage.NL ? "Subdomeinen (deprecated)" : "Subdomains (deprecated)";
                case MAX_AREA_KM2:
                    return Usage.DOMAIN_AREA.getTitle(lang);
                case MAX_ARCHIVE_KM2:
                    return Usage.DOMAIN_ARCHIVE.getTitle(lang);
                case MAX_NEW_PROJECTS:
                    return Usage.NEW.getTitle(lang);
                case MAX_PROJECT_CELLS:
                    return lang == TLanguage.NL ? "Max project cellen" : "Max project cells";
                case MIN_PROJECT_CELLS:
                    return lang == TLanguage.NL ? "Min project cellen" : "Min project cells";
                case MAX_PROJECT_DIM_M:
                    return lang == TLanguage.NL ? "Max project grootte (km2)" : "Max Project Area (km2)";
                case MAX_PROJECT_VERSIONS:
                    return lang == TLanguage.NL ? "Max project versies" : "Max Project Versions";
                case SUPPORT:
                    return lang == TLanguage.NL ? "Support" : "Support";
                case PRICE_EUR:
                    return lang == TLanguage.NL ? "Prijs per jaar (ex. BTW)" : "Price per year (ex. VAT)";
                case MAX_SHARE_MB:
                    return Usage.SHARE_MB.getTitle(lang);
                case MAX_USERS:
                    return lang == TLanguage.NL ? "Gebruikers" : "Users";
                case MIN_CELL_CM:
                    return lang == TLanguage.NL ? "Min cell grootte (cm)" : "Min cell size (cm)";
                default:
                    return StringUtils.EMPTY;
            }
        }

        public boolean isBoolean() {

            switch (this) {
                case ALLOW_BASIC:
                case ALLOW_LONG_KEEPALIVE:
                case ALLOW_PREVIEW:
                case ALLOW_SUBDOMAINS:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static final TLicense[] ACTIVE_TYPES;

    static {
        List<TLicense> types = new ArrayList<>();
        for (TLicense type : TLicense.values()) {
            Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
            if (depAnno == null) {
                types.add(type);
            }
        }
        ACTIVE_TYPES = types.toArray(new TLicense[types.size()]);
    }

    public static final String LIMIT = "Limit reached: %s You can contact Tygron Support for an upgrade of your license.";

    public static final String getTypeTitle(TLanguage lang) {
        return lang == TLanguage.NL ? "Type LTS licentie" : "Type LTS license";
    }

    private final long maxNewProjects;

    private final long maxAreaKM2;

    private final long maxArchiveKM2;

    private final long maxShareMB;

    private final long priceEUR;

    private final TColor color;

    private final long maxProjectDimM;

    private final long maxUsers;

    private TLicense(long maxUsers, long newProjects, long priceEUR, long maxProjectDim, long maxAreaKM2, long maxArchiveKM2, TColor color,
            long maxShareBytes) {
        this.maxUsers = maxUsers;
        this.maxNewProjects = newProjects;
        this.priceEUR = priceEUR;
        this.color = color;
        this.maxAreaKM2 = maxAreaKM2;
        this.maxArchiveKM2 = maxArchiveKM2;
        this.maxProjectDimM = maxProjectDim;
        this.maxShareMB = maxShareBytes / MemoryUtils.MB;
    }

    public final TColor getColor() {
        return color;
    }

    public final long getDefaultDays() {

        switch (this) {
            case NONE:
                return 0;
            case TRIAL:
                return 10;
            case STARTER:
                return 30;
            default:
                return 365;
        }
    }

    public final TLicense getUpgrade() {

        switch (this) {

            // basics
            case TRIAL:
                return STARTER;
            case STARTER:
                return T25_BRONZE;
            case EDU_FREE:
                return T25_EDU_PRO;

            // new 2025
            case BRONZE:
            case T25_BRONZE:
            case SINGLE:
            case EDU_PRO:
                return T25_SILVER;

            case SILVER:
            case T25_SILVER:
            case PROFESSIONAL:
                return T25_GOLD;

            case GOLD:
            case T25_GOLD:
            case PROFESSIONAL_REGION:
                return T25_PLATINUM;

            case PLATINUM:
            case T25_PLATINUM:
            case ENTERPRISE_REGION:
                return T25_DIAMOND;

            // no upgrade
            default:
                return null;
        }
    }

    public final long getVariable(Variable variable) {

        switch (variable) {
            case ALLOW_BASIC:
                return isSingle() || this == NONE || this == PROFESSIONAL || this == ENTERPRISE ? 0 : 1;
            case ALLOW_PREVIEW:
                return 0;
            case ALLOW_SUBDOMAINS:
                return 0;
            case ALLOW_LONG_KEEPALIVE:
                return 0;
            case ARCHIVE:
                return isOldModel() || this == NONE || maxArchiveKM2 == 0 ? Archive.NONE.ordinal() : Archive.ARCHIVE.ordinal();
            case MAX_AREA_KM2:
                return maxAreaKM2;
            case MAX_ARCHIVE_KM2:
                return maxArchiveKM2;
            case MAX_NEW_PROJECTS:
                return maxNewProjects;
            case MAX_USERS:
                return maxUsers;
            case MAX_PROJECT_DIM_M:
                return maxProjectDimM;
            case MAX_PROJECT_VERSIONS:
                return isSingle() || isEducation() || this == T25_BRONZE ? 2
                        : this == PROFESSIONAL || this == PROFESSIONAL_REGION || this == T25_SILVER ? 5 : 10;
            case SUPPORT:
                switch (this) {
                    case NONE:
                    case SINGLE:
                        return Support.COMMUNITY.ordinal();
                    case ENTERPRISE:
                    case ENTERPRISE_REGION:
                        return Support.ADVANCED.ordinal();
                    default:
                        return Support.PREMIUM.ordinal();
                }
            case MAX_SHARE_MB:
                return maxShareMB;
            case PRICE_EUR:
                return priceEUR;
            case MIN_CELL_CM:
                return isSingle() || isEducation() ? 50 : 25;
            case MIN_PROJECT_CELLS:
                return isSingle() || this == EDU_FREE || this == T25_EDU_FREE ? 10_000_000_000l : 30_000_000_000l; // 30 billion default
            case MAX_PROJECT_CELLS:
                return isSingle() || this == EDU_FREE || this == T25_EDU_FREE ? 30_000_000_000l : 60_000_000_000l; // 60 billion default
            default:
                return 0;
        }
    }

    public final boolean isEducation() {
        return this == EDU_PRO || this == EDU_FREE || this == T25_EDU_PRO || this == T25_EDU_FREE;
    }

    /**
     * Old license model from before 2025
     */
    public final boolean isOldModel() {

        return switch (this) {
            // old 2023
            case BRONZE -> true;
            case SILVER -> true;
            case GOLD -> true;
            case PLATINUM -> true;
            case DIAMOND -> true;
            // old 2024
            case SINGLE -> true;
            case ENTERPRISE -> true;
            case ENTERPRISE_REGION -> true;
            case PROFESSIONAL -> true;
            case PROFESSIONAL_REGION -> true;
            // old edu
            case EDU_FREE -> true;
            case EDU_PRO -> true;
            default -> false;
        };
    }

    /**
     * Show in contract table as valid option
     */
    public final boolean isPromoted(boolean showAll, boolean oldModel, boolean starter) {

        if (showAll) {
            return true; // show them all

        } else if (this == NONE || this == TRIAL) {
            return false; // never

        } else if (this == STARTER) {
            return starter; // only promote when i have start license

        } else {
            // show model type and always edu
            return this.isOldModel() == oldModel;
        }
    }

    private final boolean isSingle() {
        return this == TRIAL || this == STARTER || this == SINGLE;
    }

    @Override
    public final String toString() {
        return super.toString().replaceAll("_", " ");
    }
}
