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
import java.util.Collection;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Answer;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.PopupModelType;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * @author Frank Baars & Maxim Knepfle
 */
public abstract class Panel extends AttributeItem {

    public enum PanelAttribute implements ReservedAttribute {

        VISIBLE(Boolean.class, 1),

        REFRESH(Boolean.class, 1),

        VISIBLE_TIMEFRAME(Integer.class, 0),

        ATTENTION(Boolean.class, 0),

        SCALE(Double.class, 1),

        POPUP_TYPE(Integer.class, 0),

        ;

        private final Class<?> type;
        private final double[] defaultArray;

        private PanelAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    public enum PanelType {

        GLOBAL_PANEL,

        EXCEL_PANEL,

        WEB_PANEL,

        TEXT_PANEL,

        TEMPLATE_EXCEL_PANEL,

        TEMPLATE_TEXT_PANEL,

        // TODO: add EDITOR_PANEL

        ;

        public static final PanelType[] VALUES = PanelType.values();

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    private static final long serialVersionUID = 7679659744033436741L;

    public static final double MAX_WIDTH = 1000;
    public static final double MAX_HEIGHT = 4000;

    public static final double MIN_WIDTH = 30;
    public static final double MIN_HEIGHT = 30;

    private static final double DEFAULT_WIDTH = 600;
    private static final double DEFAULT_HEIGHT = 400;

    @XMLValue
    protected Double width = null;

    @XMLValue
    protected Double height = null;

    @XMLValue
    protected Double layoutX = null;

    @XMLValue
    protected Double layoutY = null;

    @XMLValue
    private Point point = null;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer stakeholderID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.OVERLAYS)
    private Integer overlayID = Item.NONE;

    @XMLValue
    @ListOfClass(Answer.class)
    private ArrayList<Answer> answers = new ArrayList<>();

    @XMLValue
    private Integer overrideDefaultAnswerID = Item.NONE;

    protected final PanelType type;

    protected Panel(PanelType type) {
        this.type = type;
    }

    public final void addAnswer(Answer newAnswer) {

        int highestID = Item.NONE;
        for (Answer answer : this.answers) {
            if (answer.getID().intValue() >= highestID) {
                highestID = answer.getID();
            }
        }
        newAnswer.setID(Integer.valueOf(highestID + 1));
        this.answers.add(newAnswer);
    }

    public final Collection<Answer> getAnswers() {
        return this.answers;
    }

    public final Answer getAnswerWithID(Integer answerID) {
        for (Answer answer : this.answers) {
            if (answer.getID().equals(answerID)) {
                return answer;
            }
        }
        return null;
    }

    public final Answer getAnswerWithListIndex(int index) {
        if (index >= 0 && index < this.answers.size()) {
            return this.answers.get(index);
        }
        return null;
    }

    public long getCalcTimeMS() {
        return 0l;
    }

    @Override
    public TColor getColor() {

        if (this.hasAttribute(BaseAttribute.COLOR)) {
            return super.getColor();
        } else {
            return this.getPopupModelType().getDefaultColor();
        }
    }

    public Answer getDefaultAnswer() {

        Answer answer = this.getAnswerWithID(this.overrideDefaultAnswerID);
        if (answer != null) {
            return answer;
        }
        return answers.size() > 0 ? answers.get(0) : null;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return PanelAttribute.values();
    }

    public double getHeight() {
        return height == null ? DEFAULT_HEIGHT : height.doubleValue();
    }

    public double getLayoutX() {
        return layoutX == null ? Item.NONE : layoutX.doubleValue();
    }

    public double getLayoutY() {
        return layoutY == null ? Item.NONE : layoutY.doubleValue();
    }

    public Overlay getOverlay() {
        return this.getItem(MapLink.OVERLAYS, getOverlayID());
    }

    public Integer getOverlayID() {
        return overlayID;
    }

    public Point getPoint() {
        return point;
    }

    public PopupModelType getPopupModelType() {

        int ordinal = (int) getOrDefault(PanelAttribute.POPUP_TYPE);
        if (ordinal >= 0 && ordinal < PopupModelType.VALUES.length) {
            return PopupModelType.VALUES[ordinal];
        }
        // fallback to first
        return PopupModelType.VALUES[0];
    }

    @Override
    public Integer getRelationID(Relation relation) {
        return relation == Relation.OWNER ? getStakeholderID() : Item.NONE;
    }

    public final double getScale() {
        return MathUtils.clamp(getOrDefault(PanelAttribute.SCALE), PopupData.MIN_SCALE, PopupData.MAX_SCALE);
    }

    public final Stakeholder getStakeholder() {
        return getItem(MapLink.STAKEHOLDERS, stakeholderID);
    }

    public Integer getStakeholderID() {
        return stakeholderID;
    }

    public final PanelType getType() {
        return type;
    }

    public int getVisibleTimeframe() {
        return (int) getOrDefault(PanelAttribute.VISIBLE_TIMEFRAME);
    }

    public double getWidth() {
        return width == null ? DEFAULT_WIDTH : width.doubleValue();
    }

    public final boolean isAttention() {
        return getOrDefault(PanelAttribute.ATTENTION) > 0;
    }

    /**
     * Excel and text panel are calculated in the simulation
     */
    public final boolean isCalculated() {
        return getType() == PanelType.TEXT_PANEL || getType() == PanelType.EXCEL_PANEL;
    }

    public final boolean isRefresh() {
        return getOrDefault(PanelAttribute.REFRESH) > 0;
    }

    public final boolean isVisible() {
        return getOrDefault(PanelAttribute.VISIBLE) > 0;
    }

    public final boolean removeAnswerWithID(Integer answerID) {

        for (Answer answer : this.answers) {
            if (answer.getID().equals(answerID)) {
                return this.answers.remove(answer);
            }
        }
        return false;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
    }

    public void setModelType(PopupModelType modelType) {
        setAttribute(PanelAttribute.POPUP_TYPE, modelType.ordinal());
    }

    public void setOverlayID(Integer overlayID) {
        this.overlayID = overlayID;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setStakeholderID(Integer stakeholderID) {
        this.stakeholderID = stakeholderID;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}
