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
package nl.tytech.data.core.item;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Geometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.structure.ItemNamespace;
import nl.tytech.core.structure.ItemNamespace.Filter;
import nl.tytech.data.core.other.IndexSortedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Item: This class is an abstract class for items sent through the network. All items are versioned, serializable and cloneable. They also
 * have an ID. The ID is the location of the item in a list.
 *
 * @author Maxim Knepfle
 */
public abstract class Item implements Serializable, Comparable<Item> {

    public enum Timing {
        BEFORE, AFTER
    }

    /**
     * Comporator to sort items on ID.
     */
    public static final Comparator<Item> ID_SORT = (i1, i2) -> i1.id.compareTo(i2.id);

    /**
     * Comporator to sort items on ID inverse.
     */
    public static final Comparator<Item> INVERSE_ID_SORT = (i1, i2) -> i2.id.compareTo(i1.id);

    /**
     * Sort on index and otherwise alphabetic
     */
    public static final Comparator<IndexSortedItem> INDEX_NAME_SORT = (a, b) -> {

        int sort = Integer.compare(a.getSortIndex(), b.getSortIndex());
        if (sort == 0) {
            sort = ObjectUtils.ALPHANUMERICAL_ORDER.compare(a, b);
        }
        return sort;
    };

    /**
     * Item with an ID larger or equal to this one are session specific.
     */
    public static final int SPECIFIC_START_ID = 1000000;

    private static final long serialVersionUID = 1769699526703175911L;

    public static final int X = 0;

    public static final int Y = 1;

    public static final int Z = 2;

    public static final int W = 3;

    /**
     * NONE is used for multiple application.
     */
    public static final Integer NONE = -1;

    /**
     * ID attribute name
     */
    public static final String ID = "ID";

    /**
     * MapLink attribute name
     */
    public static final String LINK = "link";

    protected static final void addInheritedAttributes(Map<String, Object> exportMap, Item item) {
        if (item == null) { // for flexibility on optional parents and relations, please keep check
            return;
        }
        Map<String, Object> inheritedMap = item.getExportAttributes(true);
        for (Entry<String, Object> entry : inheritedMap.entrySet()) {
            exportMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
    }

    /**
     * The items ID, also its location number in a list.
     */
    private Integer id = NONE;

    /**
     * The lord is the master containing all lists. With the Lord an items can ask for another item in another list.
     */
    @JsonIgnore
    private transient Lord lord = null;

    /**
     * Version number in the session. Each time the items is changed the items gets a new version number.
     */
    private int version = 0;

    @Override
    public int compareTo(Item other) {

        // when null value or no toString or equal, return same 0.
        if (other == null || this.toString() == null || other.toString() == null || equals(other)) {
            return 0;
        }
        // compare based on the toString
        return this.toString().compareTo(other.toString());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Item other && this.getClass() == other.getClass() && this.getID().equals(other.getID());
    }

    public MapType getDefaultMap() {
        return lord == null ? MapType.CURRENT : lord.getDefaultMap();
    }

    public final <I extends EnumOrderedItem<T>, T extends Enum<T>> List<I> getEnumItems(final MapLink mapLink,
            final Collection<T> requestEnums) {

        ItemMap<I> itemMap = this.getMap(mapLink);
        return itemMap != null ? itemMap.getEnumItems(requestEnums) : null;
    }

    /**
     * Export of Item relevant attributes
     */
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = new TreeMap<>();
        map.put(ID, this.getID());
        return map;
    }

    /**
     * Export of optional Item Polygon
     */
    public Geometry getExportGeometry() {
        return null;
    }

    /**
     * Return item ID
     *
     * @return the ID
     */
    public final Integer getID() {
        return id;
    }

    /**
     * Using the lord an item can get an item from another list. Note that you must cast the Item to the desired object. Using getList this
     * can be avoided.
     *
     * @param controlType List type
     * @param id Item ID.
     * @return The requested Item.
     */
    public final <I extends EnumOrderedItem<G>, G extends Enum<G>> I getItem(MapLink eventType, final G type) {

        if (type == null) {
            // okay!
            return null;
        }
        // requested map
        ItemMap<I> requestMap = this.getMap(eventType);
        if (requestMap == null) {
            return null;
        }
        // return the item
        return requestMap.get(type);
    }

    /**
     * Using the lord an item can get an item from another list. Note that you must cast the Item to the desired object. Using getList this
     * can be avoided.
     *
     * @param controlType List type
     * @param id Item ID.
     * @return The requested Item.
     */

    public final <I extends Item> I getItem(MapLink mapLink, final Integer id) {

        if (id == null || NONE.equals(id)) {
            // okay!
            return null;
        }
        // requested map
        ItemMap<I> requestMap = this.getMap(mapLink);
        if (requestMap == null) {
            return null;
        }
        // return the item
        return requestMap.get(id);
    }

    public final <I extends UniqueNamedItem> I getItem(MapLink mapLink, final String uniqueName) {

        if (!StringUtils.containsData(uniqueName)) {
            return null;
        }
        // requested map
        ItemMap<I> requestMap = this.getMap(mapLink);
        if (requestMap == null) {
            return null;
        }
        for (I item : requestMap.values()) {
            if (item instanceof UniqueNamedItem && uniqueName.equals(item.getName())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Using the lord get a list of items for the given ID's.
     */
    public final <I extends Item> List<I> getItems(final MapLink mapLink, final List<Integer> requestIDs) {

        if (requestIDs == null) {
            return null; // null == null
        }
        if (requestIDs.isEmpty()) {
            return new ArrayList<>(); // empty is empty
        }

        // otherwise try item map
        ItemMap<I> itemMap = this.getMap(mapLink);
        return itemMap != null ? itemMap.getItems(requestIDs) : null;
    }

    public Lord getLord() {
        return lord;
    }

    /**
     * Using the lord you can get another item map
     *
     * @param controlClass List type
     * @param id Item ID.
     * @return The requested ItemMap.
     */
    public final <I extends Item> ItemMap<I> getMap(final MapLink mapLink) {

        if (lord == null) {
            // if the lord is not set we had some problem constructing this item
            TLogger.severe(this.getClass().getSimpleName() + "(" + id + ") from " + mapLink.name() + " has no lord set.");
            return null;
        }
        return lord.getMap(mapLink);
    }

    /**
     * Returns a sorted list of the items that go before itemID
     *
     * E.g. get all previous levels (based on there sorted order)
     * @param mapLink
     * @param itemID
     * @return
     */
    public final <I extends Item> Collection<I> getPreviousSortedItems(final MapLink mapLink, final Integer itemID) {

        Item item = this.getItem(mapLink, itemID);
        if (item == null) {
            return new ArrayList<>();
        }
        // filter IDs lower then me (previous) and sort them in reverse
        return this.<I> getMap(mapLink).stream().filter(i -> i.getID().intValue() < itemID.intValue()).sorted(INVERSE_ID_SORT)
                .collect(Collectors.toList());
    }

    /**
     * Return the version of the item.
     *
     * @return the Version
     */
    public final int getVersion() {
        return version;
    }

    public final <C extends Word<E>, E extends Enum<E>> String getWord(final MapLink mapLink, E term, Object... args) {

        Word<E> word = this.getItem(mapLink, term);
        if (word == null) {
            TLogger.severe("Missing " + Word.class.getSimpleName() + " for " + mapLink + " " + term + ".");
            return null;
        }
        return StringUtils.formatEnumString(word.getTranslation(), term, args);
    }

    @Override
    public int hashCode() {
        return this.getID(); // the id is always unique?
    }

    /**
     * Reset the version and id of the item.
     */
    public void reset() {
        this.version = NONE;
        this.id = NONE;
    }

    /**
     * Set item ID.
     *
     * @param id the new ID
     */
    public final void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Set a new lord on the otherside of the network.
     *
     * @param argLord The new Lord.
     */
    public void setLord(final Lord lord) {
        this.lord = lord;
    }

    /**
     * Set item version.
     *
     * @param version the new version
     */
    public final void setVersion(final int version) {

        if (version <= this.version) {
            TLogger.severe("Version of item " + toString() + ", ID: " + this.getID() + " is set back from " + this.version + " to "
                    + version + ".");
        }
        this.version = version;
    }

    public final int syncInternalVersion() {
        updateInternalVersion(version);
        return version;
    }

    @Override
    public abstract String toString();

    /**
     * Override this method if you want to keep a multiple versions in the item.
     */
    protected void updateInternalVersion(int version) {
        // override if needed
    }

    /**
     * Check for correct asset names, not strange signs are allowed.
     */
    private String validAssetName(Object object, Field field) {

        String result = StringUtils.EMPTY;
        try {
            if (field.get(object) instanceof String name && !StringUtils.validFilename(name, null)) {
                result += "\nInvalid asset name in field: " + field.getName() + " [" + name + "] of item " + getClass().getSimpleName()
                        + " " + getID() + ".";
            }
        } catch (Exception e) {
            TLogger.exception(e);
            result += "\n" + e.toString();
        }
        return result;
    }

    /**
     * Default validate method for the item, should be overridden in subclasses.
     *
     * @return ValidationType
     */
    public String validated(boolean startSession) {
        return StringUtils.EMPTY;
    }

    /**
     * Checks if this item has incorrect links to other items and or valid asset names.
     * @return
     */
    public String validFields() {
        return validFields(this);
    }

    protected String validFields(Object object) {

        String result = StringUtils.EMPTY;
        for (Entry<Field, MapLink> entry : ItemNamespace.<MapLink> getFields(object.getClass(), Filter.LINKED).entrySet()) {
            result += validIDFieldLinkage(object, entry);
        }
        for (Field field : ItemNamespace.getFields(object.getClass(), Filter.ASSETS).keySet()) {
            result += validAssetName(object, field);
        }
        for (Entry<Field, Class<?>> entry : ItemNamespace.<Class<?>> getFields(object.getClass(), Filter.CLASSLISTS).entrySet()) {
            result += validListValues(object, entry);
        }
        return result;
    }

    private String validIDFieldLinkage(Object object, Entry<Field, MapLink> entry) {

        String result = StringUtils.EMPTY;
        Field field = entry.getKey();
        MapLink mapLink = entry.getValue();

        try {
            Object value = field.get(object);
            if (value instanceof Integer) {
                result += validIntegerLink(field, mapLink, value);

            } else if (value instanceof List) {
                @SuppressWarnings("unchecked") List<Integer> intListValue = (List<Integer>) value;
                for (Integer intValue : intListValue) {
                    result += validIntegerLink(field, mapLink, intValue);
                }
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked") Map<Integer, ?> map = (Map<Integer, ?>) value;
                Iterable<Integer> idKeys = map.keySet();
                for (Integer intValue : idKeys) {
                    result += validIntegerLink(field, mapLink, intValue);
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
            result += "\n" + e.toString();
        }
        return result;
    }

    private String validIntegerLink(Field field, MapLink mapLink, Object value) {

        String result = StringUtils.EMPTY;
        Integer intValue = (Integer) value;
        if (Item.NONE.equals(intValue)) {
            return result;
        }

        Item item = this.getItem(mapLink, intValue);
        if (item == null) {
            if (field == null) {
                result += "\nFailed linkage in [" + intValue + "] of item " + this.getClass().getSimpleName() + " " + this.getID() + " for "
                        + mapLink + ".";
            } else {
                result += "\nFailed linkage in field: " + field.getName() + " [" + intValue + "] of item " + this.getClass().getSimpleName()
                        + " " + this.getID() + ".";
            }
            return result;
        }
        return result;
    }

    private String validListValues(Object object, Entry<Field, Class<?>> entry) {

        String result = StringUtils.EMPTY;
        Field field = entry.getKey();
        Class<?> listClass = entry.getValue();

        try {
            Object value = field.get(object);
            if (value != null) {
                List<?> list = (List<?>) value;
                for (Object o : list) {
                    if (!o.getClass().isAssignableFrom(listClass)) {
                        result += "Invalid object detected in List: " + field.getName();
                    }
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
            result += "\n" + e.toString();
        }
        return result;
    }
}
