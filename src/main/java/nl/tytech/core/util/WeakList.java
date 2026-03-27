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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A Thread Safe Generic ArrayList that cleans weak references out on add, remove and get.
 *
 * @author Maxim Knepfle
 */
public final class WeakList<T> {

    private final transient List<WeakReference<T>> references = new ArrayList<>();

    public final synchronized void add(T item) {

        if (item == null) {
            return;
        }

        cleanReferences();
        references.add(new WeakReference<T>(item));
    }

    private final synchronized List<T> cleanReferences() {

        List<T> items = new ArrayList<>();
        for (int i = references.size() - 1; i >= 0; i--) {
            T item = references.get(i).get();
            if (item != null) {
                items.add(0, item);
            } else {
                references.remove(i);
            }
        }
        return items;
    }

    public final List<T> getList() {
        return cleanReferences();
    }

    public final synchronized WeakReference<T> remove(T item) {

        if (item == null) {
            return null;
        }

        cleanReferences();
        for (int i = 0; i < references.size(); i++) {
            if (item.equals(references.get(i).get())) {
                return references.remove(i);
            }
        }
        return null;
    }
}
