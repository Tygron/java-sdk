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
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.data.engine.serializable.Addition;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;
import nl.tytech.data.engine.serializable.Show;
import nl.tytech.util.StringUtils;

/**
 * ModelData: This item encapsulates the available models, this are NOT the individual models on the map.
 *
 * @author Maxim Knepfle
 */
public class ModelData extends Item implements ModelObject {

    public enum InstanceColor {

        GRAY,

        WHITE,

        VEGIE_GREEN,

        GRAIN_YELLOW,

        FLOWERS,

    }

    /**
     * Placement defines the behavior of the model in the placement algorithm.
     */
    public enum Placement {

        // point models
        POINT("point", true), //

        // edges
        EDGE("edge", true), //

        ; //

        public static final List<Placement> EMPTY = new ArrayList<>();

        private String assetName;
        /**
         * Model takes a fixed dimension e.g. 1m, 3m, 10m wide
         */
        private boolean hasDimension;

        private Placement(String enumDescription, boolean hasDimension) {
            this.assetName = enumDescription.toLowerCase();
            this.hasDimension = hasDimension;

        }

        public String getAssetName() {
            return this.assetName;
        }

        public boolean hasDimension() {
            return hasDimension;
        }
    }

    public enum Rotation {
        /**
         * Rotation is fixed.
         */
        FIXED,
        /**
         * Rotation is randomly choosen from 0, 90, 180 or 360 degrees (quaters)
         */
        QUARTER,

        /**
         * Free rotation is given the model between 0-360 degrees.
         */
        FREE
    }

    /**
     * Base angle offset for 3D models
     */
    public static final float BASE_ANGLE_DEG = 90;

    public static final String MODEL_DIR = "Models/";

    public static final String DETAILS_DIR = MODEL_DIR + "Details/";

    private static final long serialVersionUID = 8050467134935662284L;

    @XMLValue
    private String name = "0_new model";

    @XMLValue
    @ListOfClass(Addition.class)
    private ArrayList<Addition> additions = new ArrayList<>();

    @XMLValue
    private Placement placement = Placement.POINT;

    @XMLValue
    private int frequency = 1;

    @XMLValue
    private Double animMultiplier = null;

    @XMLValue
    private int variation = 1;

    @XMLValue
    private int dimension = 10;

    @XMLValue
    private Integer buffer = null;

    @XMLValue
    private double modelHeight = -1;

    @XMLValue
    private double modelWidth = -1;

    @XMLValue
    @ListOfClass(ParticleEmitterCoordinatePair.class)
    private ArrayList<ParticleEmitterCoordinatePair> particleEmitters = new ArrayList<>();

    @XMLValue
    private boolean isAlpha = false;

    @XMLValue
    private boolean isShowCloseby = false;

    @XMLValue
    private Show show = Show.FAR;

    @XMLValue
    private boolean roots = false;

    @XMLValue
    private double randomScale = 0;

    @XMLValue
    private InstanceColor instanceColor = null;

    @XMLValue
    private Rotation rotation = Rotation.FIXED;

    @JsonIgnore
    private transient String fileName = null; // calculated on the fly

    /**
     * Empty constructor.
     */
    public ModelData() {

    }

    @Override
    public List<Addition> getAdditions() {
        return additions;
    }

    public Double getAnimMultiplier() {
        return animMultiplier;
    }

    public int getBuffer() {
        return buffer == null ? dimension : buffer.intValue();
    }

    /**
     * Force standard model asset naming conventions.
     * @return
     */
    private String getCorrectFileName() {

        String result = StringUtils.lowerCaseWithUnderScores(name);
        String extension = "_" + placement.getAssetName();

        if (placement.hasDimension()) {
            extension += "_" + dimension;
        }

        extension += "_furniture_" + this.variation;

        if (result.contains(StringUtils.LANG_SPLIT)) {
            result = result.replaceFirst(StringUtils.LANG_SPLIT, extension + StringUtils.LANG_SPLIT);
        } else {
            result += extension;
        }
        return result;
    }

    /**
     * Get dimension of the model
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Returns the file name of the model.
     *
     * @return
     */
    @Override
    public String getFileName() {

        if (fileName == null) {
            fileName = DETAILS_DIR + getCorrectFileName();
        }
        return fileName;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public InstanceColor getInstanceColor() {
        return instanceColor;
    }

    public double getModelHeightM() {
        return modelHeight;
    }

    public double getModelWidthM() {
        return modelWidth;
    }

    @Override
    public String getName() {
        return name;
    }

    private Integer getNewID() {
        int newID = Item.NONE;
        for (int i = 0; i < particleEmitters.size(); i++) {
            newID = Math.max(particleEmitters.get(i).getID(), newID);
        }
        return newID + 1;
    }

    @Override
    public ParticleEmitterCoordinatePair getPair(Integer id) {
        for (int i = 0; i < particleEmitters.size(); i++) {
            if (particleEmitters.get(i).getID().equals(id)) {
                return particleEmitters.get(i);
            }
        }
        return null;
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getPairs() {
        return particleEmitters;
    }

    /**
     * @return the group the model belongs to.
     */

    public final Placement getPlacement() {
        return placement;
    }

    public double getRandomScale() {
        return randomScale;
    }

    public Rotation getRotation() {
        return rotation;
    }

    @Override
    public Show getShow() {
        return show;
    }

    @Override
    public boolean hasRoots() {
        return roots;
    }

    public final boolean hasWindDirectedParticles() {

        for (int i = 0; i < particleEmitters.size(); i++) {
            ParticleEmitterModel emitter = getItem(MapLink.PARTICLE_EMITTERS, particleEmitters.get(i).getParticleEmitterID());
            if (emitter != null && emitter.isWindDirected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAlpha() {
        return isAlpha;
    }

    public boolean isGeneric() {
        return this.getID() < Item.SPECIFIC_START_ID;
    }

    public void setFileName(String fileName) {
        this.name = fileName;
    }

    public void setModelHeightM(double modelHeightM) {
        this.modelHeight = modelHeightM;
    }

    public void setModelWidthM(double modelWidthM) {
        this.modelWidth = modelWidthM;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    @Override
    public boolean skipLOD() {
        return false;
    }

    @Override
    public String toString() {

        if (this.isShowCloseby) {
            String result = name;
            if (result.contains(StringUtils.LANG_SPLIT)) {
                result = result.replaceFirst(StringUtils.LANG_SPLIT, " (closeby)" + StringUtils.LANG_SPLIT);
            } else {
                result += " (closeby)";
            }
            return result;
        }
        return this.getCorrectFileName();
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = StringUtils.EMPTY;

        // only odd values allowed for dimension
        if (dimension <= 0) {
            result += "\nModel: " + this.getName() + " " + this.getID() + " has an invalid dimension!";
        }

        for (int i = 0; i < particleEmitters.size(); i++) {
            ParticleEmitterCoordinatePair pair = particleEmitters.get(i);
            if (Item.NONE.equals(pair.getID())) {
                pair.setID(getNewID());
            }

            Integer particleID = pair.getParticleEmitterID();
            Object emitter = this.getItem(MapLink.PARTICLE_EMITTERS, particleID);
            if (emitter == null) {
                result += "\nMissing particle emitter item for id: " + particleID + " in model: " + this.getName() + " (" + this.getID()
                        + ").";
            }
        }
        return result;
    }
}
