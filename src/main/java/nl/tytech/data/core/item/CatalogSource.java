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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.serializable.GeoCatalog;
import nl.tytech.util.color.TColor;

/**
 * Catalog source location
 *
 * @author Maxim Knepfle
 */
public class CatalogSource extends WebSource {

    private static final long serialVersionUID = -74284467549856227L;

    @XMLValue
    private GeoCatalog type = GeoCatalog.CSW;

    public CatalogSource() {

    }

    public CatalogSource(GeoCatalog type, String name, String url) {
        super(name, url);
        this.type = type;
    }

    @Override
    public TColor getColor() {
        return TColor.RED;
    }

    public GeoCatalog getType() {
        return type;
    }

    @Override
    public String getTypeName() {
        return getType() + " Catalog";
    }

    public void setType(GeoCatalog type) {
        this.type = type;
    }
}
