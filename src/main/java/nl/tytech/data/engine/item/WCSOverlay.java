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

import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.data.engine.item.GridOverlay.NoPrequel;
import nl.tytech.data.engine.item.WCSOverlay.WCSResult;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.util.StringUtils;

/**
 * WCS Service Overlay
 *
 * @author Frank Baars
 */
public class WCSOverlay extends ServiceOverlay<WCSResult, NoPrequel> {

    public enum WCSResult implements ResultType {

        NEAREST,

        INTERPOLATED,

        ;

        private WCSResult() {
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

    private static final long serialVersionUID = 4970590824751738679L;

    public static boolean isSupported(GeoFormat format) {
        return format == GeoFormat.WCS_TIFF;
    }

    public WCSOverlay() {

    }

    @Override
    protected WCSResult getDefaultResult() {
        return WCSResult.NEAREST;
    }

    @Override
    public NoPrequel[] getPrequelTypes() {
        return NoPrequel.VALUES;
    }

    @Override
    protected Class<WCSResult> getResultClass() {
        return WCSResult.class;
    }

    @Override
    public boolean supports(GeoFormat format) {
        return isSupported(format);
    }

    @Override
    public String validated(boolean startSession) {

        if (hasAttribute("INTERPOLATED")) { // convert attribute to resulttype
            this.setResultType(getAttribute("INTERPOLATED") > 0 ? WCSResult.INTERPOLATED : WCSResult.NEAREST);
            removeAttribute("INTERPOLATED");
        }
        return super.validated(startSession);
    }
}
