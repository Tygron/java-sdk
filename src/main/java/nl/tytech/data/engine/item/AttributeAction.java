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

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.util.StringUtils;

/**
 * AttributeAction: This class keeps track of the attribute actions.
 *
 * @author Frank Baars
 */
public class AttributeAction extends AttributeItem implements Action, ImageItem {

    private static final long serialVersionUID = -2026187810592396174L;

    @XMLValue
    @AssetDirectory(ACTION_IMAGE_LOCATION)
    private String imageName = DEFAULT_IMAGE;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private boolean fixedValue = true;

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return new ReservedAttribute[0];
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageLocation() {
        return ACTION_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageVersion() {
        return imageVersion;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.ATTRIBUTE_ACTIONS;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public boolean isFixedValue() {
        return fixedValue;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public void setFixedValue(boolean fixedValue) {
        this.fixedValue = fixedValue;
    }

    @Override
    public void setImageName(String name) {
        this.imageName = name;
        this.imageVersion++;
    }
}
