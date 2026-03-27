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
package nl.tytech.data.editor.item;

import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.editor.other.BGTGeoLink;
import nl.tytech.data.editor.serializable.BGTLayer;
import nl.tytech.data.engine.serializable.Category;

/**
 * @author Maxim Knepfle
 */
public class BGTTerrainLink extends TerrainGeoLink implements BGTGeoLink {

    private static final long serialVersionUID = 3227809616030984810L;

    @XMLValue
    private BGTLayer layer = null;

    @XMLValue
    private String className = null;

    @XMLValue
    private String typeName = "";

    @Override
    public List<Category> allowedCategories() {
        return null;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public BGTLayer getLayer() {
        return layer;
    }

    @Override
    public int getTransmissionTowerFoundations() {
        return 0;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean hasCategories() {
        return false;
    }
}
