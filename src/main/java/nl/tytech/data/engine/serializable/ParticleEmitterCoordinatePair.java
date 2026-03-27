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
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;

/**
 * ParticleEmitterCoordinatePair: Defines a connection between an object (or center of world when no object is related) and emitter in 3D
 * world Coordinates.
 *
 * @author Maxim Knepfle
 */
public class ParticleEmitterCoordinatePair implements Serializable {

    private static final long serialVersionUID = 4505099981435018409L;

    @XMLValue
    @ItemIDField(MapLink.PARTICLE_EMITTERS)
    private Integer particleEmitterID = Item.NONE;

    @XMLValue
    private Integer id = Item.NONE;

    @XMLValue
    private double[] offsetCoordinate = new double[3];

    public ParticleEmitterCoordinatePair() {

    }

    public ParticleEmitterCoordinatePair(Integer id, Integer emitterID) {
        this.id = id;
        this.particleEmitterID = emitterID;
    }

    public Integer getID() {
        return this.id;
    }

    public Integer getParticleEmitterID() {
        return particleEmitterID;
    }

    public double[] getWorldOffset() {
        return this.offsetCoordinate;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setParticleEmitterID(Integer particleEmitter) {
        this.particleEmitterID = particleEmitter;
    }

    public void setWorldOffset(double[] offset) {
        this.offsetCoordinate[0] = offset[0];
        this.offsetCoordinate[1] = offset[1];
        this.offsetCoordinate[2] = offset[2];
    }
}
