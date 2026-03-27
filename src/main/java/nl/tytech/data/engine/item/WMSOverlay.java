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

import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.data.engine.item.GridOverlay.NoPrequel;
import nl.tytech.data.engine.item.WMSOverlay.WMSResult;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.StringUtils;

/**
 * WMS Service Overlay
 *
 * @author Frank Baars
 */
public class WMSOverlay extends ServiceOverlay<WMSResult, NoPrequel> {

    public enum WMSResult implements ResultType {

        COLOR, FLOAT32, RED, GREEN, BLUE, ALPHA;

        private WMSResult() {
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

            switch (this) {
                case FLOAT32:
                    return "Grid Value (float32)";
                case RED, GREEN, BLUE, ALPHA:
                    return StringUtils.capitalizeWithSpacedUnderScores(this) + " Channel";
                default:
                    return StringUtils.capitalizeWithSpacedUnderScores(this);
            }
        }
    }

    private static final long serialVersionUID = 4970590824751731679L;

    public static boolean isSupported(GeoFormat format) {
        return format == GeoFormat.WMS_IMAGE;
    }

    public WMSOverlay() {

    }

    @Override
    protected WMSResult getDefaultResult() {
        return WMSResult.COLOR;
    }

    @Override
    public NoPrequel[] getPrequelTypes() {
        return NoPrequel.VALUES;
    }

    @Override
    protected Class<WMSResult> getResultClass() {
        return WMSResult.class;
    }

    @Override
    public Source getSource() {
        return getItem(MapLink.SOURCES, getSourceID());
    }

    @Override
    public boolean supports(GeoFormat format) {
        return isSupported(format);
    }
}
