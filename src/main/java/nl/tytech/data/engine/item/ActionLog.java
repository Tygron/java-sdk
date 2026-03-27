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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 * Log of an action performed by participant or system
 *
 * @author Maxim Knepfle
 */
public class ActionLog extends Item {

    private static final long serialVersionUID = -4253782210241563278L;

    @XMLValue
    private HashMap<Integer, Double> scoreBefore = new HashMap<>();

    @XMLValue
    private HashMap<Integer, Double> scoreAfter = new HashMap<>();

    @XMLValue
    private ArrayList<Integer> buildingIDs = new ArrayList<>();

    @XMLValue
    private MultiPolygon mp = JTSUtils.EMPTY;

    @XMLValue
    private Point point = null;

    @XMLValue
    private Integer itemID = Item.NONE;

    @XMLValue
    private MapLink mapLink = null;

    @XMLValue
    private long simTime;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer stakeholderID;

    @XMLValue
    private ParticipantEventType action;

    @XMLValue
    private String subject = StringUtils.EMPTY;

    @XMLValue
    private Double budget = null;

    public ActionLog() {
    }

    public ActionLog(ParticipantEventType action) {
        this.action = action;
    }

    public boolean containsAfterScore(Indicator indicator) {
        return this.scoreAfter.containsKey(indicator.getID());
    }

    public String getAction() {
        ClientTerms term = action.getClientTerm();
        if (term == null) {
            return getStakeholder() + ": " + action.toString() + " " + this.getSubject();
        }
        return this.getWord(MapLink.CLIENT_WORDS, term, getStakeholder(), getSubject());
    }

    public Double getAfterScore(Indicator indicator) {
        return scoreAfter.get(indicator.getID());
    }

    public Double getBudget() {
        return budget;
    }

    public List<Integer> getBuildingIDs() {
        return buildingIDs;
    }

    public Double getIncrease(Indicator indicator) {

        Double before = scoreBefore.get(indicator.getID());
        if (before == null) {
            return null;
        }
        Double after = scoreAfter.get(indicator.getID());
        if (after == null) {
            return null;
        }
        return after - before;
    }

    public Integer getItemID() {
        return itemID;
    }

    public MapLink getMapLink() {
        return mapLink;
    }

    public MultiPolygon getMultiPolygon() {

        List<Geometry> mps = new ArrayList<>();
        List<Building> buildings = this.getItems(MapLink.BUILDINGS, this.getBuildingIDs());
        for (Building building : buildings) {
            mps.add(building.getPolygons(null));
        }

        if (mapLink != null) {
            Item item = this.getItem(mapLink, itemID);
            if (item instanceof GeometryItem<?> g) {
                Geometry[] mpArray = g.getQTGeometries();
                for (int i = 0; i < mpArray.length; i++) {
                    mps.add(mpArray[i]);
                }
            }
        }

        if (!JTSUtils.isEmpty(this.mp)) {
            mps.add(this.mp);
        }
        return JTSUtils.createMP(mps);
    }

    public Point getPoint() {
        return point;
    }

    public long getSimTime() {
        return simTime;
    }

    public Stakeholder getStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, this.stakeholderID);
    }

    public String getSubject() {

        if (!StringUtils.containsData(subject)) {

            List<Building> buildings = this.getItems(MapLink.BUILDINGS, this.getBuildingIDs());
            for (Building building : buildings) {
                subject = building.getFunction().getName();
                return subject;
            }

            if (mapLink != null) {
                Item item = this.getItem(mapLink, this.itemID);
                if (item instanceof UniqueNamedItem) {
                    subject = ((UniqueNamedItem) item).getName();
                    return subject;
                }
                if (item != null) {
                    subject = item.toString();
                    return subject;
                }
            }
            subject = StringUtils.EMPTY;
        }
        return subject;
    }

    public boolean isFinished() {
        return scoreBefore.size() == scoreAfter.size();
    }

    public void setAfterScore(Indicator indicator) {
        scoreAfter.put(indicator.getID(), indicator.getValue(MapType.MAQUETTE));
    }

    public void setBeforeScore(Indicator indicator) {
        scoreBefore.put(indicator.getID(), indicator.getValue(MapType.MAQUETTE));
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public void setItem(MapLink mapLink, Integer itemID) {
        this.mapLink = mapLink;
        this.itemID = itemID;
    }

    public void setMultiPolygon(MultiPolygon mp) {
        this.mp = mp;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setSimTime(long simTimeMillis) {
        this.simTime = simTimeMillis;
    }

    public void setStakeholderID(Integer id) {
        this.stakeholderID = id;
    }

    @Override
    public String toString() {
        return getAction();
    }

}
