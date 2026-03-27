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

import nl.tytech.data.engine.item.DefaultRasterizationOverlay.RasterizationPrequel;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;

/**
 * Rasterize specific maplinks
 *
 * @author Frank Baars & Maxim Knepfle
 */
public abstract class DefaultRasterizationOverlay<R extends ResultType> extends RasterizationOverlay<R, RasterizationPrequel> {

    public enum RasterizationPrequel implements PrequelType {

        INPUT;

        public static final RasterizationPrequel[] VALUES = RasterizationPrequel.values();
    }

    private static final long serialVersionUID = -2874817941867350408L;

    @Override
    protected final boolean calcPrequelTimeframes() {
        return isActive() && getRasterization() == Rasterization.GRID && hasPrequel(RasterizationPrequel.INPUT);
    }

    @Override
    public final boolean calcSelfPrequel() {
        return false; // due to possible Prequel INPUT getTimeframes() loop
    }

    @Override
    public RasterizationPrequel[] getPrequelTypes() {
        return RasterizationPrequel.VALUES;
    }
}
