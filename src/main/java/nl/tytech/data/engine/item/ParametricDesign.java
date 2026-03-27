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
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.data.engine.item.GridOverlay.Key;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.PlotDesign;
import nl.tytech.data.engine.serializable.PlotDesign.PlotDesignAttribute;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Base class for Parametric Designs
 *
 * @author Maxim Knepfle
 *
 */
public class ParametricDesign extends PolygonAttributeItem {

    public enum Alignment {

        ROAD,

        SPACED;

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum DesignAttribute implements ReservedAttribute {

        BACKYARD_DISTANCE_M(Double.class, 5),

        WATER_WIDTH_M(Double.class, 3),

        KEEP_EXISTING_ROADS(Double.class, 1),

        ROAD_WIDTH_M(Double.class, 6),

        ROAD_DISTANCE_Y_M(Double.class, 40),

        ROAD_SEARCH_DISTANCE_M(Double.class, 10),

        SIDEWALK_WIDTH_M(Double.class, 2),

        PARKING_WIDTH_M(Double.class, 3),

        PARKING_LENGTH_M(Double.class, 6),

        FRACTION_BUILDINGS(Double.class, 0.25, TColor.ORANGE),

        FRACTION_ROADS(Double.class, 0.2, TColor.BLACK),

        FRACTION_WATER(Double.class, 0.0, TColor.BLUE),

        FRACTION_PUBLIC_GREEN(Double.class, 0.2, TColor.DARK_GREEN),

        FRACTION_GARDENS(Double.class, 0.3, TColor.DARK_YELLOW),

        FRACTION_PARKING(Double.class, 0.05, TColor.GRAY),

        FRACTION_REMAINDER(Double.class, 0.0, TColor.WHITE)

        ;

        public static List<DesignAttribute> getFractionValues() {

            List<DesignAttribute> details = new ArrayList<>();
            for (DesignAttribute detail : values()) {
                if (detail.isFraction()) {
                    details.add(detail);
                }
            }
            return details;
        }

        private final Class<?> type;

        private final double[] defaultArray;

        private final TColor color;

        private DesignAttribute(Class<?> type, double defaultValue) {
            this(type, defaultValue, TColor.WHITE);
        }

        private DesignAttribute(Class<?> type, double defaultValue, TColor color) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
            this.color = color;
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        public TColor getColor() {
            return color;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        public boolean isFraction() {
            return switch (this) {
                case FRACTION_GARDENS, FRACTION_BUILDINGS, FRACTION_PARKING, FRACTION_PUBLIC_GREEN, FRACTION_WATER, FRACTION_ROADS, FRACTION_REMAINDER -> true;
                default -> false;
            };
        }

        public boolean isPlotDetail() {
            return switch (this) {
                case BACKYARD_DISTANCE_M -> true;
                default -> false;
            };
        }

        public boolean isRoadDetail() {
            return switch (this) {
                case KEEP_EXISTING_ROADS, ROAD_WIDTH_M, WATER_WIDTH_M, SIDEWALK_WIDTH_M, PARKING_WIDTH_M, PARKING_LENGTH_M, ROAD_SEARCH_DISTANCE_M -> true;
                default -> false;
            };
        }
    }

    public enum Example {

        CITY_NEIGHBORHOOD(Alignment.ROAD),

        URBAN_NEIGHBORHOOD(Alignment.ROAD),

        SUBURBAN_NEIGHBORHOOD(Alignment.ROAD),

        WINDMILL_AREA(Alignment.SPACED);

        private final Alignment alignment;

        private Example(Alignment a) {
            this.alignment = a;
        }

        public Alignment getAlignment() {
            return alignment;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public enum FunctionType implements Key {

        ROAD(TColor.BLACK, DesignAttribute.FRACTION_ROADS, 0.5),

        WATER(TColor.BLUE, DesignAttribute.FRACTION_WATER, 0.5),

        PARKING(TColor.GRAY, DesignAttribute.FRACTION_PARKING, 0.5),

        SIDEWALK(TColor.LIGHT_GRAY, DesignAttribute.FRACTION_ROADS, 0.5),

        BUILDING(TColor.ORANGE, DesignAttribute.FRACTION_BUILDINGS, 1.0),

        GARDEN(TColor.DARK_YELLOW, DesignAttribute.FRACTION_GARDENS, 0.5),

        PUBLIC_GREEN(TColor.DARK_GREEN, DesignAttribute.FRACTION_PUBLIC_GREEN, 0.5),

        REMAINDER(TColor.TRANSPARENT, DesignAttribute.FRACTION_REMAINDER, 0);

        public static final FunctionType[] VALUES = FunctionType.values();

        private final TColor color;

        private final DesignAttribute fraction;

        private final double priority;

        private FunctionType(TColor color, DesignAttribute fraction, double priority) {
            this.color = color;
            this.fraction = fraction;
            this.priority = priority;
        }

        public TColor getColor() {
            return color;
        }

        public DesignAttribute getFraction() {
            return fraction;
        }

        public MapLink getMapLink() {
            return this == WATER ? MapLink.TERRAIN_TYPES : MapLink.FUNCTIONS;
        }

        public double getPriority() {
            return priority;
        }

        @Override
        public UnitType getUnitType() {
            return UnitType.NONE;
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public boolean isOutput() {
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public static final int MAX_PLOT_DESIGNS = 6;

    public static final String BUILDING_ATTRIBUTE = "DESIGN_ID";

    public static final double MARGIN = 0.01;

    private static final long serialVersionUID = -9095146102222418785L;

    public static final Integer DEFAULT_VARIANT_ID = Item.NONE;

    public static final Predicate<Building> ROAD_PREDICATE = b -> b.isRoadSystem();

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer roadID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer parkingID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer sidewalkID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.TERRAIN_TYPES)
    private Integer waterID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer gardenID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer publicGreenID = Item.NONE;

    @XMLValue
    private Alignment alignment = Alignment.SPACED;

    @XMLValue
    private ArrayList<PlotDesign> plotDesigns = new ArrayList<>();

    public PlotDesign addPlotDesign(Integer functionID) {

        int heighestID = 0;
        for (PlotDesign plotDesign : plotDesigns) {
            heighestID = Math.max(heighestID, plotDesign.getID());
        }
        heighestID++;

        PlotDesign plotDesign = new PlotDesign(heighestID, functionID);
        for (PlotDesignAttribute pdAttribute : PlotDesignAttribute.values()) {
            plotDesign.setAttributeArray(pdAttribute, pdAttribute.defaultArray());
        }
        this.plotDesigns.add(plotDesign);
        return plotDesign;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public double getAreaM2() {
        return getMultiPolygon().getArea();
    }

    public double getAreaM2(FunctionType functionType) {
        double areaM2 = getAreaM2();
        DesignAttribute attribute = functionType.getFraction();
        return attribute == null ? areaM2 : areaM2 * getOrDefault(attribute);
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return DesignAttribute.values();
    }

    public Integer getFunctionTypeID(FunctionType type) {
        return getFunctionTypeID(type, DEFAULT_VARIANT_ID);
    }

    public Integer getFunctionTypeID(FunctionType type, Integer plotDesignID) {

        return switch (type) {
            case ROAD -> roadID;
            case PARKING -> parkingID;
            case SIDEWALK -> sidewalkID;
            case WATER -> waterID;
            case GARDEN -> gardenID;
            case PUBLIC_GREEN -> publicGreenID;
            case BUILDING -> getPlotFunctionID(plotDesignID);
            default -> Item.NONE;
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends NamedItem> T getFunctionTypeItem(FunctionType type, Integer variantID) {
        return (T) getItem(type.getMapLink(), getFunctionTypeID(type, variantID));
    }

    public PlotDesign getPlotDesign(Integer designID) {
        for (PlotDesign plotDesign : plotDesigns) {
            if (plotDesign.getID().equals(designID)) {
                return plotDesign;
            }
        }
        return null;
    }

    public PlotDesign getPlotDesignAt(int index) {
        if (index >= 0 && index < plotDesigns.size()) {
            return plotDesigns.get(index);
        }
        return null;
    }

    public List<PlotDesign> getPlotDesigns() {
        return plotDesigns;
    }

    public Function getPlotFuction(PlotDesign pd) {
        return pd == null ? null : getItem(MapLink.FUNCTIONS, pd.getFunctionID());
    }

    public Function getPlotFunction(Integer plotDesignID) {
        return getPlotFuction(getPlotDesign(plotDesignID));
    }

    private Integer getPlotFunctionID(Integer plotDesignID) {
        PlotDesign pd = getPlotDesign(plotDesignID);
        return pd == null ? Item.NONE : pd.getFunctionID();
    }

    private int getPlotUnits(double plotsAreaM2, PlotDesign plotDesign) {
        return (int) Math.round(plotsAreaM2 * plotDesign.getOrDefault(PlotDesignAttribute.FRACTION) * plotDesign.getUnitM2());
    }

    public Map<Integer, Integer> getPredicatedUnits() {

        double plotsAreaM2 = getAreaM2(FunctionType.BUILDING);

        Map<Integer, Integer> plotUnits = new TreeMap<>();
        for (PlotDesign pd : getPlotDesigns()) {
            plotUnits.put(pd.getID(), getPlotUnits(plotsAreaM2, pd));
        }
        return plotUnits;
    }

    public int getPredicatedUnits(Integer plotDesignID) {
        PlotDesign pd = getPlotDesign(plotDesignID);
        if (pd == null) {
            return 0;
        }
        return getPlotUnits(getAreaM2(FunctionType.BUILDING), pd);
    }

    public int getValidFloors(FunctionType type, Integer variantID) {

        if (type == FunctionType.BUILDING) {
            return getValidFloors(variantID);
        }
        Function f = getFunctionTypeItem(type, variantID);
        return (int) (f == null ? FunctionValue.DEFAULT_FLOORS.defaultValue() : f.getValue(FunctionValue.DEFAULT_FLOORS));
    }

    public int getValidFloors(Integer plotDesignID) {

        PlotDesign pd = getPlotDesign(plotDesignID);
        return pd == null ? (int) FunctionValue.DEFAULT_FLOORS.defaultValue() : pd.getValidFloors(this);
    }

    public double getValidHeight(Integer variantID) {

        Function f = getFunctionTypeItem(FunctionType.BUILDING, variantID);
        PlotDesign pd = getPlotDesign(variantID);
        double heightM = PlotDesignAttribute.FLOOR_HEIGHT_M.defaultValue();
        if (pd != null) {
            heightM = pd.getOrDefault(PlotDesignAttribute.FLOOR_HEIGHT_M);

            if (heightM <= 0.0) { // do it random
                double defaultHeight = f.getValue(FunctionValue.FLOOR_HEIGHT_M);
                heightM = Math.max(1, Math.random() * 4.0 * defaultHeight);
            }
        }
        return heightM;
    }

    public List<Integer> getVariantIDs(FunctionType type) {

        switch (type) {
            case BUILDING:
                return getPlotDesigns().stream().map(p -> p.getID()).collect(Collectors.toList());

            default:
                List<Integer> variants = new ArrayList<>();
                variants.add(DEFAULT_VARIANT_ID);
                return variants;
        }
    }

    public boolean isActive(FunctionType type) {
        if (type == FunctionType.BUILDING) {
            return !plotDesigns.isEmpty();
        }
        return type == FunctionType.REMAINDER || !Item.NONE.equals(getFunctionTypeID(type, DEFAULT_VARIANT_ID));
    }

    public final boolean keepExistingRoads() {
        return getOrDefault(DesignAttribute.KEEP_EXISTING_ROADS) > 0;
    }

    public boolean removePlotDesign(Integer designID) {
        for (PlotDesign plotDesign : plotDesigns) {
            if (plotDesign.getID().equals(designID)) {
                return plotDesigns.remove(plotDesign);
            }
        }
        return false;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public void setFunctionTypeID(FunctionType type, Integer functionID) {

        switch (type) {
            case ROAD:
                roadID = functionID;
                break;
            case PARKING:
                parkingID = functionID;
                break;
            case SIDEWALK:
                sidewalkID = functionID;
                break;
            case WATER:
                waterID = functionID;
                break;
            case GARDEN:
                gardenID = functionID;
                break;
            case PUBLIC_GREEN:
                publicGreenID = functionID;
                break;
            default:
                break;
        }
    }
}
