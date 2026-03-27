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
package nl.tytech.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.LargeCloneItem;
import nl.tytech.util.logger.TLogger;
import se.sawano.java.text.AlphanumericComparator;

/**
 * ObjectUtils: can create new object and clone objects.
 *
 * @author Maxim Knepfle
 */
public class ObjectUtils {

    private static class _ByteArrayOutputStream extends ByteArrayOutputStream {

        private _ByteArrayOutputStream(byte[] data) {
            this.buf = data;
        }

        public byte[] getBuf() {
            return this.buf;
        }
    }

    /**
     * Single instance, seems thread safe to call compare() on
     */
    private static final AlphanumericComparator ALPHANUMERICAL_COMPARATOR = new AlphanumericComparator(new Collator() {

        @Override
        public int compare(String source, String target) {
            return String.CASE_INSENSITIVE_ORDER.compare(source, target);
        }

        @Override
        public CollationKey getCollationKey(String source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException();
        }
    });

    /**
     * Case insensitive alpha-numerical comparator for Objects (not only Strings) with NPE check.
     */
    public static final Comparator<Object> ALPHANUMERICAL_ORDER = (o1, o2) -> ALPHANUMERICAL_COMPARATOR
            .compare(o1.toString() != null ? o1.toString() : StringUtils.EMPTY, o2.toString() != null ? o2.toString() : StringUtils.EMPTY);

    /**
     * Constructs am object from a class with given parameters and values.
     *
     * @param <T> Class/Object type
     * @param classz Class of the to be created object
     * @param parameters Class parameters for constructor
     * @param values Values for constructor
     * @return the new Object T
     */
    @SuppressWarnings("unchecked")
    public static final <T> T constructObject(final Class<T> classz, final Class<?>[] parameters, final Object... values) {

        try {
            // catch java classes
            if (classz.getSuperclass() != null && classz.getSuperclass().equals(Number.class)) {
                // invoke the valueOf method
                final Method valueOf = classz.getMethod("valueOf", String.class);
                return (T) valueOf.invoke(classz, "0");
            } else if (classz.equals(Boolean.class)) {
                return (T) Boolean.valueOf(false);
            } else if (classz.equals(Calendar.class)) {
                return (T) Calendar.getInstance();
            } else if (classz.isEnum()) {
                return classz.getEnumConstants()[0];
            }

            if (Modifier.isAbstract(classz.getModifiers())) {
                TLogger.severe("Cannot instantiate abstract class " + classz.getSimpleName());
                return null;
            }

            return newInstanceForArgs(classz, values);

        } catch (Exception exp) {

            String message = "Failed to construct object: " + classz.getSimpleName()
                    + ". It should contain a constructor with the following arguments: ";
            for (Class<?> parameter : parameters) {
                message += parameter.getSimpleName() + ", ";
            }
            if (parameters.length == 0) {
                message += "NONE";
            }
            TLogger.exception(exp, message);
            return null;
        }
    }

    /**
     * Create a clone of the object. The objects are serialized and de-serialized. During this process a deep copy is created of the object.
     * This procedure is time consuming but takes time away from the programmer.
     *
     * @param orginal Orginal object to be cloned
     * @return deep-copy clone of orginal.
     */
    public static final <T> T deepCopy(final T original) {
        return deepCopy(original, true);
    }

    /**
     * Create a clone of the object. The objects are serialized and de-serialized. During this process a deep copy is created of the object.
     * This procedure is time consuming but takes time away from the programmer.
     *
     * @param orginal Orginal object to be cloned
     * @param allowCloneItem allow manual cloning
     * @return deep-copy clone of orginal.
     */
    @SuppressWarnings("unchecked")
    public static final <T> T deepCopy(final T original, boolean allowCloneItem) {

        // null's are directly returned.
        if (original == null) {
            return null;
        }

        T clone = null;

        try {

            // manual Item clone
            if (allowCloneItem && original instanceof LargeCloneItem) {
                return (T) ((LargeCloneItem) original).cloneItem(null);
            }

            // create out stream
            final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            final ObjectOutputStream outStream = new ObjectOutputStream(byteArrayStream);

            // serialize and pass the object
            outStream.writeObject(original);
            outStream.flush();

            // create in stream
            final ByteArrayInputStream bin = new ByteArrayInputStream(byteArrayStream.toByteArray());
            final ObjectInputStream inStream = new ObjectInputStream(bin);

            // read cloned object, and cast it to T, this is unsafe!
            clone = (T) inStream.readObject();

            // close streams
            outStream.close();
            inStream.close();

        } catch (NotSerializableException exp) {
            TLogger.exception(exp, "Cannot clone object, it must be serializable: " + original.getClass().getCanonicalName());
        } catch (IOException exp) {
            TLogger.exception(exp, "Clone operation failed due to a Stream IO error.");
        } catch (ClassNotFoundException exp) {
            TLogger.exception(exp, "Clone operation failed due to a class not found.");
        } catch (Exception exp) {
            TLogger.exception(exp, "Clone operation failed.");
        }
        return clone;
    }

    public static final Item[] deepCopyItems(final Item[] input, final Item[] unusedItems) {

        // null's are directly returned.
        if (input == null) {
            return null;
        }
        // early out empty array
        if (input.length == 0) {
            return new Item[0];
        }

        try {

            // recycle byte array (reduces memory usage?)
            byte[] bytes = new byte[BufferUtils.SIZE];
            Item[] clone = new Item[input.length];

            for (int i = 0; i < input.length; i++) {

                // manual Item clone
                if (input[i] instanceof LargeCloneItem lci) {
                    Item unusedItem = unusedItems != null && unusedItems.length > i ? unusedItems[i] : null;
                    clone[i] = lci.cloneItem(unusedItem);
                    continue;
                }

                // create new stream for object
                final _ByteArrayOutputStream byteArrayStream = new _ByteArrayOutputStream(bytes);
                final ObjectOutputStream outStream = new ObjectOutputStream(byteArrayStream);

                // serialize and pass the object
                outStream.writeObject(input[i]);
                outStream.flush();

                // upgrade to byte array?
                if (bytes != byteArrayStream.getBuf()) {
                    bytes = byteArrayStream.getBuf();
                }

                // create input from recycled byte array AND with correct size from output stream
                final ByteArrayInputStream bin = new ByteArrayInputStream(bytes, 0, byteArrayStream.size());
                final ObjectInputStream inStream = new ObjectInputStream(bin);

                // read cloned object, and cast it to T, this is unsafe!
                clone[i] = (Item) inStream.readObject();

                // close
                inStream.close();
                outStream.close();
            }
            return clone;

        } catch (NotSerializableException exp) {
            TLogger.exception(exp, "Cannot clone collection, it must be serializable!");
        } catch (IOException exp) {
            TLogger.exception(exp, "Clone operation failed due to a Stream IO error.");
        } catch (ClassNotFoundException exp) {
            TLogger.exception(exp, "Clone operation failed due to a class not found.");
        } catch (Exception exp) {
            TLogger.exception(exp, "Clone operation failed.");
        }
        return null;
    }

    /**
     * Returns the enum value or NULL when none found
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Enum<T>> T getEnum(String name, Class<?>... enumClasses) {

        for (Class<?> enumClass : enumClasses) {
            try {
                return Enum.valueOf((Class<T>) enumClass, name);
            } catch (Exception e) {
                // ignore
            }
        }
        // none found return NULL;
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T> T getEnumAnnotation(Enum enumm, Class clasz) {

        String fieldName = enumm.name();
        try {
            return (T) enumm.getClass().getField(fieldName).getAnnotation(clasz);
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    /**
     * Returns all interfaces of this class and its super's. If the class is an interface itself this is also returned.
     *
     * @param type Class to check
     * @return List of interfaces
     */
    public static final List<Class<?>> getInterfaces(final Class<?> type) {

        // get all the interfaces of the class
        List<Class<?>> interfaces = new ArrayList<>();
        if (type.isInterface()) {
            interfaces.add(type);
        } else {
            Class<?> loop = type;
            while (!loop.equals(Object.class)) {
                Collections.addAll(interfaces, loop.getInterfaces());
                loop = loop.getSuperclass();
            }
        }
        return interfaces;
    }

    /**
     * Class is either a primitive or related object e.g. boolean or Boolean
     */
    public static final boolean isPrimitive(Class<?> c) {
        return c != null && (c.isPrimitive() || Number.class.isAssignableFrom(c) || c == Boolean.class);
    }

    /**
     * When true e.g. Double is single variant of both Double[] and double[]
     */
    public static final boolean isSingleValueOfArray(final Class<?> arrayClass, final Class<?> singleClass) {
        return arrayClass.getSimpleName().equalsIgnoreCase(singleClass.getSimpleName() + "[]");
    }

    public static final <T> T morphObjectToClass(Object source, Class<T> targetClass) {

        TLogger.info("Moving from: " + source.getClass().getSimpleName() + " to " + targetClass.getSimpleName());
        List<Field> newFields = new ArrayList<>();
        List<Field> oldFields = new ArrayList<>();

        Class<?> oldClass = source.getClass();
        while (oldClass != null) {
            Field[] fieldArray = oldClass.getDeclaredFields();
            oldFields.addAll(Arrays.asList(fieldArray));
            oldClass = oldClass.getSuperclass();
        }

        Class<?> newClass = targetClass;
        while (newClass != null) {
            Field[] fieldArray = newClass.getDeclaredFields();
            newFields.addAll(Arrays.asList(fieldArray));
            newClass = newClass.getSuperclass();
        }

        // Here we go
        try {
            final T newItem = targetClass.getDeclaredConstructor().newInstance();
            for (Field oldField : oldFields) {
                if (Modifier.isFinal(oldField.getModifiers()) || Modifier.isStatic(oldField.getModifiers())) {
                    continue;
                }
                if (!newFields.contains(oldField)) {
                    continue;
                }
                oldField.setAccessible(true);
                Object value = oldField.get(source);
                oldField.set(newItem, value);

            }

            return newItem;

        } catch (Exception ec) {
            TLogger.exception(ec);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T newInstanceForArgs(Class<T> classz, Object[] args) {

        try {
            if (args == null || args.length == 0) {
                return classz.getDeclaredConstructor().newInstance();
            }
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }

            constructorloop: for (Constructor<?> constructor : classz.getConstructors()) {

                Class<?>[] paramClasses = constructor.getParameterTypes();
                if (parameterTypes.length != paramClasses.length) {
                    continue constructorloop;
                }

                for (int i = 0; i < paramClasses.length; ++i) {
                    if (!paramClasses[i].isAssignableFrom(parameterTypes[i])) {
                        continue constructorloop;
                    }
                }
                return (T) constructor.newInstance(args);
            }

            Constructor<T> constructor = classz.getConstructor(parameterTypes);
            return constructor.newInstance(args);

        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static final <T> T[] toArray(Class<T> classz, T[]... values) {

        // get length of combined array
        int length = 0;
        for (T[] val : values) {
            length += val.length;
        }
        // create new array
        Object array = Array.newInstance(classz, length);

        // fill with values from old arrays
        int destPos = 0;
        for (T[] val : values) {
            System.arraycopy(val, 0, array, destPos, val.length);
            destPos += val.length;
        }
        return (T[]) array;
    }

    /**
     * Check if this is an array list otherwise create one.
     * @param list
     * @return
     */
    public static final <T> ArrayList<T> toArrayList(final List<T> list) {
        return list instanceof ArrayList ? (ArrayList<T>) list : new ArrayList<>(list);
    }

    public static final <T> ArrayList<T> toArrayList(T[] array) {
        return ObjectUtils.toArrayList(Arrays.asList(array));
    }

    public static final <K, V> HashMap<K, V> toHashMap(final Map<K, V> map) {
        return map instanceof HashMap ? (HashMap<K, V>) map : new HashMap<>(map);
    }

    public static final HashMap<Object, Object> toMap(Object... array) {

        HashMap<Object, Object> map = new HashMap<>();
        if (array != null) {
            if (array.length % 2 != 0) {
                throw new IllegalArgumentException("Array cannot be uneven!");
            }
            for (int i = 0; i < array.length; i += 2) {
                map.put(array[i], array[i + 1]);
            }
        }
        return map;
    }

    public static final <T extends Object> T[] toObjectArray(final T value) {
        return toObjectArray(value, 1);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Object> T[] toObjectArray(final T value, int size) {

        if (value == null) {
            return null;
        }
        // create new array length size and set value in position i
        Object array = Array.newInstance(value.getClass(), size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, value);
        }
        return (T[]) array;
    }

    public static final double[] toPrimitiveArray(final Double value) {
        return toPrimitiveArray(value, 1);
    }

    public static final double[] toPrimitiveArray(final Double value, int size) {

        if (value == null) {
            return null;
        }
        // create new array length size and set value in position i
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = value;
        }
        return array;
    }

    public static final double[] toPrimitiveArray(final List<Double> values) {

        if (values == null) {
            return null;
        }
        // create and fill primitive array
        double[] array = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i) != null ? values.get(i) : 0.0;
        }
        return array;
    }

    public static final double[][] toPrimitiveMatrix(final double[] values) {

        if (values == null) {
            return null;
        }
        double[][] matrix = new double[values.length][1];
        for (int i = 0; i < values.length; i++) {
            matrix[i][0] = values[i];
        }
        return matrix;
    }

    public static final double[][] toPrimitiveMatrix(final List<double[]> values) {

        if (values == null) {
            return null;
        }
        double[][] matrix = new double[values.size()][];
        for (int i = 0; i < values.size(); i++) {
            matrix[i] = values.get(i);
        }
        return matrix;
    }

    // do not instantiate
    private ObjectUtils() {

    }
}
