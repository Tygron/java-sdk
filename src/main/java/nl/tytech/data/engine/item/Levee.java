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
import nl.tytech.core.item.annotations.Html;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.serializable.TerrainSpatial;
import nl.tytech.util.StringUtils;

/**
 * Levee
 *
 * @author Maxim Knepfle
 */
public class Levee extends UniqueNamedItem implements Action, ImageItem {

    private static final String DEFAULT_LEVEE_IMAGE = "levee.png";

    public static final double MIN_ANGLE_DEGREES = TerrainSpatial.MIN_ANGLE;
    public static final double MAX_ANGLE_DEGREES = TerrainSpatial.MAX_ANGLE;

    private static final long serialVersionUID = -4253782210241563578L;

    @ItemIDField(MapLink.FUNCTIONS)
    @XMLValue
    private Integer topFunctionID = Item.NONE;

    @ItemIDField(MapLink.FUNCTIONS)
    @XMLValue
    private Integer sideFunctionID = Item.NONE;

    @XMLValue
    private Double defaultHeightM = 4d;

    @XMLValue
    private Double defaultWidthM = 20d;

    @XMLValue
    private boolean fixedSize = false;

    @XMLValue
    private boolean relativeIncrease = false;

    @XMLValue
    private double angleDegrees = 45;

    @XMLValue
    private boolean useExistingFunction = false;

    @Html
    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @AssetDirectory(ACTION_IMAGE_LOCATION)
    private String imageName = DEFAULT_LEVEE_IMAGE;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    public Levee() {

    }

    public double getAngleDegrees() {
        return angleDegrees;
    }

    public double getDefaultHeightM() {
        return defaultHeightM;
    }

    public double getDefaultWidthM() {
        return defaultWidthM;
    }

    @Override
    public String getDescription() {

        if (!StringUtils.containsData(description)) {
            return getName();
        } else {
            return description;
        }
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
        return MapLink.LEVEES;
    }

    public Function getSideFunction() {
        return this.getItem(MapLink.FUNCTIONS, this.getSideFunctionID());
    }

    public Integer getSideFunctionID() {
        return sideFunctionID;
    }

    public Function getTopFunction() {
        return this.getItem(MapLink.FUNCTIONS, this.getTopFunctionID());
    }

    public Integer getTopFunctionID() {
        return topFunctionID;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public boolean isRelativeIncrease() {
        return relativeIncrease;
    }

    public void setAngleDegrees(double angleDegrees) {
        this.angleDegrees = angleDegrees;
    }

    public void setDefaultHeightM(double defaultHeightM) {
        this.defaultHeightM = defaultHeightM;
    }

    public void setDefaultWidthM(double defaultWidthM) {
        this.defaultWidthM = defaultWidthM;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    @Override
    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.imageVersion++;
    }

    public void setIsRelativeIncrease(boolean relativeIncrease) {
        this.relativeIncrease = relativeIncrease;
    }

    public void setSideFunctionID(Integer sideFunctionID) {
        this.sideFunctionID = sideFunctionID;
    }

    public void setTopFunctionID(Integer topFunctionID) {
        this.topFunctionID = topFunctionID;
    }

    public void setUseExisting(boolean useExisting) {
        this.useExistingFunction = useExisting;
    }

    public boolean useExistingFunction() {
        return useExistingFunction;
    }

}
