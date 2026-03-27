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
package nl.tytech.core.structure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.net.serializable.ItemID;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.PackageUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.jts.EmptyMultiPolygon;
import nl.tytech.util.logger.TLogger;

/**
 * ItemNamespace: In the ItemNamespace each class should have a unique name. This named is stored as a String in combination with the full
 * class. The XML writer can now store the items with their simple name.
 *
 * @author Maxim Knepfle
 */
public class ItemNamespace {

    private static class FieldCollection {

        private final Map<Field, ?> allFields = new LinkedHashMap<>();
        private final Map<Field, String> initXmlFields = new LinkedHashMap<>();
        private final Map<Field, String> saveXmlFields = new LinkedHashMap<>();
        private final Map<Field, MapLink> linkedFields = new LinkedHashMap<>();
        private final Map<Field, String> assetFields = new LinkedHashMap<>();
        private final Map<Field, Class<?>> classListFields = new LinkedHashMap<>();
        private final Map<Field, ?> htmlFields = new LinkedHashMap<>();
        private final Map<Field, ?> jsonFields = new LinkedHashMap<>();
    }

    /**
     * Filter for fields.
     */
    public enum Filter {
        INIT_XML, SAVE_XML, LINKED, ASSETS, CLASSLISTS, HTML, ALL, JSON;
    }

    private static final class SingletonHolder {

        private static final ItemNamespace INSTANCE = new ItemNamespace();
    }

    /**
     * Only these classes and ENUMS are allowed for event parameters. Makes it easier for JSON.
     */
    private static final List<Class<?>> supportedServerEventClasses = Arrays.asList(String.class, Boolean.class, Integer.class, Long.class,
            Float.class, Double.class, TColor.class, Point.class, MultiPolygon.class, GeometryCollection.class, CodedEvent.class);

    /**
     * Adds the classes of the given package name to the Itemspace.
     *
     * @param packageName Name of the package
     * @return succes
     */
    protected static final boolean addPackageClasses(final String packageName) {
        return SingletonHolder.INSTANCE._addPackageClasses(packageName);
    }

    /**
     * True when this class is in the namespace.
     *
     * @param classz
     * @return
     */
    public static final boolean containsClass(final Class<?> classz) {
        return SingletonHolder.INSTANCE._containsClass(classz);
    }

    /**
     * True when this simplename is in the namespace.
     *
     * @param classz
     * @return
     */
    public static final boolean containsSimpleName(final String simpleName) {
        return SingletonHolder.INSTANCE._containsSimpleName(simpleName);
    }

    /**
     * Get the classes for the given simple name.
     *
     * @param simpleName Simple name of the class.
     * @return Full class.
     */
    public static final Class<?> getClass(String simpleName) {
        return SingletonHolder.INSTANCE._getClass(simpleName);
    }

    public static final Collection<Class<?>> getClasses() {
        return SingletonHolder.INSTANCE._getClasses();
    }

    /**
     * Returns the field's value when object was created, Note only works on objects in ItemNamespace.
     * @param object
     * @param field
     * @return
     */
    public static Object getDefaultFieldValue(Object object, Field field) {
        return SingletonHolder.INSTANCE._getDefaultFieldValue(object, field);
    }

    /**
     * Get Event class associated with this MapLink
     */
    public static final Class<?> getEventClass(MapLink mapLink) {
        return SingletonHolder.INSTANCE._getEventClass(mapLink);
    }

    /**
     * Returns the field for the given class and name. When unknown it returns null.
     */
    public static final Field getField(final Class<?> classz, String fieldName) {
        return SingletonHolder.INSTANCE._getField(classz, fieldName);

    }

    /**
     * Returns the fields of the given class.
     *
     * @param classz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T> Map<Field, T> getFields(final Class<?> classz, Filter filter) {
        return (Map<Field, T>) SingletonHolder.INSTANCE._getFields(classz, filter);
    }

    /**
     * Get the simple name for the given class
     *
     * @param classz The class
     * @return simple name
     */
    public static final String getSimpleName(final Class<?> classz) {
        return SingletonHolder.INSTANCE._getSimpleName(classz);
    }

    /**
     * True when this is an leaf and has no further fields (branches).
     *
     * @param classz
     * @return
     */
    public static final boolean isXMLLeaf(final Class<?> classz) {
        return SingletonHolder.INSTANCE._isXMLLeaf(classz);
    }

    private final Map<Field, Object> defaultObjects = new HashMap<>();

    /**
     * Mapping of the XML fields.
     */
    private final Map<Class<?>, FieldCollection> fields = new HashMap<>();

    /**
     * Mapping of the MapLink Events.
     */
    private final Map<MapLink, Class<?>> mapLinkEvents = new HashMap<>();

    /**
     * Hashmap containing the link from simple name string to full class.
     */
    private final Map<String, Class<?>> mapping = new HashMap<>();

    /**
     * Reverse of the mapping.
     */
    private final Map<Class<?>, String> reverse = new HashMap<>();

    /**
     * Used to lock the maps when entering new data.
     */
    private final Object write = new Object();

    /**
     * Singleton
     */
    private ItemNamespace() {

        // load default java classes first
        addClass(int.class, true);
        addClass(boolean.class, true);
        addClass(byte.class, true);
        addClass(float.class, true);
        addClass(double.class, true);
        addClass(long.class, true);
        addClass(short.class, true);

        addClass(Integer.class, true);
        addClass(Boolean.class, true);
        addClass(Byte.class, true);
        addClass(Float.class, true);
        addClass(Double.class, true);
        addClass(Long.class, true);
        addClass(Short.class, true);
        addClass(String.class, true);

        // cannot use simple names, not allowed by XML parser
        addClass("intarray", int[].class, true);
        addClass("booleanarray", boolean[].class, true);
        addClass("bytearray", byte[].class, true);
        addClass("floatarray", float[].class, true);
        addClass("floatmatrix", float[][].class, true);
        addClass("longarray", long[].class, true);
        addClass("shortarray", short[].class, true);
        addClass("doublearray", double[].class, true);
        addClass("doublematrix", double[][].class, true);
        addClass("Stringarray", String[].class, true);
        addClass("Integerarray", Integer[].class, true);
        addClass("Doublearray", Double[].class, true);
        addClass("Objectarray", Object[].class, true);

        addClass(GregorianCalendar.class, true);
        addClass(Calendar.class, true);
        addClass(TColor.class, true);
        addClass(List.class, true);
        addClass(Map.class, true);

        addClass(ArrayList.class, true);
        addClass(Vector.class, true);
        addClass(EnumMap.class, true);
        addClass(HashMap.class, true);
        addClass(Hashtable.class, true);
        addClass(TreeMap.class, true);

        // util classes
        addClass(ItemID.class, false);
        addClass(MapLink.class, false);

        // geom classes
        addClass(MultiPolygon.class, true);
        addClass(GeometryCollection.class, true);
        addClass(EmptyMultiPolygon.class, true);
        addClass(Polygon.class, true);
        addClass(Point.class, true);
        addClass(LineString.class, true);
        addClass(MultiLineString.class, true);

        // check service events
        validateEnum(IOServiceEventType.class);
        validateEnum(UserServiceEventType.class);

        // validate mapLink names
        validateMapLinkNames();
    }

    /**
     * Adds the classes of the given package name to the Itemspace.
     *
     * @param packageName Name of the package
     * @return succes
     */
    private final boolean _addPackageClasses(final String packageName) {

        if (packageName == null) {
            return false;
        }

        synchronized (write) {
            List<String> classNames = PackageUtils.getPackageClassNames(packageName);

            for (String className : classNames) {

                Class<?> classz = null;

                // is the class really there?
                try {
                    classz = Class.forName(className);
                } catch (ClassNotFoundException exp1) {
                    TLogger.showstopper("Failed to add class: " + className + " to Item namespace, class was not found.");
                    return false;
                }

                if (classz.getSimpleName().equals(StringUtils.EMPTY)) {
                    // classes with no simple name are ignored. (e.g. comparators)
                    continue;
                }

                // Validate the class for human error.
                if (validateItemNamespaceClass(classz)) {
                    addClass(classz);
                } else {
                    return false;
                }
            }
            // only item namespace classes may be used for fields.
            for (Class<?> classz : fields.keySet()) {
                Map<Field, ?> classFields = _getFields(classz, Filter.SAVE_XML);
                if (classFields != null) {
                    for (Field field : classFields.keySet()) {
                        Class<?> fieldClass = field.getType();
                        if (!reverse.containsKey(fieldClass)) {
                            TLogger.showstopper(
                                    "Failed to add class: " + classz.getSimpleName() + " to Item namespace. The field: " + field.getName()
                                            + " has a class: " + fieldClass.getSimpleName() + " that is not in the Item namespace.");
                        }
                    }
                }
            }
            TLogger.info("Loaded " + classNames.size() + "\tClasses from: " + packageName);
        }
        return true;
    }

    /**
     * True when this class is in the namespace.
     *
     * @param classz
     * @return
     */
    private final boolean _containsClass(final Class<?> classz) {
        return fields.containsKey(classz);
    }

    private boolean _containsSimpleName(String simpleName) {
        return mapping.containsKey(simpleName);
    }

    /**
     * Get the classes for the given simple name.
     *
     * @param simpleName Simple name of the class.
     * @return Full class.
     */
    private final Class<?> _getClass(String simpleName) {

        // skip .class at the end
        if (simpleName.endsWith(".class")) {
            simpleName = simpleName.replaceAll(".class", "");
        }

        /**
         * No more floats allowed here
         */
        if (Float.class.getSimpleName().equals(simpleName)) {
            return Double.class;
        }
        if (float.class.getSimpleName().equals(simpleName)) {
            return double.class;
        }

        // member classes are stored with dots instead of $ in between
        if (simpleName.contains("$")) {
            simpleName = simpleName.replace("$", ".");
        }

        Class<?> result = mapping.get(simpleName);
        if (result == null) {
            TLogger.severe(simpleName + " is unknown in Item namespace.");
        }
        return result;
    }

    private final Collection<Class<?>> _getClasses() {
        return mapping.values();
    }

    private synchronized Object _getDefaultFieldValue(Object object, Field field) {

        if (!defaultObjects.containsKey(field)) {
            try {
                field.setAccessible(true);
                Class<?> classz = object.getClass();
                Object newObject = classz.getDeclaredConstructor().newInstance();
                defaultObjects.put(field, field.get(newObject));
            } catch (Exception e) {
                TLogger.exception(e);
            }
        }
        return defaultObjects.get(field);
    }

    private final Class<?> _getEventClass(MapLink mapLink) {
        return mapLinkEvents.get(mapLink);
    }

    /**
     * Returns the field for the given class and name. When unknown it returns null.
     */
    private final Field _getField(final Class<?> classz, String fieldName) {

        final Map<Field, ?> fields = _getFields(classz, Filter.ALL);
        if (fields == null || fieldName == null || fieldName.equals(StringUtils.EMPTY)) {
            return null;
        }
        for (Field field : fields.keySet()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        TLogger.severe(fieldName + " is unknown for class " + classz.getSimpleName());
        return null;
    }

    /**
     * Returns the fields of the given class.
     *
     * @param classz
     * @return
     */
    private final Map<Field, ?> _getFields(final Class<?> classz, Filter filter) {

        if (!fields.containsKey(classz)) {
            TLogger.severe(classz.getSimpleName() + " is unknown in Item namespace.");
            return null;
        }

        FieldCollection collection = fields.get(classz);
        if (collection == null) {
            return null;
        }

        switch (filter) {
            case INIT_XML:
                return collection.initXmlFields;
            case SAVE_XML:
                return collection.saveXmlFields;
            case LINKED:
                return collection.linkedFields;
            case ASSETS:
                return collection.assetFields;
            case CLASSLISTS:
                return collection.classListFields;
            case HTML:
                return collection.htmlFields;
            case JSON:
                return collection.jsonFields;
            case ALL:
            default:
                return collection.allFields;
        }
    }

    /**
     * Get the simple name for the given class
     *
     * @param classz The class
     * @return simple name
     */
    private final String _getSimpleName(final Class<?> classz) {

        String simpleName = reverse.get(classz);
        if (simpleName == null) {
            TLogger.severe(classz.getSimpleName() + " is unknown in Item namespace.");
            return null;
        }
        return simpleName;
    }

    /**
     * True when this is an leaf and has no further fields (branches).
     *
     * @param classz
     * @return
     */
    private final boolean _isXMLLeaf(final Class<?> classz) {

        final Map<Field, ?> fields = this._getFields(classz, Filter.SAVE_XML);
        return fields == null || fields.isEmpty();
    }

    private boolean addClass(final Class<?> classz) {

        return this.addClass(classz, false);
    }

    /**
     * Helper class used to add a class.
     *
     * @param classz
     * @return
     */
    private boolean addClass(final Class<?> classz, boolean java) {

        String name = classz.getSimpleName();

        if (classz.isMemberClass()) {
            name = classz.getDeclaringClass().getSimpleName() + "." + name;
        }
        return this.addClass(name, classz, java);
    }

    /**
     * Helper method used to add a class.
     *
     * @param simpleName
     * @param classz
     * @return
     */
    private boolean addClass(final String simpleName, final Class<?> classz, boolean java) {

        synchronized (write) {
            if (mapping.containsKey(simpleName) || reverse.containsKey(classz)) {
                TLogger.showstopper("Cannot have duplicate names ( " + simpleName + " ) in Item namespace.");
                return false;
            }

            // add simple name mapping
            mapping.put(simpleName, classz);

            // add the reverse
            reverse.put(classz, simpleName);

            // get the fields
            FieldCollection classzfields = null;
            if (!java) {
                // only not standard java classes get added
                classzfields = loadFields(classz);
            }
            // add the fields
            fields.put(classz, classzfields);
            return true;
        }
    }

    private boolean checkEventEditorLinked(final Class<?> classz) {

        try {
            if (EventTypeEnum.class.isAssignableFrom(classz) && classz.getSimpleName().matches("Editor(.+)EventType")) {
                Linked linked = classz.getAnnotation(Linked.class);
                if (linked == null) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " "
                            + " to Item namespace. Should contain annotation for MapLink Linked.");
                    return false;
                }
                mapLinkEvents.put(linked.value(), classz);
            }
        } catch (Exception e) {
            TLogger.exception(e);
            TLogger.showstopper("Exception in item namespace check! [" + classz.getSimpleName() + "]");
            return false;
        }
        return true;
    }

    private boolean checkEventIDField(final Class<?> classz, Object enumerator) {

        try {
            EventIDField eventIDField = classz.getField(((Enum<?>) enumerator).name()).getAnnotation(EventIDField.class);
            if (eventIDField != null) {
                if (!(enumerator instanceof EventTypeEnum)) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                            + " to Item namespace. Enums with EventIDField must implement EventTypeEnum.");
                    return false;
                }

                if (eventIDField.links().length != eventIDField.params().length) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                            + " to Item namespace. Enums with EventIDField need to have the same length for the link and params.");
                    return false;
                }

                for (int i = 0; i < eventIDField.links().length; i++) {
                    if (eventIDField.links()[i] == null) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                + " to Item namespace. MapLink may not be NULL.");
                        return false;
                    }
                }

                EventTypeEnum eventTypeEnum = (EventTypeEnum) enumerator;

                for (int i : eventIDField.params()) {
                    if (i < 0 || eventTypeEnum.getClasses().size() <= i) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                + " to Item namespace. Param: " + i + " must map to valid Index.");
                        return false;
                    }
                    Class<?> eventClass = eventTypeEnum.getClasses().get(i);
                    if (!eventClass.equals(Integer.class) && !eventClass.equals(Integer[].class) && !eventClass.equals(Integer[][].class)) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                + " to Item namespace. Enums with EventIDField has params that need to link to Integer's, Integer[]'s or Integer[][].");
                        return false;
                    }

                }
                for (int i : eventIDField.nullable()) {
                    if (i < 0 || eventTypeEnum.getClasses().size() <= i) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                + " to Item namespace. Nullable: " + i + " must map to valid Index.");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
            TLogger.showstopper("Exception in item namespace check! [" + enumerator.getClass().getSimpleName() + "." + enumerator + "]");
            return false;
        }
        return true;
    }

    private boolean checkEventParamData(final Class<?> classz, Object enumerator) {

        try {
            EventParamData eventParamData = classz.getField(((Enum<?>) enumerator).name()).getAnnotation(EventParamData.class);
            if (eventParamData != null) {
                if (!(enumerator instanceof EventTypeEnum eventTypeEnum)) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                            + " to Item namespace. Enums with " + EventParamData.class.getSimpleName() + " must implement"
                            + EventTypeEnum.class.getSimpleName());
                    return false;
                }

                if (eventParamData.params().length != eventTypeEnum.getClasses().size()) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                            + " to Item namespace. Enums with " + EventParamData.class.getSimpleName()
                            + " need to have the same amount of parameter names as the amount of input variables.");
                    return false;
                }

                if (eventParamData.defaults().length > 0) {
                    if (eventParamData.defaults().length != eventTypeEnum.getClasses().size()) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                + " to Item namespace. Enums with " + EventParamData.class.getSimpleName()
                                + " needs to have none or the same amount of default values as the amount of input variables.");
                        return false;
                    }
                    for (int i = 0; i < eventParamData.defaults().length; i++) {
                        if (StringUtils.containsData(eventParamData.defaults()[i]) && Event.getDefault(eventTypeEnum, i) == null) {
                            TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                    + " to Item namespace. Enums with " + EventParamData.class.getSimpleName()
                                    + " default values can only be applied to specific class types.");
                            return false;
                        }
                    }
                }

                Class<?> responseClass = eventTypeEnum.getResponseClass(new Object[eventTypeEnum.getClasses().size()]);
                if (responseClass != null && Number.class.isAssignableFrom(responseClass)
                        && !StringUtils.containsData(eventParamData.response())) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                            + " to Item namespace. Enums with " + EventParamData.class.getSimpleName()
                            + " must have a response description for type: " + responseClass.getSimpleName() + ".");
                    return false;
                }
            }

        } catch (Exception e) {
            TLogger.exception(e);
            TLogger.showstopper("Exception in item namespace check! [" + enumerator.getClass().getSimpleName() + "." + enumerator + "]");
            return false;
        }
        return true;
    }

    private boolean checkEventServerClassData(final Class<?> classz, Object enumerator) {

        try {
            if (!(enumerator instanceof EventTypeEnum eventTypeEnum)) {
                return true;
            }
            if (eventTypeEnum.isServerSide()) {
                for (Class<?> paramClass : eventTypeEnum.getClasses()) {
                    if (!supportedServerEventClasses.contains(paramClass)
                            && !(paramClass.getSuperclass() != null && paramClass.getSuperclass().equals(Enum.class))
                            && !paramClass.isArray()) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " " + enumerator.toString()
                                + " to Item namespace. Parameter: " + paramClass.getSimpleName()
                                + " should be simple for JSON communication.");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
            TLogger.showstopper("Exception in item namespace check! [" + enumerator.getClass().getSimpleName() + "." + enumerator + "]");
            return false;
        }
        return true;
    }

    /**
     * Helper method. Get the fields of a item. Both it super class and subclass. Only XML fields can be saved to xml.
     *
     * @param object The object
     * @return The objects fields.
     */
    private FieldCollection loadFields(final Class<?> objectClass) {

        // enums do null fields.
        if (objectClass == null || objectClass.isEnum() || objectClass.isArray()) {
            return null;
        }

        FieldCollection fieldPair = new FieldCollection();
        Class<?> walkClassz = objectClass;

        // walk each class for XML fields
        while (walkClassz != null && walkClassz != Object.class) {

            // check for XML field
            for (Field superField : walkClassz.getDeclaredFields()) {

                // set access
                superField.setAccessible(true);

                // skip static fields
                if (Modifier.isStatic(superField.getModifiers())) {
                    continue;
                }

                if (walkClassz != Item.class) {
                    fieldPair.allFields.put(superField, null);

                    if (superField.isAnnotationPresent(XMLValue.class)) {
                        fieldPair.saveXmlFields.put(superField, null);
                        if (!superField.isAnnotationPresent(DoNotSaveToInit.class)) {
                            fieldPair.initXmlFields.put(superField, null);
                        }
                    }
                    if (superField.isAnnotationPresent(ItemIDField.class)) {
                        ItemIDField a = superField.getAnnotation(ItemIDField.class);
                        fieldPair.linkedFields.put(superField, a.value());
                    }
                    if (superField.isAnnotationPresent(ListOfClass.class)) {
                        ListOfClass a = superField.getAnnotation(ListOfClass.class);
                        fieldPair.classListFields.put(superField, a.value());
                    }
                    if (superField.isAnnotationPresent(AssetDirectory.class)) {
                        AssetDirectory a = superField.getAnnotation(AssetDirectory.class);
                        fieldPair.assetFields.put(superField, a.value());
                    }
                    if (superField.isAnnotationPresent(Html.class)) {
                        fieldPair.htmlFields.put(superField, null);
                    }
                }
                if (!superField.isAnnotationPresent(JsonIgnore.class)) {
                    fieldPair.jsonFields.put(superField, null);
                }
            }
            // next
            walkClassz = walkClassz.getSuperclass();
        }
        return fieldPair;
    }

    /**
     * Validate Item enumerator for correct annotation implementation.
     * @param classz
     * @return
     */
    private boolean validateEnum(final Class<?> classz) {

        boolean result = checkEventEditorLinked(classz);
        for (Object enumerator : classz.getEnumConstants()) {
            result &= checkEventIDField(classz, enumerator);
            result &= checkEventParamData(classz, enumerator);
            result &= checkEventServerClassData(classz, enumerator);
        }
        return result;
    }

    /**
     * Helper method used to check classes in Item namespace for human error or incorrect fields.
     *
     * @param classz Class to check
     * @return result
     */
    private boolean validateItemNamespaceClass(final Class<?> classz) {

        // does it have the correct constructor?
        if (classz == null || classz.isMemberClass()) {
            return true;
        }

        if (classz.isEnum()) {
            return validateEnum(classz);
        }

        // check for empty constructor or abstract class
        boolean valid = Modifier.isAbstract(classz.getModifiers());
        for (Constructor<?> constructor : classz.getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                valid |= true;
            }
        }

        // if no empty constructor is available stop.
        if (!valid) {
            TLogger.showstopper("Failed to add class: " + classz.getSimpleName()
                    + " to Item namespace. Item classes need at least one empty accessible constructor.");
            return false;
        }

        // get all XML classes and check for Item links. These should not exist but link using ID's.
        FieldCollection fields = loadFields(classz);

        // if the class has XML fields check them
        if (fields == null) {
            return true;
        }
        for (Field field : fields.saveXmlFields.keySet()) {
            // check for the fields
            Class<?> fieldClass = field.getType();

            // cannot have other items here
            if (Item.class.isAssignableFrom(fieldClass)) {

                TLogger.showstopper(
                        "Failed to add class: " + classz.getSimpleName() + " to Item namespace. Cannot link directly to other Item: "
                                + field.getType().getSimpleName() + ". Please use the ID of the Item instead.");
                return false;
            }

            // only limited array support
            if (fieldClass.isArray() && !int[].class.isAssignableFrom(fieldClass) && !boolean[].class.isAssignableFrom(fieldClass)
                    && !byte[].class.isAssignableFrom(fieldClass) && !short[].class.isAssignableFrom(fieldClass)
                    && !float[].class.isAssignableFrom(fieldClass) && !float[][].class.isAssignableFrom(fieldClass)
                    && !double[].class.isAssignableFrom(fieldClass) && !long[].class.isAssignableFrom(fieldClass)
                    && !String[].class.isAssignableFrom(fieldClass)) {

                TLogger.showstopper("Failed to add class: " + classz.getSimpleName()
                        + " to Item namespace. Only primitives, float[] and Strings can be put in arrays. For: "
                        + field.getType().getSimpleName() + " please use a List.");
                return false;
            }

            if (fieldClass.equals(List.class)) {
                TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " to Item namespace. Field: " + field.getName()
                        + " is an Interface (List) please use ArrayList instead.");
                return false;

            } else if (fieldClass.equals(Map.class)) {
                TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " to Item namespace. Field: " + field.getName()
                        + " is an Interface (Map) please use HashMap instead.");
                return false;
            }

            // check html
            if (field.isAnnotationPresent(Html.class) && !field.getType().equals(String.class)) {
                TLogger.showstopper("Failed to add class: " + classz.getSimpleName() + " to Item namespace. The Html annotation at field: "
                        + field.getName() + " is only allowd for String fields.");
                return false;
            }

            // check field ID linkage
            if (field.isAnnotationPresent(ItemIDField.class)) {
                Class<?> type = field.getType();
                if (type.equals(Integer.class) || type.equals(int.class)) {
                    if (!field.getName().endsWith("ID")) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName()
                                + " to Item namespace. The ItemIDField annotation at field: " + field.getName()
                                + " name must end with [ID].");

                        return false;
                    }

                } else {

                    List<Class<?>> interfaces = ObjectUtils.getInterfaces(type);
                    boolean correctClass = interfaces.contains(List.class) || interfaces.contains(Map.class);
                    boolean correctParameter = true;
                    if (correctClass) {
                        // Do an extra parameter check
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType pType) {
                            if (!Integer.class.equals(pType.getActualTypeArguments()[0])) {
                                correctParameter = false;
                            }
                        }
                    }

                    if (!(correctClass && correctParameter)) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName()
                                + " to Item namespace. The ItemIDField annotation at field: " + field.getName()
                                + " is only allowed for Integer's, List<Integer> and Map<Integer,?> fields.");
                        return false;
                    }

                    if (interfaces.contains(List.class) && !field.getName().endsWith("IDs")) {
                        TLogger.showstopper("Failed to add class: " + classz.getSimpleName()
                                + " to Item namespace. The ItemIDField annotation at field: " + field.getName()
                                + " name must end with [IDs].");
                        return false;
                    }
                }

                MapLink mapLink = field.getAnnotation(ItemIDField.class).value();
                if (mapLink == null) {
                    TLogger.showstopper("Failed to add class: " + classz.getSimpleName()
                            + " to Item namespace. The ItemIDField annotation at field: " + field.getName() + " has an invalid value.");
                    return false;
                }
            }

            // check ClassList annotation if for a list
            if (field.isAnnotationPresent(ListOfClass.class)) {
                Class<?> type = field.getType();
                // if not a list stop!...
                List<Class<?>> interfaces = ObjectUtils.getInterfaces(type);
                if (!interfaces.contains(List.class)) {
                    TLogger.showstopper(
                            "Failed to add class: " + classz.getSimpleName() + " to Item namespace. The ClassList annotation at field: "
                                    + field.getName() + " is only allowed List fields.");
                    return false;
                }
            }
        }
        // the class passed all tests
        return true;

    }

    private void validateMapLinkNames() {

        List<String> humanNames = new ArrayList<>();
        List<String> normal = new ArrayList<>();

        for (MapLink mapLink : MapLink.values()) {

            String normalName = mapLink.name().toLowerCase();
            if (normal.contains(normalName)) {
                TLogger.showstopper("Cannot have double naming: " + normalName + " in MapLink");
            }
            normal.add(normalName);

            String humanName = StringUtils.capitalizeUnderScores(mapLink.name()).toLowerCase();
            if (humanNames.contains(humanName)) {
                TLogger.showstopper("Cannot have double naming: " + humanName + " in MapLink");
            }
            humanNames.add(humanName);
        }
    }
}
