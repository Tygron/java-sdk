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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.util.DetailUtils;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.Source.SourceInterface;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.data.engine.item.UnitData.TrafficType;
import nl.tytech.data.engine.other.LayerItem;
import nl.tytech.data.engine.other.LayerQueryInterface;
import nl.tytech.data.engine.other.TQLItem;
import nl.tytech.data.engine.other.TimeStateItem;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.data.engine.serializable.CGLink;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FaceType;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.data.engine.serializable.Section;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.DateUtils;
import nl.tytech.util.ItemIntersector;
import nl.tytech.util.ItemIntersector.IntersectorItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.MathUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * Building: This class keeps track of the basic Building functionality.
 *
 * @author Maxim Knepfle
 */
public class Building extends AttributeItem implements SourceInterface, TimeStateItem, GeometryItem<MultiPolygon>, ValueItem, TQLItem,
        IntersectorItem, LayerQueryInterface, LayerItem {

    public enum BuildingAttribute implements ReservedAttribute {

        CONSTRUCTION_FINISH_DATE(Double.class),

        POWER_LINE_BUILDING_ID(Integer.class),

        MODEL_ANGLE(Double.class),

        POPULATION_DENSITY_M2(Double.class),

        GEOMETRY_COLOR(TColor.class),

        VACANT(Boolean.class),

        PRIVATE_YARD(Boolean.class),

        ;

        private final Class<?> type;

        private BuildingAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return AttributeItem.ZERO;
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

    public enum Detail {

        /**
         * Total cost for constructing this building.
         */
        CONSTRUCTION_COST,
        /**
         * Total cost for demolishing (+buyout) this building.
         */
        DEMOLISH_COST,
        /**
         * Cost for buyout of inhabitants
         */
        BUYOUT_COST,
        /**
         * Total sell price of building.
         */
        SELL_PRICE,
        /**
         * The amount of m2 sellable floor space (e.g. a 3 floor building has 3 x surface size of floorspace).
         */
        SELLABLE_FLOORSPACE_M2,

        /**
         * Number of houses inside this building.
         */
        NUMBER_OF_HOUSES,

        ;

        public static final Detail[] VALUES = Detail.values();

        public boolean isMoneyDetail() {
            return this == CONSTRUCTION_COST || this == DEMOLISH_COST || this == BUYOUT_COST || this == SELL_PRICE;
        }
    }

    public enum Layer {

        BRIDGE(false),

        SURFACE(true),

        UNDERGROUND(true);

        public static final Layer[] VALUES = Layer.values();

        private final boolean terrain;

        private Layer(boolean terrain) {
            this.terrain = terrain;
        }

        public boolean isTerrain() {
            return terrain;
        }
    }

    public enum ModelStyle {

        WIREFRAME("Wireframe"),

        PLAIN("White"),

        COLORED("Wall Color\nfrom Function"),

        TEXTURED("Textured\n(Experimental)"),

        DISCO("Disco\n(For Testing)"),

        ;

        private String fullName;

        private ModelStyle(String fullName) {
            this.fullName = fullName;
        }

        public String getFullname() {
            return fullName;
        }

    }

    public static final String OWNER_ATTRIBUTE = "OWNER";
    public static final String FUNCTION_ATTRIBUTE = "FUNCTION";
    public static final String MEASURE_ID = "MEASUREID";

    private static final String BUILDING_TQL_NAME = MapLink.BUILDINGS.getTQLName();

    public static final double MIN_SIZE_M = 0.1;
    public static final double MIN_DETAILED_AREA_M2 = MIN_SIZE_M * MIN_SIZE_M;
    public static final double MIN_BASIC_AREA_M2 = 1000.0 * MIN_DETAILED_AREA_M2;

    private static final long serialVersionUID = 1763521856771901193L;

    public static final int HEIGHT_ACCURACY = 3;

    @JsonIgnore
    public transient ItemIntersector intersector = null;

    @XMLValue
    @ItemIDField(MapLink.FUNCTIONS)
    private Integer functionID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.MEASURES)
    private int measureID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.UPGRADE_TYPES)
    private int upgradeID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private int upgradeOwnerID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private int demolisherID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private int ownerID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.BUILDINGS)
    private int predecessorID = Item.NONE;

    @XMLValue
    private TimeState state = TimeState.NOTHING;

    private int modelVersion = 0;

    @XMLValue
    private ArrayList<Section> sections = new ArrayList<>();

    @XMLValue
    private Point center = null;

    @XMLValue
    private HashMap<FaceType, float[]> overrideDecals = null;

    @XMLValue
    @ItemIDField(MapLink.ADDRESSES)
    private final ArrayList<Integer> addressIDs = new ArrayList<>();

    @XMLValue
    private HashMap<Integer, Boolean> permitReceived = null;

    @XMLValue
    private ArrayList<Category> overrideCategories = null;

    @XMLValue
    @ItemIDField(MapLink.SOURCES)
    private final ArrayList<Integer> sourceIDs = new ArrayList<>();

    @XMLValue
    @ListOfClass(CGLink.class)
    private ArrayList<CGLink> customGeometries = null;

    public Building() {
    }

    public Building(final Integer functionID, final String name) {
        this();
        this.functionID = functionID;
        this.setName(name);
    }

    public boolean addAddress(Address address) {

        if (!addressIDs.contains(address.getID())) {
            addressIDs.add(address.getID());
            return true;
        }
        return false;
    }

    public void addCustomGeometryID(Integer customGeometryID, boolean primary) {

        if (customGeometries == null) {
            customGeometries = new ArrayList<>(1); // single initial capacity
        }
        customGeometries.add(new CGLink(customGeometryID, primary));
    }

    public Section addSection() {
        int floors = this.getTopFloor();
        Section section = new Section(floors, JTSUtils.EMPTY);
        addSection(section);
        return section;
    }

    public void addSection(Section section) {
        setSectionID(section);
        this.sections.add(section);
    }

    @Override
    public boolean addSource(Integer sourceID) {
        if (getItem(MapLink.SOURCES, sourceID) != null && !this.sourceIDs.contains(sourceID)) {
            sourceIDs.add(sourceID);
            return true;
        }
        return false;
    }

    public void clearCustomGeometry() {

        customGeometries = null;
        for (Section section : sections) {
            section.setRoofGeometry(null);
        }
    }

    public void clearReceivedPermits() {
        permitReceived = null;
    }

    public Address getAddress(Integer id) {
        return this.<Address> getItem(MapLink.ADDRESSES, id);
    }

    public List<Address> getAddresses() {
        return this.<Address> getItems(MapLink.ADDRESSES, getAddressIDs());
    }

    public List<Integer> getAddressIDs() {
        return addressIDs;
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {
        return this.getAttributeArray(mapType, key, true);
    }

    public double[] getAttributeArray(MapType mapType, String key, boolean allowOverride) {

        if (allowOverride && super.hasAttribute(mapType, key)) {
            return super.getAttributeArray(mapType, key);
        }
        // fallback to function, but check if it has the value when categories are overwritten
        if (!hasOverrideCategories() || this.getFunction().hasAttribute(mapType, key)) {
            return this.getFunction().getAttributeArray(mapType, key);
        }

        // fallback on the overwritten categories
        for (Category cat : getCategories()) {
            double[] catValue = cat.getAttributeArray(key);
            if (catValue != null) {
                return catValue;
            }
        }
        return EMPTY;
    }

    public final double getBuildingDetail(final MapType mapType, final Detail detail) {

        if (!isInMap(mapType)) {
            return 0;
        }
        double total = 0;
        for (Section section : sections) {
            total += DetailUtils.getBuildingDetailForM2(this, section.getLotSizeM2(), section.getFloors(), detail, this.isVacant());
        }
        return total;
    }

    @Override
    public List<Category> getCategories() {
        if (overrideCategories == null || overrideCategories.isEmpty()) {
            return this.getFunction().getCategories();
        } else {
            return overrideCategories;
        }
    }

    @Override
    public double getCategoryFraction(Category cat) {

        if (getCategories().size() == 1) {
            return 1.0;
        }
        double sum = 0;
        for (Category someCat : getCategories()) {
            sum += getValue(someCat, CategoryValue.CATEGORY_WEIGHT);
        }
        if (sum == 0) {
            return 0.0;
        }
        return getValue(cat, CategoryValue.CATEGORY_WEIGHT) / sum;
    }

    public final double getCategoryUnits(final MapType mapType, Category cat) {

        if (!isInMap(mapType)) {
            return 0.0;
        }
        double total = 0.0;
        for (Section section : sections) {
            total += DetailUtils.getCategoryUnits(this, cat, section.getLotSizeM2(), section.getFloors());
        }
        return total;
    }

    @Override
    public Point getCenterPoint() {

        if (center == null) { // lazy init
            center = JTSUtils.getCenterPoint(getPolygons(null));
        }
        return center;
    }

    public double getCenterSurfaceM() {

        Point center = getCenterPoint();
        return center != null && JTSUtils.hasZ(center) ? center.getCoordinate().getZ() : -Double.MAX_VALUE;
    }

    public TColor getColor(FaceType type) {
        return TColor.array(getValueArray(type.getColorFunctionValue()));
    }

    public Integer getConstructionYear() {
        Long date = getDateAttribute(BuildingAttribute.CONSTRUCTION_FINISH_DATE);
        if (date != null) {
            return Moment.getYear(date);
        }
        return null;
    }

    public List<CGLink> getCustomGeometries() {
        return customGeometries;
    }

    private final Long getDateAttribute(BuildingAttribute attribute) {
        if (hasAttribute(attribute)) {
            return DateUtils.toTimeMillis(getAttribute(attribute));
        } else {
            return null;
        }
    }

    public float[] getDecals(MapType mapType, FaceType faceType) {
        return getDecals(mapType, faceType, true);
    }

    public float[] getDecals(MapType mapType, FaceType faceType, boolean allowOverride) {

        if (faceType == FaceType.ROOF && getValue(mapType, FunctionValue.SOLAR_PANELS) > 0) {
            return DecalTexture.SOLAR_PANELS_DECAL;

        } else if (hasOverrideDecals(faceType)) {
            return overrideDecals.get(faceType);

        } else {
            return this.getFunction().getDecals(faceType);
        }
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return BuildingAttribute.values();
    }

    public Stakeholder getDemolisher() {
        return getItem(MapLink.STAKEHOLDERS, getDemolisherID());
    }

    public final Integer getDemolisherID() {
        return Integer.valueOf(demolisherID);
    }

    public final Envelope getEnvelope(final MapType mapType) {

        if (!isInMap(mapType)) {
            return JTSUtils.EMPTY.getEnvelopeInternal();
        }
        // optimization for single section buildings
        if (sections.size() == 1) {
            return sections.getFirst().getPolygons().getEnvelopeInternal();
        }
        // combine section polygons into one total envelope
        Envelope total = new Envelope();
        for (int i = 0; i < sections.size(); i++) {
            total.expandToInclude(sections.get(i).getPolygons().getEnvelopeInternal());
        }
        return total;
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {

        Map<String, Object> map = super.getExportAttributes(inherited);
        if (inherited) {
            addInheritedAttributes(map, getFunction());
            for (Category category : getCategories()) {
                for (CategoryValue value : CategoryValue.VALUES) {
                    String key = category.getAttributeKey(value);
                    map.putIfAbsent(key, getValueArray(MapType.CURRENT, value));
                }
            }
            for (FunctionValue functionValue : FunctionValue.ACTIVE_VALUES) {
                map.putIfAbsent(functionValue.name(), getValueArray(MapType.CURRENT, functionValue));
            }
        }
        map.put(FUNCTION_ATTRIBUTE, this.getFunction().getName());
        if (this.getOwner() != null) {
            map.put(OWNER_ATTRIBUTE, this.getOwner().getName());
        }
        if (isPartOfMeasure()) {
            map.put(MEASURE_ID, getMeasureID());
        }
        // set default floors on export based on top floor
        map.put(FunctionValue.DEFAULT_FLOORS.name(), (double) getTopFloor());
        map.put(FunctionValue.FLOOR_HEIGHT_M.name(), getValueArray(MapType.CURRENT, FunctionValue.FLOOR_HEIGHT_M));
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        return getPolygons(isPartOfMeasure() ? null : getDefaultMap());
    }

    public Section getFirstSection() {
        return sections.stream().findFirst().orElse(null);
    }

    public final double getFloorSizeM2(final MapType mapType) {

        if (!isInMap(mapType)) {
            return 0;
        }
        double total = 0;
        for (Section section : sections) {
            total += section.getLotSizeM2() * section.getFloors();
        }
        return total;
    }

    public double getFoliageCrownFactor() {

        if (getCategories().contains(Category.NATURE)) {
            return getValue(FunctionValue.FOLIAGE_CROWN_FACTOR);
        } else {
            return -1;
        }
    }

    public Function getFunction() {
        return this.getItem(MapLink.FUNCTIONS, functionID);
    }

    public Integer getFunctionID() {
        return functionID;
    }

    public final double getHeightM() {
        return getHeightM(false, false);
    }

    public double getHeightM(boolean includeRoof, boolean includeFurniture) {
        return getHeightM(null, includeRoof, includeFurniture);
    }

    public double getHeightM(Section section, boolean includeRoof, boolean includeFurniture) {

        double floors = section == null ? this.getTopFloor() : section.getFloors();
        double floorsHeight = this.getValue(FunctionValue.FLOOR_HEIGHT_M) * floors;

        if (includeRoof) {
            // add slanting roof
            floorsHeight += this.getSlantingRoofHeightM(section);
        }

        if (includeFurniture) {
            // check furniture
            double roofHeight = 0;
            for (ModelData model : this.getFunction().getModels()) {
                if (roofHeight < model.getModelHeightM()) {
                    roofHeight = model.getModelHeightM();
                }
            }
            floorsHeight += roofHeight;
        }

        // combine and return
        return floorsHeight;
    }

    @Override
    public String getImageLocation() {
        return this.getFunction().getImageLocation();
    }

    @Override
    public GeometryCollection getIntersectorGeometies(MapType mapType) {
        return this.getPolygons(mapType);
    }

    @Override
    public Building.Layer getLayer() {
        return getCategories().getFirst().getLayer();
    }

    public final double getLotSizeM2(final MapType mapType) {

        if (!isInMap(mapType)) {
            return 0;
        }
        double total = 0;
        for (Section section : sections) {
            total += section.getLotSizeM2();
        }
        return total;
    }

    public int getLowestTopFloor() {

        int low = Function.MAX_ALLOWED_FLOORS;
        for (Section section : this.sections) {
            if (section.getFloors() < low) {
                low = section.getFloors();
            }
        }
        return low;
    }

    public final int getMaxFloors() {
        return (int) this.getValue(FunctionValue.MAX_FLOORS);
    }

    /**
     * @return the measure
     */
    public final MapMeasure getMeasure() {
        return this.getItem(MapLink.MEASURES, getMeasureID());
    }

    public final Integer getMeasureID() {
        return Integer.valueOf(measureID);
    }

    public final int getMinFloors() {
        return (int) this.getValue(FunctionValue.MIN_FLOORS);
    }

    public List<ModelData> getModels(MapType mapType, Section section) {
        return this.getItems(MapLink.MODEL_DATAS, getModelSet(mapType, section).getModelIDs());
    }

    public ModelSet getModelSet(MapType mapType, Section section) {

        // optional solar panel upgrade for flat roofs and when original solar panel function
        if (getSlantingRoofHeightM(section) <= 0.0 && getValue(mapType, FunctionValue.ALIGN_ELEVATION) != Function.ALIGN_SURFACE
                && getValue(mapType, FunctionValue.SOLAR_PANELS) > 0.0) {
            return getItem(MapLink.MODEL_SETS, ModelSet.SOLAR_PANELS_ID);
        }
        // fallback to default
        return getFunction().getModelSet();
    }

    public int getModelVersion() {
        return modelVersion;
    }

    public final MultiPolygon getMultiPolygon(final Section section, final MapType mapType) {
        return isInMap(mapType) ? section.getPolygons() : JTSUtils.EMPTY;
    }

    @Override
    public String getMyParam() {
        return BUILDING_TQL_NAME;
    }

    public final Stakeholder getOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getOwnerID());
    }

    public final Integer getOwnerID() {

        Measure measure = this.getMeasure();
        if (measure != null && !this.isUpgraded()) {
            return measure.getOwnerID();
        }
        return Integer.valueOf(ownerID);
    }

    public List<Integer> getPermitters() {
        if (permitReceived == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(permitReceived.keySet());
    }

    public final GeometryCollection getPolygons(final MapType mapType) {

        if (!isInMap(mapType)) {
            return JTSUtils.EMPTY;
        }
        // optimization for single section buildings
        if (sections.size() == 1) {
            return sections.getFirst().getPolygons();
        }
        // combine section multipolygons into collection
        Geometry[] mps = new Geometry[sections.size()];
        for (int i = 0; i < sections.size(); i++) {
            mps[i] = sections.get(i).getPolygons();
        }
        return JTSUtils.createCollection(mps);
    }

    public Building getPredecessor() {
        return this.getItem(MapLink.BUILDINGS, getPredecessorID());
    }

    public Integer getPredecessorID() {
        return Integer.valueOf(predecessorID);
    }

    public Category getPrimaryCategory() {
        return getCategories().get(0); // first is primary by definition
    }

    @Override
    public final MultiPolygon[] getQTGeometries() {

        MultiPolygon[] mps = new MultiPolygon[sections.size()];
        for (int i = 0; i < sections.size(); i++) {
            mps[i] = sections.get(i).getPolygons();
        }
        return mps;
    }

    @Override
    public Integer getRelationID(Relation relation) {

        if (relation == Relation.OWNER || relation == Relation.CONSTRUCTOR) {
            return getOwnerID();
        } else if (relation == Relation.DEMOLISHER) {
            return getDemolisherID();
        }
        return Item.NONE;
    }

    public Section getSection(Integer sectionID) {

        if (sectionID == null || Item.NONE.equals(sectionID)) {
            return null;
        }
        for (Section section : sections) {
            if (section.getID().equals(sectionID)) {
                return section;
            }
        }
        return null;
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public double getSlantingRoofHeightM(Section section) {

        // first check section override
        if (section != null && section.getSlantingRoofHeightM() != null) {
            return section.getSlantingRoofHeightM();
        }
        // fallback to function value
        return this.getValue(FunctionValue.SLANTING_ROOF_HEIGHT);
    }

    @Override
    public final List<Source> getSources() {
        return this.getItems(MapLink.SOURCES, sourceIDs);
    }

    public String getTexture(FaceType faceType) {
        return getFunction().getTexture(faceType);
    }

    @Override
    public final TimeState getTimeState() {
        return isPartOfMeasure() ? getMeasure().getTimeState() : this.state;
    }

    public int getTopFloor() {

        int top = Function.MIN_ALLOWED_FLOORS;
        for (Section section : this.sections) {
            if (section.getFloors() > top) {
                top = section.getFloors();
            }
        }
        return top;
    }

    public double getTrafficHour(TrafficType type, int hour) {
        double[] array = getValueArray(type.getNumValue());
        return array.length == 1 ? array[0] : hour < array.length ? array[hour] : 0;
    }

    /**
     * Average amount of total traffic during the day
     */
    public double getTrafficTotal() {

        double sum = 0.0;
        for (TrafficType type : TrafficType.VALUES) {
            sum += MathUtils.avg(getValueArray(type.getNumValue()));
        }
        return sum;
    }

    public final String getUniqueFunctionName() {
        return getFunction().getName() + " (" + getID() + ")";
    }

    public final UpgradeType getUpgrade() {
        return this.getItem(MapLink.UPGRADE_TYPES, this.getUpgradeID());
    }

    public final Integer getUpgradeID() {
        return Integer.valueOf(upgradeID);
    }

    public final Stakeholder getUpgradeOwner() {
        return this.getItem(MapLink.STAKEHOLDERS, this.getUpgradeOwnerID());
    }

    public final Integer getUpgradeOwnerID() {
        return Integer.valueOf(upgradeOwnerID);
    }

    @Override
    public double getValue(Category cat, CategoryValue val) {
        return getValue(getDefaultMap(), cat, val);
    }

    @Override
    public double getValue(MapType mapType, Category cat, CategoryValue val) {
        return getAttribute(mapType, cat.getAttributeKey(val));
    }

    @Override
    public double getValue(MapType mapType, Value key) {

        if (key instanceof FunctionValue) {
            return getAttribute(mapType, key.name());

        } else {
            double value = 0.0;
            for (Category cat : getCategories()) {
                value += getValue(mapType, cat, (CategoryValue) key) * getCategoryFraction(cat);
            }
            return value;
        }
    }

    @Override
    public double getValue(Value key) {
        return getValue(getDefaultMap(), key);
    }

    @Override
    public double[] getValueArray(Category cat, CategoryValue val) {
        return getValueArray(getDefaultMap(), cat, val);
    }

    @Override
    public double[] getValueArray(MapType mapType, Category cat, CategoryValue val) {
        return getAttributeArray(mapType, cat.getAttributeKey(val));
    }

    @Override
    public double[] getValueArray(MapType mapType, Value key) {

        if (key instanceof FunctionValue) {
            return getAttributeArray(mapType, key.name());

        } else {
            // note: always single value for now
            double[] value = new double[] { 0.0 };
            for (Category cat : getCategories()) {
                value[0] += getValue(mapType, cat, (CategoryValue) key) * getCategoryFraction(cat);
            }
            return value;
        }
    }

    @Override
    public double[] getValueArray(Value key) {
        return getValueArray(getDefaultMap(), key);
    }

    public TColor getWallColor() {
        return this.getColor(FaceType.EXTRA);
    }

    public final boolean handleChangedValue(Category category, String attribute) {

        CategoryValue categoryValue = category.getCategoryValue(attribute);
        if (categoryValue == null) {
            return false;
        }

        double[] values = getAttributeArray(attribute);

        boolean hasParent = this.getFunction().getCategories().contains(category);

        if (hasParent && Arrays.equals(values, getFunction().getValueArray(category, categoryValue))) {
            this.removeOverrideValue(category, categoryValue);
        }

        // maybe add new category
        if (!this.getCategories().contains(category)) {
            // load in old categories
            this.overrideCategories = new ArrayList<>(this.getCategories());
            // add new one
            this.overrideCategories.add(category);
        }
        return true;
    }

    public final void handleChangedValue(MapType mapType, FunctionValue value) {

        double[] values = getAttributeArray(value);
        if (mapType != MapType.MAQUETTE && Arrays.equals(values, getFunction().getValueArray(value))) {
            this.removeOverrideValue(value);
        }

        // update skeletons
        if (value == FunctionValue.SLANTING_ROOF_HEIGHT) {
            this.updateSkeletons();
        }
    }

    public boolean hasAddress() {
        return addressIDs.size() > 0;
    }

    public boolean hasAddress(Integer addressID) {
        return addressIDs.contains(addressID);
    }

    @Override
    public boolean hasAttribute(MapType mapType, String key) {

        if (super.hasAttribute(mapType, key)) {
            return true;
        }
        // fallback to function
        return this.getFunction().hasAttribute(mapType, key);
    }

    public final boolean hasCarTraffic() {

        for (TrafficType type : TrafficType.CAR_TYPES) {
            if (getValue(type.getNumValue()) > 0.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * When true building has custom shape for visualization
     * @return
     */
    public boolean hasCustomGeometry() {
        return customGeometries != null && !customGeometries.isEmpty();
    }

    public boolean hasCustomGeometry(Integer customGeometryID) {

        if (customGeometries != null) {
            for (int i = 0; i < customGeometries.size(); i++) {
                if (customGeometries.get(i).getGeometryID().equals(customGeometryID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * When true maqiuette and current have different 3d model visualizations
     */
    public boolean hasDifferentModels() {

        Collection<String> maqAttributes = getMaquetteAttributes();
        if (maqAttributes == null) {
            return false;
        }

        // search for changed model updating attributes
        for (String maqAttribute : maqAttributes) {
            FunctionValue maqValue = FunctionValue.VALUE_MAP.get(maqAttribute);
            if (maqValue != null && maqValue.isModelUpdate()
                    && getValue(MapType.CURRENT, maqValue) != getValue(MapType.MAQUETTE, maqValue)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOverrideCategories() {
        return overrideCategories != null && !overrideCategories.isEmpty();
    }

    public boolean hasOverrideDecals(FaceType faceType) {
        return overrideDecals != null && overrideDecals.containsKey(faceType);
    }

    public boolean hasPolygons(MapType mapType) {
        if (!isInMap(mapType)) {
            return false;
        }
        // optimization for single section buildings
        if (sections.size() == 1) {
            return !JTSUtils.isEmpty(sections.getFirst().getPolygons());
        }
        // combine section multipolygons into collection
        for (int i = 0; i < sections.size(); i++) {
            if (!JTSUtils.isEmpty(sections.get(i).getPolygons())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAttributeInherited(MapType mapType, String attribute) {
        return this.getFunction().hasAttribute(mapType, attribute) && !super.hasAttribute(mapType, attribute);
    }

    public boolean isAttributeOverride(String attribute) {
        return isAttributeOverride(attribute, getAttributeArray(getDefaultMap(), attribute, true));
    }

    @Override
    public boolean isAttributeOverride(String attribute, double[] values) {
        return !Arrays.equals(getAttributeArray(getDefaultMap(), attribute, false), values);
    }

    @Override
    public boolean isAttributeRemovable(String attribute) {
        return super.hasAttribute(getDefaultMap(), attribute);
    }

    @Override
    public boolean isAttributeResettable(MapType mapType, String attribute) {
        return super.hasAttribute(mapType, attribute) && this.getFunction().hasAttribute(mapType, attribute);
    }

    public boolean isBothMapsActive() {
        return getTimeState() == TimeState.READY || getTimeState() == TimeState.CONSTRUCTING;
    }

    public final boolean isBridge() {
        return this.getCategories().contains(Category.BRIDGE);
    }

    public final boolean isCovers(MapType mapType, int rasterID, int rasters, double cellM, Geometry superCell, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isCovers(mapType, cellM, superCell, cacheID, rasterID);
    }

    public boolean isFloating() {
        return this.getValue(FunctionValue.ALIGN_ELEVATION) == Function.ALIGN_FLOATING;
    }

    public boolean isGarden() {
        return getCategories().contains(Category.GARDEN);
    }

    public boolean isInMap(MapType mapType) {
        // XXX Discuss initialization issue for buildings with measures and the createPolygonTree method.
        return mapType == null || this.getTimeState().isInMap(mapType);
    }

    public final boolean isIntersect(MapType mapType, int rasterID, int rasters, double cellM, Geometry cell, Point center, int cacheID) {

        intersector = ItemIntersector.init(intersector, this, cacheID, rasters);
        return intersector.isIntersect(mapType, cellM, cell, center, cacheID, rasterID);
    }

    public final boolean isPartOfMeasure() {
        return measureID != Item.NONE.intValue();
    }

    public Boolean isPermitted() {

        if (permitReceived != null) {
            for (Entry<Integer, Boolean> permitEntry : permitReceived.entrySet()) {
                if (Boolean.FALSE.equals(permitEntry.getValue())) {
                    return permitEntry.getValue();
                }
            }

            // first check for unanswered
            for (Entry<Integer, Boolean> permitEntry : permitReceived.entrySet()) {
                if (permitEntry.getValue() == null) {
                    return null;
                }
            }
        }
        return true;
    }

    public final boolean isResidential() {
        return getCategories().stream().anyMatch(c -> c.isResidential());
    }

    /**
     * When true building is of category type road/intersection or bridge.
     * @return
     */
    public final boolean isRoadSystem() {
        return getFunction().isRoadSystem();
    }

    /**
     * When true building is ALWAYS single floor
     */
    public final boolean isSingleFloor() {
        return getMinFloors() == 1 && getMaxFloors() == 1;
    }

    /**
     * True when building envelope is smaller then a grid cell's width or height
     */
    public final boolean isSmallIntersector(MapType mapType, double cellSizeM) {
        return intersector == null || intersector.isSmall(mapType, cellSizeM);
    }

    public final boolean isSolid() {
        return this.getValue(FunctionValue.SOLID) > 0.0;
    }

    /**
     * True when surface building (not underground) and is solid (attribute)
     */
    public final boolean isSurfaceIntersector(MapType mapType, double cellSizeM) {
        return intersector != null ? intersector.isSurface(mapType, cellSizeM) : getLayer() != Layer.UNDERGROUND && isSolid();
    }

    public final boolean isUpgraded() {
        return !Item.NONE.equals(this.getUpgradeID());
    }

    public final boolean isVacant() {
        return this.getAttribute(BuildingAttribute.VACANT) > 0.0;
    }

    public boolean isValueOverride(Category category, CategoryValue value) {
        String catKey = category.getAttributeKey(value);
        return this.isAttributeOverride(catKey);
    }

    public boolean isValueOverride(FunctionValue value) {
        return this.isAttributeOverride(value.name());
    }

    public boolean isWalledModel() {
        return this.getFunction().isWalledModel();
    }

    public boolean isZoningPermitRequired() {

        boolean permit = this.getValue(FunctionValue.ZONING_PERMIT_REQUIRED) > 0.0;
        if (permit && getUpgrade() != null) {
            return getUpgrade().isZoningPermitRequired();
        }
        return permit;
    }

    public boolean removeAddress(Address address) {
        return addressIDs.remove(address.getID());
    }

    public void removeAllAddresses() {
        addressIDs.clear();
    }

    public void removeOverrideCategories() {

        for (Category cat : Category.VALUES) {
            for (CategoryValue value : CategoryValue.VALUES) {
                String catkey = cat.getAttributeKey(value);
                this.removeAttribute(catkey, true);
            }
        }
    }

    public boolean removeOverrideValue(Category cat, CategoryValue key) {
        String catKey = cat.getAttributeKey(key);
        return this.removeAttribute(catKey, true);
    }

    public boolean removeOverrideValue(FunctionValue key) {
        return this.removeAttribute(key.name(), true);
    }

    public Section removeSection(Integer sectionID) {
        Section section = getSection(sectionID);
        if (section == null) {
            return null;
        }

        sections.remove(section);
        return section;
    }

    @Override
    public void reset() {
        super.reset();
        for (Section section : sections) {
            section.reset();
        }
    }

    public boolean setAllSectionFloors(int floors) {
        // make sure it fits
        if (!this.validAmountOfFloors(floors)) {
            return false;
        }
        if (sections.isEmpty()) {
            addSection(new Section());// add default empty section
        }
        for (Section section : sections) {
            section.setFloors(floors);
        }
        return true;
    }

    public void setAllSlantingRoofHeightsM(double slantingRoofHeightM) {

        if (sections.isEmpty()) {
            addSection(new Section());// add default empty section
        }
        for (Section section : sections) {
            section.setSlantingRoofHeightM(this, slantingRoofHeightM);
        }
    }

    public boolean setCenterHeight(double heightM) {
        return JTSUtils.setZ(getCenterPoint(), heightM);
    }

    public final void setDemolisherID(Integer demolisherID) {
        this.demolisherID = demolisherID == null ? Item.NONE : demolisherID.intValue();
    }

    public void setFunction(Function function) {
        this.setFunctionID(function.getID(), true);
    }

    public void setFunctionID(Integer functionID) {
        setFunctionID(functionID, true);
    }

    public void setFunctionID(Integer functionID, boolean updateSkeleton) {
        this.functionID = functionID;
        if (updateSkeleton) {
            this.updateSkeletons();
        }
    }

    @Override
    public void setLord(final Lord lord) {
        super.setLord(lord);

        // trim arrays
        sourceIDs.trimToSize();
        sections.trimToSize();
        addressIDs.trimToSize();
    }

    /**
     * @param measure the measure to set
     */
    public final void setMeasure(Integer measureID) {
        this.measureID = measureID == null ? Item.NONE : measureID.intValue();
    }

    /**
     * Depending on the timestate the coordinates are placed in CURRENT or MAQUETTE
     * @param mp
     */
    public final void setMultiPolygon(Integer sectionID, final MultiPolygon mp) {
        setMultiPolygon(sectionID, mp, false);
    }

    /**
     * ONLY call this when you know what you are doing, allows to force override coordinates
     * @param mp
     * @param force
     */
    public final void setMultiPolygon(Integer sectionID, final MultiPolygon mp, boolean force) {
        setMultiPolygon(sectionID, mp, force, true);
    }

    /**
     * ONLY call this when you know what you are doing, allows to force override coordinates
     * @param mp
     * @param force
     */
    public final void setMultiPolygon(Integer sectionID, final MultiPolygon mp, boolean force, boolean updateSkeletons) {

        TimeState state = this.getTimeState();

        if (force || state.before(TimeState.CONSTRUCTING) || state == TimeState.READY || state == TimeState.WAITING_FOR_DEMOLISH_DATE) {

            Section section = getSection(sectionID);
            if (section == null) {
                if (!Item.NONE.equals(sectionID)) {
                    TLogger.severe("Cannot set polygons for missing Section with id " + sectionID + " of Building " + this.getName());
                    return;
                }

                section = sections.isEmpty() ? addSection() : sections.getFirst();
            }
            section.setPolygons(mp);
            center = JTSUtils.getCenterPoint(getPolygons(null));
            if (updateSkeletons) {
                this.updateSkeletons(sectionID);
            }
        } else {
            /**
             * Why would you want this?
             */
            TLogger.warning("Cannot set polygons for building " + this.getName() + ", since timestate is " + state);
        }

    }

    public void setOverrideColor(FaceType faceType, TColor color) {

        // remove override, default is already NULL
        if (color == null) {
            this.removeOverrideValue(faceType.getColorFunctionValue());
            return;
        }

        // set override
        setOverrideValue(faceType.getColorFunctionValue(), color.toArray());
    }

    public void setOverrideDecals(FaceType faceType, float[] decalArray) {

        // remove override, default is already NULL
        if (decalArray == null) {
            if (this.overrideDecals != null) {
                this.overrideDecals.remove(faceType);
            }
            return;
        }
        if (overrideDecals == null) {
            overrideDecals = new HashMap<>();
        }
        // set override
        this.overrideDecals.put(faceType, decalArray);
    }

    public final void setOverrideValue(Category cat, CategoryValue key, double value) {
        setOverrideValue(cat, key, new double[] { value });
    }

    public final void setOverrideValue(Category cat, CategoryValue key, double[] values) {

        boolean hasParent = this.getFunction().getCategories().contains(cat);
        if (hasParent && Arrays.equals(values, getFunction().getValueArray(cat, key))) {
            this.removeOverrideValue(cat, key);
            return;
        }
        // maybe add new category
        if (!this.getCategories().contains(cat)) {
            // load in old categories
            this.overrideCategories = new ArrayList<>(this.getCategories());
            // add new one
            this.overrideCategories.add(cat);
        }
        // store as attribute
        String catKey = cat.getAttributeKey(key);
        this.setAttributeArray(catKey, values);
    }

    public final void setOverrideValue(FunctionValue key, double value) {
        setOverrideValue(key, new double[] { value });
    }

    public final void setOverrideValue(FunctionValue key, double[] values) {

        if (Arrays.equals(values, getFunction().getValueArray(key))) {
            this.removeOverrideValue(key);
        } else {
            this.setAttributeArray(key, values);
        }

        // update skeletons
        if (key == FunctionValue.SLANTING_ROOF_HEIGHT) {
            this.updateSkeletons();
        }
    }

    public final void setOwner(Stakeholder stakeholder) {
        setOwnerID(stakeholder.getID());
    }

    public final void setOwnerID(Integer stakeholderID) {
        ownerID = stakeholderID == null ? Item.NONE : stakeholderID.intValue();
    }

    public void setPermitted(Integer stakeholderID, Boolean permitted) {
        if (permitReceived == null) {
            if (permitted != null) {
                TLogger.warning("Initializing a permit with an answer: " + permitted + " is not allowed!");
                return;
            }
            permitReceived = new HashMap<>();
        }

        if (permitted != null && !permitReceived.containsKey(stakeholderID)) {
            TLogger.warning("Setting a permit for StakeholderID " + stakeholderID
                    + " which is not yet initialized is not allowed to be set to " + permitted + "!");
            return;
        }
        permitReceived.put(stakeholderID, permitted);
    }

    public void setPredecessorID(Integer predecessorID) {
        this.predecessorID = predecessorID == null ? Item.NONE : predecessorID.intValue();
    }

    private void setSectionID(Section section) {

        int maxID = 0;
        for (Section other : sections) {
            maxID = Math.max(maxID, other.getID());
        }
        section.setID(maxID + 1);
    }

    public void setSections(List<Section> sections) {

        this.sections = new ArrayList<>();
        for (Section section : sections) {
            addSection(section);
        }
        // update center
        this.center = JTSUtils.getCenterPoint(getPolygons(null));
    }

    public final void setTimeState(TimeState state) {

        if (this.isPartOfMeasure()) {
            TLogger.severe("Building is part of measure, cannot set time state, this is handled by the measure.");
            return;
        }
        this.state = state;
    }

    public void setUpgrade(Integer upgradeOwnerID, Integer upgradeID) {
        this.upgradeOwnerID = upgradeOwnerID == null ? Item.NONE : upgradeOwnerID.intValue();
        this.upgradeID = upgradeID == null ? Item.NONE : upgradeID.intValue();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    protected void updateInternalVersion(int version) {
        this.modelVersion = version;
    }

    public void updateSkeletons() {
        updateSkeletons(Item.NONE, true);
    }

    public void updateSkeletons(boolean force) {
        updateSkeletons(Item.NONE, force);
    }

    public void updateSkeletons(Integer sectionID) {
        updateSkeletons(sectionID, true);
    }

    public void updateSkeletons(Integer sectionID, boolean force) {

        if (sections.isEmpty() || this.hasCustomGeometry()) {
            return;
        }

        // roads, (underground) parking garages, metro lines
        if (isRoadSystem() || hasCarTraffic()) {
            for (Section section : this.sections) {
                if (section.getID().equals(sectionID) || Item.NONE.equals(sectionID)) {
                    if (force || !section.hasSkeletonLines()) {
                        section.updateTopLineSkeleton(true);
                    }
                }
            }
        } else if (getLayer() == Layer.UNDERGROUND) { // underground (not road): culverts must be very accurate thus do not simplify these
            for (Section section : this.sections) {
                if (section.getID().equals(sectionID) || Item.NONE.equals(sectionID)) {
                    if (force || !section.hasSkeletonLines()) {
                        section.updateTopLineSkeleton(false);
                    }
                }
            }
        } else if (getLayer() == Layer.SURFACE && isWalledModel()) { // roofs for walled surface buildings
            for (Section section : this.sections) {
                if (section.getID().equals(sectionID) || Item.NONE.equals(sectionID)) {
                    if (getSlantingRoofHeightM(section) > 0.0) {
                        if (force || !section.hasSkeletonLines()) {
                            section.updateRoofSkeleton(this);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if given amount of floors is allowed for this function.
     * @param floors
     * @return
     */
    public final boolean validAmountOfFloors(int floors) {

        if (floors < this.getValue(FunctionValue.MIN_FLOORS) || floors > this.getValue(FunctionValue.MAX_FLOORS)) {
            TLogger.warning(floors + " is an invalid amount for function " + this.getName() + ", it needs a value in range: "
                    + this.getValue(FunctionValue.MIN_FLOORS) + "-" + this.getValue(FunctionValue.MAX_FLOORS));
            return false;
        }
        return true;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        if (this.getFunction() == null) {
            return result + "\nBuilding " + this + " has no Function!";
        }

        for (Section section : sections) {
            if (Item.NONE.equals(section.getID())) {
                setSectionID(section);
            }
            if (!section.hasSkeletonLines()) {
                this.updateSkeletons(section.getID());
            }
        }

        if (!startSession) {
            if (Item.NONE.equals(this.getDemolisherID()) && TimeState.WAITING_FOR_DEMOLISH_DATE.beforeOrEqualTo(this.state)) {
                // if loaded: being demolished and demolisher is unknown, set demolisher to the owner for now
                this.setDemolisherID(this.getOwnerID());
            }
        }

        if (hasAttribute("WATER_EVAPORATION_FACTOR") && !hasAttribute(FunctionValue.WATER_TRANSPIRATION_FACTOR)) {
            setAttributeArray(FunctionValue.WATER_TRANSPIRATION_FACTOR, getAttributeArray("WATER_EVAPORATION_FACTOR"));
            this.removeAttribute("WATER_EVAPORATION_FACTOR");
            TLogger.warning(getName() + " Converted WATER_EVAPORATION_FACTOR -> " + FunctionValue.WATER_TRANSPIRATION_FACTOR);
        }
        return result;
    }
}
