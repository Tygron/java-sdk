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
package nl.tytech.data.editor.item;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.data.editor.serializable.GeoLinkType;
import nl.tytech.data.editor.serializable.GeometryMode;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Custom geo linkage
 *
 * @author Frank Baars
 */
public abstract class CustomGeoLink extends GeoLink implements NamedItem {

    private static final long serialVersionUID = -1303536489225706491L;

    public static final double DEFAULT_POINT_BUFFER = 0.5;
    public static final double DEFAULT_LINE_BUFFER = 0.5;

    public static boolean hasValue(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    @XMLValue
    @Deprecated(since = "2025.4: replaced by matchings")
    private TreeMap<String, String[]> attributes = null;

    @JsonIgnore
    @XMLValue
    @Deprecated(since = "2025.4: replaced by mappings")
    private TreeMap<String, String> mapping = null;

    @XMLValue
    private TreeMap<String, String[]> matchings = new TreeMap<>();

    @XMLValue
    private TreeMap<String, double[]> additionals = new TreeMap<>();

    @XMLValue
    private TreeMap<String, String> mappings = new TreeMap<>();

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private Double pointBuffer = null;

    @XMLValue
    private Double lineBuffer = null;

    @XMLValue
    private GeometryMode geomMode = GeometryMode.NORMAL;

    public double[] getAdditional(String attribute) {
        return additionals.get(attribute);
    }

    public Set<? extends String> getAdditionalKeys() {
        return additionals.keySet();
    }

    public abstract GeoLinkType getGeoLinkType();

    public GeometryMode getGeometryMode() {
        return geomMode;
    }

    private int getIndex(String[] values, String value) {

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public double getLineBuffer() {
        return this.lineBuffer == null ? DEFAULT_LINE_BUFFER : lineBuffer.doubleValue();
    }

    public String getMapping(String attribute) {
        return mappings.get(attribute);
    }

    public Set<String> getMappingKeys() {
        return mappings.keySet();
    }

    public String getMappingOrDefault(String attribute) {
        String mapped = getMapping(attribute);
        return StringUtils.containsData(mapped) ? mapped : attribute;
    }

    public String[] getMatching(String attribute) {
        return matchings.get(attribute);
    }

    public Set<String> getMatchingKeys() {
        return matchings.keySet();
    }

    @Override
    public String getName() {
        return name;
    }

    public double getPointBuffer() {
        return this.pointBuffer == null ? DEFAULT_POINT_BUFFER : pointBuffer.doubleValue();
    }

    public boolean removeAdditional(String attribute) {
        return additionals.remove(attribute) != null;
    }

    public boolean removeMapping(String attribute) {
        return mappings.remove(attribute) != null;
    }

    public boolean removeMatching(String attribute) {
        return matchings.remove(attribute) != null;
    }

    public boolean removeMatchingValue(String attribute, String value) {

        String[] values = matchings.get(attribute);
        if (values == null) {
            return false;
        }
        int index = getIndex(values, value);
        if (index < 0) {
            return false;
        }
        String[] result = new String[values.length - 1];
        for (int i = 0; i < index; i++) {
            result[i] = values[i];
        }

        for (int i = index + 1; i < values.length; i++) {
            result[i - 1] = values[i];
        }
        matchings.put(attribute, result);

        return true;
    }

    public boolean setAdditional(String attribute, double[] values) {

        double[] old = additionals.get(attribute);
        if (values != null && Arrays.equals(values, old)) {
            return false;
        }
        additionals.put(attribute, values);
        return true;
    }

    public void setGeometryMode(GeometryMode geomMode) {
        this.geomMode = geomMode;
    }

    public void setLineBuffer(double lineBuffer) {
        this.lineBuffer = lineBuffer;
    }

    public void setMapping(String attribute, String value) {
        mappings.put(attribute, value);
    }

    public boolean setMatching(String attribute, String[] values) {

        String[] old = matchings.get(attribute);
        if (values != null && Arrays.equals(values, old)) {
            return false;
        }
        matchings.put(attribute, values);
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPointBuffer(double pointBuffer) {
        this.pointBuffer = pointBuffer;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (attributes != null && matchings.isEmpty()) {
            matchings = attributes;
            attributes = null;
            if (matchings.size() > 0) {
                TLogger.warning(getName() + ": converted " + matchings.size() + " attributes to matchings.");
            }
        }
        if (mapping != null && mappings.isEmpty()) {
            mappings = mapping;
            mapping = null;
            if (mappings.size() > 0) {
                TLogger.warning(getName() + ": converted " + mappings.size() + " mapping to mappings.");
            }
        }
        return result;
    }
}
