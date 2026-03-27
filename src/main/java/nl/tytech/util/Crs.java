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

/**
 * Coordinate Reference System with force XY and URN options.
 *
 * @author Frank Baars
 *
 */
public class Crs {

    public static class Json extends Crs {

        /**
         * JSON spec is always longitude latitude
         */
        public Json(Crs crs) {
            super(crs, true);
        }

        /**
         * JSON spec is always longitude latitude
         */
        public Json(String urn) {
            super(urn, true);
        }
    }

    public static final String DEFAULT_AUTH = "EPSG";

    public static final String LOCAL_AUTH = "TYGRON";

    public static final String LOCAL_CODE = "LOCAL";

    private static final String[] URL_START = { "http://www.opengis.net/def/crs/", "https://www.opengis.net/def/crs/" };

    private static final String URN_START = "urn:ogc:def:crs:";

    private final String auth;
    private final String code;
    private final boolean forceXY;

    public Crs(Crs other, boolean forceXY) {
        this.auth = other.auth;
        this.code = other.code;
        this.forceXY = forceXY;
    }

    public Crs(String crsInput, boolean forceXY) {

        // remove optional starting urn
        String crsTxt = removeUrnStart(crsInput);

        // authority provide split
        if (crsTxt.contains(":")) {
            String[] array = crsTxt.split(":");
            if (array.length <= 1) {
                throw new IllegalArgumentException("Invalid CRS: " + crsInput);
            }
            // select last two values, auth may have multiple separators
            this.auth = array[array.length - 2].toUpperCase();
            this.code = array[array.length - 1].toUpperCase();
        } else { // fallback to EPSG authority
            if (crsTxt.isEmpty()) {
                throw new IllegalArgumentException("Invalid CRS: " + crsInput);
            }
            this.code = crsTxt.toUpperCase();
            this.auth = isLocal() ? LOCAL_AUTH : DEFAULT_AUTH;
        }
        this.forceXY = forceXY;
    }

    @Override
    public boolean equals(Object obj) {

        // same object
        if (this == obj) {
            return true;
        }

        // null's and other classes
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Crs other = (Crs) obj;
        return this.auth.equals(other.auth) && this.code.equals(other.code) && forceXY == other.forceXY;
    }

    public String getAuthCode() {
        return auth + ":" + code;
    }

    public String getCode() {
        return code;
    }

    public final String getUrl() {
        return URL_START[0] + auth + "/0/" + code;
    }

    public final String getUrn() {
        return URN_START + auth + "::" + code;
    }

    public boolean isForceXY() {
        return forceXY;
    }

    public boolean isLocal() {
        return LOCAL_CODE.equals(code);
    }

    private String removeUrnStart(String crsTxt) {
        if (!StringUtils.containsData(crsTxt)) {
            return StringUtils.EMPTY;
        }
        crsTxt = crsTxt.toLowerCase();
        for (int i = 0; i < URL_START.length; i++) {
            if (crsTxt.startsWith(URL_START[i])) {
                return replaceUrl(crsTxt, URL_START[i]);
            }
        }
        return crsTxt.replace(URN_START, "").replace("::", ":");
    }

    private String replaceUrl(String crsTxt, String url) {
        crsTxt = crsTxt.replace(url, "");
        int lastIndex = crsTxt.lastIndexOf("/");
        if (lastIndex < crsTxt.length()) {
            crsTxt = crsTxt.substring(0, crsTxt.indexOf("/")) + ":" + crsTxt.substring(lastIndex + 1);
        }
        return replaceUrn(crsTxt);
    }

    private String replaceUrn(String crsTxt) {
        return crsTxt.replace(URN_START, "").replace("::", ":");
    }

    @Override
    public String toString() {
        return getAuthCode() + (isForceXY() ? " (Longitude first)" : "");
    }
}
