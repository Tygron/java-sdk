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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.RestManager.TWebApplicationException;

/**
 * Util function to correctly format json, xml stuff for REST calls, also added special serializer to handle JTS classes like polygons
 *
 * @author Maxim Knepfle
 *
 */
public final class RestUtils {

    private static final int INPUTSTREAM_SLEEP_MS = 100;

    private static final int INPUTSTREAM_ATTEMPTS = 10;

    private static final int PRIMITIVE_SIZE = 64;

    private static final int OBJECT_SIZE = BufferUtils.SIZE;

    public static final JsonParser createParser(Format format, InputStream inputStream) throws IOException {

        // handle zipped or plain
        InputStream is = format.isZipped() ? new GZIPInputStream(inputStream) : inputStream;

        // wait for the stream to become available, note: this may never happen
        for (int i = 0; i < INPUTSTREAM_ATTEMPTS; i++) {
            if (is.available() == 0) {
                ThreadUtils.sleepInterruptible(INPUTSTREAM_SLEEP_MS);
            }
        }

        // connect anyway
        return JsonMapper.getLocalMapper(format).getFactory().createParser(is);
    }

    public static final boolean isSupportedGML(String contentType) {
        return contentType.startsWith(MediaType.TEXT_XML) && contentType.contains(TMediaType.SUBTYPE);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T readByteStream(InputStream inputStream, Class<T> responseClass, boolean zipped) throws Exception {

        if (responseClass == byte[].class) {
            return (T) ZipUtils.fromByteStream(inputStream, zipped);
        }
        if (responseClass == GZIPInputStream.class) {
            return (T) new GZIPInputStream(inputStream);
        }
        if (responseClass == InputStream.class) {
            return (T) inputStream;
        }
        throw new Exception(
                "Zipped Binary can only handle byte[].class, InputStream, GZIPInputStream as response class. Not: " + responseClass);
    }

    public static final <T> T readJsonStream(Format format, InputStream inputStream, Class<T> responseClass) throws IOException {

        try {
            // handle zipped or plain
            InputStream is = format.isZipped() ? new GZIPInputStream(inputStream) : inputStream;

            // wait for the stream to become available, note: this may never happen
            for (int i = 0; i < INPUTSTREAM_ATTEMPTS; i++) {
                if (is.available() == 0) {
                    ThreadUtils.sleepInterruptible(INPUTSTREAM_SLEEP_MS);
                }
            }

            // connect anyway
            return JsonMapper.getLocalMapper(format).readValue(is, responseClass);

        } finally {
            // always close int the end
            inputStream.close();
        }
    }

    public static final <T> T readJsonString(Format format, String inputString, Class<T> responseClass) throws IOException {
        return JsonMapper.getLocalMapper(format).readValue(inputString, responseClass);
    }

    public static final <T> T readJsonURL(Format format, URL url, Class<T> responseClass) throws IOException {
        return JsonMapper.getLocalMapper(format).readValue(url, responseClass);
    }

    public static final <T> T readResponse(Response response, Class<T> responseClass, Format format) throws Exception {

        if (format == Format.GML) {

            String contentType = (String) response.getHeaders().getFirst("Content-Type");
            if (isSupportedGML(contentType)) {
                // (Frank) Override that header, because it is (probably) invalid
                // https://www.w3.org/Protocols/rfc1341/4_Content-Type.html
                response.getHeaders().get("Content-Type").set(0, Format.GML.getMediaType());
            }
        }

        // get the data streamed
        int bufferSize = ObjectUtils.isPrimitive(responseClass) ? PRIMITIVE_SIZE : OBJECT_SIZE;
        InputStream inputStream = new BufferedInputStream(response.readEntity(InputStream.class), bufferSize);
        return switch (format) {
            case GML, XML -> readXML(inputStream, responseClass);
            case JSON, TJSON, SMILE, TSMILE, ZIPJSON, ZIPTJSON, ZIPSMILE, ZIPTSMILE -> readJsonStream(format, inputStream, responseClass);
            case BINARY -> readByteStream(inputStream, responseClass, false);
            case ZIPBINARY -> readByteStream(inputStream, responseClass, true);
            default -> throw new UnsupportedOperationException("Format: " + format + " is not implemented!");
        };
    }

    @SuppressWarnings("unchecked")
    public static final <T> T readXML(InputStream inputStream, Class<T> responseClass) throws Exception {

        if (responseClass != Element.class) {
            throw new Exception("XML can only handle " + Element.class.getName() + " as response class. Not: " + responseClass);
        }

        final SAXBuilder builder = new SAXBuilder();
        final Element rootElement = builder.build(inputStream).getRootElement();
        inputStream.close();
        // XML is always Element class
        return (T) rootElement;
    }

    private static final void streamJson(OutputStream os, Object result, boolean zipped, ObjectMapper mapper) throws IOException {

        if (zipped) {
            os = new GZIPOutputStream(os) {
                {
                    def.setLevel(ZipUtils.DEFAULT_COMPRESSION);
                }
            };
        }
        mapper.writeValue(os, result);
        os.close();
    }

    /**
     * Stream data without CRS Mapper
     */
    public static final void streamObject(OutputStream os, Object object, Format format) throws IOException {
        streamObject(os, object, format, JsonMapper.getLocalMapper(format));
    }

    /**
     * Stream data with CRS Mapper (coordinates converted)
     */
    public static final void streamObject(OutputStream os, Object object, Format format, ObjectMapper mapper) throws IOException {

        switch (format) {
            case HTML:
                String value = object == null ? StringUtils.EMPTY : object.toString();
                os.write(StringUtils.getBytes(value));
                break;
            case JSON:
            case TJSON:
            case SMILE:
            case TSMILE:
            case ZIPJSON:
            case ZIPTJSON:
            case ZIPSMILE:
            case ZIPTSMILE:
                streamJson(os, object, format.isZipped(), mapper);
                break;
            case BINARY:
                if (object instanceof byte[] array) {
                    os.write(array);
                } else {
                    // convert java object to byte array
                    os.write(ZipUtils.compressToJavaObjectByteArray(object, false));
                }
                break;
            case ZIPBINARY:
            default:
                throw new TWebApplicationException(TStatus.BAD_REQUEST, "Format is not allowed");
        }
    }

    public static final byte[] writeJsonBytes(Format format, Object object) throws IOException {
        return writeJsonBytes(object, JsonMapper.getLocalMapper(format), format.isZipped());
    }

    private static final byte[] writeJsonBytes(Object result, ObjectMapper mapper, boolean zipped) throws IOException {

        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        OutputStream os = fos;
        if (zipped) {
            os = new GZIPOutputStream(fos) {
                {
                    def.setLevel(ZipUtils.DEFAULT_COMPRESSION);
                }
            };
        }
        mapper.writeValue(os, result);
        os.flush();
        os.close();
        fos.close();
        return fos.toByteArray();
    }

    public static final String writeJsonString(Format format, Object object) throws IOException {
        return writeJsonString(object, JsonMapper.getLocalMapper(format));
    }

    private static final String writeJsonString(Object object, ObjectMapper mapper) throws IOException {
        return mapper.writeValueAsString(object);
    }

    public static final Object writeObject(Object object, Format format) throws IOException {
        return writeObject(object, format, JsonMapper.getLocalMapper(format));
    }

    public static final Object writeObject(Object object, Format format, ObjectMapper mapper) throws IOException {

        switch (format) {
            case HTML:
                return StringUtils.arrayToHumanString(object, StringUtils.EMPTY);
            case JSON:
            case TJSON:
                return writeJsonString(object, mapper);
            case SMILE:
            case TSMILE:
            case ZIPJSON:
            case ZIPTJSON:
            case ZIPSMILE:
            case ZIPTSMILE:
                return writeJsonBytes(object, mapper, format.isZipped());
            case BINARY:
                if (object instanceof byte[]) {
                    // return as byte array
                    return object;
                } else {
                    // convert java object to byte array
                    return ZipUtils.compressToJavaObjectByteArray(object, false);
                }
            case ZIPBINARY:
            default:
                throw new TWebApplicationException(TStatus.BAD_REQUEST, "Format is not allowed.");
        }
    }
}
