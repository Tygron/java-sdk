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
import nl.tytech.core.event.Event.SessionEventTypeEnum;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.KeyPoint;
import nl.tytech.util.StringUtils;

/**
 * @author Alexander Hofstede
 */
public class CinematicData extends Item {

    public enum EndType {
        SIMPLE,

        LOOP,

        WAIT_AT_END;
    }

    public static final int MAX_SPEED_M_SEC = 80;
    public static final double MAX_ROTATION_SPEED = 0.1f;

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -8198427741756526965L;

    @XMLValue
    private boolean animateToStartPoint = false;

    @XMLValue
    @ListOfClass(KeyPoint.class)
    private ArrayList<KeyPoint> keyPoints = new ArrayList<>();

    @XMLValue
    private String name = "No Name";

    @XMLValue
    private String voice = StringUtils.EMPTY;

    @XMLValue
    private EndType endType = EndType.SIMPLE;

    @XMLValue
    private Integer stakeholderID = Item.NONE;

    @DoNotSaveToInit
    @XMLValue
    private boolean animateToStart = false;

    @DoNotSaveToInit
    @XMLValue
    private Integer keyIndex = 0;

    @XMLValue
    @DoNotSaveToInit
    private boolean active = false;

    private transient boolean localClientOnly = false;

    public CinematicData() {

    }

    public void add(int index, KeyPoint keyPoint) {
        int maxID = Item.NONE;
        for (KeyPoint other : keyPoints) {
            maxID = Math.max(maxID, other.getID());
        }

        maxID++;
        keyPoint.setID(maxID);

        if (index < 0 || index >= keyPoints.size()) {
            keyPoints.add(keyPoint);
        } else {
            keyPoints.add(index, keyPoint);
        }
    }

    public void add(KeyPoint keyPoint) {
        add(keyPoints.size(), keyPoint);
    }

    public KeyPoint get(int index) {
        return index < keyPoints.size() ? keyPoints.get(index) : null;
    }

    public EndType getEndType() {
        return endType;
    }

    public int getIndexOf(Integer keyPointID) {
        for (int i = 0; i < keyPoints.size(); ++i) {
            KeyPoint other = keyPoints.get(i);
            if (other.getID().equals(keyPointID)) {
                return i;
            }
        }
        return -1;
    }

    public KeyPoint getKeyPoint(Integer keyPointID) {
        for (KeyPoint keyPoint : keyPoints) {
            if (keyPoint.getID().equals(keyPointID)) {
                return keyPoint;
            }
        }
        return null;

    }

    public final List<KeyPoint> getKeyPoints() {
        return keyPoints;
    }

    public Integer getKeyPointSoundID(int pointIndex) {
        KeyPoint point = keyPoints.get(pointIndex);
        return point.getSoundID();
    }

    public String getName() {
        return name;
    }

    public Integer getPointIndex() {
        return keyIndex;
    }

    public Integer getStakeholderID() {
        return stakeholderID;
    }

    public double getTotalPlayingTime() {

        double totalTime = 0;
        for (KeyPoint keyPoint : this.getKeyPoints()) {
            totalTime += keyPoint.getTime();
        }
        return totalTime;
    }

    public String getVoice() {
        return voice;
    }

    public boolean hasServerSessionEvents() {

        for (KeyPoint keyPoint : getKeyPoints()) {
            List<EventBundle> eventBundles = getItems(MapLink.EVENT_BUNDLES, keyPoint.getEventBundleIDs());
            for (EventBundle eventBundle : eventBundles) {
                for (CodedEvent event : eventBundle.getServerEvents()) {
                    if (event.getType() instanceof SessionEventTypeEnum) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int indexOf(KeyPoint keyPoint) {
        return keyPoints.indexOf(keyPoint);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAnimateToStartPoint() {
        return animateToStartPoint;
    }

    public boolean isContinuous() {
        return endType == EndType.LOOP;
    }

    public boolean isEmpty() {
        return keyPoints.isEmpty();
    }

    /**
     * When true play cinematic localy, no server events or server reporting
     * @return
     */
    public boolean isLocalClientOnly() {
        return localClientOnly;
    }

    public KeyPoint removeKeyPoint(Integer keyPointID) {
        for (int i = 0; i < keyPoints.size(); i++) {
            KeyPoint keyPoint = keyPoints.get(i);
            if (keyPoint.getID().equals(keyPointID)) {
                return keyPoints.remove(i);
            }
        }
        return null;
    }

    /**
     * Reset cinamatic's stakeholder, point, etc.
     */

    public void resetCinematic() {
        this.keyIndex = 0;
        this.active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAnimateToStartPoint(boolean animate) {
        animateToStartPoint = animate;
    }

    public void setEndType(EndType endType) {
        this.endType = endType;
    }

    public void setLocalClientOnly(boolean localClientOnly) {
        this.localClientOnly = localClientOnly;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setPointIndex(Integer pointIndex) {
        this.keyIndex = pointIndex;
    }

    public void setStakeholderID(Integer stakeholderID) {
        this.stakeholderID = stakeholderID;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public int size() {
        return keyPoints.size();
    }

    @Override
    public String toString() {
        return name + " (" + getID() + ")";
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = StringUtils.EMPTY;

        // TODO: If keypoints do not have a set id, set them once
        if (!this.getKeyPoints().isEmpty() && Item.NONE.equals(this.getKeyPoints().get(0).getID())) {
            int index = 0;
            for (KeyPoint keyPoint : this.getKeyPoints()) {
                keyPoint.setID(index);
                index++;
            }
        }

        for (KeyPoint point : this.getKeyPoints()) {
            point.setDescription(StringUtils.removeHTMLTags(point.getDescription()));
            result += validFields(point);
        }

        if (startNewSession && active) {
            result += "\nCinematic data should not be active at the start. This should be triggerd through the "
                    + Scenario.class.getSimpleName() + ". Invalid data in " + CinematicData.class.getSimpleName() + " [" + getID() + "]";
        }
        return result;
    }
}
