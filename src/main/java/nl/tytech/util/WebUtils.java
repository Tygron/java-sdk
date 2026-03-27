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

import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;
import nl.tytech.util.logger.TLogger;

/**
 * Utils for handling web encoding in e.g. urls
 *
 * @author Maxim Knepfle
 *
 */
public class WebUtils {

    /**
     * Start of HTTP Secure URL
     */
    public static final String HTTPS = "https://";

    private static final String _decode(String text, Type type) {

        // early out
        if (!StringUtils.containsData(text)) {
            return text;
        }

        // convert to clean
        try {
            // (Frank): We currently do not use MediaType: application/x-www-form-urlencoded
            // Therefore use this method, instead of URLDecoder
            // Also see: https://stackoverflow.com/questions/2678551/when-to-encode-space-to-plus-or-20
            return UriComponent.decode(text, type);
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    private static final String _encode(String text, Type type) {

        // early out
        if (!StringUtils.containsData(text)) {
            return text;
        }

        // convert to default
        try {
            // We currently do not use MediaType: application/x-www-form-urlencoded
            // Therefore use this method, instead of URLEncoder
            // Also see: https://stackoverflow.com/questions/2678551/when-to-encode-space-to-plus-or-20
            return UriComponent.encode(text, type);
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    public static final String decode(String text) {
        return _decode(text, Type.QUERY_PARAM_SPACE_ENCODED);
    }

    public static final String decodePath(String text) {
        return _decode(text, Type.PATH);
    }

    public static final String encode(String text) {
        return _encode(text, Type.QUERY_PARAM_SPACE_ENCODED);
    }

    public static final String encodePath(String text) {
        return _encode(text, Type.PATH);
    }

    public static final String getParamValue(String url, String param) {

        // split up the query from path
        String[] split = url.split("\\?");
        if (split.length > 1) {
            for (String urlParam : split[1].split("&")) {
                String[] paramPair = urlParam.split("=");
                if (paramPair.length > 1 && param.equalsIgnoreCase(paramPair[0])) {
                    return paramPair[1];
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
