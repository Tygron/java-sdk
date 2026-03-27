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

import jakarta.ws.rs.core.MediaType;

/**
 * Additional Mime types
 *
 * @author Maxim Knepfle
 *
 */
public class TMediaType extends MediaType {

    /**
     * Default content type encoding for non binary formats -> UTF-8
     */
    public static final String CHARSET_UTF8 = "; charset=utf-8";

    /**
     * Mime Type for PNG images
     */
    public static final String IMAGE_PNG = "image/png";

    /**
     * Mime Type for JPEG images
     */
    public static final String IMAGE_JPEG = "image/jpeg";

    /**
     * Mime Type for animated GIF images
     */
    public static final String IMAGE_GIF = "image/gif";

    /**
     * Mime Type for GeoTIFF and TIFF images
     */
    public static final String IMAGE_TIFF = "image/tiff";

    /**
     * Mime Type for Autocad DXF image
     */
    public static final String IMAGE_DXF = "image/vnd.dxf";

    /**
     * Mime Type for glTF Model
     */
    public static final String MODEL_GLTF = "model/gltf+json";

    /**
     * Mime Type for ico images
     */
    public static final String IMAGE_ICO = "image/vnd.microsoft.icon";

    /**
     * Mime Type for Java Archives.
     */
    public static final String APPLICATION_JAR = "application/java-archive";

    /**
     * Mime Type for Zip Archives.
     */
    public static final String APPLICATION_ZIP = "application/zip";

    /**
     * Mime Type for CSV files.
     */
    public static final String TEXT_CSV = "text/csv";

    /**
     * Mime Type for CSS Style sheets.
     */
    public static final String TEXT_CSS = "text/css";

    /**
     * Mime Type for Excel XLSX files.
     */
    public static final String APPLICATION_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * Mime Type for Word DOCX files.
     */
    public static final String APPLICATION_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    /**
     * Mime Type for Presentation files.
     */
    public static final String APPLICATION_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    /**
     * Mime Type for PDF files.
     */
    public static final String APPLICATION_PDF = "application/pdf";

    /**
     * Mime Type for Java script libs in UTF-8 encoding.
     */
    public static final String APPLICATION_JAVASCRIPT_UTF8 = "application/javascript" + CHARSET_UTF8;

    /**
     * Mime Type for plain text in UTF-8 encoding.
     */
    public static final String TEXT_PLAIN_UTF8 = MediaType.TEXT_PLAIN + CHARSET_UTF8;

    /**
     * Mime Sup Type header
     */
    public static final String SUBTYPE = "subtype";

}
