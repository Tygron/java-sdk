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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.SourcedItem;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.other.TimeStateItem;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.StringUtils;

/**
 * Measure: This class keeps track of the base Measure.
 *
 * @author Maxim Knepfle
 */
sealed public class Measure extends SourcedItem implements Action, TimeStateItem, GeometryItem<MultiPolygon>, ImageItem permits MapMeasure {

    public enum ActionType {

        CONSTRUCTION_PLAN("On construction planned"),

        CONSTRUCTION_PLAN_CANCEL("On construction canceled");

        private String description;

        private ActionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }
    }

    public enum CostType {
        CONSTRUCTION
    }

    private static final long serialVersionUID = -2467220889999050219L;

    @XMLValue
    private HashMap<ActionType, List<CodedEvent>> clientActionEvents = new HashMap<>();

    @XMLValue
    private HashMap<ActionType, List<CodedEvent>> serverActionEvents = new HashMap<>();

    @XMLValue
    protected double constructionCostsFixed = Item.NONE;

    @DoNotSaveToInit
    @XMLValue
    private boolean custom = false;

    @XMLValue
    private double incomeFixed = 0;

    @ItemIDField(MapLink.MEASURES)
    @XMLValue
    private ArrayList<Integer> dependencyIDs = new ArrayList<>();

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @AssetDirectory(ACTION_IMAGE_LOCATION)
    private String imageName = "measure.png";

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @ItemIDField(MapLink.STAKEHOLDERS)
    @XMLValue
    private Integer ownerID = Item.NONE;

    @XMLValue
    private HashMap<Integer, Boolean> landOwnerPermissions = new HashMap<>();

    @DoNotSaveToInit
    @XMLValue
    private TimeState state = TimeState.NOTHING;

    @XMLValue
    private boolean confirmationsRequired = false;

    public Measure() {

    }

    public CodedEvent addEvent(ActionType actionType, EventTypeEnum type, Object... objects) {
        boolean isServerSide = type.isServerSide();
        CodedEvent event = CodedEvent.createUniqueIDEvent(getActionEventList(isServerSide, actionType), type, objects);
        getActionEventList(isServerSide, actionType).add(event);
        return event;
    }

    public boolean areConfirmationsRequired() {
        return confirmationsRequired;
    }

    public CodedEvent getActionEvent(Integer codedEventID, boolean serverSide, ActionType actionType) {
        List<CodedEvent> codedEvents = getActionEventList(serverSide, actionType);
        if (codedEvents != null) {
            for (CodedEvent codedEvent : codedEvents) {
                if (codedEvent.getID().equals(codedEventID)) {
                    return codedEvent;
                }
            }
        }
        return null;
    }

    /**
     * This method is a intermediate step to creating a timestate based measure event system
     *
     */
    public List<CodedEvent> getActionEventList(boolean serverSide, ActionType type) {

        Map<ActionType, List<CodedEvent>> actionEvents = serverSide ? this.serverActionEvents : this.clientActionEvents;
        if (!actionEvents.containsKey(type)) {
            actionEvents.put(type, new ArrayList<CodedEvent>());
        }
        return actionEvents.get(type);
    }

    @Override
    public Point getCenterPoint() {
        return null;
    }

    public double getConstructionCosts() {

        if (this.constructionCostsFixed >= 0) {
            return this.constructionCostsFixed;
        }
        return 0;
    }

    public double getConstructionCostsFixed() {
        return this.constructionCostsFixed;
    }

    public final List<Integer> getDependencyIDs() {
        return dependencyIDs;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public String getImageLocation() {
        return ACTION_IMAGE_LOCATION + getImageName();
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageVersion() {
        return imageVersion;
    }

    public double getIncome() {
        return this.incomeFixed >= 0 ? this.incomeFixed : 0;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.MEASURES;
    }

    public final Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, getOwnerID());
    }

    public final Integer getOwnerID() {
        return this.ownerID;
    }

    public HashMap<Integer, Boolean> getPermits() {
        return landOwnerPermissions;
    }

    @Override
    public MultiPolygon[] getQTGeometries() {
        return new MultiPolygon[] {};
    }

    public Integer getRelationID(Relation relation) {
        return relation == Relation.OWNER ? getOwnerID() : Item.NONE;
    }

    /**
     * Return name of related item, if relation does not exist return empty
     */
    public final String getRelationName(Relation relation) {

        Integer linkID = this.getRelationID(relation);
        Item item = this.getItem(relation.getMapLink(), linkID);
        return item instanceof UniqueNamedItem uni ? uni.getName() : StringUtils.EMPTY;
    }

    @Override
    public final TimeState getTimeState() {
        return state;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public boolean isFixedLocation() {
        return true;
    }

    public boolean isInMap(MapType mapType) {
        return this.getTimeState().isInMap(mapType);
    }

    public boolean isPhysical() {
        return false;
    }

    public void setConformationsRequired(boolean confirmation) {
        this.confirmationsRequired = confirmation;
    }

    public final void setConstructionCostsFixed(double price) {
        this.constructionCostsFixed = price;
    }

    public void setCostFixed(CostType costType, double value) {
        switch (costType) {
            case CONSTRUCTION:
                this.constructionCostsFixed = value;
                break;
        }
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.imageVersion++;
    }

    public final void setOwnerID(Integer stakeholderID) {
        this.ownerID = stakeholderID;
    }

    public final void setTimeState(final TimeState status) {
        this.state = status;
    }

    @Override
    public final String toString() {
        return getName();
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = StringUtils.EMPTY;

        if (getConstructionCosts() < 0) {
            result += "\nConstruction Costs cannot be negative: " + getConstructionCosts() + " for: " + getName();
        }
        for (List<CodedEvent> eventList : this.clientActionEvents.values()) {
            result += EventValidationUtils.validateCodedEvents(this, eventList, false);
        }
        for (List<CodedEvent> eventList : this.serverActionEvents.values()) {
            result += EventValidationUtils.validateCodedEvents(this, eventList, true);
        }

        return result;
    }

}
