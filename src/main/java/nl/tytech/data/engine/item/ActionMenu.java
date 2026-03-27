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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.IndexSortedItem;
import nl.tytech.data.core.other.NamedItem;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * ActionMenu: contains a set of Actions that the stakeholder can build. Each Action menu has a unique icon in the menu bar.
 *
 * @author Maxim Knepfle & Alexander Hofstede
 */
public class ActionMenu extends Item implements IndexSortedItem, ImageItem, NamedItem {

    public static final String ACTION_MENU_IMAGE_LOCATION = "Gui/Images/Panels/LeftMenuPanel/Icons/";

    private static final long serialVersionUID = -2467220889999050209L;

    @XMLValue
    private HashMap<Integer, List<Integer>> buildable = new HashMap<>();

    @XMLValue
    private TColor color;

    @ItemIDField(MapLink.FUNCTIONS)
    @XMLValue
    private ArrayList<Integer> functionIDs = new ArrayList<>();

    @ItemIDField(MapLink.LEVEES)
    @XMLValue
    private ArrayList<Integer> leveeIDs = new ArrayList<>();

    @ItemIDField(MapLink.MEASURES)
    @XMLValue
    private ArrayList<Integer> measureIDs = new ArrayList<>();

    @ItemIDField(MapLink.EVENT_BUNDLES)
    @XMLValue
    private ArrayList<Integer> eventBundleIDs = new ArrayList<>();

    @ItemIDField(MapLink.UPGRADE_TYPES)
    @XMLValue
    private ArrayList<Integer> upgradeIDs = new ArrayList<>();

    @ItemIDField(MapLink.ATTRIBUTE_ACTIONS)
    @XMLValue
    private ArrayList<Integer> attributeActionIDs = new ArrayList<>();

    @XMLValue
    @ListOfClass(SpecialOption.Type.class)
    private ArrayList<SpecialOption.Type> specialOptions = new ArrayList<>();

    @XMLValue
    private String name = "New action menu";

    @XMLValue
    @AssetDirectory(ACTION_MENU_IMAGE_LOCATION)
    private String imageName = "category_house.png";

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @XMLValue
    private int sortIndex = 0;

    public ActionMenu() {

    }

    public void addAction(Action action) {

        if (action instanceof Function) {
            functionIDs.add(action.getID());
        } else if (action instanceof Measure) {
            measureIDs.add(action.getID());
        } else if (action instanceof UpgradeType) {
            upgradeIDs.add(action.getID());
        } else if (action instanceof SpecialOption so) {
            specialOptions.add(so.getType());
        } else if (action instanceof EventBundle) {
            eventBundleIDs.add(action.getID());
        } else if (action instanceof Levee) {
            leveeIDs.add(action.getID());
        } else if (action instanceof AttributeAction) {
            attributeActionIDs.add(action.getID());
        } else {
            TLogger.severe("Failed to add Action " + action + " to " + ActionMenu.class.getSimpleName() + ": " + getName()
                    + "! It does not have a corresponding storage collection!");
        }
    }

    public final boolean contains(Action action) {

        List<Integer> ids;
        if (action instanceof Function) {
            ids = functionIDs;
        } else if (action instanceof Measure) {
            ids = measureIDs;
        } else if (action instanceof UpgradeType) {
            ids = upgradeIDs;
        } else if (action instanceof SpecialOption) {
            return specialOptions.contains(((SpecialOption) action).getType());
        } else if (action instanceof EventBundle) {
            ids = eventBundleIDs;
        } else if (action instanceof Levee) {
            ids = leveeIDs;
        } else if (action instanceof AttributeAction) {
            ids = attributeActionIDs;
        } else {
            TLogger.severe("Failed to find Action " + action + " in " + ActionMenu.class.getSimpleName() + ": " + getName()
                    + "! It does not have a corresponding storage collection!");
            return false;
        }
        for (Integer ID : ids) {
            if (action.getID().equals(ID)) {
                return true;
            }
        }
        return false;
    }

    public void duplicateAccess(Integer originalID, Integer duplicateID) {
        List<Integer> originalStakeholderIDs = getAccessors(originalID);
        List<Integer> duplicateStakeholderIDs = getAccessors(duplicateID);
        duplicateStakeholderIDs.clear();
        duplicateStakeholderIDs.addAll(originalStakeholderIDs);

    }

    private final List<Integer> getAccessors(Integer scenarioID) {

        // fallback to previous level value when not available.
        if (!buildable.containsKey(scenarioID)) {
            Collection<Scenario> previousScenarios = getPreviousSortedItems(MapLink.SCENARIOS, scenarioID);
            for (Scenario previousScenario : previousScenarios) {
                List<Integer> previousAccessors = buildable.get(previousScenario.getID());
                if (previousAccessors != null) {
                    buildable.put(scenarioID, new ArrayList<>(previousAccessors));
                    break;
                }
            }
        }
        if (!buildable.containsKey(scenarioID)) {
            initAccessForScenario(scenarioID);
        }
        return buildable.get(scenarioID);
    }

    public final List<Action> getActions() {

        List<Action> actions = new ArrayList<>();
        actions.addAll(this.<Measure> getItems(MapLink.MEASURES, measureIDs));
        actions.addAll(this.<EventBundle> getItems(MapLink.EVENT_BUNDLES, eventBundleIDs));
        actions.addAll(this.<Function> getItems(MapLink.FUNCTIONS, functionIDs));
        actions.addAll(this.<Levee> getItems(MapLink.LEVEES, leveeIDs));
        actions.addAll(this.<UpgradeType> getItems(MapLink.UPGRADE_TYPES, upgradeIDs));
        actions.addAll(this.<SpecialOption, SpecialOption.Type> getEnumItems(MapLink.SPECIAL_OPTIONS, specialOptions));
        actions.addAll(this.<AttributeAction> getItems(MapLink.ATTRIBUTE_ACTIONS, attributeActionIDs));
        return actions;
    }

    public List<AttributeAction> getAttributeActions() {
        return this.getItems(MapLink.ATTRIBUTE_ACTIONS, attributeActionIDs);
    }

    public TColor getColor() {
        return color;
    }

    public List<Integer> getFunctionIDs() {
        return functionIDs;
    }

    public List<Function> getFunctions() {
        return this.getItems(MapLink.FUNCTIONS, functionIDs);
    }

    @Override
    public String getImageLocation() {
        return ACTION_MENU_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageVersion() {
        return imageVersion;
    }

    public List<Integer> getMeasureIDs() {
        return measureIDs;
    }

    public final List<Measure> getMeasurs() {
        return this.getItems(MapLink.MEASURES, measureIDs);
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public Integer getParentID() {
        return Item.NONE;
    }

    @Override
    public int getSortIndex() {
        return sortIndex;
    }

    public List<SpecialOption> getSpecialOptions() {
        return this.getEnumItems(MapLink.SPECIAL_OPTIONS, specialOptions);
    }

    public List<Integer> getUpgradeIDs() {
        return upgradeIDs;
    }

    public List<UpgradeType> getUpgrades() {
        return this.getItems(MapLink.UPGRADE_TYPES, upgradeIDs);
    }

    public void initAccessForScenario(Integer scenarioID) {
        buildable.put(scenarioID, new ArrayList<Integer>());
    }

    public final boolean isAccessable(Integer stakeholderID) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        return this.isAccessable(setting.getIntegerValue(), stakeholderID);
    }

    public final boolean isAccessable(Integer scenarioID, Integer stakeholderID) {

        List<Integer> accessorIDs = getAccessors(scenarioID);
        for (Integer accessorID : accessorIDs) {
            if (accessorID.equals(stakeholderID)) {
                return true;
            }
        }
        return false;
    }

    public void removeAccessForScenario(Integer scenarioID) {
        this.buildable.remove(scenarioID);
    }

    public void removeAction(Action action) {

        if (action instanceof Function) {
            functionIDs.remove(action.getID());
        } else if (action instanceof Measure) {
            measureIDs.remove(action.getID());
        } else if (action instanceof UpgradeType) {
            upgradeIDs.remove(action.getID());
        } else if (action instanceof SpecialOption so) {
            specialOptions.remove(so.getType());
        } else if (action instanceof EventBundle) {
            eventBundleIDs.remove(action.getID());
        } else if (action instanceof Levee) {
            leveeIDs.remove(action.getID());
        } else if (action instanceof AttributeAction) {
            attributeActionIDs.remove(action.getID());
        } else {
            TLogger.severe("Failed to remove Action " + action + " from " + ActionMenu.class.getSimpleName() + ": " + getName()
                    + "! It does not have a corresponding storage collection!");
        }
    }

    public void setAccessForAllScenarios(Integer stakeholderID, boolean accessable) {

        ItemMap<Scenario> scenarios = getMap(MapLink.SCENARIOS);
        for (Scenario scenario : scenarios) {
            this.setAccessForScenario(scenario.getID(), stakeholderID, accessable);
        }
    }

    public void setAccessForScenario(Integer scenarioID, Integer stakeholderID, boolean accessable) {

        List<Integer> accessors = this.getAccessors(scenarioID);
        if (accessable && !accessors.contains(stakeholderID)) {
            accessors.add(stakeholderID);
        }
        if (!accessable && accessors.contains(stakeholderID)) {
            accessors.remove(stakeholderID);
        }
    }

    @Override
    public void setImageName(String iconName) {
        this.imageName = iconName;
        this.imageVersion++;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSortIndex(int index) {
        this.sortIndex = index;
    }

    @Override
    public final String toString() {
        return getName();
    }
}
