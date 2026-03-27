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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import nl.tytech.core.net.Network;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.item.Moment;
import nl.tytech.naming.GeoNC;
import nl.tytech.util.logger.TLogger;

/**
 * Manager class to do all kinds of basic REST calls to web servers.
 * @author Maxim Knepfle
 *
 */
public class RestManager {

    /**
     * Server query format e.g. ?f=JSON
     */
    public enum Format {

        /**
         * HTML formatted data
         */
        HTML(MediaType.TEXT_HTML),

        /**
         * Plain XML, return as String
         */
        XML(MediaType.APPLICATION_XML),

        /**
         * Speech Synthesis Markup Language based on XML
         */
        SSML("application/ssml+xml"),

        /**
         * GML, almost similar to XML, but can contain suptypes
         */
        GML(MediaType.APPLICATION_XML),

        /**
         * Plain text JSON
         */
        JSON(MediaType.APPLICATION_JSON),

        /**
         * Typed text JSON
         */
        TJSON(MediaType.APPLICATION_JSON, "Typed JSON"),

        /**
         * GeoJSON
         */
        GEOJSON(MediaType.APPLICATION_JSON, GeoNC.GEOJSON),

        /**
         * GeoPackage
         */
        GPKG(MediaType.APPLICATION_OCTET_STREAM, GeoNC.GEOPACKAGE),

        /**
         * CityGML
         */
        CITYGML(MediaType.APPLICATION_XML, "CityGML"),

        /**
         * CityJSON
         */
        CITYJSON(MediaType.APPLICATION_JSON, "CityJSON"),

        /**
         * glTF
         */
        GLTF(TMediaType.MODEL_GLTF, "glTF"),

        /**
         * DXF
         */
        DXF(TMediaType.IMAGE_DXF, GeoNC.DXF),

        /**
         * Comma Separated Value
         */
        CSV(TMediaType.TEXT_CSV, "Comma Separated Value"),

        /**
         * Semicolon Separated Value
         */
        SSV(TMediaType.TEXT_CSV, "Semicolon Separated Value"),

        /**
         * Smile JSON
         */
        SMILE(MediaType.APPLICATION_OCTET_STREAM, "Binary JSON"),

        /**
         * Smile Typed JSON
         */
        TSMILE(MediaType.APPLICATION_OCTET_STREAM, "Binary Typed JSON"),

        /**
         * Binary zipped JSON
         */
        ZIPJSON(MediaType.APPLICATION_OCTET_STREAM, "Zipped JSON"),

        /**
         * Binary zipped Typed JSON
         */
        ZIPTJSON(MediaType.APPLICATION_OCTET_STREAM, "Zipped Typed JSON"),

        /**
         * Zipped Smile JSON
         */
        ZIPSMILE(MediaType.APPLICATION_OCTET_STREAM, "Zipped Binary JSON"),

        /**
         * Zipped Smile Typed JSON
         */
        ZIPTSMILE(MediaType.APPLICATION_OCTET_STREAM, "Zipped Binary Typed JSON"),

        /**
         * Unzipped Assets binary data
         */
        BINARY(MediaType.APPLICATION_OCTET_STREAM),

        /**
         * Zipped Assets binary data
         */
        ZIPBINARY(MediaType.APPLICATION_OCTET_STREAM, "Zipped Binary");

        public static final String QUERY = "f";

        public static final String API_VERSION = "api-version";

        public static final String CRS = "crs";

        public static final String BBOX = "bbox";

        public static final String INPUT = "input";

        public static final String GZIP = "gzip";

        /**
         * Default event input format (uncompressed smile)
         */
        public static final Format DEFAULT_EVENT = Format.SMILE;

        /**
         * Default output format for large typed items (compressed typed smile)
         */
        public static final Format DEFAULT_ITEMS = Format.ZIPTSMILE;

        private final String mediaType, mediaResponse;

        private final String desc;

        private Format(String mediaType) {
            this(mediaType, null);
        }

        private Format(String mediaType, String desc) {
            this.mediaType = mediaType;
            this.mediaResponse = mediaType + (!MediaType.APPLICATION_OCTET_STREAM.equals(mediaType) ? TMediaType.CHARSET_UTF8 : "");
            this.desc = desc != null ? desc : name();
        }

        public final String getAttachmentFileName(String name) {

            return switch (this) {
                case GPKG -> name + ".gpkg";
                case DXF -> name + ".dxf";
                default -> null;
            };
        }

        public String getDescription() {
            return desc;
        }

        /**
         * Associated media type response from server
         */
        public String getMediaResponse() {
            return mediaResponse;
        }

        /**
         * Associated media type
         */
        public String getMediaType() {
            return mediaType;
        }

        public boolean isApi() {
            return this == HTML || this == JSON || this == SMILE || this == ZIPJSON || this == ZIPSMILE;
        }

        public boolean isJson() {
            return this == JSON || this == TJSON || this == ZIPJSON || this == ZIPTJSON;
        }

        public boolean isPrettified() {
            return this == TJSON || this == JSON || this == HTML;
        }

        public boolean isSmile() {
            return this == SMILE || this == TSMILE || this == ZIPSMILE || this == ZIPTSMILE;
        }

        public boolean isTyped() {
            return this == TJSON || this == ZIPTJSON || this == TSMILE || this == ZIPTSMILE;
        }

        public boolean isZipped() {
            return this == ZIPJSON || this == ZIPTJSON || this == ZIPSMILE || this == ZIPTSMILE || this == ZIPBINARY;
        }

        /**
         * Return the uncompressed version of this format, or when uncompressed itself
         */
        public Format unZip() {
            return switch (this) {
                case ZIPBINARY -> BINARY;
                case ZIPJSON -> JSON;
                case ZIPTJSON -> TJSON;
                case ZIPSMILE -> SMILE;
                case ZIPTSMILE -> TSMILE;
                default -> this;
            };
        }

        /**
         * Return the compressed version of this format, or when compressed itself
         */
        public Format zip() {
            return switch (this) {
                case BINARY -> ZIPBINARY;
                case JSON -> ZIPJSON;
                case TJSON -> ZIPTJSON;
                case SMILE -> ZIPSMILE;
                case TSMILE -> ZIPTSMILE;
                default -> this;
            };
        }
    }

    /**
     * Default supported http methods.
     *
     */
    private enum HttpMethod {
        GET, POST, PUT, DELETE;
    }

    private record ServerSettings(MultivaluedMap<String, Object> headers, boolean chunked) {

        public RequestEntityProcessing getEntityProcessing() {
            return chunked ? RequestEntityProcessing.CHUNKED : RequestEntityProcessing.BUFFERED;
        }

        public MultivaluedMap<String, Object> getHeaders() {
            return headers;
        }
    }

    private static final class SingletonHolder {

        private static final RestManager INSTANCE = new RestManager();
    }

    public static final class TWebApplicationException extends WebApplicationException {

        private static final long serialVersionUID = 3945325793873501654L;

        private final int statusCode;

        private final String urlInfo;

        public TWebApplicationException(int statusCode, String message, String urlInfo) {
            this(statusCode, TMediaType.TEXT_PLAIN_UTF8, message, urlInfo);
        }

        public TWebApplicationException(int statusCode, String mediaType, String message, String urlInfo) {
            // put the message also in the response body
            super(message, Response.status(statusCode).entity(message).type(mediaType).build());
            this.statusCode = statusCode;
            this.urlInfo = urlInfo;

            if (TStatus.BAD_REQUEST.isCode(statusCode)) {
                TLogger.warning("Bad User Request: " + message);
            } else if (TStatus.FORBIDDEN.isCode(statusCode)) {
                TLogger.warning("Forbidden User Request: " + message);
            }
        }

        public TWebApplicationException(Response aResponse, String message) {
            super(message, aResponse);
            this.statusCode = aResponse.getStatus();
            this.urlInfo = "-";
        }

        public TWebApplicationException(TStatus status) {
            this(status, status.getMessage());
        }

        public TWebApplicationException(TStatus status, String message) {
            this(status, message, "-");
        }

        public TWebApplicationException(TStatus status, String message, String urlInfo) {
            this(status.getCode(), message, urlInfo);
        }

        /**
         * Return HTML Status code
         */
        public int getStatusCode() {
            return statusCode;
        }

        /**
         * Return valid TStatus or otherwise NULL.
         */
        public TStatus getTStatus() {
            return TStatus.getTStatus(statusCode);
        }

        /**
         * Get additional info on the URL where error came from
         */
        public String getUrlInfo() {
            return urlInfo;
        }

        /**
         * 5xx Server Error?
         */
        public boolean isServerError() {
            return statusCode >= 500 && statusCode < 600;
        }
    }

    /**
     * Default timeouts to connect
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = (int) Moment.MINUTE;
    public static final int DEFAULT_READ_TIMEOUT = (int) (5 * Moment.MINUTE);

    public static final void delete(String serverURL, String restPath) throws TWebApplicationException {
        delete(serverURL, restPath, null);
    }

    public static final <T> T delete(String serverURL, String restPath, Class<T> responseClass) throws TWebApplicationException {
        return delete(serverURL, restPath, null, responseClass);
    }

    public static final <T> T delete(String serverURL, String restPath, String[] params, Class<T> responseClass)
            throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionOther(serverURL, restPath, params, null, responseClass, Format.JSON, Format.JSON,
                HttpMethod.DELETE, null, true);
    }

    public static final <T> T get(String serverURL, String restPath, Class<T> responseClass) throws TWebApplicationException {
        return get(serverURL, restPath, null, responseClass);
    }

    public static final <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass)
            throws TWebApplicationException {
        return get(serverURL, restPath, params, responseClass, Format.JSON);
    }

    public static final <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass, Format outputFormat)
            throws TWebApplicationException {
        return get(serverURL, restPath, params, responseClass, outputFormat, null);
    }

    public static final <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass, Format outputFormat,
            boolean addQuery) throws TWebApplicationException {
        return get(serverURL, restPath, params, responseClass, outputFormat, null, addQuery);
    }

    public static final <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass, Format outputFormat,
            Integer overrideTimeout) throws TWebApplicationException {
        return get(serverURL, restPath, params, responseClass, outputFormat, overrideTimeout, true);
    }

    public static final <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass, Format outputFormat,
            Integer overrideTimeout, boolean addQuery) throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionOther(serverURL, restPath, params, null, responseClass, Format.JSON, outputFormat,
                HttpMethod.GET, overrideTimeout, addQuery);
    }

    /**
     * Return valid URL or exception
     */
    public static final URL getWebTargetURL(String serverURL, String path, String[] params) {
        return SingletonHolder.INSTANCE._getWebTargetURL(serverURL, path, params);
    }

    public static final <T> T jsonPut(String serverURL, String restPath, Object content) throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionOther(serverURL, restPath, null, content, null, Format.JSON, Format.JSON, HttpMethod.PUT,
                null, true);
    }

    public static final <T> T post(String serverURL, String restPath, Object content) throws TWebApplicationException {
        return post(serverURL, restPath, null, content);
    }

    public static final <T> T post(String serverURL, String restPath, String[] params, Object content) throws TWebApplicationException {
        return post(serverURL, restPath, params, content, null);
    }

    public static <T> T post(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass)
            throws TWebApplicationException {
        return post(serverURL, restPath, params, content, responseClass, Format.JSON);
    }

    public static final <T> T post(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass,
            Format format) throws TWebApplicationException {
        return post(serverURL, restPath, params, content, responseClass, format, null);
    }

    public static final <T> T post(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass,
            Format inputFormat, Format outputFormat, Integer overrideTimeout) throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionOther(serverURL, restPath, params, content, responseClass, inputFormat, outputFormat,
                HttpMethod.POST, overrideTimeout, true);
    }

    public static final <T> T post(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass,
            Format format, Integer overrideTimeout) throws TWebApplicationException {
        return post(serverURL, restPath, params, content, responseClass, format, format, overrideTimeout);
    }

    public static final <T> T postForm(String serverURL, String restPath, String[] params, Form form, Class<T> responseClass, Format format)
            throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionForm(serverURL, restPath, params, Entity.form(form), responseClass, format);
    }

    public static final <T> T postForm(String serverURL, String restPath, String[] params, String[] content, Class<T> responseClass,
            Format format) throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionMapForm(serverURL, restPath, params, content, responseClass, format);
    }

    public static final <T> T put(String serverURL, String restPath, Object content) throws TWebApplicationException {
        return put(serverURL, restPath, null, content);
    }

    public static <T> T put(String serverURL, String restPath, String[] params, Object content) throws TWebApplicationException {
        return put(serverURL, restPath, params, content, null);
    }

    public static <T> T put(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass)
            throws TWebApplicationException {
        return put(serverURL, restPath, params, content, responseClass, Format.JSON);
    }

    public static <T> T put(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass, Format format)
            throws TWebApplicationException {
        return put(serverURL, restPath, params, content, responseClass, format, null);
    }

    public static <T> T put(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass, Format inputFormat,
            Format outputFormat, Integer overrideTimeout) throws TWebApplicationException {
        return SingletonHolder.INSTANCE._actionOther(serverURL, restPath, params, content, responseClass, inputFormat, outputFormat,
                HttpMethod.PUT, overrideTimeout, true);
    }

    public static <T> T put(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass, Format format,
            Integer overrideTimeout) throws TWebApplicationException {
        return put(serverURL, restPath, params, content, responseClass, format, format, overrideTimeout);
    }

    public static <T> T readJsonURL(Format format, URL url, Class<T> classz) throws Exception {
        try {
            return RestUtils.readJsonURL(format, url, classz);

        } catch (Exception e) {
            if (url.toExternalForm().startsWith("http")) {
                get(url.toExternalForm(), "", new String[0], String.class);
            }
            throw e;
        }
    }

    public static void setHeaders(String serverURL, MultivaluedMap<String, Object> headers) {
        setServerSettings(serverURL, headers, true);
    }

    public static void setServerSettings(String serverURL, MultivaluedMap<String, Object> headers, boolean chunked) {
        SingletonHolder.INSTANCE._setServerSettings(serverURL, headers, chunked);
    }

    private final Client client;

    private final Map<String, WebTarget> targetMap = new HashMap<>();

    private Map<String, ServerSettings> settingsMap = new HashMap<>();

    private RestManager() {

        final ClientConfig clientConfig = new ClientConfig();
        // by default timeout is infinity! Set to our defaults
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
        clientConfig.property(ClientProperties.READ_TIMEOUT, DEFAULT_READ_TIMEOUT);
        client = ClientBuilder.newBuilder().withConfig(clientConfig).build();
    }

    private final <T> T _actionForm(String serverURL, String path, String[] params, Entity<Form> form, Class<T> responseClass,
            Format outputFormat) throws TWebApplicationException {

        // validate format
        if (outputFormat != Format.JSON && outputFormat != Format.TJSON) {
            throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid query fomat: " + outputFormat);
        }

        // Create target
        WebTarget target = getWebTarget(serverURL, path, params);

        // execute target
        return execute(target, serverURL, form, responseClass, outputFormat, HttpMethod.POST, null);
    }

    private final <T> T _actionMapForm(String serverURL, String path, String[] params, String[] content, Class<T> responseClass,
            Format outputFormat) throws TWebApplicationException {

        // create form from MultivaluedMap
        Entity<Form> fe;
        try {
            MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
            formData.add(Format.QUERY, outputFormat.name());
            if (content != null) {
                for (int i = 0; i < content.length; i += 2) {
                    formData.add(content[i], content[i + 1]);
                }
            }
            fe = Entity.form(formData);
        } catch (Exception e) {
            throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid Form Input: " + e.getMessage(), serverURL + path);
        }

        // handle form
        return _actionForm(serverURL, path, params, fe, responseClass, outputFormat);
    }

    private final <T> T _actionOther(String serverURL, String path, String[] queryParams, Object content, Class<T> responseClass,
            Format inputFormat, Format outputFormat, HttpMethod method, Integer overrideTimeout, boolean addOutputFormat)
            throws TWebApplicationException {

        if (inputFormat == null || inputFormat == Format.HTML) {
            throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid input format: " + inputFormat);
        }
        if (outputFormat == null) {
            throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid output format: " + outputFormat);
        }

        // Create target and add format in query
        WebTarget target = getWebTarget(serverURL, path, queryParams);

        // Set Output format
        if (addOutputFormat) {
            target = target.queryParam(Format.QUERY, outputFormat.name());
        }

        // create content entity
        Entity<?> entity = null;
        if (content != null) {

            // When Input is not default JSON add it as param
            if (inputFormat != Format.JSON) {
                target = target.queryParam(Format.INPUT, inputFormat.name());
            }

            // fill content entity
            try {
                if (inputFormat == Format.JSON || inputFormat == Format.TJSON) {
                    entity = Entity.json(RestUtils.writeJsonString(inputFormat, content));

                } else if (inputFormat == Format.BINARY || inputFormat == Format.ZIPBINARY) {
                    entity = Entity.entity(content, MediaType.APPLICATION_OCTET_STREAM_TYPE);

                } else if (inputFormat == Format.SSML) {
                    entity = Entity.entity(content, Format.SSML.getMediaType());

                } else {
                    entity = Entity.entity(RestUtils.writeObject(content, inputFormat), MediaType.APPLICATION_OCTET_STREAM_TYPE);
                }
            } catch (Exception e) {
                throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid " + inputFormat + " Input: " + e.getMessage(),
                        target.getUri().toString());
            }
        }

        // execute target
        return execute(target, serverURL, entity, responseClass, outputFormat, method, overrideTimeout);
    }

    private final URL _getWebTargetURL(String serverURL, String path, String[] params) {

        try {
            WebTarget target = this.buildWebTarget(serverURL, path, params);
            return target.getUri() == null ? null : target.getUri().toURL();
        } catch (IllegalArgumentException | IllegalStateException | MalformedURLException e) {
            throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid URL: " + e.getMessage());
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    private final synchronized void _setServerSettings(String serverURL, final MultivaluedMap<String, Object> headers, boolean chunked) {

        if (headers == null) {
            TLogger.severe("Invalid headers for server: " + serverURL);
            return;
        }
        // add user agent and referrer headers
        headers.putSingle(HttpHeaders.USER_AGENT, Engine.USER_AGENT);
        headers.putSingle(Network.REFERER_HEADER, SettingsManager.getServerWebAddress());

        // add to map
        ServerSettings settings = new ServerSettings(headers, chunked);
        Map<String, ServerSettings> newSettingsMap = new HashMap<>(settingsMap);
        newSettingsMap.put(serverURL, settings);

        // replace old map in 1 call.
        this.settingsMap = newSettingsMap;
    }

    private final WebTarget buildWebTarget(String serverURL, String path, String[] params) throws Exception {

        /**
         * Add existing params and cleanup serverURL
         */
        if (serverURL.contains("?")) {
            params = updateParams(serverURL, params);
            serverURL = serverURL.split("\\?")[0];
        }
        if (path != null && path.contains("?")) {
            params = updateParams(path, params);
            path = path.split("\\?")[0];
        }

        WebTarget target = getWebTarget(serverURL);
        target = target.path(path);
        if (params != null) {
            for (int i = 0; i < params.length; i += 2) {

                /**
                 * Skip empty params
                 */
                if (!StringUtils.containsData(params[i])) {
                    continue;
                }
                /**
                 * Remove spaces and make it url encoded
                 */
                String dirtyParam = StringUtils.internalTrim(params[i + 1]);
                String safeParam = WebUtils.encode(dirtyParam);
                target = target.queryParam(params[i], safeParam);
            }
        }
        return target;
    }

    private final <T> T execute(WebTarget target, String serverURL, Entity<?> entity, Class<T> responseClass, Format format,
            HttpMethod method, Integer overrideTimeout) throws TWebApplicationException {

        // create Connection builder
        ServerSettings settings = getServerSettings(serverURL);
        target.property(ClientProperties.REQUEST_ENTITY_PROCESSING, settings.getEntityProcessing());
        Builder builder = target.request(format.getMediaType());
        builder = builder.headers(settings.getHeaders());

        // override timeout for this connection only
        if (overrideTimeout != null) {
            builder.property(ClientProperties.CONNECT_TIMEOUT, overrideTimeout);
            builder.property(ClientProperties.READ_TIMEOUT, overrideTimeout);
        }

        /**
         * Execute the connection
         */
        Response response = null;
        try {
            if (method == HttpMethod.GET) {
                response = builder.get();
            } else if (method == HttpMethod.DELETE) {
                response = builder.delete();
            } else if (method == HttpMethod.POST) {
                response = builder.post(entity);
            } else if (method == HttpMethod.PUT) {
                response = builder.put(entity);
            }

        } catch (Exception e) {
            throwConnectionException(target, method, e);
            // when not a connection error falback to default error
            throw new TWebApplicationException(TStatus.CONNECTION_FAILED, e.getMessage(), target.getUri().toString() + " (" + method + ")");
        }

        /**
         * Handle the response
         */
        try {
            int statusCode = response.getStatus();
            String urlInfo = target.getUri().toString() + " (method: " + method + " code: " + statusCode + ")";

            if (TStatus.OK.isCode(statusCode) || TStatus.CREATED.isCode(statusCode) || TStatus.ACCEPTED.isCode(statusCode)) {
                try {
                    return responseClass != null ? RestUtils.readResponse(response, responseClass, format) : null;
                } catch (Exception e) {
                    throwConnectionException(target, method, e);
                    // when not a connection error falback to default error
                    throw new TWebApplicationException(TStatus.BAD_REQUEST, "Received invalid " + format + ": " + e.getMessage(), urlInfo);
                }

            } else if (TStatus.NO_CONTENT.isCode(statusCode)) {
                return null; // empty response

            } else {
                String message = response.readEntity(String.class);
                String authHeader = response.getHeaderString(HttpHeaders.WWW_AUTHENTICATE);
                if (authHeader != null) {
                    message += authHeader.replace(Network.WWW_AUTH_REALM, "").replace("\"", "");
                }
                throw new TWebApplicationException(statusCode, message, urlInfo);
            }

        } finally {
            if (responseClass != GZIPInputStream.class && responseClass != InputStream.class) {
                response.close();
            }
        }
    }

    private final ServerSettings getServerSettings(String serverURL) {

        if (!settingsMap.containsKey(serverURL)) {
            // remove params from url
            serverURL = serverURL.split("\\?")[0];

            // always have basic headers
            if (!settingsMap.containsKey(serverURL)) {
                this._setServerSettings(serverURL, new MultivaluedHashMap<>(), true);
            }
        }

        // return the settings
        return settingsMap.get(serverURL);
    }

    private final synchronized WebTarget getWebTarget(String serverURL) {

        if (!targetMap.containsKey(serverURL)) {
            TLogger.info("Creating new JSON/REST WebTarget: " + serverURL);
            targetMap.put(serverURL, client.target(serverURL));
        }
        return targetMap.get(serverURL);
    }

    private final WebTarget getWebTarget(String serverURL, String path, String[] params) {

        try {
            return buildWebTarget(serverURL, path, params);

        } catch (IllegalArgumentException e) {
            throw new TWebApplicationException(TStatus.BAD_REQUEST, "Invalid URL: " + e.getMessage());

        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    private final void throwConnectionException(WebTarget target, HttpMethod method, Exception e) throws TWebApplicationException {

        String urlInfo = target.getUri().toString() + " (" + method + ")";
        if (e instanceof SocketTimeoutException || e.getCause() instanceof SSLException
                || "Already Connected".equalsIgnoreCase(e.getMessage())) {
            throw new TWebApplicationException(TStatus.SSL_HANDSHAKE_FAILED, "SSL Failure", urlInfo);

        } else if (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException) {
            throw new TWebApplicationException(TStatus.REQUEST_TIMEOUT, "Connection Timeout", urlInfo);

        } else if (e instanceof SocketException || e.getCause() instanceof SocketException || e instanceof UnknownHostException
                || e.getCause() instanceof UnknownHostException) {
            throw new TWebApplicationException(TStatus.SERVICE_UNAVAILABLE, e.getMessage(), urlInfo);
        }
    }

    private final String[] updateParams(String url, String[] params) {

        List<String> list = new ArrayList<>();
        if (params != null) { // add existing
            list.addAll(Arrays.asList(params));
        }

        // split up the query from path
        String[] split = url.split("\\?");
        if (split.length <= 1) {
            return params; // no extra params (ends with ?)
        }

        // split up the params
        String[] urlParams = split[1].split("&");
        loop: for (String urlParam : urlParams) {

            // split into name & value
            String[] paramSplit = urlParam.split("=");

            // set name
            String name = WebUtils.encode(paramSplit[0]);

            // cannot use protected variable for Format
            if (Format.QUERY.equalsIgnoreCase(name)) {
                continue loop;
            }

            // set value
            String value = StringUtils.EMPTY;
            if (paramSplit.length >= 2) {
                value = WebUtils.encode(paramSplit[1]);
            }

            // Only add when not already in Array
            for (int i = 0; i < list.size(); i += 2) {
                if (name.equalsIgnoreCase(list.get(i))) {
                    continue loop;
                }
            }

            // Add new param to array
            list.add(name);
            list.add(value);
        }

        // convert back to array
        return list.toArray(new String[list.size()]);
    }
}
