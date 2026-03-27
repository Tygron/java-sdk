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
import java.util.Collections;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.data.engine.serializable.Addition;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;
import nl.tytech.data.engine.serializable.Show;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 * SpecialEffect: Special effect of particles.
 *
 * @author Maxim Knepfle
 */
public class SpecialEffect extends UniqueNamedItem implements ModelObject, ActiveItem {

    private static final long serialVersionUID = 9020181395434283909L;

    @XMLValue
    @ListOfClass(ParticleEmitterCoordinatePair.class)
    private ArrayList<ParticleEmitterCoordinatePair> particleEmitters = new ArrayList<>();

    @XMLValue
    private boolean active = false;

    @XMLValue
    private Point point = JTSUtils.createPoint(0, 0, 0);

    public ParticleEmitterCoordinatePair addPair(Integer particleEmitterID) {
        ParticleEmitterCoordinatePair pair = new ParticleEmitterCoordinatePair(getNewID(), particleEmitterID);
        particleEmitters.add(pair);
        return pair;
    }

    public void addPair(ParticleEmitterCoordinatePair pair) {
        pair.setID(getNewID());
        particleEmitters.add(pair);
    }

    @Override
    public List<Addition> getAdditions() {
        return Collections.emptyList();
    }

    public Point getCenter() {
        return point;
    }

    @Override
    public String getFileName() {
        return StringUtils.EMPTY;
    }

    private Integer getNewID() {
        int newID = Item.NONE;
        for (int i = 0; i < particleEmitters.size(); i++) {
            newID = Math.max(particleEmitters.get(i).getID(), newID);
        }
        return newID + 1;
    }

    @Override
    public ParticleEmitterCoordinatePair getPair(Integer pairID) {
        for (int i = 0; i < particleEmitters.size(); i++) {
            if (particleEmitters.get(i).getID().equals(pairID)) {
                return particleEmitters.get(i);
            }
        }
        return null;
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getPairs() {
        return particleEmitters;
    }

    @Override
    public Show getShow() {
        return Show.FAR;
    }

    @Override
    public boolean hasRoots() {
        return false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isAlpha() {
        return false;
    }

    public ParticleEmitterCoordinatePair removePair(Integer pairID) {
        for (int i = 0; i < particleEmitters.size(); i++) {
            if (particleEmitters.get(i).getID().equals(pairID)) {
                return particleEmitters.remove(i);
            }
        }
        return null;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCenter(Point center) {
        this.point = center;
    }

    @Override
    public boolean skipLOD() {
        return false;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public String validated(boolean startSession) {

        for (int i = 0; i < particleEmitters.size(); i++) {
            if (Item.NONE.equals(particleEmitters.get(i).getID())) {
                particleEmitters.get(i).setID(getNewID());
            }
        }

        return super.validated(startSession);
    }
}
