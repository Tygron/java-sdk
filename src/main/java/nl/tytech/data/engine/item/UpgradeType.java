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

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.data.engine.serializable.UpgradePair;
import nl.tytech.util.StringUtils;

/**
 * UpgradeType: This class keeps track of the upgrades per model.
 *
 * @author Alexander Hofstede & Maxim Knepfle, Frank Baars
 */
public class UpgradeType extends UniqueNamedItem implements Action, ImageItem {

    private static final long serialVersionUID = -3902779613875731026L;

    @XMLValue
    private boolean deprecated = false;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private double costsPerM2 = 0;

    /**
     * When true costs are per M2 floorspace (instead of plot)
     */
    @XMLValue
    private boolean costPerFloor = false;

    @XMLValue
    @AssetDirectory(ACTION_IMAGE_LOCATION)
    private String imageName = DEFAULT_IMAGE;

    @XMLValue
    @ListOfClass(UpgradePair.class)
    private ArrayList<UpgradePair> pairs = new ArrayList<>();

    @XMLValue
    private boolean mustOwn = false;

    @XMLValue
    private boolean zoningPermitRequired = true;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    public void addUpgradePair(UpgradePair pair) {

        if (containsSourceFunction(pair.getSourceFunctionID())) {
            return;
        }
        pairs.add(pair);
    }

    public boolean containsSourceFunction(Integer sourceFunctionID) {

        for (UpgradePair aPair : pairs) {
            if (aPair.getSourceFunctionID().equals(sourceFunctionID)) {
                return true;
            }
        }
        return false;
    }

    public double getCostM2() {
        return costsPerM2;
    }

    public double getCosts(double sizeM2, double floors) {
        return getCostM2() * sizeM2 * (isCostPerFloor() ? floors : 1.0);
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
        return MapLink.UPGRADE_TYPES;
    }

    public List<UpgradePair> getPairs() {
        return pairs;
    }

    public Function getTargetFunction(final Integer sourceFunctionID) {

        for (UpgradePair pair : pairs) {
            if (pair.getSourceFunctionID().equals(sourceFunctionID)) {
                return this.getItem(MapLink.FUNCTIONS, pair.getTargetFunctionID());
            }
        }
        return null;
    }

    public UpgradePair getUpgradePairForSourceID(Integer sourceFunctionID) {

        for (UpgradePair somePair : getPairs()) {
            if (somePair.getSourceFunctionID().equals(sourceFunctionID)) {
                return somePair;
            }
        }
        return null;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    public boolean isCostPerFloor() {
        return costPerFloor;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    /**
     * When true this upgrade changes the zoning and the building can be sold again
     * @return
     */
    public boolean isFunctionCategoryChange() {

        for (UpgradePair pair : pairs) {
            Function source = this.getItem(MapLink.FUNCTIONS, pair.getSourceFunctionID());
            Function target = this.getItem(MapLink.FUNCTIONS, pair.getTargetFunctionID());

            if (source.getCategories().size() != target.getCategories().size()
                    || !source.getCategories().containsAll(target.getCategories())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMustOwn() {
        return mustOwn;
    }

    public boolean isUpgradable(Integer stakeholderID, Building building) {

        if (building.getTimeState() != TimeState.READY) {
            return false;
        }
        if (isMustOwn() && !building.getOwnerID().equals(stakeholderID)) {
            return false;
        }
        return this.getTargetFunction(building.getFunctionID()) != null;
    }

    public boolean isZoningPermitRequired() {
        return zoningPermitRequired;
    }

    public void removeUpgradePair(UpgradePair pair) {
        pairs.remove(pair);
    }

    public void setCostsM2(boolean costPerFloor, double costsPerM2) {
        this.costPerFloor = costPerFloor;
        this.costsPerM2 = costsPerM2;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setImageName(String name) {
        this.imageName = name;
        this.imageVersion++;
    }

    public void setMustOwn(boolean mustOwn) {
        this.mustOwn = mustOwn;
    }

    public void setZoningPermitRequired(boolean zonePermitRequired) {
        this.zoningPermitRequired = zonePermitRequired;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String validated(boolean startNewSession) {

        String result = super.validated(startNewSession);

        for (UpgradePair pair : pairs) {
            Function source = this.getItem(MapLink.FUNCTIONS, pair.getSourceFunctionID());
            if (source == null) {
                result += "\nUpgrade " + this + " has invalid source function ID: " + pair.getSourceFunctionID();
            }
            Function target = this.getItem(MapLink.FUNCTIONS, pair.getTargetFunctionID());
            if (target == null) {
                result += "\nUpgrade " + this + " has invalid target function ID: " + pair.getTargetFunctionID();
            }
        }
        return result;
    }
}
