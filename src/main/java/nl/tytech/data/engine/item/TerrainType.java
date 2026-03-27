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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.other.LayerItem;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * TerrainType: This class keeps track of the terrain types.
 *
 * @author Frank Baars
 */
public class TerrainType extends AttributeItem implements LayerItem {

    public enum TerrainAttribute implements ReservedAttribute {

        ANGLE_OF_REPOSE(Double.class),

        WATER_DEPTH_M(Double.class),

        BOWEN_RATIO(Double.class),

        BUILDABLE(Boolean.class),

        SUBSIDENCE(Boolean.class),

        COLOR(TColor.class),

        HEAT_EFFECT(Double.class),

        LIVABILITY_EFFECT(Double.class),

        TERRAIN_MIX(Double.class),

        TEXTURE_TYPE(Double.class),

        WATER(WaterType.class),

        GROUND_INFILTRATION_MD(Double.class),

        /**
         * K
         */
        HYDRAULIC_CONDUCTIVITY_MD(Double.class),

        /**
         * KD
         */
        HYDRAULIC_CONDUCTIVITY_WITH_THICKNESS_MD(Double.class),

        VAN_GENUCHTEN_M(Double.class),

        ROOT_DEPTH_M(Double.class),

        VEGETATION_FRACTION(Double.class),

        /// TODO: Rename to fraction, or define and use it as an actual percentage
        WATER_STORAGE_PERCENTAGE(Double.class),

        WATER_MANNING(Double.class),

        WATER_MICRORELIEF_M(Double.class),

        WATER_TRANSPIRATION_FACTOR(Double.class),

        ;

        public static final TerrainAttribute[] VALUES = TerrainAttribute.values();

        private final Class<?> type;

        private TerrainAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return switch (this) {
                case ANGLE_OF_REPOSE -> new double[] { 45d };
                default -> AttributeItem.ZERO;
            };
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

    public enum WaterType {

        NONE(0), //
        WATERWAY(1), // river, channel, creek. (rivier, kanaal, beek, sloot, gracht)
        WATERBODY(2), // lake, pool, pond. (meer, plas, ven, vijver)
        SEA(3);// stretching salt water body

        public static final WaterType[] VALUES = values();

        public static WaterType getType(double value) {
            for (WaterType type : VALUES) {
                if (type.value == value) {
                    return type;
                }
            }
            return WaterType.NONE;
        }

        private double value;

        private WaterType(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    public static final Integer OPENLAND_TERRAIN_ID = 0;

    public static final Integer GRASSLAND_TERRAIN_ID = 1;

    public static final Integer WATER_TERRAIN_ID = 3;

    public static final Integer SEA_TERRAIN_ID = 19;

    public static final Integer UNKNOWN_UNDERGROUND_ID = 9;

    public static final float DEFAULT_WATER_DEPTH = 3.0f;

    private static final long serialVersionUID = 7584336450411707578L;

    @XMLValue
    private String imageName = StringUtils.EMPTY;

    @XMLValue
    private String code = StringUtils.EMPTY;

    @XMLValue
    @ItemIDField(MapLink.TERRAIN_TYPES)
    private Integer parentID = Item.NONE;

    @XMLValue
    private Layer groundLayerType = Layer.SURFACE;

    public TerrainType() {

    }

    public TerrainType(Layer groundLayerType) {
        this.groundLayerType = groundLayerType;
    }

    public TerrainType(String name) {
        setName(name);
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String attribute) {
        return getAttributeArray(mapType, attribute, true, true);
    }

    private double[] getAttributeArray(MapType mapType, String attribute, boolean allowOverride, boolean allowParent) {

        // Try override first
        if (allowOverride) {
            TerrainTypeOverride override = this.getOverride();
            if (override != null && override.hasAttribute(mapType, attribute)) {
                return override.getAttributeArray(mapType, attribute);
            }
        }

        if (allowParent && this.getLord() != null && !super.hasAttribute(mapType, attribute)) {
            TerrainType parent = getParent();
            if (parent != null && parent.hasAttribute(mapType, attribute)) {
                return parent.getAttributeArray(mapType, attribute);
            }
        }

        return super.getAttributeArray(mapType, attribute);
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {

        Collection<String> attributes = super.getAttributes(mapType);

        // Try override next
        if (this.getLord() != null) {
            Set<String> combined = null;
            TerrainType parent = getParent();
            if (parent != null) {
                combined = new HashSet<>();
                combined.addAll(parent.getAttributes(mapType));
                combined.addAll(attributes);
            }
            TerrainTypeOverride override = this.getOverride();
            if (override != null) {
                Collection<String> overrideAttributes = override.getAttributes(mapType);
                if (overrideAttributes != null) {
                    // combine, with override added last
                    if (combined == null) {
                        combined = new HashSet<>();
                        combined.addAll(attributes);
                    }
                    combined.addAll(overrideAttributes);
                }
            }

            if (combined != null) {
                attributes = combined;
            }
        }
        return attributes;
    }

    public String getCode() {
        return code;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return TerrainAttribute.VALUES;
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        if (inherited) {
            addInheritedAttributes(map, getParent());
        }
        return map;
    }

    @Override
    public Layer getLayer() {
        return groundLayerType;
    }

    private final TerrainTypeOverride getOverride() {
        return getLord() != null ? getItem(MapLink.TERRAIN_TYPE_OVERRIDES, getID()) : null;
    }

    public final String getOverrideCode() {

        // Try override first
        TerrainTypeOverride override = this.getOverride();
        if (override != null && StringUtils.containsData(override.getCode())) {
            return override.getCode();
        }
        return getCode();
    }

    public final String getOverrideName() {

        // Try override first
        TerrainTypeOverride override = this.getOverride();
        if (override != null && StringUtils.containsData(override.getName())) {
            return override.getName();
        }
        return getName();
    }

    public TerrainType getParent() {
        return getItem(MapLink.TERRAIN_TYPES, parentID);
    }

    public Integer getParentID() {
        return parentID;
    }

    public WaterType getWaterType() {

        if (getLayer() == Layer.SURFACE) {
            return WaterType.getType(getAttribute(TerrainAttribute.WATER));
        } else {
            return WaterType.NONE;
        }
    }

    @Override
    public boolean hasAttribute(MapType mapType, String key) {
        return hasAttribute(mapType, key, true, true);
    }

    public final boolean hasAttribute(MapType mapType, String key, boolean allowParent, boolean allowOverride) {

        if (super.hasAttribute(mapType, key)) {
            return true;
        }
        if (allowOverride && hasOverrideAttribute(mapType, key)) {
            return true;
        }
        if (allowParent && hasParentAttribute(mapType, key)) {
            return true;
        }
        return false;
    }

    private final boolean hasOverrideAttribute(MapType mapType, String attribute) {
        TerrainTypeOverride override = this.getOverride();
        return override != null && override.hasAttribute(mapType, attribute);
    }

    public final boolean hasOverrideCode() {
        TerrainTypeOverride override = this.getOverride();
        return override != null && StringUtils.containsData(override.getCode());
    }

    public final boolean hasOverrideName() {
        TerrainTypeOverride override = this.getOverride();
        return override != null && !getName().equals(override.getName());
    }

    /**
     * Returns true when this TerrainType has a lord, a parent and the parent has the attribute.
     * @param mapType
     * @param attribute
     * @return
     */
    protected boolean hasParentAttribute(MapType mapType, String attribute) {
        if (this.getLord() != null) {
            TerrainType parent = getParent();
            if (parent != null && parent.hasAttribute(mapType, attribute)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAttributeInherited(String attribute) {
        return hasAttribute(getDefaultMap(), attribute, false, true) != hasAttribute(getDefaultMap(), attribute, true, true);
    }

    public boolean isAttributeOverride(String attribute) {
        return isAttributeOverride(attribute, getAttributeArray(getDefaultMap(), attribute, true, true));
    }

    @Override
    public boolean isAttributeOverride(String attribute, double[] values) {
        return !Arrays.equals(getAttributeArray(getDefaultMap(), attribute, false, true), values);
    }

    @Override
    public boolean isAttributeRemovable(String attribute) {
        return this.hasOverrideAttribute(getDefaultMap(), attribute);
    }

    @Override
    public boolean isAttributeResettable(MapType mapType, String attribute) {
        return hasOverrideAttribute(mapType, attribute)
                && (hasAttribute(mapType, attribute, false, false) || hasParentAttribute(mapType, attribute));
    }

    public boolean isBuildable() {
        return this.getAttribute(TerrainAttribute.BUILDABLE) > AttributeItem.DEFAULT_VALUE;
    }

    public boolean isWater() {
        return getWaterType() != WaterType.NONE;
    }

    public boolean isWaterVisual() {
        return isWater() || this.getName().toLowerCase().contains("water");
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    @Override
    public String toString() {
        return getOverrideName();
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        if (hasAttribute("WATER_EVAPORATION_FACTOR") && !hasAttribute(TerrainAttribute.WATER_TRANSPIRATION_FACTOR)) {
            setAttributeArray(TerrainAttribute.WATER_TRANSPIRATION_FACTOR, getAttributeArray("WATER_EVAPORATION_FACTOR"));
            this.removeAttribute("WATER_EVAPORATION_FACTOR", true);
            TLogger.warning(getName() + " Converted WATER_EVAPORATION_FACTOR -> " + TerrainAttribute.WATER_TRANSPIRATION_FACTOR);
        }
        return result;
    }
}
