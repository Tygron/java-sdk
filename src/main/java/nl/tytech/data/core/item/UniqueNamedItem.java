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

import java.util.Map;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.util.StringUtils;

/**
 * Items with a unique name must extend this item class
 *
 * @author Maxim Knepfle
 *
 */
public abstract class UniqueNamedItem extends Item implements NamedItem {

    private static final long serialVersionUID = -5605037089134219736L;

    /**
     * For simple names remove unique extensions
     */
    protected static final String SIMPLE_PATTERN = "\\(\\d+\\)";

    /**
     * Attribute with name
     */
    public static final String NAME = "NAME";

    @XMLValue
    private String name = StringUtils.EMPTY;// default is NO name

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        map.put(NAME, name);
        return map;
    }

    @Override
    public final String getName() {
        return name;
    }

    public final String getSimpleName() {
        return getName().replaceAll(SIMPLE_PATTERN, StringUtils.EMPTY).trim();
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
