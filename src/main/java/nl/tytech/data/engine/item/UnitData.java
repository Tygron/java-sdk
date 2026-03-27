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

import static nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute.BUSES_ACTIVE;
import static nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute.CARS_ACTIVE;
import static nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute.TRUCKS_ACTIVE;
import static nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute.VANS_ACTIVE;
import static nl.tytech.data.engine.serializable.FunctionValue.JAM_FACTOR_BUSES;
import static nl.tytech.data.engine.serializable.FunctionValue.JAM_FACTOR_CARS;
import static nl.tytech.data.engine.serializable.FunctionValue.JAM_FACTOR_TRUCKS;
import static nl.tytech.data.engine.serializable.FunctionValue.JAM_FACTOR_VANS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Function.Region;
import nl.tytech.data.engine.item.TrafficOverlay.TrafficAttribute;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.data.engine.serializable.Addition;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.ParticleEmitterCoordinatePair;
import nl.tytech.data.engine.serializable.Show;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * ModelData: This item encapsulates the available models, this are NOT the individual models on the map.
 *
 * @author Maxim Knepfle
 */
public class UnitData extends Item implements ModelObject, ActiveItem {

    public enum TrafficType implements nl.tytech.data.engine.other.PrequelType {

        CAR(FunctionValue.NUM_CARS, JAM_FACTOR_CARS, CARS_ACTIVE, true), // MUST be ordinal 0

        VAN(FunctionValue.NUM_VANS, JAM_FACTOR_VANS, VANS_ACTIVE, true), // MUST be ordinal 1

        TRUCK(FunctionValue.NUM_TRUCKS, JAM_FACTOR_TRUCKS, TRUCKS_ACTIVE, true), // MUST be ordinal 2

        BUS(FunctionValue.NUM_BUSES, JAM_FACTOR_BUSES, BUSES_ACTIVE, true), // MUST be ordinal 3

        BICYCLE(FunctionValue.NUM_BICYCLES, null, null, false),

        PEDESTRIAN(FunctionValue.NUM_PEDESTRIANS, null, null, false),

        SHIP(FunctionValue.NUM_SHIPS, null, null, false),

        TRAM(FunctionValue.NUM_TRAMS, null, null, false),

        TRAIN(FunctionValue.NUM_TRAINS, null, null, false),

        AIR(FunctionValue.NUM_AIRPLANES, null, null, false),

        ;

        /**
         * Static reference to prevent creating new value arrays each time called.
         */
        public static final TrafficType[] VALUES = TrafficType.values();

        public static final TrafficType[] CAR_TYPES = Arrays.stream(values()).filter(t -> t.isCarTraffic()).toArray(TrafficType[]::new);

        public static final TrafficType[] RAIL_TYPES = Arrays.stream(values()).filter(t -> t.isRailTraffic()).toArray(TrafficType[]::new);

        private final FunctionValue numValue, jamValue;

        private final TrafficAttribute activeAttribute;

        private final boolean carBased;

        private TrafficType(FunctionValue numValue, FunctionValue jamValue, TrafficAttribute active, boolean carBased) {

            this.numValue = numValue;
            this.jamValue = jamValue;
            this.activeAttribute = active;
            this.carBased = carBased;
        }

        public TrafficAttribute getActiveAttribute() {
            return activeAttribute;
        }

        public final FunctionValue getJamValue() {
            return jamValue;
        }

        public final float getLaneWidth() {
            return isCarTraffic() ? 3f : this == PEDESTRIAN || this == BICYCLE ? 1f : 0f;
        }

        public final FunctionValue getNumValue() {
            return numValue;
        }

        public final boolean isCarTraffic() {
            return carBased;
        }

        public final boolean isRailTraffic() {
            return this == TRAM || this == TRAIN;
        }
    }

    public static final String UNIT_DIR = "Models/Units/";

    private static final long serialVersionUID = 8050467134935662384L;

    @XMLValue
    @ListOfClass(Region.class)
    private ArrayList<Region> regions = new ArrayList<>();

    @XMLValue
    @ListOfClass(TColor.class)
    private ArrayList<TColor> colors = new ArrayList<>(Arrays.asList(new TColor[] {
            // typical car colors
            TColor.GRAY, TColor.LIGHT_GRAY, TColor.BLACK, TColor.WHITE, TColor.GRAY, new TColor(125, 29, 10), new TColor(7, 86, 24),
            new TColor(25, 10, 118) }));

    @XMLValue
    private String name = "0_new model";

    @XMLValue
    private double proximityWarningM = -1;

    @XMLValue
    private TrafficType type = TrafficType.CAR;

    @XMLValue
    private String fileName = StringUtils.EMPTY;

    @XMLValue
    private boolean isAlpha = false;

    @XMLValue
    @ListOfClass(ParticleEmitterCoordinatePair.class)
    private ArrayList<ParticleEmitterCoordinatePair> particleEmitters = new ArrayList<>();

    @XMLValue
    private boolean active = false;

    public UnitData() {

    }

    @Override
    public List<Addition> getAdditions() {
        return Collections.emptyList();
    }

    public ArrayList<TColor> getColors() {
        /**
         * Try override first
         */
        UnitDataOverride unitDataOverride = this.getItem(MapLink.UNIT_DATA_OVERRIDES, this.getID());
        if (unitDataOverride != null && unitDataOverride.hasColors()) {
            return unitDataOverride.getColors();
        } else {
            return colors;
        }
    }

    @Override
    public String getFileName() {
        if (!StringUtils.containsData(fileName)) {
            return StringUtils.EMPTY;
        }
        return UNIT_DIR + fileName;
    }

    @Override
    public String getName() {

        /**
         * Try override first
         */
        UnitDataOverride unitDataOverride = getUnitDataOverride();
        if (unitDataOverride != null && StringUtils.containsData(unitDataOverride.getName())) {
            return unitDataOverride.getName();
        } else {
            return name;
        }
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
        // disabled particle emitters, units are instanced thus can not have particles flying around
        return null;
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getPairs() {
        // disabled particle emitters, units are instanced thus can not have particles flying around
        return new ArrayList<>();// particleEmitters;
    }

    public double getProximityWarningM() {
        return proximityWarningM;
    }

    @Override
    public Show getShow() {
        return Show.FAR;
    }

    public TrafficType getTrafficType() {
        return type;
    }

    public UnitDataOverride getUnitDataOverride() {
        return this.getItem(MapLink.UNIT_DATA_OVERRIDES, this.getID());
    }

    @Override
    public boolean hasRoots() {
        return false;
    }

    @Override
    public boolean isActive() {

        UnitDataOverride override = getUnitDataOverride();
        if (override != null && override.hasActive()) {
            return override.isActive();
        } else {
            return active;
        }
    }

    @Override
    public boolean isAlpha() {
        return isAlpha;
    }

    /**
     * When true this unit is either region or part of given region.
     * @param region
     * @return
     */
    public boolean isInRegion(Region region) {
        return regions.size() == 0 || region == null || regions.contains(region);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProximityWarningM(double proximityWarningM) {
        this.proximityWarningM = proximityWarningM;
    }

    @Override
    public boolean skipLOD() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
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
