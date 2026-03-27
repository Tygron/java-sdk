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
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.GeoTiffOverlay.GeoTiffResult;
import nl.tytech.data.engine.item.GridOverlay.NoPrequel;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * Uploaded Geo Tiff file grid
 *
 * @author Maxim Knepfle
 */
public class GeoTiffOverlay extends ResultParentOverlay<GeoTiffResult, NoPrequel> {

    public enum GeoTiffAttribute implements ReservedAttribute {

        /**
         * Band to load from GeoTIFF
         */
        BAND(Integer.class, 0);

        private final double[] defaultValues;
        private final Class<?> type;

        private GeoTiffAttribute(Class<?> type, double... defaultValue) {
            this.type = type;
            this.defaultValues = defaultValue;
        }

        @Override
        public double[] defaultArray() {
            return defaultValues;
        }

        @Override
        public double defaultValue() {
            return defaultValues[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    public enum GeoTiffResult implements ResultType {

        NEAREST,

        INTERPOLATED,

        ;

        private GeoTiffResult() {
        }

        @Override
        public byte getIndex() {
            return (byte) ordinal();
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public static final int MAX_BANDS = 4;

    private static final long serialVersionUID = -2572949484529750429L;

    @XMLValue
    @ItemIDField(MapLink.GEO_TIFFS)
    private ArrayList<Integer> geoTiffIDs = new ArrayList<>();

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return MathUtils.clamp(geoTiffIDs.size(), 1, getMaxTimeFrames());
    }

    @Override
    protected GeoTiffResult getDefaultResult() {
        return GeoTiffResult.NEAREST;
    }

    public final List<String> getGeoTiffFileNames(Integer geoTiffID) {
        List<GeoTiff> geoTiffs = getGeoTiffs();
        return geoTiffs.stream().map(g -> g.getFileName()).collect(Collectors.toList());
    }

    public List<Integer> getGeoTiffIDs() {
        return geoTiffIDs;
    }

    public List<GeoTiff> getGeoTiffs() {
        return getItems(MapLink.GEO_TIFFS, getGeoTiffIDs());
    }

    @Override
    public NoPrequel[] getPrequelTypes() {
        return NoPrequel.VALUES;
    }

    @Override
    protected Class<GeoTiffResult> getResultClass() {
        return GeoTiffResult.class;
    }

    public boolean hasGeoTiffID(Integer tiffID) {
        return geoTiffIDs.contains(tiffID);
    }

    public void setGeoTiffIDs(Integer... tiffIDs) {

        geoTiffIDs.clear();
        for (Integer tiffID : tiffIDs) {
            if (!hasGeoTiffID(tiffID)) {
                geoTiffIDs.add(tiffID);
            }
        }
    }
}
