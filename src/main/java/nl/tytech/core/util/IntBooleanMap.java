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
package nl.tytech.core.util;

import java.util.Map;

/**
 * Similar to a {@link Map} except that ints are used as keys and booleans for values.
 *
 * Taken via JME3 IntMap from <a href="http://code.google.com/p/skorpios/">http://code.google.com/p/skorpios/</a>
 *
 * @author Maxim Knepfle & skorpios
 */
public final class IntBooleanMap {

    private static final class Entry {

        private final int key;
        private boolean value;
        private Entry next;

        private Entry(int k, boolean v, Entry n) {
            key = k;
            value = v;
            next = n;
        }

        private Boolean getValue() {
            return value ? Boolean.TRUE : Boolean.FALSE;
        }

        @Override
        public String toString() {
            return key + " => " + value;
        }
    }

    private Entry[] table;

    private final float loadFactor;

    private int size, mask, capacity, threshold;

    public IntBooleanMap() {
        this(16, 0.75f);
    }

    public IntBooleanMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IntBooleanMap(int initialCapacity, float loadFactor) {

        if (initialCapacity > 1 << 30) {
            throw new IllegalArgumentException("initialCapacity is too large.");
        }
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        }
        if (loadFactor <= 0) {
            throw new IllegalArgumentException("loadFactor must be greater than zero.");
        }
        capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
        this.mask = capacity - 1;
    }

    public void clear() {

        Entry[] table = this.table;
        for (int index = table.length; --index >= 0;) {
            table[index] = null;
        }
        size = 0;
    }

    public boolean containsKey(int key) {

        int index = key & mask;
        for (Entry e = table[index]; e != null; e = e.next) {
            if (e.key == key) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(boolean value) {

        Entry[] table = this.table;
        for (int i = table.length; i-- > 0;) {
            for (Entry e = table[i]; e != null; e = e.next) {
                if (e.value == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean get(int key) {

        int index = key & mask;
        for (Entry e = table[index]; e != null; e = e.next) {
            if (e.key == key) {
                return e.getValue();
            }
        }
        return null;
    }

    public Boolean put(int key, boolean value) {

        int index = key & mask;
        // Check if key already exists.
        for (Entry e = table[index]; e != null; e = e.next) {
            if (e.key != key) {
                continue;
            }
            boolean oldValue = e.value;
            e.value = value;
            return oldValue ? Boolean.TRUE : Boolean.FALSE;
        }
        table[index] = new Entry(key, value, table[index]);

        if (size++ >= threshold) {
            // Rehash.
            int newCapacity = 2 * capacity;
            Entry[] newTable = new Entry[newCapacity];
            Entry[] src = table;
            int bucketmask = newCapacity - 1;
            for (int j = 0; j < src.length; j++) {
                Entry e = src[j];
                if (e != null) {
                    src[j] = null;
                    do {
                        Entry next = e.next;
                        index = e.key & bucketmask;
                        e.next = newTable[index];
                        newTable[index] = e;
                        e = next;
                    } while (e != null);
                }
            }
            table = newTable;
            capacity = newCapacity;
            threshold = (int) (newCapacity * loadFactor);
            mask = capacity - 1;
        }
        return null;
    }

    public Boolean remove(int key) {

        int index = key & mask;
        Entry prev = table[index];
        Entry e = prev;
        while (e != null) {
            Entry next = e.next;
            if (e.key == key) {
                size--;
                if (prev == e) {
                    table[index] = next;
                } else {
                    prev.next = next;
                }
                return e.getValue();
            }
            prev = e;
            e = next;
        }
        return null;
    }

    public int size() {
        return size;
    }
}
