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
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * KeyPoint: A point in the Cinematic.
 *
 * @author Maxim Knepfle
 */
public class KeyPoint implements Serializable {

    private static final long serialVersionUID = -96399057258718886L;

    @XMLValue
    private boolean pause = false;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private Vector3d location = new Vector3d();

    @XMLValue
    private Vector3d direction = new Vector3d();

    @XMLValue
    private Vector3d up = new Vector3d(0d, 1d, 0d);

    @XMLValue
    @ItemIDField(MapLink.EVENT_BUNDLES)
    private ArrayList<Integer> eventBundlesIDs = new ArrayList<>();

    @XMLValue
    @ItemIDField(MapLink.SOUNDS)
    private Integer soundID = Item.NONE;

    @XMLValue
    private double time = 4;

    @XMLValue
    private Integer id = Item.NONE;

    public KeyPoint() {

    }

    public String getDescription() {
        return description;
    }

    public List<Integer> getEventBundleIDs() {
        return eventBundlesIDs;
    }

    public Integer getID() {
        return id;
    }

    public final Vector3d getLocation() {
        return this.location;
    }

    public final Vector3d getLookAt() {
        return this.direction;
    }

    public String getName() {
        return "KeyPoint " + getID();
    }

    public Integer getSoundID() {
        return soundID;
    }

    public double getTime() {
        return time;
    }

    public final Vector3d getUp() {
        return this.up;
    }

    public final boolean isPause() {
        return this.pause;
    }

    public void setDescription(String newText) {
        this.description = newText;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public final void setLocation(Float[] n_location) {
        location.x = n_location[Item.X];
        location.y = n_location[Item.Y];
        location.z = n_location[Item.Z];
    }

    public final void setLocation(Vector3d n_location) {
        location.x = n_location.x;
        location.y = n_location.y;
        location.z = n_location.z;
    }

    public final void setLookAt(Float[] lookAt) {
        direction.x = lookAt[Item.X];
        direction.y = lookAt[Item.Y];
        direction.z = lookAt[Item.Z];
    }

    public final void setLookAt(Vector3d lookAt) {
        direction.x = lookAt.x;
        direction.y = lookAt.y;
        direction.z = lookAt.z;
    }

    public final void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setSoundID(Integer id) {
        this.soundID = id;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public final void setUp(Float[] n_up) {
        up.x = n_up[Item.X];
        up.y = n_up[Item.Y];
        up.z = n_up[Item.Z];
    }

    public final void setUp(Vector3d n_up) {
        up.x = n_up.x;
        up.y = n_up.y;
        up.z = n_up.z;
    }

    @Override
    public String toString() {
        return "[" + (int) (getLocation().x) + ", " + (int) (getLocation().y) + ", " + (int) (getLocation().z) + "]";
    }
}
