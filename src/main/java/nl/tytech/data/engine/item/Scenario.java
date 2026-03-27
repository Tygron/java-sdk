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
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.item.annotations.EventList;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * Scenario activates certain functionality, e.g. more zones to work in. New measures.
 *
 * @author Maxim Knepfle
 *
 */
public class Scenario extends AttributeItem {

    public enum LimitType {

        DEFAULT, NEIGHBORHOODS, MEASURE_NEIGHBORHOODS;

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum ScenarioAttribute implements ReservedAttribute {

        LIMIT_MARGIN(Double.class),

        INDICATORS(Double.class);

        public static final ScenarioAttribute[] VALUES = ScenarioAttribute.values();

        private final Class<?> type;

        private ScenarioAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return switch (this) {
                case LIMIT_MARGIN -> new double[] { 500.0 };
                default -> AttributeItem.ZERO;
            };
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

    public static final double SCORE_MARGIN = 1.0 / 2000.0; // half promille margin

    private static final long serialVersionUID = -1507601543672853064L;

    @XMLValue
    private LimitType limitType = LimitType.DEFAULT;

    @XMLValue
    @ItemIDField(MapLink.NEIGHBORHOODS)
    private final ArrayList<Integer> limitNeighborhoodIDs = new ArrayList<>();

    @XMLValue
    @EventList(serverSide = true)
    @ListOfClass(CodedEvent.class)
    private ArrayList<CodedEvent> events = new ArrayList<>();

    @XMLValue
    @NoDefaultText
    private String description = StringUtils.EMPTY;

    @XMLValue
    private boolean restartTestrun = true;

    @JsonIgnore
    private volatile boolean testrunActive = false;

    public Scenario() {

    }

    public CodedEvent addEvent(EventTypeEnum type, Object... objects) {

        CodedEvent event = CodedEvent.createUniqueIDEvent(events, type, objects);
        events.add(event);
        return event;
    }

    public CodedEvent getCodedEventForID(Integer subID) {

        for (CodedEvent codedEvent : events) {
            if (codedEvent.getID().equals(subID)) {
                return codedEvent;
            }
        }
        return null;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return ScenarioAttribute.VALUES;
    }

    public String getDescription() {
        return description;
    }

    public List<CodedEvent> getEvents() {
        return events;
    }

    public final MultiPolygon getLimitMap() {

        if (getLimitType() == LimitType.DEFAULT) {
            return null;
        }
        Envelope total = new Envelope();
        double margin = getLimitMargin();
        for (Neighborhood hood : getLimitNeighborhoods()) {
            total.expandToInclude(JTSUtils.bufferSimple(hood.getMultiPolygon(), margin).getEnvelopeInternal());
        }
        return JTSUtils.createRectangle(total);
    }

    public final double getLimitMargin() {
        return MathUtils.clamp(getOrDefault(ScenarioAttribute.LIMIT_MARGIN), 1_000_000);
    }

    public final List<Integer> getLimitNeighborhoodIDs() {
        return limitNeighborhoodIDs;
    }

    public final List<Neighborhood> getLimitNeighborhoods() {
        return getItems(MapLink.NEIGHBORHOODS, getLimitNeighborhoodIDs());
    }

    public LimitType getLimitType() {
        return limitType;
    }

    public final List<Integer> getMeasureIDs() {

        List<Integer> measureIDs = new ArrayList<>();
        for (CodedEvent serverEvent : getEvents()) {
            if (serverEvent.getType() == ParticipantEventType.MEASURE_PLAN_CONSTRUCTION) {
                measureIDs.add(((ItemID) serverEvent.getParameters().get(2)).getID());
            }
        }
        return measureIDs;
    }

    public final double getScoreImprovement() {

        double[] scores = getAttributeArray(ScenarioAttribute.INDICATORS);
        ItemMap<Indicator> indicators = this.<Indicator> getMap(MapLink.INDICATORS);

        int count = 0;
        double improvement = 0;
        for (int i = 0; i < scores.length - 1; i += 2) {
            Indicator indicator = indicators.get((int) scores[i]);
            if (indicator != null && indicator.isActive()) {
                improvement += scores[i + 1] - indicator.getValue(MapType.CURRENT);
                count++;
            }
        }
        return count > 0 ? improvement / count : 0;
    }

    public final String getWarnings() {

        if (limitType == LimitType.NEIGHBORHOODS && getLimitNeighborhoodIDs().isEmpty()) {
            return "No Neighborhoods selected!";
        }
        if (limitType == LimitType.MEASURE_NEIGHBORHOODS) {
            if (getMeasureIDs().isEmpty()) {
                return "No Measures selected!";
            } else if (getLimitNeighborhoodIDs().isEmpty()) {
                return "No Measure Neighborhood overlap!";
            }
        }
        return StringUtils.EMPTY;
    }

    public boolean isActivated() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        return setting.getIntegerValue().equals(this.getID());
    }

    public boolean isRestartTestrun() {
        return restartTestrun;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLimitType(LimitType limitType) {
        this.limitType = limitType;
    }

    public void setRestartTestrun(boolean restartTestrun) {
        this.restartTestrun = restartTestrun;
    }

    public void setTestrunActive(boolean testrunActivated) {
        this.testrunActive = testrunActivated;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String validated(boolean startNewSession) {
        return EventValidationUtils.validateCodedEvents(this, events, true);
    }

    public boolean wasTestrunActive() {
        return testrunActive;
    }
}
