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

import java.util.Collections;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * Global variable
 *
 * @author Maxim Knepfle
 */
public class Global extends UniqueNamedItem implements GeometryItem<Point> {

    public static final double DEFAULT_VALUE = 0.0;

    public static final double[] DEFAULT = new double[] { DEFAULT_VALUE };

    private static final long serialVersionUID = 1362611699416604469L;

    /**
     * Default amount of global decimals
     */
    public static final int DECIMALS = 10;

    @XMLValue
    private double[] actualValue = DEFAULT;

    @XMLValue
    private double[] previousValue = DEFAULT;

    @XMLValue
    private double[] startValue = DEFAULT;

    @XMLValue
    private Point point = null;

    @XMLValue
    private String visualisationName = StringUtils.EMPTY;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer bookValueStakeholderID = Item.NONE;

    @XMLValue
    private String query = StringUtils.EMPTY;

    @XMLValue
    private long calcTimeMS = 0;

    @XMLValue
    private Timing queryTiming = Timing.AFTER;

    public Global() {

    }

    public Global(double value) {
        setActualValue(new double[] { value });
    }

    public double[] getActualValue() {
        return actualValue;
    }

    /**
     * Return value at index or last valid index value
     */
    public double getActualValue(int index) {
        return actualValue[MathUtils.clamp(index, 0, actualValue.length - 1)];
    }

    public Stakeholder getBookValueStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, getBookValueStakeholderID());
    }

    public Integer getBookValueStakeholderID() {
        return bookValueStakeholderID;
    }

    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    @Override
    public Point getCenterPoint() {
        return point;
    }

    public String getFullName() {
        return this.getName() + (isQuery() ? " ( " + query + " )" : StringUtils.EMPTY);
    }

    public Point getPoint() {
        return point;
    }

    public double[] getPreviousValue() {
        return previousValue;
    }

    @Override
    public Point[] getQTGeometries() {
        return new Point[] { point };
    }

    public String getQuery() {
        return query;
    }

    public Timing getQueryTiming() {
        return queryTiming;
    }

    @Override
    public List<Source> getSources() {
        return Collections.emptyList();
    }

    public double[] getStartValue() {
        return startValue;
    }

    public String getVisualisationName() {
        if (StringUtils.containsData(this.visualisationName)) {
            return visualisationName;
        }
        return getName();
    }

    public boolean isBookValue() {
        return !Item.NONE.equals(bookValueStakeholderID);
    }

    public boolean isQuery() {
        return StringUtils.containsData(query);
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(point);
    }

    public void setActualValue(double[] newActualValue) {
        this.previousValue = this.actualValue; // move current to previous
        this.actualValue = newActualValue;
    }

    public void setBookValueStakeholderID(Integer stakeholderID) {
        this.bookValueStakeholderID = stakeholderID;
    }

    public void setCalcTimeMS(long timeMS) {
        this.calcTimeMS = timeMS;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setQueryTiming(Timing timing) {
        this.queryTiming = timing;
    }

    public void setStartValue(double[] startValue) {
        this.startValue = startValue;
    }

    public void setVisualisationName(String visualisationName) {
        this.visualisationName = visualisationName;
    }
}
