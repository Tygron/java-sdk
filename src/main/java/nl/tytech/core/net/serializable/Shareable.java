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

import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;
import nl.tytech.core.net.Rest;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.naming.GeoNC;
import nl.tytech.util.FileUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.TMediaType;

/**
 * Share definition class
 * @author Maxim Knepfle
 *
 */
public class Shareable implements Serializable, Comparable<Shareable> {

    public enum Type {

        GEOTIFF(TMediaType.IMAGE_TIFF, GeoFormat.GEOTIFF, "tiff", "tif"),

        GEOJSON(TMediaType.APPLICATION_JSON, GeoFormat.GEOJSON, "geojson", "json"),

        GEOPACKAGE(TMediaType.APPLICATION_OCTET_STREAM, GeoFormat.GEOPACKAGE, "gpkg"),

        CITYJSON(TMediaType.APPLICATION_JSON, "cityjson", "json"),

        CITYGML(TMediaType.APPLICATION_XML, "gml", "xml"),

        SLPK(TMediaType.APPLICATION_OCTET_STREAM, "slpk"),

        IFC(TMediaType.TEXT_PLAIN_UTF8, "ifc"),

        PNG(TMediaType.IMAGE_PNG, "png"),

        GIF(TMediaType.IMAGE_GIF, "gif"),

        JPEG(TMediaType.IMAGE_JPEG, "jpg", "jpeg"),

        CSV(TMediaType.TEXT_CSV, "csv"),

        EXCEL(TMediaType.APPLICATION_XLSX, "xlsx"),

        TXT(TMediaType.TEXT_PLAIN_UTF8, "txt"),

        DOC(TMediaType.APPLICATION_DOCX, "docx"),

        SLIDES(TMediaType.APPLICATION_PPTX, "pptx"),

        PDF(TMediaType.APPLICATION_PDF, "pdf"),

        VIEWER(TMediaType.TEXT_HTML, new String[0]),

        DIRECTORY(TMediaType.TEXT_HTML, new String[0]),

        DXF(TMediaType.TEXT_PLAIN_UTF8, "dxf"),

        ONNX(TMediaType.APPLICATION_OCTET_STREAM, "onnx"),

        ZIP(TMediaType.APPLICATION_ZIP, "zip");

        public static Type get(File file) {
            return file.isDirectory() ? Type.DIRECTORY : get(file.getName());
        }

        public static Type get(String fileName) {

            // check extension
            if (fileName.contains(".")) {
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                for (Type type : Type.values()) {
                    for (String extensio : type.extensions) {
                        if (extensio.equalsIgnoreCase(extension)) {
                            return type;
                        }
                    }
                }
            }
            return null;
        }

        private final String[] starExtensions;
        private final String[] extensions;
        private final String mediaType;
        private final GeoFormat geoFormat;

        private Type(String mediaType, GeoFormat geoFormat, String... extension) {
            this.extensions = extension;
            this.mediaType = mediaType;
            this.geoFormat = geoFormat;
            this.starExtensions = new String[extension.length];
            for (int i = 0; i < extensions.length; i++) {
                starExtensions[i] = "*." + extensions[i];
            }
        }

        private Type(String mediaType, String... extension) {
            this(mediaType, (GeoFormat) null, extension);
        }

        public String getExtension() {
            return extensions[0];
        }

        public String[] getExtensions(boolean star) {
            return star ? starExtensions : extensions;
        }

        public GeoFormat getGeoFormat() {
            return geoFormat;
        }

        public String getMediaType() {
            return mediaType;
        }

        public boolean hasExtensions() {
            return extensions != null && extensions.length > 0;
        }

        public final boolean isCertifiable() {
            return this == EXCEL;
        }

        public boolean isImage() {
            return this == GIF || this == JPEG || this == PNG;
        }

        public final boolean isZipStore() {
            // geotiff cannot be zipped due to reading issues when streaming
            // geopackage is needed for stream contents from disk
            // zip not usefull
            return this != Type.GEOTIFF && this != Type.GEOPACKAGE && this != Type.ZIP;
        }

        @Override
        public String toString() {

            return switch (this) {
                case GEOJSON -> GeoNC.GEOJSON;
                case GEOTIFF -> GeoNC.GEOTIFF;
                case GEOPACKAGE -> GeoNC.GEOPACKAGE;
                default -> name().length() <= 4 ? name() : StringUtils.capitalizeWithSpacedUnderScores(this);
            };
        }
    }

    public static final Pattern REGEX_MULTI = Pattern.compile("(.+)-(\\d).(.+)");

    private static final long serialVersionUID = 4948926945696086792L;

    public static final String getFrameName(String fileName, int timeframe) {

        if (Shareable.REGEX_MULTI.matcher(fileName).matches()) {
            String first = fileName.substring(0, fileName.lastIndexOf("-") + 1);
            String last = fileName.substring(fileName.lastIndexOf("."));
            String frame = Integer.toString(timeframe);
            while (frame.length() < fileName.length() - first.length() - last.length()) {
                frame = "0" + frame; // add more leading zeros
            }
            return first + frame + last;
        }
        return null; // not a frame name
    }

    public static final String getParentDirectory(String path) {

        String parent = "";
        String[] dirs = toValidDirectory(path).split("\\/");
        for (int i = 0; i < dirs.length - 1; i++) {
            parent += dirs[i] + "/";
        }
        return parent;
    }

    public static final String getPath(Type type, String directory, String fileName) {
        return directory + fileName + (type.hasExtensions() ? "" : "/");
    }

    public static final String getSimpleName(String path) {

        String fileName = path.lastIndexOf("/") < 0 ? path : path.substring(path.lastIndexOf("/") + 1, path.length());
        if (isFirstFrame(fileName)) {
            return fileName.substring(0, fileName.lastIndexOf("-"));
        } else {
            return FileUtils.getWithoutExtension(fileName);
        }
    }

    public static final boolean isFirstFrame(String fileName) {
        return fileName.equals(getFrameName(fileName, 0)); // when start with zero
    }

    public static final String toValidDirectory(String name) {

        if (name == null) {
            return StringUtils.EMPTY;
        }

        String valid = name.toLowerCase().trim();
        valid = valid.replaceAll("[^\\/a-z0-9_-]+", "_"); // replace funcky chars
        valid = valid.replaceAll("(\\/)+", "/"); // cleanup multiple slashes

        if (valid.startsWith("/")) {
            valid = valid.substring(1, valid.length());
        }
        if (valid.length() > 0 && !valid.endsWith("/")) {
            valid = valid + "/";
        }
        return valid;
    }

    private String token = StringUtils.randomToken();

    private String directory = StringUtils.EMPTY;

    private String fileName = StringUtils.EMPTY;

    private String owner = StringUtils.EMPTY;

    private String description = StringUtils.EMPTY;

    private String disclaimer = StringUtils.EMPTY;

    private String certificateAuthor = StringUtils.EMPTY;

    private Type type;

    private long byteSize = 0;

    private String domain = StringUtils.EMPTY;

    private String iconName = StringUtils.EMPTY;

    public Shareable() {

    }

    public Shareable(String directory, String name, String owner, String domain, Type type) {
        this.directory = directory;
        this.fileName = name;
        this.owner = owner;
        this.domain = domain;
        this.type = type;
    }

    @Override
    public int compareTo(Shareable other) {

        // directories go before files
        if (this.isDir() && !other.isDir()) {
            return -1;
        } else if (!this.isDir() && other.isDir()) {
            return 1;
        } else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.getFileName(), other.getFileName());
        }
    }

    public long getByteSize() {
        return byteSize;
    }

    public String getCertificateAuthor() {
        return certificateAuthor;
    }

    public String getDescription() {
        return description;
    }

    public String getDirectory() {
        return directory;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getDomain() {
        return domain;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFrameName(int timeframe) {
        return getFrameName(fileName, timeframe);
    }

    public String getIconName() {
        return iconName;
    }

    public String getOwner() {
        return owner;
    }

    public String getPath() {
        return Shareable.getPath(getType(), getDirectory(), getFileName());
    }

    public String getPathURL() {
        return SettingsManager.getShareWebAddress() + Rest.SHARE + getDomain() + "/" + getPath();
    }

    public String getToken() {
        return token;
    }

    public Type getType() {
        return type;
    }

    public String getURL() {
        return getURL("");
    }

    public String getURL(String subdir) {
        return getPathURL() + subdir + (hasToken() ? "?token=" + token : "");
    }

    public boolean hasToken() {
        return StringUtils.containsData(token);
    }

    public boolean isDir() {
        return type == Type.DIRECTORY;
    }

    public boolean isFirstFrame() {
        return isFirstFrame(fileName);
    }

    public boolean isViewable() {
        return type == Type.GEOJSON || type == Type.GEOPACKAGE || type == Type.GEOTIFF;
    }

    public boolean isViewer() {
        return type == Type.VIEWER;
    }

    public boolean isWritetable(User user) {
        return user.getMaxAccessLevel().ordinal() >= AccessLevel.DOMAIN_ADMIN.ordinal() || user.getUserName().equals(this.owner);
    }

    public void setByteSize(long byteSize) {
        this.byteSize = byteSize;
    }

    public void setCertificateAuthor(String author) {
        this.certificateAuthor = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getDomain() + " -> " + getPath();
    }
}
