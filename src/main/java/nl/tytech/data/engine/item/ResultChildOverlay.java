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

import java.util.Map;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.StringUtils;

/**
 * Contains result grid calculated by parent overlay
 *
 * @author Maxim Knepfle
 */
public class ResultChildOverlay<R extends ResultType, P extends PrequelType> extends GridOverlay<R, P> {

    private static final long serialVersionUID = -2572949484529751429L;

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return getParent().getTimeframes(cache);
    }

    @Override
    public String getAbstract() {
        return StringUtils.capitalizeWithSpacedUnderScores(getParent().getType()) + ": " + getResultType();
    }

    @Override
    public long getCalcTimeMS() {
        return getParent().getCalcTimeMS();
    }

    @Override
    public String getDefaultImageName() {
        return getParent().getDefaultImageName(getResultType());
    }

    @Override
    protected R getDefaultResult() {
        return getParent().getDefaultResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultParentOverlay<R, P> getParent() {
        return (ResultParentOverlay<R, P>) super.getParent();
    }

    @Override
    public P[] getPrequelTypes() {
        return getParent().getPrequelTypes();
    }

    @Override
    public Integer getRelationID(Relation relation) {
        return relation == Relation.RESULT_PARENT ? getParentID() : super.getRelationID(relation);
    }

    @Override
    public Class<R> getResultClass() {
        return getParent().getResultClass();
    }

    @Override
    public String getTimeframeText(int timeframe, String format) {
        return getParent().getTimeframeText(timeframe, format);
    }

    @Override
    public long getTimeframeTimeSec(int timeframe) {
        return getParent().getTimeframeTimeSec(timeframe);
    }
}
