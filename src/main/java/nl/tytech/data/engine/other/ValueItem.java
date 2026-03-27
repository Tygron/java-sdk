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
package nl.tytech.data.engine.other;

import java.util.Collection;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;

/**
 * ValueItem Item containing function values
 *
 * @author Maxim Knepfle
 */
public interface ValueItem {

    public Collection<Category> getCategories();

    public double getCategoryFraction(Category cat);

    public double getValue(Category cat, CategoryValue val);

    public double getValue(MapType mapType, Category cat, CategoryValue val);

    public double getValue(MapType mapType, Value key);

    public double getValue(Value key);

    public double[] getValueArray(Category cat, CategoryValue val);

    public double[] getValueArray(MapType mapType, Category cat, CategoryValue val);

    public double[] getValueArray(MapType mapType, Value key);

    public double[] getValueArray(Value key);

}
